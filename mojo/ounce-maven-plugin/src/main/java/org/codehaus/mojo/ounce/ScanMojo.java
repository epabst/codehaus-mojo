/*
 * Copyright (c) 2007, Ounce Labs, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY OUNCE LABS, INC. ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL OUNCE LABS, INC. BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.codehaus.mojo.ounce;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.ounce.core.OunceCore;
import org.codehaus.mojo.ounce.core.OunceCoreException;
import org.codehaus.mojo.ounce.utils.Utils;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.StringUtils;

/**
 * This mojo allows an on demand scan of an application and the optional publishing of the results.
 * 
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @goal scan
 * @aggregator
 * @execute lifecycle="scan" phase="package"
 */
public class ScanMojo
    extends AbstractOunceMojo
{

    /**
     * The location of the application file (.paf) to scan.
     * 
     * @parameter expression="${ounce.applicationFile}" default-value="${basedir}/${project.artifactId}.paf"
     */
    String applicationFile;

    /**
     * A name to help identify the assessment.
     * 
     * @parameter expression="${project.name}-${project.version}"
     */
    String assessmentName;

    /**
     * A filename to which to save the assessment. <br/> If filename is not specified, Ounce/Maven generates a name
     * based on the application name and timestamp and saves it to the application’s working directory.
     * 
     * @parameter expression="${ounce.assessmentOutput}"
     */
    String assessmentOutput;

    /**
     * A short string to help identify the corresponding entries in the ounceauto log file.
     * 
     * @parameter expression="${ounce.caller}"
     */
    String caller;

    /**
     * Generates an Ounce report of the specified type, including findings reports, SmartAudit Reports, and, if
     * available, custom reports. Ounce/Maven generates a report for this assessment after the scan completes. <br/> The
     * following report types are included: Findings, Findings By CWE, Findings By API, Findings By Classification,
     * Findings By File, Findings By Type, Findings By Bundle, OWASP Top Ten, PCI Data Security Standard, Ounce Software
     * Security Profile, or OWASP Top Ten 2007 <br/> If you specify reportType, then reportOutputType and
     * reportOutputPath are required.
     * 
     * @parameter expression="${ounce.reportType}"
     */
    String reportType;

    /**
     * The output to generate for the report specified in reportType. Required with reportType. Output type may be html,
     * zip, pdf-summary, pdf-detailed, pdf-comprehensive, or pdf-annotated.
     * 
     * @parameter expression="${ounce.reportOutputType}"
     */
    String reportOutputType;

    /**
     * The path to which to write the report specified in reportType. Required with reportType.
     * 
     * @parameter expression="${ounce.reportOutputPath}"
     */
    String reportOutputPath;

    /**
     * Number of lines of source code to include in the report before each finding.
     * 
     * @parameter expression="${ounce.includeSrcBefore}"
     */
    int includeSrcBefore = -1;

    /**
     * Number of lines of source code to include in the report after each finding.
     * 
     * @parameter expression="${ounce.includeSrcAfter}"
     */
    int includeSrcAfter = -1;

    /**
     * Automatically publish the assessment following the completion of the scan.
     * 
     * @parameter expression="${ounce.publish}" default-value="false"
     */
    boolean publish;

    /**
     * The location of the Ounce client installation directory if the Ounce client is not on the path.
     * 
     * @parameter expression="${ounce.installDir}"
     */
    String installDir;

    /**
     * Forces the goal to wait until the scan finishes, thus blocking the Maven build. This is useful if the scan is
     * being performed from the report mojo as part of integration with the site target and the site is getting
     * deployed.
     * 
     * @parameter expression="${ounce.wait}" default-value="false"
     */
    boolean waitForScan;

    /**
     * This is a static variable used to persist the cached results across plugin invocations.
     */
    protected static Set cache = new HashSet();

    // private List projects;
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

        if ( StringUtils.isEmpty( applicationFile ) )
        {
            throw new MojoExecutionException( "\'applicationFile\' must be defined." );
        }

        // check my cache to see if this particular set of params has already been scanned.
        if ( shouldExecute() )
        {
            try
            {
                if ( includeSrcAfter != -1 || includeSrcBefore != -1 )
                {
                    if ( options == null )
                    {
                        options = new HashMap();
                    }
                    options.put( "includeSrcAfter", new Integer( includeSrcAfter ) );
                    options.put( "includeSrcBefore", new Integer( includeSrcBefore ) );
                }
                OunceCore core = getCore();
                core.scan( Utils.convertToVariablePath( applicationFile, pathVariableMap ), assessmentName,
                           assessmentOutput, caller, reportType, reportOutputType, reportOutputPath, publish,
                           this.options, this.installDir, waitForScan, getLog() );
            }
            catch ( ComponentLookupException e )
            {
                throw new MojoExecutionException( "Unable to lookup the core interface for hint: " + coreHint, e );
            }
            catch ( OunceCoreException e )
            {
                throw new MojoExecutionException( "Nested Ouncecore exception: " + e.getLocalizedMessage(), e );
            }
        }
        else
        {
            this.getLog().info(
                                "Skipping Scan because these same parameters where already used in a scan for this project during this build. (build was probably forked)" );
        }
    }

    /**
     * This method checks the cache to see if the scan should be run. It is used to avoid multiple invocations of scan
     * in the instance of a forked build.
     * 
     * @return
     */
    protected boolean shouldExecute()
    {
        // get the hash and try to add it.
        // if it was added, then we need to execute, otherwise it was already there
        // and we don't.
        return ( cache.add( getParameterHash() ) );
    }

    /**
     * This method returns a hash of the parameters that may influence scan results. The result of this is used to
     * determine if a scan should be rerun within the same build lifetime. If any parameters change, then the scan will
     * continue. This allows multiple executions to be defined with slightly different parameters.
     * 
     * @return String of hashCodes
     */
    protected String getParameterHash()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( getSafeHash( this.applicationFile ) );
        buf.append( "-" );
        buf.append( getSafeHash( this.assessmentOutput ) );
        buf.append( "-" );
        buf.append( getSafeHash( this.caller ) );
        buf.append( "-" );
        buf.append( getSafeHash( this.pathVariableMap ) );
        buf.append( "-" );
        buf.append( getSafeHash( this.reportOutputPath ) );
        buf.append( "-" );
        buf.append( getSafeHash( this.reportOutputType ) );
        buf.append( "-" );
        buf.append( getSafeHash( this.reportType ) );
        this.getLog().debug( "Parameter Hash: " + buf.toString() );
        return buf.toString();
    }

    /**
     * Simple helper to handle null parameters for hash checking.
     * 
     * @param o
     * @return
     */
    private final int getSafeHash( Object o )
    {
        if ( o != null )
        {
            return o.hashCode();
        }
        else
        {
            return 0;
        }
    }

}
