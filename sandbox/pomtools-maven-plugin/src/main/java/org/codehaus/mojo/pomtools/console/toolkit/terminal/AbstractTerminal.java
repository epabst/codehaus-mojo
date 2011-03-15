package org.codehaus.mojo.pomtools.console.toolkit.terminal;

/*
 * Copyright 2005-2006 The Apache Software Foundation.
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

import java.io.IOException;

import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.plexus.components.interactivity.DefaultInputHandler;
import org.codehaus.plexus.components.interactivity.DefaultOutputHandler;
import org.codehaus.plexus.components.interactivity.InputHandler;
import org.codehaus.plexus.components.interactivity.OutputHandler;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public abstract class AbstractTerminal
    implements Terminal
{
    private final InputHandler inputHandler;

    private final OutputHandler outputHandler;

    public AbstractTerminal()
        throws ConsoleExecutionException
    {
        try
        {
            DefaultInputHandler in = new DefaultInputHandler();
            in.initialize();
            DefaultOutputHandler out = new DefaultOutputHandler();
            out.initialize();

            inputHandler = in;
            outputHandler = out;
        }
        catch ( InitializationException e )
        {
            throw new ConsoleExecutionException( "Error initializing terminal", e );
        }

    }

    public AbstractTerminal( InputHandler inputHandler, OutputHandler outputHandler )
        throws ConsoleExecutionException
    {
        this.inputHandler = inputHandler;
        this.outputHandler = outputHandler;
    }

    public void write( String s )
        throws ConsoleExecutionException
    {
        try
        {
            outputHandler.write( s );
        }
        catch ( IOException e )
        {
            throw new ConsoleExecutionException( "An error occurred while writing console output." );
        }
    }

    public String readLine()
        throws ConsoleExecutionException
    {
        try
        {
            return inputHandler.readLine();
        }
        catch ( IOException e )
        {
            throw new ConsoleExecutionException( "An error occurred while reading user input." );
        }
    }
}
