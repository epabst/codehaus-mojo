package org.codehaus.mojo.commons.attributes;

/*
 * Copyright (c) 2004-2006, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.attributes.compiler.AttributeCompiler;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

public abstract class AbstractCommonsAttributeMojo
    extends AbstractMojo
{

    private final static String PATH_SEPARATOR = ", ";

    /**
     * @parameter expression="${project}"
     * 
     * @required
     */
    protected MavenProject project;
    
    /**
     * @parameter expression="${project.build.directory}"
     * 
     * @required
     */
    protected String projectBuildDirectory;
    
    public AbstractCommonsAttributeMojo()
    {
    }

    public void execute( String sourcePath, File outputDirectory, Set includes, Set excludes ) 
        throws MojoExecutionException
    {

        File sourceDirectory = new File( sourcePath ); 
        if (!shouldExecute( sourceDirectory ))
        {
            return;
        }
        
        outputDirectory.mkdirs();

        Project antProject = new Project();
        AttributeCompiler compiler = new AttributeCompiler();
        compiler.setProject(antProject);
        compiler.setDestdir(outputDirectory);

        /* setting includes / excludes */
        FileSet fs = new FileSet();
        fs.setIncludes( includes.isEmpty() ? "**/*.java" : setToFileSet( includes ) );
        if ( !excludes.isEmpty() )
        {
            fs.setExcludes( setToFileSet( excludes ) );
        }
        fs.setDir(sourceDirectory);
        
        compiler.addFileset(fs);
        
        compiler.execute();
    }

    private boolean shouldExecute( File sourceDirectory )
    {
        // Only execute for java projects
        ArtifactHandler artifactHandler = this.project.getArtifact().getArtifactHandler();

        String error = null;
        if ( !"java".equals( artifactHandler.getLanguage() ) )
        {
            error = "Not executing commons-attributes compiler as this is not a Java project.";
        }
        else if ( !sourceDirectory.exists() )
        {
            error = "No sources found - No commons-attributes compilation done";
        }

        if ( ( error != null ) && getLog().isDebugEnabled() )
        {
            getLog().debug( error );
        }
        return error == null;
    }
    
    private String setToFileSet( Set paths )
    {
        if ( paths.isEmpty() )
        {
            return "";
        }
        
        StringBuffer sb = new StringBuffer();
        Iterator it = paths.iterator();
        while ( it.hasNext() )
        {
            String path = (String) it.next();
            sb.append( path );
            if ( it.hasNext() )
            {
                sb.append( PATH_SEPARATOR );
            }
        }
        return sb.toString();
    }

}
