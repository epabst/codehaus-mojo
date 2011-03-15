package org.codehaus.mojo.pde.descriptor;

/*
 * Copyright 2006 The Apache Software Foundation.
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
import java.io.IOException;
import java.io.Reader;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.pde.descriptor.io.xpp3.FeatureXpp3Reader;
import org.codehaus.mojo.pde.descriptor.io.xpp3.FragmentXpp3Reader;
import org.codehaus.mojo.pde.descriptor.io.xpp3.PluginXpp3Reader;
import org.codehaus.mojo.pde.descriptor.io.xpp3.ProductXpp3Reader;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * 
 * 
 */
public class DescriptorUtil
{

    /**
     * Load the descriptor file for the pde project. This will either load the product descriptor
     * (based on the productFilename) or one of the eclipse artifacts in the following order:
     * feature ("feature.xml"), fragment ("fragment.xml"), plugin ("plugin.xml").
     * 
     * @param basedir the pde plugin base directory
     * @param productFilename optional, if specified then the eclipse product file that will be
     *            returned.
     * @return the descriptor for the pde project
     * @throws MojoExecutionException build failures.
     */
    public static Descriptor getDescriptor( File basedir, String productFilename )
        throws MojoExecutionException
    {
        Descriptor d = null;

        if ( productFilename != null )
        {
            d = loadProductDescriptor( basedir, productFilename );
        }
        else
        {
            d = loadFeatureDescriptor( basedir );

            if ( d == null )
            {
                d = loadFragmentDescriptor( basedir );
            }

            if ( d == null )
            {
                d = loadPluginDescriptor( basedir );
            }
        }

        if ( d == null )
        {
            throw new MojoExecutionException( basedir.getPath() + " is not a PDE project." );
        }

        return d;
    }

    /**
     * Load Feature from pde project directory.
     * 
     * @param basedir the pde project directory.
     * @return a Descriptor containing the Feature
     * @throws MojoExecutionException build failures.
     */
    private static Descriptor loadFeatureDescriptor( File basedir )
        throws MojoExecutionException
    {
        FeatureDescriptor descriptor = null;

        File file = new File( basedir, "feature.xml" );

        if ( file.exists() )
        {
            try
            {
                descriptor = readFeatureDescriptor( ReaderFactory.newXmlReader( file ) );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error reading " + file );
            }
        }

        return descriptor;

    }

    /**
     * Load Fragment from pde project directory.
     * 
     * @param basedir the pde project directory.
     * @return a Descriptor containing the Fragment
     * @throws MojoExecutionException build failures.
     */
    private static Descriptor loadFragmentDescriptor( File basedir )
        throws MojoExecutionException
    {
        FeatureDescriptor descriptor = null;

        File file = new File( basedir, "fragment.xml" );
        if ( file.exists() )
        {
            try
            {
                descriptor = readFeatureDescriptor( ReaderFactory.newXmlReader( file ) );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error reading " + file );
            }
        }

        return descriptor;

    }

    /**
     * Load Plugin from pde project directory.
     * 
     * @param basedir the pde project directory.
     * @return a Descriptor containing the Plugin
     * @throws MojoExecutionException build failures.
     */
    private static Descriptor loadPluginDescriptor( File basedir )
        throws MojoExecutionException
    {
        PluginDescriptor d = null;

        File file = new File( basedir, "plugin.xml" );
        if ( file.exists() )
        {
            try
            {
                d = readPluginDescriptor( ReaderFactory.newXmlReader( file ) );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error reading " + file );
            }
        }
        else
        {
            d = new PluginDescriptor();
        }

        if ( d.getId() == null || d.getVersion() == null )
        {
            ManifestBean bean = new ManifestBean( basedir );
            if ( d.getId() == null )
            {
                d.setId( bean.getId() );
            }

            if ( d.getVersion() == null )
            {
                d.setVersion( bean.getVersion() );
            }
        }

        return d;

    }

    /**
     * Load Product from pde project directory.
     * 
     * @param basedir the pde project directory.
     * @param productFilename the file name for the *.product file for the project.
     * @return a Descriptor containing the Product
     * @throws MojoExecutionException build failures.
     */
    private static Descriptor loadProductDescriptor( File basedir, String productFilename )
        throws MojoExecutionException
    {
        ProductDescriptor d = null;

        File file = new File( basedir, productFilename );
        if ( file.exists() )
        {
            try
            {
                d = readProductDescriptor( ReaderFactory.newXmlReader( file ) );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error reading " + file );
            }
        }

        return d;
    }

    /**
     * Load the plugin.xml via a Reader
     * 
     * @param reader plugin file
     * @throws MojoExecutionException build failures.
     * @return PluginDescriptor
     */
    public static PluginDescriptor readPluginDescriptor( Reader reader )
        throws MojoExecutionException
    {
        PluginDescriptor descriptor;
        try
        {
            PluginXpp3Reader r = new PluginXpp3Reader();

            descriptor = r.read( reader, false );

        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error reading plugin.xml descriptor", e );
        }
        catch ( XmlPullParserException e )
        {
            throw new MojoExecutionException( "Error reading plugin.xml descriptor", e );
        }
        finally
        {
            IOUtil.close( reader );
        }

        return descriptor;
    }

    /**
     * Load the feature.xml via a Reader
     * 
     * @param reader the feature file
     * @return FeatureDescriptor
     * @throws MojoExecutionException build failures.
     */
    public static FeatureDescriptor readFeatureDescriptor( Reader reader )
        throws MojoExecutionException
    {
        FeatureDescriptor descriptor;
        try
        {
            FeatureXpp3Reader r = new FeatureXpp3Reader();
            descriptor = r.read( reader, false );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error reading feature.xml descriptor", e );
        }
        catch ( XmlPullParserException e )
        {
            throw new MojoExecutionException( "Error reading feature.xml descriptor", e );
        }
        finally
        {
            IOUtil.close( reader );
        }

        return descriptor;
    }

    /**
     * Load the fragment.xml via a Reader
     * 
     * @param reader the fragment file
     * @return FragmentDescriptor
     * @throws MojoExecutionException build failures.
     */
    public static FragmentDescriptor readFragmentDescriptor( Reader reader )
        throws MojoExecutionException
    {
        FragmentDescriptor descriptor;
        try
        {
            FragmentXpp3Reader r = new FragmentXpp3Reader();
            descriptor = r.read( reader, false );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error reading fragment.xml descriptor", e );
        }
        catch ( XmlPullParserException e )
        {
            throw new MojoExecutionException( "Error reading fragment.xml descriptor", e );
        }
        finally
        {
            IOUtil.close( reader );
        }

        return descriptor;
    }

    /**
     * Load the *.product via a Reader
     * 
     * @param reader the product file
     * @return ProductDescriptor
     * @throws MojoExecutionException build failures.
     */
    public static ProductDescriptor readProductDescriptor( Reader reader )
        throws MojoExecutionException
    {
        ProductDescriptor descriptor;
        try
        {
            ProductXpp3Reader r = new ProductXpp3Reader();
            descriptor = r.read( reader, false );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error reading *.product descriptor", e );
        }
        catch ( XmlPullParserException e )
        {
            throw new MojoExecutionException( "Error reading *.product descriptor", e );
        }
        finally
        {
            IOUtil.close( reader );
        }

        return descriptor;
    }

    /**
     * Return the String version of the pdetype based on the descriptor.
     * 
     * @param descriptor the project descriptor
     * @return product, plugin, feature, or fragment based upon the descriptor.
     */
    public static String getPDEType( Descriptor descriptor )
    {
        if ( descriptor instanceof ProductDescriptor )
        {
            return "product";
        }
        if ( descriptor instanceof PluginDescriptor )
        {
            return "plugin";
        }
        if ( descriptor instanceof FeatureDescriptor )
        {
            return "feature";
        }
        if ( descriptor instanceof FragmentDescriptor )
        {
            return "fragment";
        }

        return null;
    }

}
