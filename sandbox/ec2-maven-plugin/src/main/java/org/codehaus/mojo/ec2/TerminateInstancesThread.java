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

import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.ReservationDescription.Instance;

/**
 * Adds a hook to stop the instances when the jvm shuts down. Adding a mojo to the post-integration-test phase won't
 * work as the build will not continue if there are test errors.
 * 
 * @author Carlos Sanchez <carlos@apache.org>
 */
public class TerminateInstancesThread
    extends Thread
{

    private Jec2 ec2;

    private List<Instance> instances;

    protected TerminateInstancesThread( Jec2 ec2, List<Instance> instances )
    {
        this.ec2 = ec2;
        this.instances = instances;
    }

    @Override
    public void run()
    {
        setName( "Terminate EC2 instances" );

        List<String> instanceIds = new ArrayList<String>( instances.size() );
        for ( Instance instance : instances )
        {
            instanceIds.add( instance.getInstanceId() );
        }

        if ( !instanceIds.isEmpty() )
        {
            try
            {
                ec2.terminateInstances( instanceIds );
            }
            catch ( EC2Exception e )
            {
                throw new RuntimeException( "Error shutting down EC2 instances: " + e.getMessage(), e );
            }
        }
    }

    public static void addShutdownHook( Jec2 ec2, List<Instance> instances )
    {
        TerminateInstancesThread thread = new TerminateInstancesThread( ec2, instances );
        Runtime.getRuntime().addShutdownHook( thread );
    }
}
