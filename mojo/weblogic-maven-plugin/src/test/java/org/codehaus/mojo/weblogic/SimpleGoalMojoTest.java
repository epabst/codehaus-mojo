package org.codehaus.mojo.weblogic;

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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * A simple test case to validate that all of the goals for
 * the project exists.
 *
 * @author <a href="mailto:josborn@belltracy.com">Jon Osborn</a>
 * @version $Id$
 * @description Test to make sure that the goals for the project exist.
 */
public class SimpleGoalMojoTest
    extends AbstractMojoTestCase
{

    /**
     * The location for the test plugin pom configuration for testing.
     */
    public static final String TEST_POM = "src/test/resources/unit/basic-test/basic-test-plugin-config.xml";

    private File testPom = null;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {

        // required for mojo lookups to work
        super.setUp();

        this.testPom = new File( getBasedir(), TEST_POM );

        assertTrue( "test pom was not found at " + this.testPom, this.testPom.exists() );
    }

    /**
     * Test that we can find the jwsc goal
     *
     * @throws Exception when the lookup fails
     */
    public void testMojoJwscGoal()
        throws Exception
    {

        final AbstractWeblogicMojo mojo = (AbstractWeblogicMojo) lookupMojo( "jwsc", this.testPom );

        assertNotNull( mojo );
    }

    /**
     * Test that we can find the deploy goal
     *
     * @throws Exception when the lookup fails
     */
    public void testMojoDeployGoal()
        throws Exception
    {

        final AbstractWeblogicMojo mojo = (AbstractWeblogicMojo) lookupMojo( "deploy", this.testPom );

        assertNotNull( mojo );
    }

    /**
     * Test that we can find the Appc goal
     *
     * @throws Exception when the lookup fails
     */
    public void testMojoAppcGoal()
        throws Exception
    {

        final AbstractWeblogicMojo mojo = (AbstractWeblogicMojo) lookupMojo( "appc", this.testPom );

        assertNotNull( mojo );
    }

    /**
     * Test that we can find the clientgen goal
     *
     * @throws Exception when the lookup fails
     * @see org.codehaus.mojo.weblogic.ClientGenMojo
     */
    public void testMojoClientGenGoal()
        throws Exception
    {

        final ClientGenMojo mojo = (ClientGenMojo) lookupMojo( "clientgen", this.testPom );

        assertNotNull( mojo );
    }

    /**
     * Test that we can find the deploy goal
     *
     * @throws Exception when the lookup fails
     * @see org.codehaus.mojo.weblogic.ClientGen9Mojo
     */
    public void testMojoClientGen9Goal()
        throws Exception
    {

        final ClientGen9Mojo mojo = (ClientGen9Mojo) lookupMojo( "clientgen9", this.testPom );

        assertNotNull( mojo );
    }

    /**
     * Test an invalid goal to be sure the valid ones are 'real'.
     *
     * @throws Exception - throws exception when something fails.
     */
    public void testMojoInvalidGoal()
        throws Exception
    {
        try
        {
            final AbstractMojo mojo = (AbstractMojo) lookupMojo( "non-existant-goal", this.testPom );
            assertNull( "Mojo should should have been null.", mojo );
        }
        catch ( org.codehaus.plexus.component.repository.exception.ComponentLookupException e )
        {
            //eat
            return;
        }
        assertTrue( "Component lookup failed because it did not result in an exception.", false );
    }
}
