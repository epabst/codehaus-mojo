/*
The MIT License

Copyright (c) 2004, The Codehaus

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package org.codehaus.mojo.scmchangelog;

import org.codehaus.mojo.scmchangelog.scm.util.ColorConsoleLogger;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.ScmProviderRepositoryWithHost;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.codehaus.mojo.scmchangelog.changelog.Release;
import org.codehaus.mojo.scmchangelog.changelog.ReleaseAlphabeticalComparator;
import org.codehaus.mojo.scmchangelog.changelog.log.grammar.GrammarEnum;
import org.codehaus.mojo.scmchangelog.changelog.log.Issue;
import org.codehaus.mojo.scmchangelog.changelog.log.OperationTypeEnum;
import org.codehaus.mojo.scmchangelog.changelog.log.ScmLogEntry;
import org.codehaus.mojo.scmchangelog.scm.util.ScmAdapter;
import org.codehaus.mojo.scmchangelog.scm.ScmAdapterFactory;
import org.codehaus.mojo.scmchangelog.tracker.BugTrackLinker;
import org.codehaus.mojo.scmchangelog.tracker.BugTrackers;
import org.codehaus.mojo.scmchangelog.tracker.BugzillaBugTrackLinker;
import org.codehaus.mojo.scmchangelog.tracker.JiraBugTrackLinker;
import org.codehaus.mojo.scmchangelog.tracker.SourceforgeBugTrackLinker;
import org.codehaus.mojo.scmchangelog.tracker.XPlannerBugTrackLinker;
import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.plugin.logging.Log;
/**
 * Goal which produces a changelog report based on the Subversion logs.
 * @author ehsavoie
 * @version $Id$
 * @goal report
 */
public class ScmActivityReport
    extends AbstractMavenReport
{
  private boolean logHasBeenColorized = false;
  /**
   * Used to specify the date format of the log entries that are retrieved from your SCM system.
   */
  private static SimpleDateFormat simpleDateFormat;
  /**
   * Used to specify the date format of the log entries that are retrieved from your SCM system.
   */
  private static SimpleDateFormat simpleTimestampFormat;
  /**
   * The separator of issue comments. Should be grammar.issueSeparator
   */
  private String commentSeparator;
  /**
   * Used to specify the tracker issue url pattern.
   *
   * @parameter expression="${changelog.trackerUrlPattern}" default-value="${project.issueManagement.url}"
   * @required
   */
  private String trackerUrlPattern;
  /**
   * Used to specify the tracker type.
   *
   * @parameter expression="${changelog.trackerType}" default-value="${project.issueManagement.system}"
   * @required
   */
  private String trackerType;
  /**
   * Used to specify the date format of the log entries that are retrieved from your SCM system.
   *
   * @parameter expression="${changelog.dateFormat}" default-value="yyyy-MM-dd"
   * @required
   */
  private String dateFormat;
  /**
   * Used to specify the date format of the log entries that are retrieved from your SCM system.
   *
   * @parameter expression="${changelog.timestampFormat}" default-value="dd/MM/yyyy HH:mm"
   * @required
   */
  private String timestampFormat;
  /**
   * The base directory.
   *
   * @parameter expression="${basedir}"
   * @required
   */
  private File basedir;
  /**
   * Grammar name for parsing svn logs
   *
   * @parameter expression="${changelog.grammar}" default-value="MANU"
   * @required
   */
  private String grammar;
  /**
   * Used to specify if the releases should be ordered alphabetally or historically.
   * Supported values are <code>historic</code> and <code>alphabetic</code>.
   *
   * @parameter expression="${changelog.releasesOrder}" default-value="historic"
   */
  private String releasesOrder;
  /**
   * The user name (used by svn and starteam protocol).
   *
   * @parameter expression="${username}"
   */
  private String username;
  /**
   * The user password (used by svn and starteam protocol).
   *
   * @parameter expression="${password}"
   */
  private String password;
  /**
   * The private key (used by java svn).
   *
   * @parameter expression="${privateKey}"
   */
  private String privateKey;
  /**
   * The passphrase (used by java svn).
   *
   * @parameter expression="${passphrase}"
   */
  private String passphrase;
  /**
   * The url of tags base directory (used by svn protocol).
   * @parameter expression="${tagBase}"
   */
  private String tagBase;

  /**
   * The regexp used to filter tags and branches names to produce the report.
   * @parameter expression="${filter}"
   */
  private String filter;
  /**
   * The Maven Project Object
   *
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;
  /**
   * The directory where the report will be generated
   *
   * @parameter expression="${project.reporting.outputDirectory}"
   * @required
   * @readonly
   */
  private File outputDirectory;
  /**
   * @parameter expression="${component.org.apache.maven.doxia.siterenderer.Renderer}"
   * @required
   * @readonly
   */
  private Renderer siteRenderer;
  /**
   * @parameter expression="${settings}"
   * @required
   * @readonly
   */
  private Settings settings;
  /**
   * @parameter expression="${component.org.apache.maven.scm.manager.ScmManager}"
   * @required
   * @readonly
   */
  private ScmManager manager;
  /**
   * The SCM connection URL.
   *
   * @parameter expression="${connectionUrl}" default-value="${project.scm.connection}"
   */
  private String connectionUrl;
  /**
   * The SCM connection URL for developers.
   *
   * @parameter expression="${connectionUrl}" default-value="${project.scm.developerConnection}"
   */
  private String developerConnectionUrl;
  /**
   * The type of connection to use (connection or developerConnection).
   *
   * @parameter expression="${connectionType}" default-value="connection"
   */
  private String connectionType;
  /**
   * The link builder to the bug tracker.
   */
  private BugTrackLinker bugLinker;
  /**
   * The resource bundle for internationalization.
   */
  private ResourceBundle bundle = getBundle( Locale.getDefault() );

  /**
   * Returns the current Maven Project
   * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
   * @return the current Maven Project
   */
  protected MavenProject getProject()
  {
    return project;
  }

  /**
   * Return the output directory
   * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
   * @return the output directory
   */
  protected String getOutputDirectory()
  {
    if ( !outputDirectory.isAbsolute() )
    {
      outputDirectory = new File( project.getBasedir(),
          outputDirectory.getPath() );
    }

    return outputDirectory.getAbsolutePath();
  }

  /**
   * Return the Maven Site renderer
   * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
   * @return the Maven Site renderer
   */
  protected Renderer getSiteRenderer()
  {
    return siteRenderer;
  }

  /**
   *
   * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
   * @param locale the current locale
   * @return description of the report.
   */
  public String getDescription( Locale locale )
  {
    return this.bundle.getString( "report.svn.changelog.description" );
  }

  /**
   *
   * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
   * @param locale the current locale
   * @return the name of the report.
   */
  public String getName( Locale locale )
  {
    return this.bundle.getString( "report.svn.changelog.plugin.name" );
  }

  /**
   * @return the name of the report.
   * @see org.apache.maven.reporting.MavenReport#getOutputName()
   */
  public String getOutputName()
  {
    return this.bundle.getString( "report.svn.changelog.plugin.output.name" );
  }

  /**
   *
   * @see org.apache.maven.reporting.MavenReport#getOutputName()
   * @param locale the current locale
   * @return the resource bundle.
   */
  protected ResourceBundle getBundle( Locale locale )
  {
    return ResourceBundle.getBundle( "scm-activity", locale,
        this.getClass().getClassLoader() );
  }

  /**
   * Build the report.
   * @param locale the current locale.
   * @throws org.apache.maven.reporting.MavenReportException in case of an error
   * generating the report.
   */
  protected void executeReport( Locale locale )
      throws MavenReportException
  {
    try
    {
      this.bundle = getBundle( locale );

      ScmRepository repository = getScmRepository();
      GrammarEnum realGrammar = GrammarEnum.valueOf( grammar );
      this.commentSeparator = realGrammar.getIssueSeparator();

      ScmFileSet fileSet = getFileSet( "" );
      getLog().debug( "FileSet : "  + fileSet );
      ScmAdapter adapter = ScmAdapterFactory.getInstance( getScmManager(), realGrammar, repository , getLog() );
      List releases = adapter.getListOfReleases( repository, fileSet );
      if ( "alphabetic".equalsIgnoreCase( releasesOrder ) )
      {
        Collections.sort( releases, new ReleaseAlphabeticalComparator() );
      }
      doGenerateReport( releases, getSink() );
    }
    catch ( MojoExecutionException ex )
    {
      Logger.getLogger( ScmActivityReport.class.getName() ).log( Level.SEVERE, null, ex );
      throw new MavenReportException( this.bundle.getString( "error" ), ex );
    }
    catch ( IOException ex )
    {
      Logger.getLogger( ScmActivityReport.class.getName() ).log( Level.SEVERE, null, ex );
      throw new MavenReportException( this.bundle.getString( "error" ), ex );
    }
    catch ( ScmException ex )
    {
      Logger.getLogger( ScmActivityReport.class.getName() ).log( Level.SEVERE, null, ex );
      throw new MavenReportException( this.bundle.getString( "error" ), ex );
    }

    try
    {
      ReportHelper.copyImage( this.bundle.getString( "report.svn.changelog.img.update" ),
              getReportOutputDirectory().getAbsolutePath() );
      ReportHelper.copyImage( this.bundle.getString( "report.svn.changelog.img.fix" ),
              getReportOutputDirectory().getAbsolutePath() );
      ReportHelper.copyImage( this.bundle.getString( "report.svn.changelog.img.remove" ),
              getReportOutputDirectory().getAbsolutePath() );
      ReportHelper.copyImage( this.bundle.getString( "report.svn.changelog.img.add" ),
              getReportOutputDirectory().getAbsolutePath() );
    }
    catch ( IOException ioex )
    {
      throw new MavenReportException( this.bundle.getString( "error.copy.images" ), ioex );
    }
  }

  /**
   * Generates the report for this mojo.
   *
   * @param releases releases to generate the report from.
   * @param sink the report formatting tool.
   */
  protected void doGenerateReport( List releases, Sink sink )
  {
    sink.head();
    sink.title();
    sink.text( bundle.getString( "report.svn.changelog.header" ) );
    sink.title_();
    sink.head_();
    sink.body();
    sink.section1();
    sink.sectionTitle1();
    sink.text( bundle.getString( "report.svn.changelog.mainTitle" ) );
    sink.sectionTitle1_();

    // Summary section
    doSummarySection( releases, sink );

    Iterator iter = releases.iterator();

    while ( iter.hasNext() )
    {
      Release release = ( Release ) iter.next();
      doRelease( release, sink );
    }

    sink.section1_();
    sink.body_();
    sink.flush();
    sink.close();
  }

  /**
   * Generates the summary report for a release.
   *
   * @param releases the releases to generate the report from.
   * @param sink the report formatting tool.
   */
  private void doSummarySection( List releases, Sink sink )
  {
    sink.paragraph();
    sink.table();
    sink.tableRow();
    sink.tableHeaderCell();
    sink.text( bundle.getString( "report.svn.changelog.version" ) );
    sink.tableHeaderCell_();
    sink.tableHeaderCell();
    sink.text( bundle.getString( "report.svn.changelog.date" ) );
    sink.tableHeaderCell_();
    sink.tableHeaderCell();
    sink.text( bundle.getString( "report.svn.changelog.author" ) );
    sink.tableHeaderCell_();
    sink.tableHeaderCell();
    sink.text( bundle.getString( "report.svn.changelog.revision" ) );
    sink.tableHeaderCell_();
    sink.tableRow_();

    Iterator iter = releases.iterator();

    while ( iter.hasNext() )
    {
      Release release = ( Release ) iter.next();
      sink.tableRow();
      sink.tableCell();
      sink.link( '#' 
          + release.getTag().getTitle() );
      sink.text( release.getTag().getTitle() );
      sink.link_();
      sink.tableCell_();
      sink.tableCell();
      sink.text( formatTimestamp( release.getTag().getDate() ) );
      sink.tableCell_();
      sink.tableCell();
      sink.text( release.getTag().getAuthor() );
      sink.tableCell_();
      sink.tableCell();

      if ( release.getTag().getEndRevision() == null )
      {
        sink.text( this.bundle.getString( "scm.head.name" ) );
      }
      else
      {
        sink.text( release.getTag().getEndRevision() );
      }

      sink.text( ":"
          + release.getTag().getStartRevision() );
      sink.tableCell_();
      sink.tableRow_();
    }

    sink.table_();
    sink.paragraph_();
  }

  /**
   * Generates the report for a release.
   *
   * @param release release to generate the report from.
   * @param sink the report formatting tool.
   */
  private void doRelease( Release release, Sink sink )
  {
    sink.sectionTitle2();
    sink.anchor( release.getTag().getTitle() );
    sink.text( release.getTag().getTitle() 
        + " - " 
        + formatDate( release.getTag().getDate() ) );
    sink.anchor_();
    sink.sectionTitle2_();
    sink.paragraph();

    sink.table();
    sink.tableRow();
    sink.tableHeaderCell( this.bundle.getString( "report.svn.changelog.img_cell.width" ) );
    sink.text( bundle.getString( "report.svn.changelog.type" ) );
    sink.tableHeaderCell_();
    sink.tableHeaderCell( this.bundle.getString( "report.svn.changelog.bug_cell.width" ) );
    sink.text( bundle.getString( "report.svn.changelog.bug" ) );
    sink.tableHeaderCell_();
    sink.tableHeaderCell();
    sink.text( bundle.getString( "report.svn.changelog.details" ) );
    sink.tableHeaderCell_();
    sink.tableHeaderCell( this.bundle.getString( "report.svn.changelog.date_cell.width" ) );
    sink.text( bundle.getString( "report.svn.changelog.date" ) );
    sink.tableHeaderCell_();
    sink.tableHeaderCell( this.bundle.getString( "report.svn.changelog.author_cell.width" ) );
    sink.text( bundle.getString( "report.svn.changelog.author" ) );
    sink.tableHeaderCell_();
    sink.tableHeaderCell( this.bundle.getString( "report.svn.changelog.revision_cell.width" ) );
    sink.text( bundle.getString( "report.svn.changelog.revision" ) );
    sink.tableHeaderCell_();
    sink.tableRow_();    
    List entries = release.getEntries();
    Collections.sort( entries );
    Collections.reverse( entries );
    getLog().info( "We have "
        + entries.size() 
        + " entries for "
        + release.getTag().getTitle() );
    Iterator iterEntry = entries.iterator();
    while ( iterEntry.hasNext() )
    {
      ScmLogEntry entry = ( ScmLogEntry ) iterEntry.next();
      getLog().info( "Entry : "
          + entry.getRevision()
          + entry.getMessage().getComment() );
      sink.tableRow();
      sink.tableCell( this.bundle.getString( "report.svn.changelog.img_cell.width" ) );

      if ( !entry.getMessage().getIssues().isEmpty() )
      {
        Iterator iterIssue = entry.getMessage().getIssues().iterator();

        while ( iterIssue.hasNext() )
        {
          Issue issue = ( Issue ) iterIssue.next();
          sink.figure();

          if ( OperationTypeEnum.UPDATE.equals( issue.getType() ) )
          {
            sink.figureGraphics( this.bundle.getString( "report.svn.changelog.img.update" ) );
          } 
          else if ( OperationTypeEnum.ADD.equals( issue.getType() ) )
          {
            sink.figureGraphics( this.bundle.getString( "report.svn.changelog.img.add" ) );
          } 
          else if ( OperationTypeEnum.FIX.equals( issue.getType() ) )
          {
            sink.figureGraphics( this.bundle.getString( "report.svn.changelog.img.fix" ) );
          } 
          else if ( OperationTypeEnum.REMOVE.equals( issue.getType() ) )
          {
            sink.figureGraphics( this.bundle.getString( "report.svn.changelog.img.remove" ) );
          }

          sink.figureCaption();
          sink.figureCaption_();
          sink.figure_();
          sink.lineBreak();
        }
      } 
      else
      {
        sink.figure();
        sink.figureGraphics( this.bundle.getString( "report.svn.changelog.img.update" ) );
        sink.figure_();
      }

      sink.tableCell_();
      sink.tableCell( this.bundle.getString( "report.svn.changelog.bug_cell.width" ) );

      if ( !entry.getMessage().getIssues().isEmpty() )
      {
        Iterator iterIssue = entry.getMessage().getIssues().iterator();

        while ( iterIssue.hasNext() )
        {
          Issue issue = ( Issue ) iterIssue.next();

          if ( issue.getIssue() != null && ! "".equals( issue.getIssue() ) )
          {
            sink.link( doLinkIssue( issue.getIssue() ) );
            sink.text( issue.getIssue() );
            sink.link_();
          }
          else
          {
            sink.text( "" );
          }
          sink.lineBreak();
        }
      }

      sink.tableCell_();
      sink.tableCell();

      String comment = entry.getMessage().getComment();

      if ( comment != null )
      {
        StringTokenizer tokenizer = new StringTokenizer( comment, commentSeparator );
        while ( tokenizer.hasMoreTokens() )
        {
          sink.text( tokenizer.nextToken() );
          sink.lineBreak();
        }
      }

      sink.tableCell_();
      sink.tableCell( this.bundle.getString( "report.svn.changelog.date_cell.width" ) );
      sink.text( formatTimestamp( entry.getDate() ) );
      sink.tableCell_();
      sink.tableCell( this.bundle.getString( "report.svn.changelog.author_cell.width" ) );
      sink.text( entry.getAuthor() );
      sink.tableCell_();
      sink.tableCell( this.bundle.getString( "report.svn.changelog.revision_cell.width" ) );
      sink.text( entry.getRevision() );
      sink.tableCell_();
      sink.tableRow_();
    }

    sink.table_();
    sink.paragraph_();
  }

  /**
   * Format a java.util.Date using the specified date format.
   * @param date the date to be formatted.
   * @return the formatted date as a String.
   */
  protected String formatDate( Date date )
  {
    if ( simpleDateFormat == null )
    {
      simpleDateFormat = new SimpleDateFormat( this.dateFormat );
    }

    return simpleDateFormat.format( date );
  }

  /**
   * Format a java.util.Date using the specified timestamp format.
   * @param date the date to be formatted.
   * @return the formatted date as a String.
   */
  protected String formatTimestamp( Date date )
  {
    if ( simpleTimestampFormat == null )
    {
      simpleTimestampFormat = new SimpleDateFormat( this.timestampFormat );
    }

    return simpleTimestampFormat.format( date );
  }

  /**
   *
   * @return the implementation of the BugTrackLinker.
   */
  protected BugTrackLinker getBugTrackLinker()
  {
    if ( bugLinker == null )
    {
      getLog().info( "Building bugLinker of type " 
          + getTrackerType()
          + " with pattern "
          + this.trackerUrlPattern );

      if ( BugTrackers.JIRA.equals( BugTrackers.valueOf( getTrackerType() ) ) )
      {
        this.bugLinker = new JiraBugTrackLinker( this.trackerUrlPattern );
      } 
      else if ( BugTrackers.BUGZILLA.equals( BugTrackers.valueOf( getTrackerType() ) ) )
      {
        this.bugLinker = new BugzillaBugTrackLinker( this.trackerUrlPattern );
      }
      else if ( BugTrackers.XPLANNER.equals( BugTrackers.valueOf( getTrackerType() ) ) )
      {
        this.bugLinker = new XPlannerBugTrackLinker( this.trackerUrlPattern );
      }
      else
      {
        this.bugLinker = new SourceforgeBugTrackLinker( this.trackerUrlPattern );
      }
    }

    return this.bugLinker;
  }

  /**
   * Computes the URL to the issue in the bug tracker.
   * @param issue ghe id of the issue.
   * @return the URL to the issue in the bug tracker.
   */
  protected String doLinkIssue( String issue )
  {
    return getBugTrackLinker().getLinkUrlForBug( issue );
  }

  /**
   * Getter for the bug tracker type.
   * @return the bug tracker type.
   */
  protected String getTrackerType()
  {
    return this.trackerType.toLowerCase();
  }

  /**
   * Getter for the connection URL to the SCM.
   * @return the connection URL to the SCM.
   */
  public String getConnectionUrl()
  {
    boolean requireDeveloperConnection = !"connection".equals( connectionType.toLowerCase() );

    if ( StringUtils.isNotEmpty( connectionUrl ) 
        && !requireDeveloperConnection )
    {
      return connectionUrl;
    }
    else if ( StringUtils.isNotEmpty( developerConnectionUrl ) )
    {
      return developerConnectionUrl;
    }

    if ( requireDeveloperConnection )
    {
      throw new NullPointerException( this.bundle.getString(
          "error.developer.connection.url" ) );
    } 
    else
    {
      throw new NullPointerException( this.bundle.getString(
          "error.connection.url" ) );
    }
  }

  /**
   * Setter for the connection URL to the SCM.
   * @param connectionUrl the connection URL to the SCM.
   */
  public void setConnectionUrl( String connectionUrl )
  {
    this.connectionUrl = connectionUrl;
  }

  /**
   * Getter for the SCM Manager with our own SCM providers.
   * @return a SCM Manager with our own SCM providers.
   */
  public ScmManager getScmManager()
  {
    ScmAdapterFactory.registerProviders( manager, GrammarEnum.valueOf( grammar ),
            getLog() , getPattern() );
    return manager;
  }

  /**
   * Compile the filter into a pattern. If the filter is null or empty returns null.
   * @return thefilter compiled as a pattern. If the filter is null or empty returns null.
   */
  protected Pattern getPattern()
  {
    if ( this.filter != null && ! "".equals( this.filter ) )
    {
      return Pattern.compile( this.filter );
    }
    return  null;
  }

  /**
   * Build a SCM file set for a file in the SCM.
   * @param fileName the file/dir in the SCM.
   * @return the corresponding fileset.
   * @throws java.io.IOException in case of an I/O error.
   */
  public ScmFileSet getFileSet( String fileName )
      throws IOException
  {
    return new ScmFileSet( basedir, new File( fileName ) );
  }

  /**
   * Build a SCM file set for a file in the SCM.
   * @param includes include filters.
   * @param excludes exclude filters.
   * @return the corresponding fileset.
   * @throws java.io.IOException in case of an I/O error.
   */
  public ScmFileSet getFileSet( String includes, String excludes )
      throws IOException
  {
    if ( ( includes != null ) || ( excludes != null ) )
    {
      return new ScmFileSet( basedir, includes, excludes );
    }
    else
    {
      return new ScmFileSet( basedir );
    }
  }

  /**
   * Getter for the ScmRepository.
   * @return the ScmRepository.
   * @throws org.apache.maven.scm.ScmException in case of an error in creating the ScmRepository.
   */
  public ScmRepository getScmRepository()
      throws ScmException
  {
    ScmRepository repository;

    try
    {
      repository = getScmManager().makeScmRepository( getConnectionUrl() );

      ScmProviderRepository providerRepo = repository.getProviderRepository();

      if ( !StringUtils.isEmpty( username ) )
      {
        providerRepo.setUser( username );
      }

      if ( !StringUtils.isEmpty( password ) )
      {
        providerRepo.setPassword( password );
      }

      if ( repository.getProviderRepository() instanceof ScmProviderRepositoryWithHost )
      {
        ScmProviderRepositoryWithHost repo = ( ScmProviderRepositoryWithHost ) repository.getProviderRepository();
        loadInfosFromSettings( repo );

        if ( !StringUtils.isEmpty( username ) )
        {
          repo.setUser( username );
        }

        if ( !StringUtils.isEmpty( password ) )
        {
          repo.setPassword( password );
        }

        if ( !StringUtils.isEmpty( privateKey ) )
        {
          repo.setPrivateKey( privateKey );
        }

        if ( !StringUtils.isEmpty( passphrase ) )
        {
          repo.setPassphrase( passphrase );
        }
      }
      ScmAdapterFactory.setTagBase( repository , tagBase );
    }
    catch ( ScmRepositoryException e )
    {
      if ( !e.getValidationMessages().isEmpty() )
      {
        Iterator i = e.getValidationMessages().iterator();
        while ( i.hasNext() )
        {
          String message = ( String ) i.next();
          getLog().error( message );
        }
      }
      throw new ScmException( this.bundle.getString( "error.scm.provider" ), e );
    }
    catch ( Exception e )
    {
      getLog().error( e );
      throw new ScmException( this.bundle.getString( "error.scm.provider" ), e );
    }
    return repository;
  }

  /**
   * Load username password from settings if user has not set them in JVM properties.
   * @param repo the Scm repository provider.
   */
  private void loadInfosFromSettings( ScmProviderRepositoryWithHost repo )
  {
    if ( ( username == null ) || ( password == null ) )
    {
      String host = repo.getHost();
      int port = repo.getPort();

      if ( port > 0 )
      {
        host += ( this.bundle.getString( ":" ) + port );
      }

      Server server = this.settings.getServer( host );

      if ( server != null )
      {
        if ( username == null )
        {
          username = server.getUsername();
        }

        if ( password == null )
        {
          password = server.getPassword();
        }

        if ( privateKey == null )
        {
          privateKey = server.getPrivateKey();
        }

        if ( passphrase == null )
        {
          passphrase = server.getPassphrase();
        }
      }
    }
  }

  /**
   * Manages the result of the SCM command.
   * @param result the result of the SCM command.
   * @throws org.apache.maven.plugin.MojoExecutionException in case of an error executing the Mojo.
   */
  public void checkResult( ScmResult result )
      throws MojoExecutionException
  {
    if ( !result.isSuccess() )
    {
      getLog().error( this.bundle.getString( "error.provider" ) );
      getLog().error( ( result.getProviderMessage() == null ) ? "" : result.getProviderMessage() );
      getLog().error( this.bundle.getString( "command.output" ) );
      getLog().error( ( result.getCommandOutput() == null ) ? "" : result.getCommandOutput() );
      throw new MojoExecutionException( this.bundle.getString( "error.command" ) 
          + StringUtils.defaultString( result.getProviderMessage() ) );
    }
  }

    /**
     * @see org.apache.maven.plugin.Mojo#setLog(org.apache.maven.plugin.logging.Log)
     */
    public void setLog( Log log )
    {
      if ( isColorized() )
      {
        super.setLog( new ColorConsoleLogger( log ) );
        logHasBeenColorized = true;
      }
      else
      {
        super.setLog( log );
      }
    }

    /**
     * Returns the logger that has been injected into this mojo. If no logger has
     * been setup yet, a <code>SystemStreamLog</code>
     * logger will be created and returned.
     * <br/><br/>
     * <strong>Note:</strong>
     * The logger returned by this method must not be cached in an instance field during the construction of the mojo.
     * This would cause the mojo to use a wrongly configured default logger when being run by Maven. The proper logger
     * gets injected by the Plexus container <em>after</em> the mojo has been constructed. Therefore, simply call this
     * method directly whenever you need the logger, it is fast enough and needs no caching.
     *
     * @see org.apache.maven.plugin.Mojo#getLog()
     */
    public Log getLog()
    {
      Log log = super.getLog();
      if ( ! logHasBeenColorized && isColorized() )
      {
        log = new ColorConsoleLogger( log );
        super.setLog( log );
        logHasBeenColorized = true;
      }
      return log;
    }

    /**
     * Indicates if the logs will be in ANSI color.
     * @return true if System property <i>colorized.cosole</i> is set - false otherwise.
     */
    private boolean isColorized()
    {
      return System.getProperty( "colorized.console" ) != null;
    }
}
