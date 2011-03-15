package org.codehaus.mojo.smc;

/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Generate graphes and HTML tables for the various smc state diagrams.
 *
 * @author <a href="jerome@coffeebreaks.org">Jerome Lacoste</a>
 * @goal smcreports
 * @execute phase="generate-sources"
 */
public class SmcReportMojo
    extends AbstractMavenReport
{
    /**
     * <i>Maven Internal</i>: List of artifacts for the plugin.
     *
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @readonly
     */
    protected List pluginClasspathList;

    /**
     * Source directory containing the *.sm files.
     *
     * @parameter expression="${project.basedir}/src/main/smc"
     * @required
     */
    private File sourceDirectory;

    /**
     * The output directory for the report.
     *
     * @parameter expression="${project.reporting.outputDirectory}/smc"
     * @required
     */
    private File outputDirectory;

    /**
     * <i>Maven Internal</i>: The Doxia Site Renderer.
     *
     * @component
     */
    private SiteRenderer siteRenderer;

    /**
     * <i>Maven Internal</i>: Project to interact with.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Graph verbosity level
     * {@link <a href="http://smc.sourceforge.net/SmcManSec10.htm#GraphLevels"/>}
     *
     * @parameter expression="${glevel}"
     */
    private Integer glevel;

    /**
     * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
     */
    public String getName( Locale locale )
    {
        return "State Machine Compiler";
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
     */
    public String getDescription( Locale locale )
    {
        return "State Machine Compiler Diagrams.";
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
     */
    protected String getOutputDirectory()
    {
        return outputDirectory.getAbsolutePath();
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
     */
    protected MavenProject getProject()
    {
        return project;
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
     */
    protected SiteRenderer getSiteRenderer()
    {
        return siteRenderer;
    }

    /**
     * Enable verbose.
     *
     * @parameter expression="${verbose}" default-value="false"
     */
    private boolean verbose;

    /**
     * @see org.apache.maven.reporting.MavenReport#generate(org.codehaus.doxia.sink.Sink, java.util.Locale)
     */
    public void generate( Sink sink, Locale locale )
        throws MavenReportException
    {
        executeReport( locale );
    }

    protected void executeReport(Locale locale) throws MavenReportException {
        if (!sourceDirectory.exists())
        {
            getLog().error("sourceDirectory " + sourceDirectory + " doesn't exist.");
            return;
        }
        if (!sourceDirectory.isDirectory())
        {
            throw new MavenReportException("sourceDirectory " + sourceDirectory + " isn't a directory");
        }
        List files;
        try
        {
            files = Util.getSmFiles(sourceDirectory);
        }
        catch (IOException e)
        {
            throw new MavenReportException("Couldn't find the .sm files in " + sourceDirectory, e);
        }

        // FIXME catch errors (redirect System.err?)
        getLog().debug("Found " + files.size() + " .sm file(s) to process.");
        for (int i = 0; i < files.size(); i++) {
            File smFile = (File) files.get(i);
            getLog().debug("Handling " + smFile);
            /*
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
            */

            if (! outputDirectory.exists() && ! outputDirectory.mkdirs()) {
                getLog().warn("the destination directory (" + outputDirectory
                        + ") for file " + smFile.getAbsolutePath()
                        + "  doesn't exist and couldn't be created. The goal with probably fail.");
            }

            /*
            if (theClass != null) {
                File file = new File(theOutputDirectory, theClass + "Context.java");
                if (file.exists() && file.lastModified() > smFile.lastModified()) {
                    getLog().debug("Skipping regeneration of " + file + " as it is newer than " + smFile);
                    continue;
                }
            } else {
                getLog().warn("Didn't identify the %class from the .sm file");
            }
            */

            // HTML page
            List arguments = new ArrayList();
            if (verbose) {
                arguments.add("-verbose");
            }
            arguments.add("-ret");
            arguments.add("-d");
            arguments.add(outputDirectory.getAbsolutePath());
            arguments.add("-table");
            // Scm uses too many class members... We have to override the _suffix variable
            // otherwise it is not taken into acocunt and the .dot file is written over the html one.
            arguments.add("-su");
            arguments.add("html");
            arguments.add(smFile.getAbsolutePath());
            executeSmc(arguments);

            // HTML graph
            List arguments2 = new ArrayList();
            if (verbose) {
                arguments2.add("-verbose");
            }
            arguments2.add("-ret");
            arguments2.add("-d");
            arguments2.add(outputDirectory.getAbsolutePath());
            arguments2.add("-graph");
            if ( glevel != null )
            {
                // let's smc validate...
                arguments2.add("-glevel");
                arguments2.add(glevel.toString());
            }
            // Scm uses too many class members... We have to override the _suffix variable
            // otherwise it is not taken into acocunt and the .dot file is written over the html one.
            arguments2.add("-su");
            arguments2.add("dot");
            arguments2.add(smFile.getAbsolutePath());
            executeSmc(arguments2);
        }

        // try converting the .dot files into png
        try
        {
            List dotFiles = FileUtils.getFiles( outputDirectory, "**/*.dot", null );
            DotConvertor.convert( dotFiles, new String[] { "png" }, getLog());
        }
        catch ( CommandLineException e )
        {
            getLog().warn( "Couldn't convert the .dot files: failed finding them.", e );
        }
        catch ( IOException e )
        {
            getLog().warn( "Couldn't convert the .dot files: failed finding them.", e );
        }

        // trick the system
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("/refresh.html");
        if ( stream == null ) {
            getLog().error( "Failure to generate refresh.html: template not found.");
        }
        else {
            try {
                Util.copyStreamToFile( stream, new File( outputDirectory, "refresh.html").getAbsolutePath() );
            } catch (IOException e) {
                getLog().error( "Failure to generate refresh.html. ", e );
            }
        }

        getLog().debug("Adding outputDirectory to source root: " + outputDirectory);
        this.project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
    }

    private void executeSmc(List arguments) throws MavenReportException {
        try {
            Util.executeSmc(arguments, getLog());
        } catch (Exception e) {
            throw new MavenReportException("Failure to execute Smc", e);
        }
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getOutputName()
     */
    public String getOutputName()
    {
        return "smc/refresh";
    }

    public boolean isExternalReport()
    {
        return true;
    }

    public boolean canGenerateReport()
    {
        try
        {
            return sourceDirectory != null && sourceDirectory.exists() && Util.getSmFiles(sourceDirectory).size() > 0;
        }
        catch (IOException e)
        {
            getLog().error("error while searching for .sm files ", e);
            return false;
        }
    }
}
