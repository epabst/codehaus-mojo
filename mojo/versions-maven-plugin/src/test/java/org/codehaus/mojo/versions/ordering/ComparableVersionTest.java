package org.codehaus.mojo.versions.ordering;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import junit.framework.TestCase;

public class ComparableVersionTest
    extends TestCase
{

    public void testCompareTo()
            throws Exception
    {
        assertEquals( 0, new ComparableVersion("1").compareTo(new ComparableVersion("1.0")));
        assertEquals( 0, new ComparableVersion("1.0-final").compareTo(new ComparableVersion("1.0")));
        assertEquals( 0, new ComparableVersion("1.0.final").compareTo(new ComparableVersion("1.0")));
        assertTrue( new ComparableVersion("2.0").compareTo(new ComparableVersion("1.0")) > 0 );
        assertTrue( new ComparableVersion("1.0.1").compareTo(new ComparableVersion("1.0")) > 0 );
        assertTrue( new ComparableVersion("1.0.beta").compareTo(new ComparableVersion("1.0")) < 0 );
        assertTrue( new ComparableVersion("1.0.beta2").compareTo(new ComparableVersion("1.0")) < 0 );
        assertTrue( new ComparableVersion("1.0.b2").compareTo(new ComparableVersion("1.0")) < 0 );
        assertTrue( new ComparableVersion("1.0-b2").compareTo(new ComparableVersion("1.0")) < 0 );
        assertTrue( new ComparableVersion("1.0.sp1").compareTo(new ComparableVersion("1.0")) > 0 );
        assertTrue( new ComparableVersion("1.0-sp1").compareTo(new ComparableVersion("1.0")) > 0 );
    }

}