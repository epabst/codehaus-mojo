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
import org.apache.maven.reporting.MavenReportException;

public class FitnessePageTest
    extends TestCase
{

    public void testIsFitnessePageResult()
    {
        FitnessePage tPage = new FitnessePage();
        tPage.setFileName( "target/test-classes/onlyOneReport/fitnesse/fitnesseResult_localhost_SuiteCoverage3.html" );
        assertTrue( tPage.isFitnessePageResult() );
        tPage.setFileName( "target/test-classes/onlyOneReport/fitnesse/fitnesseResult_localhost_SuiteCoverage3_output.txt" );
        assertFalse( tPage.isFitnessePageResult() );
        tPage.setFileName( "target/test-classes/remote/fitnesse.js" );
        assertFalse( tPage.isFitnessePageResult() );
        tPage.setFileName( "target/test-classes/remote/fitnesse_base.css" );
        assertFalse( tPage.isFitnessePageResult() );
        tPage.setFileName( "target/test-classes/remote/SuiteBrut.html" );
        assertFalse( tPage.isFitnessePageResult() );
    }

    public void testGetFitnessePageName()
        throws MavenReportException
    {
        FitnessePage tPage = new FitnessePage();
        tPage.setFileName( "target/test-classes/reportDir/WithClover/clover/fitnesse/fitnesseResult_localhost_SuiteCoverage3.html" );
        assertEquals( "localhost_SuiteCoverage3", tPage.getFitnessePageName() );
        tPage.setFileName( "target/test-classes/onlyOneReport/fitnesse/fitnesseResult_localhost_SuiteCoverage3.html" );
        assertEquals( "localhost_SuiteCoverage3", tPage.getFitnessePageName() );
    }

    public void testGetName()
    {
        FitnessePage tPage = new FitnessePage();
        tPage.setFileName( "target/test-classes/reportDir/WithClover/clover/fitnesse/fitnesseResult_localhost_SuiteCoverage3.html" );
        assertEquals( "fitnesseResult_localhost_SuiteCoverage3.html", tPage.getName() );
    }

    public void testGetStatusOk()
        throws MojoExecutionException
    {
        FitnessePage tPage = new FitnessePage();
        tPage.setFileName( "target/test-classes/remoteFailure/SuiteInfraOk.html" );
        assertEquals( FitnessePage.STATUS_OK, tPage.getStatus() );
        tPage.setFileName( "target/test-classes/remoteFailure/SuiteOk.html" );
        assertEquals( FitnessePage.STATUS_OK, tPage.getStatus() );
        tPage.setFileName( "target/test-classes/remoteFailure/SuiteInfraOk.html" );
        assertEquals( FitnessePage.STATUS_OK, tPage.getStatus() );
    }

    public void testGetStatusFail()
        throws MojoExecutionException
    {
        FitnessePage tPage = new FitnessePage();
        tPage.setFileName( "target/test-classes/remoteFailure/SuiteFail.html" );
        assertEquals( FitnessePage.STATUS_FAIL, tPage.getStatus() );
        tPage.setFileName( "target/test-classes/remoteFailure/SuiteInfraFail.html" );
        assertEquals( FitnessePage.STATUS_FAIL, tPage.getStatus() );
        tPage.setFileName( "target/test-classes/remoteFailure/TestFail.html" );
        assertEquals( FitnessePage.STATUS_FAIL, tPage.getStatus() );
    }

    public void testGetStatusError()
        throws MojoExecutionException
    {
        FitnessePage tPage = new FitnessePage();
        tPage.setFileName( "target/test-classes/remoteFailure/SuiteException.html" );
        assertEquals( FitnessePage.STATUS_ERROR, tPage.getStatus() );
        tPage.setFileName( "target/test-classes/remoteFailure/TestException.htm" );
        assertEquals( FitnessePage.STATUS_ERROR, tPage.getStatus() );
    }

    public void testGetStatusWithInvalidPage()
    {
        FitnessePage tPage = new FitnessePage();
        try
        {
            tPage.setFileName( "target/test-classes/remote/fitnesse_base.css" );
            tPage.getStatus();
            fail( "Should not pass" );
        }
        catch ( MojoExecutionException e )
        {
            assertTrue( e.getMessage().startsWith( "This file isn't a FitNesse result page [" ) );
            ;
        }
        try
        {
            tPage.setFileName( "target/test-classes/remote/kfklsqjdlqkjfq.SQ" );
            tPage.getStatus();
            fail( "Should not pass" );
        }
        catch ( MojoExecutionException e )
        {
            assertTrue( e.getMessage().startsWith( "This file isn't a FitNesse result page [" ) );
            assertTrue( e.getMessage().endsWith( "kfklsqjdlqkjfq.SQ]" ) );
        }

    }

}
