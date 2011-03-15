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

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.xerox.amazonws.ec2.InstanceType;

/**
 * Convenience class to bridge to the Typica API
 * 
 * @author Carlos Sanchez <carlos@apache.org>
 */
public class LaunchConfiguration
    extends com.xerox.amazonws.ec2.LaunchConfiguration
{
    private static final String CHARSET = "UTF-8";

    private boolean wait = true;

    private boolean terminate = true;

    private String elasticIp;

    public LaunchConfiguration()
    {
        super( null );
    }

    public void setWait( boolean wait )
    {
        this.wait = wait;
    }

    /**
     * Wait until the image is started
     * 
     * @return
     */
    public boolean isWait()
    {
        return wait;
    }

    public void setTerminate( boolean terminate )
    {
        this.terminate = terminate;
    }

    /**
     * Terminate the instance at the end of the build
     * 
     * @return
     */
    public boolean isTerminate()
    {
        return terminate;
    }

    public void setUserDataString( String userDataString )
    {
        try
        {
            setUserData( userDataString.getBytes( CHARSET ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            /* shouldn't happen */
            throw new RuntimeException( "Unsupported encoding " + CHARSET, e );
        }
    }

    /**
     * Associate this elastic IP to the instance started. You can start only one instance for it to work.
     * 
     * @return
     */
    public String getElasticIp()
    {
        return elasticIp;
    }

    /**
     * The user data as a string in UTF-8
     * 
     * @return
     */
    public String getUserDataString()
    {
        try
        {
            return new String( getUserData(), CHARSET );
        }
        catch ( UnsupportedEncodingException e )
        {
            /* shouldn't happen */
            throw new RuntimeException( "Unsupported encoding " + CHARSET, e );
        }
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }

    public void setInstanceTypeString( String instanceTypeString )
    {
        setInstanceType( InstanceType.getTypeFromString( instanceTypeString ) );
    }

    public String getInstanceTypeString()
    {
        return getInstanceType().name();
    }
}
