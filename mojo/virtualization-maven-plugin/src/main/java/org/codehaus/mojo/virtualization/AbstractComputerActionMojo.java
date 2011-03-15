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

import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.Datacenter;
import net.java.dev.vcc.api.Success;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The base class for mojos that operate on a collection of virtual computers.
 *
 * @since 0.0.1-alpha-1
 */
public abstract class AbstractComputerActionMojo
    extends AbstractDatacenterMojo
{
    /**
     * The computers to process.
     *
     * @parameter
     * @since 0.0.1-alpha-1
     */
    private String[] computers;

    /**
     * The computer to process.
     *
     * @parameter expression="${virtualization.computer}"
     * @since 0.0.1-alpha-1
     */
    private String computer;

    /**
     * The number of seconds to wait for operations to complete.
     *
     * @parameter default-value="60"
     * @since 0.0.1-alpha-1
     */
    private int timeout;

    /**
     * {@inheritDoc}
     */
    protected final void execute( Datacenter datacenter )
        throws MojoFailureException, MojoExecutionException
    {
        Set<String> targets = new HashSet<String>();
        if ( computer != null )
        {
            targets.add( computer );
        }
        if ( computers != null )
        {
            targets.addAll( Arrays.asList( computers ) );
        }
        Map<String, Future<Success>> results = new LinkedHashMap<String, Future<Success>>();
        for ( Computer c : datacenter.getAllComputers() )
        {
            if ( targets.contains( c.getId().toString() ) )
            {
                getLog().debug( "Computer " + c.getName() + " is in state " + c.getState() );
                results.put( c.getName(), doAction( c ) );
                targets.remove( c.getName() );
            }
        }
        if ( !targets.isEmpty() )
        {
            throw new MojoFailureException( "Could not find the following computers: " + targets );
        }
        long giveUp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis( timeout );
        while ( System.currentTimeMillis() < giveUp && !results.isEmpty() )
        {
            for ( Iterator<Map.Entry<String, Future<Success>>> it = results.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry<String, Future<Success>> entry = it.next();
                try
                {
                    entry.getValue().get( giveUp - System.currentTimeMillis(), TimeUnit.MILLISECONDS );
                    recordSuccess( entry.getKey() );
                    it.remove();
                }
                catch ( ExecutionException e )
                {
                    recordFailure( entry.getKey() );
                    it.remove();
                    throw new MojoExecutionException( e.getMessage(), e );
                }
                catch ( TimeoutException e )
                {
                    throw new MojoExecutionException( e.getMessage(), e );
                }
                catch ( InterruptedException e )
                {
                    throw new MojoExecutionException( e.getMessage(), e );
                }
            }
        }
    }

    /**
     * Records that the operation on the named computer failed.
     *
     * @param name The computer name.
     * @since 0.0.1-alpha-1
     */
    protected abstract void recordFailure( String name );

    /**
     * Records that the operation on the named computer succeeded.
     *
     * @param name The computer name.
     * @since 0.0.1-alpha-1
     */
    protected abstract void recordSuccess( String name );

    /**
     * Preforms the operation on the specified computer, returning a future for completion of the operation.
     *
     * @param computer The computer.
     * @return A future for completion of the operation.
     * @since 0.0.1-alpha-1
     */
    protected abstract Future<Success> doAction( Computer computer );
}
