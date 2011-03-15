package org.codehaus.mojo.pomtools.console.screens.editors;

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

import org.apache.maven.artifact.InvalidArtifactRTException;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadata;
import org.codehaus.mojo.pomtools.PomToolsPluginContext;
import org.codehaus.mojo.pomtools.console.screens.AbstractModelScreen;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleEvent;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleExecutionException;
import org.codehaus.mojo.pomtools.console.toolkit.ConsoleScreenDisplay;
import org.codehaus.mojo.pomtools.console.toolkit.event.ConsoleEventDispatcher;
import org.codehaus.mojo.pomtools.console.toolkit.event.NumericRangeListener;
import org.codehaus.mojo.pomtools.console.toolkit.widgets.LabeledList;
import org.codehaus.mojo.pomtools.helpers.MetadataHelper;
import org.codehaus.mojo.pomtools.helpers.ModelHelper;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanField;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class EditVersionScreen
    extends AbstractModelScreen
{
    private final ObjectWrapper wrappedObject;
    
    private final BeanField field;
    
    private RepositoryMetadata metadata;
    
    private boolean hasVersions = false;
    
    private EditStringScreen manualEditScreen;
    
    public EditVersionScreen( ObjectWrapper obj, BeanField field )
    {
        super( field.getFullFieldName( obj ) );
    
        this.wrappedObject = obj;
        this.field = field;
        
        try
        {
            MetadataHelper helper = PomToolsPluginContext.getInstance().getMetadataHelper();
            
            this.metadata = helper.getMetadata( obj );
            
            hasVersions = metadata.getMetadata().getVersioning() != null;
        }
        catch ( ArtifactMetadataRetrievalException e )
        {
            // stuff it
            hasVersions = false;
        }
        catch ( InvalidArtifactRTException e )
        {
            // stuff it
            hasVersions = false;
        }
    }
    
    protected boolean isVersionsAvailable()
    {
        return hasVersions;
    }
    
    protected EditStringScreen getManualEditScreen() 
    {
        if ( manualEditScreen == null )
        {
            manualEditScreen = new EditStringScreen( wrappedObject, field );            
        }
        
        String message = ( isVersionsAvailable() )
                         ? null
                         : "Warning: Unable to locate any version information for: " 
                             + ModelHelper.versionlessKey( this.wrappedObject );
        
        manualEditScreen.setAddlMessage( message );
        
        return manualEditScreen;
    }

    public ConsoleScreenDisplay getDisplay()
        throws ConsoleExecutionException
    {
        if ( isVersionsAvailable() )
        {
            return getSelectFromListDisplay();
        }
        else
        {
            return getManualEditScreen().getDisplay();
        }
    }
    
    protected ConsoleScreenDisplay getManualEditDisplay()
        throws ConsoleExecutionException
    {
        return getManualEditScreen().getDisplay();
    }
    
    protected ConsoleScreenDisplay getSelectFromListDisplay() 
        throws ConsoleExecutionException        
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append( getFieldNameHeader(  field.getFullFieldName( wrappedObject )  ) );
        
        String prompt = null;
        
        String currentVersion = (String) wrappedObject.getFieldValue( field );
        
        LabeledList il = new LabeledList( getTerminal(), true, true );
        
        List versions = metadata.getMetadata().getVersioning().getVersions();
        
        int index = 1;
        for ( Iterator i = versions.iterator(); i.hasNext(); )
        {
            String version = (String) i.next();
            
            if ( StringUtils.equals( currentVersion, version ) )
            {
                version += " (current)";
            }
            
            il.add( numberPrompt( index++ ), version );
        }
        
        sb.append( il.getOutput() );
        
        prompt = "Please select a version";
        
        return createDisplay( sb.toString(), prompt );        
    }

    public ConsoleEventDispatcher getEventDispatcher()
        throws ConsoleExecutionException
    {
        if ( !isVersionsAvailable() )
        {
            return getManualEditDispatcher();
        }
        else
        {
            return getSelectFromListDispatcher();
        }
    }
    
    protected ConsoleEventDispatcher getManualEditDispatcher()
        throws ConsoleExecutionException
    {
        return getManualEditScreen().getEventDispatcher();
    }
    
    protected ConsoleEventDispatcher getSelectFromListDispatcher()
        throws ConsoleExecutionException
    {
        ConsoleEventDispatcher ced = getDefaultEventDispatcher();
        
        final List versions = metadata.getMetadata().getVersioning().getVersions();
        
        ced.add( new NumericRangeListener( 1, versions.size(), "Select a version." )
        {
            public void processEvent( ConsoleEvent event )
                throws ConsoleExecutionException
            {
                String version = (String) versions.get( Integer.parseInt( event.getConsoleInput() ) - 1 ); 

                wrappedObject.setFieldValue( field, version );
                
                event.setReturnToPreviousScreen();
            }
        } );
        
        return ced;
    }

}
