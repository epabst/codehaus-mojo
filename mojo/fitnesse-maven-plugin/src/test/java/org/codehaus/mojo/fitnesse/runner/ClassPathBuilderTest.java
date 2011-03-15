package org.codehaus.mojo.fitnesse.runner;

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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.mojo.fitnesse.ClassPathSubstitution;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class ClassPathBuilderTest
    extends MockObjectTestCase
{

    public void testTransformPathFull()
    {
        Mock tMockLog = mock( Log.class );
        tMockLog.stubs().method( "debug" ).withAnyArguments();
        Log tLogProxy = (Log) tMockLog.proxy();

        String tServerPath =
            "/etc/udd/m2_repository/org/fitnesse/fitlibrary/20050731/fitlibrary-20050731.jar;"
                + "/etc/udd/m2_repository/org/fitnesse/fitnesse/20050731/fitnesse-20050731.jar;"
                + "/etc/udd/m2_repository/com/octo/infra/secu-fitnesse/1.0-SNAPSHOT/secu-fitnesse-1.0-SNAPSHOT.jar;"
                + "/etc/udd/m2_repository/commons-httpclient/commons-httpclient/3.1-beta1/commons-httpclient-3.1-beta1.jar;"
                + "/etc/udd/m2_repository/commons-codec/commons-codec/1.2/commons-codec-1.2.jar;"
                + "/etc/udd/m2_repository/commons-logging/commons-logging/1.0.3/commons-logging-1.0.3.jar;"
                + "/etc/udd/m2_repository/org/apache/maven/plugins/maven-fitnesse-plugin-sample-multiproject-jar1/1.0-SNAPSHOT/maven-fitnesse-plugin-sample-multiproject-jar1-1.0-SNAPSHOT-clover.jar;"
                + "/etc/udd/m2_repository/org/apache/maven/plugins/maven-fitnesse-plugin-sample-multiproject-jar2/1.0-SNAPSHOT/maven-fitnesse-plugin-sample-multiproject-jar2-1.0-SNAPSHOT-clover.jar;"
                + "/etc/udd/m2_repository/com/cenqua/clover/clover/1.3.13/clover-1.3.13.jar;fitnesse.jar;fitlibrary.jar";

        String tExpectedPath =
            "D:\\maven\\m2_repository\\org\\fitnesse\\fitlibrary\\20050731\\fitlibrary-20050731.jar;"
                + "D:\\maven\\m2_repository\\org\\fitnesse\\fitnesse\\20050731\\fitnesse-20050731.jar;"
                + "D:\\maven\\m2_repository\\com\\octo\\infra\\secu-fitnesse\\1.0-SNAPSHOT\\secu-fitnesse-1.0-SNAPSHOT.jar;"
                + "D:\\maven\\m2_repository\\commons-httpclient\\commons-httpclient\\3.1-beta1\\commons-httpclient-3.1-beta1.jar;"
                + "D:\\maven\\m2_repository\\commons-codec\\commons-codec\\1.2\\commons-codec-1.2.jar;"
                + "D:\\maven\\m2_repository\\commons-logging\\commons-logging\\1.0.3\\commons-logging-1.0.3.jar;"
                + "D:\\maven\\m2_repository\\org\\apache\\maven\\plugins\\maven-fitnesse-plugin-sample-multiproject-jar1\\1.0-SNAPSHOT\\maven-fitnesse-plugin-sample-multiproject-jar1-1.0-SNAPSHOT-clover.jar;"
                + "D:\\maven\\m2_repository\\org\\apache\\maven\\plugins\\maven-fitnesse-plugin-sample-multiproject-jar2\\1.0-SNAPSHOT\\maven-fitnesse-plugin-sample-multiproject-jar2-1.0-SNAPSHOT-clover.jar;"
                + "D:\\maven\\m2_repository\\com\\cenqua\\clover\\clover\\1.3.13\\clover-1.3.13.jar;"
                + "fitnesse.jar;fitlibrary.jar";
        String tExpected2 =
            "\\etc\\udd\\m2_repository\\org\\fitnesse\\fitlibrary\\20050731\\fitlibrary-20050731.jar;"
                + "\\etc\\udd\\m2_repository\\org\\fitnesse\\fitnesse\\20050731\\fitnesse-20050731.jar;"
                + "\\etc\\udd\\m2_repository\\com\\octo\\infra\\secu-fitnesse\\1.0-SNAPSHOT\\secu-fitnesse-1.0-SNAPSHOT.jar;"
                + "\\etc\\udd\\m2_repository\\commons-httpclient\\commons-httpclient\\3.1-beta1\\commons-httpclient-3.1-beta1.jar;"
                + "\\etc\\udd\\m2_repository\\commons-codec\\commons-codec\\1.2\\commons-codec-1.2.jar;"
                + "\\etc\\udd\\m2_repository\\commons-logging\\commons-logging\\1.0.3\\commons-logging-1.0.3.jar;"
                + "\\etc\\udd\\m2_repository\\org\\apache\\maven\\plugins\\maven-fitnesse-plugin-sample-multiproject-jar1\\1.0-SNAPSHOT\\maven-fitnesse-plugin-sample-multiproject-jar1-1.0-SNAPSHOT-clover.jar;"
                + "\\etc\\udd\\m2_repository\\org\\apache\\maven\\plugins\\maven-fitnesse-plugin-sample-multiproject-jar2\\1.0-SNAPSHOT\\maven-fitnesse-plugin-sample-multiproject-jar2-1.0-SNAPSHOT-clover.jar;"
                + "\\etc\\udd\\m2_repository\\com\\cenqua\\clover\\clover\\1.3.13\\clover-1.3.13.jar;fitnesse.jar;fitlibrary.jar";

        ClassPathBuilder tBuilder = new ClassPathBuilder( tLogProxy );
        List tSubsi = new ArrayList();
        tSubsi.add( new ClassPathSubstitution( "/etc/udd/m2_repository", "D:\\maven\\m2_repository" ) );
        tSubsi.add( new ClassPathSubstitution( "/", "\\" ) );
        assertEquals( tExpectedPath, tBuilder.transformPath( tServerPath, tSubsi ) );

        // Order is important
        tSubsi = new ArrayList();
        tSubsi.add( new ClassPathSubstitution( "/", "\\" ) );
        tSubsi.add( new ClassPathSubstitution( "/etc/udd/m2_repository", "D:\\maven\\m2_repository" ) );
        assertEquals( tExpected2, tBuilder.transformPath( tServerPath, tSubsi ) );

        tSubsi = new ArrayList();
        tSubsi.add( new ClassPathSubstitution( "/etc/udd/m2_repository", "D:/maven/m2_repository" ) );
        tSubsi.add( new ClassPathSubstitution( "/", "\\" ) );
        assertEquals( tExpectedPath, tBuilder.transformPath( tServerPath, tSubsi ) );

        tSubsi = new ArrayList();
        assertEquals( tServerPath, tBuilder.transformPath( tServerPath, tSubsi ) );
    }

    public void testTransformPathWithTrailingSpaceAndWithoutQuoteBis()
    {
        Mock tMockLog = mock( Log.class );
        tMockLog.stubs().method( "debug" ).withAnyArguments();
        Log tLogProxy = (Log) tMockLog.proxy();

        String tServerPath =
            "\"/etc/udd/m2_repository/org/fitnesse/fit library/20050731/fitlibrary-20050731.jar   \";"
                + "\"  /etc/udd/m2_repository/org/fitnesse/fitnesse/20050731/fitnesse-20050731.jar\";";
        String tExpectedPath =
            "/etc/udd/m2_repository/org/fitnesse/fit library/20050731/fitlibrary-20050731.jar;"
                + "/etc/udd/m2_repository/org/fitnesse/fitnesse/20050731/fitnesse-20050731.jar;";
        ClassPathBuilder tBuilder = new ClassPathBuilder( tLogProxy );
        List tSubsi = new ArrayList();
        assertEquals( tExpectedPath, tBuilder.transformPath( tServerPath, tSubsi ) );

    }

    public void testReplaceAll()
    {
        String tServerPath = "/etc/udd/m2_repository/org/fitnesse/fitlibrary/20050731/fitlibrary-20050731.jar;";

        String tExpectedPath =
            "\\etc\\udd\\m2_repository\\org\\fitnesse\\fitlibrary\\20050731\\fitlibrary-20050731.jar;";

        ClassPathBuilder tBuilder = new ClassPathBuilder();
        assertEquals( tExpectedPath, tBuilder.replaceAll( tServerPath, "/", "\\" ) );

        tExpectedPath = "/etc/udd/m2_repository/org/fitnesse/fitlibrary/20090731/fitlibrary-20090731.jar;";
        assertEquals( tExpectedPath, tBuilder.replaceAll( tServerPath, "20050731", "20090731" ) );
    }

}
