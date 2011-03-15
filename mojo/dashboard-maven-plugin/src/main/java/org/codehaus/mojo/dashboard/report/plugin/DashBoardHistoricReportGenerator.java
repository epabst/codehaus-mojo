package org.codehaus.mojo.dashboard.report.plugin;

/*
 *  Copyright 2007 David Vicente
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;
import org.hibernate.Query;

import org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.CloverReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.CoberturaReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.CpdReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.DashBoardMavenProject;
import org.codehaus.mojo.dashboard.report.plugin.beans.FindBugsReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.IDashBoardReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.PmdReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.SurefireReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.TagListReportBean;
import org.codehaus.mojo.dashboard.report.plugin.chart.IChartRenderer;
import org.codehaus.mojo.dashboard.report.plugin.chart.time.CheckstyleTimeChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.time.CloverTimeChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.time.CoberturaTimeChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.time.CpdTimeChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.time.FindBugsTimeChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.time.MarkerTimeChartDecorator;
import org.codehaus.mojo.dashboard.report.plugin.chart.time.PmdTimeChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.time.SurefirePercentAxisDecorator;
import org.codehaus.mojo.dashboard.report.plugin.chart.time.SurefireTimeChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.time.TaglistTimeChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.time.TimeChartRenderer;
import org.codehaus.mojo.dashboard.report.plugin.configuration.Configuration;
import org.codehaus.mojo.dashboard.report.plugin.configuration.Graph;
import org.codehaus.mojo.dashboard.report.plugin.configuration.Section;
import org.codehaus.mojo.dashboard.report.plugin.hibernate.HibernateService;

/**
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 */
public class DashBoardHistoricReportGenerator extends AbstractDashBoardGenerator
{
    /**
     *
     */
    private final DashBoardMavenProject mavenProject;

    private final Map map = new Hashtable();

    private final HibernateService hibernateService;

    private Long dashBoardMavenProjectID;

    private final Configuration configuration;

    private boolean keepVersionAsDiscriminantCriteria = true;

    private boolean generateGraphs = true;

    /**
     * Date format used to parse or format a date for the period.
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

    public DashBoardHistoricReportGenerator( DashBoardMavenProject mavenProject, HibernateService hibernateService,
                                             Configuration configuration, boolean keepVersionAsDiscriminantCriteria,
                                             boolean generateGraphs, Log log )
    {
        super( log );
        this.mavenProject = mavenProject;
        this.hibernateService = hibernateService;
        this.configuration = configuration;
        this.keepVersionAsDiscriminantCriteria = keepVersionAsDiscriminantCriteria;
        this.generateGraphs = generateGraphs;
        Set reports = mavenProject.getReports();
        Iterator iter = reports.iterator();
        while ( iter.hasNext() )
        {
            IDashBoardReportBean report = (IDashBoardReportBean) iter.next();
            if ( report != null )
            {
                this.map.put( report.getClass(), report );
            }
        }
        String queryStr = "";
        if ( keepVersionAsDiscriminantCriteria )
        {
            queryStr =
                "org.codehaus.mojo.dashboard.report.plugin.beans.DashBoardMavenProject.getDashBoardMavenProjectID";
        }
        else
        {
            queryStr =
                "org.codehaus.mojo.dashboard.report.plugin.beans.DashBoardMavenProject.getDashBoardMavenProjectIDWithoutVersion";
        }
        Query query = hibernateService.getSession().getNamedQuery( queryStr );
        query.setParameter( "artifactid", mavenProject.getArtifactId() );
        query.setParameter( "groupid", mavenProject.getGroupId() );
        if ( keepVersionAsDiscriminantCriteria )
        {
            query.setParameter( "version", mavenProject.getVersion() );
        }
        List result = query.list();
        if ( result != null && !result.isEmpty() )
        {
            this.dashBoardMavenProjectID = (Long) ( result.get( 0 ) );
        }

    }

    public void doGenerateReport( ResourceBundle bundle, Sink sink )
    {

        this.createTitle( bundle, sink );
        addDashboardCss(sink);
        sink.body();
        sink.anchor( "top" );
        sink.anchor_();
        this.createHeader( bundle, sink );

        if ( this.map.get( CoberturaReportBean.class ) != null )
        {
            this.createCoberturaSection( bundle, sink, (CoberturaReportBean) this.map.get( CoberturaReportBean.class ) );
        }
        if ( this.map.get( CloverReportBean.class ) != null )
        {
            this.createCloverSection( bundle, sink, (CloverReportBean) this.map.get( CloverReportBean.class ) );
        }
        if ( this.map.get( SurefireReportBean.class ) != null )
        {
            this.createSurefireSection( bundle, sink, (SurefireReportBean) this.map.get( SurefireReportBean.class ) );
        }
        if ( this.map.get( CheckstyleReportBean.class ) != null )
        {
            this.createCheckStyleSection( bundle, sink,
                                          (CheckstyleReportBean) this.map.get( CheckstyleReportBean.class ) );
        }
        if ( this.map.get( PmdReportBean.class ) != null )
        {
            this.createPmdSection( bundle, sink, (PmdReportBean) this.map.get( PmdReportBean.class ) );
        }
        if ( this.map.get( CpdReportBean.class ) != null )
        {
            this.createCpdSection( bundle, sink, (CpdReportBean) this.map.get( CpdReportBean.class ) );
        }
        if ( this.map.get( FindBugsReportBean.class ) != null )
        {
            this.createFindBugsSection( bundle, sink, (FindBugsReportBean) this.map.get( FindBugsReportBean.class ) );
        }
        if ( this.map.get( TagListReportBean.class ) != null )
        {
            this.createTaglistSection( bundle, sink, (TagListReportBean) this.map.get( TagListReportBean.class ) );
        }

        sink.body_();

        sink.flush();

        sink.close();
    }

    public void createTitle( ResourceBundle bundle, Sink sink )
    {
        sink.head();
        sink.title();
        sink.text( bundle.getString( "dashboard.report.name" ) );
        sink.title_();
        sink.head_();
    }

    public void createHeader( ResourceBundle bundle, Sink sink )
    {
        sink.section1();

        sink.sectionTitle1();
        sink.text( bundle.getString( "dashboard.report.name" ) );
        sink.sectionTitle1_();

        sink.text( "Date Generated: " + new SimpleDateFormat().format( new Date( System.currentTimeMillis() ) ) );
        sink.horizontalRule();

        sink.sectionTitle3();
        sink.text( "[" );
        sink.link( "dashboard-report.html" );
        sink.text( "Summary Dashboard" );
        sink.link_();
        sink.text( "]" );
        sink.sectionTitle3_();
        sink.horizontalRule();

        if ( this.map.get( CoberturaReportBean.class ) != null )
        {
            sink.text( "[" );
            sink.link( "#cobertura" );
            sink.text( bundle.getString( "report.cobertura.header" ) );
            sink.link_();
            sink.text( "]" );
            sink.lineBreak();
        }
        if ( this.map.get( CloverReportBean.class ) != null )
        {
            sink.text( "[" );
            sink.link( "#clover" );
            sink.text( bundle.getString( "report.clover.header" ) );
            sink.link_();
            sink.text( "]" );
            sink.lineBreak();
        }
        if ( this.map.get( SurefireReportBean.class ) != null )
        {
            sink.text( "[" );
            sink.link( "#surefire" );
            sink.text( bundle.getString( "report.surefire.header" ) );
            sink.link_();
            sink.text( "]" );
            sink.lineBreak();
        }
        if ( this.map.get( CheckstyleReportBean.class ) != null )
        {
            sink.text( "[" );
            sink.link( "#checkstyle" );
            sink.text( bundle.getString( "report.checkstyle.header" ) );
            sink.link_();
            sink.text( "]" );
            sink.lineBreak();
        }
        if ( this.map.get( PmdReportBean.class ) != null )
        {
            sink.text( "[" );
            sink.link( "#pmd" );
            sink.text( bundle.getString( "report.pmd.header" ) );
            sink.link_();
            sink.text( "]" );
            sink.lineBreak();
        }
        if ( this.map.get( CpdReportBean.class ) != null )
        {
            sink.text( "[" );
            sink.link( "#cpd" );
            sink.text( bundle.getString( "report.cpd.header" ) );
            sink.link_();
            sink.text( "]" );
            sink.lineBreak();
        }
        if ( this.map.get( FindBugsReportBean.class ) != null )
        {
            sink.text( "[" );
            sink.link( "#findbugs" );
            sink.text( bundle.getString( "report.findbugs.header" ) );
            sink.link_();
            sink.text( "]" );
            sink.lineBreak();
        }
        if ( this.map.get( TagListReportBean.class ) != null )
        {
            sink.text( "[" );
            sink.link( "#taglist" );
            sink.text( bundle.getString( "report.taglist.header" ) );
            sink.link_();
            sink.text( "]" );
            sink.lineBreak();
        }
        /*
         * if ( map.get( JDependReportBean.class ) != null ) { sink.text( "[" ); sink.link( "#jdepend" ); sink.text(
         * bundle.getString( "report.xrefpackage.header" ) ); sink.link_(); sink.text( "]" ); sink.lineBreak(); }
         */

        sink.horizontalRule();
        sink.lineBreak();
        if ( !this.generateGraphs )
        {
            sink.text( "[" + bundle.getString( "dashboard.report.generategraphs.disabled" ) + "]" );
            sink.lineBreak();
        }
        sink.section1_();
    }

    public void createSurefireSection( ResourceBundle bundle, Sink sink, SurefireReportBean report )
    {

        sink.section1();

        sink.sectionTitle2();
        sink.anchor( "surefire" );
        sink.anchor_();
        sink.link( "dashboard-report.html#surefire" );
        sink.text( bundle.getString( "report.surefire.header" ) );
        sink.link_();

        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.section1_();
        sink.lineBreak();

        if ( report == null )
        {
            sink.text( "Error: Unable to generate Surefire historic graphs." );
        }
        else
        {
            Section section = this.configuration.getSectionById( "surefire.summary" );
            if ( section == null )
            {
                sink.text( "Error: Unable to generate Surefire historic graphs." );
            }
            else
            {
                List graphs = section.getGraphs();

                Iterator iter = graphs.iterator();

                while ( iter.hasNext() )
                {
                    Graph graph = (Graph) iter.next();

                    String namedQuery = "";
                    if ( this.keepVersionAsDiscriminantCriteria )
                    {
                        namedQuery =
                            "org.codehaus.mojo.dashboard.report.plugin.beans.SurefireReportBean.getSurefireByPeriodByVersion";
                    }
                    else
                    {
                        namedQuery =
                            "org.codehaus.mojo.dashboard.report.plugin.beans.SurefireReportBean.getSurefireByPeriod";
                    }

                    Query query = this.hibernateService.getSession().getNamedQuery( namedQuery );

                    query.setParameter( "id", this.dashBoardMavenProjectID );
                    query.setParameter( "startdate", graph.getStartPeriodDate() );
                    query.setParameter( "enddate", graph.getEndPeriodDate() );
                    List result = query.list();
                    if ( !this.generateGraphs )
                    {
                        sink.sectionTitle3();
                        sink.text( this.mavenProject.getProjectName() + " : " + graph.getTitle() );
                        sink.sectionTitle3_();

                        if ( result != null && result.size() > 0 )
                        {
                            sink.table();
                            sink.tableRow();
                            sink.tableHeaderCell();
                            sink.text( "Date" );
                            sink.tableHeaderCell_();
                            sink.tableHeaderCell();
                            sink.text( bundle.getString( "report.surefire.label.successrate" ) );
                            sink.tableHeaderCell_();
                            sink.tableHeaderCell();
                            sink.text( bundle.getString( "report.surefire.label.tests" ) );
                            sink.tableHeaderCell_();
                            sink.tableHeaderCell();
                            sink.text( bundle.getString( "report.surefire.label.errors" ) );
                            sink.tableHeaderCell_();
                            sink.tableHeaderCell();
                            sink.text( bundle.getString( "report.surefire.label.failures" ) );
                            sink.tableHeaderCell_();
                            sink.tableHeaderCell();
                            sink.text( bundle.getString( "report.surefire.label.skipped" ) );
                            sink.tableHeaderCell_();
                            sink.tableRow_();

                            Iterator iterRes = result.iterator();
                            while ( iterRes.hasNext() )
                            {
                                sink.tableRow();
                                SurefireReportBean reportH = (SurefireReportBean) iterRes.next();
                                Date date = reportH.getDateGeneration();

                                this.sinkCell( sink, normalizeToString( date ) );
                                this.sinkCellPercentGraphic( sink, reportH.getSucessRate() / 100, "surefire" );
                                this.sinkCell( sink, Integer.toString( reportH.getNbTests() ) );
                                this.sinkCell( sink, Integer.toString( reportH.getNbErrors() ) );
                                this.sinkCell( sink, Integer.toString( reportH.getNbFailures() ) );
                                this.sinkCell( sink, Integer.toString( reportH.getNbSkipped() ) );
                            }
                            sink.table_();
                        }
                        else
                        {
                            sink.text( "[" + bundle.getString( "dashboard.report.generategraphs.nodata" ) + "]" );
                            sink.lineBreak();
                        }
                    }
                    else
                    {
                        IChartRenderer chart1 =
                            new TimeChartRenderer( new SurefireTimeChartStrategy( bundle,
                                                                                  this.mavenProject.getProjectName()
                                                                                                  + " : "
                                                                                                  + graph.getTitle(),
                                                                                  result, graph.getTimeUnit(),
                                                                                  graph.getStartPeriodDate(),
                                                                                  graph.getEndPeriodDate() ) );
                        chart1 = new SurefirePercentAxisDecorator( chart1, result );
                        if ( !this.keepVersionAsDiscriminantCriteria )
                        {
                            String versionQuery =
                                "org.codehaus.mojo.dashboard.report.plugin.beans.SurefireReportBean.getMarkerVersionByDate";
                            query = this.hibernateService.getSession().getNamedQuery( versionQuery );
                            query.setParameter( "id", this.dashBoardMavenProjectID );
                            query.setParameter( "startdate", graph.getStartPeriodDate() );
                            query.setParameter( "enddate", graph.getEndPeriodDate() );
                            result = query.list();
                            chart1 = new MarkerTimeChartDecorator( chart1, result );
                        }

                        if ( !chart1.isEmpty() )
                        {
                            String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                            filename += "_Histo_Surefire." + chart1.getFileExtension();
                            String prefix = graph.getId();
                            filename = prefix.replace( '.', '_' ) + filename;
                            filename = filename.replace( ' ', '_' );
                            String filenameWithPath = this.getImagesPath() + "/" + filename;
                            this.getLog().debug( "createHistoSurefireGraph = " + filename );
                            try
                            {
                                chart1.saveToFile( filenameWithPath );
                                String link = "images/" + filename;
                                link = link.replace( ' ', '_' );
                                sink.figure();
                                sink.figureGraphics( link );
                                sink.figure_();
                            }
                            catch ( IOException e )
                            {
                                this.getLog().debug( "createHistoSurefireGraph exception = " + e.getMessage() );
                            }
                        }
                        sink.lineBreak();
                    }
                    sink.lineBreak();
                }
            }
        }

    }

    /**
     * Fixes MOJO-813. addition of Clover support. written by <a href="mailto:mbeerman@yahoo.com">Matthew Beermann</a>
     *
     * @param bundle
     * @param sink
     */
    public void createCloverSection( ResourceBundle bundle, Sink sink, CloverReportBean report )
    {
        sink.section1();

        sink.sectionTitle2();
        sink.anchor( "clover" );
        sink.anchor_();
        sink.link( "dashboard-report.html#clover" );
        sink.text( bundle.getString( "report.clover.header" ) );
        sink.link_();

        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.section1_();
        sink.lineBreak();

        if ( report == null )
        {
            sink.text( "Error: Unable to generate Clover historic graphs." );
        }
        else
        {
            Section section = this.configuration.getSectionById( "clover.summary" );
            if ( section == null )
            {
                sink.text( "Error: Unable to generate Clover historic graphs." );
            }
            else
            {
                List graphs = section.getGraphs();

                Iterator iter = graphs.iterator();

                while ( iter.hasNext() )
                {
                    Graph graph = (Graph) iter.next();

                    String namedQuery = "";
                    if ( this.keepVersionAsDiscriminantCriteria )
                    {
                        namedQuery =
                            "org.codehaus.mojo.dashboard.report.plugin.beans.CloverReportBean.getCloverByPeriodByVersion";
                    }
                    else
                    {
                        namedQuery =
                            "org.codehaus.mojo.dashboard.report.plugin.beans.CloverReportBean.getCloverByPeriod";
                    }

                    Query query = this.hibernateService.getSession().getNamedQuery( namedQuery );
                    query.setParameter( "id", this.dashBoardMavenProjectID );
                    query.setParameter( "startdate", graph.getStartPeriodDate() );
                    query.setParameter( "enddate", graph.getEndPeriodDate() );
                    List result = query.list();
                    if ( !this.generateGraphs )
                    {
                        sink.sectionTitle3();
                        sink.text( this.mavenProject.getProjectName() + " : " + graph.getTitle() );
                        sink.sectionTitle3_();

                        if ( result != null && result.size() > 0 )
                        {
                            sink.table();
                            sink.tableRow();
                            sink.tableHeaderCell();
                            sink.text( "Date" );
                            sink.tableHeaderCell_();

                            sink.tableHeaderCell();
                            sink.text( bundle.getString( "report.clover.label.total" ) );
                            sink.tableHeaderCell_();
                            sink.tableHeaderCell();
                            sink.text( bundle.getString( "report.clover.label.conditionals" ) );
                            sink.tableHeaderCell_();
                            sink.tableHeaderCell();
                            sink.text( bundle.getString( "report.clover.label.statements" ) );
                            sink.tableHeaderCell_();
                            sink.tableHeaderCell();
                            sink.text( bundle.getString( "report.clover.label.methods" ) );
                            sink.tableHeaderCell_();

                            sink.tableRow_();

                            Iterator iterRes = result.iterator();
                            while ( iterRes.hasNext() )
                            {
                                sink.tableRow();
                                CloverReportBean clover = (CloverReportBean) iterRes.next();
                                Date date = clover.getDateGeneration();

                                this.sinkCell( sink, normalizeToString( date ) );

                                this.sinkCellPercentGraphic( sink, clover.getPercentCoveredElements(), "clover" );

                                this.sinkCell( sink, getPercentValue( clover.getPercentCoveredConditionals() ) );
                                this.sinkCell( sink, getPercentValue( clover.getPercentCoveredStatements() ) );
                                this.sinkCell( sink, getPercentValue( clover.getPercentCoveredMethods() ) );

                            }
                            sink.table_();
                        }
                        else
                        {
                            sink.text( "[" + bundle.getString( "dashboard.report.generategraphs.nodata" ) + "]" );
                            sink.lineBreak();
                        }
                    }
                    else
                    {
                        IChartRenderer chart1 =
                            new TimeChartRenderer( new CloverTimeChartStrategy( bundle,
                                                                                this.mavenProject.getProjectName()
                                                                                                + " : "
                                                                                                + graph.getTitle(),
                                                                                result, graph.getTimeUnit(),
                                                                                graph.getStartPeriodDate(),
                                                                                graph.getEndPeriodDate() ) );
                        if ( !this.keepVersionAsDiscriminantCriteria )
                        {
                            String versionQuery =
                                "org.codehaus.mojo.dashboard.report.plugin.beans.CloverReportBean.getMarkerVersionByDate";
                            query = this.hibernateService.getSession().getNamedQuery( versionQuery );
                            query.setParameter( "id", this.dashBoardMavenProjectID );
                            query.setParameter( "startdate", graph.getStartPeriodDate() );
                            query.setParameter( "enddate", graph.getEndPeriodDate() );
                            result = query.list();
                            chart1 = new MarkerTimeChartDecorator( chart1, result );
                        }
                        if ( !chart1.isEmpty() )
                        {
                            String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                            filename += "_Histo_Clover." + chart1.getFileExtension();
                            String prefix = graph.getId();
                            filename = prefix.replace( '.', '_' ) + filename;
                            filename = filename.replace( ' ', '_' );
                            String filenameWithPath = this.getImagesPath() + "/" + filename;
                            this.getLog().debug( "createHistoCloverGraph = " + filename );
                            try
                            {
                                chart1.saveToFile( filenameWithPath );
                                String link = "images/" + filename;
                                link = link.replace( ' ', '_' );
                                sink.figure();
                                sink.figureGraphics( link );
                                sink.figure_();
                            }
                            catch ( IOException e )
                            {
                                this.getLog().debug( "createHistoCloverGraph exception = " + e.getMessage() );
                            }
                        }
                        sink.lineBreak();
                    }
                }
                sink.lineBreak();
            }
        }

        sink.lineBreak();

    }

    public void createCoberturaSection( ResourceBundle bundle, Sink sink, CoberturaReportBean report )
    {

        sink.section1();

        sink.sectionTitle2();
        sink.anchor( "cobertura" );
        sink.anchor_();
        sink.link( "dashboard-report.html#cobertura" );
        sink.text( bundle.getString( "report.cobertura.header" ) );
        sink.link_();

        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.section1_();
        sink.lineBreak();
        if ( report == null )
        {
            sink.text( "Error: Unable to generate Cobertura historic graphs." );
        }
        else
        {
            Section section = this.configuration.getSectionById( "cobertura.summary" );
            if ( section == null )
            {
                sink.text( "Error: Unable to generate Cobertura historic graphs." );
            }
            else
            {
                List graphs = section.getGraphs();

                Iterator iter = graphs.iterator();

                while ( iter.hasNext() )
                {
                    Graph graph = (Graph) iter.next();

                    String namedQuery = "";
                    if ( this.keepVersionAsDiscriminantCriteria )
                    {
                        namedQuery =
                            "org.codehaus.mojo.dashboard.report.plugin.beans.CoberturaReportBean.getCoberturaByPeriodByVersion";
                    }
                    else
                    {
                        namedQuery =
                            "org.codehaus.mojo.dashboard.report.plugin.beans.CoberturaReportBean.getCoberturaByPeriod";
                    }

                    Query query = this.hibernateService.getSession().getNamedQuery( namedQuery );
                    query.setParameter( "id", this.dashBoardMavenProjectID );
                    query.setParameter( "startdate", graph.getStartPeriodDate() );
                    query.setParameter( "enddate", graph.getEndPeriodDate() );
                    List result = query.list();

                    if ( !this.generateGraphs )
                    {
                        sink.sectionTitle3();
                        sink.text( this.mavenProject.getProjectName() + " : " + graph.getTitle() );
                        sink.sectionTitle3_();

                        if ( result != null && result.size() > 0 )
                        {
                            sink.table();
                            sink.tableRow();

                            sinkHeader( sink, "Date" );
                            sinkHeader( sink, bundle.getString( "report.cobertura.label.nbclasses" ) );
                            sinkHeader( sink, bundle.getString( "report.cobertura.label.linecover" ) );
                            sinkHeader( sink, bundle.getString( "report.cobertura.label.branchcover" ) );

                            sink.tableRow_();

                            Iterator iterRes = result.iterator();
                            while ( iterRes.hasNext() )
                            {
                                CoberturaReportBean cober = (CoberturaReportBean) iterRes.next();
                                Date date = cober.getDateGeneration();

                                sink.tableRow();
                                this.sinkCell( sink, normalizeToString( date ) );
                                this.sinkCell( sink, Integer.toString( cober.getNbClasses() ) );
                                sinkCellPercentGraphic( sink, cober.getLineCoverRate(), "cobertura" );
                                sinkCellPercentGraphic( sink, cober.getBranchCoverRate(), "cobertura" );
                                sink.tableRow_();
                            }
                            sink.table_();
                        }
                        else
                        {
                            sink.text( "[" + bundle.getString( "dashboard.report.generategraphs.nodata" ) + "]" );
                            sink.lineBreak();
                        }
                    }
                    else
                    {
                        IChartRenderer chart1 =
                            new TimeChartRenderer( new CoberturaTimeChartStrategy( bundle,
                                                                                   this.mavenProject.getProjectName()
                                                                                                   + " : "
                                                                                                   + graph.getTitle(),
                                                                                   result, graph.getTimeUnit(),
                                                                                   graph.getStartPeriodDate(),
                                                                                   graph.getEndPeriodDate() ) );
                        if ( !this.keepVersionAsDiscriminantCriteria )
                        {
                            String versionQuery =
                                "org.codehaus.mojo.dashboard.report.plugin.beans.CoberturaReportBean.getMarkerVersionByDate";
                            query = this.hibernateService.getSession().getNamedQuery( versionQuery );
                            query.setParameter( "id", this.dashBoardMavenProjectID );
                            query.setParameter( "startdate", graph.getStartPeriodDate() );
                            query.setParameter( "enddate", graph.getEndPeriodDate() );
                            result = query.list();
                            chart1 = new MarkerTimeChartDecorator( chart1, result );
                        }

                        if ( !chart1.isEmpty() )
                        {
                            String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                            filename += "_Histo_Cobertura." + chart1.getFileExtension();
                            String prefix = graph.getId();
                            filename = prefix.replace( '.', '_' ) + filename;
                            filename = filename.replace( ' ', '_' );
                            String filenameWithPath = this.getImagesPath() + "/" + filename;
                            this.getLog().debug( "createHistoCoberturaGraph = " + filename );
                            try
                            {
                                chart1.saveToFile( filenameWithPath );
                                String link = "images/" + filename;
                                link = link.replace( ' ', '_' );
                                sink.figure();
                                sink.figureGraphics( link );
                                sink.figure_();
                            }
                            catch ( IOException e )
                            {
                                this.getLog().debug( "createHistoCoberturaGraph exception = " + e.getMessage() );
                            }
                        }
                        sink.lineBreak();
                    }
                    sink.lineBreak();
                }
            }
        }
        sink.lineBreak();

    }

    public void createPmdSection( ResourceBundle bundle, Sink sink, PmdReportBean report )
    {

        sink.section1();

        sink.sectionTitle2();
        sink.anchor( "pmd" );
        sink.anchor_();
        sink.link( "dashboard-report.html#pmd" );
        sink.text( bundle.getString( "report.pmd.header" ) );
        sink.link_();
        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.section1_();
        sink.lineBreak();
        if ( report == null )
        {
            sink.text( "Error: Unable to generate PMD historic graphs." );
        }
        else
        {
            Section section = this.configuration.getSectionById( "pmd.summary" );
            if ( section == null )
            {
                sink.text( "Error: Unable to generate PMD historic graphs." );
            }
            else
            {
                List graphs = section.getGraphs();

                Iterator iter = graphs.iterator();

                while ( iter.hasNext() )
                {
                    Graph graph = (Graph) iter.next();

                    String namedQuery = "";
                    if ( this.keepVersionAsDiscriminantCriteria )
                    {
                        namedQuery =
                            "org.codehaus.mojo.dashboard.report.plugin.beans.PmdReportBean.getPmdByPeriodByVersion";
                    }
                    else
                    {
                        namedQuery = "org.codehaus.mojo.dashboard.report.plugin.beans.PmdReportBean.getPmdByPeriod";
                    }

                    Query query = this.hibernateService.getSession().getNamedQuery( namedQuery );
                    query.setParameter( "id", this.dashBoardMavenProjectID );
                    query.setParameter( "startdate", graph.getStartPeriodDate() );
                    query.setParameter( "enddate", graph.getEndPeriodDate() );
                    List result = query.list();
                    if ( !this.generateGraphs )
                    {
                        sink.sectionTitle3();
                        sink.text( this.mavenProject.getProjectName() + " : " + graph.getTitle() );
                        sink.sectionTitle3_();

                        if ( result != null && result.size() > 0 )
                        {
                            sink.table();
                            sink.tableRow();
                            sink.tableHeaderCell();
                            sink.text( "Date" );
                            sink.tableHeaderCell_();

                            this.sinkHeader( sink, bundle.getString( "report.pmd.label.nbclasses" ) );

                            this.sinkHeader( sink, bundle.getString( "report.pmd.label.nbviolations" ) );

                            sink.tableRow_();

                            Iterator iterRes = result.iterator();
                            while ( iterRes.hasNext() )
                            {
                                sink.tableRow();
                                PmdReportBean pmd = (PmdReportBean) iterRes.next();
                                Date date = pmd.getDateGeneration();

                                this.sinkCell( sink, normalizeToString( date ) );
                                this.sinkCell( sink, Integer.toString( pmd.getNbClasses() ) );
                                this.sinkCell( sink, Integer.toString( pmd.getNbViolations() ) );

                            }
                            sink.table_();
                        }
                        else
                        {
                            sink.text( "[" + bundle.getString( "dashboard.report.generategraphs.nodata" ) + "]" );
                            sink.lineBreak();
                        }
                    }
                    else
                    {
                        IChartRenderer chart1 =
                            new TimeChartRenderer( new PmdTimeChartStrategy( bundle, this.mavenProject.getProjectName()
                                            + " : " + graph.getTitle(), result, graph.getTimeUnit(),
                                                                             graph.getStartPeriodDate(),
                                                                             graph.getEndPeriodDate() ) );
                        if ( !this.keepVersionAsDiscriminantCriteria )
                        {
                            String versionQuery =
                                "org.codehaus.mojo.dashboard.report.plugin.beans.PmdReportBean.getMarkerVersionByDate";
                            query = this.hibernateService.getSession().getNamedQuery( versionQuery );
                            query.setParameter( "id", this.dashBoardMavenProjectID );
                            query.setParameter( "startdate", graph.getStartPeriodDate() );
                            query.setParameter( "enddate", graph.getEndPeriodDate() );
                            result = query.list();
                            chart1 = new MarkerTimeChartDecorator( chart1, result );
                        }
                        if ( !chart1.isEmpty() )
                        {
                            String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                            filename += "_Histo_Pmd." + chart1.getFileExtension();
                            String prefix = graph.getId();
                            filename = prefix.replace( '.', '_' ) + filename;
                            filename = filename.replace( ' ', '_' );
                            String filenameWithPath = this.getImagesPath() + "/" + filename;
                            this.getLog().debug( "createHistoPmdGraph = " + filename );
                            try
                            {
                                chart1.saveToFile( filenameWithPath );
                                String link = "images/" + filename;
                                link = link.replace( ' ', '_' );
                                sink.figure();
                                sink.figureGraphics( link );
                                sink.figure_();
                            }
                            catch ( IOException e )
                            {
                                this.getLog().debug( "createHistoPmdGraph exception = " + e.getMessage() );
                            }
                        }
                        sink.lineBreak();
                    }
                    sink.lineBreak();
                }
            }
        }
        sink.lineBreak();
    }

    public void createCpdSection( ResourceBundle bundle, Sink sink, CpdReportBean report )
    {

        sink.section1();

        sink.sectionTitle2();
        sink.anchor( "cpd" );
        sink.anchor_();
        sink.link( "dashboard-report.html#cpd" );
        sink.text( bundle.getString( "report.cpd.header" ) );
        sink.link_();

        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.section1_();
        sink.lineBreak();
        if ( report == null )
        {
            sink.text( "Error: Unable to generate CPD historic graphs." );
        }
        else
        {
            Section section = this.configuration.getSectionById( "cpd.summary" );
            if ( section == null )
            {
                sink.text( "Error: Unable to generate CPD historic graphs." );
            }
            else
            {
                List graphs = section.getGraphs();

                Iterator iter = graphs.iterator();

                while ( iter.hasNext() )
                {
                    Graph graph = (Graph) iter.next();

                    String namedQuery = "";
                    if ( this.keepVersionAsDiscriminantCriteria )
                    {
                        namedQuery =
                            "org.codehaus.mojo.dashboard.report.plugin.beans.CpdReportBean.getCpdByPeriodByVersion";
                    }
                    else
                    {
                        namedQuery = "org.codehaus.mojo.dashboard.report.plugin.beans.CpdReportBean.getCpdByPeriod";
                    }

                    Query query = this.hibernateService.getSession().getNamedQuery( namedQuery );
                    query.setParameter( "id", this.dashBoardMavenProjectID );
                    query.setParameter( "startdate", graph.getStartPeriodDate() );
                    query.setParameter( "enddate", graph.getEndPeriodDate() );
                    List result = query.list();
                    if ( !this.generateGraphs )
                    {
                        sink.sectionTitle3();
                        sink.text( this.mavenProject.getProjectName() + " : " + graph.getTitle() );
                        sink.sectionTitle3_();

                        if ( result != null && result.size() > 0 )
                        {
                            sink.table();
                            sink.tableRow();
                            sink.tableHeaderCell();
                            sink.text( "Date" );
                            sink.tableHeaderCell_();

                            this.sinkHeader( sink, bundle.getString( "report.cpd.label.nbclasses" ) );

                            this.sinkHeader( sink, bundle.getString( "report.cpd.label.nbduplicate" ) );

                            sink.tableRow_();

                            Iterator iterRes = result.iterator();
                            while ( iterRes.hasNext() )
                            {
                                sink.tableRow();
                                CpdReportBean cpd = (CpdReportBean) iterRes.next();
                                Date date = cpd.getDateGeneration();

                                this.sinkCell( sink, normalizeToString( date ) );
                                this.sinkCell( sink, Integer.toString( cpd.getNbClasses() ) );
                                this.sinkCell( sink, Integer.toString( cpd.getNbDuplicate() ) );

                            }
                            sink.table_();
                        }
                        else
                        {
                            sink.text( "[" + bundle.getString( "dashboard.report.generategraphs.nodata" ) + "]" );
                            sink.lineBreak();
                        }
                    }
                    else
                    {
                        IChartRenderer chart1 =
                            new TimeChartRenderer( new CpdTimeChartStrategy( bundle, this.mavenProject.getProjectName()
                                            + " : " + graph.getTitle(), result, graph.getTimeUnit(),
                                                                             graph.getStartPeriodDate(),
                                                                             graph.getEndPeriodDate() ) );
                        if ( !this.keepVersionAsDiscriminantCriteria )
                        {
                            String versionQuery =
                                "org.codehaus.mojo.dashboard.report.plugin.beans.CpdReportBean.getMarkerVersionByDate";
                            query = this.hibernateService.getSession().getNamedQuery( versionQuery );
                            query.setParameter( "id", this.dashBoardMavenProjectID );
                            query.setParameter( "startdate", graph.getStartPeriodDate() );
                            query.setParameter( "enddate", graph.getEndPeriodDate() );
                            result = query.list();
                            chart1 = new MarkerTimeChartDecorator( chart1, result );
                        }
                        if ( !chart1.isEmpty() )
                        {
                            String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                            filename += "_Histo_Cpd." + chart1.getFileExtension();
                            String prefix = graph.getId();
                            filename = prefix.replace( '.', '_' ) + filename;
                            filename = filename.replace( ' ', '_' );
                            String filenameWithPath = this.getImagesPath() + "/" + filename;
                            this.getLog().debug( "createHistoCpdGraph = " + filename );
                            try
                            {
                                chart1.saveToFile( filenameWithPath );
                                String link = "images/" + filename;
                                link = link.replace( ' ', '_' );
                                sink.figure();
                                sink.figureGraphics( link );
                                sink.figure_();
                            }
                            catch ( IOException e )
                            {
                                this.getLog().debug( "createHistoCpdGraph exception = " + e.getMessage() );
                            }
                        }
                        sink.lineBreak();
                    }
                    sink.lineBreak();
                }
            }
        }
        sink.lineBreak();
    }

    public void createCheckStyleSection( ResourceBundle bundle, Sink sink, CheckstyleReportBean report )
    {

        sink.section1();
        sink.sectionTitle2();
        sink.anchor( "checkstyle" );
        sink.anchor_();
        sink.link( "dashboard-report.html#checkstyle" );
        sink.text( bundle.getString( "report.checkstyle.header" ) );
        sink.link_();

        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.section1_();
        if ( report == null )
        {
            sink.text( "Error: Unable to generate Checkstyle historic graphs." );
        }
        else
        {
            Section section = this.configuration.getSectionById( "checkstyle.summary" );
            if ( section == null )
            {
                sink.text( "Error: Unable to generate Checkstyle historic graphs." );
            }
            else
            {
                List graphs = section.getGraphs();

                Iterator iter = graphs.iterator();

                while ( iter.hasNext() )
                {
                    Graph graph = (Graph) iter.next();

                    String namedQuery = "";
                    if ( this.keepVersionAsDiscriminantCriteria )
                    {
                        namedQuery =
                            "org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleReportBean.getCheckstyleByPeriodByVersion";
                    }
                    else
                    {
                        namedQuery =
                            "org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleReportBean.getCheckstyleByPeriod";
                    }

                    Query query = this.hibernateService.getSession().getNamedQuery( namedQuery );
                    query.setParameter( "id", this.dashBoardMavenProjectID );
                    query.setParameter( "startdate", graph.getStartPeriodDate() );
                    query.setParameter( "enddate", graph.getEndPeriodDate() );
                    List result = query.list();
                    if ( !this.generateGraphs )
                    {
                        sink.sectionTitle3();
                        sink.text( this.mavenProject.getProjectName() + " : " + graph.getTitle() );
                        sink.sectionTitle3_();

                        if ( result != null && result.size() > 0 )
                        {
                            sink.table();
                            sink.tableRow();
                            sink.tableHeaderCell();
                            sink.text( "Date" );
                            sink.tableHeaderCell_();

                            sink.tableHeaderCell();
                            sink.text( bundle.getString( "report.checkstyle.files" ) );
                            sink.tableHeaderCell_();

                            sink.tableHeaderCell();
                            sink.text( bundle.getString( "report.checkstyle.column.total" ) );
                            sink.tableHeaderCell_();

                            sink.tableHeaderCell();
                            sink.text( bundle.getString( "report.checkstyle.column.infos" ) );
                            sink.nonBreakingSpace();
                            this.iconInfo( sink );
                            sink.tableHeaderCell_();

                            sink.tableHeaderCell();
                            sink.text( bundle.getString( "report.checkstyle.column.warnings" ) );
                            sink.nonBreakingSpace();
                            this.iconWarning( sink );
                            sink.tableHeaderCell_();

                            sink.tableHeaderCell();
                            sink.text( bundle.getString( "report.checkstyle.column.errors" ) );
                            sink.nonBreakingSpace();
                            this.iconError( sink );
                            sink.tableHeaderCell_();

                            sink.tableRow_();

                            Iterator iterRes = result.iterator();
                            while ( iterRes.hasNext() )
                            {
                                sink.tableRow();
                                CheckstyleReportBean check = (CheckstyleReportBean) iterRes.next();
                                Date date = check.getDateGeneration();

                                this.sinkCell( sink, normalizeToString( date ) );
                                this.sinkCell( sink, Integer.toString( check.getNbClasses() ) );
                                this.sinkCell( sink, Integer.toString( check.getNbTotal() ) );

                                sink.tableCell();
                                sink.text( Integer.toString( check.getNbInfos() ) );
                                sinkInvertPercentGraphic( sink, check.getPercentInfos() );
                                sink.tableCell_();
                                sink.tableCell();
                                sink.text( Integer.toString( check.getNbWarnings() ) );
                                sinkInvertPercentGraphic( sink, check.getPercentWarnings() );
                                sink.tableCell_();
                                sink.tableCell();
                                sink.text( Integer.toString( check.getNbErrors() ) );
                                sinkInvertPercentGraphic( sink, check.getPercentErrors() );
                                sink.tableCell_();

                            }
                            sink.table_();
                        }
                        else
                        {
                            sink.text( "[" + bundle.getString( "dashboard.report.generategraphs.nodata" ) + "]" );
                            sink.lineBreak();
                        }
                    }
                    else
                    {
                        IChartRenderer chart1 =
                            new TimeChartRenderer( new CheckstyleTimeChartStrategy( bundle,
                                                                                    this.mavenProject.getProjectName()
                                                                                                    + " : "
                                                                                                    + graph.getTitle(),
                                                                                    result, graph.getTimeUnit(),
                                                                                    graph.getStartPeriodDate(),
                                                                                    graph.getEndPeriodDate() ) );
                        if ( !this.keepVersionAsDiscriminantCriteria )
                        {
                            String versionQuery =
                                "org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleReportBean.getMarkerVersionByDate";
                            query = this.hibernateService.getSession().getNamedQuery( versionQuery );
                            query.setParameter( "id", this.dashBoardMavenProjectID );
                            query.setParameter( "startdate", graph.getStartPeriodDate() );
                            query.setParameter( "enddate", graph.getEndPeriodDate() );
                            result = query.list();
                            chart1 = new MarkerTimeChartDecorator( chart1, result );
                        }
                        if ( !chart1.isEmpty() )
                        {
                            String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                            filename += "_Histo_Checkstyle." + chart1.getFileExtension();
                            String prefix = graph.getId();
                            filename = prefix.replace( '.', '_' ) + filename;
                            filename = filename.replace( ' ', '_' );
                            String filenameWithPath = this.getImagesPath() + "/" + filename;
                            this.getLog().debug( "createHistoCheckstyleGraph = " + filename );
                            try
                            {
                                chart1.saveToFile( filenameWithPath );
                                String link = "images/" + filename;
                                link = link.replace( ' ', '_' );
                                sink.figure();
                                sink.figureGraphics( link );
                                sink.figure_();
                            }
                            catch ( IOException e )
                            {
                                this.getLog().debug( "createHistoCheckstyleGraph exception = " + e.getMessage() );
                            }
                        }
                        sink.lineBreak();
                    }
                    sink.lineBreak();
                }
            }
        }
        sink.lineBreak();

    }

    public void createFindBugsSection( ResourceBundle bundle, Sink sink, FindBugsReportBean report )
    {

        sink.section1();

        sink.sectionTitle2();
        sink.anchor( "findbugs" );
        sink.anchor_();
        sink.link( "dashboard-report.html#findbugs" );
        sink.text( bundle.getString( "report.findbugs.header" ) );
        sink.link_();

        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.section1_();
        sink.lineBreak();

        Section section = this.configuration.getSectionById( "findbugs.summary" );
        if ( report == null )
        {
            sink.text( "Error: Unable to generate FindBugs historic graphs." );
        }
        else
        {
            if ( section == null )
            {
                sink.text( "Error: Unable to generate FindBugs historic graphs." );
            }
            else
            {
                List graphs = section.getGraphs();

                Iterator iter = graphs.iterator();

                while ( iter.hasNext() )
                {
                    Graph graph = (Graph) iter.next();

                    String namedQuery = "";
                    if ( this.keepVersionAsDiscriminantCriteria )
                    {
                        namedQuery =
                            "org.codehaus.mojo.dashboard.report.plugin.beans.FindBugsReportBean.getFindBugsByPeriodByVersion";
                    }
                    else
                    {
                        namedQuery =
                            "org.codehaus.mojo.dashboard.report.plugin.beans.FindBugsReportBean.getFindBugsByPeriod";
                    }

                    Query query = this.hibernateService.getSession().getNamedQuery( namedQuery );
                    query.setParameter( "id", this.dashBoardMavenProjectID );
                    query.setParameter( "startdate", graph.getStartPeriodDate() );
                    query.setParameter( "enddate", graph.getEndPeriodDate() );
                    List result = query.list();
                    if ( !this.generateGraphs )
                    {
                        sink.sectionTitle3();
                        sink.text( this.mavenProject.getProjectName() + " : " + graph.getTitle() );
                        sink.sectionTitle3_();

                        if ( result != null && result.size() > 0 )
                        {
                            sink.table();
                            sink.tableRow();
                            this.sinkHeader( sink, "Date" );
                            this.sinkHeader( sink, bundle.getString( "report.findbugs.label.nbclasses" ) );
                            this.sinkHeader( sink, bundle.getString( "report.findbugs.label.nbbugs" ) );
                            this.sinkHeader( sink, bundle.getString( "report.findbugs.label.nberrors" ) );
                            this.sinkHeader( sink, bundle.getString( "report.findbugs.label.nbMissingClasses" ) );
                            sink.tableRow_();

                            Iterator iterRes = result.iterator();
                            while ( iterRes.hasNext() )
                            {
                                sink.tableRow();
                                FindBugsReportBean findbugs = (FindBugsReportBean) iterRes.next();
                                Date date = findbugs.getDateGeneration();

                                this.sinkCell( sink, normalizeToString( date ) );

                                this.sinkCell( sink, Integer.toString( findbugs.getNbClasses() ) );
                                this.sinkCell( sink, Integer.toString( findbugs.getNbBugs() ) );
                                this.sinkCell( sink, Integer.toString( findbugs.getNbErrors() ) );
                                this.sinkCell( sink, Integer.toString( findbugs.getNbMissingClasses() ) );

                            }
                            sink.table_();
                        }
                        else
                        {
                            sink.text( "[" + bundle.getString( "dashboard.report.generategraphs.nodata" ) + "]" );
                            sink.lineBreak();
                        }
                    }
                    else
                    {
                        IChartRenderer chart1 =
                            new TimeChartRenderer( new FindBugsTimeChartStrategy( bundle,
                                                                                  this.mavenProject.getProjectName()
                                                                                                  + " : "
                                                                                                  + graph.getTitle(),
                                                                                  result, graph.getTimeUnit(),
                                                                                  graph.getStartPeriodDate(),
                                                                                  graph.getEndPeriodDate() ) );
                        if ( !this.keepVersionAsDiscriminantCriteria )
                        {
                            String versionQuery =
                                "org.codehaus.mojo.dashboard.report.plugin.beans.FindBugsReportBean.getMarkerVersionByDate";
                            query = this.hibernateService.getSession().getNamedQuery( versionQuery );
                            query.setParameter( "id", this.dashBoardMavenProjectID );
                            query.setParameter( "startdate", graph.getStartPeriodDate() );
                            query.setParameter( "enddate", graph.getEndPeriodDate() );
                            result = query.list();
                            chart1 = new MarkerTimeChartDecorator( chart1, result );
                        }
                        if ( !chart1.isEmpty() )
                        {
                            String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                            filename += "_Histo_FindBugs." + chart1.getFileExtension();
                            String prefix = graph.getId();
                            filename = prefix.replace( '.', '_' ) + filename;
                            filename = filename.replace( ' ', '_' );
                            String filenameWithPath = this.getImagesPath() + "/" + filename;
                            this.getLog().debug( "createHistoFindBugsGraph = " + filename );
                            try
                            {
                                chart1.saveToFile( filenameWithPath );
                                String link = "images/" + filename;
                                link = link.replace( ' ', '_' );
                                sink.figure();
                                sink.figureGraphics( link );
                                sink.figure_();
                            }
                            catch ( IOException e )
                            {
                                this.getLog().debug( "createHistoFindBugsGraph exception = " + e.getMessage() );
                            }
                        }
                        sink.lineBreak();
                    }
                    sink.lineBreak();
                }
            }
        }
        sink.lineBreak();
    }

    public void createTaglistSection( ResourceBundle bundle, Sink sink, TagListReportBean report )
    {

        sink.section1();

        sink.sectionTitle2();
        sink.anchor( "taglist" );
        sink.anchor_();
        sink.link( "dashboard-report.html#taglist" );
        sink.text( bundle.getString( "report.taglist.header" ) );
        sink.link_();

        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.section1_();
        sink.lineBreak();
        if ( report == null )
        {
            sink.text( "Error: Unable to generate Taglist historic graphs." );
        }
        else
        {
            Section section = this.configuration.getSectionById( "taglist.summary" );
            if ( section == null )
            {
                sink.text( "Error: Unable to generate Taglist historic graphs." );
            }
            else
            {
                List graphs = section.getGraphs();

                Iterator iter = graphs.iterator();

                while ( iter.hasNext() )
                {
                    Graph graph = (Graph) iter.next();

                    String namedQuery = "";
                    if ( this.keepVersionAsDiscriminantCriteria )
                    {
                        namedQuery =
                            "org.codehaus.mojo.dashboard.report.plugin.beans.TagListReportBean.getTagListByPeriodByVersion";
                    }
                    else
                    {
                        namedQuery =
                            "org.codehaus.mojo.dashboard.report.plugin.beans.TagListReportBean.getTagListByPeriod";
                    }

                    Query query = this.hibernateService.getSession().getNamedQuery( namedQuery );
                    query.setParameter( "id", this.dashBoardMavenProjectID );
                    query.setParameter( "startdate", graph.getStartPeriodDate() );
                    query.setParameter( "enddate", graph.getEndPeriodDate() );
                    List result = query.list();
                    if ( !this.generateGraphs )
                    {
                        sink.sectionTitle3();
                        sink.text( this.mavenProject.getProjectName() + " : " + graph.getTitle() );
                        sink.sectionTitle3_();

                        if ( result != null && result.size() > 0 )
                        {
                            sink.table();
                            sink.tableRow();
                            this.sinkHeader( sink, "Date" );
                            this.sinkHeader( sink, bundle.getString( "report.taglist.label.nbclasses" ) );
                            this.sinkHeader( sink, bundle.getString( "report.taglist.column.nboccurs" ) );
                            sink.tableRow_();

                            Iterator iterRes = result.iterator();
                            while ( iterRes.hasNext() )
                            {
                                sink.tableRow();
                                TagListReportBean taglistReport = (TagListReportBean) iterRes.next();
                                Date date = taglistReport.getDateGeneration();

                                this.sinkCell( sink, normalizeToString( date ) );

                                this.sinkCell( sink, Integer.toString( taglistReport.getNbClasses() ) );
                                this.sinkCell( sink, Integer.toString( taglistReport.getNbTotal() ) );
                            }
                            sink.table_();
                        }
                        else
                        {
                            sink.text( "[" + bundle.getString( "dashboard.report.generategraphs.nodata" ) + "]" );
                            sink.lineBreak();
                        }
                    }
                    else
                    {
                        IChartRenderer chart1 =
                            new TimeChartRenderer( new TaglistTimeChartStrategy( bundle,
                                                                                 this.mavenProject.getProjectName()
                                                                                                 + " : "
                                                                                                 + graph.getTitle(),
                                                                                 result, graph.getTimeUnit(),
                                                                                 graph.getStartPeriodDate(),
                                                                                 graph.getEndPeriodDate() ) );
                        if ( !this.keepVersionAsDiscriminantCriteria )
                        {
                            String versionQuery =
                                "org.codehaus.mojo.dashboard.report.plugin.beans.TagListReportBean.getMarkerVersionByDate";
                            query = this.hibernateService.getSession().getNamedQuery( versionQuery );
                            query.setParameter( "id", this.dashBoardMavenProjectID );
                            query.setParameter( "startdate", graph.getStartPeriodDate() );
                            query.setParameter( "enddate", graph.getEndPeriodDate() );
                            result = query.list();
                            chart1 = new MarkerTimeChartDecorator( chart1, result );
                        }
                        if ( !chart1.isEmpty() )
                        {
                            String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                            filename += "_Histo_Taglist." + chart1.getFileExtension();
                            String prefix = graph.getId();
                            filename = prefix.replace( '.', '_' ) + filename;
                            filename = filename.replace( ' ', '_' );
                            String filenameWithPath = this.getImagesPath() + "/" + filename;
                            this.getLog().debug( "createHistoTaglistGraph = " + filename );
                            try
                            {
                                chart1.saveToFile( filenameWithPath );
                                String link = "images/" + filename;
                                link = link.replace( ' ', '_' );
                                sink.figure();
                                sink.figureGraphics( link );
                                sink.figure_();
                            }
                            catch ( IOException e )
                            {
                                this.getLog().debug( "createHistoTaglistGraph exception = " + e.getMessage() );
                            }
                        }
                        sink.lineBreak();
                    }
                    sink.lineBreak();
                }
            }
        }
        sink.lineBreak();
    }

    /**
     * Format the specified date to String.
     *
     * @param date
     *            Date to format
     * @return Formatted date.
     */
    public String normalizeToString( Date date )
    {
        return this.dateFormat.format( date );
    }
}