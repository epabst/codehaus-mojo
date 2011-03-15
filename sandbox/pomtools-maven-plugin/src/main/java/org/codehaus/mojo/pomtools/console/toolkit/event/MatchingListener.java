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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.codehaus.mojo.pomtools.console.toolkit.ConsoleConfigurationException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public abstract class MatchingListener
    extends AbstractEventListener
{
    private Set acceptedInput = new HashSet();
    
    private final String descriptionKey;

    public MatchingListener( String inputString, String description )
    {
        this( new String[] { inputString }, description, null );
    }

    public MatchingListener( String[] inputStrings, String description )
    {
        this( inputStrings, description, null );
    }
    
    public MatchingListener( String[] inputStrings, String description, String descriptionKey )
    {
        super( description );
        
        if ( inputStrings == null || inputStrings.length == 0 )
        {
            throw new IllegalArgumentException( "inputStrings must contain at least 1 String to match on" );
        }
        
        this.descriptionKey = StringUtils.defaultString( descriptionKey, inputStrings[0] );
        
        for ( int i = 0; i < inputStrings.length; i++ )
        {
            acceptedInput.add( inputStrings[i].toLowerCase() );
        }
    }

    /** Validates that there are no other MatchingListeners in the dispatcher that
     * accept the same input as this listener.
     * 
     */
    public void validateConfiguration( ConsoleEventDispatcher dispatcher )
        throws ConsoleConfigurationException
    {
        for ( Iterator iter = dispatcher.getListeners().iterator(); iter.hasNext(); )
        {
            ConsoleEventListener listener = (ConsoleEventListener) iter.next();
            
            if ( listener instanceof MatchingListener )
            {
                MatchingListener that = (MatchingListener) listener;
                
                for ( Iterator inputIter = acceptedInput.iterator(); inputIter.hasNext(); )
                {
                    String inputStr = (String) inputIter.next();
                    
                    if ( that.acceptedInput.contains( inputStr ) )
                    {
                        throw new ConsoleConfigurationException( "Duplicate matching listener for the input: "
                                                                 + inputStr 
                                                                 + "\nDescription: " + this.getDescription() 
                                                                 + "\nDescription: " + that.getDescription() );
                    }
                }
            }
        }
    }
    
    public boolean accept( ConsoleEvent event )
    {
        String consoleInput = StringUtils.defaultString( event.getConsoleInput() ).toLowerCase();
        
        return acceptedInput.contains( consoleInput );
    }

    public String getDescriptionKey()
    {
        return descriptionKey;
    }
}