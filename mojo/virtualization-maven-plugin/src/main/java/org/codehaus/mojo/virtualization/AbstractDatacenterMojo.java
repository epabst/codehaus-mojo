package org.codehaus.mojo.virtualization;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import net.java.dev.vcc.api.Datacenter;
import net.java.dev.vcc.api.DatacenterManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;

/**
 * The base class for mojos that require a datacenter connection.
 *
 * @since 0.0.1-alpha-1
 */
public abstract class AbstractDatacenterMojo
    extends AbstractMojo
{
    /**
     * The URI fo the datacenter.
     *
     * @parameter expression="${virtualization.datacenterUri}"
     * @required
     * @since 0.0.1-alpha-1
     */
    private String datacenterUri;

    /**
     * The username to connect with.
     *
     * @parameter expression="${virtualization.username}"
     * @required
     * @since 0.0.1-alpha-1
     */
    private String username;

    /**
     * The password to connect with.
     *
     * @parameter expression="${virtualization.password}"
     * @required
     * @since 0.0.1-alpha-1
     */
    private String password;

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        MavenLogFactory.setLog( getLog() );
        if ( datacenterUri == null )
        {
            throw new MojoFailureException( "You must provide the datacenter URI" );
        }
        Datacenter datacenter = null;
        try
        {
            datacenter = DatacenterManager.getConnection( datacenterUri, username, password.toCharArray() );
            if ( datacenter == null )
            {
                throw new MojoFailureException( "Unknown datacenter URI" );
            }
            execute( datacenter );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        finally
        {
            if ( datacenter != null )
            {
                datacenter.close();
            }
            MavenLogFactory.setLog( null );
        }
    }

    /**
     * Execute the mojo on the specified datacenter.
     *
     * @param datacenter The datacenter connection.
     * @throws MojoFailureException   if things are wrong.
     * @throws MojoExecutionException when things go bad.
     * @since 0.0.1-alpha-1
     */
    protected abstract void execute( Datacenter datacenter )
        throws MojoFailureException, MojoExecutionException;
}
