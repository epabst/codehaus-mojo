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
import net.sf.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * The Bean which serves as Proxy To Hibernate API <br/>
 *
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka </a>
 * @author <a href="cameron@braid.com.au">Cameron Braid </a>
 * @version $Id$
 */
public class SchemaUpdateBean
    extends CommonOperationsBean
{
    private String properties = null;

    private String config = null;

    private boolean text = false;

    private List resources = null;

    public boolean getText()
    {
        return text;
    }

    public void setText( boolean b )
    {
        print( "text [" + b + "]" );
        text = b;
    }

    public String getConfig()
    {
        return config;
    }

    public String getProperties()
    {
        return properties;
    }

    public void setConfig( String string )
    {
        print( "config [" + string + "]" );
        config = string;
    }

    public void setProperties( String string )
    {
        print( "properties [" + string + "]" );
        properties = string;
    }

    public void setResources( List resources )
    {
        this.resources = resources;
    }

    public List getResources()
    {
        return this.resources;
    }

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

                updateSchema();
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

    private void updateSchema()
        throws HibernateException, IOException
    {
        Configuration cfg = getConfiguration();

        SchemaUpdate schemaUpdate = getSchemaUpdate( cfg );

        schemaUpdate.execute( getText(), !getText() );
    }

    protected Configuration getConfiguration()
        throws HibernateException, IOException
    {
        Configuration cfg = new Configuration();

        if ( getConfig() != null )
        {
            File f = new File( getConfig() );

            cfg.configure( f );

            print( "Configuration XML file loaded: " + getConfig() );
        }
        else
        {
            print( "No hibernate configuration file loaded: None specified." );
        }

        if ( getProperties() != null )
        {
            Properties properties = new Properties();

            properties.load( new FileInputStream( getProperties() ) );

            cfg.setProperties( properties );

            print( "Configuration Properties file loaded: " + getProperties() );
        }
        else
        {
            print( "No hibernate properties file loaded: None specified." );
        }

        if ( getBasedir() != null )
        {
            File[] files = getIncludeFiles();

            for ( int i = 0; i < files.length; i++ )
            {
                File file = files[ i ];

                print( "added [" + file.getAbsolutePath() + "]" );

                if ( file.getName().endsWith( ".jar" ) )
                {
                    cfg.addJar( file );
                }
                else
                {
                    cfg.addFile( file.getAbsolutePath() );
                }
            }
        }

        if ( resources != null )
        {
            for ( Iterator iter = resources.iterator(); iter.hasNext(); )
            {
                String resource = (String) iter.next();

                URL url = Thread.currentThread().getContextClassLoader().getResource( resource );

                if ( url == null )
                {
                    throw new RuntimeException( "Resource [" + resource + "] not found" );
                }
                cfg.addURL( url );

                print( "added [" + resource + "]" );
            }
        }

        return cfg;
    }

    private SchemaUpdate getSchemaUpdate( Configuration cfg )
        throws HibernateException, IOException
    {
        SchemaUpdate schemaUpdate;

        if ( getProperties() == null )
        {
            schemaUpdate = new SchemaUpdate( cfg );
        }
        else
        {
            Properties properties = new Properties();

            properties.load( new FileInputStream( getProperties() ) );

            schemaUpdate = new SchemaUpdate( cfg, properties );
        }

        return schemaUpdate;
    }

    protected URLClassLoader getClassLoader()
        throws MalformedURLException
    {
        String[] classpath = getClasspath();

        URL[] urls = new URL[classpath.length];

        for ( int i = urls.length - 1; i >= 0; i-- )
        {
            urls[ i ] = new URL( "file:" + classpath[ i ] );

            print( "cp [" + urls[ i ] + "]" );
        }

        return new URLClassLoader( urls );
    }
}
