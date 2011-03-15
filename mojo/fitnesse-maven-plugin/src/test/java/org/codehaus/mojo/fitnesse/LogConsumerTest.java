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

import java.util.logging.Level;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.mojo.fitnesse.log.LogConsumer;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class LogConsumerTest
    extends MockObjectTestCase
{
    public void testConsumeLine()
    {
        Mock tMockLog = mock( Log.class );
        tMockLog.stubs().method( "info" ).withAnyArguments();
        Log tLogProxy = (Log) tMockLog.proxy();

        LogConsumer tConsumer = new LogConsumer( tLogProxy, Level.INFO );
        assertFalse( tConsumer.hasGeneratedResultFile() );

        tConsumer.consumeLine( "...." );
        assertFalse( tConsumer.hasGeneratedResultFile() );
        tConsumer.consumeLine( "TestSimpleClass1 has failures" );
        assertFalse( tConsumer.hasGeneratedResultFile() );
        tConsumer.consumeLine( "Test Pages: 0 right, 1 wrong, 0 ignored, 0 exceptions" );
        assertFalse( tConsumer.hasGeneratedResultFile() );
        tConsumer.consumeLine( "Assertions: 4 right, 1 wrong, 0 ignored, 0 exceptions" );
        assertFalse( tConsumer.hasGeneratedResultFile() );
        tConsumer.consumeLine( "Formatting as html to D:\\SCM\\ProjectSVN\\maven-fitnesse-plugin\\src\\it\\multiproject\\target/fitnesse/fitnesseResultSuiteCoverage2.html" );
        assertTrue( tConsumer.hasGeneratedResultFile() );
        tConsumer.consumeLine( "------------------------------------------------------------------------" );
        assertTrue( tConsumer.hasGeneratedResultFile() );
    }

}
