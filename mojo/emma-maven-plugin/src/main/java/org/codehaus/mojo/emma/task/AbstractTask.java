package org.codehaus.mojo.emma.task;

/*
 * The MIT License
 *
 * Copyright (c) 2007-8, The Codehaus
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for executing EMMA actions.
 * 
 * @author <a href="mailto:alexandre.roman@gmail.com">Alexandre ROMAN</a>
 */
public abstract class AbstractTask
{
    /**
     * The output directory.
     */
    private File outputDirectory;

    /**
     * Is verbose.
     */
    private boolean verbose;

    /**
     * Is the task verbose.
     * 
     * @return <code>true</code> if the task is verbose.
     */
    public boolean isVerbose()
    {
        return verbose;
    }

    /**
     * Sets the verbosity of the task.
     * 
     * @param verbose <code>true</code> if the task is verbose.
     */
    public void setVerbose( boolean verbose )
    {
        this.verbose = verbose;
    }

    /**
     * Gets the output directory.
     * 
     * @return the output directory.
     */
    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * Sets the output directory.
     * 
     * @param outputDirectory the output directory.
     */
    public void setOutputDirectory( File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Returns the canonical paths of an array of files.
     * 
     * @param files the files.
     * @return the canonical paths of an array of files.
     * @throws IOException if things went wrong.
     */
    protected String[] getCanonicalPaths( File[] files )
        throws IOException
    {
        if ( files == null )
        {
            return null;
        }
        final List nonNullPaths = new ArrayList( files.length );
        for ( int i = 0; i < files.length; ++i )
        {
            if ( files[i] == null )
            {
                continue;
            }
            nonNullPaths.add( files[i].getCanonicalPath() );
        }
        return (String[]) nonNullPaths.toArray( new String[nonNullPaths.size()] );
    }
}
