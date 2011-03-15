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

import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.plexus.components.interactivity.InputHandler;
import org.codehaus.plexus.components.interactivity.OutputHandler;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class GenericTerminal
    extends AbstractTerminal
{
    public GenericTerminal()
        throws ConsoleExecutionException
    {
        super();
    }

    public GenericTerminal( InputHandler inputHandler, OutputHandler outputHandler )
        throws ConsoleExecutionException
    {
        super( inputHandler, outputHandler );
    }

    public void clearScreen()
        throws ConsoleExecutionException
    {
        // This is a generic implementation and doesn't support clearing the screen.
    }

    public boolean supportsClearScreen()
    {
        return false;
    }

    public boolean supportsFormatting()
    {
        return false;
    }

    public String bold( String s )
    {
        return s;
    }

    public String underline( String s )
    {
        return s;
    }

    public String reverse( String s )
    {
        return s;
    }

    public int length( String s )
    {
        return ( s == null ) ? 0 : s.length();
    }

    public int encodingLength( String s )
    {
        return 0;
    }
}
