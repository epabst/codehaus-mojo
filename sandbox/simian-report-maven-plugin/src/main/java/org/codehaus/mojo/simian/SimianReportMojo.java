package org.codehaus.mojo.simian;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import au.com.redhillconsulting.simian.Checker;
import au.com.redhillconsulting.simian.FileLoader;
import au.com.redhillconsulting.simian.Option;
import au.com.redhillconsulting.simian.Options;
import au.com.redhillconsulting.simian.SourceFile;

/**
 * Implement the Simian report.
 * 
 * @author Miguel Griffa
 * @version $Id$
 * @goal simian
 * @description Runs the simian tool on the project sources
 * @todo needs to support the multiple source roots (based on pmd plugin)
 */
public class SimianReportMojo
    extends AbstractMavenReport
{
    /**
     * @parameter expression="${project.build.sourceDirectory}"
     * @required
     * @readonly
     */
    private String sourceDirectory;

    /**
     * @parameter expression="${project.build.testSourceDirectory}"
     * @required
     * @readonly
     */
    private String testDirectory;

    /**
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     */
    private String outputDirectory;

    /**
     * @component
     */
    private SiteRenderer siteRenderer;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    // Simian options
    /**
     * Matches will contain at least the specified number of lines
     * @parameter  default-value="6"
     * @optional 
     */
    private int minimumThreshold = 6;

    /**
     * MyVariable and myvariable would both match
     * @parameter  
     * @optional 
     */
    private boolean ignoreStrings;

    /**
     * Completely ignores all identfiers
     * @parameter  
     * @optional 
     */
    private boolean ignoreIdentifiers;

    /**
     * Completely ignores variable names (field, parameter and local). Eg. int foo = 1; and int bar = 1 would both match
     * @parameter 
     * @optional 
     */
    private boolean ignoreVariableNames;

    /**
     * int x = 1; and int x = 576; would both match
     * @parameter  
     * @optional 
     */
    private boolean ignoreNumbers;

    /**
     * 'A', "one" and 27.8would all match
     * @parameter  
     * @optional 
     */
    private boolean ignoreLiterals;

    /**
     * public, protected, static, etc.
     * @parameter 
     * @optional 
     */
    private boolean ignoreModifiers;

    /**
     * Link to jxr plugin report? If this option is set to <code>true</code>, links are added to jxr plugin
     * @parameter default-value="true"
     * @optional
     */
    private boolean linkToJxr = true;

    private Locale locale;

    /**
     * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
     */
    public String getName( final Locale locale )
    {
        return getBundle( locale ).getString( "report.simian.name" );
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
     */
    public String getDescription( final Locale locale )
    {
        return getBundle( locale ).getString( "report.simian.description" );
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
     */
    protected String getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
     */
    protected MavenProject getProject()
    {
        return project;
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
     */
    protected SiteRenderer getSiteRenderer()
    {
        return siteRenderer;
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale)
     */
    public void executeReport( final Locale locale )
        throws MavenReportException
    {
        this.locale = locale;
        final Sink sink = getSink();
        //
        final SimianAuditListener listener = new SimianAuditListener();
        listener.setLog( getLog() );

        final Options options = getOptions();

        final Checker checker = new Checker( listener, options );

        final FileLoader loader = new FileLoader( checker );

        List files = null;
        try
        {
            files = getFilesToProcess( "**/*.java", null );
        }
        catch ( IOException e )
        {
            throw new MavenReportException( "error in getFilesToProcess", e );
        }

        for ( Iterator i = files.iterator(); i.hasNext(); )
        {
            final File file = (File) i.next();
            try
            {
                loader.load( file );
            }
            catch ( IOException e )
            {
                throw new MavenReportException( "error loading " + file, e );
            }

        }

        if ( checker.check() )
        {
            getLog().debug( "Duplicate lines were found!" );
        }

        sink.body();

        summarySection( sink, listener );

        sink.section1();
        sink.sectionTitle1();
        sink.text( getBundle( locale ).getString( "report.simian.duplications" ) );
        sink.sectionTitle1_();

        Record[] records = listener.getRecords();

        List recordsList = Arrays.asList( records );
        Collections.sort( recordsList, new RecordSizeComparator() );

        Record[] blocksInfo = getDifferentBlockIds( records );

        for ( int i = 0; i < blocksInfo.length; i++ )
        {
            sink.section2();
            sink.sectionTitle2();
            sink.text( MessageFormat.format( getBundle( locale ).getString( "report.simian.duplications.subsection" ),
                                             new Integer[] { new Integer( blocksInfo[i].getBlockSize() ) } ) );
            sink.sectionTitle2_();

            sink.list();

            Record[] bsRecords = getRecordsForBlockId( records, blocksInfo[i].getBlockId() );

            for ( int j = 0; j < bsRecords.length; j++ )
            {
                Record r = bsRecords[j];
                sink.listItem();

                if ( linkToJxr )
                    sink.link( getLink( r ) + "#" + r.getStartLine() );
                sink.text( getSourceFilenameWithoutBasedir( r ) );

                sink.text( " ( " + r.getStartLine() + " - " + r.getEndLine() + " ) " );

                sink.listItem_();
            }
            if ( linkToJxr )
                sink.link_();

            sink.list();
            sink.section2_();
        }

        sink.section1_();

        sink.body_();
        sink.close();
        getLog().info( "Simian report done in " + listener.getElapsed() + " ms" );
    }

    private Record[] getRecordsForBlockId( Record[] records, int id )
    {
        List l = new ArrayList( records.length );

        for ( int i = 0; i < records.length; i++ )
        {
            if ( records[i].getBlockId() == id && !records[i].isVisited() )
            {
                l.add( records[i] );
                records[i].setVisited( true );
            }
        }
        Collections.sort( l, new Comparator()
        {
            public int compare( Object arg0, Object arg1 )
            {
                Record r0 = (Record) arg0;
                Record r1 = (Record) arg1;
                return r0.getSourcefile().getFilename().compareTo( r1.getSourcefile().getFilename() );
            }
        } );
        return (Record[]) l.toArray( new Record[l.size()] );
    }

    private Record[] getDifferentBlockIds( Record[] records )
    {
        final List added = new ArrayList();
        List l = new ArrayList( records.length );

        for ( int i = 0; i < records.length; i++ )
        {
            Integer id = new Integer( records[i].getBlockId() );

            if ( !added.contains( id ) )
            {
                added.add( id );
                l.add( records[i] );
            }
        }

        return (Record[]) l.toArray( new Record[l.size()] );
    }

    private void summarySection( final Sink sink, SimianAuditListener listener )
    {
        sink.section1();
        sink.sectionTitle1();
        sink.text( getBundle( locale ).getString( "report.simian.summary" ) );
        sink.sectionTitle1_();

        sink.table();

        sink.tableRow();
        sink.tableCell();
        sink.text( getBundle( locale ).getString( "report.simian.threshold" ) );
        sink.tableCell_();
        sink.tableCell();
        sink.text( "" + this.minimumThreshold );
        sink.tableCell_();
        sink.tableRow_();

        sink.tableRow();
        sink.tableCell();
        sink.text( getBundle( locale ).getString( "report.simian.total.duplicate.lines" ) );
        sink.tableCell_();
        sink.tableCell();
        sink.text( "" + listener.getDuplicateLineCount() );
        sink.tableCell_();
        sink.tableRow_();

        sink.tableRow();
        sink.tableCell();
        sink.text( getBundle( locale ).getString( "report.simian.total.duplicate.blocks" ) );
        sink.tableCell_();
        sink.tableCell();
        sink.text( "" + listener.getBlockCount() );
        sink.tableCell_();
        sink.tableRow_();

        sink.tableRow();
        sink.tableCell();
        sink.text( getBundle( locale ).getString( "report.simian.total.duplicate.files" ) );
        sink.tableCell_();
        sink.tableCell();
        sink.text( "" + listener.getFileWithDuplicateCount() );
        sink.tableCell_();
        sink.tableRow_();

        sink.tableRow();
        sink.tableCell();
        sink.text( getBundle( locale ).getString( "report.simian.total.processed.lines" ) );
        sink.tableCell_();
        sink.tableCell();
        sink.text( "" + listener.getTotalSourceLines() );
        sink.tableCell_();
        sink.tableRow_();

        sink.tableRow();
        sink.tableCell();
        sink.text( getBundle( locale ).getString( "report.simian.total.processed.files" ) );
        sink.tableCell_();
        sink.tableCell();
        sink.text( "" + listener.getFileProcessedCount() );
        sink.tableCell_();
        sink.tableRow_();

        sink.tableRow();
        sink.tableCell();
        sink.text( getBundle( locale ).getString( "report.simian.total.scantime" ) );
        sink.tableCell_();
        sink.tableCell();
        sink.text( listener.getElapsed() + "ms" );
        sink.tableCell_();
        sink.tableRow_();

        sink.table_();

        sink.section1_();
    }

    private String getLink( Record r )
    {

        String ret = "#";
        String sourceFile = r.getSourcefile().getFilename();

        // check if source is 'core' or test link to jxr is different 

        if ( ret.startsWith( "/" ) )
        {
            ret = ret.substring( 1 );
        }
        if ( sourceFile.startsWith( sourceDirectory ) )
        {
            ret = "xref" + sourceFile.substring( sourceDirectory.length() );
        }
        if ( sourceFile.startsWith( testDirectory ) )
        {
            ret = "xref-test" + sourceFile.substring( testDirectory.length() );
        }
        if ( ret.endsWith( ".java" ) )
        {
            ret = ret.substring( 0, ret.length() - 5 ).concat( ".html" );
        }
        return ret;
    }

    private String getSourceFilenameWithoutBasedir( Record r )
    {
        final SourceFile sourcefile = r.getSourcefile();
        return getSourceFilenameWithoutBasedir( sourcefile );
    }

    private String getSourceFilenameWithoutBasedir( final SourceFile sourcefile )
    {
        StringBuffer sb = new StringBuffer( sourcefile.getFilename() );
        String path = project.getBasedir().getAbsolutePath();

        if ( sb.subSequence( 0, path.length() ).equals( path ) )
            sb = sb.delete( 0, path.length() );

        return sb.toString();
    }

    private void rowSeparator( final Sink sink, final int cells )
    {
        // simple separator
        sink.tableRow();

        for ( int i = 0; i < cells; i++ )
        {
            sink.tableCell();
            sink.text( "" );
            sink.tableCell_();
        }
        sink.tableRow_();
    }

    private Options getOptions()
    {
        final Options options = new Options();
        options.setThreshold( 6 );
        options.setOption( Option.IGNORE_STRINGS, ignoreStrings );
        options.setOption( Option.IGNORE_IDENTIFIERS, ignoreIdentifiers );
        options.setOption( Option.IGNORE_NUMBERS, ignoreNumbers );
        options.setOption( Option.IGNORE_VARIABLE_NAMES, ignoreVariableNames );
        options.setOption( Option.IGNORE_LITERALS, ignoreLiterals );
        options.setOption( Option.IGNORE_MODIFIERS, ignoreModifiers );
        return options;
    }

    private SourceFile[] getAffectedFiles( Record[] records, Comparator comp )
    {
        List l = new ArrayList( records.length );

        for ( int i = 0; i < records.length; i++ )
        {
            if ( !l.contains( records[i].getSourcefile() ) )
            {
                l.add( records[i].getSourcefile() );
            }
        }
        Collections.sort( l, comp );
        return (SourceFile[]) l.toArray( new SourceFile[l.size()] );
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getOutputName()
     */
    public String getOutputName()
    {
        return "simian";
    }

    private List getFilesToProcess( final String includes, final String excludes )
        throws IOException
    {
        final File dir = new File( getProject().getBuild().getSourceDirectory() );
        if ( !dir.exists() )
        {
            return Collections.EMPTY_LIST;
        }

        final StringBuffer excludesStr = new StringBuffer();
        if ( StringUtils.isNotEmpty( excludes ) )
        {
            excludesStr.append( excludes );
        }
        final String[] defaultExcludes = FileUtils.getDefaultExcludes();
        for ( int i = 0; i < defaultExcludes.length; i++ )
        {
            if ( excludesStr.length() > 0 )
            {
                excludesStr.append( "," );
            }
            excludesStr.append( defaultExcludes[i] );
        }

        return FileUtils.getFiles( dir, includes, excludesStr.toString() );
    }

    private static ResourceBundle getBundle( final Locale locale )
    {
        return ResourceBundle.getBundle( "simian-report", locale, SimianReportMojo.class.getClassLoader() );
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#canGenerateReport()
     */
    public boolean canGenerateReport()
    {
        final ArtifactHandler artifactHandler = project.getArtifact().getArtifactHandler();
        return ( "java".equals( artifactHandler.getLanguage() ) );
    }

    private static final class RecordSizeComparator
        implements Comparator
    {

        public int compare( Object arg0, Object arg1 )
        {
            Record r0 = (Record) arg0;
            Record r1 = (Record) arg1;
            return new Integer( r1.getBlockSize() ).compareTo( new Integer( r0.getBlockSize() ) );
        }

    }

}
