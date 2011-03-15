/*
 * Copyright 2007 The Apache Software Foundation.
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
package org.codehaus.mojo.webtest;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Abstract base class to capture the relevant parameters.
 */
public abstract class AbstractWebtestMojo extends AbstractMojo
{  
    /**
     * The Maven project object
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    /**
     * The plugin dependencies.
     *
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @readonly
     */
    private List artifacts;

    /**
     * Specifies whether the client should automatically follow page
     * refresh requests.
     *
     * @parameter expression="${webtest.autorefresh}" default-value="true"
     * @required
     */
    private boolean autorefresh;

    /**
     * Defines the constant base path used to construct request URLs,
     * e.g. "shop" can be considered a basepath in "http:/www.myhost.com/shop/productlist"
     * and "http:/www.myhost.com/shop/checkout".
     *
     * @parameter expression="${webtest.basepath}" default-value=""
     */
    private String basepath;

    /**
     * Specifies the name of an ant property which is set to true if the
     * test fails. When using this property, set "haltonfailure" to "false".
     * Then check the ant property after performing all of your tests and
     * if set fail. This allows your tests to continue running even if
     * some fail (allowing you to catch multiple errors with one test run)
     * but still ultimately fails the build if anything went wrong.
     *
     * @parameter expression="${webtest.failureproperty}" default-value=""
     */
    private String failureproperty;
  
    /**
     * Determines if the execution of the current &lt;webtest&lt; stops if a
     * program error (unhandled exception) is encountered in one of
     * the test steps. An ANT BuildFailed exception will be raised
     * if it is set to "Yes". This will terminate the execution of
     * the ANT script. Setting the option to "false" will stop the
     * execution of the current &lt;webtest&gt; but continues with
     * the next ANT task, e.g. another &lt;webtest&gt;.
     *
     * @parameter expression="${webtest.haltonerror}" default-value="false"
     * @required
     */
    private boolean haltonerror;
  
    /**
     * Determines if the execution of the current test halts if a failure of
     * one of the test steps is detected, e.g. a &lt;verify...&gt; test did not
     * pass. Even when set to "false" all of the trailing &lt;step&gt;s of the
     * current &lt;webtest&gt; will be skipped but processing will continue with
     * the next &lt;webtest&gt;.
     *
     * @parameter expression="${webtest.haltonfailure}" default-value="false"
     * @required
     */
    private boolean haltonfailure;

    /**
     * Defines the hostname to use for executing requests, e.g. "localhost" or "www.myhost.com".
     *
     * @parameter expression="${webtest.host}" default-value="localhost"
     * @required
     */
    private String host;

    /**
     * Defines the loglevel passed to the ANT script
     *
     * @parameter expression="${webtest.loglevel}" default-value="WARN"
     * @required
     */
    private String loglevel;

    /**
     * Defines the port number to use for executing requests, e.g. "8080".
     *
     * @parameter expression="${webtest.port}" default-value="80"
     * @required
     */
    private String port;

    /**
     * Defines the protocol to use for executing requests.
     * "http:" and "file:" are currently the only supported protocols.
     *
     * @parameter expression="${webtest.protocol}" default-value="http"
     * @required
     */
    private String protocol;

    /**
     * The subdirectory created under site containing
     * the webtest report.
     *
     * @parameter expression="${webtest.reportdirectory}" default-value="${basedir}/target/site/webtest"
     * @required
     */
    private File reportdirectory;

    /**
     * The name of the created report using XSLT
     *
     * @parameter expression="${webtest.reportname}" default-value="index.html"
     * @required
     */
    private String reportname;

    /**
     * Defines the resultpath of saved responses (for example last response).
     *
     * @parameter expression="${webtest.resultpath}"  default-value="${project.build.directory}/webtest/webtest-results"
     * @required
     */
    private File resultpath;

    /**
     * Defines the class for report generation, e.g "com.canoo.webtest.reporting.PlainTextReporter" or
     * "com.canoo.webtest.reporting.XmlReporter".
     *
     * @parameter expression="${webtest.resultreporterclass}"  default-value="com.canoo.webtest.reporting.XmlReporter"
     */
    private String resultreporterclass;

    /**
     * Sets the name to use if the current response received from the target
     * host is to be saved into a file. A unique id will also be appended
     * after the prefix.
     *
     * @parameter expression="${webtest.saveprefix}" default-value="response"
     * @required
     */
    private String saveprefix;
    
    /**
     * Defines whether the last response received from the target host
     * is saved into the file &lt;saveprefix&gt;&lt;uniqueId&gt;.html.
     * saveprefix is the value set in the &lt;saveprefix&gt; config element.
     * uniqueId is a timestamp at the time the file is saved.
     * This is especially helpful for debugging your tests.
     *
     * @parameter expression="${webtest.saveresponse}" default-value="true"
     * @required
     */
    private boolean saveresponse;

    /**
     * Specifies the location of the source files to be used.
     *
     * @parameter expression="${webtest.sourcedirectory}" default-value="${basedir}/src/test/webtest"
     * @required
     */
    private File sourcedirectory;

    /**
     * The ANT file to be executed as globbing pattern within
     * the 'sourcedirectory'. Depending on you requirements
     * you can choose a different file or multiple files. The
     * default value is "${basedir}/src/test/webtest/webtest.xml"
     *
     * @parameter expression="${webtest.sourcefile}"  default-value="webtest.xml"
     * @required
     */
    private String sourcefile;

   /**
    * The ANT target to call when executing a Canoo WebTest script.
    *
    * @parameter expression="${webtest.target}" default-value="all"
z   */
    private String target;

    /**
     * Defines the timeout value in seconds for both connection and socket.
     * The connection timeout is the maximum allowed time until a connection
     * is etablished. The socket timeout is the timeout for waiting for data.
     * A timeout value of zero is interpreted as an infinite timeout.
     *
     * @parameter expression="${webtest.timeout}" default-value="300"
     * @required
     */
    private int timeout;

    /**
     * String to prepend to project and dependency property names.
     * @parameter expression="${webtest.propertyprefix}" default-value=""
     */
    private String propertyprefix = "";

    /////////////////////////////////////////////////////////////////////////
    // Setters
    /////////////////////////////////////////////////////////////////////////
    
    public MavenProject getProject()
    {
        return project;
    }

    public List getArtifacts()
    {
        return artifacts;
    }

    public boolean isAutorefresh()
    {
        return autorefresh;
    }

    public String getBasepath()
    {
        return ( basepath != null ? basepath : "" );
    }

    public String getFailureproperty()
    {
        return failureproperty;
    }

    public boolean isHaltonerror()
    {
        return haltonerror;
    }

    public boolean isHaltonfailure()
    {
        return haltonfailure;
    }

    public String getHost()
    {
        return host;
    }

    public String getPort()
    {
        return port;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public File getResultpath()
    {
        return resultpath;
    }

    public String getReportname()
    {
        return reportname;
    }

    public String getResultReporterClass()
    {
        return resultreporterclass;
    }

    public boolean isSaveResponse()
    {
        return saveresponse;
    }

    public String getSaveprefix()
    {
        return saveprefix;
    }

    public File getSourcedirectory()
    {
        return sourcedirectory;
    }

    public String getSourcefile()
    {
        return sourcefile;
    }

    public String getTarget()
    {
        return target;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public File getReportdirectory()
    {
        return reportdirectory;
    }

    public String getSourceUrl()
    {
        return this.protocol + "://" + this.host + ":" + this.port + "/" + this.getBasepath();
    }

    public String getLoglevel()
    {
        return loglevel;
    }

    public String getPropertyPrefix()
    {
        return propertyprefix;
    }

    /**
   * @return instance containing all configuration parameters passed to Canoo WebTest
   */
    protected Properties toProperties()
    {
        NullSafeProperties result = new NullSafeProperties();

        // copy the available properties
        Properties mavenProps = project.getProperties();
        Iterator iter = mavenProps.keySet().iterator();
        while ( iter.hasNext() )
        {
            String key = (String)iter.next();
            result.setProperty( propertyprefix + key, mavenProps.getProperty( key ) );
        }

        // add some of the common maven properties adding an optional prefix
        result.setProperty( propertyprefix, "project.groupId", project.getGroupId() );
        result.setProperty( propertyprefix, "project.artifactId", project.getArtifactId() );
        result.setProperty( propertyprefix, "project.name", project.getName() );
        result.setProperty( propertyprefix, "project.description", project.getDescription() );
        result.setProperty( propertyprefix, "project.version", project.getVersion() );
        result.setProperty( propertyprefix, "project.packaging", project.getPackaging() );
        result.setProperty( propertyprefix, "project.build.directory", project.getBuild().getDirectory() );
        result.setProperty( propertyprefix, "project.build.outputDirectory", project.getBuild().getOutputDirectory() );
        result.setProperty( propertyprefix, "project.build.outputTestDirectory", project.getBuild().getTestOutputDirectory() );
        result.setProperty( propertyprefix, "project.build.sourceDirectory", project.getBuild().getSourceDirectory() );
        result.setProperty( propertyprefix, "project.build.testSourceDirectory", project.getBuild().getTestSourceDirectory() );
        result.setProperty( propertyprefix, "project.build.finalName", project.getBuild().getFinalName() );

        // set the webtest specific properties
        result.setProperty( "autorefresh", Boolean.toString( this.isAutorefresh() ) );
        result.setProperty( "basepath", this.getBasepath() );
        result.setProperty( "haltonerror", Boolean.toString( this.isHaltonerror() ) );
        result.setProperty( "haltonfailure", Boolean.toString( this.isHaltonfailure() ) );
        result.setProperty( "host", this.getHost() );
        result.setProperty( "port", this.getPort() );
        result.setProperty( "protocol", this.getProtocol() );
        result.setProperty( "resultpath", this.getResultpath().getAbsolutePath() );
        result.setProperty( "saveresponse", Boolean.toString( this.isSaveResponse() ) );
        result.setProperty( "summary", "true" );
        result.setProperty( "timeout", Integer.toString( this.getTimeout() ) );
        result.setProperty( "webtest.resultreporterclass", getResultReporterClass() );

        // configure the logging of the various webtest components       
        System.setProperty( "logLevel.webtest", this.getLoglevel() );
        System.setProperty( "logLevel.htmlunit", this.getLoglevel() );
        System.setProperty( "logLevel.httpclientWire", this.getLoglevel() );

        return result;
    }

    /**
     * Helper class to avoid NPE in the case of adding a 'null' value
     */
    private class NullSafeProperties extends Properties {

        @Override
        public Object setProperty(String key, String value)
        {
            if(key != null && value != null)
            {
                return super.setProperty(key, value);
            }
            else
            {
                return null;
            }
        }

        public Object setProperty(String prefix, String key, String value)
        {
            String currKey = (prefix != null ? prefix : "") + key.toString();
            return setProperty(currKey, value);
        }
    }

}
