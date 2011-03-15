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

import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.codehaus.mojo.webtest.components.ReportCollector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;

/**
 * Checks the webtest result file for test failures and throws
 * an exception if one or more tests failed. This is useful when
 * 'haltonfailure' and 'haltonerror' are set to 'false' thereby
 * indicating a successful build even in the case that nothing
 * worked. Invoking 'webtest:verify' allows to fail the build
 * later on behalf of the user.
 *
 * @goal verify-result
 * @phase verify
 */
public class WebtestVerifyResultMojo
    extends AbstractWebtestMojo
{
    /**
     * Parses the result files and throws an exception if one or more
     * tests have failed.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException the execution failed
     */
    public void execute() throws MojoExecutionException
    {
        int nrOfFailures = 0;
        int nrOfTests = 0;

        ReportCollector walker = new ReportCollector( "WebTestReport.xml" );
        File[] currResultFileList = walker.run( this.getResultpath() );

        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            for ( int i = 0; i < currResultFileList.length; i++ )
            {
                File currResultFile = currResultFileList[i];
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse( currResultFile );
                NodeList testResultList = document.getElementsByTagName( "testresult" );
                nrOfTests += testResultList.getLength();

                for ( int j = 0; j < testResultList.getLength(); j++ )
                {
                    Node testResultNode = testResultList.item( j );
                    String testResult = testResultNode.getAttributes().getNamedItem( "successful" ).getNodeValue();
                    if ( !testResult.equalsIgnoreCase( "yes" ) && ( !testResult.equalsIgnoreCase( "true" ) ) )
                    {
                        nrOfFailures++;
                    }
                }
            }

            if ( nrOfFailures > 0 )
            {
                String msg = "There were test failures : " + nrOfFailures + "/" + nrOfTests;
                throw new MojoExecutionException( msg );
            }
            else
            {
                this.getLog().info( "There were no test failures : " + nrOfFailures + "/" + nrOfTests );
            }
        }
        catch ( MojoExecutionException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            String msg = "Executing webtest:verify failed";
            throw new MojoExecutionException( msg, e );
        }
    }
}
