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
public class GrepTest extends TestCase
{
    public GrepTest( String string )
    {
        super( string );
    }


    public void testGrepWithMultipleHits() throws Exception
    {
        String[] files = {"pom.xml"};
        Grep grepper = new Grep(new File("."), files, "developers");
        int nrOfHits = grepper.match().length;
        assertTrue(nrOfHits == 1);
    }

    public void testGrepWithNoHits() throws Exception
    {
        String[] files = {"pom.xml"};
        Grep grepper = new Grep(new File("."), files, "grmppffffffffff");
        int nrOfHits = grepper.match().length;
        assertTrue(nrOfHits == 0);
    }

    public void testGrepWithMultipleFiles() throws Exception
    {
        String[] files = {"pom.xml", "LICENSE.txt"};
        Grep grepper = new Grep(new File("."),files, "webtest");
        int nrOfHits = grepper.match().length;
        assertTrue(nrOfHits == 1);
    }

}
