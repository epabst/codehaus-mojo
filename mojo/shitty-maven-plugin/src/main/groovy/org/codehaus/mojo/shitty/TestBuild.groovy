/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.mojo.shitty

import org.apache.maven.plugin.logging.Log
import org.apache.maven.plugin.MojoExecutionException

import org.apache.tools.ant.Project
import org.apache.tools.ant.ExitStatusException

import org.apache.commons.lang.time.StopWatch
import org.codehaus.mojo.shitty.util.PrefixingStream
import org.codehaus.groovy.maven.common.StreamPair
import org.codehaus.groovy.maven.common.SystemOutputHijacker

/**
 * Represents a test build.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
class TestBuild
    implements Runnable
{
    private final Log log

    private final TestMojo mojo

    private final PrintStream out

    private final PrintStream err

    final StopWatch watch = new StopWatch()

    final File pomFile

    final String name
    
    final File baseDir

    final List goals

    final List flags

    final Properties properties

    final File logFile

    private Throwable failure
    
    private String status

    TestBuild(final TestMojo mojo, final String name, final File pomFile) {
        assert mojo
        assert pomFile
        assert pomFile.exists()
        assert pomFile.isFile()
        assert pomFile.canRead()
        
        this.mojo = mojo
        this.log = mojo.log
        this.pomFile = pomFile
        this.baseDir = pomFile.parentFile
        this.name = name

        this.out = System.out
        this.err = System.err
        this.watch = new StopWatch()

        this.goals = discoverGoals()
        this.flags = discoverFlags()
        this.properties = discoverProperties()
        this.logFile = createLogFile()
    }

    Throwable getFailure() {
        return failure
    }

    boolean isFailed() {
        return failure != null
    }
    
    String getStatus() {
        return status
    }
    
    String toString() {
        def s = "$name (${pomFile})"
        
        // Append our status if we have one
        if (status != null) {
            return "$s - $status"
        }
        
        return s
    }

    //
    // Configuration Discovery
    //

    private List discoverGoals() {
        def file = new File(baseDir, mojo.goalsFile)

        if (!file.exists()) {
            throw new MojoExecutionException("Missing goals file (${mojo.goalsFile}) in project directory: $baseDir")
        }

        log.debug("Loading goals: $file")

        def list = []

        // Multipule goals could be on the same line
        file.text.tokenize().each {
            list << it
        }

        log.debug("Goals: $list")

        return list
    }
    
    private List discoverFlags() {
        def list = []

        // Add flags from the flagsFile if it exists
        def file = new File(baseDir, mojo.flagsFile)

        if (file.exists()) {
            log.debug("Loading flags: $file")

            // Some flags might need spaces, so each line is a flag def
            file.eachLine { line ->
                list << line
            }
        }

        // Add other flags if there are any
        if (mojo.flags) {
            mojo.flags.tokenize().each {
                list << it
            }
        }

        log.debug("Flags: $list")

        return list
    }

    private Properties discoverProperties() {
        def props = new Properties()

        def file = new File(baseDir, mojo.propertiesFile)

        if (file.exists()) {
            log.debug("Loading properties: $file")

            props.load(file.newInputStream())
        }

        log.debug("Properties: $props")

        return props
    }

    private File createLogFile() {
        def file = new File(baseDir, mojo.buildLogFile)
        file.parentFile.mkdirs()

        // When running the setup script it will append, so make sure we start with a fresh log
        if (file.exists()) {
            file.delete()
        }

        file.createNewFile()

        log.debug("Log file: $file")

        return file
    }

    //
    // Test Execution
    //

    private Closure block = { Closure task ->
        out.println('-' * 79)

        try {
            return task.call()
        }
        finally {
            out.println('-' * 79)
        }
    }

    private StreamPair createLogStreams() {
        def base = new BufferedOutputStream(new FileOutputStream(logFile))

        return new StreamPair(new PrefixingStream('OUT: ', base), new PrefixingStream('ERR: ', base))
    }

    void run() {
        if (status != null) {
            throw new IllegalStateException("Test build is already running or has finished already; can not re-run: ${toString()}")
        }
        
        status = 'RUNNING'
        
        log.debug('Running')
        
        // Register streams for this thread
        def streams = createLogStreams()
        SystemOutputHijacker.register(streams)

        // Run the build
        try {
            watch.start()
            
            try {
                block {
                    build()
                }
                
                status = 'FINISHED'
            }
            finally {
                watch.stop()
            }
        }
        catch (Throwable t) {
            failure = t
            
            status = 'FAILED'
            
            // Special handling for Ant's exit status
            if (t instanceof ExitStatusException) {
                // Capture the exit status, but omit the useless trace
                err.println("FAILURE: Execution exited with status: ${t.status}")
            }
            else {
                // Capture the failure to the log file w/full trace
                err.println("FAILURE: $t (${t.getClass().name})")
                t.printStackTrace(err)
            }
        }
        finally {
            // Close and deregister our streams
            streams.flush()

            streams.close()

            SystemOutputHijacker.deregister()
        }

        log.debug("Finished (${watch})")
    }

    private void build() {
        // Display some details of the build before we start
        out.println("TEST BUILD: $pomFile")
        
        out.println("Goals: ${goals.join(' ')}")

        if (flags) {
            out.println("Flags: ${flags.join(' ')}")
        }

        if (properties) {
            out.println("Properties: ${properties}")
        }

        // Invoke pre-test script
        invokeScript('setup', mojo.setupScriptFilename)

        // Invoke Maven to run the test
        block {
            maven()
        }
        
        //
        // TODO: Pass any failure exception to validation script to allow it to descide if its a failure or not
        //
        
        // Invoke post-test validate script
        invokeScript('validate', mojo.validateScriptFilename)

        // We are good, save the state to the build.log and the mvn log
        out.println('SUCCESS')
    }

    private AntBuilder createAnt() {
        def ant = new AntBuilder()

        def logger = ant.antProject.buildListeners[0]

        logger.emacsMode = true

        if (mojo.debug) {
            logger.messageOutputLevel = Project.MSG_DEBUG
        }
        else if (mojo.verbose) {
            logger.messageOutputLevel = Project.MSG_VERBOSE
        }

        return ant
    }

    private void maven() {
        def ant = createAnt()

        ant.java(classname: 'org.codehaus.classworlds.Launcher', failonerror: true, fork: true) {
            // Setup the classpath based on what Maven 2.x needs
            classpath {
                fileset(dir: mavenHome) {
                    include(name: 'boot/*.jar')
                }
            }

            // Setup props required to boot Maven
            sysproperty(key: 'classworlds.conf', value: "$mavenHome/bin/m2.conf")
            sysproperty(key: 'maven.home', value: mavenHome)

            // If the local repo was specified on the command-line, then propagate it to children
            def localRepo = System.getProperty('maven.repo.local')
            if (localRepo) {
                log.debug("Using local repo: $localRepo")
                sysproperty(key: 'maven.repo.local', value: localRepo)
            }

            // If the global settings file was specified on the command-line, then propagate it to children
            def globalSettings = System.getProperty('org.apache.maven.global-settings')
            if (globalSettings) {
                log.debug("Using global settings: $globalSettings")
                sysproperty(key: 'org.apache.maven.global-settings', value: globalSettings)
            }

            // If the user settings file was specified on the command-line, then propagate it to children
            def userSettings = System.getProperty('org.apache.maven.user-settings')
            if (userSettings) {
                log.debug("Using user settings: $userSettings")
                sysproperty(key: 'org.apache.maven.user-settings', value: userSettings)
            }

            // Enable debug output if user asked, or the log stream is set to debug
            if (mojo.debug || log.debugEnabled) {
                arg(value: '-X')
            }

            // Set the projects pom
            arg(value: '--file')
            arg(file: pomFile)

            // Add some default flags
            arg(value: '--batch-mode')
            arg(value: '--errors')

            if (mojo.offline) {
                arg(value: '--offline')
            }

            flags.each {
                arg(value: it)
            }

            // Add properties (Using -D here to let Maven handle instead of Java)
            properties.each { key, value ->
                arg(value: "-D${key}=${value}")
            }

            goals.each {
                arg(value: it)
            }
        }
    }

    /**
     * Get the Maven home directory.
     */
    private File getMavenHome() {
        String path = System.properties['maven.home']

        if (!path) {
            // This should really never happen
            throw new InternalError("Missing 'maven.home' system property")
        }

        def home = new File(path)

        if (!home.exists()) {
            // This should really never happen
            throw new InternalError("Missing 'maven.home' directory; property is configured but directory is missing")
        }

        return home.canonicalFile
    }

    /**
     * Invoke a script.
     */
    private void invokeScript(final String type, final String fileName) {
        assert type
        assert fileName

        def script = new File(baseDir, fileName)

        if (!script.exists()) {
            log.debug("Skipping script execution; missing file: $script")
            return;
        }

        log.debug("Executing script: $script")

        // Setup a new ant builder rooted to the correct basedir
        def ant = createAnt()
        ant.antProject.baseDir = baseDir

        // Pass some details to the script about its environment
        def binding = new Binding([
            basedir: baseDir,
            ant: ant,
            log: new ScriptLogger(StreamPair.SYSTEM),
            goals: goals,
            properties: properties,
            flags: flags,
            settings: mojo.settings
        ])

        //
        // TODO: Add configuration to set the compile dir
        //
        
        // Setup the class loader for the script to include the projects test scope classpath
        def cl = new GroovyClassLoader()
        
        log.debug('Script classpath:')
        mojo.project.testClasspathElements.each {
            def file = new File("$it")
            log.debug("    $file")
            cl.addURL(file.toURL())
        }
        
        def shell = new GroovyShell(cl, binding)

        def result

        block {
            out.println("SCRIPT ($type): $script")

            try {
                result = shell.evaluate(script)
            }
            catch (Throwable t) {
                // Save the details to the log
                err.println("SCRIPT FAILED: $t")
                t.printStackTrace(err)

                throw new ScriptException(script, type, t)
            }
        }

        // If we have a result and it evaluates to false, then it's a failure
        if (result != null && !result) {
            throw new ScriptException(script, type, result)
        }
    }
}

/**
 * Thrown when a script execution fails.
 */
class ScriptException
    extends Exception
{
    final File script

    final String type

    final Object result

    //
    // NOTE: Have to "${}..." as String to prevent CCE on GStringImpl muck on super() calls :-(
    //

    ScriptException(final File script, final String type, final Object result) {
        super("Script ($type) reported failure: ${String.valueOf(result)}" as String)

        this.script = script
        this.type = type
        this.result = result
    }

    ScriptException(final File script, final String type, final Throwable cause) {
        super("Script ($type) failed: $cause" as String, cause)

        this.script = script
        this.type = type
        this.result = null
    }
}