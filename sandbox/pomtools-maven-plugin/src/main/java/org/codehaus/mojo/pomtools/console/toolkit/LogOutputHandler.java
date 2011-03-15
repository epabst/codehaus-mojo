package org.codehaus.mojo.pomtools.console.toolkit;

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

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.components.interactivity.OutputHandler;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class LogOutputHandler implements OutputHandler
{
    private final Log log;

    public LogOutputHandler( Log log )
    {
        this.log = log;        
    }

    public void write( String s )
        throws IOException
    {
        // For this case, prepend a newline so our text starts on the left border
        // rather than after the "[INFO] " that the logger inserts
        log.info( "\n" + s );        
    }
    
//    public void write( String s )
//        throws IOException
//    {
//        String[] lines = StringUtils.split( s, "\n" );
//
//        for ( int i = 0; i < lines.length; i++ )
//        {
//            log.info( lines[i] );
//        }
//    }

    public void writeLine( String s )
        throws IOException
    {
        write( s );
    }

}
