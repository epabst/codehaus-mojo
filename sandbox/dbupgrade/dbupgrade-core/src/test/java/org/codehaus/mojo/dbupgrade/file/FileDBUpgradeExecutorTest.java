package org.codehaus.mojo.dbupgrade.file;

import java.io.File;

import junit.framework.TestCase;

import org.codehaus.mojo.dbupgrade.DBUpgradeException;
import org.codehaus.mojo.dbupgrade.DBUpgradeLifecycle;

/*
 * Copyright 2000-2010 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

public class FileDBUpgradeExecutorTest
    extends TestCase
{
    private FileListDBUpgradeConfiguration config;

    private DBUpgradeLifecycle upgrader;

    private File dataDirectory = new File( "src/test/resources/org/codehaus/mojo/dbupgrade/file" );

    protected void setUp()
        throws Exception
    {
        config = new FileListDBUpgradeConfiguration();
        config.setUsername( "sa" );
        config.setPassword( "" );
        config.setDriver( "org.hsqldb.jdbcDriver" );
        config.setUrl( "jdbc:hsqldb:mem:target/testdb2s" );
        config.setVersionTableName( "upgradeinfo" );
        config.setVersionColumnName( "upgradeversion" );
        config.setScriptDirectory( dataDirectory );
        upgrader = new FileDBUpgradeLifecycle( config );

    }

    /**
     * test 2 upgrade versions using both SQL and java
     * @throws Exception
     */
    public void testGoodDBUpgradeExecutorTest()
        throws Exception
    {
        //version 1
        config.setUpgradeFile( new File( dataDirectory, "version-1.lst" ) );
        assertEquals( 2, upgrader.upgrade() );

        //version 1.1
        upgrader = new FileDBUpgradeLifecycle( config );
        config.setUpgradeFile( new File( dataDirectory, "version-1.1.lst" ) );
        assertEquals( 1, upgrader.upgrade() );

        //version 1.1 again
        upgrader = new FileDBUpgradeLifecycle( config );
        config.setUpgradeFile( new File( dataDirectory, "version-1.1.lst" ) );
        assertEquals( 0, upgrader.upgrade() );

        //version 2
        upgrader = new FileDBUpgradeLifecycle( config );
        config.setUpgradeFile( new File( dataDirectory, "version-2.lst" ) );
        assertEquals( 2, upgrader.upgrade() );

        //version 2
        upgrader = new FileDBUpgradeLifecycle( config );
        config.setUpgradeFile( new File( dataDirectory, "version-2.lst" ) );
        assertEquals( 0, upgrader.upgrade() );

    }

    public void testMissingDBUpgraderFileTest()
        throws Exception
    {
        try
        {
            config.setUpgradeFile( new File( dataDirectory, "version-bogus.lst" ) );
            upgrader.upgrade();
            fail( "Exception expected." );
        }
        catch ( DBUpgradeException e )
        {
        }
    }

    public void testMissingDBUpgraderScriptFileTest()
        throws Exception
    {
        //version 3
        config.setUpgradeFile( new File( dataDirectory, "bad.lst" ) );
        try
        {
            upgrader.upgrade();
            fail( "Missing SQL script not detected." );
        }
        catch ( DBUpgradeException e )
        {

        }
    }

    public void testBadList()
        throws Exception
    {
        //version 3. This test depends on testGoodDBUpgradeExecutorTest()
        config.setUpgradeFile( new File( dataDirectory, "version-3.lst" ) );
        try
        {
            upgrader.upgrade();
            fail( "Exception expected." );
        }
        catch ( DBUpgradeException e )
        {

        }
    }

}
