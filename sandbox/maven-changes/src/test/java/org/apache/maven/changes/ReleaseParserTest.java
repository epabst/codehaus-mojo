package org.apache.maven.changes;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

/**
 * @author <a href="snicoll@apache.org">Stephane Nicoll</a>
 * @version $Id$
 */
public class ReleaseParserTest
    extends TestCase
{
    public void testParseValidFile()
    {
        final InputStream in = getClass().getResourceAsStream( "/changes.xml" );
        try
        {
            final Release[] releases = ReleaseParser.parse( new InputStreamReader( in ) );
            assertNotNull( releases );
            assertEquals( 5, releases.length );

        }
        catch ( InvalidChangesException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; changes.xml is valid" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; changes.xml is valid" );
        }
    }

    public void testParseInvalidFile()
    {
        final InputStream in = getClass().getResourceAsStream( "/invalid-changes.xml" );
        try
        {
            ReleaseParser.parse( new InputStreamReader( in ) );
            fail("Should have failed to parse invalid-changes.xml");

        }
        catch ( InvalidChangesException e )
        {
            // OK
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            fail( "Should not have failed" );
        }
    }

    public void testParseReleaseOneDotZero()
    {
        final InputStream in = getClass().getResourceAsStream( "/changes.xml" );
        try
        {
            final Release release = ReleaseParser.parse( new InputStreamReader( in ), "1.0" );
            assertNotNull( release );
            assertEquals( "2002-08-04", release.getReleaseDate() );
            assertEquals( "First release.", release.getDescription() );
            assertEquals( 1, release.getActions().length );
            assertEquals( "snicoll", release.getActions()[0].getAuthor() );
            assertEquals( ActionType.ADD, release.getActions()[0].getType() );
            assertEquals( "Initial plugin created.", release.getActions()[0].getDescription() );
            assertNull( release.getActions()[0].getIssueId() );
            assertNull( release.getActions()[0].getDueTo() );
            assertNull( release.getActions()[0].getDueToEmail() );

        }
        catch ( InvalidChangesException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; changes.xml is valid" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; changes.xml is valid" );
        }
        catch ( ReleaseNotFoundException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; release 1.0 exists in changes.xml" );
        }
    }

    public void testParseReleaseOneDotOne()
    {
        final InputStream in = getClass().getResourceAsStream( "/changes.xml" );
        try
        {
            final Release release = ReleaseParser.parse( new InputStreamReader( in ), "1.1" );
            assertNotNull( release );
            assertEquals( "2003-05-08", release.getReleaseDate() );
            assertNull( release.getDescription() );
            assertEquals( 2, release.getActions().length );

            // First one (make sure that the natural order (order in the file) is respected
            assertEquals( "aat", release.getActions()[0].getAuthor() );
            assertEquals( ActionType.ADD, release.getActions()[0].getType() );
            assertEquals( "Added autogeneration of some component.", release.getActions()[0].getDescription() );
            assertNull( release.getActions()[0].getIssueId() );
            assertNull( release.getActions()[0].getDueTo() );
            assertNull( release.getActions()[0].getDueToEmail() );

            assertEquals( "snicoll", release.getActions()[1].getAuthor() );
            assertEquals( ActionType.FIX, release.getActions()[1].getType() );
            assertEquals( "Fixed this nasty bug.", release.getActions()[1].getDescription() );
            assertEquals( "TST-23", release.getActions()[1].getIssueId() );
            assertNull( release.getActions()[1].getDueTo() );
            assertNull( release.getActions()[0].getDueToEmail() );

        }
        catch ( InvalidChangesException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; changes.xml is valid" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; changes.xml is valid" );
        }
        catch ( ReleaseNotFoundException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; release 1.0 exists in changes.xml" );
        }
    }

    public void testParseReleaseOneDotTwoDotOne()
    {
        final InputStream in = getClass().getResourceAsStream( "/changes.xml" );
        try
        {
            final Release release = ReleaseParser.parse( new InputStreamReader( in ), "1.2.1" );
            assertNotNull( release );
            assertEquals( 1, release.getActions().length );
            assertEquals( "snicoll", release.getActions()[0].getAuthor() );
            assertEquals( ActionType.FIX, release.getActions()[0].getType() );
            assertEquals( "Another documentation of some properties were outdated.", release.getActions()[0].getDescription() );
            assertEquals( "TST-29", release.getActions()[0].getIssueId() );
            assertEquals( "friend", release.getActions()[0].getDueTo() );
            assertEquals( "friend@somedomain.org", release.getActions()[0].getDueToEmail() );

        }
        catch ( InvalidChangesException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; changes.xml is valid" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; changes.xml is valid" );
        }
        catch ( ReleaseNotFoundException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; release 1.0 exists in changes.xml" );
        }
    }

    public void testParseUnexistingVersion()
    {
        final InputStream in = getClass().getResourceAsStream( "/changes.xml" );
        try
        {
            ReleaseParser.parse( new InputStreamReader( in ), "1.2.1.5.4.1.3.2.1.5" );
            fail("Should have failed, version does not exist");

        }
        catch ( InvalidChangesException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; changes.xml is valid" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; changes.xml is valid" );
        }
        catch ( ReleaseNotFoundException e )
        {
            // OK
        }
    }

    public void testParseInvalidActionType()
    {
        final InputStream in = getClass().getResourceAsStream( "/invalid-changes.xml" );
        try
        {
            ReleaseParser.parse( new InputStreamReader( in ), "1.0" );
            fail("Should have failed, invalid type");

        }
        catch ( InvalidChangesException e )
        {
              // OK
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            fail( "Should not have failed" );
        }
        catch ( ReleaseNotFoundException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; release exists" );
        }
    }

    public void testParseMissingAuthor()
    {
        final InputStream in = getClass().getResourceAsStream( "/invalid-changes.xml" );
        try
        {
            ReleaseParser.parse( new InputStreamReader( in ), "1.2" );
            fail("Should have failed, missing author in action");

        }
        catch ( InvalidChangesException e )
        {
              // OK
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            fail( "Should not have failed" );
        }
        catch ( ReleaseNotFoundException e )
        {
            e.printStackTrace();
            fail( "Should not have failed ; release exists" );
        }
    }

}
