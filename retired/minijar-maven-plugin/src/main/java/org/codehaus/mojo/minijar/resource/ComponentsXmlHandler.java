package org.codehaus.mojo.minijar.resource;

/*
 * Copyright 2005 The Apache Software Foundation.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;
import org.vafer.dependency.resources.ResourceHandler;
import org.vafer.dependency.resources.Version;
import org.vafer.dependency.utils.Jar;

public final class ComponentsXmlHandler implements ResourceHandler
{
	public static final String COMPONENTS_XML_PATH = "META-INF/plexus/components.xml";
	
	private Map components;

	public void onStartProcessing( JarOutputStream pOutput )
		throws IOException
	{
		components = new LinkedHashMap();
	}
	
	public void onStartJar( Jar pJar, JarOutputStream pOutput )
		throws IOException
	{		
	}
	
	public InputStream onResource( Jar pJar, String oldName, String newName, Version[] versions, InputStream inputStream )
		throws IOException
	{
		if ( COMPONENTS_XML_PATH.equals( oldName ) )
		{
			// needs to be aggregated
			
	        final File file = File.createTempFile( "minijar", "tmp" );
	        file.deleteOnExit();
	        
	        final OutputStream os = new FileOutputStream( file );
	        IOUtils.copy( inputStream, os );
	        os.close();
			
	        final Xpp3Dom dom;
	        
	        try
	        {
	            dom = Xpp3DomBuilder.build( new FileReader( file ) );
	        }
	        catch ( Exception e )
	        {
	            throw new IOException( "Error parsing components.xml in " + pJar + " at " + oldName );
	        }

	        final Xpp3Dom[] children = dom.getChild( "components" ).getChildren( "component" );
	        
	        for ( int i = 0; i < children.length; i++ )
	        {
	            final Xpp3Dom component = children[i];

	            final String role = component.getChild( "role" ).getValue();

	            final Xpp3Dom child = component.getChild( "role-hint" );

	            final String roleHint = child != null ? child.getValue() : "";

	            components.put( role + roleHint, component );
	        }	        

	        return new FileInputStream(file);	        
		}

		return inputStream;
	}

	public void onStopJar( Jar pJar, JarOutputStream pOutput )
		throws IOException
	{		
	}

	public void onStopProcessing( JarOutputStream pOutput )
		throws IOException
	{
		if ( components.size() == 0 )
		{
			// no components information available
			return;
		}

        final Xpp3Dom dom = new Xpp3Dom( "component-set" );
        final Xpp3Dom componentDom = new Xpp3Dom( "components" );

        dom.addChild( componentDom );

        for ( Iterator it = components.values().iterator(); it.hasNext(); )
        {
            final Xpp3Dom component = (Xpp3Dom) it.next();
            componentDom.addChild( component );
        }

        
		// insert aggregated license information into new jar
		        
		pOutput.putNextEntry( new JarEntry( COMPONENTS_XML_PATH ) );

        Xpp3DomWriter.write( new OutputStreamWriter( pOutput ), dom );
	}

}
