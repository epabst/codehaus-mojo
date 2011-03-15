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
import org.dbunit.ant.Export;
import org.dbunit.ant.Query;
import org.dbunit.ant.Table;
import org.dbunit.database.IDatabaseConnection;

/**
 * Execute DbUnit Export operation
 * 
 * @goal export
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 * @version $Id$
 * 
 */
public class ExportMojo
    extends AbstractDbUnitMojo
{
    /**
     * Location of exported DataSet file
     * @parameter expression="${dest}" default-value="${project.build.directory}/dbunit/export.xml"
     */
    protected File dest;
    
    /**
     * DataSet file format
     * @parameter expression="${format}" default-value="xml"
     */
    protected String format;
    
    /**
     * doctype
     * @parameter expression="${doctype}"
     */
    protected String doctype;
    
    /**
     * List of DbUnit's Table.  See DbUnit's JavaDoc for details
     * @parameter
     */
    protected Table [] tables;
    
    /**
     * List of DbUnit's Query.  See DbUnit's JavaDoc for details
     * @parameter
     */
    protected Query [] queries;
    
    /**
     * Set to true to order exported data according to integrity constraints defined in DB.
     * @parameter expression="${ordered}"
     */
    protected boolean ordered;
    
    /**
     * Encoding of exported data.
     * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
     */
    protected String encoding;
    

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( skip )
        {
            this.getLog().info( "Skip export execution" );
            return;
        }

        super.execute();
        
        try
        {
            //dbunit require dest directory is ready
            dest.getParentFile().mkdirs();
            
            IDatabaseConnection connection = createConnection();
            try
            {
                Export export = new Export();
                export.setOrdered( ordered );
                for ( int i = 0 ; queries != null && i < queries.length; ++ i ) 
                {
                    export.addQuery( (Query ) queries[i] );
                }
                for ( int i = 0 ; tables != null && i < tables.length; ++ i ) 
                {
                    export.addTable( (Table ) tables[i] );
                }
                
                export.setDest( dest );
                export.setDoctype( doctype );
                export.setFormat( format );
                export.setEncoding( encoding );
                
                export.execute( connection );
            }
            finally
            {
                connection.close();
            }
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error executing export", e );
        }

    }
}
