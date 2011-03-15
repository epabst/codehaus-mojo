package org.codehaus.mojo.castor;

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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.PlexusTestCase;

public class ConvertDTD2XSDMojoTest
    extends PlexusTestCase
{

    private static final String DTD_SOURCE = getBasedir() + "/src/test/resources/jnlp-6.0.10.dtd";

    private static final String XSD_DEST = getBasedir() + "/target/generated-resources/castor/xsd/jnlp-6.0.10.xsd";

    ConvertDTD2XSDMojo convertDTD2XSDMojo;

    public void setUp()
        throws IOException
    {
        FileUtils.deleteQuietly( new File( XSD_DEST ) );

        this.convertDTD2XSDMojo = new ConvertDTD2XSDMojo();
    }

    public void tearDown()
        throws IOException
    {
        convertDTD2XSDMojo = null;
    }

    public void testExecute()
        throws MojoExecutionException
    {

        File dest = new File( XSD_DEST );

        this.convertDTD2XSDMojo.setSource( new File( DTD_SOURCE ) );
        this.convertDTD2XSDMojo.setDest( dest );
        this.convertDTD2XSDMojo.execute();

        assertTrue( dest.exists() );

    }

}
