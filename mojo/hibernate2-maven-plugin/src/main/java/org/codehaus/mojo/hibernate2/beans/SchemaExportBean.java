package org.codehaus.mojo.hibernate2.beans;

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

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The Bean which serves as Proxy To Hibernate API <br/>
 *
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka </a>
 * @author <a href="cameron@braid.com.au">Cameron Braid </a>
 * @version $Id$
 */
public class SchemaExportBean
    extends SchemaUpdateBean
{
    private String schemaOutputFile = null;

    private String delimiter = null;

    private boolean drop = false;

    public String getSchemaOutputFile()
    {
        return schemaOutputFile;
    }

    public String getOutputFile()
    {
        return schemaOutputFile;
    }

    public void setSchemaOutputFile( String string )
    {
        schemaOutputFile = string;
    }

    public void setOutputFile( String string )
    {
        print( "outputFile [" + string + "]" );

        String path = string.substring( 0, string.lastIndexOf( '/' ) );

        File f = new File( path );

        f.mkdirs();

        schemaOutputFile = string;
    }

    public boolean isDrop()
    {
        return drop;
    }

    public void setDrop( boolean b )
    {
        print( "drop [" + drop + "]" );
        drop = b;
    }

    public String getDelimiter()
    {
        return delimiter;
    }

    public void setDelimiter( String string )
    {
        print( "delimiter [" + delimiter + "]" );
        delimiter = string;
    }

    /**
     * Hibernate requires that Java classes (beans) are accesible on the
     * classpath. As they are not in plugin classpath we have to take care
     * about. To assure that we have them visible for plugin classloader we will
     * make temporay change to context classloader which will be restored when
     * method terminates.
     */
    public void execute()
        throws MojoExecutionException
    {
        try
        {
            Thread currentThread = Thread.currentThread();

            ClassLoader oldClassLoader = currentThread.getContextClassLoader();

            try
            {
                currentThread.setContextClassLoader( getClassLoader() );

                exportSchema();
            }
            finally
            {
                currentThread.setContextClassLoader( oldClassLoader );
            }
        }
        catch ( Exception ex )
        {
            throw new MojoExecutionException( "Error executing SchemaExportBean execute", ex );
        }
    }

    private void exportSchema()
        throws HibernateException, IOException
    {
        Configuration cfg = getConfiguration();

        SchemaExport schemaExport = getSchemaExport( cfg );

        schemaExport.setOutputFile( schemaOutputFile );

        schemaExport.setDelimiter( delimiter );

        if ( isDrop() )
        {
            schemaExport.drop( getText(), !getText() );
        }
        else
        {
            schemaExport.create( getText(), !getText() );
        }
    }

    private SchemaExport getSchemaExport( Configuration cfg )
        throws HibernateException, IOException
    {
        SchemaExport schemaExport;

        if ( getProperties() == null )
        {
            schemaExport = new SchemaExport( cfg );
        }
        else
        {
            Properties properties = new Properties();

            properties.load( new FileInputStream( getProperties() ) );

            schemaExport = new SchemaExport( cfg, properties );
        }

        schemaExport.setOutputFile( getOutputFile() );

        schemaExport.setDelimiter( getDelimiter() );

        return schemaExport;
    }
}
