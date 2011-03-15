package org.codehaus.mojo.pomtools.console.screens;

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

import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleUtils;
import org.codehaus.mojo.pomtools.console.toolkit.event.CatchAllListener;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.helpers.LocalStringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ErrorMessageScreen
    extends AbstractModelScreen
{

    private final String title;
    private final String errorMessage;

    public ErrorMessageScreen( String title, String errorMessage )
    {
        super( title );
        
        this.title = title;
        this.errorMessage = errorMessage;
    }

    public ConsoleScreenDisplay getDisplay()
        throws ConsoleExecutionException
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append( getHeader( getTerminal().bold( title ) ) );
        
        sb.append( LocalStringUtils.makeEndWith( ConsoleUtils.wordWrap( errorMessage, getTerminalWidth() ),
                                                 NEWLINE ) );
        
        sb.append( NEWLINE );

        return createDisplay( sb.toString(), PRESS_ENTER_TO_CONTINUE, false );
    }

    public ConsoleEventDispatcher getEventDispatcher()
        throws ConsoleExecutionException
    {
        return new ConsoleEventDispatcher().add( new CatchAllListener()
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                event.setReturnToPreviousScreen();
            }
        } );
    }

}
