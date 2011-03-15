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

import com.jcoverage.coverage.Instrument;

import java.util.ArrayList;
 
/**
 * @goal instrument
 *
 * @description Goal for instrumenting source code.
 *
 * @prereq compiler:compile
 *
 * @parameter
 *  name="buildDirectory"
 *  type="String"
 *  required="true"
 *  validator=""
 *  expression="#project.build.outputDirectory"
 *  description=""
 * @parameter
 *  name="outputDirectory"
 *  type="String"
 *  required="true"
 *  validator=""
 *  expression="#project.build.directory/jcoverage"
 *  description=""
 * // TODO: need a way to describe it as empty by default
 * @parameter
 *  name="ignorePattern"
 *  type="String"
 *  required="false"
 *  validator=""
 *  expression="#maven.jcoverage.ignorePattern"
 *  description=""
 *
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class JCoverageMojo
    extends AbstractPlugin
{
    public void execute( PluginExecutionRequest request, PluginExecutionResponse response )
        throws Exception
    {
        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String buildDirectory = (String) request.getParameter( "buildDirectory" );

        String outputDirectory = (String) request.getParameter( "outputDirectory" );

        String ignorePattern = (String) request.getParameter( "ignorePattern" );

        ArrayList argsList = new ArrayList();

/* TODO: add back when we can pass the dir to surefire:test
        argsList.add( "-d" );

        argsList.add( outputDirectory );
*/

        if ( ignorePattern != null && !"".equals( ignorePattern ) )
        {
            argsList.add( "-ignore" );

            argsList.add( ignorePattern );
        }

        argsList.add( buildDirectory );
        
        Instrument.main( (String[])argsList.toArray( new String [0] ) );
org.apache.log4j.BasicConfigurator.configure();
org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
    }
}
