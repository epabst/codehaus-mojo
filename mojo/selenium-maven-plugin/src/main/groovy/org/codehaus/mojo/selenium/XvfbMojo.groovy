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
import org.codehaus.gmaven.mojo.support.ProcessLauncher

/**
 * Starts an Xvfb instance suitable for handling X11 displays for headless systems.
 * Use this in combonation with the <tt>start-server</tt> goal to allow browsers
 * to be launched on headless unix systems.
 *
 * <p>
 * Optionally uses 'xauth' to setup authentication for the Xvfb instance to allow running tests using the
 * frame buffer server when another X server is already running.
 * </p>
 *
 * @goal xvfb
 * @since 1.0-beta-2
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
class XvfbMojo
    extends GroovyMojo
{
    /**
     * The 'Xvfb' command to execute.
     *
     * @parameter default-value="Xvfb"
     * @required
     */
    String xvfbExecutable
    
    /**
     * Use 'xauth' to setup permissions for the Xvfb server.  This requires 'xauth' be installed
     * and may be required when an X server is already running.
     *
     * @parameter default-value="true"
     */
    boolean xauthEnabled
    
    /**
     * The 'xauth' command to execute.
     *
     * @parameter default-value="xauth"
     */
    String xauthExecutable
    
    /**
     * The 'xauth' protocol.
     *
     * @parameter default-value="."
     */
    String xauthProtocol
    
    /**
     * The file where X authentication data is stored for the Xvfb session.
     * Default is to generate a temporary file.
     *
     * @parameter
     */
    File authenticationFile
    
    /**
     * The default display to use.  SSH usualy eats up :10, so lets use :20.  That starts at port 6020.
     */
    static final int DEFAULT_DISPLAY_NUMBER = 20
    
    /**
     * The X11 display to use.  Default value is <tt>:20</tt>.
     *
     * @parameter
     */
    String display
    
    //
    // TODO: See if we need default options for Firefox [ '-screen', '0', '1024x768x24' ]
    //
    
    /**
     * A list of additional options to pass to the Xvfb process.
     *
     * @parameter
     */
    String[] options
    
    /**
     * The location of the file to write the display properties which will be picked up
     * by the <tt>start-server</tt> goal.
     *
     * @parameter default-value="${project.build.directory}/selenium/display.properties"
     * @required
     */
    File displayPropertiesFile
    
    /**
     * Enable logging mode.
     *
     * @parameter default-value="true"
     */
    boolean logOutput

    /**
     * The file that Xvfb output will be written to.
     *
     * @parameter default-value="${project.build.directory}/selenium/xvfb.log"
     */
    File logFile
    
    /**
     * Flag to control if we background the process or block Maven execution.
     *
     * @parameter default-value="true"
     */
    boolean background
    
    /**
     * Skip goal execution
     *
     * @parameter expression="${maven.test.skip}" default-value="false"
     */
    boolean skip

    /**
     * Reuse Xvfb process
     * 
     * @parameter expression="${xvfb.reuse}" default-value="false"
     * @since 1.1
     */
    boolean reuse

    static final String DISPLAY_FILE_PROP = "org.codehaus.mojo.selenium.xvfb.displayFile"

    //
    // Mojo
    //

    void execute() {
        if (skip) {
            log.info('Skipping execution')
            return
        }
        
        def savedDisplayPropertiesFile = System.getProperty(DISPLAY_FILE_PROP)
        if (savedDisplayPropertiesFile != null) {
            log.info('Using existing Xvfb...')
            if (!savedDisplayPropertiesFile.equals(displayPropertiesFile.getAbsolutePath())) {
                ant.copy(file: savedDisplayPropertiesFile, tofile: displayPropertiesFile)
            }
            return
        }

        log.info('Starting Xvfb...')
        
        // Figure out what the display number is, and generate the properties file
        if (!display) {
            display = detectUsableDisplay()
        }
        else {
            if (isDisplayInUse(display)) {
                fail("It appears that the configured display is already in use: $display")
            }
        }
        
        log.info("Using display: $display")
        
        if (xauthEnabled) {
            setupXauthority()
        }
        
        createDisplayProperties()
        
        if (logOutput) {
            ant.mkdir(dir: logFile.parentFile)
        }
        
        def launcher = new ProcessLauncher(name: 'Xvfb', background: background)
        
        launcher.process = {
            ant.exec(executable: xvfbExecutable, failonerror: true) {
                if (logOutput) {
                    log.info("Redirecting output to: $logFile")
                    redirector(output: logFile)
                }
                
                if (xauthEnabled) {
                    env(key: 'XAUTHORITY', file: authenticationFile)
                }
                
                // Set the display
                arg(value: display)
                
                // Add extra options
                if (options) {
                    options.each {
                        arg(value: it)
                    }
                }
            }
            
            if (xauthEnabled) {
                ant.delete(file: authenticationFile)
            }
            
            //
            // FIXME: Really need an easy way to nuke this process... Ant's exec interface 
            //        doesn't really cut it :-(
            //
        }
        
        launcher.verifier = {
            def success = isDisplayInUse(display)
            if (success && reuse) {
                System.setProperty(DISPLAY_FILE_PROP, displayPropertiesFile.getAbsolutePath())
            }
            return success
        }
        
        launcher.launch()
    }
    
    private void createDisplayProperties() {
        // Write out the display properties so that the start-server goal can pick it up
        ant.mkdir(dir: displayPropertiesFile.parentFile)
        def props = new Properties()
        props.setProperty('DISPLAY', display)
        
        // Write the xauth file so clients pick up the right perms
        if (xauthEnabled) {
            assert authenticationFile
            props.setProperty('XAUTHORITY', authenticationFile.canonicalPath)
        }
        
        props.store(displayPropertiesFile.newOutputStream(), 'Xvfb Display Properties')
    }
    
    /**
     * Generate a 128-bit random hexadecimal number for use with the X authority system.
     */
    private String createCookie() {
        def cookie
        
        byte[] bytes = new byte[16]
        new Random().nextBytes(bytes)
        cookie = new BigInteger(bytes)

        String cookieHex = cookie.abs().toString(16)
        int padding = 32 - cookieHex.length()
        for (int i = 0; i < padding; i++) {
            cookieHex = "0" + cookieHex
        }
        return cookieHex
    }
    
    /**
     * Setup the X authentication file (Xauthority)
     */
    private void setupXauthority() {
        if (!authenticationFile) {
            authenticationFile = File.createTempFile('Xvfb', '.Xauthority')
            authenticationFile.deleteOnExit()
        }
        
        log.info("Using Xauthority file: $authenticationFile")
        
        ant.delete(file: authenticationFile)
        
        //
        // TODO: Really need a way to check if this will execute or not first
        //
        
        def cookie = createCookie()
        log.debug("Using cookie: $cookie")
        
        // Use xauth to configure authentication for the display using a generated cookie
        ant.exec(executable: xauthExecutable, failonerror: true) {
            env(key: 'XAUTHORITY', file: authenticationFile)
            
            arg(value: 'add')
            arg(value: display)
            arg(value: xauthProtocol)
            arg(value: cookie)
            
            //
            // FIXME: This spits out junk to STDERR, not very nice... :-(
            //
        }
        
        if (!authenticationFile.exists()) {
            fail("It appears that 'xauth' failed to create the Xauthority file: $authenticationFile")
        }
    }
    
    /**
     * Detect which display is usable.
     */
    private String detectUsableDisplay() {
        log.debug('Detecting a usable display...')
        
        boolean found = false
        int n = DEFAULT_DISPLAY_NUMBER
        
        while (!found && (n <= DEFAULT_DISPLAY_NUMBER + 10)) {
            def d = ":$n"
            log.debug("Trying display: $d")
            
            if (!isDisplayInUse(d)) {
                return d
            }
            else {
                n++
            }
        }
        
        fail("Could not find a usable display")
    }
    
    /**
     * Decode the port number for the display.
     */
    private int decodeDisplayPort(display) {
        assert display
        
        def m = display =~ /[^:]*:([0-9]*)(\.([0-9]*))?/
        
        def i = Integer.parseInt(m[0][1])
        
        //
        // Normally, the first X11 display is on port 6000, the next on port 6001,
        // which get abbreviated as :0, :1 and so on.
        //
        
        return 6000 + i
    }
    
    /**
     * Check if the given display is in use or not.
     */
    private boolean isDisplayInUse(display) {
        int port = decodeDisplayPort(display)
        
        log.debug("Checking if display is in use: $display on port: $port")
        
        try {
            def socket = new Socket('localhost', port)
            socket.close()
            return true
        }
        catch (ConnectException e) {
            return false
        }
    }

}
