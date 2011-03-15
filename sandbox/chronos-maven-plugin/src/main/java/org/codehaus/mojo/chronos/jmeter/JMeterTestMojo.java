/*
 * Copyright (C) 2008 Digital Sundhed (SDSD)
 *
 * All source code and information supplied as part of chronos
 * is copyright to its contributers.
 *
 * The source code has been released under a dual license - meaning you can
 * use either licensed version of the library with your code.
 *
 * It is released under the Common Public License 1.0, a copy of which can
 * be found at the link below.
 * http://www.opensource.org/licenses/cpl.php
 *
 * It is released under the LGPL (GNU Lesser General Public License), either
 * version 2.1 of the License, or (at your option) any later version. A copy
 * of which can be found at the link below.
 * http://www.gnu.org/copyleft/lesser.html
 */
package org.codehaus.mojo.chronos.jmeter;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.chronos.Utils;
import org.codehaus.mojo.chronos.gc.GCLogParser;
import org.codehaus.mojo.chronos.gc.GCSamples;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.xml.sax.SAXException;

/**
 * Invokes JMeter.<br />
 * JMeter is invoked by spawning a separate process to make it possible to control startup parameters. Can also be used
 * by specifying a .jtl file as input and (possibly) a garbage collection logfile.
 * 
 * @author ksr@lakeside.dk
 * @goal jmeter
 * @phase integration-test
 */
public class JMeterTestMojo extends JMeterMojo {
    /**
     * The current maven project.
     * 
     * @parameter expression="${project}"
     */
    protected MavenProject project;

    /**
     * The inputfile. This could either be a .jtl file or a .jmx file. In the latter case, jmeter is invoked, and the
     * generated .jtl file parsed afterwards.
     * 
     * @parameter
     * @required
     */
    private File input;

    /**
     * The id of the jmeter invocation.
     * 
     * @parameter default-value="performancetest"
     */
    private String dataid;

    /**
     * Will garbage collections be logged? Note that this is really only relevant if your tests are junitsamples in
     * jmeter.
     * 
     * @parameter default-value=true
     */
    private boolean loggc;

    /**
     * The name of an (optional) garbage collection logfile. Only used when loggc is set to true.
     * 
     * @parameter
     */
    private File gclogfile;

    /**
     * Clasname of an (optional) bootstrapperclass. The purpose is to allow bootstrapping the proces eg. by initializing
     * testdata in a relational database without measuring the time.
     * 
     * @parameter
     */
    private String bootstrapper;

    public void execute() throws MojoExecutionException {
        ensureJMeter();
        
        if(!input.exists()) {
            throw new MojoExecutionException("Invalid argument 'input', file " + input.getPath() + " does notexist.");
        }

        if(getJtlFile().exists() && input.lastModified() > getJtlFile().lastModified()) {
            getLog().info("clearing old testlog");
            getJtlFile().delete();
        }
        if(!getJtlFile().exists()) {
            if(bootstrapper != null) {
                getLog().info("Launching bootstrapper " + bootstrapper);
                JavaCommand bootstrapCmd = new JavaCommand(project.getBasedir().getAbsolutePath(), getLog());
                bootstrapCmd.addArgument("-cp");
                StringBuffer classPath = new StringBuffer();
                Iterator it = getDependencyUtil().getDependencies(project).iterator();
                while (it.hasNext()) {
                    Artifact artifact = (Artifact)it.next();
                    classPath.append(artifact.getFile());
                    if(it.hasNext()) {
                        classPath.append(File.pathSeparatorChar);
                    }
                }
                bootstrapCmd.addArgument(classPath.toString());
                bootstrapCmd.addArgument(bootstrapper);

                try {
                    int result = bootstrapCmd.execute();
                    if(result != 0) {
                        throw new MojoExecutionException("Result of " + bootstrapCmd + " execution is: '" + result
                                + "'.");
                    }
                } catch (CommandLineException e) {
                    throw new MojoExecutionException("Could not create bootstrapper", e);
                }
            } else {
                getLog().info("No bootstrapper class found");
            }
            JavaCommand java = getJavaLauncher();
            java.addArgument("-jar");
            String jmeterJar = getJmeterJar().getAbsolutePath();
            java.addArgument(jmeterJar);
            // non-gui
            java.addArgument("-n");
            // testplan inside this file
            java.addArgument("-t");
            java.addArgument(input.getAbsolutePath());
            // output jtl
            java.addArgument("-l");
            java.addArgument(getJtlFile().getAbsolutePath());

            getLog().info("Excuting test " + input.getPath());

            executeJmeter(java);
        } else {
            getLog().info("jtl file " + getJtlFile().getAbsolutePath() + " up-to-date, skipping...");
        }
        if(loggc && getGcLogFile().exists()) {
            parseGCLog();
        }
        parseJmeterLog();
    }

    public void setInput(File input) {
        this.input = input;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }

    public void setLoggc(boolean loggc) {
        this.loggc = loggc;
    }

    public void setGclogfile(File gclogfile) {
        this.gclogfile = gclogfile;
    }

    private void parseGCLog() throws MojoExecutionException {
        try {
            GCSamples samples = new GCLogParser().parseGCLog(getGcLogFile());
            Utils.writeObject(samples, Utils.getGcSamplesSer(getProject().getBasedir(), dataid));
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to parse garbage collection log", e);
        }
    }

    private void parseJmeterLog() throws MojoExecutionException {
        File perfSamplesSer = Utils.getPerformanceSamplesSer(getProject().getBasedir(), dataid);
        try {
            ResponsetimeSamples samples = new JMeterLogParser().parseJMeterLog(getJtlFile());
            Utils.writeObject(samples, perfSamplesSer);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not parse jmeter log", e);
        } catch (SAXException e) {
            throw new MojoExecutionException("Could not parse jmeter log", e);
        }
    }

    private File getJtlFile() {
        if(input.getName().endsWith(".jtl")) {
            return input;
        }
        File chronosDir = Utils.getChronosDir(getProject().getBasedir());
        return new File(chronosDir, "jmeterlog-" + dataid + ".jtl");
    }

    protected final File getGcLogFile() {
        if(!loggc) {
            return null;
        }
        if(gclogfile != null) {
            return gclogfile;
        }
        File chronosDir = Utils.getChronosDir(getProject().getBasedir());
        return new File(chronosDir, "gclog-" + dataid + ".txt");
    }

    protected final MavenProject getProject() {
        return project;
    }
}
