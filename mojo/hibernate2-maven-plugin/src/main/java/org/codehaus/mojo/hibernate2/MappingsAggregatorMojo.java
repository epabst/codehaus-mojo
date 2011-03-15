package org.codehaus.mojo.hibernate2;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.hibernate2.beans.CommonOperationsBean;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;

/**
 * @goal aggregate-mappings
 *
 * @requiresDependencyResolution
 *
 * @description A Maven 2.0 Hibernate plugin for schema update
 *
 * @phase process-sources
 *
 * @version $Id$
 */
public class MappingsAggregatorMojo
    extends CommonOperationsBean
{
    /**
     * a filename to output the aggregated hibernate mappings to
     *
     * @parameter expression="${project.build.directory}/schema/aggregate.sql"
     * @required
     */
    private String outputFile;

    public void execute()
        throws MojoExecutionException
    {
        try
        {
            String version = null;

            if ( getBasedir() == null )
            {
                throw new MojoExecutionException( "Required configuration missing: basedir" );
            }

            File files[] = getIncludeFiles();
            if ( files == null || files.length <= 0 )
            {
                return;
            }
            File f = new File( getOutputFile() );
            if ( !f.exists() )
            {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter( new FileWriter( f ), format );
            writer.setEntityResolver( new HibernateEntityResolver() );
            //writer.setResolveEntityRefs(false);
            Document finalDoc = DocumentHelper.createDocument();
            Element rootHM = null;
            for ( int i = 0; i < files.length; i++ )
            {
                print( "Parsing: " + files[ i ].getAbsolutePath() );
                SAXReader reader = new SAXReader( false );
                reader.setEntityResolver( new HibernateEntityResolver() );
                //reader.setIncludeExternalDTDDeclarations(false);
                //reader.setIncludeExternalDTDDeclarations(false);
                Document current = reader.read( files[ i ] );
                String currentVersion = getVersion( current );
                if ( version == null )
                {
                    version = currentVersion;
                    finalDoc.setProcessingInstructions( current.processingInstructions() );
                    finalDoc.setDocType( current.getDocType() );
                    rootHM = finalDoc.addElement( "hibernate-mapping" );
                }
                else if ( !version.equals( currentVersion ) )
                {
                    //LOG.warn("Mapping in " + files[i].getName() + " is not of the same mapping version as " + files[0].getName() + " mapping, so merge is impossible. Skipping");
                    continue;
                }
                for ( Iterator iter = current.selectSingleNode( "hibernate-mapping" ).selectNodes(
                    "class" ).iterator(); iter.hasNext(); rootHM.add( (Element) ( (Element) iter.next() ).clone() ) )
                {
                }
            }

            print( "Writing aggregate file: " + f.getAbsolutePath() );
            writer.write( finalDoc );
            writer.close();
        }
        catch ( Exception ex )
        {
            throw new MojoExecutionException( "Error in executing MappingsAgrregatorBean", ex );
        }
    }

    public String getOutputFile()
    {
        return outputFile;
    }

    public void setOutputFile( String outputFile )
    {
        this.outputFile = outputFile;
    }

    private String getVersion( Document current )
    {
        String docType = current.getDocType().getText();
        if ( docType == null || "".equals( docType.trim() ) )
        {
            return "";
        }
        if ( docType.indexOf( "hibernate-mapping-2.0.dtd" ) > 0 )
        {
            return "2.0";
        }
        if ( docType.indexOf( "hibernate-mapping-1.1.dtd" ) > 0 )
        {
            return "1.1";
        }
        else
        {
            return null;
        }
    }
}
