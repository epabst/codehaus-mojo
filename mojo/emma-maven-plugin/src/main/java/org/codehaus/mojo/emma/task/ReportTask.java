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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.vladium.emma.AppLoggers;
import com.vladium.emma.report.IReportProperties;
import com.vladium.emma.report.ReportProcessor;
import com.vladium.logging.ILogLevels;

/**
 * Create a report from EMMA coverage data.
 * 
 * @author <a href="mailto:alexandre.roman@gmail.com">Alexandre ROMAN</a>
 */
public class ReportTask
    extends AbstractTask
{

    /**
     * The report types.
     */
    private static final Map TYPE_REPORT_FILES = new HashMap( 2 );

    static
    {
        TYPE_REPORT_FILES.put( "xml", "coverage.xml" );
        TYPE_REPORT_FILES.put( "html", "index.html" );
    }

    /**
     * The source paths.
     */
    private File[] sourcePaths = new File[0];

    /**
     * The encoding.
     */
    private String encoding;

    /**
     * The depth.
     */
    private String depth;

    /**
     * The columns.
     */
    private String columns;

    /**
     * The sort ordering.
     */
    private String sort;

    /**
     * The metrics.
     */
    private String metrics;

    /**
     * The metadata file.
     */
    private File metadataFile;

    /**
     * The data files.
     */
    private File[] dataFiles = new File[0];

    /**
     * should only an XML report be generated.
     */
    private boolean generateOnlyXml;

    /**
     * Executes the task.
     * @throws IOException 
     */
    public void execute() throws IOException
    {
        final ReportProcessor processor = ReportProcessor.create();

        final List paths = new ArrayList( 2 );
        // set metadata file
        if ( metadataFile != null )
        {
            paths.add( metadataFile.getCanonicalPath() );
        }

        // set additional coverage data files
        final String[] data = getCanonicalPaths( dataFiles );
        if ( data != null )
        {
            paths.addAll( Arrays.asList( data ) );
        }

        processor.setDataPath( (String[]) paths.toArray( new String[paths.size()] ) );

        // set paths to Java source code
        final String[] sources = getCanonicalPaths( sourcePaths );
        if ( sources != null )
        {
            processor.setSourcePath( sources );
        }

        final List types = new ArrayList( 2 );
        types.add( "xml" );
        if ( !generateOnlyXml )
        {
            types.add( "html" );
        }

        final Properties props = new Properties();
        for ( final Iterator iterator = types.iterator(); iterator.hasNext(); )
        {
            final String type = (String) iterator.next();
            final String prefix = IReportProperties.PREFIX + type + ".";
            final File outputFile = new File( getOutputDirectory(), (String) TYPE_REPORT_FILES.get( type ) );
            props.setProperty( prefix + IReportProperties.OUT_FILE, outputFile.getCanonicalPath() );
            // set report options
            if ( columns != null )
            {
                props.setProperty( prefix + IReportProperties.COLUMNS, columns );
            }
            if ( sort != null )
            {
                props.setProperty( prefix + IReportProperties.SORT, sort );
            }
            if ( metrics != null )
            {
                props.setProperty( prefix + IReportProperties.METRICS, metrics );
            }
            if ( depth != null )
            {
                props.setProperty( prefix + IReportProperties.DEPTH, depth );
            }
            if ( encoding != null )
            {
                props.setProperty( prefix + IReportProperties.OUT_ENCODING, encoding );
            }
        }
        if ( isVerbose() )
        {
            props.setProperty( AppLoggers.PROPERTY_VERBOSITY_LEVEL, ILogLevels.VERBOSE_STRING );
        }

        processor.setReportTypes( (String[]) types.toArray( new String[types.size()] ) );
        processor.setPropertyOverrides( props );

        processor.run();
    }

    /**
     * Gets the columns.
     * 
     * @return the columns.
     */
    public String getColumns()
    {
        return columns;
    }

    /**
     * Sets the columns.
     * 
     * @param columns the columns.
     */
    public void setColumns( String columns )
    {
        this.columns = columns;
    }

    /**
     * Gets the depth.
     * 
     * @return the depth.
     */
    public String getDepth()
    {
        return depth;
    }

    /**
     * Sets the depth.
     * 
     * @param depth the depth.
     */
    public void setDepth( String depth )
    {
        this.depth = depth;
    }

    /**
     * Gets the encoding.
     * 
     * @return the encoding.
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * Sets the encoding.
     * 
     * @param encoding the encoding.
     */
    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    /**
     * Gets the metrics.
     * 
     * @return the metrics.
     */
    public String getMetrics()
    {
        return metrics;
    }

    /**
     * Sets the metrics.
     * 
     * @param metrics the metrics.
     */
    public void setMetrics( String metrics )
    {
        this.metrics = metrics;
    }

    /**
     * Gets the sort order.
     * 
     * @return the sort order.
     */
    public String getSort()
    {
        return sort;
    }

    /**
     * Sets the sort order.
     * 
     * @param sort the sort order.
     */
    public void setSort( String sort )
    {
        this.sort = sort;
    }

    /**
     * Gets the source paths.
     * 
     * @return the source paths.
     */
    public File[] getSourcePaths()
    {
        return sourcePaths;
    }

    /**
     * Sets the source paths.
     * 
     * @param sourcePaths the source paths.
     */
    public void setSourcePaths( File[] sourcePaths )
    {
        this.sourcePaths = sourcePaths;
    }

    /**
     * Gets the data files.
     * 
     * @return the data files.
     */
    public File[] getDataFiles()
    {
        return dataFiles;
    }

    /**
     * Sets the data files.
     * 
     * @param dataFiles the data files.
     */
    public void setDataFiles( File[] dataFiles )
    {
        this.dataFiles = dataFiles;
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
     * Should only the XML report be generated.
     * 
     * @return <code>true</code> if only the XML report should be generated.
     */
    public boolean isGenerateOnlyXml()
    {
        return generateOnlyXml;
    }

    /**
     * Sets whether only the XML report be generated.
     * 
     * @param generateOnlyXml <code>true</code> if only the XML report should be generated.
     */
    public void setGenerateOnlyXml( boolean generateOnlyXml )
    {
        this.generateOnlyXml = generateOnlyXml;
    }
}
