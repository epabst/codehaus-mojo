package org.codehaus.mojo.hibernate3.configuration;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.util.ReflectHelper;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.model.Build;
import org.apache.maven.artifact.Artifact;
import org.jboss.util.file.ArchiveBrowser;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Embeddable;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.File;

import javassist.bytecode.ClassFile;
import javassist.bytecode.AnnotationsAttribute;

public class AnnotationComponentConfiguration
    extends AbstractComponentConfiguration
{
// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface ComponentConfiguration ---------------------

    public String getName()
    {
        return "annotationconfiguration";
    }

// -------------------------- OTHER METHODS --------------------------

    protected Configuration createConfiguration()
    {
        // retrievethe Build object
        Build build = getExporterMojo().getProject().getBuild();

        // now create an empty arraylist that is going to hold our entities
        List<String> entities = new ArrayList<String>();

        try
        {
            if ( getExporterMojo().getComponentProperty( "scan-classes", false ) )
            {
                scanForClasses( new File( build.getOutputDirectory() ), entities );
                scanForClasses( new File( build.getTestOutputDirectory() ), entities );
            }

            if ( getExporterMojo().getComponentProperty( "scan-jars", false ) )
            {
                @SuppressWarnings( "unchecked" )
                List<Artifact> runtimeArtifacts = getExporterMojo().getProject().getRuntimeArtifacts();
                for ( Artifact a : runtimeArtifacts )
                {
                    File artifactFile = a.getFile();
                    if ( !artifactFile.isDirectory() )
                    {
                        getExporterMojo().getLog().debug( "[URL] " + artifactFile.toURI().toURL().toString() );
                        scanForClasses( artifactFile, entities );
                    }
                }

                @SuppressWarnings( "unchecked" )
                List<Artifact> testArtifacts = getExporterMojo().getProject().getTestArtifacts();
                for ( Artifact a : testArtifacts )
                {
                    File artifactFile = a.getFile();
                    if ( !artifactFile.isDirectory() )
                    {
                        getExporterMojo().getLog().debug( "[URL] " + artifactFile.toURI().toURL().toString() );
                        scanForClasses( artifactFile, entities );
                    }
                }
            }
        }
        catch ( MalformedURLException e )
        {
            getExporterMojo().getLog().error( e.getMessage(), e );
            return null;
        }

        // now create the configuration object
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        addNamedAnnotatedClasses( configuration, entities );
        return configuration;
    }

    private void scanForClasses( File directory, List<String> entities )
        throws MalformedURLException
    {
        if ( directory.list() != null )
        {
            getExporterMojo().getLog().debug( "[scanForClasses] " + directory );
            URL jar = directory.toURI().toURL();
            Iterator<?> it;
            try
            {
                it = ArchiveBrowser.getBrowser( jar, new ArchiveBrowser.Filter()
                {
                    public boolean accept( String filename )
                    {
                        return filename.endsWith( ".class" );
                    }
                } );
            }
            catch ( RuntimeException e )
            {
                throw new RuntimeException( "error trying to scan <jar-file>: " + jar.toString(), e );
            }

            // need to look into every entry in the archive to see if anybody has tags
            // defined.
            while ( it.hasNext() )
            {
                InputStream stream = (InputStream) it.next();
                DataInputStream dstream = new DataInputStream( stream );
                ClassFile cf = null;
                try
                {
                    try
                    {
                        cf = new ClassFile( dstream );
                    }
                    finally
                    {
                        dstream.close();
                        stream.close();
                    }
                }
                catch ( IOException e )
                {
                    throw new RuntimeException( e );
                }

                AnnotationsAttribute visible =
                    (AnnotationsAttribute) cf.getAttribute( AnnotationsAttribute.visibleTag );
                if ( visible != null )
                {
                    boolean isEntity = visible.getAnnotation( Entity.class.getName() ) != null;
                    if ( isEntity )
                    {
                        getExporterMojo().getLog().info( "found EJB3 Entity bean: " + cf.getName() );
                        entities.add( cf.getName() );
                    }
                    boolean isEmbeddable = visible.getAnnotation( Embeddable.class.getName() ) != null;
                    if ( isEmbeddable )
                    {
                        getExporterMojo().getLog().info( "found EJB3 @Embeddable: " + cf.getName() );
                        entities.add( cf.getName() );
                    }
                    boolean isEmbeddableSuperclass = visible.getAnnotation( MappedSuperclass.class.getName() ) != null;
                    if ( isEmbeddableSuperclass )
                    {
                        getExporterMojo().getLog().info( "found EJB3 @MappedSuperclass: " + cf.getName() );
                        entities.add( cf.getName() );
                    }
                }
            }
        }
    }

    private void addNamedAnnotatedClasses( AnnotationConfiguration cfg, Collection<String> classNames )
    {
        for ( String name : classNames )
        {
            try
            {
                Class<?> clazz = classForName( name );
                cfg.addAnnotatedClass( clazz );
            }
            catch ( ClassNotFoundException cnfe )
            {
                Package pkg;
                try
                {
                    pkg = classForName( name + ".package-info" ).getPackage();
                }
                catch ( ClassNotFoundException e )
                {
                    pkg = null;
                }
                if ( pkg == null )
                {
                    throw new PersistenceException( name + " class or package not found", cnfe );
                }
                else
                {
                    cfg.addPackage( name );
                }
            }
        }
    }

    private Class<?> classForName( String className )
        throws ClassNotFoundException
    {
        return ReflectHelper.classForName( className, this.getClass() );
    }

    protected void validateParameters()
        throws MojoExecutionException
    {
        super.validateParameters();
        if ( getConfigurationFile() == null && !getExporterMojo().getComponentProperty( "scan-classes", false ) &&
            !getExporterMojo().getComponentProperty( "scan-jars", false ) )
        {
            throw new MojoExecutionException( "No hibernate.cfg.xml configuration provided. " +
                "Annotated classes/packages is only configurable via hibernate.cfg.xml" );
        }
    }
}
