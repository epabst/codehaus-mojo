package org.codehaus.mojo.antlr3;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.Collections;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.mojo.antlr3.Antlr3PluginMojo;

/**
 * @author <a href="mailto:dave@badgers-in-foil.co.uk">David Holroyd</a>
 * @version $Id $
 */
public class Antlr3PluginMojoTest extends AbstractMojoTestCase
{
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        // if we don't override this, the superclass implementation is used,
        // and fails
    }

    public void testGetIncludesPatterns()
    {
        Antlr3PluginMojo mojo = new CompileAntlr3PluginMojo();
        assertEquals( Collections.singleton( "**/*.g" ), mojo.getIncludesPatterns() );
        mojo.includes.add( "*.g3" );
        assertEquals( Collections.singleton( "*.g3" ), mojo.getIncludesPatterns() );
    }

    public void testSimple() throws Exception
    {
        File testPom = new File( getBasedir(), "src/test/resources/unit/simple-grammar-test/simple-grammar-test-plugin-config.xml" );
        Antlr3PluginMojo mojo = (CompileAntlr3PluginMojo) lookupMojo( "antlr", testPom );
        mojo.execute();

        File out = new File( getBasedir(), "target/test/unit/simple-grammar-test/target/generated-sources/antlr");
        assertExists( new File( out, "SimpleParser.java" ) );
        assertExists( new File( out, "SimpleLexer.java" ) );
        assertExists( new File( out, "Simple.tokens" ) );
    }

    public void testTokenVocab() throws Exception
    {
        File testPom = new File( getBasedir(), "src/test/resources/unit/tokenvocab-grammar-test/tokenvocab-grammar-test-plugin-config.xml" );
        Antlr3PluginMojo mojo = (CompileAntlr3PluginMojo) lookupMojo( "antlr", testPom );
        mojo.execute();

        File out = new File( getBasedir(), "target/test/unit/tokenvocab-grammar-test/target/generated-sources/antlr");
        assertExists( new File( out, "AParser.java" ) );
        assertExists( new File( out, "ALexer.java" ) );
        assertExists( new File( out, "A.tokens" ) );
        assertExists( new File( out, "B.java" ) );
    }

    public void testTokenVocabWithPackage() throws Exception
    {
        File testPom = new File( getBasedir(), "src/test/resources/unit/tokenvocab-package-test/config.xml" );

        Antlr3PluginMojo mojo = (CompileAntlr3PluginMojo) lookupMojo( "antlr", testPom );
        mojo.execute();

        File out = new File( getBasedir(), "target/test/unit/tokenvocab-package-test/target/generated-sources/antlr");
        assertExists( new File( out, "pkg/AParser.java" ) );
        assertExists( new File( out, "pkg/ALexer.java" ) );
        assertExists( new File( out, "pkg/A.tokens" ) );
        assertExists( new File( out, "pkg/B.java" ) );
        assertNotExists( new File( out, "pkg/B__.g" ) );
    }

    private static final void assertExists(File f) {
        assertTrue("File should exist "+f, f.exists());
    }

    private static final void assertNotExists(File f) {
        assertFalse("File should not exist "+f, f.exists());
    }
}