package org.codehaus.mojo.jpox;

/*
 * Copyright (c) 2004, Codehaus.org
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

import org.codehaus.plexus.util.cli.Commandline;

/**
 * Provides detailed information about the database - limits and datatypes
 * support.
 * <p>
 * <em>Currently this seems to be printing out the information for each of available database datatypes twice.</em>
 * 
 * @goal schema-dbinfo
 * @requiresDependencyResolution
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class JpoxSchemaDatabaseInfoMojo extends AbstractJpoxSchemaMojo
{

    private static final String OPERATION_MODE_DB_INFO = "-dbinfo";

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.mojo.jpox.AbstractJpoxSchemaMojo#prepareModeSpecificCommandLineArguments(org.codehaus.plexus.util.cli.Commandline)
     */
    protected void prepareModeSpecificCommandLineArguments( Commandline cl )
    {
        cl.createArgument().setValue( OPERATION_MODE_DB_INFO );
    }

}
