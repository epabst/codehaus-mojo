
package org.codehaus.mojo.codenarc
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

import org.apache.maven.reporting.AbstractMavenReport
import org.apache.maven.project.MavenProject
import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.apache.maven.doxia.tools.SiteTool
import org.codehaus.plexus.resource.ResourceManager
import org.codehaus.plexus.resource.loader.FileResourceLoader

/**
 * Create a CodeNarc Report.
 *
 * @goal codenarc
 * @execute phase="site"
 * @requiresDependencyResolution compile
 * @requiresProject
 * 
 * @author <a href="mailto:gleclaire@codehaus.org">Garvin LeClaire</a>
 * @version $Id: CodeNarcMojo.groovy gleclaire $
 */
class CodeNarcMojo extends AbstractMavenReport {
    /**
     * The name of the Plug-In.
     *
     */
    static final String PLUGIN_NAME = "codenarc"

    /**
     * The name of the property resource bundle (Filesystem).
     *
     */
    static final String BUNDLE_NAME = "codenarc"

    /**
     * The key to get the name of the Plug-In from the bundle.
     *
     */
    static final String NAME_KEY = "report.codenarc.name"

    /**
     * The key to get the description of the Plug-In from the bundle.
     *
     */
    static final String DESCRIPTION_KEY = "report.codenarc.description"

    /**
     * The handle for the resource bundle.
     *
     */
    ResourceBundle bundle

    /**
     * Maven Project
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    MavenProject project
  
    /**
     * Location where generated html will be created.
     *
     * @parameter default-value="${project.reporting.outputDirectory}"
     * @required
     */

    File outputDirectory

    /**
     * Specifies the directory where the xml output will be generated.
     *
     * @parameter default-value="${project.build.directory}"
     * @required
     */
    File xmlOutputDirectory

    /**
     * Doxia Site Renderer.
     *
     * @component
     * @required
     * @readonly
     */
    SiteRenderer siteRenderer

    /**
     * SiteTool.
     *
     * @component role="org.apache.maven.doxia.tools.SiteTool"
     * @required
     * @readonly
     */
    protected SiteTool siteTool

    /**
     * @component
     * @required
     * @readonly
     */
    ResourceManager resourceManager

    /**
     * Skip entire check.
     *
     * @parameter expression="${codenarc.skip}" default-value="false"
     */
    boolean skip


    /**
     * The CodeNarc rulesets to use. See the <a href="http://codenarc.sourceforge.net/codenarc-rules-basic.html">Basic Rules</a>
     *  for a list of some included. Defaults to the "rulesets/basic.xml,rulesets/exceptions.xml,rulesets/imports.xml"
     *
     * @parameter
     */
    String rulesetfiles = new String("rulesets/basic.xml,rulesets/exceptions.xml,rulesets/imports.xml")



    /**
     * Specifies the location of the source directory to be used for Checkstyle.
     *
     * @parameter default-value="${project.build.sourceDirectory}"
     * @required
     */
    File sourceDirectory

    /**
     * Specifies the location of the test source directory to be used for
     * Checkstyle.
     *
     * @parameter default-value="${project.build.testSourceDirectory}"
     *
     */
    File testSourceDirectory


    /**
     * Executes the generation of the report.
     *
     * Callback from Maven Site Plugin or from AbstractMavenReport.execute() => generate().
     *
     * @param locale
     *            the locale the report should be generated for
     * @see org.apache.maven.reporting.AbstractMavenReport #executeReport(java.util.Locale)
     *
     */
    protected void executeReport( Locale locale ) {
        resourceManager.addSearchPath( FileResourceLoader.ID, project.getFile().getParentFile().getAbsolutePath() )
        resourceManager.addSearchPath( "url", "" )

        resourceManager.setOutputDirectory( new File( project.getBuild().getDirectory() ) )

        log.debug("resourceManager outputDirectory is " + resourceManager.outputDirectory )

        def xmlReportFileName = "${xmlOutputDirectory}/CodeNarc.xml"
        File outputFile = new File(xmlReportFileName)


        def ant = new AntBuilder()

        ant.taskdef(name:"codenarc", classname:"org.codenarc.ant.CodeNarcTask")
        ant.target(name:"runCodeNarc") {

            log.info("Rule Set Files is ${rulesetfiles}")
            codenarc( ruleSetFiles: rulesetfiles, maxPriority1Violations:"0") {

                report(type:"xml") {
                    option(name:"outputFile", value:"${xmlReportFileName}")
                    option(name:"title", value:"My Sample Code")

                }

                log.info("sourceDirectory is ${sourceDirectory}")

                fileset(dir:"${sourceDirectory}") {
                    include(name:"**/*.groovy")
                }

                if (testSourceDirectory && testSourceDirectory.exists()) {
                    log.info("testSourceDirectory is ${testSourceDirectory}")

                    fileset(dir:"${testSourceDirectory}") {
                        include(name:"**/*.groovy")
                    }
                }
            }
        }

        if (!outputDirectory.exists()) {
            if ( !outputDirectory.mkdirs() ) {
                fail("Cannot create html output directory")
            }
        }

        if (outputFile.exists()) {
            log.info("Generating CodeNarc HTML")

            CodeNarcReportGenerator generator = new CodeNarcReportGenerator( getSink(), getBundle(locale), this.project.getBasedir(), siteTool)

            generator.setLog(log)

            generator.setCodeNarcResults(new XmlSlurper().parse(outputFile))

            generator.setOutputDirectory(new File(outputDirectory.getAbsolutePath()))

            generator.generateReport()

        }

    }

    /**
     * Checks whether prerequisites for generating this report are given.
     *
     * @return true if report can be generated, otherwise false
     * @see org.apache.maven.reporting.MavenReport#canGenerateReport()
     */
    boolean canGenerateReport() {

        def canGenerate = false

        log.info("sourceDirectory is ${sourceDirectory}" )

        if ( !skip  && sourceDirectory.exists())
        {
            canGenerate = true
        }

        return canGenerate
    }

    /**
     * Returns the plugins description for the "generated reports" overview page.
     *
     * @param locale
     *            the locale the report should be generated for
     *
     * @return description of the report
     * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
     */
    String getDescription( Locale locale ) {
        return getBundle(locale).getString(DESCRIPTION_KEY)
    }

    /**
     * Returns the plugins name for the "generated reports" overview page and the menu.
     *
     * @param locale
     *            the locale the report should be generated for
     *
     * @return name of the report
     * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
     */
    String getName( Locale locale ) {
        return getBundle(locale).getString(NAME_KEY)
    }

    /**
     * Returns report output file name, without the extension.
     *
     * Called by AbstractMavenReport.execute() for creating the sink.
     *
     * @return name of the generated page
     * @see org.apache.maven.reporting.MavenReport#getOutputName()
     */
    String getOutputName() {
        return PLUGIN_NAME
    }

    protected MavenProject getProject() {
        return this.project
    }

    /**
     * Returns the report output directory.
     *
     * Called by AbstractMavenReport.execute() for creating the sink.
     *
     * @return full path to the directory where the files in the site get copied to
     * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
     */
    protected String getOutputDirectory() {
        return outputDirectory.getAbsolutePath()
    }

    protected SiteRenderer getSiteRenderer() {
        return this.siteRenderer
    }

    /**
     * Get the File reference for a File passed in as a string reference.
     *
     * @param resource
     *            The file for the resource manager to locate
     * @return The File of the resource
     *
     */
    protected File getRuleFile(String resource) {

        assert resource

        String location = resource

        if ( location.indexOf('/') != -1 ) {
            location = location.substring(location.lastIndexOf('/') + 1)
        }

        log.info("location of ruleFile file is " + location)


        File ruleFile = resourceManager.getResourceAsFile(resource, location)

        log.info( "location of ruleFile is " + ruleFile )

        return ruleFile

    }

    ResourceBundle getBundle(locale) {

        this.bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale, CodeNarcMojo.class.getClassLoader())

        log.debug("Mojo Locale is " + this.bundle.getLocale().getLanguage())

        return bundle
    }
}
