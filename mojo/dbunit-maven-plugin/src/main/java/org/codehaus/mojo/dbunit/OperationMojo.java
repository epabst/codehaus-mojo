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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dbunit.ant.Operation;
import org.dbunit.database.IDatabaseConnection;

/**
 * Execute DbUnit's Database Operation with an external dataset file.
 * 
 * @goal operation
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * 
 */
public class OperationMojo
    extends AbstractDbUnitMojo
{
    /**
     * Type of Database operation to perform. Supported types are UPDATE, 
     * INSERT, DELETE, DELETE_ALL, REFRESH, CLEAN_INSERT, MSSQL_INSERT, 
     * MSSQL_REFRESH, MSSQL_CLEAN_INSERT
     * 
     * @parameter expression="${type}" 
     * @required
     */
    protected String type;

    /**
     * When true, place the entired operation in one transaction
     * @parameter expression="${transaction}" default-value="false"
     */
    protected boolean transaction;

    /**
     * DataSet file
     * Please use sources instead.
     * @parameter expression="${src}"
     * @deprecated 1.0
     */
    protected File src;
    
    /**
     * DataSet files.
     * @parameter 
     */
    protected File[] sources;

    /**
     * Dataset file format type. Valid types are: flat, xml, csv, and dtd
     * 
     * @parameter expression="${format}" default-value="xml";
     * @required
     */
    protected String format;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( skip )
        {
            this.getLog().info( "Skip operation: " + type + " execution" );
            
            return;
        }

        super.execute();
        
        List concatenatedSources = new ArrayList();
        CollectionUtils.addIgnoreNull( concatenatedSources, src );
        if ( sources != null ) {
            concatenatedSources.addAll( Arrays.asList( sources ) );
        }
        
        try
        {
            IDatabaseConnection connection = createConnection();
            
            try
            {
                for ( Iterator i = concatenatedSources.iterator(); i.hasNext(); ) {
                    File source = (File) i.next();
                    Operation op = new Operation();
                    op.setFormat( format );
                    op.setSrc( source );
                    op.setTransaction( transaction );
                    op.setType( type );
                    op.execute( connection );
                }
            }
            finally
            {
                connection.close();
            }
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error executing database operation: " + type, e );
        }
    }
}
