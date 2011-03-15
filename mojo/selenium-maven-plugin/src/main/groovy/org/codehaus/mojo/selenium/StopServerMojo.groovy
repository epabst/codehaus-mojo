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

package org.codehaus.mojo.selenium

import org.codehaus.gmaven.mojo.GroovyMojo

/**
 * Stop the Selenium server.
 *
 * @goal stop-server
 * @since 1.0-beta-2
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
class StopServerMojo
    extends GroovyMojo
{
    /**
     * The port number of the server to connect to.
     *
     * @parameter expression="${port}" default-value="4444"
     */
    int port
    
    /**
     * Skip goal execution
     *
     * @parameter expression="${maven.test.skip}" default-value="false"
     */
    boolean skip
    
    //
    // Mojo
    //

    void execute() {
        if (skip) {
            log.info('Skipping execution')
            return
        }
        
        println('Stopping Selenium server...')
        
        def url = new URL("http://localhost:$port/selenium-server/driver/?cmd=shutDownSeleniumServer")
        
        log.debug("Stop request URL: $url")
        
        url.openConnection().content
        
        println('Stop request sent')
    }
}
