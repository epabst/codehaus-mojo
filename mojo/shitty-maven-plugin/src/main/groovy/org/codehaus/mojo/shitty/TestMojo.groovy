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
import org.apache.maven.shared.model.fileset.FileSet
import org.apache.maven.shared.model.fileset.util.FileSetManager
import org.apache.maven.settings.Settings

import org.apache.commons.lang.time.StopWatch

import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue
import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor
import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit

import jline.Terminal
import jline.ANSIBuffer
import org.codehaus.mojo.shitty.util.NamedThreadFactory
import org.codehaus.groovy.maven.common.SystemOutputHijacker

/**
 * Invoke child Maven builds to perform super helpful integration testing.
 *
 * @goal test
 * @phase integration-test
 * @since 1.0-alpha-1
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
class TestMojo
    extends ShittyMojoSupport
{
    /**
     * The set of test builds (<tt>pom.xml</tt> files) to execute. Defaults to all <tt>pom.xml</tt> files under <tt>src/it</tt>.
     *
     * @parameter
     */
    FileSet[] projects

    /**
     * Directory where integration test projects files are copied to before execution.
     *
     * @parameter default-value="${project.build.directory}/shitty"
     */
    File workingDirectory
    
    /**
     * Skip test build execution.
     *
     * @parameter expression="${maven.test.skip}" default-value="false"
     */
    boolean skip

    /**
     * Ignore test build failures.  Normally if failures are detected the build will be stopped.
     * Setting this to <tt>true</tt> will cause the build to report success.
     *
     * @parameter expression="${maven.test.failure.ignore}" default-value="false"
     */
    boolean ignoreFailures
    
    /**
     * Provides custom selection of projects to be executed.
     *
     * @parameter expression="${tests}"
     */
    String tests
    
    /**
     * The name of the project-specific file that contains the list of goals
     * to execute for that test.  Multiple goals may be given on a single line,
     * or spread over multiple lines in the file.
     * 
     * @parameter default-value="goals.txt"
     * @required
     */
    String goalsFile
    
    //
    // As much as I'd like to default this to target/build.log, stuff that goes in target
    // gets nuked by the clean goals, which messes up our logging completely.
    //
    
    /**
     * The name of the build log to capture output to.
     *
     * @parameter default-value="build.log"
     * @required
     */
    String buildLogFile
    
    /**
     * The name of the project-specific file that contains the list of additional flags to pass to the <tt>mvn</tt> executable.
     * Some flags (or rather flag arguments) might have spaces, so there is one flag (or flag argument) per-line in the file.
     * 
     * @parameter default-value="flags.txt"
     */
    String flagsFile
    
    /**
     * Additional flags to pass to the <tt>mvn</tt> executable.  Multiple flags are separated by white-space.  To use a
     * flag which contains white-space see <tt>flagsFile</tt>.
     * 
     * @parameter expression="${flags}"
     */
    String flags
    
    /**
     * The name of the project-specific file that contains the execution (system) properties to be defined for the test.
     * 
     * @parameter default-value="test.properties"
     */
    String propertiesFile
    
    /**
     * The filename of the <em>setup</em> script.  If this file exists as a peer to the test build pom, then it will be executed
     * <em>before</em> the test build is executed.
     *
     * @parameter default-value="setup.groovy"
     */
    String setupScriptFilename
    
    /**
     * The filename of the <em>validate</em> script.  If this file exists as a peer to the test build pom, then it will be executed
     * <em>after</em> the test build is executed.
     *
     * @parameter default-value="validate.groovy"
     */
    String validateScriptFilename
    
    /**
     * If <tt>true</tt> then test builds are run offline.  By default picks up the offline settings from the current environment.
     *
     * @parameter expression="${offline}" default-value="${settings.offline}"
     */
    boolean offline

    /**
     * Enable or disable use of ANSI colors.  By default this value is auto-detected.
     *
     * @parameter expression="${color}"
     */
    boolean color = Terminal.terminal.isANSISupported()
    
    /**
     * Set to <tt>true</tt> to run test builds in parallel.
     *
     * @parameter expression="${parallel}" default-value="false"
     */
    boolean parallel
    
    /**
     * The number of threads in the pool to use when test build execution is run in parallel.
     * Default is the number of available processors + 1.
     *
     * @parameter expression="${threadCount}"
     */
    int threadCount = Runtime.runtime.availableProcessors() + 1
    
    /**
     * The number of seconds to wait for <em>all</em> parallel test build executions to complete.
     *
     * @parameter expression="${parallelTimeOut}" default-value="600"
     */
    int parallelTimeOut
    
    /**
     * When <tt>true</tt> displays extra details about the test build execution, and reports full logs for failed test builds.
     *
     * @parameter expression="${verbose}" default-value="false"
     */
    boolean verbose
    
    /**
     * Enable child debug flags (ie. <tt>mvn -X ...</tt>).
     *
     * @parameter expression="${debug}" default-value="false"
     */
    boolean debug
    
    /**
     * @parameter expression="${settings}
     * @required
     * @readonly
     */
    Settings settings
    
    //
    // State
    //
    
    private List failures = []
    
    private int count = 0
    
    private StopWatch suiteWatch = new StopWatch()
    
    private ThreadPoolExecutor threadPool
    
    private ANSIBuffer colorBuffer() {
        return new ANSIBuffer(ansiEnabled: color)
    }
    
    //
    // Mojo
    //
    
    void execute() {
        if (skip) {
            log.info('Skipping test execution')
            return
        }
        
        if (offline) {
            log.debug('Tests will be built offline')
        }
        
        if (parallel) {
            log.debug('Tests will be run in parallel')
            
            threadPool = new ThreadPoolExecutor(
                threadCount,
                threadCount,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(),
                new NamedThreadFactory(TestBuild.class),
                new CallerRunsPolicy())
        }
        
        // Install the hijacker and register the current system streams for this thread
        SystemOutputHijacker.install(System.out, System.err)
        
        try {
            runAll()
        }
        finally {
            SystemOutputHijacker.uninstall()
        }
    }
    
    private List testBuilds
    
    private int maxTestBuildNameLength = 0
    
    private List getTestBuilds() {
        if (testBuilds == null) {
            List list = []
            
            //
            // TODO: Copy projects to workingDirectory
            //
            
            FileSetManager fsm = new FileSetManager(log, log.debugEnabled)
            
            getTestFileSets().each { fileset ->
                fileset = resolveFileSet(fileset)
                
                def basedir = new File(fileset.directory)
                def includes = fsm.getIncludedFiles(fileset)
                
                includes.each { fileName ->
                    def pomFile = new File(basedir, fileName)
                    
                    def name = basedir.toURI().relativize(pomFile.parentFile.toURI()).toString()
                    if (name.endsWith('/')) {
                        name = name[0..-2]
                    }
                    
                    def build = new TestBuild(this, name, pomFile)
                    
                    list << build
                    
                    // Keep track of the max name length
                    int l = build.name.size()
                    if (l > maxTestBuildNameLength) {
                        maxTestBuildNameLength = l
                    }
                }
            }

            list.sort { x, y -> x.pomFile <=> y.pomFile }
            
            if (log.debugEnabled) {
                log.debug('Test builds:')
                
                list.each {
                    log.debug("    $it")
                }
            }
            
            testBuilds = list
        }
        
        return testBuilds
    }
    
    private List testFileSets
    
    private List getTestFileSets() {
        if (testFileSets == null) {
            // If no projects were configured, then setup the default
            if (!projects) {
                def fileset = new FileSet(directory: 'src/it')
                fileset.addInclude('*/pom.xml')
                
                projects = [ fileset ] as FileSet[]
            }
            
            List list
            
            // Apply custom pattern to select specific tests or a single test
            if (tests) {
                log.debug("Selecting matching test builds: $tests")
                
                list = []
                
                for (p in projects) {
                    def fileset = new FileSet(directory: p.directory)
                    
                    tests.tokenize(',').each {
                        def include = "${it.trim()}"
                        
                        if (!include.endsWith('/pom.xml')) {
                            include = "${include}/pom.xml"
                        }
                        
                        fileset.addInclude(include)
                    }
                    
                    list << fileset
                }
            }
            else {
                list = projects as List
            }
            
            if (log.debugEnabled) {
                log.debug('Test filesets:')
                
                list.each {
                    log.debug("    ${it.directory} + ${it.includes} - ${it.excludes}")
                }
            }
            
            testFileSets = list
        }
        
        return testFileSets
    }
    
    private void runAll() {
        log.debug('Running all test builds')
        
        def builds = getTestBuilds()
        
        if (builds.isEmpty()) {
            log.warn('No test builds were found; skipping execution')
            return
        }
        
        log.info("Starting ${builds.size()} test builds")
        
        if (verbose) {
            builds.each {
                log.info("    $it")
            }
        }
        
        log.info('')
        log.info('-' * 79)
        log.info('')
        
        suiteWatch.start()
        
        // Create tasks for each test build and run them (or if parallel enabled, queue for threaded execution)
        builds.each { build ->
            runSingle(build)
        }
        
        // If we are running in parallel, then wait for the tasks to complete
        if (parallel) {
            log.debug("Waiting for test builds to complete; timeout after $parallelTimeOut seconds")
            
            // Shutdown the threads
            threadPool.shutdown()
            
            // And then wait for the pool to shutdown
            try {
                threadPool.awaitTermination(parallelTimeOut, TimeUnit.SECONDS)
                
                log.debug('All test build tasks finished')
            }
            catch (InterruptedException e) {
                // if we didn't shutdown in time, then force it to shutdown and then puke
                def unfinished = threadPool.shutdownNow()

                def msg = "${unfinished.size()} test build tasks(s) failed to finish in the required time ($parallelTimeOut seconds)"
                
                // Dump the unfinished tasks
                log.debug("${msg}:")
                unfinished.each {
                    log.debug("    $it")
                }

                // And then die
                fail(msg)
            }
        }
        
        suiteWatch.stop()
        
        report()
    }
    
    private void runSingle(final TestBuild build) {
        assert build
        
        log.debug("Running single: $build")
        
        def task = new TestBuildRunner(this, build)
        
        count++
        
        // Queue or run the test depending on if we are parallel or not
        if (parallel) {
            log.debug("Queuing task: $task")
            
            threadPool.execute(task)
        }
        else {
            log.debug("Running task: $task")
            
            task.run()
        }
    }
    
    /**
     * Generate the test report summary.
     */
    private void report() {
        assert count != 0
        
        log.info('')
        log.info('-' * 79)

        log.info(colorBuffer()
            .append('Test Summary (')
            .bold("$suiteWatch")
            .append(')')
            .toString())

        def passed = count - failures.size()
        def failed = failures.size()
        
        def buff
        
        buff = colorBuffer()
        buff.append('    Passed: ')
        if (passed == 0) {
            buff.red("${passed}")
        }
        else {
            buff.green("${passed}")
        }
        log.info(buff.toString())

        buff = colorBuffer()
        buff.append('    Failed: ')
        if (failed == 0) {
            buff.green("${failed}")
        }
        else {
            buff.red("${failed}")
        }
        log.info(buff.toString())
        
        log.info('-' * 79)
        log.info('')
        
        if (failures.size() == 0) {
            return
        }
        
        log.info(colorBuffer()
            .append('The following tests ')
            .red('failed')
            .append(':')
            .toString())

        failures.each { build ->
            log.info(colorBuffer()
                .append('    * ')
                .red(build.name)
                .append(' - ')
                .bold(build.logFile.path)
                .toString())
        }
        
        log.info('')
        
        def msg = "$failed of $count tests failed"
        
        if (!ignoreFailures) {
            fail(msg)
        }
        
        log.warn("${msg}; ignoring")
    }
}

/**
* Runs a {@link TestBuild}.
*/
class TestBuildRunner
    implements Runnable
{
    private final TestMojo mojo
    
    private final Log log
    
    private final TestBuild build
    
    TestBuildRunner(final TestMojo mojo, final TestBuild build) {
        assert mojo
        assert build
        
        this.mojo = mojo
        this.log = mojo.log
        this.build = build
    }
    
    // Renders the prefix
    def prefix = {
        def buff = mojo.colorBuffer()
        
        String name = build.name
        
        // Make the size of the name uniform for easier on the eyes display
        if (name.size() < mojo.maxTestBuildNameLength) {
            name = name.padRight(mojo.maxTestBuildNameLength, ' ')
        }
        
        buff.append('').bold(name).append(' ')
        
        return buff
    }
    
    // Logs the build label
    def buildLabel = {
        def buff = prefix().cyan('RUNNING')
        
        // Include the pom if verbose
        if (mojo.verbose) {
            buff.append(' (').append(build.pomFile.path).append(')')
        }
        
        synchronized (log) {
            log.info(buff as String)
        }
    }
    
    // Logs the build result label
    def resultLabel = {
        def buff = prefix()
        
        if (build.failed) {
            buff.red('FAILURE')
        }
        else {
            buff.green('SUCCESS')
        }
        
        buff.append(' (').append("$build.watch").append(') ')
        
        if (build.failed) {
            buff.red(build.failure.message)
            
            // Include the failure type and the log path when verbose
            if (mojo.verbose) {
                buff.append(' (').append(build.failure.getClass().name).append(')')
                
                buff.append(' - ').append(build.logFile.path)
            }
        }
        
        synchronized (log) {
            log.info(buff as String)
        }
    }
    
    // Logs the log dump label
    def logLabel = {
        def buff = prefix().append('Log: ')
        
        buff.bold(build.logFile as String)
        buff.append(' (').append("${build.logFile.length()}").append(' bytes').append(')')
        
        synchronized (log) {
            log.info(buff as String)
        }
    }
    
    // Renders a marker
    def marker = { String c ->
        def buff = mojo.colorBuffer()
        
        def width = Terminal.terminal.terminalWidth
        
        if (width < 1) {
            width = 80
        }
        
        buff.cyan(c * (width - 1))
        
        return buff as String
    }
    
    // Prints the build log
    def dumpLog = { PrintStream out ->
        assert out
        
        synchronized (log) {
            synchronized (out) {
                def buff = prefix().append('....... Build Log: ')
                
                buff.bold(build.logFile.path)
                
                buff.append(' (').append("${build.logFile.length()}").append(' bytes').append(')')
                
                log.info(buff as String)
                
                // out.println()
                out.println marker('>')
                
                try {
                    build.logFile.eachLine { line ->
                        // Make bits to STDERR red
                        if (line.startsWith('ERR')) {
                            line = mojo.colorBuffer().red(line).toString()
                        }
                        
                        out.println line
                    }
                }
                finally {
                    out.println marker('<')
                }
                
                // out.println()
                out.flush()
            }
        }
    }
    
    void run() {
        log.debug("Running: $build")
        
        try {
            doRun()
            
            log.debug("Finished: $build")
        }
        catch (Throwable t) {
            log.error("Unexpected failure for build: ${build}, cause: $t", t)
        }
    }
    
    private void doRun() {
        buildLabel()

        build.run()
        
        resultLabel()

        if (build.failed) {
            // Remember which builds have failed
            synchronized (mojo.failures) {
                mojo.failures << build
            }
            
            synchronized (log) {
                // Spit out failed logs to console if asked
                if (mojo.verbose) {
                    dumpLog(System.out)
                }
                
                // Spit out more details if debug is enabled
                if (log.debugEnabled) {
                    def cause = build.failure
                    def msg = "Test build failed: $cause (${cause.getClass().name})"
                    
                    // Um, don't dump the stack of the stupid Ant exit crapo
                    if (cause instanceof org.apache.tools.ant.ExitStatusException) {
                        log.debug(msg)
                    }
                    else {
                        log.debug(msg, cause)
                    }
                }
            }
        }
    }
}
