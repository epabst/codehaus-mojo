package org.apache.maven.plugin;

/*
 * LICENSE
 */

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ApiDocsPlugin
{
    // parameters

    private File sourceDirectory;

    private File outputDirectory;

    private String title;

    // members

    private JavaDocBuilder builder;

    private VelocityEngine velocity;

    private Map classes;

    // ----------------------------------------------------------------------
    // Plugin Implementation
    // ----------------------------------------------------------------------

    public void execute()
        throws Exception
    {
        initializeSources();

        initializeVelocity();

        generateClassDocs();

        generateListDocs();

        // misc
        Template template = getTemplate( "templates/style.css.vm" );

        File outputFile = new File( outputDirectory, "style.css" );

        FileWriter output = new FileWriter( outputFile );

        VelocityContext context = createVelocityContext( null );

        template.merge( context, output );

        output.close();
    }

    public void initializeSources()
        throws Exception
    {
        if ( !outputDirectory.exists() && !outputDirectory.mkdirs() )
        {
            throw new Exception( "Could not make the output directory: '" + outputDirectory.getAbsolutePath() );
        }

        builder = new JavaDocBuilder();

        debug( "outputDirectory: " + outputDirectory );
        debug( "sourceDirectory: " + sourceDirectory );

        builder.addSourceTree( sourceDirectory );

        classes = new TreeMap();

        JavaSource[] sources = builder.getSources();

        for ( int i = 0; i < sources.length; i++ )
        {
            JavaClass[] javaClasses = sources[ i ].getClasses();

            for ( int j = 0; j < javaClasses.length; j++ )
            {
                classes.put( javaClasses[ j ].asType().getValue(), new ApiClass( javaClasses[ j ] ) );
            }
        }
    }

    public void generateClassDocs()
        throws Exception
    {
        Iterator it = classes.values().iterator();

        while ( it.hasNext() )
        {
            generateDocsForClass( (ApiClass) it.next() );
        }
    }

    public void generateListDocs()
        throws Exception
    {
        VelocityContext context = createVelocityContext( null );

        // initialize the velocity context
        context.put( "classes", classes );

        Template template = getTemplate( "templates/all-classes.vm" );

        FileWriter output = new FileWriter( new File( outputDirectory, "all-classes.html" ) );

        template.merge( context, output );

        output.close();
    }

    private void generateDocsForClass( ApiClass clazz )
        throws Exception
    {
        debug( "Generating docs for: " + clazz.getFullName() );

        VelocityContext context = createVelocityContext( clazz );

        // initialize the velocity context
        context.put( "class", clazz );

        Template template = getTemplate( "templates/class.vm" );

        File outputFile = getOutputFile( clazz.getFullName() );

        FileWriter output = new FileWriter( outputFile );

        template.merge( context, output );

        output.close();
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    /**
     * @return Returns the outputDirectory.
     */
    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * @param outputDirectory The outputDirectory to set.
     */
    public void setOutputDirectory( File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @return Returns the sourceDirectory.
     */
    public File getSourceDirectory()
    {
        return sourceDirectory;
    }

    /**
     * @param sourceDirectory The sourceDirectory to set.
     */
    public void setSourceDirectory( File sourceDirectory )
    {
        this.sourceDirectory = sourceDirectory;
    }

    public ApiClass getApiClass( String name )
    {
        return (ApiClass) classes.get( name );
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    private void initializeVelocity()
        throws Exception
    {
        velocity = new VelocityEngine();

        velocity.setProperty( "runtime.log.logsystem.class", VelocityLogger.class.getName() );
        velocity.setProperty( "resource.loader", "classpath" );
        velocity.setProperty( "classpath.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );

        velocity.init();
    }

    private Template getTemplate( String template )
        throws Exception
    {
        try
        {
            return velocity.getTemplate( template );
        }
        catch ( ResourceNotFoundException ex )
        {
            throw new Exception( "Exception while getting template", ex );
        }
        catch ( ParseErrorException ex )
        {
            throw new Exception( "Exception while getting template", ex );
        }
        catch ( MethodInvocationException ex )
        {
            throw new Exception( "Exception while getting template", ex );
        }
        catch ( Exception ex )
        {
            throw new Exception( "Exception while getting template", ex );
        }
    }

    private File getOutputFile( String clazz )
    {
        clazz = clazz.replace( '.', File.separatorChar );

        File file = new File( outputDirectory, clazz + ".html" );

        int index = clazz.lastIndexOf( File.separatorChar );

        if ( index != -1 )
        {
            File dir = new File( outputDirectory, clazz.substring( 0, index ) );

            dir.mkdirs();
        }

        return file;
    }

    private VelocityContext createVelocityContext( ApiClass clazz )
    {
        VelocityContext context = new VelocityContext();

        if ( title == null )
        {
            context.put( "title", "API Documentation" );
        }
        else
        {
            context.put( "title", title );
        }

        String rootPage = ".";

        if ( clazz != null )
        {
            int i = clazz.getFullName().indexOf( '.' );

            while ( i != -1 )
            {
                rootPage += "/..";

                i = clazz.getFullName().indexOf( '.', i + 1 );
            }
        }

        context.put( "rootPage", rootPage );

        return context;
    }

    private void debug( String msg )
    {
        System.err.println( msg );
    }

    private void info( String msg )
    {
        System.out.println( msg );
    }
}
