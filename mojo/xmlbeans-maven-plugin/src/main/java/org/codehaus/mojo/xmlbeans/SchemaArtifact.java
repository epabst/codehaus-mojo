package org.codehaus.mojo.xmlbeans;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;

public class SchemaArtifact
{

    private static final String[] XSD_SUFFIXES = {"xsd", "XSD"};

    /**
     * Assuming that the artifact has a file handle, returns a collection of strings
     * pointing to each xsd file within the jar.
     *
     * @return Collection of xsd file paths relative to the jar file.
     * @number MXMLBEANS-21
     */
    public static Map getFilePaths( Artifact artifact, Log logger, File prefix )
            throws XmlBeansException
    {
        Map xsds = new HashMap();
        File artifactFile = artifact.getFile();
        List nextSet;
        if ( artifactFile != null )
        {
            try
            {
                FilteredJarFile jarFile = new FilteredJarFile( artifactFile, logger );

                nextSet = jarFile.getEntryPathsAndExtract( XSD_SUFFIXES, prefix );
                String fileName;
                for ( Iterator i = nextSet.iterator(); i.hasNext(); )
                {
                    fileName = ( String ) i.next();
                    if ( logger.isDebugEnabled() )
                    {
                        logger.debug( "Adding " + fileName + "from an artifact." );
                    }
                    xsds.put( fileName, new File( prefix, fileName ) );
                }

            }
            catch ( IOException ioe )
            {
                throw new XmlBeansException( XmlBeansException.XSD_ARTIFACT_JAR, ioe );

            }
        }
        else
        {
            throw new XmlBeansException( XmlBeansException.ARTIFACT_FILE_PATH, artifact.toString() );
        }

        return xsds;
    }

}
