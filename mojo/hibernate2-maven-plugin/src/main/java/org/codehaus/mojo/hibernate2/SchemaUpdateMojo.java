package org.codehaus.mojo.hibernate2;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.hibernate2.beans.CommonOperationsBean;
import org.codehaus.mojo.hibernate2.beans.SchemaUpdateBean;

import java.io.File;
import java.util.List;

/**
 * @goal schema-update
 *
 * @requiresDependencyResolution
 *
 * @description A Maven 2.0 Hibernate plugin for schema update
 *
 * @phase process-classes
 *
 * @version $Id$
 */
public class SchemaUpdateMojo
    extends CommonOperationsBean
{
    /**
     * @readonly
     * @parameter expression="${project}"
     * @required
     */
    private org.apache.maven.project.MavenProject project;

    /**
     * @readonly
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private String classesPath;

    /**
     * the name of the hibernate.cfg.xml file
     *
     * @parameter
     */
    private String config;

    /**
     * The location of Hibernate configuration file (Java properties file). This file is also set in the runtime by Hibernate for configuring Hibernate Session, so it's better to keep it separately from the project's configuration files like project.properties etc.
     *
     * @parameter
     */
    private String properties;

    /**
     * If set to true, then the generated SQL will be output only to the filesystem, not inserted into the database.
     *
     * @parameter default-value=false
     */
    private boolean text;

    /**
     * List of file resources
     *
     * @parameter
     */
    private List resources;

    private SchemaUpdateBean schemaUpdate;

    public SchemaUpdateMojo()
    {
        schemaUpdate = new SchemaUpdateBean();
    }

    public void execute()
        throws MojoExecutionException
    {
        schemaUpdate.setQuiet( getQuiet() );

        schemaUpdate.setConfig( getConfig() );

        schemaUpdate.setProperties( getProperties() );

        schemaUpdate.setText( getText() );

        schemaUpdate.setBasedir( getBasedir() );

        schemaUpdate.setExcludes( getExcludes() );

        schemaUpdate.setIncludes( getIncludes() );

        try
        {
            schemaUpdate.setClasspath( buildClasspath() );

            schemaUpdate.setResources( getResources() );

            schemaUpdate.execute();
        }
        catch ( Exception ex )
        {
            throw new MojoExecutionException( "Error executing schema export", ex );
        }
    }

    protected String[] buildClasspath()
        throws Exception
    {
        List paths = project.getCompileClasspathElements();

        paths.add( new File( classesPath ).getAbsolutePath() + "/" );

        return (String[]) paths.toArray( new String[ 0 ] );
    }

    public String getConfig()
    {
        return config;
    }

    public void setConfig( String config )
    {
        this.config = config;
    }

    public String getProperties()
    {
        return properties;
    }

    public void setProperties( String properties )
    {
        this.properties = properties;
    }

    public boolean getText()
    {
        return text;
    }

    public void setText( boolean text )
    {
        this.text = text;
    }

    public List getResources()
    {
        return resources;
    }

    public void setResources( List resources )
    {
        this.resources = resources;
    }
}
