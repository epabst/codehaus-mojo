package org.codehaus.mojo.emma;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.emma.task.ReportTask;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.SelectorUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Check last intrumentation results.
 * 
 * @author <a href="mailto:alexandre.roman@gmail.com">Alexandre ROMAN</a>
 * @goal check
 * @execute phase="test" lifecycle="emma"
 * @phase verify
 */
public class EmmaCheckMojo
    extends AbstractEmmaMojo
{
    /**
     * The tag types.
     */
    private static final Map TAG_2_TYPE = new HashMap();

    static
    {
        TAG_2_TYPE.put( "all", CoverageResult.Type.ALL );
        TAG_2_TYPE.put( "package", CoverageResult.Type.PACKAGE );
        TAG_2_TYPE.put( "class", CoverageResult.Type.CLASS );
        TAG_2_TYPE.put( "method", CoverageResult.Type.METHOD );
    }

    /**
     * Location to XML coverage file.
     * 
     * @parameter expression="${emma.coverageFile}"
     *            default-value="${project.reporting.outputDirectory}/emma/coverage.xml"
     * @required
     */
    protected File coverageFile;

    /**
     * Check configuration.
     * 
     * @parameter
     */
    protected CheckConfiguration check;

    /**
     * Location to store class coverage metadata.
     * 
     * @parameter expression="${emma.metadataFile}" default-value="${project.build.directory}/coverage.em"
     * @required
     */
    protected File metadataFile;

    /**
     * Class coverage data files.
     * 
     * @parameter
     */
    protected File[] dataFiles;

    /**
     * Location to store EMMA generated resources.
     * 
     * @parameter default-value="${project.reporting.outputDirectory}/emma"
     * @required
     */
    protected File outputDirectory;

    /**
     * Does the work.
     * 
     * @throws MojoExecutionException if things go wrong.
     * @throws MojoFailureException if things go wrong.
     */
    protected void doExecute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( dataFiles == null )
        {
            dataFiles = new File[] { new File( project.getBasedir(), "coverage.ec" ) };
        }

        dataFiles = EmmaUtils.fixDataFileLocations( project, dataFiles );

        if ( coverageFile == null || !coverageFile.exists() )
        {
            if ( dataFiles != null && dataFiles.length > 0 )
            {
                // XML report was not generated: let's generate it now!
                coverageFile = generateReport();
            }
            else
            {
                getLog().info( "Not checking EMMA coverage results, as no results were found" );
                return;
            }
        }
        if ( check == null )
        {
            getLog().info( "Not checking EMMA coverage results, as no configuration was set" );
            return;
        }

        getLog().info( "Checking EMMA coverage results" );

        // read XML coverage results
        final Document doc;
        InputStream input = null;
        try
        {
            input = new FileInputStream( coverageFile );
            final Builder builder = new Builder();
            doc = builder.build( input );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Failed to read EMMA coverage results from file: "
                + coverageFile.getPath(), e );
        }
        finally
        {
            IOUtil.close( input );
        }

        CoverageResult allResults = null;
        final List results = new ArrayList( 3 );

        // parse XML coverage results
        try
        {
            final Nodes allNodes = doc.query( "/report/data/all" );
            if ( allNodes.size() > 0 )
            {
                allResults = toCoverageResult( (Element) allNodes.get( 0 ) );
            }

            final String[] expressions =
                { "/report/data/all/package", "/report/data/all/package/srcfile/class",
                    "/report/data/all/package/srcfile/class/method" };
            for ( int i = 0; i < expressions.length; ++i )
            {
                final Nodes nodes = doc.query( expressions[i] );
                final int nodesLen = nodes.size();
                for ( int j = 0; j < nodesLen; ++j )
                {
                    final CoverageResult r = toCoverageResult( (Element) nodes.get( j ) );
                    if ( r != null )
                    {
                        results.add( r );
                    }
                }
            }
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Failed to parse EMMA coverage results from file: "
                + coverageFile.getName(), e );
        }

        if ( getLog().isDebugEnabled() )
        {
            if ( allResults == null && results.isEmpty() )
            {
                getLog().debug( "No coverage results!" );
            }
            else
            {
                getLog().debug( "Coverage results:" );
                if ( allResults != null )
                {
                    getLog().debug( " o " + allResults );
                }

                // sort results for easier reading
                Collections.sort( results, new CoverageResultComparator() );

                for ( final Iterator i = results.iterator(); i.hasNext(); )
                {
                    final CoverageResult r = (CoverageResult) i.next();
                    getLog().debug( " o " + r );
                }
            }
        }

        // check coverage results
        checkResults( allResults, results );
    }

    /**
     * Checks the results against the targets.
     * 
     * @param allResults The overall results.
     * @param results The results based on regex limits.
     * @throws MojoExecutionException if something goes wrong.
     */
    private void checkResults( CoverageResult allResults, List results )
        throws MojoFailureException
    {
        if ( allResults != null )
        {
            if ( allResults.getBlockRate() != CoverageResult.UNKNOWN_RATE
                && allResults.getBlockRate() < check.getBlockRate() )
            {
                getLog().warn(
                               "Insufficient code coverage for blocks: " + allResults.getBlockRate() + "% < "
                                   + check.getBlockRate() + "%" );
                fail();
                return;
            }
            else if ( allResults.getClassRate() != CoverageResult.UNKNOWN_RATE
                && allResults.getClassRate() < check.getClassRate() )
            {
                getLog().warn(
                               "Insufficient code coverage for classes: " + allResults.getClassRate() + "% < "
                                   + check.getClassRate() + "%" );
                fail();
                return;
            }
            else if ( allResults.getMethodRate() != CoverageResult.UNKNOWN_RATE
                && allResults.getMethodRate() < check.getMethodRate() )
            {
                getLog().warn(
                               "Insufficient code coverage for methods: " + allResults.getMethodRate() + "% < "
                                   + check.getMethodRate() + "%" );
                fail();
                return;
            }
            else if ( allResults.getLineRate() != CoverageResult.UNKNOWN_RATE
                && allResults.getLineRate() < check.getLineRate() )
            {
                getLog().warn(
                               "Insufficient code coverage for lines: " + allResults.getLineRate() + "% < "
                                   + check.getLineRate() + "%" );
                fail();
                return;
            }
        }

        for ( int i = 0; i < check.getRegexes().length; ++i )
        {
            final CheckConfiguration.Regex regex = check.getRegexes()[i];

            for ( final Iterator j = results.iterator(); j.hasNext(); )
            {
                final CoverageResult result = (CoverageResult) j.next();
                if ( !SelectorUtils.match( regex.getPattern(), result.getName() ) )
                {
                    continue;
                }
                if ( result.getBlockRate() != CoverageResult.UNKNOWN_RATE
                    && result.getBlockRate() < regex.getBlockRate() )
                {
                    getLog().warn(
                                   "Insufficient code coverage for blocks in " + regex.getPattern() + ": "
                                       + result.getBlockRate() + "% < " + regex.getBlockRate() + "%" );
                    fail();
                    return;
                }
                else if ( result.getClassRate() != CoverageResult.UNKNOWN_RATE
                    && result.getClassRate() < regex.getClassRate() )
                {
                    getLog().warn(
                                   "Insufficient code coverage for classes in " + regex.getPattern() + ": "
                                       + result.getClassRate() + "% < " + regex.getClassRate() + "%" );
                    fail();
                    return;
                }
                else if ( result.getMethodRate() != CoverageResult.UNKNOWN_RATE
                    && result.getMethodRate() < regex.getMethodRate() )
                {
                    getLog().warn(
                                   "Insufficient code coverage for methods in " + regex.getPattern() + ": "
                                       + result.getMethodRate() + "% < " + regex.getMethodRate() + "%" );
                    fail();
                    return;
                }
                else if ( result.getLineRate() != CoverageResult.UNKNOWN_RATE
                    && result.getLineRate() < regex.getLineRate() )
                {
                    getLog().warn(
                                   "Insufficient code coverage for lines in " + regex.getPattern() + ": "
                                       + result.getLineRate() + "% < " + regex.getLineRate() + "%" );
                    fail();
                    return;
                }
            }
        }

        getLog().info( "EMMA coverage results are valid" );
    }

    /**
     * Fail Mojo execution if coverage results are not valid.
     * 
     * @throws MojoFailureException if the results are not valid 
     */
    private void fail()
        throws MojoFailureException
    {
        final String failMsg = "Failed to validate EMMA coverage results: see report for more information";
        if ( check.isHaltOnFailure() )
        {
            throw new MojoFailureException( failMsg );
        }
        else
        {
            getLog().warn( failMsg );
        }
    }

    /**
     * Convert XML element to {@link CoverageResult} instance.
     * 
     * @param elem The element to convert.
     * @return The {@link CoverageResult}.
     */
    private CoverageResult toCoverageResult( Element elem )
    {
        final CoverageResult.Type type = (CoverageResult.Type) TAG_2_TYPE.get( elem.getLocalName() );
        if ( type == null )
        {
            return null;
        }

        final CoverageResult result;
        if ( CoverageResult.Type.ALL.equals( type ) )
        {
            result = new CoverageResult();
        }
        else
        {
            final String name;
            if ( CoverageResult.Type.CLASS.equals( type ) )
            {
                name = fullClassName( elem );
            }
            else if ( CoverageResult.Type.METHOD.equals( type ) )
            {
                name = fullMethodName( elem );
            }
            else
            {
                name = elem.getAttributeValue( "name" );
            }
            result = new CoverageResult( type, name );
        }

        final Nodes coverageNodes = elem.query( "coverage" );
        final int len = coverageNodes.size();
        for ( int i = 0; i < len; ++i )
        {
            final Element coverageElem = (Element) coverageNodes.get( i );
            final String coverageType = coverageElem.getAttributeValue( "type" );
            if ( StringUtils.isEmpty( coverageType ) )
            {
                continue;
            }
            final String coverageValueStr = coverageElem.getAttributeValue( "value" );
            if ( StringUtils.isEmpty( coverageValueStr ) )
            {
                continue;
            }

            final int percentIndex = coverageValueStr.indexOf( '%' );
            if ( percentIndex == -1 )
            {
                continue;
            }

            final int coverageValue;
            try
            {
                coverageValue = Integer.parseInt( coverageValueStr.substring( 0, percentIndex ).trim() );
            }
            catch ( NumberFormatException e )
            {
                getLog().debug( "Failed to parse coverage value: " + coverageValueStr, e );
                continue;
            }

            if ( coverageType.startsWith( "class" ) )
            {
                result.setClassRate( coverageValue );
            }
            else if ( coverageType.startsWith( "method" ) )
            {
                result.setMethodRate( coverageValue );
            }
            else if ( coverageType.startsWith( "block" ) )
            {
                result.setBlockRate( coverageValue );
            }
            else if ( coverageType.startsWith( "line" ) )
            {
                result.setLineRate( coverageValue );
            }
        }

        return result;
    }

    /**
     * Get full class name (package + class) for "class" XML element.
     * 
     * @param elem The element.
     * @return the full class name (package + class) for "class" XML element.
     */
    private String fullClassName( Element elem )
    {
        final Element packageElem = (Element) elem.getParent().getParent();
        final String packageName = packageElem.getAttributeValue( "name" );
        final String className = elem.getAttributeValue( "name" );
        return packageName.length() != 0 ? packageName + "." + className : className;
    }

    /**
     * Get full method name (package + class + method) for "method" XML element.
     * 
     * @param elem The element.
     * @return the full method name (package + class + method) for "method" XML element.
     */
    private String fullMethodName( Element elem )
    {
        final Element classElem = (Element) elem.getParent();
        final String name = elem.getAttributeValue( "name" );
        final int i = name.indexOf( " (" );
        final String methodName;
        if ( i != -1 )
        {
            methodName = name.substring( 0, i );
        }
        else
        {
            // no parenthesis found: must be a static block
            methodName = "static";
        }
        return fullClassName( classElem ) + "." + methodName;
    }

    /**
     * Generate the report.
     * 
     * @return the report file.
     * @throws MojoExecutionException if things go wrong.
     */
    private File generateReport()
        throws MojoExecutionException
    {
        final ReportTask task = new ReportTask();
        task.setVerbose( verbose );
        task.setMetadataFile( metadataFile );
        task.setDataFiles( dataFiles );
        task.setOutputDirectory( outputDirectory );
        task.setGenerateOnlyXml( true );

        try
        {
            task.execute();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }

        return new File( outputDirectory, "coverage.xml" );
    }

    /**
     * Sort {@link CoverageResult} instance against their name.
     */
    private static class CoverageResultComparator
        implements Comparator
    {
        /**
         * Compares two parameters.
         * 
         * @param o1 the first parameter.
         * @param o2 the second parameter.
         * @return &lt; 0 if o1 &lt; o2; == 0 if o1 == o2; &gt; 0 if o1 &gt; o2
         */
        public int compare( Object o1, Object o2 )
        {
            final CoverageResult r1 = (CoverageResult) o1;
            final CoverageResult r2 = (CoverageResult) o2;
            return r1.getName().compareTo( r2.getName() );
        }
    }
}
