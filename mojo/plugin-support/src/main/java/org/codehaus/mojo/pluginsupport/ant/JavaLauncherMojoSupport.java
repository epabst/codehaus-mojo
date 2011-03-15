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

package org.codehaus.mojo.pluginsupport.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.codehaus.plexus.util.FileUtils;

import org.codehaus.mojo.pluginsupport.util.ObjectHolder;

//
// FIXME: Need to find a better way to allow plugins to re-use the parameter configuration!
//

/**
 * Support for mojos that launch Java processes.
 *
 * @version $Id$
 */
public abstract class JavaLauncherMojoSupport
    extends AntMojoSupport
{
    //
    // TODO: Use AntHelper component and extend from MojoSupport
    //
    
    private Timer timer = new Timer(true);

    /**
     * Set the maximum memory for the forked JVM.
     *
     * @parameter expression="${maximumMemory}"
     */
    private String maximumMemory = null;
    
    /**
     * The base working directory where process will be started from, a sub-directory
     * the process name will be used for the effective working directory.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected File baseWorkingDirectory = null;

    /**
     * Enable logging mode.
     *
     * @parameter expression="${logOutput}" default-value="false"
     */
    protected boolean logOutput = false;

    /**
     * Flag to control if we background the process or block Maven execution.
     *
     * @parameter default-value="false"
     * @required
     */
    protected boolean background = false;

    /**
     * Timeout for the process in seconds.
     *
     * @parameter expression="${timeout}" default-value="-1"
     */
    protected int timeout = -1;

    /**
     * Time in seconds to wait while verifing that the process has started (if there is custom validation).
     *
     * @parameter expression="${verifyTimeout}" default-value="-1"
     */
    private int verifyTimeout = -1;

    /**
     * An array of option sets which can be enabled by setting <tt>options</tt>.
     *
     * @parameter
     */
    protected OptionSet[] optionSets = null;

    /**
     * A comma seperated list of <tt>optionSets</tt> to enabled.
     *
     * @parameter expression="${options}"
     */
    protected String options = null;

    /**
     * Map of of plugin artifacts.
     *
     * @parameter expression="${plugin.artifactMap}"
     * @required
     * @readonly
     */
    protected Map pluginArtifactMap = null;

    protected void doExecute() throws Exception {
        log.info("Starting " + getProcessTitle() + "...");

        final Java java = (Java)createTask("java");

        File workingDirectory = getWorkingDirectory();
        FileUtils.forceMkdir(workingDirectory);
        java.setDir(workingDirectory);

        java.setFailonerror(true);
        java.setFork(true);

        if (maximumMemory != null) {
            java.setMaxmemory(maximumMemory);
        }
        
        if (timeout > 0) {
            log.info("Timeout after: " + timeout + " seconds");

            java.setTimeout(new Long(timeout * 1000));
        }

        if (logOutput) {
            File file = getLogFile();
            log.info("Redirecting output to: " + file);
            FileUtils.forceMkdir(file.getParentFile());

            java.setLogError(true);
            java.setOutput(file);
        }

        java.setClassname(getClassName());
        setClassPath(java.createClasspath());

        applyOptionSets(java);

        customizeJava(java);

        // Holds any exception that was thrown during startup
        final ObjectHolder errorHolder = new ObjectHolder();

        // Start the process in a seperate thread
        Thread t = new Thread(getProcessTitle() + " Runner") {
            public void run() {
                try {
                    java.execute();
                }
                catch (Exception e) {
                    errorHolder.set(e);

                    //
                    // NOTE: Don't log here, as when the JVM exists an exception will get thrown by Ant
                    //       but that should be fine.
                    //
                }
            }
        };
        t.start();

        log.debug("Waiting for " + getProcessTitle() + "...");

        // Setup a callback to time out verification
        final ObjectHolder verifyTimedOut = new ObjectHolder();

        TimerTask timeoutTask = new TimerTask() {
            public void run() {
                verifyTimedOut.set(Boolean.TRUE);
            }
        };

        if (verifyTimeout > 0) {
            log.debug("Starting verify timeout task; triggers in: " + verifyTimeout + "s");
            timer.schedule(timeoutTask, verifyTimeout * 1000);
        }
        
        // Verify the process has started
        boolean started = false;
        while (!started) {
            if (verifyTimedOut.isSet()) {
                throw new MojoExecutionException("Unable to verify if the " + getProcessTitle() + " process was started in the given time");
            }

            if (errorHolder.isSet()) {
                throw new MojoExecutionException("Failed to launch " + getProcessTitle(), (Throwable)errorHolder.get());
            }

            try {
                started = verifyProcessStarted();
            }
            catch (Exception e) {
                // ignore
            }

            Thread.sleep(1000);
        }

        log.info(getProcessTitle() + " started");

        if (!background) {
            log.info("Waiting for " + getProcessTitle() + " to shutdown...");

            t.join();
        }
    }

    protected Artifact getPluginArtifact(final String name) throws MojoExecutionException {
        assert name != null;

        Artifact artifact = (Artifact)pluginArtifactMap.get(name);
        if (artifact == null) {
            throw new MojoExecutionException("Unable to locate '" + name + "' in the list of plugin artifacts");
        }

        return artifact;
    }

    protected void appendArtifactFile(final Path classpath, final String name) throws MojoExecutionException {
        assert classpath != null;
        assert name != null;

        appendArtifact(classpath, getPluginArtifact(name));
    }

    protected void appendArtifact(final Path classpath, final Artifact artifact) throws MojoExecutionException {
        assert classpath != null;
        assert artifact != null;

        File file = artifact.getFile();
        if (file == null) {
            throw new MojoExecutionException("Artifact does not have an attached file: " + artifact);
        }
        
        classpath.createPathElement().setLocation(file);
    }

    private void applyOptionSets(final Java java) throws MojoExecutionException {
        assert java != null;

        //
        // TODO: Add optionSet activation
        //

        // Apply option sets
        if (options != null  && (optionSets == null || optionSets.length == 0)) {
            throw new MojoExecutionException("At least one optionSet must be defined to select one using options");
        }
        else if (options == null) {
            options = "default";
        }

        if (optionSets != null && optionSets.length != 0) {
            OptionSet[] sets = selectOptionSets();

            for (int i=0; i < sets.length; i++) {
                if (log.isDebugEnabled()) {
                    log.debug("Selected option set: " + sets[i]);
                }
                else {
                    log.info("Selected option set: " + sets[i].getId());
                }

                String[] options = sets[i].getOptions();
                if (options != null) {
                    for (int j=0; j < options.length; j++) {
                        java.createJvmarg().setValue(options[j]);
                    }
                }

                Properties props = sets[i].getProperties();
                if (props != null) {
                    Iterator iter = props.keySet().iterator();
                    while (iter.hasNext()) {
                        String name = (String)iter.next();
                        String value = props.getProperty(name);

                        setSystemProperty(java, name, value);
                    }
                }
            }
        }
    }

    private OptionSet[] selectOptionSets() throws MojoExecutionException {
        // Make a map of the option sets and validate ids
        Map map = new HashMap();
        for (int i=0; i<optionSets.length; i++) {
            if (log.isDebugEnabled()) {
                log.debug("Checking option set: " + optionSets[i]);
            }

            String id = optionSets[i].getId();

            if (id == null && optionSets.length > 1) {
                throw new MojoExecutionException("Must specify id for optionSet when more than one optionSet is configured");
            }
            else if (id == null && optionSets.length == 1) {
                id = "default";
                optionSets[i].setId(id);
            }

            assert id != null;
            id = id.trim();

            if (map.containsKey(id)) {
                throw new MojoExecutionException("Must specify unique id for optionSet: " + optionSets[i]);
            }
            map.put(id, optionSets[i]);
        }

        StringTokenizer stok = new StringTokenizer(options, ",");

        List selected = new ArrayList();
        while (stok.hasMoreTokens()) {
            String id = stok.nextToken();
            OptionSet set = (OptionSet)map.get(options);

            if (set == null) {
                if ("default".equals(options)) {
                    log.debug("Default optionSet selected, but no optionSet defined with that id; ignoring");
                }
                else {
                    throw new MojoExecutionException("Missing optionSet for id: " + id);
                }
            }
            else {
                selected.add(set);
            }
        }

        return (OptionSet[]) selected.toArray(new OptionSet[selected.size()]);
    }

    //
    // MojoSupport Hooks
    //

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project = null;

    protected MavenProject getProject() {
        return project;
    }

    /**
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    protected ArtifactRepository artifactRepository = null;

    protected ArtifactRepository getArtifactRepository() {
        return artifactRepository;
    }

    //
    // Sub-class API
    //

    protected abstract String getProcessName();

    protected String getProcessTitle() {
        return getProcessName();
    }

    protected File getWorkingDirectory() {
        return new File(baseWorkingDirectory, getProcessName());
    }

    protected File getLogFile() {
        return new File(getWorkingDirectory(), getProcessName() + ".log");
    }

    protected abstract String getClassName();

    protected abstract void setClassPath(Path classpath) throws Exception;

    protected void customizeJava(final Java java) throws MojoExecutionException {
        assert java != null;

        // nothing by default
    }
    
    protected boolean verifyProcessStarted() throws Exception {
        return true;
    }
}
