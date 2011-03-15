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
import java.util.Properties;

import com.vladium.emma.AppLoggers;
import com.vladium.emma.instr.InstrProcessor;
import com.vladium.emma.instr.InstrProcessor.OutMode;
import com.vladium.logging.ILogLevels;

/**
 * Instrument classes.
 * 
 * @author <a href="mailto:alexandre.roman@gmail.com">Alexandre ROMAN</a>
 */
public class InstrumentTask
    extends AbstractTask
{
    /**
     * The instrumentation paths.
     */
    private File[] instrumentationPaths = new File[0];

    /**
     * Should the results be merged.
     */
    private boolean merge;

    /**
     * The metadata file.
     */
    private File metadataFile;

    /**
     * The filters.
     */
    private String[] filters = new String[0];

    /**
     * Executes the task.
     * @throws IOException 
     */
    public void execute() throws IOException
    {
        final InstrProcessor processor = InstrProcessor.create();
        processor.setInstrOutDir( getOutputDirectory().getCanonicalPath() );
        processor.setMetaOutFile( metadataFile.getCanonicalPath() );
        processor.setMetaOutMerge( Boolean.valueOf( merge ) );
        processor.setOutMode( OutMode.OUT_MODE_FULLCOPY );

        // set instrumentation paths
        final String[] paths = getCanonicalPaths( instrumentationPaths );
        if ( paths != null )
        {
            processor.setInstrPath( paths, true );
        }

        // set instrumentation filters
        if ( filters != null )
        {
            final List nonNullFilters = new ArrayList( filters.length );
            for ( int i = 0; i < filters.length; ++i )
            {
                if ( filters[i] == null )
                {
                    continue;
                }
                nonNullFilters.add( filters[i] );
            }
            processor.setInclExclFilter( (String[]) nonNullFilters.toArray( new String[nonNullFilters.size()] ) );
        }
        final Properties props = new Properties();
        if ( isVerbose() )
        {
            props.setProperty( AppLoggers.PROPERTY_VERBOSITY_LEVEL, ILogLevels.VERBOSE_STRING );
        }
        processor.setPropertyOverrides( props );

        processor.run();
    }

    /**
     * Gets the instrumentation paths.
     * 
     * @return the instrumentation paths.
     */
    public File[] getInstrumentationPaths()
    {
        return instrumentationPaths;
    }

    /**
     * Sets the instrumentation paths.
     * 
     * @param instrumentationPaths the instrumentation paths.
     */
    public void setInstrumentationPaths( File[] instrumentationPaths )
    {
        this.instrumentationPaths = instrumentationPaths;
    }

    /**
     * Gets the merge mode.
     * 
     * @return the merge mode.
     */
    public boolean isMerge()
    {
        return merge;
    }

    /**
     * Sets the merge mode.
     * 
     * @param merge the merge mode.
     */
    public void setMerge( boolean merge )
    {
        this.merge = merge;
    }

    /**
     * Gets the metadata file.
     * 
     * @return the metadata file.
     */
    public File getMetadataFile()
    {
        return metadataFile;
    }

    /**
     * Sets the metadata file.
     * 
     * @param metadataFile the metadata file.
     */
    public void setMetadataFile( File metadataFile )
    {
        this.metadataFile = metadataFile;
    }

    /**
     * Gets the filters.
     * 
     * @return the filters.
     */
    public String[] getFilters()
    {
        return filters;
    }

    /**
     * Sets the filters.
     * 
     * @param filters the filters.
     */
    public void setFilters( String[] filters )
    {
        this.filters = filters;
    }
}
