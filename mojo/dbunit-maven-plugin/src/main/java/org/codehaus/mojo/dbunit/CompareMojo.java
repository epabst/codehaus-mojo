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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dbunit.ant.Compare;
import org.dbunit.ant.Query;
import org.dbunit.ant.Table;
import org.dbunit.database.IDatabaseConnection;

/**
 * Execute DbUnit Compare operation
 * 
 * @goal compare
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Id$
 * 
 */
public class CompareMojo
    extends AbstractDbUnitMojo
{
    /**
     * DataSet file
     * 
     * @parameter expression="${src}"
     * @required
     */
    protected File src;
    
    /**
     * DataSet file format
     * @parameter expression="${format}" default-value="xml"
     */
    protected String format;
    
    /**
     * sort
     * @parameter expression="${sort}"
     */
    protected boolean sort;
    
    /**
     * List of DbUnit's Table.  See DbUnit's org.dbunit.ant.Table JavaDoc for details
     * @parameter
     */
    protected Table [] tables;
    
    /**
     * List of DbUnit's Query.  See DbUnit's org.dbunit.ant.Query JavaDoc for details
     * @parameter
     */
    protected Query [] queries;
    

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( skip )
        {
            this.getLog().info( "Skip DbUnit comparison" );
            return;
        }

        super.execute();
        
        try
        {
            IDatabaseConnection connection = createConnection();
            try
            {
                Compare dbUnitCompare = new Compare();
                dbUnitCompare.setSrc( src );
                dbUnitCompare.setFormat( format );
                dbUnitCompare.setSort( sort );
                
                for ( int i = 0 ; queries != null && i < queries.length; ++ i ) 
                {
                    dbUnitCompare.addQuery( (Query ) queries[i] );
                }
                for ( int i = 0 ; tables != null && i < tables.length; ++ i ) 
                {
                    dbUnitCompare.addTable( (Table ) tables[i] );
                }
                
                dbUnitCompare.execute( connection );
            }
            finally
            {
                connection.close();
            }
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error executing DbUnit comparison.", e );
        }

    }
}
