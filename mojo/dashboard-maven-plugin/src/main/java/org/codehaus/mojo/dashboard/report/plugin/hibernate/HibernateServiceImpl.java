package org.codehaus.mojo.dashboard.report.plugin.hibernate;

/*
 * Copyright 2007 David Vicente
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
import java.net.URL;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * Service Hibernate
 * 
 * @plexus.component role="org.codehaus.mojo.dashboard.report.plugin.hibernate.HibernateService"
 *                   lifecycle-handler="plexus-configurable"
 * 
 * @author David Vicente
 */
public class HibernateServiceImpl implements HibernateService, Initializable
{

    private SessionFactory sessionFactory;

    private Configuration hibConfig;

    private Session session;

    /**
     * @plexus.configuration default-value="hibernate.cfg.xml"
     */
    private String mapping = "hibernate.cfg.xml";

    private String dialect;

    private String driverClass;

    private String url;

    private String username;

    private String password;

    /**
     * @see org.codehaus.mojo.dashboard.report.plugin.hibernate.HibernateService#getSessionFactory()
     */
    public SessionFactory getSessionFactory()
    {
        if ( sessionFactory == null )
        {
            if ( dialect != null )
            {
                hibConfig.setProperty( "hibernate.dialect", dialect );
            }
            if ( driverClass != null )
            {
                hibConfig.setProperty( "hibernate.connection.driver_class", driverClass );
            }
            if ( url != null )
            {
                hibConfig.setProperty( "hibernate.connection.url", url );
            }
            if ( username != null )
            {
                hibConfig.setProperty( "hibernate.connection.username", username );
            }
            if ( password != null )
            {
                hibConfig.setProperty( "hibernate.connection.password", password );
            }             

            try
            {
                sessionFactory = hibConfig.buildSessionFactory();
            }
            catch ( HibernateException e )
            {
                throw new RuntimeException( "Problem creating session factory: ", e );
            }
        }
        return sessionFactory;
    }

    public Session getSession()
    {
        if ( session == null )
        {
            session = getSessionFactory().openSession();
        }
        return session;
    }

    /**
     * @see org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable#initialize()
     */
    public void initialize() throws InitializationException
    {
        hibConfig = new Configuration();

        try
        {       
            File file = new File( mapping );

            if ( file.exists() )
            {
                hibConfig.configure( file );
            }
            else
            {
                URL url = HibernateServiceImpl.class.getClassLoader().getResource( mapping );
                System.out.println( "url = " + url );
                if ( url != null )
                {
                    hibConfig.configure( url );
                }
                else
                {
                    throw new RuntimeException( "Couldn't find mapping file: " + mapping );
                }
            }
        }
        catch ( HibernateException e )
        {
            throw new InitializationException( "Mapping problem.", e );
        }
    }

    public Configuration getConfiguration()
    {
        return hibConfig;
    }

    /**
     * @return the mapping
     */
    public String getMapping()
    {
        return mapping;
    }

    /**
     * @param mapping
     *            the mapping to set
     */
    public void setMapping( String mapping )
    {
        this.mapping = mapping;
    }

    public String getDialect()
    {
        return dialect;
    }

    public void setDialect( String dialect )
    {
        this.dialect = dialect;
    }

    public String getDriverClass()
    {
        return driverClass;
    }

    public void setDriverClass( String driverClass )
    {
        this.driverClass = driverClass;
    }

    public String getPassword()
    {
        return password == null ? "" : password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getConnectionUrl()
    {
        return url;
    }

    public void setConnectionUrl( String url )
    {
        this.url = url;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public void save( Object obj )
    {
        Transaction transac = getSession().beginTransaction();
        session.save( obj );
        transac.commit();
    }

    public void update( Object obj )
    {
        Transaction transac = getSession().beginTransaction();
        session.update( obj );
        transac.commit();
    }

    public void saveOrUpdate( Object obj )
    {
        Transaction transac = getSession().beginTransaction();
        session.saveOrUpdate( obj );
        transac.commit();
    }
}