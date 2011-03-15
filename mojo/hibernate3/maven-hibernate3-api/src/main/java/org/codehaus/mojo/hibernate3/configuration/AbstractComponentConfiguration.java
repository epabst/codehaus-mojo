package org.codehaus.mojo.hibernate3.configuration;

import org.codehaus.mojo.hibernate3.ExporterMojo;
import org.codehaus.mojo.hibernate3.HibernateUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.xml.sax.EntityResolver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public abstract class AbstractComponentConfiguration
    implements ComponentConfiguration
{
// ------------------------------ FIELDS ------------------------------

    private ExporterMojo exporterMojo;

// --------------------- GETTER / SETTER METHODS ---------------------

    public ExporterMojo getExporterMojo()
    {
        return exporterMojo;
    }

    public void setExporterMojo( ExporterMojo exporterMojo )
    {
        this.exporterMojo = exporterMojo;
    }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface ComponentConfiguration ---------------------

    /**
     * @see ComponentConfiguration#getConfiguration(ExporterMojo)
     */
    public Configuration getConfiguration( ExporterMojo exporterMojo )
        throws MojoExecutionException
    {
        setExporterMojo( exporterMojo );

        validateParameters();

        Configuration configuration = createConfiguration();
        if ( configuration == null )
        {
            throw new MojoExecutionException( "Couldn't create Configuration object" );
        }
        doConfiguration( configuration );
        configuration.buildMappings();
        return configuration;
    }

// -------------------------- OTHER METHODS --------------------------

    protected abstract Configuration createConfiguration();

    protected void doConfiguration( Configuration configuration )
    {
        String entityResolver = getExporterMojo().getComponentProperty( "entityresolver" );
        if ( entityResolver != null )
        {
            Object object = HibernateUtils.getClass( entityResolver, this.getClass() );
            if ( object != null )
            {
                configuration.setEntityResolver( (EntityResolver) object );
            }
        }

        String namingStrategy = getExporterMojo().getComponentProperty( "namingstrategy" );
        if ( namingStrategy != null )
        {
            Object object = HibernateUtils.getClass( namingStrategy, this.getClass() );
            if ( object != null )
            {
                getExporterMojo().getLog().info( "Using as namingstrategy " + namingStrategy );
                configuration.setNamingStrategy( (NamingStrategy) object );
            }
            else
            {
                getExporterMojo().getLog().error( "Couldn't resolve " + namingStrategy );
            }
        }

        URL configurationUrl = getConfigurationFile();
        if ( configurationUrl != null )
        {
            configuration.configure( configurationUrl );
        }

        Properties propertyFile = getPropertyFile();
        if ( propertyFile != null )
        {
            configuration.setProperties( propertyFile );
        }
    }

    protected URL getConfigurationFile()
    {
        String configurationFile =
            getExporterMojo().getComponentProperty( "configurationfile", "src/main/resources/hibernate.cfg.xml" );
        String configurationResource =
            getExporterMojo().getComponentProperty( "configurationresource", "hibernate.cfg.xml" );
        final File basedir = getExporterMojo().getProject().getBasedir();
        getExporterMojo().getLog().debug( "basedir: " + basedir );

        URL config = null;
        try
        {
            File configfile = HibernateUtils.getFile( basedir, configurationFile );
            if ( configfile == null )
            {
                getExporterMojo().getLog().debug(
                    configurationFile + " not found within the project. Trying absolute path." );
                configfile = HibernateUtils.getFile( null, configurationFile );
            }
            if ( configfile != null )
            {
                config = configfile.toURI().toURL();
            }
        }
        catch ( MalformedURLException e )
        {
            getExporterMojo().getLog().warn( "Configuration file path was malformed", e );
        }

        if ( config == null )
        {
            config = Thread.currentThread().getContextClassLoader().getResource( configurationResource );
        }

        if ( config != null )
        {
            getExporterMojo().getLog().info( "Configuration XML file loaded: " + config.toString() );
            return config;
        }

        getExporterMojo().getLog().info( "No hibernate configuration file loaded." );
        return null;
    }

    protected Properties getPropertyFile()
    {
        String propertyFile =
            getExporterMojo().getComponentProperty( "propertyfile", "src/main/resources/database.properties" );
        String propertyResource = getExporterMojo().getComponentProperty( "propertyresource", "database.properties" );

        InputStream propInputStream = null;

        // Attempt to locate the propertyfile
        File propFile = HibernateUtils.getFile( getExporterMojo().getProject().getBasedir(), propertyFile );
        if ( propFile == null )
        {
            getExporterMojo().getLog().debug( propertyFile + " not found within the project. Trying absolute path." );
            propFile = HibernateUtils.getFile( null, propertyFile );
        }
        if ( propFile != null )
        {
            try
            {
                propInputStream = new FileInputStream( propFile );
            }
            catch ( IOException ioe )
            {
                getExporterMojo().getLog().debug(
                    "No hibernate properties file loaded from " + propFile.getPath() + ": " + ioe.getMessage() );
            }
        }

        // If we can't find the file propertyfile and the propertyresource is on the
        // classpath, open it as a stream
        if ( Thread.currentThread().getContextClassLoader().getResource( propertyResource ) != null )
        {
            propInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( propertyResource );
        }

        if ( propInputStream != null )
        {
            try
            {
                getExporterMojo().getLog().info( "Configuration Properties file loaded: " + propFile );
                Properties properties = new Properties();
                properties.load( propInputStream );
                return properties;
            }
            catch ( IOException ioe )
            {
                getExporterMojo().getLog().debug( "No hibernate properties file loaded: " + ioe.getMessage() );
            }
        }

        getExporterMojo().getLog().info( "No hibernate properties file loaded." );
        return null;
    }

    protected void validateParameters()
        throws MojoExecutionException
    {
        // noop
    }
}
