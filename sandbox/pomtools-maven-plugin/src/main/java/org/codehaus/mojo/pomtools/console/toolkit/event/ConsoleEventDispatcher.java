package org.codehaus.mojo.pomtools.console.toolkit.event;

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

import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;


/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ConsoleEventDispatcher
{
    private LinkedList listeners = new LinkedList();

    public ConsoleEventDispatcher()
    {
    }
    
    public ConsoleEventDispatcher add( ConsoleEventListener listener )
    {
        listener.validateConfiguration( this );
        
        listeners.add( listener );

        return this;
    }
    
    public ConsoleEventDispatcher addFirst( ConsoleEventListener listener )
    {
        listener.validateConfiguration( this );
        
        listeners.addFirst( listener );

        return this;
    }

    public void dispatchEvent( ConsoleEvent event )
        throws ConsoleExecutionException
    {
        for ( Iterator i = listeners.iterator(); i.hasNext() && !event.isConsumed(); )
        {
            ConsoleEventListener c = (ConsoleEventListener) i.next();

            if ( c.accept( event ) ) 
            {
                event.consume();
                c.processEvent( event );
            }
        }
    }
   
    public List getListeners()
    {
        return listeners;
    }
        
}