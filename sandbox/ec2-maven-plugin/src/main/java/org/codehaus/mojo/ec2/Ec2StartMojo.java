package org.codehaus.mojo.ec2;

/*
 * Copyright 2008 Exist Global, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.ReservationDescription.Instance;

/**
 * Mojo to start Amazon EC2 instances
 * 
 * @goal start
 * @author Carlos Sanchez <carlos@apache.org>
 */
public class Ec2StartMojo
    extends AbstractEc2Mojo
{

    final Logger logger = LoggerFactory.getLogger( Ec2StartMojo.class );

    /**
     * @parameter
     */
    private List<LaunchConfiguration> launchConfigurations;

    public void doExecute( Jec2 ec2 )
        throws MojoExecutionException, MojoFailureException
    {

        try
        {
            List<Instance> instancesStarted = new ArrayList<Instance>( launchConfigurations.size() );
            List<Instance> instancesToWaitFor = new ArrayList<Instance>( launchConfigurations.size() );
            List<Instance> instancesToTerminate = new ArrayList<Instance>( launchConfigurations.size() );
            Map<Instance, String> instanceIp = new HashMap<Instance, String>();

            for ( LaunchConfiguration lc : launchConfigurations )
            {
                if ( lc.getImageId() == null )
                {
                    throw new MojoFailureException( "You must specify the imageId in the <launchConfiguration>" );
                }

                ReservationDescription reservation = ec2.runInstances( lc );

                instancesStarted.addAll( reservation.getInstances() );
                if ( lc.isWait() )
                {
                    instancesToWaitFor.addAll( reservation.getInstances() );
                }
                if ( lc.isTerminate() )
                {
                    instancesToTerminate.addAll( reservation.getInstances() );
                }
                if ( lc.getElasticIp() != null )
                {
                    int s = reservation.getInstances().size();
                    if ( s != 1 )
                    {
                        logger.warn( "Tried to assign elatic IP to " + ( s > 1 ? "more than one image" : "zero images" ) );
                    }
                    else
                    {
                        instanceIp.put( reservation.getInstances().get( 0 ), lc.getElasticIp() );
                    }
                }
            }

            if ( logger.isInfoEnabled() )
            {
                for ( Instance instance : instancesStarted )
                {
                    logger.info( "Starting AMI {}", instance.getImageId() );
                }
            }

            if ( !instancesToTerminate.isEmpty() )
            {
                TerminateInstancesThread.addShutdownHook( ec2, instancesToTerminate );
            }
            waitForInstancesToStart( ec2, instancesToWaitFor );
            associateElasticIps( ec2, instanceIp );
        }
        catch ( EC2Exception e )
        {
            throw new MojoExecutionException( "Exception in EC2: " + e.getMessage(), e );
        }
    }

    /**
     * wait for instances to be started
     * 
     * @throws MojoExecutionException
     */
    private void waitForInstancesToStart( Jec2 ec2, List<Instance> instances )
        throws MojoExecutionException
    {
        List<String> instanceIds = new ArrayList<String>( instances.size() );
        for ( Instance instance : instances )
        {
            instanceIds.add( instance.getInstanceId() );
        }

        while ( !instanceIds.isEmpty() )
        {
            try
            {
                for ( ReservationDescription description : ec2.describeInstances( instanceIds ) )
                {
                    for ( Instance currentInstance : description.getInstances() )
                    {
                        if ( currentInstance.isRunning() )
                        {
                            instanceIds.remove( currentInstance.getInstanceId() );
                        }
                    }
                }
            }
            catch ( EC2Exception e )
            {
                throw new MojoExecutionException( "Error describing instances " + instanceIds, e );
            }

            if ( instanceIds.isEmpty() )
            {
                return;
            }

            try
            {
                logger.info( "Waiting for instances to start: {}", instanceIds );
                Thread.sleep( 10 * 1000 ); // 10 sec
            }
            catch ( InterruptedException e )
            {
                throw new MojoExecutionException( "Poll for available interrupted : " + e.getMessage(), e );
            }
        }
    }

    private void associateElasticIps( Jec2 ec2, Map<Instance, String> instanceIp )
        throws MojoExecutionException
    {
        for ( Map.Entry<Instance, String> entry : instanceIp.entrySet() )
        {
            String ip = entry.getValue();
            String instanceId = entry.getKey().getInstanceId();
            try
            {
                ec2.associateAddress( instanceId, ip );
            }
            catch ( EC2Exception e )
            {
                throw new MojoExecutionException( "Exception trying to associate ip " + ip + " to instance "
                    + instanceId, e );
            }
        }
    }

    private void waitForPort( Instance instance, int port )
    {
        InetSocketAddress address = new InetSocketAddress( instance.getDnsName(), port );
        Socket s = null;
        try
        {
            s = new Socket();
            s.connect( address, 5 * 1000 );
            return;
        }
        catch ( IOException e )
        {
            // wait
        }
        finally
        {
            try
            {
                if ( s != null )
                {
                    s.close();
                }
            }
            catch ( IOException e )
            {
            }
        }
    }
}
