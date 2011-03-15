package org.codehaus.mojo.dashboard.report.plugin;

/*
 * Copyright 2007 David Vicente
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;
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

/**
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 *
 */
public class DashBoardMaven1ReportGenerator extends AbstractDashBoardGenerator
{

    private String dashboardAnchorLink = "/dashboard-report.html";

    private DashBoardMavenProject mavenProject;

    private Map map = new Hashtable();

    private boolean isDBAvailable = false;

    /**
     *
     * @param dashboardReport
     */
    public DashBoardMaven1ReportGenerator( DashBoardMavenProject mavenProject, boolean isDBAvailable, Log log )
    {

        super( log );
        this.mavenProject = mavenProject;
        this.isDBAvailable = isDBAvailable;
        Set reports = mavenProject.getReports();
        Iterator iter = reports.iterator();
        while ( iter.hasNext() )
        {
            IDashBoardReportBean report = (IDashBoardReportBean) iter.next();
            if ( report != null )
            {
                map.put( report.getClass(), report );
            }
        }

    }

    public void doGenerateReport( ResourceBundle bundle, Sink sink )
    {

        createTitle( bundle, sink );
        addDashboardCss(sink);
        this.sinkJavascriptCode( sink );
        sink.body();
        sink.anchor( "top" );
        sink.anchor_();
        createHeader( bundle, sink );

        createBodyReport( bundle, sink );

        sink.body_();

        sink.flush();

        sink.close();
    }

    public void createTitle( ResourceBundle bundle, Sink sink )
    {
        sink.head();
        sink.title();
        sink.text( bundle.getString( "dashboard.multireport.name" ) );
        sink.title_();
        sink.head_();
    }

    public void createHeader( ResourceBundle bundle, Sink sink )
    {
        sink.section1();

        sink.sectionTitle1();
        sink.text( bundle.getString( "dashboard.multireport.name" ) + " : " + this.mavenProject.getProjectName() );
        sink.sectionTitle1_();

        sink.text( "Date Generated: " + new SimpleDateFormat().format( new Date( System.currentTimeMillis() ) ) );
        sink.horizontalRule();

        if ( this.isDBAvailable )
        {
            sink.sectionTitle3();
            sink.bold();
            sink.text( "[" );
            sink.link( "dashboard-report-historic.html" );
            sink.text( "Go to Historic page" );
            sink.link_();
            sink.text( "]" );
            sink.bold_();
            sink.sectionTitle3_();
            sink.horizontalRule();
        }
        sink.lineBreak();
        sink.section1_();
    }

    public void createBodyReport( ResourceBundle bundle, Sink sink )
    {
        System.out.println( "DashBoardMultiReportGenerator createBodyByReport(...)" );

        createAllSection( bundle, sink );

    }

    public void createAllSection( ResourceBundle bundle, Sink sink )
    {

        sink.table();
        writeSuperHeader( sink );
        writeHeader( bundle, sink, true );
        createAllLineByReport( bundle, sink, mavenProject, true, "" );
        createTotalLine( bundle, sink, mavenProject );
        writeHeader( bundle, sink, false );
        writeSuperHeader( sink );
        sink.table_();
        sink.lineBreak();

    }

    public void createAllLineByReport( ResourceBundle bundle, Sink sink, DashBoardMavenProject mavenProject,
                                       boolean isRoot, String prefix )
    {

        if ( mavenProject.getModules() != null && !mavenProject.getModules().isEmpty() )
        {
            Iterator iter = mavenProject.getModules().iterator();
            if ( !isRoot )
            {
                prefix = writeMultiProjectRow( sink, mavenProject, prefix, dashboardAnchorLink );
            }
            while ( iter.hasNext() )
            {
                DashBoardMavenProject subproject = (DashBoardMavenProject) iter.next();
                createAllLineByReport( bundle, sink, subproject, false, prefix );
            }
        }
        else
        {
            sink.tableRow();
            writeProjectCell( sink, mavenProject, prefix, dashboardAnchorLink );

            if ( map.get( CoberturaReportBean.class ) != null )
            {
                CoberturaReportBean coberReportBean =
                    (CoberturaReportBean) mavenProject.getReportsByType( CoberturaReportBean.class );
                if ( coberReportBean != null )
                {

                    sinkCellClass( sink, Integer.toString( coberReportBean.getNbClasses() ), "cobertura" );
                    // sinkCellClass( sink, getPercentValue( coberReportBean.getLineCoverRate() ) ,"cobertura");
                    sinkCellPercentGraphic( sink, coberReportBean.getLineCoverRate(), "cobertura" );
                    // sinkCellClass( sink, getPercentValue( coberReportBean.getBranchCoverRate() ),"cobertura" );
                    sinkCellPercentGraphic( sink, coberReportBean.getBranchCoverRate(), "cobertura" );
                    sinkHeaderBold( sink, "|" );

                }
                else
                {
                    sinkCellClass( sink, "", "cobertura" );
                    sinkCellClass( sink, "", "cobertura" );
                    sinkCellClass( sink, "", "cobertura" );
                    sinkHeaderBold( sink, "|" );
                }
            }
            if ( map.get( CloverReportBean.class ) != null )
            {
                CloverReportBean cloverReportBean =
                    (CloverReportBean) mavenProject.getReportsByType( CloverReportBean.class );
                if ( cloverReportBean != null )
                {

                    sinkCellPercentGraphic( sink, cloverReportBean.getPercentCoveredConditionals(), "clover", "("
                                    + cloverReportBean.getCoveredElements() + " / " + cloverReportBean.getElements()
                                    + ")" );
                    this.sinkCellClass( sink, cloverReportBean.getConditionalsLabel(), "clover" );

                    this.sinkCellClass( sink, cloverReportBean.getStatementsLabel(), "clover" );

                    this.sinkCellClass( sink, cloverReportBean.getMethodsLabel(), "clover" );

                    sinkHeaderBold( sink, "|" );
                }
                else
                {
                    sinkCellClass( sink, "", "clover" );
                    sinkCellClass( sink, "", "clover" );
                    sinkCellClass( sink, "", "clover" );
                    sinkCellClass( sink, "", "clover" );
                    sinkHeaderBold( sink, "|" );
                }
            }
            if ( map.get( SurefireReportBean.class ) != null )
            {
                SurefireReportBean fireReportBean =
                    (SurefireReportBean) mavenProject.getReportsByType( SurefireReportBean.class );
                if ( fireReportBean != null )
                {

                    // sinkCellClass( sink, Double.toString( fireReportBean.getSucessRate() ) + "%" , "surefire");
                    sinkCellPercentGraphic( sink, fireReportBean.getSucessRate() / 100, "surefire" );

                    sinkCellClass( sink, Integer.toString( fireReportBean.getNbTests() ), "surefire" );

                    sinkCellClass( sink, Integer.toString( fireReportBean.getNbErrors() ), "surefire" );

                    sinkCellClass( sink, Integer.toString( fireReportBean.getNbFailures() ), "surefire" );

                    sinkCellClass( sink, Integer.toString( fireReportBean.getNbSkipped() ), "surefire" );

                    sinkCellClass( sink, Double.toString( fireReportBean.getElapsedTime() ), "surefire" );
                    sinkHeaderBold( sink, "|" );

                }
                else
                {
                    sinkCellClass( sink, "", "surefire" );
                    sinkCellClass( sink, "", "surefire" );
                    sinkCellClass( sink, "", "surefire" );
                    sinkCellClass( sink, "", "surefire" );
                    sinkCellClass( sink, "", "surefire" );
                    sinkCellClass( sink, "", "surefire" );
                    sinkHeaderBold( sink, "|" );
                }
            }
            if ( map.get( CheckstyleReportBean.class ) != null )
            {
                CheckstyleReportBean checkStyleReport =
                    (CheckstyleReportBean) mavenProject.getReportsByType( CheckstyleReportBean.class );
                if ( checkStyleReport != null )
                {
                    sinkCellClass( sink, Integer.toString( checkStyleReport.getNbClasses() ), "checkstyle" );
                    sinkCellClass( sink, Integer.toString( checkStyleReport.getNbTotal() ), "checkstyle" );
                    sinkCellClass( sink, Integer.toString( checkStyleReport.getNbInfos() ), "checkstyle" );
                    sinkCellClass( sink, Integer.toString( checkStyleReport.getNbWarnings() ), "checkstyle" );
                    sinkCellClass( sink, Integer.toString( checkStyleReport.getNbErrors() ), "checkstyle" );
                    sinkHeaderBold( sink, "|" );
                }
                else
                {
                    sinkCellClass( sink, "", "checkstyle" );
                    sinkCellClass( sink, "", "checkstyle" );
                    sinkCellClass( sink, "", "checkstyle" );
                    sinkCellClass( sink, "", "checkstyle" );
                    sinkCellClass( sink, "", "checkstyle" );
                    sinkHeaderBold( sink, "|" );
                }
            }
            if ( map.get( PmdReportBean.class ) != null )
            {
                PmdReportBean pmdReportBean = (PmdReportBean) mavenProject.getReportsByType( PmdReportBean.class );
                if ( pmdReportBean != null )
                {
                    sinkCellClass( sink, Integer.toString( pmdReportBean.getNbClasses() ), "pmd" );
                    sinkCellClass( sink, Integer.toString( pmdReportBean.getNbViolations() ), "pmd" );
                    sinkHeaderBold( sink, "|" );

                }
                else
                {
                    sinkCellClass( sink, "", "pmd" );
                    sinkCellClass( sink, "", "pmd" );
                    sinkHeaderBold( sink, "|" );
                }
            }
            if ( map.get( CpdReportBean.class ) != null )
            {
                CpdReportBean cpdReportBean = (CpdReportBean) mavenProject.getReportsByType( CpdReportBean.class );
                if ( cpdReportBean != null )
                {
                    sinkCellClass( sink, Integer.toString( cpdReportBean.getNbClasses() ), "cpd" );
                    sinkCellClass( sink, Integer.toString( cpdReportBean.getNbDuplicate() ), "cpd" );
                    sinkHeaderBold( sink, "|" );
                }
                else
                {
                    sinkCellClass( sink, "", "cpd" );
                    sinkCellClass( sink, "", "cpd" );
                    sinkHeaderBold( sink, "|" );
                }
            }
            if ( map.get( FindBugsReportBean.class ) != null )
            {
                FindBugsReportBean findBugsReportBean =
                    (FindBugsReportBean) mavenProject.getReportsByType( FindBugsReportBean.class );
                if ( findBugsReportBean != null )
                {
                    sinkCellClass( sink, Integer.toString( findBugsReportBean.getNbClasses() ), "findbugs" );
                    sinkCellClass( sink, Integer.toString( findBugsReportBean.getNbBugs() ), "findbugs" );
                    sinkCellClass( sink, Integer.toString( findBugsReportBean.getNbErrors() ), "findbugs" );
                    sinkCellClass( sink, Integer.toString( findBugsReportBean.getNbMissingClasses() ), "findbugs" );
                    sinkHeaderBold( sink, "|" );
                }
                else
                {
                    sinkCellClass( sink, "", "findbugs" );
                    sinkCellClass( sink, "", "findbugs" );
                    sinkCellClass( sink, "", "findbugs" );
                    sinkCellClass( sink, "", "findbugs" );
                    sinkHeaderBold( sink, "|" );
                }
            }
            if ( map.get( TagListReportBean.class ) != null )
            {
                TagListReportBean taglistReportBean =
                    (TagListReportBean) mavenProject.getReportsByType( TagListReportBean.class );
                if ( taglistReportBean != null )
                {
                    sinkCellClass( sink, Integer.toString( taglistReportBean.getNbClasses() ), "taglist" );
                    sinkCellClass( sink, Integer.toString( taglistReportBean.getNbTotal() ), "taglist" );
                    sinkHeaderBold( sink, "|" );
                }
                else
                {
                    sinkCellClass( sink, "", "taglist" );
                    sinkCellClass( sink, "", "taglist" );
                    sinkHeaderBold( sink, "|" );
                }

            }

            sink.tableRow_();
        }
    }

    private void writeProjectCell( Sink sink, DashBoardMavenProject mavenProject, String prefix, String suffix )
    {
        if ( prefix == null || prefix.length() == 0 )
        {
            String artefactId = mavenProject.getArtifactId();
            String link = artefactId.substring( artefactId.lastIndexOf( "." ) + 1, artefactId.length() );
            sinkCellWithLink( sink, mavenProject.getProjectName(), link + suffix );
        }
        else
        {
            int nbTab = prefix.split( "/" ).length;
            String artefactId = mavenProject.getArtifactId();
            String link = prefix + "/" + artefactId.substring( artefactId.lastIndexOf( "." ) + 1, artefactId.length() );
            sinkCellTabWithLink( sink, mavenProject.getProjectName(), nbTab, link + suffix );
        }
    }

    private String writeMultiProjectRow( Sink sink, DashBoardMavenProject mavenProject, String prefix, String suffix )
    {
        if ( prefix == null || prefix.length() == 0 )
        {
            String artefactId = mavenProject.getArtifactId();
            prefix = artefactId.substring( artefactId.lastIndexOf( "." ) + 1, artefactId.length() );
            sink.tableRow();
            sinkCellBoldWithLink( sink, mavenProject.getProjectName(), prefix + suffix );
            sink.tableRow_();
        }
        else
        {
            sink.tableRow();
            int nbTab = prefix.split( "/" ).length;
            String artefactId = mavenProject.getArtifactId();
            prefix = prefix + "/" + artefactId.substring( artefactId.lastIndexOf( "." ) + 1, artefactId.length() );
            sinkCellTabBoldWithLink( sink, mavenProject.getProjectName(), nbTab, prefix + suffix );
            sink.tableRow_();

        }
        return prefix;
    }

    private void writeSuperHeader( Sink sink )
    {
        sink.tableRow();
        sink.tableHeaderCell();
        sink.nonBreakingSpace();
        sink.tableHeaderCell_();
        if ( map.get( CoberturaReportBean.class ) != null )
        {
            sinkSuperHeaderClass( sink, "Cobertura", 3, "cobertura" );
            this.sinkHeaderCollapsedIcon( sink, "cobertura" );
        }
        if ( map.get( CloverReportBean.class ) != null )
        {
            sinkSuperHeaderClass( sink, "Clover", 4, "clover" );
            this.sinkHeaderCollapsedIcon( sink, "clover" );
        }
        if ( map.get( SurefireReportBean.class ) != null )
        {
            sinkSuperHeaderClass( sink, "Surefire", 6, "surefire" );
            this.sinkHeaderCollapsedIcon( sink, "surefire" );
        }
        if ( map.get( CheckstyleReportBean.class ) != null )
        {
            sinkSuperHeaderClass( sink, "Checkstyle", 5, "checkstyle" );
            this.sinkHeaderCollapsedIcon( sink, "checkstyle" );
        }
        if ( map.get( PmdReportBean.class ) != null )
        {
            sinkSuperHeaderClass( sink, "PMD", 2, "pmd" );
            this.sinkHeaderCollapsedIcon( sink, "pmd" );
        }
        if ( map.get( CpdReportBean.class ) != null )
        {
            sinkSuperHeaderClass( sink, "CPD", 2, "cpd" );
            this.sinkHeaderCollapsedIcon( sink, "cpd" );
        }
        if ( map.get( FindBugsReportBean.class ) != null )
        {
            sinkSuperHeaderClass( sink, "FindBugs", 4, "findbugs" );
            this.sinkHeaderCollapsedIcon( sink, "findbugs" );
        }
        if ( map.get( TagListReportBean.class ) != null )
        {
            sinkSuperHeaderClass( sink, "Tag List", 2, "taglist" );
            this.sinkHeaderCollapsedIcon( sink, "taglist" );
        }

        sink.tableRow_();
    }

    private void writeHeader( ResourceBundle bundle, Sink sink, boolean upside )
    {
        sink.tableRow();
        if ( upside )
        {
            sinkHeader( sink, bundle.getString( "report.project.name.header" ) );
        }
        else
        {
            sinkHeader( sink, "" );
        }
        if ( map.get( CoberturaReportBean.class ) != null )
        {
            sinkHeaderClass( sink, bundle.getString( "report.cobertura.label.nbclasses" ), "cobertura" );
            sinkHeaderClass( sink, bundle.getString( "report.cobertura.label.linecover" ), "cobertura" );
            sinkHeaderClass( sink, bundle.getString( "report.cobertura.label.branchcover" ), "cobertura" );
            sinkHeaderBold( sink, "" );
        }
        if ( map.get( CloverReportBean.class ) != null )
        {
            sinkHeaderClass( sink, bundle.getString( "report.clover.label.total" ), "clover" );
            sinkHeaderClass( sink, bundle.getString( "report.clover.label.conditionals" ), "clover" );
            sinkHeaderClass( sink, bundle.getString( "report.clover.label.statements" ), "clover" );
            sinkHeaderClass( sink, bundle.getString( "report.clover.label.methods" ), "clover" );
            sinkHeaderBold( sink, "" );
        }
        if ( map.get( SurefireReportBean.class ) != null )
        {
            sinkHeaderClass( sink, bundle.getString( "report.surefire.label.successrate" ), "surefire" );
            sinkHeaderClass( sink, bundle.getString( "report.surefire.label.tests" ), "surefire" );
            sinkHeaderClass( sink, bundle.getString( "report.surefire.label.errors" ), "surefire" );
            sinkHeaderClass( sink, bundle.getString( "report.surefire.label.failures" ), "surefire" );
            sinkHeaderClass( sink, bundle.getString( "report.surefire.label.skipped" ), "surefire" );
            sinkHeaderClass( sink, bundle.getString( "report.surefire.label.time" ), "surefire" );
            sinkHeaderBold( sink, "" );
        }
        if ( map.get( CheckstyleReportBean.class ) != null )
        {

            sinkHeaderClass( sink, bundle.getString( "report.checkstyle.files" ), "checkstyle" );
            sinkHeaderClass( sink, bundle.getString( "report.checkstyle.column.total" ), "checkstyle" );

            sink.rawText( "<th class=\"checkstyle\">" + bundle.getString( "report.checkstyle.column.infos" ) );
            sink.nonBreakingSpace();
            iconInfo( sink );
            sink.rawText( "</th>" );

            sink.rawText( "<th class=\"checkstyle\">" + bundle.getString( "report.checkstyle.column.warnings" ) );
            sink.nonBreakingSpace();
            iconWarning( sink );
            sink.rawText( "</th>" );

            sink.rawText( "<th class=\"checkstyle\">" + bundle.getString( "report.checkstyle.column.errors" ) );
            sink.nonBreakingSpace();
            iconError( sink );
            sink.rawText( "</th>" );

            sinkHeaderBold( sink, "" );
        }
        if ( map.get( PmdReportBean.class ) != null )
        {
            sinkHeaderClass( sink, bundle.getString( "report.pmd.label.nbclasses" ), "pmd" );
            sinkHeaderClass( sink, bundle.getString( "report.pmd.label.nbviolations" ), "pmd" );
            sinkHeaderBold( sink, "" );
        }
        if ( map.get( CpdReportBean.class ) != null )
        {
            sinkHeaderClass( sink, bundle.getString( "report.cpd.label.nbclasses" ), "cpd" );
            sinkHeaderClass( sink, bundle.getString( "report.cpd.label.nbduplicate" ), "cpd" );
            sinkHeaderBold( sink, "" );
        }
        if ( map.get( FindBugsReportBean.class ) != null )
        {
            sinkHeaderClass( sink, bundle.getString( "report.findbugs.label.nbclasses" ), "findbugs" );
            sinkHeaderClass( sink, bundle.getString( "report.findbugs.label.nbbugs" ), "findbugs" );
            sinkHeaderClass( sink, bundle.getString( "report.findbugs.label.nberrors" ), "findbugs" );
            sinkHeaderClass( sink, bundle.getString( "report.findbugs.label.nbMissingClasses" ), "findbugs" );
            sinkHeaderBold( sink, "" );
        }
        if ( map.get( TagListReportBean.class ) != null )
        {
            sinkHeaderClass( sink, bundle.getString( "report.taglist.label.nbclasses" ), "taglist" );
            sinkHeaderClass( sink, bundle.getString( "report.taglist.column.nboccurs" ), "taglist" );
            sinkHeaderBold( sink, "" );
        }
        sink.tableRow_();
    }

    public void createTotalLine( ResourceBundle bundle, Sink sink, DashBoardMavenProject mavenProject )
    {
        sink.tableRow();
        sinkHeader( sink, "Total" );
        CoberturaReportBean reportBean =
            (CoberturaReportBean) mavenProject.getReportsByType( CoberturaReportBean.class );
        if ( reportBean != null )
        {
            sinkHeaderClass( sink, Integer.toString( reportBean.getNbClasses() ), "cobertura" );
            // sinkHeaderClass( sink, getPercentValue( reportBean.getLineCoverRate() ) , "cobertura");
            sinkHeaderCellPercentGraphic( sink, reportBean.getLineCoverRate(), "cobertura" );
            // sinkHeaderClass( sink, getPercentValue( reportBean.getBranchCoverRate() ) , "cobertura");
            sinkHeaderCellPercentGraphic( sink, reportBean.getBranchCoverRate(), "cobertura" );
            sinkHeaderBold( sink, "|" );
        }
        CloverReportBean cloverReportBean = (CloverReportBean) mavenProject.getReportsByType( CloverReportBean.class );
        if ( cloverReportBean != null )
        {
            sinkHeaderCellPercentGraphic( sink, cloverReportBean.getPercentCoveredConditionals(), "clover", "("
                            + cloverReportBean.getCoveredElements() + " / " + cloverReportBean.getElements() + ")" );

            sinkHeaderCellPercentGraphic( sink, cloverReportBean.getPercentCoveredConditionals(), "clover", "("
                            + cloverReportBean.getCoveredConditionals() + " / " + cloverReportBean.getConditionals()
                            + ")" );

            sinkHeaderCellPercentGraphic( sink, cloverReportBean.getPercentCoveredStatements(), "clover", "("
                            + cloverReportBean.getCoveredStatements() + " / " + cloverReportBean.getStatements() + ")" );

            sinkHeaderCellPercentGraphic( sink, cloverReportBean.getPercentCoveredMethods(), "clover", "("
                            + cloverReportBean.getCoveredMethods() + " / " + cloverReportBean.getMethods() + ")" );

            sinkHeaderBold( sink, "|" );
        }
        SurefireReportBean fireReportBean =
            (SurefireReportBean) mavenProject.getReportsByType( SurefireReportBean.class );
        if ( fireReportBean != null )
        {
            sinkHeaderCellPercentGraphic( sink, fireReportBean.getSucessRate() / 100, "surefire" );

            sinkHeaderClass( sink, Integer.toString( fireReportBean.getNbTests() ), "surefire" );

            sinkHeaderClass( sink, Integer.toString( fireReportBean.getNbErrors() ), "surefire" );

            sinkHeaderClass( sink, Integer.toString( fireReportBean.getNbFailures() ), "surefire" );

            sinkHeaderClass( sink, Integer.toString( fireReportBean.getNbSkipped() ), "surefire" );

            sinkHeaderClass( sink, Double.toString( fireReportBean.getElapsedTime() ), "surefire" );
            sinkHeaderBold( sink, "|" );
        }
        CheckstyleReportBean checkstyleReportBean =
            (CheckstyleReportBean) mavenProject.getReportsByType( CheckstyleReportBean.class );
        if ( checkstyleReportBean != null )
        {
            sinkHeaderClass( sink, Integer.toString( checkstyleReportBean.getNbClasses() ), "checkstyle" );
            sinkHeaderClass( sink, Integer.toString( checkstyleReportBean.getNbTotal() ), "checkstyle" );

            tableHeaderCellClass( sink, "checkstyle" );
            sink.text( Integer.toString( checkstyleReportBean.getNbInfos() ) );
            sinkInvertPercentGraphic( sink, checkstyleReportBean.getPercentInfos() );
            tableHeaderCell_( sink );
            tableHeaderCellClass( sink, "checkstyle" );
            sink.text( Integer.toString( checkstyleReportBean.getNbWarnings() ) );
            sinkInvertPercentGraphic( sink, checkstyleReportBean.getPercentWarnings() );
            tableHeaderCell_( sink );
            tableHeaderCellClass( sink, "checkstyle" );
            sink.text( Integer.toString( checkstyleReportBean.getNbErrors() ) );
            sinkInvertPercentGraphic( sink, checkstyleReportBean.getPercentErrors() );
            tableHeaderCell_( sink );

            sinkHeaderBold( sink, "|" );
        }
        PmdReportBean pmdReportBean = (PmdReportBean) mavenProject.getReportsByType( PmdReportBean.class );
        if ( pmdReportBean != null )
        {
            sinkHeaderClass( sink, Integer.toString( pmdReportBean.getNbClasses() ), "pmd" );
            sinkHeaderClass( sink, Integer.toString( pmdReportBean.getNbViolations() ), "pmd" );
            sinkHeaderBold( sink, "|" );

        }
        CpdReportBean cpdReportBean = (CpdReportBean) mavenProject.getReportsByType( CpdReportBean.class );
        if ( cpdReportBean != null )
        {
            sinkHeaderClass( sink, Integer.toString( cpdReportBean.getNbClasses() ), "cpd" );
            sinkHeaderClass( sink, Integer.toString( cpdReportBean.getNbDuplicate() ), "cpd" );
            sinkHeaderBold( sink, "|" );

        }
        FindBugsReportBean findBugsReportBean =
            (FindBugsReportBean) mavenProject.getReportsByType( FindBugsReportBean.class );
        if ( findBugsReportBean != null )
        {
            sinkHeaderClass( sink, Integer.toString( findBugsReportBean.getNbClasses() ), "findbugs" );
            sinkHeaderClass( sink, Integer.toString( findBugsReportBean.getNbBugs() ), "findbugs" );
            sinkHeaderClass( sink, Integer.toString( findBugsReportBean.getNbErrors() ), "findbugs" );
            sinkHeaderClass( sink, Integer.toString( findBugsReportBean.getNbMissingClasses() ), "findbugs" );
            sinkHeaderBold( sink, "|" );
        }
        TagListReportBean taglistReportBean =
            (TagListReportBean) mavenProject.getReportsByType( TagListReportBean.class );
        if ( taglistReportBean != null )
        {
            sinkHeaderClass( sink, Integer.toString( taglistReportBean.getNbClasses() ), "taglist" );
            sinkHeaderClass( sink, Integer.toString( taglistReportBean.getNbTotal() ), "taglist" );
            sinkHeaderBold( sink, "|" );
        }
        sink.tableRow_();
    }

    private void sinkHeaderCollapsedIcon( Sink sink, String id )
    {
        sink.tableHeaderCell();
        String idImg = "Collapsed" + id;
        sink.rawText( "<IMG SRC=\"./images/previous.gif\" ALT=\"" + id + "\" name=\"" + idImg
                        + "\" onclick=\"javascript:toggleCol('" + idImg + "','" + id + "');\">" );
        sink.tableHeaderCell_();
    }

    private void sinkJavascriptCode( Sink sink )
    {

        StringBuffer buff = new StringBuffer();

        buff.append( "<script type=\"text/javascript\">" );
        buff.append( "        function toggleCol(imageID,strCol){" );
        buff.append( "            var ths = document.getElementsByTagName(\"th\");" );
        buff.append( "            var tds = document.getElementsByTagName(\"td\");" );
        buff.append( "            var mesimages = document.getElementsByName(imageID);" );
        buff.append( "            for (idx in ths) {" );
        buff.append( "                if (ths[idx].className == strCol)" );
        buff.append( "                {" );
        buff.append( "                    if (ths[idx].style.display == \"none\") {" );
        buff.append( "                        ths[idx].style.display = \"\";" );
        buff.append( "                        for (var i = 0; i < mesimages.length; i++) {" );
        buff.append( "                            mesimages[i].src = './images/previous.gif';" );
        buff.append( "                        }" );
        buff.append( "                    }" );
        buff.append( "                    else {" );
        buff.append( "                        ths[idx].style.display = \"none\";" );
        buff.append( "                        for (var i = 0; i < mesimages.length; i++) {" );
        buff.append( "                            mesimages[i].src = './images/next.gif';" );
        buff.append( "                        }" );
        buff.append( "                    }" );
        buff.append( "                }" );
        buff.append( "            }" );
        buff.append( "            for (idx in tds) {" );
        buff.append( "                if (tds[idx].className == strCol)" );
        buff.append( "                {" );
        buff.append( "                    if (tds[idx].style.display == \"none\") " );
        buff.append( "                        tds[idx].style.display = \"\";" );
        buff.append( "                    else{" );
        buff.append( "                        tds[idx].style.display = \"none\";" );
        buff.append( "                    }" );
        buff.append( "                }" );
        buff.append( "            }" );
        buff.append( " }" );
        buff.append( "</script>" );
        sink.rawText( buff.toString() );
    }
}