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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.plexus.components.interactivity.InputHandler;
import org.codehaus.plexus.components.interactivity.OutputHandler;

/**
 *  Very simple terminal emulation for VT100 or linux terminals
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class VT100Terminal
    extends AbstractTerminal
{
    private static final String ESCAPE = "\033";
    
    private static final String BOLD = ESCAPE + "[1m";
    
    //private static final String DIM  = ESCAPE + "[2m";
    
    private static final String UNDERLINE  = ESCAPE + "[4m";

    private static final String REVERSE  = ESCAPE + "[7m";

    //private static final String STRIKETHROUGH  = ESCAPE + "[9m";
    
    private static final String NORMAL = ESCAPE + "[0m";

    private static final String RESET_TERMINAL = ESCAPE + "c";
    
    private static final Pattern TERMINAL_ESCAPE_CODES = Pattern.compile( ESCAPE + "\\[.+?m" ); 
    
    public VT100Terminal()
        throws ConsoleExecutionException
    {
        super();
    }

    public VT100Terminal( InputHandler inputHandler, OutputHandler outputHandler )
        throws ConsoleExecutionException
    {
        super( inputHandler, outputHandler );
    }

    public void clearScreen()
        throws ConsoleExecutionException
    {
        write( RESET_TERMINAL );

    }

    public boolean supportsClearScreen()
    {
        return true;
    }

    public boolean supportsFormatting()
    {
        return true;
    }
    
    public String bold( String s )
    {
        if ( s == null )
        {
            return null;
        }
        
        return BOLD + s + NORMAL;
    }

    public String underline( String s )
    {
        if ( s == null )
        {
            return null;
        }
        
        return UNDERLINE + s + NORMAL;
    }

    public String reverse( String s )
    {
        if ( s == null )
        {
            return null;
        }
        
        return REVERSE + s + NORMAL;
    }

    
    /** Returns the length of the String without any escape codes 
     * 
     */
    public int length( String s )
    {
        if ( s == null )
        {
            return 0;
        }
        
        Matcher m = TERMINAL_ESCAPE_CODES.matcher( s );
        
        if ( m.find() )
        {
            return m.replaceAll( "" ).length();
        }
        else
        {
            return s.length();
        }
    }

    public int encodingLength( String s )
    {
        if ( s == null )
        {
            return 0;
        }
        
        return s.length() - length( s );
    }
}
