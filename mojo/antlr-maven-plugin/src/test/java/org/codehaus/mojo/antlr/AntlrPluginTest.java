package org.codehaus.mojo.antlr;

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
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.DefaultArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.StubArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.antlr.stubs.DependencyArtifactStubFactory;
import org.codehaus.mojo.antlr.stubs.DependencyProjectStub;
import org.codehaus.plexus.util.FileUtils;

/**
 * <code>Unit tests</code> of Antlr plugin
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class AntlrPluginTest
    extends AbstractMojoTestCase
{
	protected File testDir;
    protected DependencyArtifactStubFactory artifactStubFactory;

	/**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        // required for mojo lookups to work
        super.setUp();
        testDir = new File( getBasedir() );
		testDir = new File( testDir, "target" );
		testDir = new File( testDir, "unit-tests" );
		testDir = new File( testDir, "antlr-tests" );
        removeDirectory( testDir );
        assertFalse( testDir.exists() );

        artifactStubFactory = new DependencyArtifactStubFactory( this.testDir, false );
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown()
        throws Exception
    {
		if ( testDir != null )
        {
            try
            {
                removeDirectory( testDir );
            }
            catch ( IOException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                fail( "Trying to remove directory:" + testDir + "\r\n" + e.toString() );
            }
            assertFalse( testDir.exists() );

            testDir = null;
        }

        artifactStubFactory = null;
    }

    /**
     * Method to test Antlr generation
     *
     * @throws Exception
     */
    public void testJavaGrammar()
        throws Exception
    {
        File testPom = new File( getBasedir(),
                                 "src/test/resources/unit/java-grammar-test/java-grammar-test-plugin-config.xml" );
        AntlrPlugin mojo = (AntlrPlugin) lookupMojo( testPom, true );
        mojo.execute();

        File outputDir = new File( getBasedir(),
                                   "target/test/unit/java-grammar-test/target/generated-sources/antlr/" );
        assertTrue( new File( outputDir, "JavaLexer.java" ).exists() );
        assertTrue( new File( outputDir, "JavaRecognizer.java" ).exists() );
        assertTrue( new File( outputDir, "JavaTokenTypes.java" ).exists() );
        assertTrue( new File( outputDir, "JavaTokenTypes.txt" ).exists() );
    }

    /**
     * Method to test Antlr generation
     *
     * @throws Exception
     */
    public void testJavaGrammarInheritance()
        throws Exception
    {
        File testPom = new File( getBasedir(),
                                 "src/test/resources/unit/java-grammar-inheritance-test/java-grammar-inheritance-test-plugin-config.xml" );
        AntlrPlugin mojo = (AntlrPlugin) lookupMojo( testPom, true );
        mojo.execute();

        File outputDir = new File( getBasedir(),
                                   "target/test/unit/java-grammar-inheritance-test/target/generated-sources/antlr/" );
        assertTrue( outputDir.exists() );
        assertTrue( new File( outputDir, "GnuCEmitter.java" ).exists() );
        assertTrue( new File( outputDir, "GnuCEmitterTokenTypes.java" ).exists() );
        assertTrue( new File( outputDir, "GnuCLexer.java" ).exists() );
        assertTrue( new File( outputDir, "GnuCLexerTokenTypes.java" ).exists() );
        assertTrue( new File( outputDir, "GnuCParser.java" ).exists() );
        assertTrue( new File( outputDir, "GNUCTokenTypes.java" ).exists() );
        assertTrue( new File( outputDir, "GnuCTreeParser.java" ).exists() );
        assertTrue( new File( outputDir, "GnuCTreeParserTokenTypes.java" ).exists() );
        assertTrue( new File( outputDir, "StdCLexer.java" ).exists() );
        assertTrue( new File( outputDir, "StdCParser.java" ).exists() );
        assertTrue( new File( outputDir, "STDCTokenTypes.java" ).exists() );
    }

	public void testMissingAntlrDependency() throws Exception {
		try {
			File testPom = new File( getBasedir(),
									 "src/test/resources/unit/no-antlr-dep/no-antlr-dep-test-plugin-config.xml" );
			AntlrPlugin mojo = (AntlrPlugin) lookupMojo( testPom, false );
			mojo.execute();

			fail( "was expecting failure due to missing antlr dep" );
		}
		catch ( AntlrPlugin.NoAntlrDependencyDefinedException expected ) {
			// expected behavior
		}
	}

	protected Mojo lookupMojo(File pom, boolean addAntlrDep) throws Exception {
        AntlrPlugin mojo = (AntlrPlugin) super.lookupMojo( "generate", pom );

		assertNotNull( mojo );
		assertNotNull( mojo.project );
		MavenProject project = mojo.project;

		if ( addAntlrDep ) {
			Artifact antlrArtifact = artifactStubFactory.createArtifact( "antlr", "antlr", "2.7.7" );
			resolveArtifact( antlrArtifact );

			ArrayList artifacts = new ArrayList();
			artifacts.add( antlrArtifact );
			( ( DependencyProjectStub ) project ).setCompileArtifacts( artifacts );
		}

		return mojo;
	}

	protected void resolveArtifact(Artifact artifact) throws Exception {
		if ( artifact.isResolved() ) {
			return;
		}

		File localRepoDir = new File( new File( new File( getBasedir(), "target" ), "test" ), "stub-repo" );
		ArtifactRepository localRepository = new StubArtifactRepository( localRepoDir.getAbsolutePath() );

		DefaultArtifactRepositoryFactory artifactRepositoryFactory = new DefaultArtifactRepositoryFactory();
		ArtifactRepository centralRepo = artifactRepositoryFactory.createArtifactRepository(
				"central-stub",
				"http://repo1.maven.org/maven2/",
				new DefaultRepositoryLayout(),
				new ArtifactRepositoryPolicy( true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS, ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE ),
				new ArtifactRepositoryPolicy( true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS, ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE )
		);
		ArrayList remoteRepositories = new ArrayList();
		remoteRepositories.add( centralRepo );

		ArtifactResolver artifactResolver = ( ArtifactResolver ) super.lookup( "org.apache.maven.artifact.resolver.ArtifactResolver" );
		artifactResolver.resolve( artifact, remoteRepositories, localRepository );
		artifact.setResolved( true );
	}

	public static void removeDirectory( File dir ) throws IOException {
		FileUtils.deleteDirectory( dir );
    }
}
