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
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Id$
 */
public class OperationMojoTest
    extends AbstractDbUnitMojoTest
{
    
    public void testCleanInsertOperation()
        throws Exception
    {
        //init database with fixed data
        OperationMojo operation = new OperationMojo();
        this.populateMojoCommonConfiguration( operation );
        operation.src = new File( p.getProperty( "xmlDataSource" ) );
        operation.format = "xml";
        operation.type = "CLEAN_INSERT";
        operation.execute();
        
        //check to makesure we have 2 rows after inserts thru dataset
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery( "select count(*) from person" );
        rs.next();
        assertEquals( 2, rs.getInt(1) );  
        
        //export database to another dataset file
        File exportFile = new File( getBasedir(), "target/export.xml" );
        ExportMojo export = new ExportMojo();
        this.populateMojoCommonConfiguration( export );
        export.dest = exportFile;
        export.format = "xml";
        export.execute();
        
        //then import the exported dataset file back to DB
        operation.src = exportFile;
        operation.execute();
        
        //check to makesure we have 2 rows
        st = c.createStatement();
        rs = st.executeQuery( "select count(*) from person" );
        rs.next();
        assertEquals( 2, rs.getInt(1) );     
        
        //finally compare the current contents of the DB with the orginal dataset file
        CompareMojo compare = new CompareMojo();
        this.populateMojoCommonConfiguration( compare );
        compare.src = new File( p.getProperty( "xmlDataSource" ) );
        compare.format = "xml";
        compare.sort =  false ;
        compare.execute();
    }

    public void testSkip()
        throws Exception
    {
        //init database with fixed data
        OperationMojo operation = new OperationMojo();
        this.populateMojoCommonConfiguration( operation );
        operation.src = new File( p.getProperty( "xmlDataSource" ) );
        operation.format = "xml";
        operation.type = "CLEAN_INSERT";
        operation.skip = true;
        operation.execute();
            
        //check to makesure we have 0 rows
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery( "select count(*) from person" );
        rs.next();
        //no data  since skip is set
        assertEquals( 0, rs.getInt(1) );           
    }
}
