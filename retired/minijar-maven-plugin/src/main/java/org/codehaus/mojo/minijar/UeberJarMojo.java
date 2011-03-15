package org.codehaus.mojo.minijar;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarOutputStream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.mojo.minijar.resource.ComponentsXmlHandler;
import org.codehaus.mojo.minijar.resource.LicenseHandler;
import org.vafer.dependency.Clazz;
import org.vafer.dependency.Console;
import org.vafer.dependency.resources.ResourceHandler;
import org.vafer.dependency.resources.Version;
import org.vafer.dependency.utils.Jar;
import org.vafer.dependency.utils.JarUtils;

/**
 * Creates an ueberjar including all dependencies into one jar.
 *
 * @goal ueberjar
 * @requiresDependencyResolution compile
 * @execute phase="package"
 */
public final class UeberJarMojo
    extends AbstractPluginMojo
{
    /** @component */
    private MavenProjectHelper projectHelper;

    /**
     * Defines the pattern of the name of final ueber jar.
     * Possible substitutions are [artifactId] [version] and [groupId].
     * 
     * @parameter expression="${name}" default-value="[artifactId]-[version]-ueber.jar"
     */ 
    protected String name;
	
    /**
     * Defines whether the original artifact should be include so that
     * the ueberjar is basically self contained. If set to false the
     * ueberjar will only include the dependencies of the project.
     * 
     * @parameter expression="${includeArtifact}" default-value="true"
     */
    private boolean includeArtifact;

    /**
     * By default the new ueberjar gets attached as an additional
     * artifact. If you want to replace the orignal artifact set
     * this to true.
     * 
     * @parameter expression="${replaceArtifact}" default-value="false"
     */
    private boolean replaceArtifact;

    
    private final ResourceHandler[] handlers = {
    	new LicenseHandler(),
    	new ComponentsXmlHandler()
    };

    
    private InputStream handle( Jar jar, String oldName, String newName, Version[] versions, InputStream inputStream )
		throws IOException
	{
		InputStream is = inputStream;
		for (int i = 0; i < handlers.length; i++)
		{
			if ( is == null )
			{
				return null;
			}

			is = handlers[i].onResource( jar, oldName, newName, versions, is );			
		}
		return is;
    }
    
    /**
     * Creates a combine jar of the dependencies and (as configured) also the build artifact
     *
     * @param pRemovable Set of classes that can be removed
     * @throws MojoExecutionException on error
     */
    public void execute( final Set pRemovable, final Set pDependencies, final Set pRelocateDependencies )
        throws MojoExecutionException
    {
        final Set artifacts = new HashSet(pDependencies);

        final Artifact projectArtifact = getProject().getArtifact();

        if ( includeArtifact )
        {
            getLog().info( "Including project artifact." );
            artifacts.add( projectArtifact );
        }
        
        final Jar[] jars = new Jar[ artifacts.size() ];

        final Iterator it = artifacts.iterator();
        for (int i = 0; i < jars.length; i++)
        {
            final Artifact artifact = (Artifact) it.next();
            final File file = artifact.getFile();
            
            try {
            	final Jar jar = new Jar( file, artifact != projectArtifact );
            	
				jars[i] = jar; 				
			}
            catch ( FileNotFoundException e )
            {
				throw new MojoExecutionException( "Could not locate jar " + file, e );
			}            
        }

        final Map variables = new HashMap();
        variables.put( "artifactId", projectArtifact.getArtifactId() );
        variables.put( "groupId", projectArtifact.getGroupId() );
        variables.put( "version", projectArtifact.getVersion() );

        final String newName = replaceVariables( variables, name );

        final File outputJar = new File( buildDirectory, newName );
        
        try
        {
            JarUtils.processJars(
            		jars,
            		new ResourceHandler() {

						public void onStartProcessing( JarOutputStream pOutput )
							throws IOException
						{
							for (int i = 0; i < handlers.length; i++)
							{
								handlers[i].onStartProcessing( pOutput );								
							}
						}

						public void onStartJar( Jar jar, JarOutputStream pOutput )
							throws IOException
						{
								for (int i = 0; i < handlers.length; i++)
								{
									handlers[i].onStartJar( jar, pOutput );								
								}
						}

						public InputStream onResource( Jar jar, String oldName, String newName, Version[] versions, InputStream inputStream )
							throws IOException
						{
							
							if ( jar != versions[0].getJar() )
							{
								// only process the first version of it
								return null;
							}
							
							
							if ( !oldName.endsWith( ".class" ) )
							{
								return handle( jar, oldName, newName, versions, inputStream );
	        				}

							final String clazzName = oldName.replace( '/' , '.' ).substring( 0, oldName.length() - ".class".length() );
							
							if (pRemovable.contains(new Clazz ( clazzName )))
							{
								// FIXME: artifact not available
//								if (isInKeepUnusedClassesFromArtifacts( artifact ) )
//								{
//									return handle( jar, oldName, newName, versions, inputStream );
//								}

								if ( isInKeepUnusedClasses( name ) )
								{
									return handle( jar, oldName, newName, versions, inputStream );
								}

								return null;
							}
							
							return handle( jar, oldName, newName, versions, inputStream );
						}

						public void onStopJar( Jar pJar, JarOutputStream pOutput )
							throws IOException
						{
								for (int i = 0; i < handlers.length; i++)
								{
									handlers[i].onStopJar( pJar, pOutput );								
								}
						}

						public void onStopProcessing( JarOutputStream pOutput )
							throws IOException
						{
								for (int i = 0; i < handlers.length; i++)
								{
									handlers[i].onStopProcessing( pOutput );								
								}
						}
            			
            		},
            		new FileOutputStream( outputJar ),
            		new Console()
            		{
            			public void println( String pString )
            			{
            				getLog().debug( pString );
            			}            	
            		}
            	);
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Could not create combined output jar " + outputJar, e );
        }

		if ( replaceArtifact )
		{
			getLog().info( "Replacing artifact." );
			outputJar.renameTo( projectArtifact.getFile() );
		}
		else
		{
			getLog().info( "Attaching artifact." );
			projectHelper.attachArtifact( getProject(), "jar", "ueber", outputJar );
		}
    }

}
