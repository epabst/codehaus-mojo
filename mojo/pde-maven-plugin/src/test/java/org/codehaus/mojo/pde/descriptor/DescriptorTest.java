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

import org.codehaus.plexus.PlexusTestCase;

/**
 * Test case for Descriptor.
 */
public class DescriptorTest
    extends PlexusTestCase
{

    /**
     * Test fails correctly when there is no descriptor available in project.
     * @throws Exception test failures.
     */
    public void testNoPDEDescriptor()
        throws Exception
    {
        try
        {
            DescriptorUtil.getDescriptor( new File( getBasedir(), "src/test/resources" ), null );
            fail( "Expected exception not found" );
        }
        catch ( Exception e )
        {
            assertTrue( true );
        }
    }

    /**
     * Test a feature can be loaded correctly.
     * 
     * @throws Exception test failures.
     */
    public void testLoadFeatureDescriptor()
        throws Exception
    {
        Descriptor descriptor =
            DescriptorUtil.getDescriptor( new File( getBasedir(), "src/test/resources/feature" ), null );

        assertEquals( "net.sourceforge.eclipseccase", descriptor.getId() );
        assertEquals( "1.1.1", descriptor.getVersion() );
        assertEquals( "feature", DescriptorUtil.getPDEType( descriptor ) );
    }

    /**
     * Test a plugin can be loaded correctly.
     * @throws Exception test failures.
     */
    public void testLoadPluginDescriptor()
        throws Exception
    {
        Descriptor descriptor =
            DescriptorUtil.getDescriptor( new File( getBasedir(), "src/test/resources/plugin" ), null );

        assertEquals( "org.eclipse.examples.helloworld", descriptor.getId() );
        assertEquals( "0.0.0", descriptor.getVersion() );
        assertEquals( "plugin", DescriptorUtil.getPDEType( descriptor ) );
    }

    /**
     * Test a plugin manifest can be loaded correctly
     * @throws Exception test failures.
     */
    public void testLoadPluginManifestDescriptor()
        throws Exception
    {
        Descriptor descriptor =
            DescriptorUtil.getDescriptor( new File( getBasedir(), "src/test/resources/manifest" ), null );

        assertEquals( "net.sourceforge.clearcase", descriptor.getId() );
        assertEquals( "1.0.5", descriptor.getVersion() );
        assertEquals( "plugin", DescriptorUtil.getPDEType( descriptor ) );

    }

    /**
     * Test a manifest can be read when there is no plugin.xml file.
     * @throws Exception test failures.
     */
    public void testLoadManifestNoPluginDescriptor()
        throws Exception
    {
        Descriptor descriptor =
            DescriptorUtil.getDescriptor( new File( getBasedir(), "src/test/resources/manifest-noplugin" ), null );

        assertEquals( "net.sourceforge.clearcase", descriptor.getId() );
        assertEquals( "1.0.5", descriptor.getVersion() );
        assertEquals( "plugin", DescriptorUtil.getPDEType( descriptor ) );

    }

    /**
     * Test a product can be read correctly.
     * @throws Exception test failures.
     */
    public void testLoadProductDescriptor()
        throws Exception
    {
        Descriptor descriptor =
            DescriptorUtil.getDescriptor( new File( getBasedir(),
                                                    "src/test/resources/product-with-plugin/plugins/test.plugin" ),
                "pde-test.product" );

        assertEquals( "test.pde.ApplicationPlugin.product", descriptor.getId() );
        assertEquals( "product", DescriptorUtil.getPDEType( descriptor ) );
    }

}
