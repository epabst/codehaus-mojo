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
import net.java.dev.vcc.api.PowerState;
import net.java.dev.vcc.api.Success;
import net.java.dev.vcc.api.commands.SuspendComputer;
import net.java.dev.vcc.util.CompletedFuture;

import java.util.concurrent.Future;

/**
 * Suspends computers.
 *
 * @goal suspend-computer
 * @requiresProject false
 * @since 0.0.1-alpha-1
 */
public class SuspendComputerMojo
    extends AbstractComputerActionMojo
{
    /**
     * {@inheritDoc}
     */
    protected void recordFailure( String name )
    {
        getLog().error( "Computer " + name + " failed to suspend." );
    }

    /**
     * {@inheritDoc}
     */
    protected void recordSuccess( String name )
    {
        getLog().info( "Computer " + name + " suspendend." );
    }

    /**
     * {@inheritDoc}
     */
    protected Future<Success> doAction( Computer c )
    {
        if ( PowerState.SUSPENDED.equals( c.getState() ) )
        {
            return new CompletedFuture<Success>( Success.getInstance() );
        }
        else
        {
            getLog().info( "Suspending computer " + c.getName() + "..." );
            return c.execute( new SuspendComputer() );
        }
    }
}