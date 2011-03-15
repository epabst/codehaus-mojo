/*
 * Copyright 2007 The Apache Software Foundation.
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
package org.codehaus.mojo.webtest.components;

import junit.framework.TestCase;

import java.io.File;

/**
 * Greps through a file line by line and searches for matching strings.
 */
public class WebTestReportCollectorTest extends TestCase
{
    public WebTestReportCollectorTest( String string )
    {
        super( string );
    }

    /**
     * Pick up all pom.xml from the subdirectories.
     *
     * @throws Exception the test failed
     */
    public void testWithMultipleHits() throws Exception
    {
        ReportCollector walker = new ReportCollector("pom.xml");
        File[] result = walker.run(new File("./src/test"));
        assertNotNull(result);
        assertTrue(result.length >= 9);
    }

}
