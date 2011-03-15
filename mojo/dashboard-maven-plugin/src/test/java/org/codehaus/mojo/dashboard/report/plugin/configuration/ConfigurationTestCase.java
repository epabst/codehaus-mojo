package org.codehaus.mojo.dashboard.report.plugin.configuration;

/*
 * Copyright 2006 David Vicente
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

import junit.framework.TestCase;

/**
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public class ConfigurationTestCase extends TestCase
{

    public void testEmptyConfiguration() {
        Configuration config = new Configuration();
        assertNull( "version should be null", config.getVersion() );
        assertNotNull( "getSections() must return an empty list", config.getSections() );
        assertEquals( 0, config.getSections().size() );
        assertEquals( null, config.getSectionById( "foobar" ));
//        assertEquals( null, config.getSectionById( null ) );
    }

}
