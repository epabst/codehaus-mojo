package org.codehaus.mojo.smc;

/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License" );
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.IOException;
import java.io.File;

/**
 * Generates the Java sources from the *.sm files.
 * <p/>
 *
 * @author <a href="jerome@coffeebreaks.org">Jerome Lacoste</a>
 * @version $Id$
 * @goal smc
 * @phase generate-sources
 * @requiresProject
 * @requiresDependencyResolution
 * @see <a href="http://www.intellij.org/twiki/bin/view/Main/IntelliJUIDesignerFAQ">ui designer Ant tasks documentation</a>.
 */
public class SmcMojo
        extends AbstractMojo {
    /**
     * Project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Source directory containing the *.sm files.
     *
     * @parameter expression="${project.basedir}/src/main/smc"
     * @required
     */
    private File sourceDirectory;

    /**
     * The directory will be added as Project's Resource.
     *
     * @parameter expression="${outputDirectory}" default-value="${project.build.directory}/generated-sources/smc/smc"
     * @required
     */
    private File outputDirectory;

    /**
     * Fork the compilation task.
     *
     * @parameter expression="${fork}" default-value="false"
     * @required
     */
    private boolean fork;

    /**
     * Fail on error ?
     *
     * @parameter expression="${failOnError}" default-value="true"
     * @required
     */
    private boolean failOnError;

    /**
     * Enable debug.
     *
     * @parameter expression="${debug}" default-value="false"
     */
    private boolean debug;

    /**
     * Enable verbose.
     *
     * @parameter expression="${verbose}" default-value="false"
     */
    private boolean verbose;

    /**
     * Enable sync.
     *
     * @parameter expression="${sync}" default-value="false"
     */
    private boolean sync;

    /**
     * Enable reflect.
     *
     * @parameter expression="${reflect}" default-value="false"
     * @required
     */
    private boolean reflect;

    public void execute()
            throws MojoExecutionException {

        if (!sourceDirectory.exists()) {
            getLog().error("sourceDirectory " + sourceDirectory + " doesn't exist.");
            return;
        }
        if (!sourceDirectory.isDirectory()) {
            throw new MojoExecutionException("sourceDirectory " + sourceDirectory + " isn't a directory");
        }
        List files;
        try {
            files = Util.getSmFiles(sourceDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException("Couldn't find the .sm files in " + sourceDirectory, e);
        }

        getLog().debug("Found " + files.size() + " .sm file(s) to process.");
        for (int i = 0; i < files.size(); i++) {
            File smFile = (File) files.get(i);
            getLog().debug("Handling " + smFile);
            Properties properties;
            String classPackage = null;
            String theClass = null;
            try {
                properties = Util.getSmFileHeader(smFile);
                classPackage = properties.getProperty("package");
                theClass = properties.getProperty("class");
            } catch (IOException e) {
                getLog().error("Couldn't identify the package for file " + smFile.getAbsolutePath(), e);
            }
            if (classPackage != null) {
                classPackage = classPackage.replace(".", File.separator);
            }
            File theOutputDirectory = new File(outputDirectory + File.separator
                    + classPackage);

            if (! theOutputDirectory.exists() && ! theOutputDirectory.mkdirs()) {
                getLog().warn("the destination directory (" + theOutputDirectory
                        + ") for file " + smFile.getAbsolutePath()
                        + "  doesn't exist and couldn't be created. The goal with probably fail.");
            }

            if (theClass != null) {
                File file = new File(theOutputDirectory, theClass + "Context.java");
                if (file.exists() && file.lastModified() > smFile.lastModified()) {
                    getLog().debug("Skipping regeneration of " + file + " as it is newer than " + smFile);
                    continue;
                }
            } else {
                getLog().warn("Didn't identify the %class from the .sm file");
            }

            List arguments = new ArrayList();
            arguments.add("-ret"); // critical otherwise Smc does a System.exit().
            arguments.add("-d");
            arguments.add(theOutputDirectory.getAbsolutePath());
            if (sync) {
                arguments.add("-sync");
            }
            if (verbose) {
                arguments.add("-verbose");
            }
            if (reflect) {
            	arguments.add("-reflect");
            }
            arguments.add("-java");
            arguments.add(smFile.getAbsolutePath());

            executeSmc(arguments);
        }

        getLog().debug("Adding outputDirectory to source root: " + outputDirectory);
        this.project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
    }

    private void executeSmc(List arguments) throws MojoExecutionException {
        try {
            Util.executeSmc(arguments, getLog());
        } catch (Exception e) {
            throw new MojoExecutionException("Failure to execute Smc", e);
        }
    }

}
