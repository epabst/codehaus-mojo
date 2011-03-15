package org.codehaus.mojo.fitnesse;

/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

import junit.framework.TestCase;

import org.apache.maven.plugin.MojoExecutionException;

public class TestFitnesse
    extends TestCase
{

    public void testGetTypeByDefault()
        throws MojoExecutionException
    {
        checkGetTypeByDefaultWithDefaultValue( null );
        checkGetTypeByDefaultWithDefaultValue( "" );
    }

    private void checkGetTypeByDefaultWithDefaultValue( String tDefault )
        throws MojoExecutionException
    {
        Fitnesse tFit = new Fitnesse( "localhost", 80, "aWikiSpace.SuiteMySuite", null );
        tFit.setType( tDefault );
        assertEquals( Fitnesse.PAGE_TYPE_SUITE, tFit.getType() );

        tFit = new Fitnesse( "localhost", 80, "aWikiSpace.TestMySuite", null );
        tFit.setType( tDefault );
        assertEquals( Fitnesse.PAGE_TYPE_TEST, tFit.getType() );

        tFit = new Fitnesse( "localhost", 80, "SuiteWikiSpace.SuiteMySuite", null );
        tFit.setType( tDefault );
        assertEquals( Fitnesse.PAGE_TYPE_SUITE, tFit.getType() );

        tFit = new Fitnesse( "localhost", 80, "SuiteWikiSpace.TestMySuite", null );
        tFit.setType( tDefault );
        assertEquals( Fitnesse.PAGE_TYPE_TEST, tFit.getType() );

        tFit = new Fitnesse( "localhost", 80, "TestWikiSpace.SuiteMySuite", null );
        tFit.setType( tDefault );
        assertEquals( Fitnesse.PAGE_TYPE_SUITE, tFit.getType() );

        tFit = new Fitnesse( "localhost", 80, "TestWikiSpace.TestMySuite", null );
        tFit.setType( tDefault );
        assertEquals( Fitnesse.PAGE_TYPE_TEST, tFit.getType() );

        tFit = new Fitnesse( "localhost", 80, "SuiteMySuite", null );
        tFit.setType( tDefault );
        assertEquals( Fitnesse.PAGE_TYPE_SUITE, tFit.getType() );

        tFit = new Fitnesse( "localhost", 80, "TestMySuite", null );
        tFit.setType( tDefault );
        assertEquals( Fitnesse.PAGE_TYPE_TEST, tFit.getType() );
    }

    public void testGetTypeWithInvalidType()
    {
        Fitnesse tFit = new Fitnesse( "localhost", 80, "aWikiSpace.SuiteMySuite", null );
        try
        {
            tFit.setType( "invalid" );
            tFit.getType();
        }
        catch ( MojoExecutionException e )
        {
            assertEquals(
                          "Invalid type [invalid] for the server [Fitnesse address=http://localhost:80/aWikiSpace.SuiteMySuite], should be either [suite] or [test].",
                          e.getMessage() );
        }
    }

    public void testGetTypeWithInvalidName()
    {
        Fitnesse tFit = new Fitnesse( "localhost", 80, "aWikiSpace.ksgkjMySuite", null );
        try
        {
            tFit.setType( "" );
            tFit.getType();
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( "Parameter 'type' is mandatory when the page name doesn't begin "
                + "with 'Test' or 'Suite' according to FitNesse " + "convention. FitNesse server is: Fitnesse "
                + "address=http://localhost:80/aWikiSpace.ksgkjMySuite", e.getMessage() );
        }
    }

    public void testGetTypeWithExplicitType()
        throws MojoExecutionException
    {
        checkGetTypeWithExplicitType( Fitnesse.PAGE_TYPE_SUITE, Fitnesse.PAGE_TYPE_TEST );
        checkGetTypeWithExplicitType( "Suite", "Test" );
        checkGetTypeWithExplicitType( "SuItE", "TeSt" );
    }

    private void checkGetTypeWithExplicitType( String pSuiteParam, String pTestParam )
        throws MojoExecutionException
    {
        Fitnesse tFit = new Fitnesse( "localhost", 80, "aWikiSpace.SuiteMySuite", null );
        tFit.setType( pSuiteParam );
        assertEquals( Fitnesse.PAGE_TYPE_SUITE, tFit.getType() );
        tFit.setType( pTestParam );
        assertEquals( Fitnesse.PAGE_TYPE_TEST, tFit.getType() );

        tFit = new Fitnesse( "localhost", 80, "aWikiSpace.TestMySuite", null );
        tFit.setType( pSuiteParam );
        assertEquals( Fitnesse.PAGE_TYPE_SUITE, tFit.getType() );
        tFit.setType( pTestParam );
        assertEquals( Fitnesse.PAGE_TYPE_TEST, tFit.getType() );

        tFit = new Fitnesse( "localhost", 80, "SuiteWikiSpace.SuiteMySuite", null );
        tFit.setType( pSuiteParam );
        assertEquals( Fitnesse.PAGE_TYPE_SUITE, tFit.getType() );
        tFit.setType( pTestParam );
        assertEquals( Fitnesse.PAGE_TYPE_TEST, tFit.getType() );

        tFit = new Fitnesse( "localhost", 80, "SuiteWikiSpace.TestMySuite", null );
        tFit.setType( pSuiteParam );
        assertEquals( Fitnesse.PAGE_TYPE_SUITE, tFit.getType() );
        tFit.setType( pTestParam );
        assertEquals( Fitnesse.PAGE_TYPE_TEST, tFit.getType() );

        tFit = new Fitnesse( "localhost", 80, "TestWikiSpace.SuiteMySuite", null );
        tFit.setType( pSuiteParam );
        assertEquals( Fitnesse.PAGE_TYPE_SUITE, tFit.getType() );
        tFit.setType( pTestParam );
        assertEquals( Fitnesse.PAGE_TYPE_TEST, tFit.getType() );

        tFit = new Fitnesse( "localhost", 80, "TestWikiSpace.TestMySuite", null );
        tFit.setType( pSuiteParam );
        assertEquals( Fitnesse.PAGE_TYPE_SUITE, tFit.getType() );
        tFit.setType( pTestParam );
        assertEquals( Fitnesse.PAGE_TYPE_TEST, tFit.getType() );
    }

    public void testCheckConfigurationOk()
        throws MojoExecutionException
    {
        Fitnesse tFit = new Fitnesse( "localhost", 80, "myPage", null );
        tFit.checkConfiguration();
    }

    public void testCheckConfigurationWithBadServer()
        throws MojoExecutionException
    {
        Fitnesse tFit = new Fitnesse( null, 80, "myPage", null );
        try
        {
            tFit.checkConfiguration();
            fail( "Should not pass !" );
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( "Fitnesse host is mandatory.", e.getMessage() );
        }
        try
        {
            tFit.setHostName( "" );
            tFit.checkConfiguration();
            fail( "Should not pass !" );
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( "Fitnesse host is mandatory.", e.getMessage() );
        }
    }

    public void testCheckConfigurationWithBadPort()
        throws MojoExecutionException
    {
        Fitnesse tFit = new Fitnesse( "localhost", -80, "myPage", null );
        try
        {
            tFit.checkConfiguration();
            fail( "Should not pass !" );
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( "The port should be a valid IP port [-80].", e.getMessage() );
        }
    }

    public void testCheckConfigurationWithBadPage()
        throws MojoExecutionException
    {
        Fitnesse tFit = new Fitnesse( "localhost", 80, null, null );
        try
        {
            tFit.checkConfiguration();
            fail( "Should not pass !" );
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( "Fitnesse page name is mandatory.", e.getMessage() );
        }
        try
        {
            tFit.setPageName( "" );
            tFit.checkConfiguration();
            fail( "Should not pass !" );
        }
        catch ( MojoExecutionException e )
        {
            assertEquals( "Fitnesse page name is mandatory.", e.getMessage() );
        }
    }
}
