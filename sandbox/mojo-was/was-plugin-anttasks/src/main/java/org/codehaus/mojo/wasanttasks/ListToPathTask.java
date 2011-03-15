package org.codehaus.mojo.wasanttasks;

/*
 * The MIT License
 *
 * Copyright (c) 2006, DNB Nor.
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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.util.Iterator;

/*
 * This class gets the artifacts from the Maven2 project and
 * converts them into a pathelement for use by a Maven2 ant
 * based mojo. Based on an original thought by Martin van der Plas
 *
 * Author  : Hermod Opstvedt
 * Version : 1.0
 * Date    : 03 april 2006
 */

public class ListToPathTask
    extends Task
{

    private String targetRef;

    private String mavenproject;

    /**
     * @param mavenproject the mavenproject to set
     */
    public void setMavenproject( String mavenproject )
    {
        log( "mavenproject: " + mavenproject, Project.MSG_DEBUG );
        this.mavenproject = mavenproject;
    }

    /**
     * @param targetRef the targetRef to set
     */
    public void setTargetRef( String targetRef )
    {
        log( "targetRef: " + targetRef, Project.MSG_DEBUG );
        this.targetRef = targetRef;
    }

    public void execute()
        throws BuildException
    {

        try
        {
            log( "Building classpath from dependencies", Project.MSG_DEBUG );

            if ( targetRef == null || targetRef.length() == 0 )
            {
                throw new BuildException( "targetRef not set" );
            }

            if ( mavenproject == null || mavenproject.length() == 0 )
            {
                throw new BuildException( "mavenproject reference not set" );
            }

            MavenProject pom = (MavenProject) getProject().getReferences().get( mavenproject );
            if ( pom == null )
            {
                throw new BuildException( "Unable to get Maven project file (pom)" );
            }
            log( "Found " + pom.getArtifacts().size() + " artifacts", Project.MSG_DEBUG );

            Iterator it2 = pom.getArtifacts().iterator();
            Path path = new Path( getProject() );
            while ( it2.hasNext() )
            {
                path.setLocation( ( (Artifact) it2.next() ).getFile() );
            }
            getProject().addReference( targetRef, path );
        }
        catch ( Exception e )
        {
            log( "Failure: " + e.getMessage() );
            if ( e.getCause() != null )
            {
                log( "Caused by: " + e.getCause().getMessage() );
            }
            throw new BuildException( e );
        }
	}
}
