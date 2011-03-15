package org.codehaus.mojo.antlr3;

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
import java.util.ArrayList;
import java.util.List;

import org.codehaus.mojo.antlr3.AntlrHelper;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:dave@badgers-in-foil.co.uk">David Holroyd</a>
 * @version $Id $
 */
public class AntlrHelperTest extends TestCase
{
    private File tmpDir;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws IOException
    {
        tmpDir = new File( System.getProperty( "java.io.tmpdir" ), "maven-antlr3-plugin-test" );
        tmpDir.mkdir();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown()
    {
        // delete files (assumes no subdirs created),
        File[] files = tmpDir.listFiles();
        for ( int i = 0; i < files.length; i++ )
        {
            files[i].delete();
        }
        // delete the folder itself,
        tmpDir.delete();
    }

    public void testBuildRequired() throws IOException, InterruptedException
    {
        File fileA = new File( tmpDir, "a" );
        File fileB = new File( tmpDir, "b" );
        assertTrue( fileA.createNewFile() );
        assertTrue( fileA.setLastModified( fileA.lastModified() - 10000 ) );
        assertTrue( fileB.createNewFile() );
        List targets = new ArrayList();

        targets.add( fileB );
        assertFalse( AntlrHelper.buildRequired( fileA.getPath(), targets ) );

        targets.clear();
        targets.add( fileA );
        assertTrue( AntlrHelper.buildRequired( fileB.getPath(), targets ) );
    }
}