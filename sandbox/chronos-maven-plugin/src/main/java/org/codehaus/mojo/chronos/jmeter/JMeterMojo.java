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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.chronos.download.DownloadHelper;
import org.codehaus.plexus.util.cli.CommandLineException;

/**
 * Invokes JMeter. JMeter is invoked by spawning a separate process to make it possible to control startup parameters.
 * 
 * @author ksr@lakeside.dk
 */
public abstract class JMeterMojo extends AbstractMojo {
    /**
     * The path to the jmeter installation. The recommended way to set this is by specifying a property jmeter.home in
     * the pom.xml or settings.xml.
     * 
     * @parameter expression="${project.basedir}/jmeter"
     */
    private String jmeterhome;

    /**
     * The URL from where jMeter can be downloaded.
     * 
     * @parameter default-value="http://www.eu.apache.org/dist/jakarta/jmeter/binaries/jakarta-jmeter-2.4.zip"
     */
    private String jMeterRemoteLocation;

    /**
     * The heapsize (both initial and max) of the spawned jvm invoking jmeter.
     * 
     * @parameter default-value="256m"
     */
    private String heap = "256m";

    /**
     * Specifies the corresponding jvm option of the spawned jvm invoking jmeter.
     * 
     * @parameter default-value="128m";
     */
    private String newsize = "128m";

    /**
     * Specifies the corresponding jvm option of the spawned jvm invoking jmeter.
     * 
     * @parameter
     */
    private String survivorratio;

    /**
     * Specifies the corresponding jvm option of the spawned jvm invoking jmeter.
     * 
     * @parameter
     */
    private String targetsurvivorratio;

    /**
     * Specifies the corresponding jvm option of the spawned jvm invoking jmeter.
     * 
     * @parameter default-value="2"
     */
    private String maxtenuringthreshold = "2";

    /**
     * Specifies the corresponding jvm option of the spawned jvm invoking jmeter.
     * 
     * @parameter
     */
    private String maxliveobjectevacuationratio;

    /**
     * How often will rmi garbage collections be performed? JVM option of the spawned jvm invoking jmeter.
     * 
     * @parameter default-value = "600000";
     */
    private String rmigcinterval = "600000";

    /**
     * The size of the part of the sawned jmeter jvm's memory, where classes e.g. are stored.
     * 
     * @parameter default-value = "64m";
     */
    private String permsize = "64m";

    /**
     * System-properties to the launched jvm.
     * 
     * @parameter
     */
    private Properties sysproperties = new Properties();

    /**
     * <b>Optional</b> Miscellaneous configuration parameters used when launching JMeter
     * 
     * @parameter
     */
    private List options;

    /**
     * Configuration parameters used for configurating the .
     * 
     * @parameter
     */
    private List gcargs;

    protected final void ensureJMeter() throws MojoExecutionException {
        File jMeterJar = getJmeterJar();
        if(!jMeterJar.exists()) {
            try {
                DownloadHelper.downloadJMeter(jMeterRemoteLocation, jmeterhome);
            } catch (IOException ex) {
                throw new MojoExecutionException("Error during jMeter download", ex);
            }
        }
    }

    protected final void executeJmeter(JavaCommand java) throws MojoExecutionException {
        DependencyUtil deps = getDependencyUtil();
        List copied = null;
        try {
            copied = deps.copyDependencies(getProject());
        } catch (IOException e) {
            throw new MojoExecutionException("Execution failed", e);
        }
        try {
            int result = java.execute();
            if(result != 0) {
                throw new MojoExecutionException("Result of " + java + " execution is: '" + result + "'.");
            }
        } catch (CommandLineException e) {
            throw new MojoExecutionException("Execution failed", e);
        } finally {
            if(copied != null) {
                deps.cleanUpDependencies(copied);
            }
        }
    }

    protected final DependencyUtil getDependencyUtil() {
        return new DependencyUtil(jmeterhome, getLog());
    }

    protected final JavaCommand getJavaLauncher() throws MojoExecutionException {
        if(jmeterhome == null) {
            throw new MojoExecutionException("Missing jmeterhome. You must eithe define a property jmeter.home "
                    + "or set the jmeterhome explicitly in your plugin execution");
        }

        JavaCommand java = new JavaCommand(getProject().getBasedir().getAbsolutePath(), getLog());
        java.addSystemProperty("user.dir", jmeterhome + "/bin");
        // Removed - it is only supported on SUN VM's, and only affects the HotSpot compiler.
        // And since the changes are only required for high throughput server applications.
        // It will be removed for the client launcher.
        // java.addArgument("-server");
        java.addArgument("-Xms" + heap);
        java.addArgument("-Xmx" + heap);
        java.addArgument("-XX:NewSize=" + newsize);
        java.addArgument("-XX:MaxNewSize=" + newsize);
        if(survivorratio != null) {
            java.addArgument("-XX:SurvivorRatio=" + survivorratio);
        }
        if(targetsurvivorratio != null) {
            java.addArgument("-XX:TargetSurvivorRatio=" + targetsurvivorratio);
        }
        java.addArgument("-XX:MaxTenuringThreshold=" + maxtenuringthreshold);
        if(maxliveobjectevacuationratio != null) {
            java.addArgument("-XXMaxLiveObjectEvacuationRatio=" + maxliveobjectevacuationratio);
        }
        java.addArgument("-XX:PermSize=" + permsize);
        java.addArgument("-XX:MaxPermSize=" + permsize);
        File gclog = getGcLogFile();
        if(gclog != null) {
            if(isSunVm()) {
                java.addArgument("-verbose:gc");
                java.addArgument("-Xloggc:" + gclog.getAbsolutePath());
            } else if(!isSunVm()) {
                if(gcargs != null) {
                    for (Iterator iterator = gcargs.iterator(); iterator.hasNext();) {
                        java.addArgument((String)iterator.next());
                    }
                }
            }
        }
        if(!sysproperties.containsKey("sun.rmi.dgc.client.gcInterval")) {
            sysproperties.setProperty("sun.rmi.dgc.client.gcInterval", rmigcinterval);
        }
        if(!sysproperties.containsKey("sun.rmi.dgc.server.gcInterval")) {
            sysproperties.setProperty("sun.rmi.dgc.server.gcInterval", rmigcinterval);
        }
        Enumeration sysPropNames = sysproperties.propertyNames();
        while (sysPropNames.hasMoreElements()) {
            String name = (String)sysPropNames.nextElement();
            String value = sysproperties.getProperty(name);
            java.addSystemProperty(name, value);
        }
        if(options != null) {
            for (Iterator it = options.iterator(); it.hasNext();) {
                String option = (String)it.next();
                java.addArgument(option);
            }
        }
        return java;
    }

    protected final File getJmeterJar() {
        return new File(new File(jmeterhome, "bin"), "ApacheJMeter.jar");
    }

    protected abstract File getGcLogFile();

    protected abstract MavenProject getProject();

    private boolean isSunVm() {
        return System.getProperty("java.vendor").startsWith("Sun ");
    }
}
