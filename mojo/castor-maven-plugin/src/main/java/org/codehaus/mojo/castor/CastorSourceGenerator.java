/*
 * Copyright 2005 The Codehaus.
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
package org.codehaus.mojo.castor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.exolab.castor.builder.SourceGenerator;
import org.exolab.castor.builder.binding.ExtendedBinding;
import org.exolab.castor.builder.factory.FieldInfoFactory;
import org.exolab.castor.builder.info.CollectionInfo;

/**
 * Override Castor's SourceGenerator to inject exception handling. Code based on Castor XML code generator, release
 * 1.1-M2
 */
class CastorSourceGenerator
    extends SourceGenerator
{
    /**
     * {@link Log} instance to use for any logging.
     */
    private Log log;

    /**
     * {@link FieldInfoFactory} instance to be used during code generation. We need to save this in order to override
     * its properties later since SourceGenerator doesn't give us access to this and the properties are only read during
     * the constructor call
     */
    private FieldInfoFactory fieldInfoFactory;

    /**
     * Indicates whether logging should be verbose. As base class does not provide access to this variable, we intercept
     * setting it and store its value here
     */
    private boolean verbose;

    /**
     * Creates a default {@link CastorSourceGenerator} instance.
     */
    public CastorSourceGenerator()
    {
        this( new FieldInfoFactory() );
    }

    /**
     * Creates an instance of {@link CastorSourceGenerator}, configured with a field info factory.
     * 
     * @param fieldInfoFactory {@link FieldInfoFactory} instance to be used during code generation.
     */
    public CastorSourceGenerator( FieldInfoFactory fieldInfoFactory )
    {
        super( fieldInfoFactory );
        this.fieldInfoFactory = fieldInfoFactory;
    }

    /**
     * Creates an instance of {@link CastorSourceGenerator}, configured with a field info factory and a binding file.
     * 
     * @param fieldInfoFactory {@link FieldInfoFactory} instance to be used during code generation.
     * @param extendedBinding Binding file to be used during code generation.
     */
    public CastorSourceGenerator( FieldInfoFactory fieldInfoFactory, ExtendedBinding extendedBinding )
    {
        super( fieldInfoFactory, extendedBinding );
        this.fieldInfoFactory = fieldInfoFactory;
    }

    /**
     * Factory method to create a {@link CastorSourceGenerator} instance, preset with default values.
     * 
     * @param types A {@link FieldInfoFactory} type.
     * @return a {@link CastorSourceGenerator} instance
     * @throws MojoExecutionException To signal that an invalid type has been passed.
     */
    public static CastorSourceGenerator createSourceGenerator( String types )
        throws MojoExecutionException
    {
        // Create Source Generator with appropriate type factory
        CastorSourceGenerator sgen;
        if ( types != null )
        {
            try
            {
                String typ = "j2".equals( types ) ? "arraylist" : types;
                FieldInfoFactory factory = new FieldInfoFactory( typ );
                sgen = new CastorSourceGenerator( factory );
            }
            catch ( Exception e )
            {
                try
                {
                    sgen = new CastorSourceGenerator( (FieldInfoFactory) Class.forName( types ).newInstance() );
                }
                catch ( Exception e2 )
                {
                    throw new MojoExecutionException( "Invalid types \"" + types + "\": " + e.getMessage() );
                }
            }
        }
        else
        {
            sgen = new CastorSourceGenerator(); // default
        }
        return sgen;
    }

    /**
     * Sets the {@link Log} instance to use for logging.
     * 
     * @param log The {@link Log} instance to use for logging
     */
    public void setLog( Log log )
    {
        this.log = log;
    }

    /**
     * Returns the {@link Log} instance to use for logging.
     * 
     * @return The {@link Log} instance to use for logging.
     */
    public Log getLog()
    {
        return log;
    }

    /**
     * Helper method to output log statements.
     * 
     * @param msg The log message to be output.
     */
    public void log( String msg )
    {
        getLog().info( msg );
    }

    /**
     * Helper method to (conditionally) output a log statement.
     * 
     * @param msg The log message to be output.
     */
    public void verbose( String msg )
    {
        if ( verbose )
        {
            getLog().info( msg );
        }
    }

    /**
     * Sets a user-specified line separator stype on the {@link CastorSourceGenerator}.
     * 
     * @param lineSeparator A user-specified line separator style.
     * @throws MojoExecutionException If an invalid line separator stype has been specified.
     */
    public void setLineSeparatorStyle( String lineSeparator )
        throws MojoExecutionException
    {
        // Set Line Separator
        String lineSep = System.getProperty( "line.separator" );
        if ( lineSeparator != null )
        {
            if ( "win".equals( lineSeparator ) )
            {
                log( "Using Windows style line separation." );
                lineSep = "\r\n";
            }
            else if ( "unix".equals( lineSeparator ) )
            {
                log( "Using UNIX style line separation." );
                lineSep = "\n";
            }
            else if ( "mac".equals( lineSeparator ) )
            {
                log( "Using Macintosh style line separation." );
                lineSep = "\r";
            }
            else
            {
                throw new MojoExecutionException( "Invalid value for lineseparator, must be win, unix, or mac." );
            }
        }
        setLineSeparator( lineSep );
    }

    /**
     * Indicates whether source generation should be 'verbose'.
     * 
     * @param verbose True if source generation should be 'verbose'.
     * @see org.exolab.castor.builder.SourceGenerator#setVerbose(boolean)
     */
    public void setVerbose( final boolean verbose )
    {
        this.verbose = verbose;
        super.setVerbose( verbose );
    }

    /**
     * Sets a user-specific binding file to be used during code generation.
     * 
     * @param bindingFile A user-specified binding file.
     */
    public void setBindingFile( final File bindingFile )
    {
        setBinding( bindingFile.getAbsolutePath() );
    }

    /**
     * Sets user-specific code generator properties for the code generation process.
     * 
     * @param properties User-specific code generator properties.
     * @throws MojoExecutionException Indicates that the user-specific properties cannot be accessed.
     */
    public void setBuilderProperties( File properties )
        throws MojoExecutionException
    {
        // Set Builder Properties;
        if ( properties != null )
        {
            String filePath = properties.getAbsolutePath();
            Properties customProperties = new Properties();
            try
            {
                customProperties.load( new FileInputStream( filePath ) );
            }
            catch ( FileNotFoundException e )
            {
                throw new MojoExecutionException( "Properties file \"" + filePath + "\" not found" );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Can't read properties file \"" + filePath + "\": " + e );
            }
            setDefaultProperties( customProperties );

            // these properties are read at contstruction time and copied into FieldInfoFactory
            // se we set them directly in the fieldInfoFactory here.
            if ( generateExtraCollectionMethods() )
            {
                verbose( "Overriding default castorbuilder.properties and setting createExtraMethods to true" );
                fieldInfoFactory.setCreateExtraMethods( true );
            }

            String suffix = getProperty( CollectionInfo.REFERENCE_SUFFIX_PROPERTY, null );
            if ( suffix != null )
            {
                verbose( "Overriding default castorbuilder.properties and " + "setting referenceSuffixProperty to "
                    + suffix );
            }
            fieldInfoFactory.setReferenceMethodSuffix( suffix );

            if ( boundPropertiesEnabled() )
            {
                verbose( "Overriding default castorbuilder.properties and setting boundProperties to true" );
                fieldInfoFactory.setBoundProperties( true );
            }

        }
    }

}
