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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xerox.amazonws.ec2.ConsoleOutput;
import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.ImageDescription;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.KeyPairInfo;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.ReservationDescription.Instance;

/**
 * Mojo to describe the Amazon EC2 instances
 * 
 * @goal describe
 * @author Carlos Sanchez <carlos@apache.org>
 */
public class Ec2DescribeMojo
    extends AbstractEc2Mojo
{

    final Logger logger = LoggerFactory.getLogger( Ec2DescribeMojo.class );

    public void doExecute( Jec2 ec2 )
        throws MojoExecutionException, MojoFailureException
    {

        try
        {
            // describe images
            List<String> params = new ArrayList<String>();
            List<ImageDescription> images = ec2.describeImages( params );
            logger.info( "Available Images" );
            for ( ImageDescription img : images )
            {
                if ( img.getImageState().equals( "available" ) )
                {
                    logger.info( "{}\t{}\t{}", new String[] { img.getImageId(), img.getImageLocation(),
                        img.getImageOwnerId() } );
                }
            }

            // describe instances
            params = new ArrayList<String>();
            List<ReservationDescription> instances = ec2.describeInstances( params );
            logger.info( "Instances" );
            String instanceId = "";
            for ( ReservationDescription res : instances )
            {
                logger.info( res.getOwner() + "\t" + res.getReservationId() );
                if ( res.getInstances() != null )
                {
                    for ( Instance inst : res.getInstances() )
                    {
                        logger.info( "\t" + inst.getImageId() + "\t" + inst.getDnsName() + "\t" + inst.getState() +
                            "\t" + inst.getKeyName() );
                        instanceId = inst.getInstanceId();
                    }
                }
            }

            // test console output
            ConsoleOutput consOutput = ec2.getConsoleOutput( instanceId );
            logger.info( "Console Output:" );
            logger.info( consOutput.getOutput() );

            // show keypairs
            List<KeyPairInfo> info = ec2.describeKeyPairs( new String[] {} );
            logger.info( "keypair list" );
            for ( KeyPairInfo i : info )
            {
                logger.info( "keypair : " + i.getKeyName() + ", " + i.getKeyFingerprint() );
            }
        }
        catch ( EC2Exception e )
        {
            throw new MojoExecutionException( "Exception in EC2: " + e.getMessage(), e );
        }
    }

}
