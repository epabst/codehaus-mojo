package org.codehaus.mojo.hibernate3;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.antrun.AntRunMojo;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.util.ReflectionUtils;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Abstract class for any hibernate goal.
 */
public abstract class AbstractHibernateMojo
    extends AbstractMojo
    implements HibernateMojo
{
// ------------------------------ FIELDS ------------------------------

    /**
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    protected MavenSession session;

    /**
     * @parameter expression="${plugin.classRealm}"
     * @required
     */
    protected Object classRealm;

    /**
     * The local Maven repository
     *
     * @parameter expression="${localRepository}"
     * @readonly
     */
    protected ArtifactRepository localRepository;

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface Mojo ---------------------

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            AntRunMojo antRunMojo = new AntRunMojo();
            antRunMojo.setLog( getLog() );
            ReflectionUtils.setVariableValueInObject( antRunMojo, "project", session.getCurrentProject() );
            ReflectionUtils.setVariableValueInObject( antRunMojo, "target", getConfiguration() );
            ReflectionUtils.setVariableValueInObject( antRunMojo, "localRepository", localRepository );
            ReflectionUtils.setVariableValueInObject( antRunMojo, "versionsPropertyName",
                                                      "maven.project.dependencies.versions" );
            antRunMojo.execute();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "There was an error creating the AntRun task.", e );
        }
    }

// -------------------------- OTHER METHODS --------------------------

    protected ClassLoader getAntClassLoader()
        throws MojoExecutionException
    {
        try
        {
            ClassRealm realm;
            if ( classRealm instanceof ClassRealm )
            {
                realm = (ClassRealm) classRealm;
            }
            else
            {
                Class c = Class.forName( "org.codehaus.classworlds.ClassRealmAdapter" );
                Method method = c.getMethod( "getInstance", classRealm.getClass() );
                realm = (ClassRealm) method.invoke( null, classRealm );
            }
            for ( Object o : session.getCurrentProject().getRuntimeClasspathElements() )
            {
                realm.addConstituent( new File( o.toString() ).toURI().toURL() );
            }
            return realm.getClassLoader();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "There was an error creating the ClassLoader", e );
        }
    }
}
