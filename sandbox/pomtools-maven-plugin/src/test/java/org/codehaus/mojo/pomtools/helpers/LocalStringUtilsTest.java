package org.codehaus.mojo.pomtools.helpers;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import org.codehaus.plexus.PlexusTestCase;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class LocalStringUtilsTest
    extends PlexusTestCase
{
    public LocalStringUtilsTest()
    {
        super();
    }

    public void testSplitCamelCase()
    {
        assertEquals( null, LocalStringUtils.splitCamelCase( null ) );
        
        assertEquals( "", LocalStringUtils.splitCamelCase( "" ) );
        
        assertEquals( " ", LocalStringUtils.splitCamelCase( " " ) );
        
        assertEquals( "Foo Bar Foo", LocalStringUtils.splitCamelCase( "fooBarFoo" ) );
        
        assertEquals( "Foo Bar", LocalStringUtils.splitCamelCase( "foo Bar" ) );
        
        assertEquals( "Foo Bar Foo", LocalStringUtils.splitCamelCase( "fooBar foo" ) );
        
        assertEquals( "Foo Bar\nFoo", LocalStringUtils.splitCamelCase( "fooBar\nfoo" ) );
        
        assertEquals( "Ci Management", LocalStringUtils.splitCamelCase( "ciManagement" ) );
    }
   
    public void testMakeEndWith()
    {
        assertEquals( null, LocalStringUtils.makeEndWith( null, "s" ) );
        
        assertEquals( "s", LocalStringUtils.makeEndWith( "", "s" ) );
        
        assertEquals( " s", LocalStringUtils.makeEndWith( " ", "s" ) );
        
        
        assertEquals( "foobar", LocalStringUtils.makeEndWith( "foo", "bar" ) );
        
        assertEquals( "foobar", LocalStringUtils.makeEndWith( "foobar", "bar" ) );
        
        assertEquals( "text\n", LocalStringUtils.makeEndWith( "text\n", "\n" ) );
        
        
    }

}
