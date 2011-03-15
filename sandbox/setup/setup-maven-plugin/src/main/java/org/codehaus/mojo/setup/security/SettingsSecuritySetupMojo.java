package org.codehaus.mojo.setup.security;

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

import java.util.Collections;

import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.mojo.setup.AbstractSetupMojo;
import org.codehaus.mojo.setup.SetupManager;
import org.codehaus.plexus.util.StringUtils;

/**
 * Configure a settings-security.xml file according to <a
 * href="http://maven.apache.org/guides/mini/guide-encryption.html">How to create a master password</a>
 * This plugin doesn't require a template. Providing a password is enough
 * 
 * <p>
 * Default template filename: <code>settings-security.xml</code><br/>
 * Properties filename: <code>settings-security.properties</code><br/>
 * Requires: <code>Maven 2.1.0</code>
 * </p>
 * 
 * @goal settings-security
 * @author Robert Scholte
 * @since 1.0.0
 * 
 */
public class SettingsSecuritySetupMojo
    extends AbstractSetupMojo
{
    protected static final String DEFAULT_TEMPLATE_FILENAME = "settings-security.xml";
    
    protected static final String PROPERTIES_FILENAME = "settings-security.properties";

    /**
     * @component
     */
    private Invoker invoker;
    
    /**
     * @component role-hint="settings-security"
     */
    private SetupManager fileProcessor;

    /**
     * Define the master-password, which will be encrypted and added to the settings-security.xml
     * A master-password is required to support encrypted passwords in other settings files
     * 
     * @parameter expression="${password}"
     */
    private String password;

    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    protected VersionRange getMavenVersionRange() throws InvalidVersionSpecificationException
    {
        return VersionRange.createFromVersionSpec( "[2.1.0,)" );
    }
    
    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    public void preProcess()
        throws MojoExecutionException, MojoFailureException
    {
        if ( StringUtils.isNotEmpty( password ) ) 
        {
            InvocationRequest request = new DefaultInvocationRequest();
            request.setGoals( Collections.singletonList( "--encrypt-master-password "
                + password ) );
            request.setProperties( getSession().getUserProperties() );
            final StringBuffer encryptedMasterPassword = new StringBuffer();
            request.setOutputHandler( new InvocationOutputHandler()
            {
                public void consumeLine( String line )
                {
                    encryptedMasterPassword.append( line );
                }
            } );
            try
            {
                InvocationResult result = invoker.execute( request );
                if ( result.getExitCode() == 0 ) 
                {
                    getSetupRequest().getAdditionalProperties().put( "master", encryptedMasterPassword.toString() );    
                }
                else if ( result.getExecutionException() == null ) 
                {
                    throw new MojoExecutionException( "Failed to encrypt master password" );
                }
                else 
                {
                    throw new MojoExecutionException( result.getExecutionException().getMessage() );
                }
            }
            catch ( MavenInvocationException e )
            {
                throw new MojoExecutionException( e.getMessage() );
            }
        }
    }
    
    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    public SetupManager getSetupManager()
    {
        return fileProcessor;
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
    protected String getDefaultTemplateFilename()
    {
        return DEFAULT_TEMPLATE_FILENAME;
    }
}
