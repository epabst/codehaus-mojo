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

import java.util.Iterator;
import java.util.List;

import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleUtils;
import org.codehaus.mojo.pomtools.console.toolkit.event.CatchAllListener;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.helpers.LocalStringUtils;
import org.codehaus.mojo.pomtools.validation.ProjectValidationResult;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ModelValidationScreen
    extends AbstractModelScreen
{
    private final List validationResults;
    
    private final String errorMessage;

    public ModelValidationScreen( List validationResults )
    {
        this( validationResults, null );
    }
     
    public ModelValidationScreen( List validationResults, String errorMessage )
    {
        super( null );
        this.validationResults = validationResults;
        
        this.errorMessage = errorMessage;
    }

    public ConsoleScreenDisplay getDisplay()
        throws ConsoleExecutionException
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append( getHeader( "Model Validation Results" ) );
        
        if ( errorMessage != null )
        {
            String tmpMessage = ConsoleUtils.wordWrap( errorMessage, getTerminalWidth() );
            
            sb.append( LocalStringUtils.makeEndWith( tmpMessage, NEWLINE ) )
              .append( NEWLINE );
        }
        
        for ( Iterator iter = validationResults.iterator(); iter.hasNext(); )
        {
            ProjectValidationResult result = (ProjectValidationResult) iter.next();
            
            sb.append( result.getProject().getValueLabel() )
              .append( NEWLINE );
            
            String text = result.getValidationResult().render( "  " );
            
            sb.append( text );
            
            if ( !text.endsWith( NEWLINE ) )
            {
                sb.append( NEWLINE );
            }
        }
        

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
