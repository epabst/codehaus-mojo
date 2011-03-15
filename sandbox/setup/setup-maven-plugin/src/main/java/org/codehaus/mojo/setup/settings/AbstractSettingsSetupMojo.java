package org.codehaus.mojo.setup.settings;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.DefaultSettingsReader;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.mojo.setup.AbstractSetupMojo;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

/**
 * Abstract class for both global and user settings.
 * 
 * @author Robert Scholte
 * @since 1.0.0
 */
public abstract class AbstractSettingsSetupMojo
    extends AbstractSetupMojo
{
    /**
     * @component
     */
    private Invoker invoker;
    
    /**
     * @component
     */
    private SecDispatcher secDispatcher;
    
    /**
     * Set this property to true, if you want all server-passwords to be encrypted.
     * This requires an settings-security.xml file with a encrypted master-password.
     * See the <a href="settings-security-mojo.html">setup:settings-security</a> goal for further details.
     * Only the not-yet encrypted passwords will be encrypted.
     * 
     * @parameter expression="${encrypt-passwords}"
     */
    private boolean encryptPasswords = false;
    
    /**
     * If the user hasn't set settingsTemplate, look for this file in basedir or classpath
     */
    protected static final String DEFAULT_TEMPLATE_FILENAME = "settings.xml";

    /**
     *
     */
    protected static final String PROPERTIES_FILENAME = "settings.properties";

    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    protected String getDefaultTemplateFilename()
    {
        return DEFAULT_TEMPLATE_FILENAME;
    }


    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    protected String getPropertiesFilename()
    {
        return PROPERTIES_FILENAME;
    }
    
    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    protected VersionRange getMavenVersionRange() throws InvalidVersionSpecificationException
    {
        return VersionRange.createFromVersionSpec( "[3.0,)" );
    }
    
    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    protected void preProcess()
        throws MojoExecutionException, MojoFailureException
    {
        if ( encryptPasswords )
        {
            Set < String > passwords = new HashSet < String > ();
            if ( resolveTemplateFile() != null )
            {
                passwords.addAll( getTemplatePasswords( resolveTemplateFile() ) );
            }
            
            for ( Server server : getSession().getSettings().getServers() )
            {
                if ( !isEncrypted( server.getPassword() ) )
                {
                    passwords.add( server.getPassword() );
                }
            }

            final StringBuffer encryptedPassword = new StringBuffer();
            InvocationRequest request = new DefaultInvocationRequest();
            
            //to be able to change settings-security path
            request.setProperties( getSession().getUserProperties() );
            
            request.setOutputHandler( new InvocationOutputHandler()
            {
                public void consumeLine( String line )
                {
                    encryptedPassword.append( line );
                }
            } );
            
            Map < String, String > encryptedPasswordMap = new HashMap < String, String > ();
            for ( String password : passwords )
            {
                request.setGoals( Collections.singletonList( "--encrypt-password " + password ) );
                encryptedPassword.setLength( 0 );
                try
                {
                    InvocationResult ir = invoker.execute( request );
                    if ( ir.getExitCode() == 0 ) 
                    {
                        encryptedPasswordMap.put( password, encryptedPassword.toString() );
                    }
                    else if ( ir.getExecutionException() == null ) 
                    {
                        throw new MojoFailureException( "Could not encrypt passwords" );
                    }
                    else 
                    {
                        throw new MojoFailureException( ir.getExecutionException().getMessage() );
                    }
                }
                catch ( MavenInvocationException e )
                {
                    if ( getLog().isWarnEnabled() ) 
                    {
                        getLog().warn( "encrypting password failed: " + e.getMessage() );
                    }
                    throw new MojoFailureException( "Could not encrypt passwords: " + e.getMessage() );
                }
                
            }
            getSetupRequest().getAdditionalProperties().put( "encryptedPasswordMap", encryptedPasswordMap );
        }
    }
    
    /**
     * Check if this password is encrypted or not
     * 
     * @param password the value to check
     * @return true if is is encrypted
     */
    private boolean isEncrypted( String password )
    {
        boolean result = true;
        try
        {
            result = !secDispatcher.decrypt( password ).equals( password );
        }
        catch ( SecDispatcherException e )
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( e.getMessage(), e );
            }
        }
        return result;
    }


    /**
     * Collect all the passwords, which are already in the template
     * 
     * @param templateFile the settingsfile with the passwords 
     * @return non-null set of passwords
     */
    private Set < String > getTemplatePasswords( File templateFile )
    {
        Set < String > result = new HashSet < String > ();
        try
        {
            Settings settings = new DefaultSettingsReader().read( templateFile, null );
            for ( Server server : settings.getServers() )
            {
                if ( !isEncrypted( server.getPassword() ) )
                {
                    result.add( server.getPassword() );
                }
            }
        }
        catch ( IOException e )
        {
            //nop
        }
        return result;
    }   
}
