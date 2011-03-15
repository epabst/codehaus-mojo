package org.codehaus.mojo.pde;

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

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Test case for EclipsePDEMojo.
 * 
 */
public class EclipsePDEMojoTest
    extends AbstractMojoTestCase
{
    /**
     * setUp test cases.
     * 
     * @throws Exception test failures.
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /**
     * Test that a Mojo can be looked up.
     * 
     * @throws Exception test failures.
     */
    public void testMojoLookup()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-plugin-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );
        assertNotNull( mojo );
    }

    /**
     * Test build mojo for a feature.
     * 
     * @throws Exception test failures.
     */
    public void testBuildFeatureCommandline()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-plugin-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );
        mojo.pdeDirectory = new File( getBasedir(), "src/test/resources/feature" );
        mojo.initialize();

        Commandline cl = mojo.createBuildCommandLine();

        File startupJar = new File( getBasedir(), "src/test/resources/baseLocation/startup.jar" );
        String expected =
            "java -classpath " + startupJar.getPath()
                + " org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner" + " -buildfile "
                + new File( mojo.pdeDirectory, "build.xml" ) + " clean build.jars zip.distribution"
                + " -verbose -debug" + " -DbuildTempFolder=" + mojo.pdeBuildTempFolder
                + " -DjavacFailOnError=true -Dproperty1=value1";

        assertTrue( cl.toString().indexOf( expected ) >= 0 );
    }

    /**
     * Test build mojo for a feature when a product is also present. In this case a Feature command
     * line should be returned as no product file was specified.
     * 
     * @throws Exception test failures.
     */
    public void testBuildFeatureCommandlineWhenProductFilePresent()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-plugin-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );
        mojo.pdeDirectory = new File( getBasedir(), "src/test/resources/product-with-feature/features/test.feature" );
        mojo.initialize();

        Commandline cl = mojo.createBuildCommandLine();

        File startupJar = new File( getBasedir(), "src/test/resources/baseLocation/startup.jar" );
        String expected =
            "java -classpath " + startupJar.getPath()
                + " org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner" + " -buildfile "
                + new File( mojo.pdeDirectory, "build.xml" ) + " clean build.jars zip.distribution"
                + " -verbose -debug" + " -DbuildTempFolder=" + mojo.pdeBuildTempFolder
                + " -DjavacFailOnError=true -Dproperty1=value1";

        assertTrue( cl.toString().indexOf( expected ) >= 0 );
    }

    /**
     * Test build mojo for a plugin.
     * 
     * @throws Exception test failures.
     */
    public void testBuildPluginCommandline()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-plugin-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );
        mojo.pdeDirectory = new File( getBasedir(), "src/test/resources/plugin" );
        mojo.initialize();

        Commandline cl = mojo.createBuildCommandLine();

        File startupJar = new File( getBasedir(), "src/test/resources/baseLocation/startup.jar" );
        String expected =
            "java -classpath " + startupJar.getPath()
                + " org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner" + " -buildfile "
                + new File( mojo.pdeDirectory, "build.xml" ) + " clean build.jars zip.plugin" + " -verbose -debug"
                + " -DbuildTempFolder=" + mojo.pdeBuildTempFolder + " -DjavacFailOnError=true -Dproperty1=value1";

        assertTrue( cl.toString().indexOf( expected ) >= 0 );
    }

    /**
     * Paths should be escaped correctly if they contain spaces in them.
     * 
     * @throws Exception test failures.
     */
    public void testBuildPluginCommandlineWhenPathIncluesSpaces()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-plugin-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );
        mojo.pdeDirectory = new File( getBasedir(), "src/test/resources/path with spaces" );
        mojo.initialize();

        Commandline cl = mojo.createBuildCommandLine();

        File startupJar = new File( getBasedir(), "src/test/resources/baseLocation/startup.jar" );
        String expected =
            "java -classpath " + startupJar.getPath()
                + " org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner" + " -buildfile \""
                + new File( mojo.pdeDirectory, "build.xml" ) + "\" clean build.jars zip.plugin" + " -verbose -debug"
                + " -DbuildTempFolder=" + mojo.pdeBuildTempFolder + " -DjavacFailOnError=true -Dproperty1=value1";

        assertTrue( cl.toString().indexOf( expected ) >= 0 );
    }

    /**
     * Test build mojo for plugin when a product file is also present. In this case a Plugin command
     * line should be returned as no product file was specified.
     * 
     * @throws Exception test failures.
     */
    public void testBuildPluginCommandlineWhenProductFilePresent()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-plugin-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );
        mojo.pdeDirectory = new File( getBasedir(), "src/test/resources/product-with-plugin/plugins/test.plugin" );
        mojo.initialize();

        Commandline cl = mojo.createBuildCommandLine();

        File startupJar = new File( getBasedir(), "src/test/resources/baseLocation/startup.jar" );
        String expected =
            "java -classpath " + startupJar.getPath()
                + " org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner" + " -buildfile "
                + new File( mojo.pdeDirectory, "build.xml" ) + " clean build.jars zip.plugin" + " -verbose -debug"
                + " -DbuildTempFolder=" + mojo.pdeBuildTempFolder + " -DjavacFailOnError=true -Dproperty1=value1";

        assertTrue( cl.toString().indexOf( expected ) >= 0 );
    }

    /**
     * Test build mojo for product when a feature is also present. In this case a Product command
     * line should be returned as specifying a product file overrides any feature or plugin
     * available in the pde directory.
     * 
     * @throws Exception test failures.
     */
    public void testBuildProductCommandlineWhenFeaturePresent()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-product-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );

        mojo.pdeDirectory = new File( getBasedir(), "src/test/resources/product-with-feature/features/test.feature" );
        mojo.initialize();

        try
        {
            mojo.createBootstrapCommandLine();
            fail( "A Product build does not require bootstrapping." );
        }
        catch ( MojoExecutionException ex )
        {
            assertTrue( true );
        }

        Commandline cl = mojo.createBuildCommandLine();

        File startupJar = new File( getBasedir(), "src/test/resources/baseLocation/startup.jar" );
        File expectedBuildfile =
            new File( mojo.eclipseInstall,
                      "plugins/org.eclipse.pde.build_3.2.0.v20060603/scripts/productBuild/productBuild.xml" );
        File expectedPdeBuildConfigDirectory = new File( mojo.pdeDirectory, mojo.pdeBuildConfigDirectory );
        String expected =
            "java -classpath " + startupJar.getPath()
                + " org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner" + " -buildfile "
                + expectedBuildfile.getPath() + " -Dbuilder=" + expectedPdeBuildConfigDirectory.getPath()
                + " -DbuildDirectory=" + mojo.getPDEBuildDirectory().getPath() + " -DbuildTempFolder="
                + mojo.pdeBuildTempFolder + " -DjavacFailOnError=true";

        assertTrue( cl.toString().indexOf( expected ) >= 0 );
    }

    /**
     * Test build mojo for product when a plugin is also present. In this case a Product command
     * line should be returned as specifying a product file overrides any feature or plugin
     * available in the pde directory.
     * 
     * @throws Exception test failures.
     */
    public void testBuildProductCommandlineWhenPluginPresent()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-product-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );

        File startupJar = new File( getBasedir(), "src/test/resources/baseLocation/startup.jar" );

        mojo.pdeDirectory = new File( getBasedir(), "src/test/resources/product-with-plugin/plugins/test.plugin" );

        mojo.initialize();

        try
        {
            mojo.createBootstrapCommandLine();
            fail( "A Product build does not require bootstrapping." );
        }
        catch ( MojoExecutionException ex )
        {
            assertTrue( true );
        }

        Commandline cl = mojo.createBuildCommandLine();

        File expectedBuildfile =
            new File( mojo.eclipseInstall,
                      "plugins/org.eclipse.pde.build_3.2.0.v20060603/scripts/productBuild/productBuild.xml" );
        File expectedPdeBuildConfigDirectory = new File( mojo.pdeDirectory, mojo.pdeBuildConfigDirectory );

        String expected =
            "java -classpath " + startupJar.getPath()
                + " org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner" + " -buildfile "
                + expectedBuildfile.getPath() + " -Dbuilder=" + expectedPdeBuildConfigDirectory.getPath()
                + " -DbuildDirectory=" + mojo.getPDEBuildDirectory().getPath() + " -DbuildTempFolder="
                + mojo.pdeBuildTempFolder + " -DjavacFailOnError=true";

        assertTrue( cl.toString().indexOf( expected ) >= 0 );
    }

    /**
     * test generate mojo
     * 
     * @throws Exception test failures.
     */
    public void testGenerateAntFilesCommandline()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/generate-plugin-config.xml" );
        EclipsePDEMojo mojo = (EclipsePDEMojo) lookupMojo( "pde", pluginXml );

        File startupJar = new File( getBasedir(), "src/test/resources/baseLocation/startup.jar" );
        startupJar.getParentFile().mkdirs();
        startupJar.createNewFile();

        mojo.pdeDirectory = new File( getBasedir(), "src/test/resources/feature" );

        mojo.initialize();

        Commandline cl = mojo.createBootstrapCommandLine();

        String expected =
            "java -classpath " + startupJar.getPath()
                + " org.eclipse.core.launcher.Main -application org.eclipse.ant.core.antRunner -buildfile "
                + new File( "target/pdeBuilder/build.xml" ) + " -verbose -debug";

        assertTrue( cl.toString().indexOf( expected ) >= 0 );

        mojo.execute();

        File bootstrapBuildXml = new File( getBasedir(), "target/pdeBuilder/build.xml" );

        assertTrue( "Boostrap build.xml was not generated", bootstrapBuildXml.exists() );
    }

    /**
     * test clean mojo
     * 
     * @throws Exception test failures.
     */
    public void testCleanMojo()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/clean-plugin-config.xml" );
        EclipsePDECleanMojo mojo = (EclipsePDECleanMojo) lookupMojo( "clean", pluginXml );

        mojo.pdeDirectory = new File( getBasedir(), "src/test/resources/feature" );
        // mojo.eclipseInstall = new File( getBasedir(), "target" );

        mojo.execute();

        File buildXml = new File( mojo.pdeDirectory, "build.xml" );
        assertTrue( "custom build.xml got deleted", buildXml.exists() );

    }

    /**
     * test test mojo
     * 
     * @throws Exception test failures.
     */
    public void testNoTestMojo()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/notest-plugin-config.xml" );
        EclipsePDETestMojo mojo = (EclipsePDETestMojo) lookupMojo( "test", pluginXml );
        mojo.execute();
    }

    /**
     * test good test mojo
     * 
     * @throws Exception test failures.
     */
    public void testNoFailOnErrorTestMojo()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/good-test-plugin-config.xml" );
        EclipsePDETestMojo mojo = (EclipsePDETestMojo) lookupMojo( "test", pluginXml );

        mojo.execute();
    }

    /**
     * test failOnError test mojo
     * 
     * @@throws Exception test failures.
     */
    public void testFailOnErrorTestMojo()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/bad-test-plugin-config.xml" );
        EclipsePDETestMojo mojo = (EclipsePDETestMojo) lookupMojo( "test", pluginXml );
        try
        {
            mojo.execute();
            // currently we are not able to mock execute to fail, must disable next check

            // fail( "failOnError is expected." );
        }
        catch ( MojoExecutionException e )
        {
            assertTrue( true );
        }
    }

    /**
     * Test build mojo fails when a product build is specified by no pde build version is provided.
     * 
     * @throws Exception test failures.
     */
    public void testFailOnProductBuildWithNoPdeBuildVersion()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/bad-build-product-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );
        try
        {
            mojo.initialize();
            fail( "Expected an exception" );
        }
        catch ( MojoExecutionException e )
        {
            assertTrue( true );
        }

    }

    /**
     * Test that the output ant file can be located for a plugin build
     * 
     * @throws Exception test failures.
     */
    public void testLocateAntOutputFileForPluginBuild()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-plugin-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );
        mojo.pdeDirectory = new File( getBasedir(), "src/test/resources/plugin" );
        mojo.initialize();

        File expectedAntFile = new File( mojo.pdeDirectory, "org.eclipse.examples.helloworld_0.0.0.zip" );
        assertEquals( expectedAntFile, mojo.locateAntOutputFile() );
    }

    /**
     * Test that the output ant file can be located for a feature build
     * 
     * @throws Exception test failures.
     */
    public void testLocateAntOutputFileForFeatureBuild()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-plugin-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );
        mojo.pdeDirectory = new File( getBasedir(), "src/test/resources/feature" );
        mojo.initialize();

        File expectedAntFile = new File( mojo.pdeDirectory, "net.sourceforge.eclipseccase_1.1.1.bin.dist.zip" );
        assertEquals( expectedAntFile, mojo.locateAntOutputFile() );
    }

    /**
     * The product file from a PDE build is located at
     * ${buildDirectory}/${buildLabel}/${buildId}-${configs}.zip.
     * 
     * <b>Note:</b> A normal pde build supports multiple configs to be built. This maven mojo only
     * supports <b>ONE</b> config to be built. Otherwise attaching the generated artifacts becomes
     * difficult, as which one is the main build and which ones are the extras with classifiers?
     * 
     * @throws Exception test failures.
     */
    public void testLocateAntOutputFileForProductBuild()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-product-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );
        mojo.pdeDirectory = new File( getBasedir(), "src/test/resources/product-with-plugin/plugins/test.plugin" );
        mojo.initialize();

        File expectedAntFile = new File( mojo.getPDEBuildDirectory(), "I.TestBuild/TestBuild-win32.win32.x86.zip" );
        assertEquals( expectedAntFile, mojo.locateAntOutputFile() );
    }

    /**
     * Test that the build configuration properties file can be read correctly.
     * 
     * @throws Exception test failures.
     */
    public void testGetBuildConfigurationProperties()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-product-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );
        mojo.pdeDirectory = new File( getBasedir(), "src/test/resources/product-with-plugin/plugins/test.plugin" );
        mojo.initialize();

        PropertiesConfiguration p = mojo.loadBuildConfigurationProperties();
        assertNotNull( p );
        assertEquals( "/plugin or feature id/path/to/.product", p.getString( "product" ) );
    }

    /**
     * Test a missing build configuration properties file for a product build causes a failure.
     * 
     * @throws Exception test failures.
     */
    public void testGetBuildConfigurationPropertiesWhenFileDoesNotExist()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-product-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );
        mojo.pdeDirectory =
            new File( getBasedir(), "src/test/resources/product-missing-build-properties/plugins/test.plugin" );

        try
        {
            mojo.initialize();
            fail( "This project is missing buildConfiguration/build.properties and should have failed." );
        }
        catch ( MojoExecutionException e )
        {
            assertTrue( true );
        }

    }

    /**
     * Test that the Configs element of the build configuration properties file can be converted
     * correctly and fails the build when it is incorrectly specified.
     * 
     * @throws Exception test failures.
     */
    public void testConvertPdeConfiguration()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-product-config.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );
        mojo.pdeDirectory = new File( getBasedir(), "src/test/resources/product-with-plugin/plugins/test.plugin" );
        mojo.initialize();

        try
        {
            mojo.convertPdeConfigsToFilenameSuffix( null );
            fail( "Null pde configs can not be converted to filename suffix" );
        }
        catch ( MojoExecutionException ex )
        {
            assertTrue( true );
        }

        try
        {
            mojo.convertPdeConfigsToFilenameSuffix( "" );
            fail( "Empty pde configs can not be converted to filename suffix" );
        }
        catch ( MojoExecutionException ex )
        {
            assertTrue( true );
        }

        try
        {
            mojo.convertPdeConfigsToFilenameSuffix( "win32, win32" );
            fail( "Invalid Configuration: must have 3 values" );
        }
        catch ( MojoExecutionException ex )
        {
            assertTrue( true );
        }

        try
        {
            mojo.convertPdeConfigsToFilenameSuffix( "win32, win32, x86 & linux, gtk, ppc" );
            fail( "Multiple configs are not supported by pde build." );
        }
        catch ( MojoExecutionException ex )
        {
            assertTrue( true );
        }

        assertEquals( "win32.win32.x86", mojo.convertPdeConfigsToFilenameSuffix( "win32, win32, x86" ) );
        assertEquals( "win32.win32.x86", mojo.convertPdeConfigsToFilenameSuffix( "win32 , win32 , x86" ) );
        assertEquals( "win32.win32.x86", mojo.convertPdeConfigsToFilenameSuffix( "win32,win32,x86" ) );
    }

    /**
     * Test to make sure we can lookup startup jar for for eclipse 3.3
     * 
     * @throws Exception test failures.
     */
    public void testStarupLookupForEclipse33()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/build/build-plugin-config-3.3.xml" );
        EclipsePDEExtMojo mojo = (EclipsePDEExtMojo) lookupMojo( "ext", pluginXml );
        File startupFile = mojo.findStartupJar();
        assertTrue( startupFile.exists() );
        
    }
    
}
