package org.codehaus.mojo.fitnesse.log;

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

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public class MultipleConsumerTest
    extends MockObjectTestCase
{

    public void testConsumeLine()
    {
        String tStrA = "AaAaAa";
        String tStrB = "Bababa";

        Mock tMockLog = mock( LogConsumer.class );
        Mock tMockFile = mock( FileConsumer.class );
        tMockLog.expects( once() ).method( "consumeLine" ).with( eq( tStrA ) );
        tMockLog.expects( once() ).method( "consumeLine" ).with( eq( tStrB ) );
        tMockFile.expects( once() ).method( "consumeLine" ).with( eq( tStrA ) );
        tMockFile.expects( once() ).method( "consumeLine" ).with( eq( tStrB ) );

        LogConsumer tLog = (LogConsumer) tMockLog.proxy();
        FileConsumer tFile = (FileConsumer) tMockFile.proxy();
        MultipleConsumer tMult = new MultipleConsumer( tLog, tFile );

        tMult.consumeLine( tStrA );
        tMult.consumeLine( tStrB );

        tMockLog.verify();
        tMockFile.verify();
    }

}
