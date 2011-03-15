package org.codehaus.mojo.solaris;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.Logger;

import java.io.File;

/**
 * @author <a href="mailto:trygvis@codehaus.org">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class GeneratePrototypeMojoTest
    extends AbstractMojoTestCase
{
    LoggerManager loggerManager;

    protected void setUp()
        throws Exception 
    {
        super.setUp();
        loggerManager = (LoggerManager) lookup( LoggerManager.ROLE );
        loggerManager.setThreshold( Logger.LEVEL_DEBUG );
    }

    protected void tearDown()
        throws Exception
    {
        release( loggerManager );
    }

    public void testProjectBasicConfiguration()
        throws Exception
    {
        doTest();
    }

    public void testProjectDefaults()
        throws Exception
    {
        doTest();
    }

    public void testProjectRelocation()
        throws Exception
    {
        doTest();
    }

    private void doTest() throws Exception {
        String name = StringUtils.addAndDeHump( getName().substring( 4 ) );

        File root = getTestFile("src/test/projects/" + name + "/target/root");
        File assembledPkg = getTestFile("target/" + name + "/solaris/assembled-pkg");
        FileUtils.deleteDirectory( assembledPkg );
        FileUtils.copyDirectoryStructure(root, assembledPkg);

        File expectedPrototype = getTestFile( "src/test/projects/" + name + "/prototype" );
        File generatedPrototype = new File( assembledPkg, "generated-prototype" );

        Mojo mojo = lookupMojo( "generate-prototype", getTestFile( "src/test/projects/" + name + "/test-pom.xml" ) );

        mojo.execute();

        String expected = FileUtils.fileRead( expectedPrototype );
        String actual = FileUtils.fileRead( generatedPrototype );

        assertEquals( expected, actual );
    }
}
