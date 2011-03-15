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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.vafer.dependency.Clazz;
import org.vafer.dependency.Console;
import org.vafer.dependency.resources.ResourceHandler;
import org.vafer.dependency.resources.Version;
import org.vafer.dependency.utils.Jar;
import org.vafer.dependency.utils.JarUtils;


/**
 * Creates stripped down versions of the dependencies.
 * 
 * @goal minijars
 * @requiresDependencyResolution compile
 * @execute phase="package"
 */
public final class MiniJarsMojo
    extends AbstractPluginMojo
{
    /**
     * Defines the pattern of the name of final ueber jar.
     * Possible substitutions are [artifactId] [version] and [groupId].
     * 
     * @parameter expression="${name}" default-value="[artifactId]-[version]-mini.jar"
     */
    protected String name;

    /**
     * Creates individual stripped jars of the dependencies
     * @param pRemovable Set of classes that are supposed to be removed
     * @throws MojoExecutionException on error
     */
    public void execute( final Set pRemovable, final Set pDependencies, final Set pRelocateDependencies )
        throws MojoExecutionException    
    {
    	
        for ( Iterator i = pDependencies.iterator(); i.hasNext(); )
        {
            final Artifact dependency = (Artifact) i.next();
            
            final Map variables = new HashMap();
            
            variables.put( "artifactId", dependency.getArtifactId() );
            variables.put( "groupId", dependency.getGroupId() );
            variables.put( "version", dependency.getVersion() );
            
            final String newName = replaceVariables( variables, name ); 
            	            	
            final File inputJar = dependency.getFile();
            final File outputJar = new File( buildDirectory, newName );
            
            try
            {
            	final Jar jar = new Jar( inputJar, false );
            	
            	JarUtils.processJars(
            			new Jar[] { jar },
            			new ResourceHandler() {
            				
							public void onStartProcessing( JarOutputStream pOutput )
								throws IOException
							{
							}

							public void onStartJar( Jar pJar, JarOutputStream pOutput )
								throws IOException
							{
							}

							public InputStream onResource( Jar jar, String oldName, String newName, Version[] versions, InputStream inputStream )
							{

								if ( jar != versions[0].getJar() )
								{
									// only process the first version of it

									getLog().info( "Ignoring resource " + oldName);

									return null;
								}

								final String clazzName = oldName.replace( '/' , '.' ).substring( 0, oldName.length() - ".class".length() );
								
								if ( pRemovable.contains(new Clazz ( clazzName ) ) )
								{
									if ( isInKeepUnusedClassesFromArtifacts( dependency ) )
									{
										return inputStream;
									}
									
									if ( isInKeepUnusedClasses( name ) )
									{
										return inputStream;
									}
									
									return null;
								}
								
								return inputStream;
							}

							public void onStopJar(Jar pJar, JarOutputStream pOutput)
								throws IOException
							{
							}

							public void onStopProcessing(JarOutputStream pOutput)
								throws IOException
							{
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
            catch ( ZipException ze )
            {
                getLog().info( "No references to jar " + inputJar.getName() + ". You can safely omit that dependency." );
                
                if ( outputJar.exists() )
                {
                    outputJar.delete();
                }
                continue;
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Could not create mini jar " + outputJar, e );
            }
            
            getLog().info( "Original length of " + inputJar.getName() + " was " + inputJar.length() + " bytes. " + "Was able shrink it to " + outputJar.getName() + " at " + outputJar.length() + " bytes (" + (int) ( 100 * outputJar.length() / inputJar.length() ) + "%)" );
        }
    }
}
