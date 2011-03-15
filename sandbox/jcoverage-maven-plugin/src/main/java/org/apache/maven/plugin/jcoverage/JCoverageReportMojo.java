package org.apache.maven.plugin.jcoverage;

/* ====================================================================
 *   Copyright 2001-2004 The Apache Software Foundation.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * ====================================================================
 */

import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;
import org.apache.maven.plugin.jcoverage.report.CoverageReportGenerator;

import com.jcoverage.coverage.Instrumentation;
import com.jcoverage.coverage.reporting.xml.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * @goal report
 *
 * @description Goal for create a jcoverage report.
 *
 * @prereq jcoverage:instrument
 * @prereq surefire:test
 *
 * @parameter
 *  name="srcDirectory"
 *  type="String"
 *  required="true"
 *  validator=""
 *  expression="#project.build.sourceDirectory"
 *  description=""
 * @parameter
 *  name="destDirectory"
 *  type="String"
 *  required="true"
 *  validator=""
 *  expression="#project.build.directory/jcoverage"
 *  description=""
 * @parameter
 *  name="outputDirectory"
 *  type="String"
 *  required="true"
 *  validator=""
 *  expression="#project.build.directory/docs/jcoverage"
 *  description=""
 *
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class JCoverageReportMojo
    extends AbstractPlugin
{
    public void execute( PluginExecutionRequest request, PluginExecutionResponse response )
        throws Exception
    {
        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String srcDirectory = (String) request.getParameter( "srcDirectory" );

        String destDirectory = (String) request.getParameter( "destDirectory" );

        String outputDirectory = (String) request.getParameter( "outputDirectory" );

        String styleSheet = "style.css";

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        File destDir = new File( destDirectory );

        destDir.mkdirs();

        File outputDir = new File( outputDirectory );

        outputDir.mkdirs();

        String serializedInstrumentationFile = Instrumentation.FILE_NAME;

        ArrayList argsList = new ArrayList();

        argsList.add( "-i" );

        argsList.add( serializedInstrumentationFile );

        argsList.add( "-s" );

        argsList.add( srcDirectory );

        argsList.add( "-o" );

        argsList.add( destDirectory );

        Main.main( (String[])argsList.toArray( new String [0] ) );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        copyStyle( styleSheet, outputDirectory );

        CoverageReportGenerator generator = new CoverageReportGenerator();

        generator.setDataFile( destDirectory + "/coverage.xml" );

        generator.setOutputDir(outputDirectory);

        generator.execute();
    }


    private void copy( InputStream input, OutputStream output )
        throws Exception
    {
        byte[] buffer = new byte[1024];

        int n;

        while ( -1 != ( n = input.read( buffer ) ) )
        {
            output.write( buffer, 0, n );
        }

        shutdownStream( input );

        shutdownStream( output );
    }

    protected void shutdownStream( InputStream input )
    {
        if ( input != null )
        {
            try
            {
                input.close();
            }
            catch ( Exception e )
            {
            }
        }
    }

    protected void shutdownStream( OutputStream output )
    {
        if ( output != null )
        {
            try
            {
                output.close();
            }
            catch ( Exception e )
            {
            }
        }
    }

    private void copyStyle( String styleSheet, String outputDirectory )
        throws Exception
    {
        File styles = new File( outputDirectory );

        styles.mkdirs();

        File f = new File( outputDirectory, styleSheet );

        FileOutputStream w = new FileOutputStream( f );

        InputStream is = getStream( styleSheet );

        copy( is, w );
    }

    private InputStream getStream( String name )
        throws Exception
    {
        return this.getClass().getClassLoader().getResourceAsStream( name );
    }
}