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
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.vafer.dependency.Clazzpath;
import org.vafer.dependency.ClazzpathUnit;

/**
 * Common class for the minijar mojos 
 */
public abstract class AbstractPluginMojo
    extends AbstractMojo
{
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    
    
    /**
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    protected File buildDirectory;

    
    
    /**
     * If provided the default is to relocate no dependencies
     * but the ones specified. This parameter is mutual
     * exclusive to excludeDependenciesInRelocation.
     * 
     * @parameter expression="${includeDependenciesInRelocation}"
     */
    protected HashSet includeDependenciesInRelocation = null;

    /**
     * If provided the default is to relocate all dependencies
     * but exclude the ones specified. This parameter is mutual
     * exclusive to includeDependenciesInRelocation.
	 *
     * @parameter expression="${excludeDependenciesInRelocation}"
     */
    protected HashSet excludeDependenciesInRelocation = null;


    
    /**
     * If provided the default is to include no dependencies
     * but the ones specified. This parameter is mutual
     * exclusive to excludeDependencies.
     * 
     * @parameter expression="${includeDependencies}"
     */
    protected HashSet includeDependencies = null;

    /**
     * If provided the default is to include all dependencies
     * but remove the ones specified. This parameter is mutual
     * exclusive to includeDependencies.
	 *
     * @parameter expression="${excludeDependencies}"
     */
    protected HashSet excludeDependencies = null;
        
    
    
    /**
     * By default minijar will analyse the class dependencies and remove
     * classes that are not required for the execution of the project.
     * See the "keep.." parameters to explicitly override the behaviour
     * for classes or resource that only loaded via reflection or set
     * this parameter to false to turn off the magic.
     * 
     * @parameter expression="${stripUnusedClasses}" default-value="true"
     */
    protected boolean stripUnusedClasses;

    
    /**
     * Explicitly mark all classes from the specified artifacts to be
     * kept - no matter whether the analysis of minijar would suggest
     * to remove (some of) them.
     *  
     * @parameter expression="${keepUnusedClassesFromArtifacts}"
     */
    protected HashSet keepUnusedClassesFromArtifacts = new HashSet();
    
    /**
     * Explicitly mark classes matching the given patterns to be
     * kept - no matter whether the analysis of minijar suggests
     * otherwise.
     * 
     * @parameter expression="${keepUnusedClasses}"
     */
    protected HashSet keepUnusedClasses = new HashSet();

    
    protected MavenProject getProject()
    {
        if ( project.getExecutionProject() != null )
        {
            return project.getExecutionProject();
        }
        else
        {
            return project;
        }
    }
    
    /**
     * Substitute the variables in the given expression with the
     * values from the Map.
     * 
     * @param pVariables
     * @param pExpression
     * @return
     */
    protected String replaceVariables( final Map pVariables, final String pExpression )
    {
    	
    	final char[] s = pExpression.toCharArray();
    	
    	final char[] open = "[".toCharArray();
    	final char[] close = "]".toCharArray();
    	
    	final StringBuffer out = new StringBuffer();
    	StringBuffer sb = new StringBuffer();
    	char[] watch = open;
    	int w = 0;
    	for (int i = 0; i < s.length; i++)
    	{
			char c = s[i];
			
			if ( c == watch[w] )
			{
				w++;
				if (watch.length == w)
				{					
					if(watch == open)
					{
						// found open
						out.append( sb );						
						sb = new StringBuffer();
						watch = close;
					}
					else if ( watch == close )
					{
						// found close
						final String variable = (String) pVariables.get( sb.toString() ); 
						if ( variable != null )
						{
							out.append( variable );
						}
						else
						{
							getLog().error( "Unknown variable " + sb );
						}
						sb = new StringBuffer();
						watch = open;
					}					
					w = 0;
				}
			}
			else
			{				
				if  (w > 0 )
				{
					sb.append( watch, 0, w );
				}

				sb.append( c );
				
				w = 0;
			}
		}
    	
    	if ( watch == open )
    	{
    		out.append( sb );
    	}

    	return out.toString();    	
    }


    public boolean isInKeepUnusedClassesFromArtifacts( final Artifact artifact ) {
    	
    	final String id = artifact.getGroupId() + ":" + artifact.getArtifactId();

    	return keepUnusedClassesFromArtifacts.contains(id);
    }

    public boolean isInKeepUnusedClasses( final String clazzname ) {

    	// FIXME: do matching
    	
    	return keepUnusedClasses.contains(clazzname);
    }

    /**
     * Converts a Set of artifact id's into a Set of artifacts
     * @param artifacts
     * @param ids
     * @return null if no ids were given otherwise the Set of artifacts
     */
    private Set getArtifactsFromIds( final Set artifacts, final Set ids )
    {
    	if ( ids == null )
    	{
    		return null;
    	}
    	
    	final Set result = new HashSet();
    	
    	for ( Iterator it = artifacts.iterator(); it.hasNext(); )
    	{
			final Artifact artifact = (Artifact) it.next();
			
			final String id = artifact.getGroupId() + ":" + artifact.getArtifactId();
			
			if ( ids.contains( id ) )
			{
				result.add( artifact );
			}
		}
    	
    	return result;
    }
    
    
    private Set combine( final Set all, final Set included, final Set excluded, final Set defaults )
    {
    	final Set result = new HashSet();
    	
    	if ( excluded != null )
    	{
    		result.addAll(all);
    		result.removeAll(excluded);
    	}

    	if ( included != null )
    	{
    		result.addAll(included);
    	}

    	if ( excluded == null && included == null)
    	{
    		result.addAll(defaults);
    	}
    	
    	return result;
    }
    
    
    public abstract void execute( final Set removable, final Set dependencies, final Set relocateDependencies )
		throws MojoExecutionException;


    /**
     * Main entry point
     * @throws MojoExecutionException on error
     */
    public void execute()
        throws MojoExecutionException
    {
        final Set removable = new HashSet();

        /** START - THIS SHOULD NOT BE REQUIRED **/
        if ( excludeDependencies != null && excludeDependencies.size() == 0 )
        {
        	excludeDependencies = null;
        }
        if ( includeDependencies != null && includeDependencies.size() == 0 )
        {
        	includeDependencies = null;
        }
        if ( excludeDependenciesInRelocation != null && excludeDependenciesInRelocation.size() == 0 )
        {
        	excludeDependenciesInRelocation = null;
        }
        if ( includeDependenciesInRelocation != null && includeDependenciesInRelocation.size() == 0 )
        {
        	includeDependenciesInRelocation = null;
        }
        /** STOP - THIS SHOULD NOT BE REQUIRED **/
        
        if ( excludeDependencies != null && includeDependencies != null ) {
        	throw new MojoExecutionException( "Both parameters excludeDependencies and includeDependencies are mutual exclusive" );
        }

        if ( excludeDependenciesInRelocation != null && includeDependenciesInRelocation != null ) {
        	throw new MojoExecutionException( "Both parameters excludeDependenciesInRelocation and includeDependenciesInRelocation are mutual exclusive" );
        }

        final Set projectArtifacts = getProject().getArtifacts(); 
        
        final Set dependencies = combine(
        		projectArtifacts,
        		getArtifactsFromIds( projectArtifacts, includeDependencies ),
        		getArtifactsFromIds( projectArtifacts, excludeDependencies ),
        		projectArtifacts
        		);
        
        final Set relocateDependencies = combine(
        		projectArtifacts,
        		getArtifactsFromIds( projectArtifacts, includeDependenciesInRelocation ),
        		getArtifactsFromIds( projectArtifacts, excludeDependenciesInRelocation ),
        		projectArtifacts
        		);

        getLog().debug( "dependencies: " + dependencies );
        getLog().debug( "relocateDependencies: " + relocateDependencies );
        
        if ( stripUnusedClasses )
        {
            getLog().info( "Calculating transitive hull of class dependencies." );

            try
            {
                final Artifact projectArtifact = getProject().getArtifact();
                
                if ( projectArtifact == null )
                {
                    throw new MojoExecutionException( "No project artifact" );
                }
                
                final File projectArtifactFile = projectArtifact.getFile();
                
                if ( projectArtifactFile == null )
                {
                    throw new MojoExecutionException( "No project artifact file" );
                }
                
            	final Clazzpath clazzpath = new Clazzpath();
                final ClazzpathUnit jar = new ClazzpathUnit( clazzpath, projectArtifactFile.getAbsolutePath() );
            
                for ( Iterator i = project.getArtifacts().iterator(); i.hasNext(); )
                {
                    final Artifact dependency = (Artifact) i.next();
                    
                    if ( !dependencies.contains( dependency ) )
                    {
                    	getLog().info( "Ignoring " + dependency );
                    	continue;
                    }                    

                    new ClazzpathUnit( clazzpath, dependency.getFile().getAbsolutePath() );                    	
                }
            
                removable.addAll( clazzpath.getClazzes() );
                
                final int total = removable.size();
                
                removable.removeAll( jar.getClazzes() );
                removable.removeAll( jar.getTransitiveDependencies() );
                
                getLog().info( "Can remove " + removable.size() + " of " + total + " classes (" + (int) ( 100 * removable.size() / total ) + "%)." );                                

                //getLog().debug( "Can remove " + removable );                                

            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Could not analyse classpath dependencies", e );
            }
        }

        execute( removable, dependencies, relocateDependencies );
    }

}
