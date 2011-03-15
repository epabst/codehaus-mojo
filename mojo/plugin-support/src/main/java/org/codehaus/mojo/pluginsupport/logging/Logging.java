/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.codehaus.mojo.pluginsupport.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Maven plugin logging helpers to initialize and reset logging.
 *
 * @version $Id$
 */
public class Logging
{
    private static boolean initialized;

    public synchronized static void init() {
        if (!initialized) {
            LogFactory.releaseAll();
            
            //
            // FIXME: Need to move this back to the car-maven-plugin, need to revisit
            //        all this mojo logging to JCL/Log4j muck...
            //
            
            // Setup the delegating log
            System.setProperty("org.apache.commons.logging.Log", "org.codehaus.mojo.pluginsupport.logging.DelegatingLog");

            // Make sure that Geronimo bootstrap logging does not clobber our logging
            System.setProperty("geronimo.bootstrap.logging.enabled", "false");
            
            Log log = LogFactory.getLog(Logging.class);
            log.debug("Initialized");

            initialized = true;
        }
    }

    public synchronized static void reset() {
        if (initialized) {
            Log log = LogFactory.getLog(Logging.class);
            log.debug("Resetting");

            LogFactory.releaseAll();

            // Restore a reasonable default log impl
            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");

            // Make SimpleLog look more like Maven logs
            System.setProperty("org.apache.commons.logging.simplelog.showShortLogname", "false");
            
            // Restore default Geornimo bootstrap behavior
            System.getProperties().remove("geronimo.bootstrap.logging.enabled");

            initialized = false;
        }
    }
}
