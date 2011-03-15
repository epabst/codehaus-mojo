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
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEventClosure;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.event.DefaultListener;
import org.codehaus.mojo.pomtools.console.toolkit.event.MatchingListener;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ConfirmYesNoScreen extends AbstractModelScreen
{
    private final String prompt;
    private final ConsoleEventClosure yesClosure;
    private final ConsoleEventClosure noClosure;
    
    public ConfirmYesNoScreen( String prompt, ConsoleEventClosure yesClosure, ConsoleEventClosure noClosure )
    {
        super( null );

        this.prompt = prompt;
        this.yesClosure = yesClosure;
        this.noClosure = noClosure;
    }

    public ConsoleScreenDisplay getDisplay()
        throws ConsoleExecutionException
    {
        ConsoleScreenDisplay display = createDisplay( null, getTerminal().bold( prompt + " [y/n]" ), false );
        
        display.setRedrawPreviousContents( true );
        
        return display;
    }

    public ConsoleEventDispatcher getEventDispatcher()
        throws ConsoleExecutionException
    {
        ConsoleEventDispatcher ced = new ConsoleEventDispatcher();

        ced.add( new MatchingListener( new String[] { "yes", "y", "n", "no" }, "Enter yes or no." )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                if ( event.getConsoleInput().substring( 0, 1 ).equalsIgnoreCase( "y" ) )
                {
                    if ( yesClosure != null )
                    {
                        yesClosure.execute( event );
                    }
                    
                    event.setReturnToPreviousScreen( );                    
                }
                else
                {
                    if ( noClosure != null )
                    {
                        noClosure.execute( event );
                    }
                    
                    event.setReturnToPreviousScreen( );
                }

            }

        } );
        
        ced.add( new DefaultListener()
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                event.setReturnToPreviousScreen( );
            }
        } );
        
        return ced;
    }


}
