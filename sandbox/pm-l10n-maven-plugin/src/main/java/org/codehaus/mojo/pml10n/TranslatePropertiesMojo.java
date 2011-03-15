package org.codehaus.mojo.pml10n;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * Translates properties files.
 *
 * @author Stephen Connolly
 * @goal translate-properties
 * @requiresProject true
 * @phase generate-resources
 * @since 1.0-alpha-1
 */
public class TranslatePropertiesMojo
    extends AbstractMojo
{
    /**
     * The output directory into which to copy the resources.
     *
     * @required
     * @parameter expression="${project.build.outputDirectory}"
     */
    private File outputDirectory;

    /**
     * The locales to ensure we have translations for.
     *
     * @required
     * @parameter
     */
    private String[] targetLocales;

    /**
     * The source directory from which to copy the resources.
     *
     * @required
     * @parameter default-value="${basedir}/src/i18n/resources"
     */
    private File sourceDirectory;

    /**
     * The default locale.
     *
     * @parameter default-value="en"
     */
    private String masterLocale;

    /**
     * The translator to use.
     *
     * @parameter default-value="google"
     */
    private String translator;

    /**
     * The referrer string to provide to the translator.
     *
     * @required
     * @parameter expression="${referrer}
     */
    private String referrer;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( !sourceDirectory.isDirectory() )
        {
            getLog().warn( "No properties files to translate" );
            return;
        }
        GoogleInterpreter translator;
        try
        {
            translator = new GoogleInterpreter( referrer );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir( sourceDirectory );
        scanner.scan();
        Set names = new TreeSet( Arrays.asList( scanner.getIncludedFiles() ) );
        Iterator i = names.iterator();
        while ( i.hasNext() )
        {
            final String sourceName = (String) i.next();
            File source = new File( sourceDirectory, sourceName );
            File destination = new File( outputDirectory, sourceName );
            Properties props = new Properties();
            FileInputStream fis = null;
            try
            {
                getLog().info( "Processing " + sourceName );
                FileUtils.copyFileIfModified( source, destination );
                FileUtils.copyFileIfModified( source, localiseBundle( destination, masterLocale ) );
                fis = new FileInputStream( source );
                props.load( fis );
                for ( int j = 0; j < targetLocales.length; j++ )
                {
                    File translation = localiseBundle( destination, targetLocales[j] );
                    if ( translation.isFile() && translation.lastModified() > source.lastModified() )
                    {
                        continue;
                    }
                    Properties translated = new Properties();
                    for ( Enumeration k = props.propertyNames(); k.hasMoreElements(); )
                    {
                        String name = (String) k.nextElement();
                        String sourceMessage = props.getProperty( name );
                        translated.setProperty( name, translator.translate( sourceMessage, new Locale( masterLocale ),
                                                                            new Locale( targetLocales[j] ) ) );
                    }
                    FileOutputStream fos = null;
                    try
                    {
                        fos = new FileOutputStream( translation );
                        translated.store( fos, "Translated by " + this.translator );
                    }
                    finally
                    {
                        if ( fos != null )
                        {
                            fos.close();
                        }
                    }

                }
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( e.getMessage(), e );
            }
            finally
            {
                if ( fis != null )
                {
                    try
                    {
                        fis.close();
                    }
                    catch ( IOException e )
                    {
                        // ignore
                    }
                }
            }
        }
    }

    private File localiseBundle( File bundleFile, String locale )
    {
        return new File( bundleFile.getParent(), localizeBundle( bundleFile.getName(), locale ) );
    }

    private String localizeBundle( String budleName, String locale )
    {
        return FileUtils.removeExtension( budleName ) + "_" + locale + "." + FileUtils.extension( budleName );
    }
}
