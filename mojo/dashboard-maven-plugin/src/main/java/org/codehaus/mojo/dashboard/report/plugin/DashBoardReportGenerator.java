package org.codehaus.mojo.dashboard.report.plugin;

/*
 * Copyright 2006 David Vicente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;

import org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleError;
import org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.CloverReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.CoberturaReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.CpdReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.DashBoardMavenProject;
import org.codehaus.mojo.dashboard.report.plugin.beans.FindBugsReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.IDashBoardReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.JDependReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.PmdReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.SurefireReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.TagListReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.XRefPackageBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.comparator.DescNbErrorCheckstyleComparator;
import org.codehaus.mojo.dashboard.report.plugin.chart.BarChartRenderer;
import org.codehaus.mojo.dashboard.report.plugin.chart.CheckstyleErrorsPieChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.CheckstylePieChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.CloverBarChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.CoberturaBarChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.FindbugsCategoriesPieChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.FindbugsPrioritiesPieChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.IChartRenderer;
import org.codehaus.mojo.dashboard.report.plugin.chart.PieChart3DRenderer;
import org.codehaus.mojo.dashboard.report.plugin.chart.PieChartRenderer;
import org.codehaus.mojo.dashboard.report.plugin.chart.SurefirePieChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.chart.TaglistPieChartStrategy;
import org.codehaus.mojo.dashboard.report.plugin.utils.ChartUtils;

/**
 * <ul>
 * <li>Add "CheckStyle Violations" graph written by <a href="mailto:srivollet@objectif-informatique.fr">Sylvain
 * Rivollet</a></li>
 * <li>Add Clover support written by <a href="mailto:mbeerman@yahoo.com">Matthew Beermann</a></li>
 * <li>Add Taglist support written by <a href="mailto:henrik.lynggaard@gmail.com">Henrik Lynggaard</a></li>
 * </ul>
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 */
public class DashBoardReportGenerator extends AbstractDashBoardGenerator
{
    /**
     *
     */
    private static final int SPECIFIC_WIDTH = 800;

    /**
     *
     */
    private final DashBoardMavenProject mavenProject;

    private final Map map = new Hashtable();

    private boolean isDBAvailable = false;

    private boolean generateGraphs = true;

    /**
     *
     */
    private boolean summary = false;

    public DashBoardReportGenerator( DashBoardMavenProject mavenProject, boolean summary, boolean isDBAvailable,
                                     boolean generateGraphs, Log log )
    {
        super( log );
        this.mavenProject = mavenProject;
        this.summary = summary;
        this.isDBAvailable = isDBAvailable;
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
        if ( this.map.get( JDependReportBean.class ) != null )
        {
            this.createJDependSection( bundle, sink, (JDependReportBean) this.map.get( JDependReportBean.class ) );
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

        if ( this.summary )
        {
            sink.sectionTitle3();
            sink.text( "[" );
            sink.link( "dashboard-report-details.html" );
            sink.text( "Detailed Dashboard" );
            sink.link_();
            sink.text( "]" );
            sink.sectionTitle3_();
            sink.horizontalRule();
        }

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
        if ( this.map.get( JDependReportBean.class ) != null )
        {
            sink.text( "[" );
            sink.link( "#jdepend" );
            sink.text( bundle.getString( "report.xrefpackage.header" ) );
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
        if ( this.summary )
        {
            sink.link( "dashboard-report-details.html#surefire" );
            sink.text( bundle.getString( "report.surefire.header" ) );
            sink.link_();
        }
        else
        {
            sink.link( "./surefire-report.html" );
            sink.text( bundle.getString( "report.surefire.header" ) );
            sink.link_();
        }
        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.lineBreak();
        if ( this.isDBAvailable )
        {
            this.linkToHistoricPage( sink, "surefire" );
        }
        sink.section1_();
        sink.lineBreak();
        sink.table();
        sink.tableRow();

        this.sinkHeader( sink, bundle.getString( "report.surefire.label.successrate" ) );

        this.sinkHeader( sink, bundle.getString( "report.surefire.label.tests" ) );

        this.sinkHeader( sink, bundle.getString( "report.surefire.label.errors" ) );

        this.sinkHeader( sink, bundle.getString( "report.surefire.label.failures" ) );

        this.sinkHeader( sink, bundle.getString( "report.surefire.label.skipped" ) );

        this.sinkHeader( sink, bundle.getString( "report.surefire.label.time" ) );

        sink.tableRow_();

        if ( report != null )
        {
            // List testSuites = report.parseXMLReportFiles();
            // Map summary = report.getSummary( testSuites );
            sink.tableRow();

            this.sinkCellPercentGraphic( sink, report.getSucessRate() / 100, "surefire" );

            this.sinkCell( sink, Integer.toString( report.getNbTests() ) );

            this.sinkCell( sink, Integer.toString( report.getNbErrors() ) );

            this.sinkCell( sink, Integer.toString( report.getNbFailures() ) );

            this.sinkCell( sink, Integer.toString( report.getNbSkipped() ) );

            this.sinkCell( sink, Double.toString( report.getElapsedTime() ) );

            sink.tableRow_();
        }
        else
        {
            sink.tableRow();

            this.sinkCell( sink, "0" );

            this.sinkCell( sink, "0" );

            this.sinkCell( sink, "0" );

            this.sinkCell( sink, "0" );

            this.sinkCell( sink, "0" + "%" );

            this.sinkCell( sink, "0" );

            sink.tableRow_();
        }

        sink.table_();

        sink.lineBreak();
        if ( this.generateGraphs )
        {
            IChartRenderer chart =
                new PieChartRenderer(
                                      new SurefirePieChartStrategy( bundle, this.mavenProject.getProjectName(), report ),
                                      DashBoardReportGenerator.SPECIFIC_WIDTH, ChartUtils.STANDARD_HEIGHT );
            if ( !chart.isEmpty() )
            {
                String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                if ( this.summary )
                {
                    filename += "_Summary_Surefire." + chart.getFileExtension();
                }
                else
                {
                    filename += "_Surefire." + chart.getFileExtension();
                }
                filename = filename.replace( ' ', '_' );
                String filenameWithPath = this.getImagesPath() + "/" + filename;
                this.getLog().debug( "createSurefireGraph = " + filename );
                try
                {
                    chart.saveToFile( filenameWithPath );
                    String link = "images/" + filename;
                    link = link.replace( ' ', '_' );
                    sink.figure();
                    sink.figureGraphics( link );
                    sink.figure_();
                }
                catch ( IOException e )
                {
                    this.getLog().debug( "createSurefireGraph exception = " + e.getMessage() );
                }
            }
        }
        // else
        // {
        // sink.text("[" + bundle.getString("dashboard.report.generategraphs.disabled") + "]");
        // sink.lineBreak();
        // }
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
        if ( this.summary )
        {
            sink.link( "dashboard-report-details.html#clover" );
            sink.text( bundle.getString( "report.clover.header" ) );
            sink.link_();
        }
        else
        {
            sink.link( "./clover/index.html" );
            sink.text( bundle.getString( "report.clover.header" ) );
            sink.link_();
        }
        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.lineBreak();
        if ( this.isDBAvailable )
        {
            this.linkToHistoricPage( sink, "clover" );
        }
        sink.section1_();
        sink.lineBreak();
        sink.table();
        sink.tableRow();

        this.sinkHeader( sink, bundle.getString( "report.clover.label.total" ) );

        this.sinkHeader( sink, bundle.getString( "report.clover.label.conditionals" ) );

        this.sinkHeader( sink, bundle.getString( "report.clover.label.statements" ) );

        this.sinkHeader( sink, bundle.getString( "report.clover.label.methods" ) );

        sink.tableRow_();

        if ( report != null )
        {
            sink.tableRow();

            if ( this.generateGraphs )
            {

                this.sinkCell( sink, report.getElementsLabel() );

                this.sinkCell( sink, report.getConditionalsLabel() );

                this.sinkCell( sink, report.getStatementsLabel() );

                this.sinkCell( sink, report.getMethodsLabel() );

            }
            else
            {
                sink.tableCell();
                sinkPercentGraphic( sink, report.getPercentCoveredConditionals() );
                sink.text( "(" + report.getCoveredElements() + " / " + report.getElements() + ")" );
                sink.tableCell_();
                sink.tableCell();
                sinkPercentGraphic( sink, report.getPercentCoveredConditionals() );
                sink.text( "(" + report.getCoveredConditionals() + " / " + report.getConditionals() + ")" );
                sink.tableCell_();
                sink.tableCell();
                sinkPercentGraphic( sink, report.getPercentCoveredStatements() );
                sink.text( "(" + report.getCoveredStatements() + " / " + report.getStatements() + ")" );
                sink.tableCell_();
                sink.tableCell();
                sinkPercentGraphic( sink, report.getPercentCoveredMethods() );
                sink.text( "(" + report.getCoveredMethods() + " / " + report.getMethods() + ")" );
                sink.tableCell_();
            }

            sink.tableRow_();
        }
        else
        {
            sink.tableRow();

            this.sinkCell( sink, "0" );

            this.sinkCell( sink, "0" );

            this.sinkCell( sink, "0" );

            this.sinkCell( sink, "0" );

            this.sinkCell( sink, "0" + "%" );

            this.sinkCell( sink, "0" );

            sink.tableRow_();
        }
        sink.table_();

        sink.lineBreak();
        if ( this.generateGraphs )
        {
            Map datas = new Hashtable();
            datas.put( this.mavenProject.getProjectName(), report );
            IChartRenderer chart =
                new BarChartRenderer( new CloverBarChartStrategy( bundle, this.mavenProject.getProjectName(), datas ),
                                      DashBoardReportGenerator.SPECIFIC_WIDTH + 50, ChartUtils.STANDARD_HEIGHT );
            if ( !chart.isEmpty() )
            {
                String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                if ( this.summary )
                {
                    filename += "_Summary_Clover." + chart.getFileExtension();
                }
                else
                {
                    filename += "_Clover." + chart.getFileExtension();
                }
                filename = filename.replace( ' ', '_' );
                String filenameWithPath = this.getImagesPath() + "/" + filename;
                this.getLog().debug( "createCloverGraph = " + filename );
                try
                {
                    chart.saveToFile( filenameWithPath );
                    String link = "images/" + filename;
                    link = link.replace( ' ', '_' );
                    sink.figure();
                    sink.figureGraphics( link );
                    sink.figure_();
                }
                catch ( IOException e )
                {
                    this.getLog().debug( "createCloverGraph exception = " + e.getMessage() );
                }
            }
        }
        // else
        // {
        // sink.text("[" + bundle.getString("dashboard.report.generategraphs.disabled") + "]");
        // sink.lineBreak();
        // }
    }

    public void createCoberturaSection( ResourceBundle bundle, Sink sink, CoberturaReportBean report )
    {

        sink.section1();

        sink.sectionTitle2();
        sink.anchor( "cobertura" );
        sink.anchor_();
        if ( this.summary )
        {
            sink.link( "dashboard-report-details.html#cobertura" );
            sink.text( bundle.getString( "report.cobertura.header" ) );
            sink.link_();
        }
        else
        {
            sink.link( "./cobertura/index.html" );
            sink.text( bundle.getString( "report.cobertura.header" ) );
            sink.link_();
        }

        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.lineBreak();
        if ( this.isDBAvailable )
        {
            this.linkToHistoricPage( sink, "cobertura" );
        }
        sink.section1_();
        sink.lineBreak();
        if ( report == null )
        {
            sink.text( "Error: Unable to read from Cobertura data file ." );
        }
        else
        {
            sink.table();
            sink.tableRow();

            this.sinkHeader( sink, bundle.getString( "report.cobertura.label.nbclasses" ) );

            this.sinkHeader( sink, bundle.getString( "report.cobertura.label.linecover" ) );

            this.sinkHeader( sink, bundle.getString( "report.cobertura.label.branchcover" ) );

            sink.tableRow_();

            sink.tableRow();
            this.sinkCell( sink, Integer.toString( report.getNbClasses() ) );

            if ( this.generateGraphs )
            {
                this.sinkCell( sink, getPercentValue( report.getLineCoverRate() ) );
                this.sinkCell( sink, getPercentValue( report.getBranchCoverRate() ) );
            }
            else
            {
                sinkCellPercentGraphic( sink, report.getLineCoverRate(), "cobertura" );
                sinkCellPercentGraphic( sink, report.getBranchCoverRate(), "cobertura" );
            }

            sink.tableRow_();

            sink.table_();
        }
        sink.lineBreak();
        if ( this.generateGraphs )
        {
            Map datas = new Hashtable();
            datas.put( this.mavenProject.getProjectName(), report );
            IChartRenderer chart =
                new BarChartRenderer(
                                      new CoberturaBarChartStrategy( bundle, this.mavenProject.getProjectName(), datas ),
                                      DashBoardReportGenerator.SPECIFIC_WIDTH, ChartUtils.STANDARD_HEIGHT );
            if ( !chart.isEmpty() )
            {
                String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                if ( this.summary )
                {
                    filename += "_Summary_Cobertura." + chart.getFileExtension();
                }
                else
                {
                    filename += "_Cobertura." + chart.getFileExtension();
                }
                filename = filename.replace( ' ', '_' );
                String filenameWithPath = this.getImagesPath() + "/" + filename;
                this.getLog().debug( "createCoberturaGraph = " + filename );
                try
                {
                    chart.saveToFile( filenameWithPath );
                    String link = "images/" + filename;
                    link = link.replace( ' ', '_' );
                    sink.figure();
                    sink.figureGraphics( link );
                    sink.figure_();
                }
                catch ( IOException e )
                {
                    this.getLog().debug( "createCoberturaGraph exception = " + e.getMessage() );
                }
            }
        }
        // else
        // {
        // sink.text("[" + bundle.getString("dashboard.report.generategraphs.disabled") + "]");
        // sink.lineBreak();
        // }
    }

    public void createPmdSection( ResourceBundle bundle, Sink sink, PmdReportBean report )
    {

        sink.section1();

        sink.sectionTitle2();
        sink.anchor( "pmd" );
        sink.anchor_();
        if ( this.summary )
        {
            sink.link( "dashboard-report-details.html#pmd" );
            sink.text( bundle.getString( "report.pmd.header" ) );
            sink.link_();
        }
        else
        {
            sink.link( "./pmd.html" );
            sink.text( bundle.getString( "report.pmd.header" ) );
            sink.link_();
        }

        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.lineBreak();
        if ( this.isDBAvailable )
        {
            this.linkToHistoricPage( sink, "pmd" );
        }
        sink.section1_();
        sink.lineBreak();
        if ( report == null )
        {
            sink.text( "Error: Unable to read from PMD data file ." );
        }
        else
        {
            sink.table();
            sink.tableRow();

            this.sinkHeader( sink, bundle.getString( "report.pmd.label.nbclasses" ) );

            this.sinkHeader( sink, bundle.getString( "report.pmd.label.nbviolations" ) );

            sink.tableRow_();

            sink.tableRow();

            this.sinkCell( sink, Integer.toString( report.getNbClasses() ) );

            this.sinkCell( sink, Integer.toString( report.getNbViolations() ) );

            sink.tableRow_();

            sink.table_();
        }
        sink.lineBreak();
    }

    public void createCpdSection( ResourceBundle bundle, Sink sink, CpdReportBean report )
    {

        sink.section1();

        sink.sectionTitle2();
        sink.anchor( "cpd" );
        sink.anchor_();
        if ( this.summary )
        {
            sink.link( "dashboard-report-details.html#cpd" );
            sink.text( bundle.getString( "report.cpd.header" ) );
            sink.link_();
        }
        else
        {
            sink.link( "./cpd.html" );
            sink.text( bundle.getString( "report.cpd.header" ) );
            sink.link_();
        }
        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.lineBreak();
        if ( this.isDBAvailable )
        {
            this.linkToHistoricPage( sink, "cpd" );
        }
        sink.section1_();
        sink.lineBreak();
        if ( report == null )
        {
            sink.text( "Error: Unable to read from CPD data file ." );
        }
        else
        {
            sink.table();
            sink.tableRow();

            this.sinkHeader( sink, bundle.getString( "report.cpd.label.nbclasses" ) );

            this.sinkHeader( sink, bundle.getString( "report.cpd.label.nbduplicate" ) );

            sink.tableRow_();

            sink.tableRow();

            this.sinkCell( sink, Integer.toString( report.getNbClasses() ) );

            this.sinkCell( sink, Integer.toString( report.getNbDuplicate() ) );

            sink.tableRow_();

            sink.table_();
        }
        sink.lineBreak();
    }

    public void createCheckStyleSection( ResourceBundle bundle, Sink sink, CheckstyleReportBean report )
    {

        sink.section1();
        sink.sectionTitle2();
        sink.anchor( "checkstyle" );
        sink.anchor_();
        if ( this.summary )
        {
            sink.link( "dashboard-report-details.html#checkstyle" );
            sink.text( bundle.getString( "report.checkstyle.header" ) );
            sink.link_();
        }
        else
        {
            sink.link( "./checkstyle.html" );
            sink.text( bundle.getString( "report.checkstyle.header" ) );
            sink.link_();
        }
        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.lineBreak();
        if ( this.isDBAvailable )
        {
            this.linkToHistoricPage( sink, "checkstyle" );
        }
        sink.section1_();
        if ( report == null )
        {
            sink.text( "Error: Unable to read from checkstyle data file ." );
        }
        else
        {
            sink.table();

            sink.tableRow();
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

            sink.tableRow();

            this.sinkCell( sink, Integer.toString( report.getNbClasses() ) );
            this.sinkCell( sink, Integer.toString( report.getNbTotal() ) );

            sink.tableCell();
            sink.text( Integer.toString( report.getNbInfos() ) );
            sinkInvertPercentGraphic( sink, report.getPercentInfos() );
            sink.tableCell_();
            sink.tableCell();
            sink.text( Integer.toString( report.getNbWarnings() ) );
            sinkInvertPercentGraphic( sink, report.getPercentWarnings() );
            sink.tableCell_();
            sink.tableCell();
            sink.text( Integer.toString( report.getNbErrors() ) );
            sinkInvertPercentGraphic( sink, report.getPercentErrors() );
            sink.tableCell_();

            sink.tableRow_();
            sink.table_();
        }
        sink.lineBreak();
        if ( this.generateGraphs )
        {
            IChartRenderer chart =
                new PieChartRenderer( new CheckstylePieChartStrategy( bundle, this.mavenProject.getProjectName(),
                                                                      report ),
                                      DashBoardReportGenerator.SPECIFIC_WIDTH, ChartUtils.STANDARD_HEIGHT );
            if ( !chart.isEmpty() )
            {
                String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                if ( this.summary )
                {
                    filename += "_Summary_CheckStyle." + chart.getFileExtension();
                }
                else
                {
                    filename += "_CheckStyle." + chart.getFileExtension();
                }
                filename = filename.replace( ' ', '_' );
                String filenameWithPath = this.getImagesPath() + "/" + filename;
                this.getLog().debug( "createCheckStyleGraph = " + filename );
                try
                {
                    chart.saveToFile( filenameWithPath );
                    String link = "images/" + filename;
                    link = link.replace( ' ', '_' );
                    sink.figure();
                    sink.figureGraphics( link );
                    sink.figure_();
                }
                catch ( IOException e )
                {
                    this.getLog().debug( "createCheckStyleGraph exception = " + e.getMessage() );
                }
            }

            // error management for Checkstyle Violations Chart. Fixes MOJO-679 .
            // Written by <a href="mailto:srivollet@objectif-informatique.fr">Sylvain Rivollet</a>.

            IChartRenderer chartError =
                new PieChart3DRenderer(
                                        new CheckstyleErrorsPieChartStrategy(
                                                                              bundle,
                                                                              this.mavenProject.getProjectName()
                                                                                              + " "
                                                                                              + bundle.getString( "chart.checkstyle.violations.title" ),
                                                                              report ), ChartUtils.STANDARD_WIDTH,
                                        ChartUtils.STANDARD_HEIGHT );
            sink.lineBreak();
            sink.lineBreak();

            if ( !chartError.isEmpty() )
            {
                String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                if ( this.summary )
                {
                    filename += "_Summary_CheckStyle_Error." + chartError.getFileExtension();
                }
                else
                {
                    filename += "_CheckStyle_Error." + chartError.getFileExtension();
                }
                filename = filename.replace( ' ', '_' );
                String filenameWithPath = this.getImagesPath() + "/" + filename;
                try
                {
                    chartError.saveToFile( filenameWithPath );
                    String link = "images/" + filename;
                    link = link.replace( ' ', '_' );
                    sink.figure();
                    sink.figureGraphics( link );
                    sink.figure_();
                }
                catch ( IOException e )
                {
                    this.getLog().debug( "createCheckStyleGraphError exception = " + e.getMessage() );
                }
            }
        }
        else
        {

            sink.sectionTitle3();
            sink.text( this.mavenProject.getProjectName() + " "
                            + bundle.getString( "chart.checkstyle.violations.title" ) );
            sink.sectionTitle3_();

            double percentVal = 0;
            int nbItInfPercent = 0;
            CheckstyleError error = new CheckstyleError();
            List errorsUnSorted = report.getErrors();
            Collections.sort( errorsUnSorted, new DescNbErrorCheckstyleComparator() );
            Iterator iterator = errorsUnSorted.iterator();
            int total = report.getNbTotal();

            sink.table();

            sink.tableRow();
            sink.tableHeaderCell();
            sink.text( "Violations" );
            sink.tableHeaderCell_();

            sink.tableHeaderCell();
            sink.text( "Rate" );
            sink.tableHeaderCell_();

            sink.tableRow_();

            while ( iterator.hasNext() )
            {
                error = (CheckstyleError) iterator.next();
                percentVal = (double) ( error.getNbIteration() * 1d / total );
                if ( percentVal > 0.01d )
                {
                    sink.tableRow();
                    this.sinkCell( sink, error.getMessage() );
                    this.sinkCellInvertPercentGraphic( sink, percentVal, "checkstyle" );
                    sink.tableRow_();
                }
                else
                {
                    nbItInfPercent += error.getNbIteration();
                }
            }

            if ( nbItInfPercent > 0 )
            {
                percentVal = (double) ( nbItInfPercent * 1d / total );
                sink.tableRow();
                this.sinkCell( sink, bundle.getString( "chart.checkstyle.violations.others.label" ) );
                this.sinkCellInvertPercentGraphic( sink, percentVal, "checkstyle" );
                sink.tableRow_();
            }

            sink.table_();
        }

    }

    public void createJDependSection( ResourceBundle bundle, Sink sink, JDependReportBean report )
    {
        this.getLog().debug( "createJDependSection creation." );
        sink.section1();

        sink.sectionTitle2();
        sink.anchor( "jdepend" );
        sink.anchor_();
        if ( this.summary )
        {
            sink.link( "dashboard-report-details.html#jdepend" );
            sink.text( "Top " + this.getNbExportedPackagesSummary() + " "
                            + bundle.getString( "report.xrefpackage.header" ) );
            sink.link_();
        }
        else
        {
            sink.link( "./jdepend-report.html" );
            sink.text( bundle.getString( "report.xrefpackage.header" ) );
            sink.link_();
        }
        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.lineBreak();

        sink.section1_();
        sink.lineBreak();
        if ( report == null )
        {
            sink.text( "Error: Unable to read from Jdepend data file ." );
        }
        else
        {
            double averageAC = report.getAverageAfferentCoupling();

            double calcul = ( Math.rint( averageAC * 1000 ) ) / 1000;

            sink.sectionTitle3();
            sink.text( bundle.getString( "report.xrefpackage.label.average" ) + " = " + calcul );
            sink.lineBreak();
            sink.text( " Nb Packages = " + report.getNbPackages() );
            sink.sectionTitle3_();
            sink.table();
            sink.tableRow();

            this.sinkHeader( sink, bundle.getString( "report.xrefpackage.label.package" ) );

            this.sinkHeader( sink, bundle.getString( "report.xrefpackage.label.ac" ) );

            this.sinkHeader( sink, bundle.getString( "report.xrefpackage.label.linecover" ) );

            this.sinkHeader( sink, bundle.getString( "report.xrefpackage.label.branchcover" ) );

            sink.tableRow_();

            List pack = report.getPackages();
            Iterator iter = pack.iterator();
            boolean threshold = false;
            int nbPack = 0;
            while ( iter.hasNext() )
            {
                nbPack = nbPack + 1;
                XRefPackageBean bean = (XRefPackageBean) iter.next();
                Integer ac = bean.getAfferentCoupling();
                if ( ac.doubleValue() <= averageAC && !threshold )
                {
                    threshold = true;
                    sink.tableRow();
                    this.sinkCellBold( sink, bundle.getString( "report.xrefpackage.label.threshold" ) );
                    this.sinkCellBold( sink, String.valueOf( calcul ) );
                    // sinkHeader( sink, "" );
                    // sinkHeader( sink, "" );
                    sink.tableRow_();
                }
                sink.tableRow();
                this.sinkCell( sink, bean.getPackageName() );
                this.sinkCell( sink, ac.toString() );
                // this.sinkCell(sink, getPercentValue(bean.getLineCoverRate()));
                sinkCellPercentGraphic( sink, bean.getLineCoverRate(), "cobertura" );
                // this.sinkCell(sink, getPercentValue(bean.getBranchCoverRate()));
                sinkCellPercentGraphic( sink, bean.getBranchCoverRate(), "cobertura" );
                sink.tableRow_();
                if ( this.summary && nbPack >= this.getNbExportedPackagesSummary() )
                {
                    break;
                }
            }

            sink.table_();
        }
        sink.lineBreak();
    }

    public void createFindBugsSection( ResourceBundle bundle, Sink sink, FindBugsReportBean report )
    {

        sink.section1();
        sink.sectionTitle2();
        sink.anchor( "findbugs" );
        sink.anchor_();
        if ( this.summary )
        {
            sink.link( "dashboard-report-details.html#findbugs" );
            sink.text( bundle.getString( "report.findbugs.header" ) );
            sink.link_();
        }
        else
        {
            sink.link( "./findbugs.html" );
            sink.text( bundle.getString( "report.findbugs.header" ) );
            sink.link_();
        }
        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.lineBreak();
        if ( this.isDBAvailable )
        {
            this.linkToHistoricPage( sink, "findbugs" );
        }
        sink.section1_();
        if ( report == null )
        {
            sink.text( "Error: Unable to read from findbugs data file ." );
        }
        else
        {
            sink.table();

            sink.tableRow();
            sink.tableHeaderCell();
            sink.text( bundle.getString( "report.findbugs.label.nbclasses" ) );
            sink.tableHeaderCell_();

            sink.tableHeaderCell();
            sink.text( bundle.getString( "report.findbugs.label.nbbugs" ) );
            sink.tableHeaderCell_();

            sink.tableHeaderCell();
            sink.text( bundle.getString( "report.findbugs.label.nberrors" ) );
            sink.tableHeaderCell_();

            sink.tableHeaderCell();
            sink.text( bundle.getString( "report.findbugs.label.nbMissingClasses" ) );
            sink.tableHeaderCell_();

            sink.tableRow_();

            sink.tableRow();

            this.sinkCell( sink, Integer.toString( report.getNbClasses() ) );
            this.sinkCell( sink, Integer.toString( report.getNbBugs() ) );
            this.sinkCell( sink, Integer.toString( report.getNbErrors() ) );
            this.sinkCell( sink, Integer.toString( report.getNbMissingClasses() ) );

            sink.tableRow_();

            sink.table_();
        }
        sink.lineBreak();
        if ( this.generateGraphs )
        {
            IChartRenderer chartError =
                new PieChart3DRenderer(
                                        new FindbugsCategoriesPieChartStrategy(
                                                                                bundle,
                                                                                this.mavenProject.getProjectName()
                                                                                                + " "
                                                                                                + bundle.getString( "chart.findbugs.bycategory.title" ),
                                                                                report ), ChartUtils.STANDARD_WIDTH,
                                        ChartUtils.STANDARD_HEIGHT );
            sink.lineBreak();
            sink.lineBreak();

            if ( !chartError.isEmpty() )
            {
                String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                if ( this.summary )
                {
                    filename += "_Summary_Findbugs_Categories." + chartError.getFileExtension();
                }
                else
                {
                    filename += "_Findbugs_Categories." + chartError.getFileExtension();
                }
                filename = filename.replace( ' ', '_' );
                String filenameWithPath = this.getImagesPath() + "/" + filename;
                try
                {
                    chartError.saveToFile( filenameWithPath );
                    String link = "images/" + filename;
                    link = link.replace( ' ', '_' );
                    sink.figure();
                    sink.figureGraphics( link );
                    sink.figure_();
                }
                catch ( IOException e )
                {
                    this.getLog().debug( "createFindbugsCategoriesGraphError exception = " + e.getMessage() );
                }
            }
            sink.lineBreak();
            chartError =
                new PieChart3DRenderer(
                                        new FindbugsPrioritiesPieChartStrategy(
                                                                                bundle,
                                                                                this.mavenProject.getProjectName()
                                                                                                + " "
                                                                                                + bundle.getString( "chart.findbugs.bypriority.title" ),
                                                                                report ), ChartUtils.STANDARD_WIDTH,
                                        ChartUtils.STANDARD_HEIGHT );
            sink.lineBreak();
            sink.lineBreak();

            if ( !chartError.isEmpty() )
            {
                String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                if ( this.summary )
                {
                    filename += "_Summary_Findbugs_Priorities." + chartError.getFileExtension();
                }
                else
                {
                    filename += "_Findbugs_Priorities." + chartError.getFileExtension();
                }
                filename = filename.replace( ' ', '_' );
                String filenameWithPath = this.getImagesPath() + "/" + filename;
                try
                {
                    chartError.saveToFile( filenameWithPath );
                    String link = "images/" + filename;
                    link = link.replace( ' ', '_' );
                    sink.figure();
                    sink.figureGraphics( link );
                    sink.figure_();
                }
                catch ( IOException e )
                {
                    this.getLog().debug( "createFindbugsPrioritiesGraphError exception = " + e.getMessage() );
                }
            }
        }
        else
        {
            sink.sectionTitle3();
            sink.text( this.mavenProject.getProjectName() + " " + bundle.getString( "chart.findbugs.bycategory.title" ) );
            sink.sectionTitle3_();
            sink.table();
            sink.tableRow();
            sink.tableHeaderCell();
            sink.text( "Categories" );
            sink.tableHeaderCell_();

            sink.tableHeaderCell();
            sink.text( "Rate" );
            sink.tableHeaderCell_();

            sink.tableRow_();

            Map categories = report.getCategories();
            Iterator iterator = categories.keySet().iterator();
            while ( iterator.hasNext() )
            {
                String category = (String) iterator.next();
                Integer value = (Integer) categories.get( category );
                double percentVal = value.intValue() / (double) report.getNbBugs();

                sink.tableRow();
                this.sinkCell( sink, category );
                this.sinkCellInvertPercentGraphic( sink, percentVal, "findbugs" );
                sink.tableRow_();
            }
            sink.table_();
            sink.lineBreak();

            sink.sectionTitle3();
            sink.text( this.mavenProject.getProjectName() + " " + bundle.getString( "chart.findbugs.bypriority.title" ) );
            sink.sectionTitle3_();
            sink.table();
            sink.tableRow();
            sink.tableHeaderCell();
            sink.text( "Priorities" );
            sink.tableHeaderCell_();

            sink.tableHeaderCell();
            sink.text( "Rate" );
            sink.tableHeaderCell_();

            sink.tableRow_();

            Map priorities = report.getPriorities();
            Iterator iteratorP = priorities.keySet().iterator();
            while ( iteratorP.hasNext() )
            {
                String priority = (String) iteratorP.next();
                Integer value = (Integer) priorities.get( priority );
                double percentVal = value.intValue() / (double) report.getNbBugs();

                sink.tableRow();
                this.sinkCell( sink, priority );
                this.sinkCellInvertPercentGraphic( sink, percentVal, "findbugs" );
                sink.tableRow_();
            }
            sink.table_();
        }
    }

    public void createTaglistSection( ResourceBundle bundle, Sink sink, TagListReportBean report )
    {

        sink.section1();
        sink.sectionTitle2();
        sink.anchor( "taglist" );
        sink.anchor_();
        if ( this.summary )
        {
            sink.link( "dashboard-report-details.html#taglist" );
            sink.text( bundle.getString( "report.taglist.header" ) );
            sink.link_();
        }
        else
        {
            sink.link( "./taglist.html" );
            sink.text( bundle.getString( "report.taglist.header" ) );
            sink.link_();
        }
        sink.sectionTitle2_();
        this.linkToTopPage( sink );
        sink.lineBreak();
        if ( this.isDBAvailable )
        {
            this.linkToHistoricPage( sink, "taglist" );
        }
        sink.section1_();

        if ( report == null )
        {
            sink.text( "Error: Unable to read from taglist data file ." );
        }
        else
        {
            sink.table();

            sink.tableRow();
            sink.tableHeaderCell();
            sink.text( bundle.getString( "report.taglist.column.tags" ) );
            sink.tableHeaderCell_();

            sink.tableHeaderCell();
            sink.text( bundle.getString( "report.taglist.column.nboccurs" ) );
            sink.tableHeaderCell_();

            sink.tableRow_();

            for ( Iterator ite = report.getTags().entrySet().iterator(); ite.hasNext(); )
            {
                sink.tableRow();
                Map.Entry entry = (Map.Entry) ite.next();
                this.sinkCell( sink, (String) entry.getKey() );
                this.sinkCell( sink, ( (Integer) entry.getValue() ).toString() );
                sink.tableRow_();

            }

            sink.tableRow();
            sink.tableHeaderCell();
            sink.text( "Total" );
            sink.tableHeaderCell_();

            sink.tableHeaderCell();
            sink.text( Integer.toString( report.getNbTotal() ) );
            sink.tableHeaderCell_();
            sink.tableRow_();
            sink.tableRow();
            sink.tableHeaderCell();
            sink.text( bundle.getString( "report.taglist.label.nbclasses" ) );
            sink.tableHeaderCell_();

            sink.tableHeaderCell();
            sink.text( Integer.toString( report.getNbClasses() ) );
            sink.tableHeaderCell_();
            sink.tableRow_();

            sink.table_();

        }
        sink.lineBreak();
        if ( this.generateGraphs )
        {
            IChartRenderer chart =
                new PieChart3DRenderer( new TaglistPieChartStrategy( bundle, this.mavenProject.getProjectName() + " "
                                + bundle.getString( "chart.taglist.bytags.title" ), report ),
                                        DashBoardReportGenerator.SPECIFIC_WIDTH, ChartUtils.STANDARD_HEIGHT );
            if ( !chart.isEmpty() )
            {
                String filename = this.replaceForbiddenChar( this.mavenProject.getProjectName() );
                if ( this.summary )
                {
                    filename += "_Summary_Taglist." + chart.getFileExtension();
                }
                else
                {
                    filename += "_Taglist." + chart.getFileExtension();
                }
                filename = filename.replace( ' ', '_' );
                String filenameWithPath = this.getImagesPath() + "/" + filename;
                this.getLog().debug( "createTaglistGraph = " + filename );
                try
                {
                    chart.saveToFile( filenameWithPath );
                    String link = "images/" + filename;
                    link = link.replace( ' ', '_' );
                    sink.figure();
                    sink.figureGraphics( link );
                    sink.figure_();
                }
                catch ( IOException e )
                {
                    this.getLog().debug( "createTaglistGraph exception = " + e.getMessage() );
                }
            }
        }
        // else
        // {
        // sink.text("[" + bundle.getString("dashboard.report.generategraphs.disabled") + "]");
        // sink.lineBreak();
        // }
    }
}
