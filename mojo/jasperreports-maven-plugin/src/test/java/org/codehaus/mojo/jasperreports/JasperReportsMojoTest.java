package org.codehaus.mojo.jasperreports;

/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License") you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.File;

/**
 * @author gjoseph
 * @author $Author$ (last edit)
 * @version $Revision$
 */
public class JasperReportsMojoTest
    extends TestCase
{
    public void testClasspathBuildingAddsStuffIfNeeded()
    {
        List fakeElements = Arrays.asList( new String[]{"foo", "bar", "baz"} );
        JasperReportsMojo mojo = new JasperReportsMojo();
        assertEquals( "foo" + File.pathSeparator + "bar" + File.pathSeparator + "baz",
                mojo.buildClasspathString( fakeElements, null ) );
        assertEquals( "foo" + File.pathSeparator + "bar" + File.pathSeparator + "baz" + File.pathSeparator + "bingo",
                mojo.buildClasspathString( fakeElements, "bingo" ) );
    }

    public void testClasspathBuildingWorksWithEmptyList()
    {
        JasperReportsMojo mojo = new JasperReportsMojo();
        assertEquals( "", mojo.buildClasspathString( Collections.EMPTY_LIST, null ) );
        assertEquals( "plop", mojo.buildClasspathString( Collections.EMPTY_LIST, "plop" ) );
    }

    public void testClasspathBuildingWorksWithSingletonList()
    {
        JasperReportsMojo mojo = new JasperReportsMojo();
        assertEquals( "foo", mojo.buildClasspathString( Collections.singletonList( "foo" ), null ) );
        assertEquals( "foo" + File.pathSeparator + "plop",
                mojo.buildClasspathString( Collections.singletonList( "foo" ), "plop" ) );
    }
}
