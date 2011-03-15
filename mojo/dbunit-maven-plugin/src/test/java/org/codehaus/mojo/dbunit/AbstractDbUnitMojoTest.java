package org.codehaus.mojo.dbunit;

/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Id$
 */
public abstract class AbstractDbUnitMojoTest
    extends TestCase
{
    protected Properties p;

    protected Connection c;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        loadTestProperties();
        loadDriver();
        initDB();
 
    }

    protected void tearDown()
        throws Exception
    {
        if ( c != null )
        {
            c.close();
        }
        super.tearDown();
    }

    private void loadTestProperties()
        throws Exception
    {
        p = new Properties();
        p.load( getClass().getResourceAsStream( "/test.properties" ) );
    }
    
    private void loadDriver()
    {
        try
        {
            Class.forName( p.getProperty( "driver" ) );
        }
        catch ( Exception e )
        {
            System.out.println( "ERROR: failed to load driver." );
            e.printStackTrace();
        }
    }

    private Connection getConnection()
        throws SQLException
    {
        return DriverManager.getConnection( p.getProperty( "url" ), p.getProperty( "username" ), p
            .getProperty( "password" ) );
    }
    
    private void initDB()
        throws SQLException
    {
        c = getConnection();

        Statement st = c.createStatement();
        st.executeUpdate( "drop table person if exists" );
        st.executeUpdate( "create table person ( id integer, first_name varchar, last_name varchar)" );        
    }
    
    protected void populateMojoCommonConfiguration( AbstractDbUnitMojo mojo )
    {
        // populate parameters
        mojo.driver = p.getProperty( "driver" ) ;
        mojo.username = p.getProperty( "username" ) ;
        mojo.password =  p.getProperty( "password" );
        mojo.url =  p.getProperty( "url" ) ;
        mojo.schema =  p.getProperty( "schema" ) ;
        mojo.dataTypeFactoryName = p.getProperty( "dataTypeFactory", "org.dbunit.dataset.datatype.DefaultDataTypeFactory"  );
        mojo.metadataHandlerName = p.getProperty( "metadataHandler", "org.dbunit.database.DefaultMetadataHandler"  );
        mojo.supportBatchStatement = getBooleanProperty( "supportBatchStatement" ) ;
        mojo.useQualifiedTableNames = getBooleanProperty( "useQualifiedTableNames" );
        mojo.escapePattern = p.getProperty( "datatypeWarning" );

    }

    private boolean getBooleanProperty( String key )
    {
        return Boolean.valueOf( p.getProperty( key, "false" ) ).booleanValue();
    }    
    
    protected static File getBasedir()
    {
        return new File( System.getProperty( "basedir", System.getProperty( "user.dir" ) ));
    }
}
