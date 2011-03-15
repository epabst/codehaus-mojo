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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.mojo.pomtools.console.toolkit.terminal.Terminal;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ConsoleEvent
{
    private boolean consumed = false;

    private boolean exitApplication = false;

    private final Terminal terminal;

    private final String input;

    private ConsoleScreen nextScreen;

    private List consoleMessages;

    private boolean returnToPreviousScreen = false;
    
    private boolean returnToFirstScreen = false;

    public ConsoleEvent( Terminal terminal, String input )
    {
        this.terminal = terminal;
        this.input = input;
    }

    /**
     * Consumes this event.
     */
    public void consume()
    {
        consumed = true;
    }

    /**
     * Returns whether this event has been consumed.
     */
    public boolean isConsumed()
    {
        return consumed;
    }

    public String getConsoleInput()
    {
        return input;
    }

    public boolean isExitApplication()
    {
        return exitApplication;
    }

    public void setExitApplication( boolean exitApplication )
    {
        this.exitApplication = exitApplication;
    }

    public ConsoleScreen getNextScreen()
    {
        return nextScreen;
    }

    public void setNextScreen( ConsoleScreen nextScreen )
    {
        this.nextScreen = nextScreen;
    }

    public boolean isReturnToPreviousScreen()
    {
        return returnToPreviousScreen;
    }

    public void setReturnToPreviousScreen( boolean returnToPreviousScreen )
    {
        this.returnToPreviousScreen = returnToPreviousScreen;
    }

    public void setReturnToPreviousScreen()
    {
        setReturnToPreviousScreen( true );
    }
    
    public boolean isReturnToFirstScreen()
    {
        return returnToFirstScreen;
    }

    public void setReturnToFirstScreen( boolean returnToFirstScreen )
    {
        this.returnToFirstScreen = returnToFirstScreen;
    }
    
    public void setReturnToFirstScreen()
    {
        setReturnToFirstScreen( true );
    }

    public List getConsoleMessages()
    {
        return consoleMessages;
    }

    /** Adds a message to be displayed to the user on the next screen redraw.
     * 
     * @param consoleMessage the message to display
     */
    public void addConsoleMessage( String consoleMessage )
    {
        if ( this.consoleMessages == null )
        {
            this.consoleMessages = new ArrayList();
        }
        
        this.consoleMessages.add( consoleMessage );
    }

    public Terminal getTerminal()
    {
        return terminal;
    }
}
