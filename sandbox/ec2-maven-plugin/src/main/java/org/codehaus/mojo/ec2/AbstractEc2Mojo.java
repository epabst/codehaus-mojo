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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xerox.amazonws.ec2.Jec2;

/**
 * Abstract mojo for Amazon EC2
 * 
 * @author Carlos Sanchez <carlos@apache.org>
 */
public abstract class AbstractEc2Mojo
    extends AbstractMojo
{

    final Logger logger = LoggerFactory.getLogger( AbstractEc2Mojo.class );

    /**
     * @parameter expression="${accessKeyId}"
     */
    private String accessKeyId;

    /**
     * @parameter expression="${secretAccessKey}"
     */
    private String secretAccessKey;

    public void setAccessKeyId( String accessKeyId )
    {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeyId()
    {
        return accessKeyId;
    }

    public void setSecretAccessKey( String secretAccessKey )
    {
        this.secretAccessKey = secretAccessKey;
    }

    public String getSecretAccessKey()
    {
        return secretAccessKey;
    }

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( ( getAccessKeyId() == null ) || ( getAccessKeyId().length() == 0 ) )
        {
            throw new MojoFailureException( "accessKeyId was not provided" );
        }

        if ( ( getSecretAccessKey() == null ) || ( getSecretAccessKey().length() == 0 ) )
        {
            throw new MojoFailureException( "secretAccessKey was not provided" );
        }

        Jec2 ec2 = new Jec2( getAccessKeyId(), getSecretAccessKey() );

        doExecute( ec2 );
    }

    protected abstract void doExecute( Jec2 ec2 )
        throws MojoExecutionException, MojoFailureException;
}
