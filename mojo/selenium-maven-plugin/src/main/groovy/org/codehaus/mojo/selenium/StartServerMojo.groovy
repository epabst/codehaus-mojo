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
import org.apache.commons.lang.SystemUtils
import com.thoughtworks.selenium.DefaultSelenium
import org.codehaus.gmaven.mojo.support.ProcessLauncher

/**
 * Start the Selenium server.
 *
 * @goal start-server
 * @since 1.0-beta-1
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
class StartServerMojo
    extends GroovyMojo
{
    /**
     * The port number of the server to connect to.
     *
     * @parameter expression="${port}" default-value="4444"
     */
    int port
    
    /**
     * Timeout for the server in seconds.
     *
     * @parameter expression="${timeout}" default-value="-1"
     */
    int timeout

    /**
     * Enable the server's debug mode..
     *
     * @parameter expression="${debug}" default-value="false"
     */
    boolean debug
    
    /**
     * Working directory where Selenium server will be started from.
     *
     * @parameter expression="${project.build.directory}/selenium"
     * @required
     */
    File workingDirectory

    /**
     * Enable logging mode.
     *
     * @parameter expression="${logOutput}" default-value="false"
     */
    boolean logOutput

    /**
     * The file that Selenium server logs will be written to.
     *
     * @parameter expression="${logFile}" default-value="${project.build.directory}/selenium/server.log"
     * @required
     */
    File logFile

    /**
     * Flag to control if we background the server or block Maven execution.
     *
     * @parameter expression="${background}" default-value="false"
     * @required
     */
    boolean background

    /**
     * Attempt to verify the named browser configuration.  Must be one of the
     * standard valid browser names (and must start with a *), e.g. *firefox, *iexplore, *custom.
     *
     * @parameter expression="${verifyBrowser}"
     */
    String verifyBrowser
    
    /**
     * Puts you into a mode where the test web site executes in a frame. This mode should only be
     * selected if the application under test does not use frames.
     *
     * @parameter expression="${singleWindow}" default-value="false"
     * @since 1.0-rc-1
     */
    boolean singleWindow
    
    /**
     * Sets the browser mode (e.g. "*iexplore" for all sessions).
     *
     * @parameter expression="${forcedBrowserMode}"
     * @since 1.0-beta-3
     */
    String forcedBrowserMode
    
    /**
     * Stops re-initialization and spawning of the browser between tests.
     *
     * @parameter expression="${browserSessionReuse}" default-value="false"
     * @since 1.0-beta-3
     */
    boolean browserSessionReuse
    
    /**
     * The file or resource to use for default user-extensions.js.
     *
     * @parameter default-value="org/codehaus/mojo/selenium/default-user-extensions.js"
     */
    String defaultUserExtensions

    /**
     * Enable or disable default user-extensions.js
     *
     * @parameter default-value="true"
     */
    boolean defaultUserExtensionsEnabled

    /**
     * Location of the user-extensions.js to load into the server.
     * If defaultUserExtensionsEnabled is true, then this file will be appended to the defaults.
     *
     * @parameter expression="${userExtensions}"
     */
    String userExtensions
    
    /**
     * By default, we proxy every browser request; set this
     * flag to make the browser use our proxy only for URLs containing
     * '/selenium-server'
     *
     * @parameter expression="${avoidProxy}" default-value="false"
     * @since 1.0-beta-3
     */
    boolean avoidProxy
    
    /**
     * Normally a fresh empty Firefox profile is created every time we launch.
     * You can specify a directory to make us copy your profile directory instead.
     *
     * @parameter expression="${firefoxProfileTemplate}"
     */
    File firefoxProfileTemplate
    
    /**
     * Enables logging on the browser side; logging
     * messages will be transmitted to the server.  This can affect
     * performance.
     *
     * @parameter expression="${browserSideLog}" defalut-value="false"
     * @since 1.0-beta-3
     */
    boolean browserSideLog
    
    /**
     * If the browser does not have user profiles,
     * make sure every new session has no artifacts from previous
     * sessions.  For example, enabling this option will cause all user
     * cookies to be archived before launching IE, and restored after IE
     * is closed.
     *
     * @parameter expression="${ensureCleanSession}" default-value="false"
     * @since 1.0-beta-3
     */
    boolean ensureCleanSession
    
    /**
     * Forces the Selenium proxy to trust all
     * SSL certificates.  This doesn't work in browsers that don't use the
     * Selenium proxy.
     *
     * @parameter expression="${trustAllSSLCertificates}" default-value="false"
     * @since 1.0-beta-3
     */
    boolean trustAllSSLCertificates
    
    /**
     * The location of the file to read the display properties.
     *
     * @parameter default-value="${project.build.directory}/selenium/display.properties"
     */
    File displayPropertiesFile
    
    /**
     * The location of the SSL trust-store.
     *
     * @parameter expression="${trustStore}"
     * @since 1.0-beta-3
     */
    File trustStore
    
    /**
     * The password for the SSL trust-store.
     *
     * @parameter expression="${trustStorePassword}"
     * @since 1.0-beta-3
     */
    String trustStorePassword

    /**
     * Configure the Selenium Server to use <tt>http.proxyPort</tt>.
     *
     * @parameter expression="${proxyPort}"
     * @since 1.0-rc-2
     */
    String proxyPort

    /**
     * Configure the Selenium Server to use <tt>http.proxyHost</tt>.
     *
     * @parameter expression="${proxyHost}"
     * @since 1.0-rc-2
     */
    String proxyHost

    /**
     * Configure the Selenium Server to use <tt>http.nonProxyHosts</tt>.
     *
     * @parameter expression="${nonProxyHosts}"
     * @since 1.0-rc-2
     */
    String nonProxyHosts

    /**
     * Allows the server startup to be skipped.
     *
     * @parameter expression="${maven.test.skip}" default-value="false"
     * @since 1.0-beta-2
     */
    boolean skip
    
    //
    // Components
    //
    
    /**
     * @parameter expression="${plugin.artifactMap}"
     * @required
     * @readonly
     */
    Map pluginArtifactMap

    //
    // Mojo
    //

    void execute() {
        if (skip) {
            log.info('Skipping startup')
            return
        }
        
        ant.mkdir(dir: workingDirectory)
        
        if (logOutput) {
            ant.mkdir(dir: logFile.parentFile)
        }
        
        def pluginArifact = { id ->
            def artifact = pluginArtifactMap[id]
            if (!artifact) {
                fail("Unable to locate '$id' in the list of plugin artifacts")
            }
            
            log.debug("Using plugin artifact: ${artifact.file}")
            
            return artifact.file
        }
        
        def launcher = new ProcessLauncher(name: 'Selenium Server', background: background)
        
        launcher.process = {
            ant.java(classname: 'org.openqa.selenium.server.SeleniumServer',
                     fork: true,
                     dir: workingDirectory,
                     failonerror: true)
            {
                classpath() {
                    // Add our plugin artifact to pick up log4j configuration
                    pathelement(location: getClass().protectionDomain.codeSource.location.file)
                    pathelement(location: pluginArifact('log4j:log4j'))
                    pathelement(location: pluginArifact('org.seleniumhq.selenium.server:selenium-server'))
                }
                
                // Set display properties if the properties file exists
                if (displayPropertiesFile && displayPropertiesFile.exists()) {
                    log.info("Including display properties from: $displayPropertiesFile")
                    
                    def props = new Properties()
                    props.load(displayPropertiesFile.newInputStream())
                    props.each { key, value ->
                        env(key: key, value: value)
                    }
                }
                // If the system looks like Unix (and not Mac OS X) then complain if DISPLAY is not set
                else if (SystemUtils.IS_OS_UNIX && !SystemUtils.IS_OS_MAC_OSX) {
                    def tmp = System.getenv('DISPLAY')
                    if (!tmp) {
                        log.warn('OS appears to be Unix and no DISPLAY environment variable has been detected. ' + 
                                 'Browser maybe unable to function correctly. ' + 
                                 'Consider using the selenium:xvfb goal to enable headless operation.')
                    }
                }
                
                if (logOutput) {
                    log.info("Redirecting output to: $logFile")
                    redirector(output: logFile)
                }
                
                // Configure Selenium's logging
                sysproperty(key: 'selenium.log', value: logFile)
                sysproperty(key: 'selenium.loglevel', value: debug ? 'DEBUG' : 'INFO')
                sysproperty(key: 'log4j.configuration', value: 'org/codehaus/mojo/selenium/log4j.properties')
                
                arg(value: '-port')
                arg(value: "$port")
                
                if (timeout > 0) {
                    arg(value: '-timeout')
                    arg(value: "$timeout")
                }
                
                if (debug) {
                    arg(value: '-debug')
                }
                
                if (singleWindow) {
                    arg(value: '-singleWindow')
                }
                
                if (forcedBrowserMode) {
                    arg(value: '-forcedBrowserMode')
                    arg(value: forcedBrowserMode)
                }
                
                if (avoidProxy) {
                    arg(value: '-avoidProxy')
                }
                
                if (browserSideLog) {
                    arg(value: '-browserSideLog')
                }
                
                if (ensureCleanSession) {
                    arg(value: '-ensureCleanSession')
                }
                
                if (trustAllSSLCertificates) {
                    arg(value: '-trustAllSSLCertificates')
                }
                
                if (firefoxProfileTemplate) {
                    if (!firefoxProfileTemplate.exists()) {
                        log.warn("Missing Firefox profile template directory: $firefoxProfileTemplate")
                    }
                    
                    arg(value: '-firefoxProfileTemplate')
                    arg(file: firefoxProfileTemplate)
                }
                
                if (browserSessionReuse) {
                    arg(value: '-browserSessionReuse')
                }
                
                // Maybe configure user extensions
                def file = createUserExtensionsFile()
                if (file) {
                    log.info("User extensions: $file")
                    arg(value: '-userExtensions')
                    arg(file: file)
                }
                
                if (trustStore) {
                    if (!trustStore.exists()) {
                        log.warn("Missing SSL trust-store: $trustStore")
                    }
                    
                    sysproperty(key: 'javax.net.ssl.trustStore', file: trustStore)
                }
                
                if (trustStorePassword) {
                    sysproperty(key: 'javax.net.ssl.trustStorePassword', value: trustStorePassword)
                }

                if (proxyHost) {
                    sysproperty(key: 'http.proxyHost', value: proxyHost)
                }

                if (proxyPort) {
                    sysproperty(key: 'http.proxyPort', value: proxyPort)
                }

                if (nonProxyHosts) {
                    sysproperty(key: 'http.nonProxyHosts', value: nonProxyHosts)
                }
            }
        }
        
        URL url = new URL("http://localhost:$port/selenium-server")
        
        launcher.verifier = {
            log.debug("Trying connection to: $url")
            
            try {
                url.openConnection().content
                
                //
                // Use the Java client API to try and validate that it can actually
                // fire up a browser.  As just launching the server won't really
                // provide feedback if firefox (or whatever browser) isn't on the path/runnable.
                //
                
                if (verifyBrowser) {
                    log.info("Verifying broweser configuration for: $verifyBrowser")
                    
                    try {
                        def selenium = new DefaultSelenium('localhost', port, verifyBrowser, "http://localhost:$port/selenium-server")
                        
                        try {
                            selenium.start()
                        }
                        finally {
                            selenium.stop()
                        }
                    }
                    catch (Exception e) {
                        fail("Failed to verify browser: $verifyBrowser", e)
                    }
                }
                
                return true
            }
            catch (Exception e) {
                return false
            }
        }
        
        launcher.launch()
    }

    /**
     * Create the user-extensions.js file to use, or null if it should not be installed.
     */
    private File createUserExtensionsFile() {
        if (!defaultUserExtensionsEnabled && userExtensions == null) {
            return null
        }
        
        def resolveResource = { name ->
            if (name == null) return null
            
            def url
            def file = new File(name)
            if (file.exists()) {
                url = file.toURI().toURL()
            }
            else {
                try {
                    url = new URL(name)
                }
                catch (MalformedURLException e) {
                    url = Thread.currentThread().contextClassLoader.getResource(name)
                }
            }
            
            if (!url) {
                fail("Could not resolve resource: $name")
            }
            
            log.debug("Resolved resource '$name' as: $url")
            
            return url
        }
        
        // File needs to be named 'user-extensions.js' or Selenium server will puke
        def file = new File(workingDirectory, 'user-extensions.js')
        if (file.exists()) {
            log.debug("Reusing previously generated file: $file")
            return file
        }

        def writer = file.newPrintWriter()
        
        if (defaultUserExtensionsEnabled) {
            def url = resolveResource(defaultUserExtensions)
            log.debug("Using defaults: $url")

            writer.println('//')
            writer.println("// Default user extensions from: $url")
            writer.println('//')
            writer << url.openStream()
        }

        if (userExtensions) {
            def url = resolveResource(userExtensions)
            log.debug("Using user extensions: $url")

            writer.println('//')
            writer.println("// User extensions from: $url")
            writer.println('//')
            writer << url.openStream()
        }

        writer.flush()
        writer.close()

        return file
    }
}
