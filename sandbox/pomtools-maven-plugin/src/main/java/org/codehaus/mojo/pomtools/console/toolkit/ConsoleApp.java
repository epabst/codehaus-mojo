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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.terminal.Terminal;
import org.codehaus.mojo.pomtools.helpers.LocalStringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ConsoleApp 
{
    private final Terminal terminal;
    
    private final InitialState initialState;
    
    private static final String NEWLINE = "\n";

    private LinkedList screens;
    
    private static ThreadLocal threadLocal = new ThreadLocal();

    public ConsoleApp( Terminal terminal, InitialState initialState )
    {
        this.terminal = terminal;
        this.initialState = initialState;
        
        initialize();
    }
    
    protected void initialize()
    {
        this.screens = new LinkedList();
        
        screens.add( initialState.getInitialScreen() );
    }
    
    /** Returns the ThreadLocal instance of this ConsoleApp. You must
     * call {@link #setCurrent(ConsoleApp)} before calling this function.
     * @throws IllegalStateException if the current connection is not set
     */
    public static ConsoleApp getCurrent() 
    {
        ConsoleApp app = (ConsoleApp) threadLocal.get();
        if ( app == null ) 
        {
            throw new IllegalStateException( "ConsoleApp in the thread-local variable was null, " 
                                            + "please call setCurrent() first" );
        }
        return app;
    }

    /** Sets the ThreadLocal ConsoleApp instance.  See {@see #getCurrent} for more information. */
    public static void setCurrent( ConsoleApp app ) 
    { 
        threadLocal.set( app ); 
    }

    public static boolean hasCurrent() 
    {
        return threadLocal.get() != null;
    }
    

    public void execute()
        throws ConsoleExecutionException
    {
        ConsoleScreen screen = null;
        ConsoleScreenDisplay display = null;
        ConsoleScreenDisplay previousDisplay = null;
        
        List consoleMessages = null;
        
        while ( true )
        {
            screen = (ConsoleScreen) screens.getLast();

            ConsoleEventDispatcher dispatcher = screen.getEventDispatcher();
            
            previousDisplay = display;
            display = screen.getDisplay();

            drawScreenContents( display, consoleMessages, previousDisplay );
            
            // Now that we have used it, reset it back to null
            consoleMessages = null;
            
            ConsoleEvent event = new ConsoleEvent( terminal, terminal.readLine() );

            // Now make our screen handle the event.
            dispatcher.dispatchEvent( event );

            // Now see what action (if any) we need to take on the result
            if ( !event.isConsumed() )
            {
                event.addConsoleMessage( "Your response was not understood, please try again." );                    
            }
            
            if ( event.getConsoleMessages() != null && !event.getConsoleMessages().isEmpty() )
            {
                consoleMessages = event.getConsoleMessages();
            }
            
            
            if ( event.isExitApplication() )
            {
                break;
            }
            else if ( event.isReturnToFirstScreen() )
            {
                initialize();
            }
            else if ( event.isReturnToPreviousScreen() )
            {
                if ( screens.size() > 1 )
                {
                    screens.removeLast();
                }
            }
            else if ( event.getNextScreen() != null )
            {
                screens.add( event.getNextScreen() );
            }
        }
    }
    
    private void drawScreenContents( ConsoleScreenDisplay display, List consoleMessages, 
                                     ConsoleScreenDisplay previousDisplay )
        throws ConsoleExecutionException
    {
        if ( terminal.supportsClearScreen() && display.isRedrawPreviousContents() ) 
        {
            display.setContents( previousDisplay.getContents() );            
        }
        
        if ( terminal.supportsClearScreen() && display.isClearScreen() )
        {
            terminal.clearScreen();
        }

        StringBuffer sb = new StringBuffer();
        if ( display.getContents() != null )
        {
            sb.append( LocalStringUtils.makeEndWith( display.getContents(), NEWLINE ) );
            
            sb.append( NEWLINE );
        }
        
        if ( consoleMessages != null )
        {
            for ( Iterator iter = consoleMessages.iterator(); iter.hasNext(); )
            {
                sb.append( LocalStringUtils.makeEndWith( terminal.bold( "<< " + (String) iter.next() ), NEWLINE ) );
            }
        }
        
        sb.append( display.getPrompt() );
        
        sb.append( " " );
        
        terminal.write( sb.toString() );
    }
    
    public Terminal getTerminal()
    {
        return terminal;
    }
    
    public interface InitialState
    {
        ConsoleScreen getInitialScreen();
    }

}