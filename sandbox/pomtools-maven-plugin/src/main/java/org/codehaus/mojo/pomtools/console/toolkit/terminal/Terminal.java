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

import java.util.regex.Pattern;

import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public interface Terminal
{
    Pattern BOLD_START = Pattern.compile( "<b>" );
    Pattern BOLD_END   = Pattern.compile( "</b>" );
    
    void write( String s )
        throws ConsoleExecutionException;

    String readLine()
        throws ConsoleExecutionException;

    boolean supportsClearScreen();

    void clearScreen()
        throws ConsoleExecutionException;
    
    boolean supportsFormatting();
    
    String bold( String s );
    
    String underline( String s );
    
    String reverse( String s );
    
    int length( String s );
    
    int encodingLength( String s );
}
