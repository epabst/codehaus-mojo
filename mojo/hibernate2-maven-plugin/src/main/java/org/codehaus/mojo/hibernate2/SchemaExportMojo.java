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
import org.codehaus.mojo.hibernate2.beans.SchemaExportBean;

/**
 * @goal schema-export
 *
 * @requiresDependencyResolution
 *
 * @description A Maven 2.0 Hibernate plugin for schema export
 *
 * @phase process-classes
 *
 * @author <a href="cameron@braid.com.au">Cameron Braid</a>
 * @version $Id$
 */
public class SchemaExportMojo
    extends SchemaUpdateMojo
{
    /**
     * the delimiter to separate each generated DDL statement.
     *
     * @parameter
     */
    private String delimiter;

    /**
     * Indicates whether to drop to generate just a drop table script. Default is false.
     *
     * @parameter default-value=false
     */
    private boolean drop;

    /**
     * a filename to output the DDL statements to
     *
     * @parameter expression="${project.build.directory}/schema/ddl.sql"
     * @required
     */
    private String outputFile;

    protected SchemaExportBean schemaExport;

    public SchemaExportMojo()
    {
        schemaExport = new SchemaExportBean();
    }

    public void execute()
        throws MojoExecutionException
    {
        schemaExport.setQuiet( getQuiet() );

        schemaExport.setConfig( getConfig() );

        schemaExport.setProperties( getProperties() );

        schemaExport.setDelimiter( getDelimiter() );

        schemaExport.setDrop( getDrop() );

        schemaExport.setOutputFile( getOutputFile() );

        schemaExport.setText( getText() );

        schemaExport.setBasedir( getBasedir() );

        schemaExport.setExcludes( getExcludes() );

        schemaExport.setIncludes( getIncludes() );

        try
        {
            schemaExport.setClasspath( buildClasspath() );

            schemaExport.setResources( getResources() );

            schemaExport.execute();
        }
        catch ( Exception ex )
        {
            throw new MojoExecutionException( "Error executing schema export", ex );
        }
    }

    public String getDelimiter()
    {
        return delimiter;
    }

    public void setDelimiter( String delimiter )
    {
        this.delimiter = delimiter;
    }

    public boolean getDrop()
    {
        return drop;
    }

    public void setDrop( boolean drop )
    {
        this.drop = drop;
    }

    public String getOutputFile()
    {
        return outputFile;
    }

    public void setOutputFile( String outputFile )
    {
        this.outputFile = outputFile;
    }
}
