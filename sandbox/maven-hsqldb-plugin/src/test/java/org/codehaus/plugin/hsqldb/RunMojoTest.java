package org.codehaus.plugin.hsqldb;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plugin.hsqldb.RunMojo;

/**
 *
 *@author Valerio Schiavoni
 *
 */
public class RunMojoTest extends AbstractMojoTestCase
{

    private static final String DEFAULT_PASSWORD = "";

    private static final String DEFAULT_USERNAME = "sa";

    RunMojo mojoDefault;
    
    RunMojo mojoCustom;
    
    RunMojo mojoWebServer;
    
    /* (non-Javadoc)
     * @see org.apache.maven.plugin.testing.AbstractMojoTestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        //required for mojoDefault lookups to work
        super.setUp();
        File testPomDefault = new File( getBasedir() , "target/test-classes/unit/hsqldb-run-default/plugin-config.xml" );
        mojoDefault = (RunMojo) lookupMojo( "run", testPomDefault );
        
        File testPomCustom = new File( getBasedir() , "target/test-classes/unit/hsqldb-run-custom/plugin-config.xml" );
        mojoCustom = (RunMojo) lookupMojo( "run", testPomCustom );
        
        File testPomWebServer = new File( getBasedir() , "target/test-classes/unit/hsqldb-run-webserver/plugin-config.xml" );
        mojoWebServer = (RunMojo) lookupMojo( "run", testPomWebServer );
        
    }

//    public void testHsqlStartInMemoryUsingDefaultSettings() throws MojoExecutionException, ClassNotFoundException, SQLException  {
//        assertNotNull(mojoDefault);
//
//        mojoDefault.execute();
//
//        //load jdbc driver
//        Class.forName("org.hsqldb.jdbcDriver");
//
//        final Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:test", DEFAULT_USERNAME, DEFAULT_PASSWORD);
//
//        assertNotNull(connection);
//    }
    
    public void testHsqlStartInMemoryUsingCustomSettings() throws MojoExecutionException, ClassNotFoundException, SQLException  {
        assertNotNull(mojoCustom);

        mojoCustom.execute();

        //load jdbc driver
        Class.forName("org.hsqldb.jdbcDriver");

        final Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:test", DEFAULT_USERNAME, DEFAULT_PASSWORD);

        assertNotNull(connection);
        
    }
    
    public void testHsqlStartInMemoryUsingWebServerSettings() throws MojoExecutionException, ClassNotFoundException, SQLException  {
        assertNotNull(mojoWebServer);

        mojoWebServer.execute();

        //load jdbc driver
        Class.forName("org.hsqldb.jdbcDriver");

        final Connection connection = DriverManager.getConnection("jdbc:hsqldb:file:test", DEFAULT_USERNAME, DEFAULT_PASSWORD);

        assertNotNull(connection);
    }
   
    /* (non-Javadoc)
     * @see org.codehaus.plexus.PlexusTestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        
        assertFalse(FileUtils.fileExists( "test" ));
        
    }

}
