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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.cobertura.coveragedata.CoverageData;
import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.ProjectData;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.surefire.report.SurefireReportParser;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.reporting.MavenReportException;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.codehaus.mojo.dashboard.report.plugin.beans.AbstractReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleError;
import org.codehaus.mojo.dashboard.report.plugin.beans.CheckstyleReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.CloverReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.CoberturaReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.CpdReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.DashBoardMavenProject;
import org.codehaus.mojo.dashboard.report.plugin.beans.FindBugsReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.JDependReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.PmdReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.SurefireReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.TagListReportBean;
import org.codehaus.mojo.dashboard.report.plugin.beans.XRefPackageBean;
import org.codehaus.mojo.jdepend.JDependXMLReportParser;
import org.codehaus.mojo.jdepend.objects.JDPackage;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringInputStream;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.interpolation.MapBasedValueSource;
import org.codehaus.plexus.util.interpolation.ObjectBasedValueSource;
import org.codehaus.plexus.util.interpolation.RegexBasedInterpolator;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * <ul>
 * <li>Add "CheckStyle Violations" graph written by <a href="mailto:srivollet@objectif-informatique.fr">Sylvain
 * Rivollet</a></li>
 * <li>Add Clover support written by <a href="mailto:mbeerman@yahoo.com">Matthew Beermann</a></li>
 * </ul>
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 */
public class DashBoardUtils
{
    /**
     * 
     */
    private Log log;

    /**
     * 
     */
    private static DashBoardUtils dashBoardUtils = null;

    /**
     * <p>
     * The Clover datafile location.
     * </p>
     * expression="${project.build.directory}/site/clover/clover.xml"
     */
    private final String cloverDataFile = "site/clover/clover.xml";

    /**
     * <p>
     * The Cobertura Datafile Location.
     * </p>
     * expression="${basedir}/cobertura.ser"
     */
    private final String coberturaDataFile = "cobertura.ser";

    /**
     * <p>
     * The PMD Datafile Location.
     * </p>
     * expression="${project.build.directory}/pmd.xml"
     */
    private final String pmdDataFile = "pmd.xml";

    /**
     * <p>
     * The CPD Datafile Location.
     * </p>
     * expression="${project.build.directory}/cpd.xml"
     */
    private final String cpdDataFile = "cpd.xml";

    /**
     * <p>
     * The checkstyle Datafile Location.
     * </p>
     * expression="${project.build.directory}/checkstyle-result.xml"
     */
    private final String checkstyleDataFile = "checkstyle-result.xml";

    /**
     * <p>
     * The JDepend Datafile Location.
     * </p>
     * expression="${project.build.directory}/jdepend-report.xml"
     */
    private final String jDependDataFile = "jdepend-report.xml";

    /**
     * <p>
     * The FindBugs Datafile Location.
     * </p>
     * expression="${project.build.directory}/findbugs.xml"
     */
    private final String findBugsDataFile = "findbugs.xml";

    /**
     * <p>
     * The Taglist Datafile Location.
     * </p>
     * expression="${project.build.directory}/taglist/taglist.xml"
     */
    private static String taglistDataFile = "taglist/taglist.xml";

    /**
     * The local repository.
     */
    private ArtifactRepository localRepo;

    /**
     * Project builder
     */
    private MavenProjectBuilder projectBuilder;

    /**
     * 
     */
    private final HashMap projectMap = new HashMap();

    private boolean dbPersist = false;

    /**
     * @param log
     * @param mavenProjectBuilder
     * @param localRepository
     * @return
     */
    public static DashBoardUtils getInstance( Log log, MavenProjectBuilder mavenProjectBuilder,
                                              ArtifactRepository localRepository, boolean dbPersist )
    {

        if ( dashBoardUtils == null )
        {
            dashBoardUtils = new DashBoardUtils( log, mavenProjectBuilder, localRepository, dbPersist );
        }
        else
        {
            dashBoardUtils.log = log;
            dashBoardUtils.projectBuilder = mavenProjectBuilder;
            dashBoardUtils.localRepo = localRepository;
            dashBoardUtils.dbPersist = dbPersist;
        }
        return dashBoardUtils;
    }

    /**
     * private Constructor
     */
    private DashBoardUtils( Log log, MavenProjectBuilder mavenProjectBuilder, ArtifactRepository localRepository,
                            boolean dbPersist )
    {
        this.log = log;
        this.projectBuilder = mavenProjectBuilder;
        this.localRepo = localRepository;
        this.dbPersist = dbPersist;
    }

    public DashBoardMavenProject getDashBoardMavenProject( MavenProject project, String dashboardDataFile,
                                                           Date generatedDate )
    {

        String projectName = project.getName();
        // Fixes MOJO-801. NPE in a particular three level multimodule build
        this.fillProjectMap( project );

        DashBoardMavenProject mavenProject;
        if ( project.getModules().size() > 0 )
        {
            // String artefactId = project.getGroupId() + "." + project.getArtifactId();
            mavenProject =
                new DashBoardMavenProject( project.getArtifactId(), project.getGroupId(), projectName,
                                           project.getVersion() );
            for ( int i = 0; i < project.getModules().size(); i++ )
            {
                String modulename = (String) project.getModules().get( i );

                MavenProject proj = this.getModuleMavenProject( project, modulename );
                String key = proj.getGroupId() + "." + proj.getArtifactId();
                if ( this.projectMap.containsKey( key ) )
                {
                    MavenProject realproj = (MavenProject) this.projectMap.get( key );
                    DashBoardMavenProject subMavenProject =
                        dashBoardUtils.getDashBoardMavenProject( realproj, dashboardDataFile, generatedDate );
                    mavenProject.addModule( subMavenProject );
                }

            }

        }
        else
        {
            mavenProject =
                new DashBoardMavenProject( project.getArtifactId(), project.getGroupId(), projectName,
                                           project.getVersion() );
            for ( Iterator reports = project.getReportPlugins().iterator(); reports.hasNext(); )
            {
                ReportPlugin report = (ReportPlugin) reports.next();
                String artifactId = report.getArtifactId();
                AbstractReportBean dashBoardReport = null;
                if ( "maven-checkstyle-plugin".equals( artifactId ) || "checkstyle-maven-plugin".equals( artifactId ) )
                {
                    dashBoardReport = this.getCheckstyleReport( project, generatedDate );
                }
                else if ( "maven-clover-plugin".equals( artifactId ) )
                {
                    dashBoardReport = this.getCloverReport( project, generatedDate );
                }
                else if ( "maven-surefire-report-plugin".equals( artifactId )
                                || "surefire-report-maven-plugin".equals( artifactId ) )
                {
                    dashBoardReport = this.getSurefireReport( project, generatedDate );
                }
                else if ( "cobertura-maven-plugin".equals( artifactId ) || "maven-cobertura-plugin".equals( artifactId ) )
                {
                    dashBoardReport = this.getCoberturaReport( project, generatedDate );
                }
                else if ( "maven-pmd-plugin".equals( artifactId ) || "pmd-maven-plugin".equals( artifactId ) )
                {
                    dashBoardReport = this.getCpdReport( project, generatedDate );
                    if ( dashBoardReport != null )
                    {
                        mavenProject.addReport( dashBoardReport );
                    }
                    dashBoardReport = this.getPmdReport( project, generatedDate );
                }
                else if ( "maven-findbugs-plugin".equals( artifactId ) || "findbugs-maven-plugin".equals( artifactId ) )
                {
                    dashBoardReport = this.getFindBugsReport( project, generatedDate );
                }
                else if ( "maven-jdepend-plugin".equals( artifactId ) || "jdepend-maven-plugin".equals( artifactId ) )
                {
                    if ( !this.dbPersist )
                    {
                        dashBoardReport = this.getJDependReport( project, generatedDate );
                    }
                }
                else if ( "maven-taglist-plugin".equals( artifactId ) || "taglist-maven-plugin".equals( artifactId ) )
                {
                    dashBoardReport = this.getTaglistReport( project, generatedDate );
                }
                if ( dashBoardReport != null )
                {
                    mavenProject.addReport( dashBoardReport );
                }
            }
        }
        return mavenProject;
    }

    /**
     * Fixes MOJO-801. NPE in a particular three level multimodule build
     * 
     * @param project
     */
    private void fillProjectMap( MavenProject project )
    {
        if ( project.getModules().size() > 0 )
        {
            Iterator iter = project.getCollectedProjects().iterator();

            while ( iter.hasNext() )
            {
                MavenProject proj = (MavenProject) iter.next();
                String key = proj.getGroupId() + "." + proj.getArtifactId();
                if ( !this.projectMap.containsKey( key ) )
                {
                    this.projectMap.put( key, proj );
                }
            }
        }
        else
        {
            String key = project.getGroupId() + "." + project.getArtifactId();
            if ( !this.projectMap.containsKey( key ) )
            {
                this.projectMap.put( key, project );
            }
        }
    }

    /**
     * @param project
     * @param module
     * @return
     */
    private MavenProject getModuleMavenProject( MavenProject project, String module )
    {
        MavenProject projectModule = null;
        File f = new File( project.getBasedir(), module + "/pom.xml" );
        if ( f.exists() )
        {
            try
            {
                projectModule = this.projectBuilder.build( f, this.localRepo, null );
            }
            catch ( ProjectBuildingException e )
            {
                this.log.error( "Unable to read local module-POM \"" + module + "\".", e );
            }
        }
        return projectModule;
    }

    /**
     * get the CoberturaReportBean which represents the Cobertura .ser file analysis
     * 
     * @param projectName
     * @param coberturaDataFile
     * @return
     */
    protected CoberturaReportBean getCoberturaReport( MavenProject project, Date generatedDate )
    {

        CoberturaReportBean coberturaReport = new CoberturaReportBean( generatedDate );

        File coberturaFile = this.getCoberturaDataFile( project );

        if ( coberturaFile != null && coberturaFile.exists() && coberturaFile.isFile() )
        {
            this.log.debug( "getCoberturaReport = " + coberturaFile.toString() );
            ProjectData projectData = CoverageDataFileHandler.loadCoverageData( coberturaFile );
            if ( projectData != null )
            {
                coberturaReport.setNbClasses( projectData.getNumberOfClasses() );
                double lineCoverage = -1;
                double branchCoverage = -1;

                if ( projectData.getNumberOfValidLines() > 0 )
                {
                    lineCoverage = projectData.getLineCoverageRate();
                }
                if ( projectData.getNumberOfValidBranches() > 0 )
                {
                    branchCoverage = projectData.getBranchCoverageRate();
                }
                coberturaReport.setLineCoverRate( lineCoverage );
                coberturaReport.setBranchCoverRate( branchCoverage );
                coberturaReport.setNumberOfCoveredBranches( projectData.getNumberOfCoveredBranches() );
                coberturaReport.setNumberOfValidBranches( projectData.getNumberOfValidBranches() );
                coberturaReport.setNumberOfCoveredLines( projectData.getNumberOfCoveredLines() );
                coberturaReport.setNumberOfValidLines( projectData.getNumberOfValidLines() );
                // System.out.println( "DashBoardReportGenerator this.use_cobertura_report = true" );
            }
            else
            {
                coberturaReport = null;
            }
        }
        else
        {
            coberturaReport = null;
        }
        return coberturaReport;
    }

    /**
     * @param projectName
     * @param checkstyleDataFile
     * @return
     */
    protected CheckstyleReportBean getCheckstyleReport( MavenProject project, Date generatedDate )
    {
        CheckstyleReportBean checkstyleReport = new CheckstyleReportBean( generatedDate );
        File checkstyleFile = new File( project.getBuild().getDirectory(), this.checkstyleDataFile );
        if ( checkstyleFile.exists() && checkstyleFile.isFile() )
        {
            Document doc = this.getDocument( checkstyleFile );
            if ( doc != null )
            {
                Element cpd = doc.getDocumentElement();
                NodeList files = cpd.getElementsByTagName( "file" );
                NodeList total = cpd.getElementsByTagName( "error" );
                int nbInfos = 0;
                int nbWarnings = 0;
                int nbErrors = 0;
                for ( int i = 0; i < total.getLength(); i++ )
                {
                    Element error = (Element) total.item( i );
                    CheckstyleError checkstyleError = new CheckstyleError();
                    String severity = error.getAttribute( "severity" );
                    if ( severity.equalsIgnoreCase( "info" ) )
                    {
                        nbInfos++;
                    }
                    else if ( severity.equalsIgnoreCase( "warning" ) )
                    {
                        nbWarnings++;
                    }
                    else if ( severity.equalsIgnoreCase( "error" ) )
                    {
                        nbErrors++;
                    }
                    // error management for Checkstyle Violations Chart. Fixes MOJO-679 .
                    // Written by <a href="mailto:srivollet@objectif-informatique.fr">Sylvain Rivollet</a>.
                    checkstyleError.setType( error.getAttribute( "severity" ) );
                    checkstyleError.setNameClass( error.getAttribute( "source" ) );
                    checkstyleError.setMessage( error.getAttribute( "message" ) );
                    checkstyleReport.addError( checkstyleError );
                }
                checkstyleReport.setNbClasses( files.getLength() );
                checkstyleReport.setNbErrors( nbErrors );
                checkstyleReport.setNbInfos( nbInfos );
                checkstyleReport.setNbTotal( total.getLength() );
                checkstyleReport.setNbWarnings( nbWarnings );
            }
            else
            {
                checkstyleReport = null;
            }
        }
        else
        {
            checkstyleReport = null;
        }

        return checkstyleReport;

    }

    /**
     * @param project
     * @return
     */
    protected CpdReportBean getCpdReport( MavenProject project, Date generatedDate )
    {
        CpdReportBean cpdReport = new CpdReportBean( generatedDate );
        File cpdFile = new File( project.getBuild().getDirectory(), this.cpdDataFile );
        if ( cpdFile.exists() && cpdFile.isFile() )
        {
            Document doc = this.getDocument( cpdFile );
            if ( doc != null )
            {
                Element cpd = doc.getDocumentElement();
                NodeList duplications = cpd.getElementsByTagName( "duplication" );
                NodeList files = cpd.getElementsByTagName( "file" );
                Vector filelist = new Vector();
                for ( int i = 0; i < files.getLength(); i++ )
                {
                    Element file = (Element) files.item( i );
                    if ( !filelist.contains( file.getAttribute( "path" ) ) )
                    {
                        filelist.add( file.getAttribute( "path" ) );
                    }
                }
                cpdReport.setNbClasses( filelist.size() );
                cpdReport.setNbDuplicate( duplications.getLength() );
            }
            else
            {
                cpdReport = null;
            }
        }
        else
        {
            cpdReport = null;
        }
        return cpdReport;
    }

    /**
     * @param project
     * @return
     */
    protected PmdReportBean getPmdReport( MavenProject project, Date generatedDate )
    {
        PmdReportBean pmdReport = new PmdReportBean( generatedDate );
        File pmdFile = new File( project.getBuild().getDirectory(), this.pmdDataFile );
        if ( pmdFile.exists() && pmdFile.isFile() )
        {
            Document doc = this.getDocument( pmdFile );
            if ( doc != null )
            {
                Element pmd = doc.getDocumentElement();
                NodeList files = pmd.getElementsByTagName( "file" );
                NodeList violations = pmd.getElementsByTagName( "violation" );
                pmdReport.setNbClasses( files.getLength() );
                pmdReport.setNbViolations( violations.getLength() );
            }
            else
            {
                pmdReport = null;
            }
        }
        else
        {
            pmdReport = null;
        }
        return pmdReport;

    }

    /**
     * @param project
     * @return
     */
    protected SurefireReportBean getSurefireReport( MavenProject project, Date generatedDate )
    {
        SurefireReportBean surefireReport = new SurefireReportBean( generatedDate );

        File surefireDirectory = this.getSurefireDirectory( project );

        if ( surefireDirectory != null && surefireDirectory.exists() && surefireDirectory.isDirectory() )
        {
            // SurefireReportParser report = new SurefireReportParser( surefireDirectory, Locale.getDefault() );
            File[] surefireDirectories = { surefireDirectory };
            SurefireReportParser report = new SurefireReportParser( surefireDirectories, Locale.getDefault() );
            List testSuites;
            try
            {
                testSuites = report.parseXMLReportFiles();
                Map summary = report.getSummary( testSuites );

                surefireReport.setNbTests( Integer.parseInt( (String) summary.get( "totalTests" ) ) );

                surefireReport.setNbErrors( Integer.parseInt( (String) summary.get( "totalErrors" ) ) );

                surefireReport.setNbFailures( Integer.parseInt( (String) summary.get( "totalFailures" ) ) );

                if ( summary.get( "totalSkipped" ) != null )
                {
                    surefireReport.setNbSkipped( Integer.parseInt( (String) summary.get( "totalSkipped" ) ) );
                }
                else
                {
                    surefireReport.setNbSkipped( 0 );
                }
                // MOJO-624 correction
                NumberFormat format = NumberFormat.getInstance( Locale.getDefault() );
                String percent = (String) summary.get( "totalPercentage" );
                try
                {
                    surefireReport.setSucessRate( format.parse( percent ).doubleValue() );
                }
                catch ( ParseException e )
                {
                    this.log.info( "SurefireReportBean setSucessRate Unexpected number format exception..", e );
                    surefireReport.setSucessRate( 0.0 );
                }
                String elapsed = (String) summary.get( "totalElapsedTime" );
                try
                {
                    surefireReport.setElapsedTime( format.parse( elapsed ).doubleValue() );
                }
                catch ( ParseException e )
                {
                    this.log.info( "SurefireReportBean setElapsedTime Unexpected number format exception..", e );
                    surefireReport.setElapsedTime( 0.0 );
                }

            }
            catch ( MavenReportException e )
            {
                this.log.error( "SurefireReportBean creation failed.", e );
                surefireReport = null;
            }
        }
        else
        {
            surefireReport = null;
        }
        return surefireReport;

    }

    /**
     * Fixes MOJO-813. addition of Clover support written by <a href="mailto:mbeerman@yahoo.com">Matthew Beermann</a>
     * 
     * @param project
     * @return
     */
    protected CloverReportBean getCloverReport( MavenProject project, Date generatedDate )
    {
        CloverReportBean cloverReport = new CloverReportBean( generatedDate );

        File cloverReportFile = new File( project.getBuild().getDirectory(), this.cloverDataFile );

        if ( cloverReportFile != null && cloverReportFile.exists() && cloverReportFile.isFile() )
        {
            try
            {
                Document doc = this.getDocument( cloverReportFile );
                if ( doc != null )
                {

                    NodeList allMetrics = doc.getElementsByTagName( "metrics" );
                    Element metrics = null;
                    for ( int i = 0; i < allMetrics.getLength(); i++ )
                    {
                        Element candidate = (Element) allMetrics.item( i );
                        if ( candidate.getParentNode().getNodeName().equals( "project" ) )
                        {
                            metrics = candidate;
                            break;
                        }
                    }
                    if ( metrics == null )
                    {
                        return null;
                    }

                    cloverReport.setConditionals( Integer.parseInt( metrics.getAttribute( "conditionals" ) ) );
                    cloverReport.setStatements( Integer.parseInt( metrics.getAttribute( "statements" ) ) );
                    cloverReport.setMethods( Integer.parseInt( metrics.getAttribute( "methods" ) ) );
                    cloverReport.setElements( Integer.parseInt( metrics.getAttribute( "elements" ) ) );

                    cloverReport.setCoveredConditionals( Integer.parseInt( metrics.getAttribute( "coveredconditionals" ) ) );
                    cloverReport.setCoveredStatements( Integer.parseInt( metrics.getAttribute( "coveredstatements" ) ) );
                    cloverReport.setCoveredMethods( Integer.parseInt( metrics.getAttribute( "coveredmethods" ) ) );
                    cloverReport.setCoveredElements( Integer.parseInt( metrics.getAttribute( "coveredelements" ) ) );
                }
                else
                {
                    cloverReport = null;
                }

            }
            catch ( Exception e )
            {
                this.log.error( "CloverReportBean creation failed.", e );
                cloverReport = null;
            }
        }
        else
        {
            cloverReport = null;
        }

        return cloverReport;
    }

    /**
     * get the JDependReportBean which represents the Cobertura .ser file analysis
     * 
     * @param project
     * @return
     */
    protected JDependReportBean getJDependReport( MavenProject project, Date generatedDate )
    {

        this.log.debug( "JDependReportBean creation = " + project.getName() + "." );
        JDependReportBean jDependReport = new JDependReportBean( generatedDate );

        File coberturaFile = getCoberturaDataFile( project );
        File jDependFile = new File( project.getBuild().getDirectory(), this.jDependDataFile );
        ProjectData projectData = null;
        if ( coberturaFile.exists() && coberturaFile.isFile() )
        {
            projectData = CoverageDataFileHandler.loadCoverageData( coberturaFile );
        }
        if ( jDependFile.exists() && jDependFile.isFile() )
        {
            JDependXMLReportParser xmlParser;
            try
            {
                xmlParser = new JDependXMLReportParser( jDependFile );
                List packages = xmlParser.getPackages();
                if ( packages != null && packages.size() > 0 )
                {

                    Iterator iter = packages.iterator();
                    while ( iter.hasNext() )
                    {
                        JDPackage pack = (JDPackage) iter.next();
                        XRefPackageBean bean = new XRefPackageBean();
                        bean.setPackageName( pack.getPackageName() );
                        int iCa = Integer.parseInt( pack.getStats().getCa() );
                        bean.setAfferentCoupling( new Integer( iCa ) );
                        if ( projectData != null )
                        {
                            CoverageData data = projectData.getChild( pack.getPackageName() );
                            if ( data != null )
                            {
                                bean.setLineCoverRate( data.getLineCoverageRate() );
                                bean.setBranchCoverRate( data.getBranchCoverageRate() );
                            }
                        }
                        jDependReport.addPackage( bean );
                    }
                }
                else
                {
                    jDependReport = null;
                }
            }
            catch ( ParserConfigurationException e )
            {
                this.log.error( "JDependReportBean creation failed.", e );
                jDependReport = null;
            }
            catch ( SAXException e )
            {
                this.log.error( "JDependReportBean creation failed.", e );
                jDependReport = null;
            }
            catch ( IOException e )
            {
                this.log.error( "JDependReportBean creation failed.", e );
                jDependReport = null;
            }
        }
        else
        {
            jDependReport = null;
        }

        return jDependReport;

    }

    /**
     * @param projectName
     * @param FindBugsDataFile
     * @return
     */
    protected FindBugsReportBean getFindBugsReport( MavenProject project, Date generatedDate )
    {
        FindBugsReportBean findBugsReport = new FindBugsReportBean( generatedDate );
        File findBugsFile = new File( project.getBuild().getDirectory(), this.findBugsDataFile );
        if ( findBugsFile.exists() && findBugsFile.isFile() )
        {
            Document doc = this.getDocument( findBugsFile );
            if ( doc != null )
            {
                Element root = doc.getDocumentElement();
                NodeList files = root.getElementsByTagName( "file" );
                NodeList bugs = root.getElementsByTagName( "BugInstance" );
                NodeList missing = root.getElementsByTagName( "MissingClass" );
                findBugsReport.setNbClasses( files.getLength() );
                findBugsReport.setNbErrors( 0 );
                findBugsReport.setNbBugs( bugs.getLength() );
                findBugsReport.setNbMissingClasses( missing.getLength() );
                for ( int i = 0; i < bugs.getLength(); i++ )
                {
                    Element bugInstance = (Element) bugs.item( i );
                    String category = bugInstance.getAttribute( "category" );
                    findBugsReport.addCategory( category );
                    String priority = bugInstance.getAttribute( "priority" );
                    findBugsReport.addPriority( priority );
                }
            }
            else
            {
                findBugsReport = null;
            }
        }
        else
        {
            findBugsReport = null;
        }

        return findBugsReport;
    }

    /**
     * @param project
     * @param generatedDate
     * @return
     */
    protected TagListReportBean getTaglistReport( MavenProject project, Date generatedDate )
    {
        TagListReportBean taglistReport = new TagListReportBean( generatedDate );
        File taglistFile = new File( project.getBuild().getDirectory(), taglistDataFile );
        if ( taglistFile.exists() && taglistFile.isFile() )
        {
            Document doc = this.getDocument( taglistFile );
            if ( doc != null )
            {
                Element root = doc.getDocumentElement();
                NodeList tags = root.getElementsByTagName( "tag" );
                for ( int i = 0; i < tags.getLength(); i++ )
                {
                    Element tag = (Element) tags.item( i );
                    String tagName = tag.getAttribute( "name" );
                    Integer tagCount = Integer.valueOf( tag.getAttribute( "count" ) );
                    taglistReport.setTag( tagName, tagCount );
                }
                NodeList files = root.getElementsByTagName( "file" );
                Map classFiles = new HashMap();
                for ( int i = 0; i < files.getLength(); i++ )
                {
                    Element file = (Element) files.item( i );
                    String className = file.getAttribute( "name" );
                    classFiles.put( className, new Integer( 1 ) );
                }
                taglistReport.setNbClasses( classFiles.size() );
            }
            else
            {
                taglistReport = null;
            }
        }
        else
        {
            taglistReport = null;
        }

        return taglistReport;
    }

    /**
     * @param xmlFilename
     * @return
     */
    private Document getDocument( File xmlFilename )
    {
        Document doc = null;

        FileInputStream fileInputStream;
        try
        {
            fileInputStream = new FileInputStream( xmlFilename );
            InputStreamReader inputStreamReader = new InputStreamReader( fileInputStream, "UTF-8" );
            InputSource inputSource = new InputSource( inputStreamReader );

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // this.log.info(builder.getClass().toString());
            doc = builder.parse( inputSource );
        }
        catch ( FileNotFoundException e )
        {
            this.log.error( "DashBoardUtils getDocument() FileNotFoundException : ", e );
            doc = null;
        }
        catch ( UnsupportedEncodingException e )
        {
            this.log.error( "DashBoardUtils getDocument() UnsupportedEncodingException : ", e );
            doc = null;
        }
        catch ( ParserConfigurationException e )
        {
            this.log.error( "DashBoardUtils getDocument() ParserConfigurationException : ", e );
            doc = null;
        }
        catch ( FactoryConfigurationError e )
        {
            this.log.error( "DashBoardUtils getDocument() FactoryConfigurationError : ", e );
            doc = null;
        }
        catch ( SAXException e )
        {
            this.log.error( "DashBoardUtils getDocument() SAXException : ", e );
            doc = null;
        }
        catch ( IOException e )
        {
            this.log.error( "DashBoardUtils getDocument() IOException : ", e );
            doc = null;
        }

        return doc;
    }

    /**
     * @param project
     * @param dashboardDataFile
     * @return
     */
    protected DashBoardMavenProject readXMLDashBoardReport( MavenProject project, String dashboardDataFile )
    {
        DashBoardMavenProject mavenProject = null;
        try
        {

            // Instanciation de la classe XStream
            XStream xstream = new XStream( new DomDriver() );
            // Instanciation d'un fichier
            File fichier = new File( project.getBuild().getDirectory(), dashboardDataFile );

            // Redirection du fichier /target/dashboard-report.xml vers un flux
            // d'entrée fichier
            FileInputStream fis = new FileInputStream( fichier );

            try
            {
                xstream.setMode( XStream.NO_REFERENCES );
                // Convertion du contenu de l'objet DashBoardReportBean en XML
                xstream.alias( "MavenProject", DashBoardMavenProject.class );
                xstream.alias( "xrefpackage", XRefPackageBean.class );
                xstream.alias( "checkstyleerror", CheckstyleError.class );
                xstream.useAttributeFor( "id", Long.class );
                xstream.useAttributeFor( "artifactId", String.class );
                xstream.useAttributeFor( "groupId", String.class );
                xstream.useAttributeFor( "version", String.class );
                xstream.useAttributeFor( "projectName", String.class );
                xstream.useAttributeFor( "generatedDate", Date.class );
                xstream.useAttributeFor( "averageAfferentCoupling", String.class );
                xstream.useAttributeFor( "nbPackages", String.class );
                xstream.useAttributeFor( "sumAC", String.class );
                // Désérialisation du fichier /target/dashboard-multi-report.xml vers un nouvel
                // objet DashBoardReportBean
                mavenProject = (DashBoardMavenProject) xstream.fromXML( fis );

            }
            finally
            {
                // On s'assure de fermer le flux quoi qu'il arrive
                fis.close();
            }
        }
        catch ( FileNotFoundException e )
        {
            System.out.println( "readXMLDashBoardReport() for project " + project.getName() + " failed :"
                            + e.getMessage() );
            mavenProject = null;
        }
        catch ( IOException ioe )
        {
            System.out.println( "readXMLDashBoardReport() for project " + project.getName() + " failed :"
                            + ioe.getMessage() );
            mavenProject = null;
        }
        return mavenProject;
    }

    /**
     * @param project
     * @param dashBoardReport
     * @param dashboardDataFile
     */
    protected void saveXMLDashBoardReport( MavenProject project, DashBoardMavenProject mavenProject,
                                           String dashboardDataFile )
    {
        try
        {
            // Instanciation de la classe XStream
            XStream xstream = new XStream( new DomDriver() );
            // Instanciation d'un fichier
            File dir = new File( project.getBuild().getDirectory() );
            if ( !dir.exists() )
            {
                dir.mkdirs();
            }
            File fichier = new File( dir, dashboardDataFile );
            // Instanciation d'un flux de sortie fichier vers le xml
            FileOutputStream fos = new FileOutputStream( fichier );
            OutputStreamWriter output = new OutputStreamWriter( fos, "UTF-8" );
            try
            {
                output.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
                xstream.setMode( XStream.NO_REFERENCES );
                // Convertion du contenu de l'objet DashBoardReportBean en XML
                xstream.alias( "MavenProject", DashBoardMavenProject.class );
                xstream.alias( "xrefpackage", XRefPackageBean.class );
                xstream.alias( "checkstyleerror", CheckstyleError.class );
                xstream.useAttributeFor( "id", Long.class );
                xstream.useAttributeFor( "artifactId", String.class );
                xstream.useAttributeFor( "groupId", String.class );
                xstream.useAttributeFor( "version", String.class );
                xstream.useAttributeFor( "projectName", String.class );
                xstream.useAttributeFor( "generatedDate", Date.class );
                xstream.useAttributeFor( "averageAfferentCoupling", String.class );
                xstream.useAttributeFor( "nbPackages", String.class );
                xstream.useAttributeFor( "sumAC", String.class );
                // Sérialisation de l'objet dashBoardReport dans /target/dashboard-report.xml
                xstream.toXML( mavenProject, output );
            }
            finally
            {
                // On s'assure de fermer le flux quoi qu'il arrive
                fos.close();
            }

        }
        catch ( FileNotFoundException e )
        {
            this.log.error( "saveXMLDashBoardReport() failed.", e );
        }
        catch ( IOException ioe )
        {
            this.log.error( "saveXMLDashBoardReport() failed.", ioe );
        }
    }

    /**
     * get the Cobertura datafile. MOJO-644 : pb with different versions of Cobertura plugin. for cobertura 2.1
     * default-value="${project.build.directory}/cobertura/cobertura.ser" for cobertura 2.0
     * default-value="${basedir}/cobertura.ser" MOJO-674 : NumberFormatException fixed by using DefaultArtifactVersion
     * class MOJO-749 : NPE if dashboard called on parent project (when no cobertura version in pom.xml)
     * 
     * @param project
     *            MavenProject to be processed
     * @return cobertura.ser File object
     */
    private File getCoberturaDataFile( MavenProject project )
    {
        /*
         * HACK to address the broken datafile location code in Cobertura See
         * https://sourceforge.net/tracker/index.php?func=detail&aid=1543280&group_id=130558&atid=720017 Until patch is
         * commited, this hack will be in place.
         */
        File coberturaFile = null;
        File brokenDatafile = new File( project.getBasedir(), this.coberturaDataFile );
        File dataFile = new File( project.getBuild().getDirectory(), "cobertura/" + this.coberturaDataFile );
        if ( brokenDatafile != null && brokenDatafile.exists() )
        {
            try
            {
                FileUtils.copyFile( brokenDatafile, dataFile );
                brokenDatafile.delete();
                coberturaFile = dataFile;
            }
            catch ( IOException e )
            {
                coberturaFile = null;
            }
        }

        if ( dataFile != null && dataFile.exists() )
        {
            coberturaFile = dataFile;
        }
        else
        {
            coberturaFile = null;
        }
        return coberturaFile;
    }

    /**
     * get the Surefire directory as configured in surefire plugin configuration section. MOJO-615 : pb with different
     * surefire reports directories
     * 
     * @param project
     *            The Maven Project object
     * @return the surefire directory
     */
    private File getSurefireDirectory( MavenProject project )
    {
        File surefireDir = null;
        String dirPath =
            this.getConfiguration( project, "maven-surefire-plugin", "org.apache.maven.plugins", "reportsDirectory",
                                   "${project.build.directory}/surefire-reports" );
        if ( dirPath != null && dirPath.length() > 0 )
        {
            surefireDir = new File( dirPath );
        }
        return surefireDir;
    }

    /**
     * @param project
     * @param pluginArtifact
     * @param optionName
     * @param defaultValue
     * @return
     */
    private String getConfiguration( MavenProject project, String pluginArtifact, String pluginGroupId,
                                     String optionName, String defaultValue )
    {
        String result = null;
        String value = "";
        try
        {
            value = getMavenPluginConfiguration( project, pluginArtifact, pluginGroupId, optionName, "" );
            if ( value != null && value.length() > 0 )
            {
                if ( value.indexOf( "$" ) > -1 )
                {
                    result = this.getInterpolatorValue( project, value );
                }
                else
                {

                    File dir = new File( value );
                    boolean isExists = dir.exists();
                    if ( !isExists )
                    {
                        File resultFile = FileUtils.resolveFile( project.getBasedir(), value );
                        result = resultFile.getAbsolutePath();
                    }
                    else
                    {
                        result = value;
                    }
                }
            }
            else
            {
                result = this.getInterpolatorValue( project, defaultValue );
            }
        }
        catch ( IOException e )
        {
            result = null;
            this.log.error( "DashBoardUtils getConfiguration() : ", e );
        }
        return result;
    }

    /**
     * @param project
     * @param value
     * @return
     */
    private String getInterpolatorValue( MavenProject project, String value )
    {

        RegexBasedInterpolator interpolator = new RegexBasedInterpolator();
        interpolator.addValueSource( new ObjectBasedValueSource( project ) );
        interpolator.addValueSource( new MapBasedValueSource( project.getProperties() ) );

        String result = interpolator.interpolate( value, "project" );

        return result;
    }

    /**
     * Return the optionName value defined in a project for a given artifactId plugin.
     * 
     * @param project
     *            not null
     * @param pluginArtifact
     *            not null
     * @param optionName
     *            an Xpath expression from the plugin <code>&lt;configuration/&gt;</code>
     * @param defaultValue
     * @return the value for the option name (comma separated value in the case of list) or null if not found
     * @throws IOException
     *             if any
     */
    private static String getMavenPluginConfiguration( MavenProject project, String pluginArtifact,
                                                       String pluginGroupId, String optionName, String defaultValue )
        throws IOException
    {
        for ( Iterator it = project.getModel().getBuild().getPlugins().iterator(); it.hasNext(); )
        {
            Plugin plugin = (Plugin) it.next();

            if ( ( plugin.getGroupId().equals( pluginGroupId ) ) && ( plugin.getArtifactId().equals( pluginArtifact ) ) )
            {
                Xpp3Dom pluginConf = (Xpp3Dom) plugin.getConfiguration();

                if ( pluginConf != null )
                {
                    StringBuffer sb = new StringBuffer();
                    try
                    {
                        Document doc =
                            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                                                                                             new StringInputStream(
                                                                                                                    pluginConf.toString() ) );

                        XObject obj = XPathAPI.eval( doc, "//configuration//" + optionName );

                        if ( StringUtils.isNotEmpty( obj.toString() ) )
                        {
                            StringTokenizer token = new StringTokenizer( obj.toString(), "\n " );
                            while ( token.hasMoreTokens() )
                            {
                                sb.append( token.nextToken().trim() );
                                if ( token.hasMoreElements() )
                                {
                                    sb.append( "," );
                                }
                            }
                            return sb.toString();
                        }
                    }
                    catch ( Exception e )
                    {
                        throw new IOException( "Exception occured" + e.getMessage() );
                    }
                }
            }
        }

        return defaultValue;
    }
}
