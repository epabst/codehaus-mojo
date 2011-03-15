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
import net.java.dev.vcc.api.PowerState;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Lists the computers in a datacenter.
 *
 * @goal list-computers
 * @requiresProject false
 * @since 0.0.1-alpha-1
 */
public class ListComputersMojo
    extends AbstractDatacenterMojo
{
    /**
     * {@inheritDoc}
     */
    protected void execute( Datacenter datacenter )
        throws MojoFailureException, MojoExecutionException
    {
        SortedSet<Computer> computers = new TreeSet<Computer>( new Comparator<Computer>()
        {
            public int compare( Computer o1, Computer o2 )
            {
                return o1.getName().compareToIgnoreCase( o2.getName() );
            }
        } );
        computers.addAll( datacenter.getAllComputers() );
        for ( Computer computer : computers )
        {
            PowerState state = computer.getState();
            getLog().info(
                StringUtils.rightPad( "\"" + computer.getName() + "\" ", 70 - state.toString().length(), "." ) + " "
                    + state.toString() );
        }
    }
}
