package org.codehaus.mojo.l10n;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.AbstractMavenReportRenderer;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * A simple report for keeping track of l10n status. It lists all bundle properties
 * files and the number of properties in them. For a configurable list of locales it also
 * tracks the progress of localization.
 *
 * @author <a href="mkleint@codehaus.org">Milos Kleint</a>
 * @goal report
 */
public class L10NStatusReport
    extends AbstractMavenReport
{

    /**
     * Report output directory.
     *
     * @parameter default-value="${project.build.directory}/generated-site/xdoc"
     */
    private File outputDirectory;

    /**
     * Doxia Site Renderer.
     *
     * @component
     */
    private Renderer siteRenderer;

    /**
     * A list of locale strings that are to be watched for l10n status.
     *
     * @parameter
     */
    private List locales;

    /**
     * The Maven Project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The list of resources that are scanned for properties bundles.
     *
     * @parameter default-value="${project.resources}"
     * @readonly
     */
    private List resources;

    /**
     * A list of exclude patterns to use. By default no files are excluded.
     *
     * @parameter
     */
    private List excludes;

    /**
     * A list of include patterns to use. By default all <code>*.properties</code> files are included.
     *
     * @parameter
     */
    private List includes;

    /**
     * The projects in the reactor for aggregation report.
     *
     * @parameter expression="${reactorProjects}"
     * @readonly
     */
    protected List reactorProjects;

    /**
     * Whether to build an aggregated report at the root, or build individual reports.
     *
     * @parameter expression="${maven.l10n.aggregate}" default-value="false"
     */
    protected boolean aggregate;


    private static final String[] DEFAULT_INCLUDES = {"**/*.properties"};

    private static final String[] EMPTY_STRING_ARRAY = {};


    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
     */
    protected Renderer getSiteRenderer()
    {
        return siteRenderer;
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
     */
    protected String getOutputDirectory()
    {
        return outputDirectory.getAbsolutePath();
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
     */
    protected MavenProject getProject()
    {
        return project;
    }

    public boolean canGenerateReport()
    {
        return canGenerateReport( constructResourceDirs() );
    }

    /**
     * @param sourceDirs
     * @return true if the report can be generated
     */
    protected boolean canGenerateReport( Map sourceDirs )
    {
        boolean canGenerate = !sourceDirs.isEmpty();

        if ( aggregate && !project.isExecutionRoot() )
        {
            canGenerate = false;
        }
        return canGenerate;
    }

    /**
     * Collects resource definitions from all projects in reactor.
     *
     * @return
     */
    protected Map constructResourceDirs()
    {
        Map sourceDirs = new HashMap();
        if ( aggregate )
        {
            for ( Iterator i = reactorProjects.iterator(); i.hasNext(); )
            {
                MavenProject prj = (MavenProject) i.next();
                if ( prj.getResources() != null && !prj.getResources().isEmpty() )
                {
                    sourceDirs.put( prj, new ArrayList( prj.getResources() ) );
                }

            }
        }
        else
        {
            if ( resources != null && !resources.isEmpty() )
            {
                sourceDirs.put( project, new ArrayList( resources ) );
            }
        }
        return sourceDirs;
    }


    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale)
     */
    protected void executeReport( Locale locale )
        throws MavenReportException
    {
        Set included = new TreeSet( new WrapperComparator() );
        Map res = constructResourceDirs();
        for ( Iterator it = res.keySet().iterator(); it.hasNext(); )
        {
            MavenProject prj = (MavenProject) it.next();
            List lst = (List) res.get( prj );
            for ( Iterator i = lst.iterator(); i.hasNext(); )
            {
                Resource resource = (Resource) i.next();

                File resourceDirectory = new File( resource.getDirectory() );

                if ( !resourceDirectory.exists() )
                {
                    getLog().info( "Resource directory does not exist: " + resourceDirectory );
                    continue;
                }

                DirectoryScanner scanner = new DirectoryScanner();

                scanner.setBasedir( resource.getDirectory() );
                List allIncludes = new ArrayList();
                if ( resource.getIncludes() != null && !resource.getIncludes().isEmpty() )
                {
                    allIncludes.addAll( resource.getIncludes() );
                }
                if ( includes != null && !includes.isEmpty() )
                {
                    allIncludes.addAll( includes );
                }

                if ( allIncludes.isEmpty() )
                {
                    scanner.setIncludes( DEFAULT_INCLUDES );
                }
                else
                {
                    scanner.setIncludes( (String[]) allIncludes.toArray( EMPTY_STRING_ARRAY ) );
                }

                List allExcludes = new ArrayList();
                if ( resource.getExcludes() != null && !resource.getExcludes().isEmpty() )
                {
                    allExcludes.addAll( resource.getExcludes() );
                }
                else if ( excludes != null && !excludes.isEmpty() )
                {
                    allExcludes.addAll( excludes );
                }

                scanner.setExcludes( (String[]) allExcludes.toArray( EMPTY_STRING_ARRAY ) );

                scanner.addDefaultExcludes();
                scanner.scan();

                List includedFiles = Arrays.asList( scanner.getIncludedFiles() );
                for ( Iterator j = includedFiles.iterator(); j.hasNext(); )
                {
                    String name = (String) j.next();
                    File source = new File( resource.getDirectory(), name );
                    included.add( new Wrapper( name, source, prj ) );
                }
            }
        }

        // Write the overview
        L10NStatusRenderer r = new L10NStatusRenderer( getSink(), getBundle( locale ), included, locale );
        r.render();
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
     */
    public String getDescription( Locale locale )
    {
        return getBundle( locale ).getString( "report.l10n.description" );
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
     */
    public String getName( Locale locale )
    {
        return getBundle( locale ).getString( "report.l10n.name" );
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getOutputName()
     */
    public String getOutputName()
    {
        return "l10n-status";
    }

    private static ResourceBundle getBundle( Locale locale )
    {
        return ResourceBundle.getBundle( "l10n-status-report", locale, L10NStatusReport.class.getClassLoader() );
    }

    /**
     * Generates an overview page with a list of properties bundles
     * and a link to each locale's status.
     */
    class L10NStatusRenderer
        extends AbstractMavenReportRenderer
    {

        private final ResourceBundle bundle;

        /**
         * The locale in which the report will be rendered.
         */
        private final Locale rendererLocale;

        private Set files;

        private Pattern localedPattern = Pattern.compile( ".*_[a-zA-Z]{2}[_]?[a-zA-Z]{0,2}?\\.properties" );

        public L10NStatusRenderer( Sink sink, ResourceBundle bundle, Set files, Locale rendererLocale )
        {
            super( sink );

            this.bundle = bundle;
            this.files = files;
            this.rendererLocale = rendererLocale;
        }

        /**
         * @see org.apache.maven.reporting.MavenReportRenderer#getTitle()
         */
        public String getTitle()
        {
            return bundle.getString( "report.l10n.title" );
        }

        /**
         * @see org.apache.maven.reporting.AbstractMavenReportRenderer#renderBody()
         */
        public void renderBody()
        {
            startSection( getTitle() );

            paragraph( bundle.getString( "report.l10n.intro" ) );
            startSection( bundle.getString( "report.l10n.summary" ) );

            startTable();
            tableCaption( bundle.getString( "report.l10n.summary.caption" ) );
            String defaultLocaleColumnName = bundle.getString( "report.l10n.column.default" );
            String pathColumnName = bundle.getString( "report.l10n.column.path" );
            String missingFileLabel = bundle.getString( "report.l10n.missingFile" );
            String missingKeysLabel = bundle.getString( "report.l10n.missingKey" );
            String okLabel = bundle.getString( "report.l10n.ok" );
            String totalLabel = bundle.getString( "report.l10n.total" );
            String additionalKeysLabel = bundle.getString( "report.l10n.additional" );
            String nontranslatedKeysLabel = bundle.getString( "report.l10n.nontranslated" );
            String[] headers = new String[locales != null ? locales.size() + 2 : 2];
            Map localeDisplayNames = new HashMap();
            headers[0] = pathColumnName;
            headers[1] = defaultLocaleColumnName;
            if ( locales != null )
            {
                Iterator it = locales.iterator();
                int ind = 2;
                while ( it.hasNext() )
                {
                    final String localeCode = (String) it.next();
                    headers[ind] = localeCode;
                    ind = ind + 1;

                    Locale locale = createLocale( localeCode );
                    if ( locale == null )
                    {
                        // If the localeCode were in an unknown format use the localeCode itself as a fallback value
                        localeDisplayNames.put( localeCode, localeCode );
                    }
                    else
                    {
                        localeDisplayNames.put( localeCode, locale.getDisplayName( rendererLocale ) );
                    }
                }
            }
            tableHeader( headers );
            int[] count = new int[locales != null ? locales.size() + 1 : 1];
            Arrays.fill( count, 0 );
            Iterator it = files.iterator();
            MavenProject lastPrj = null;
            Set usedFiles = new TreeSet( new WrapperComparator() );
            while ( it.hasNext() )
            {
                Wrapper wr = (Wrapper) it.next();
                if ( reactorProjects.size() > 1 && ( lastPrj == null || lastPrj != wr.getProject() ) )
                {
                    lastPrj = wr.getProject();
                    sink.tableRow();
                    String name = wr.getProject().getName();
                    if ( name == null )
                    {
                        name = wr.getProject().getGroupId() + ":" + wr.getProject().getArtifactId();
                    }
                    tableCell( "<b><i>" + name + "</b></i>", true );
                    sink.tableRow_();
                }
                if ( wr.getFile().getName().endsWith( ".properties" )
                    && !localedPattern.matcher( wr.getFile().getName() ).matches() )
                {
                    usedFiles.add( wr );
                    sink.tableRow();
                    tableCell( wr.getPath() );
                    Properties props = new Properties();
                    BufferedInputStream in = null;
                    try
                    {
                        in = new BufferedInputStream( new FileInputStream( wr.getFile() ) );
                        props.load( in );
                        wr.getProperties().put( Wrapper.DEFAULT_LOCALE, props );
                        tableCell( "" + props.size(), true );
                        count[0] = count[0] + props.size();
                        if ( locales != null )
                        {
                            Iterator it2 = locales.iterator();
                            int i = 1;
                            while ( it2.hasNext() )
                            {
                                String loc = (String) it2.next();
                                String nm = wr.getFile().getName();
                                String fn = nm.substring( 0, nm.length() - ".properties".length() );
                                File locFile = new File( wr.getFile().getParentFile(), fn + "_" + loc + ".properties" );
                                if ( locFile.exists() )
                                {
                                    BufferedInputStream in2 = null;
                                    Properties props2 = new Properties();
                                    try
                                    {
                                        in2 = new BufferedInputStream( new FileInputStream( locFile ) );
                                        props2.load( in2 );
                                        wr.getProperties().put( loc, props2 );
                                        Set missing = new HashSet( props.keySet() );
                                        missing.removeAll( props2.keySet() );
                                        Set additional = new HashSet( props2.keySet() );
                                        additional.removeAll( props.keySet() );
                                        Set nonTranslated = new HashSet();
                                        Iterator itx = props.keySet().iterator();
                                        while ( itx.hasNext() )
                                        {
                                            String k = (String) itx.next();
                                            String val1 = props.getProperty( k );
                                            String val2 = props2.getProperty( k );
                                            if ( val2 != null && val1.equals( val2 ) )
                                            {
                                                nonTranslated.add( k );
                                            }
                                        }
                                        count[i] = count[i] + ( props.size() - missing.size() - nonTranslated.size() );
                                        StringBuffer statusRows = new StringBuffer();
                                        if ( missing.size() != 0 )
                                        {
                                            statusRows.append( "<tr><td>" + missingKeysLabel + "</td><td><b>"
                                                + missing.size() + "</b></td></tr>" );
                                        }
                                        else
                                        {
                                            statusRows.append( "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>" );
                                        }
                                        if ( additional.size() != 0 )
                                        {
                                            statusRows.append( "<tr><td>" + additionalKeysLabel + "</td><td><b>"
                                                + additional.size() + "</b></td></tr>" );
                                        }
                                        else
                                        {
                                            statusRows.append( "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>" );
                                        }
                                        if ( nonTranslated.size() != 0 )
                                        {
                                            statusRows.append( "<tr><td>" + nontranslatedKeysLabel + "</td><td><b>"
                                                + nonTranslated.size() + "</b></td></tr>" );
                                        }
                                        tableCell( wrapInTable( okLabel, statusRows.toString() ), true );
                                    }
                                    finally
                                    {
                                        IOUtil.close( in2 );
                                    }
                                }
                                else
                                {
                                    tableCell( missingFileLabel );
                                    count[i] = count[i] + 0;
                                }
                                i = i + 1;
                            }
                        }
                    }
                    catch ( IOException ex )
                    {
                        getLog().error( ex );
                    }
                    finally
                    {
                        IOUtil.close( in );
                    }
                    sink.tableRow_();
                }
            }
            sink.tableRow();
            tableCell( totalLabel );
            for ( int i = 0; i < count.length; i++ )
            {
                if ( i != 0 && count[0] != 0 )
                {
                    tableCell( "<b>" + count[i] + "</b><br />(" + ( count[i] * 100 / count[0] ) + "&nbsp;%)", true );
                }
                else if ( i == 0 )
                {
                    tableCell( "<b>" + count[i] + "</b>", true );
                }
            }
            sink.tableRow_();

            endTable();
            sink.paragraph();
            text( bundle.getString( "report.l10n.legend" ) );
            sink.paragraph_();
            sink.list();
            sink.listItem();
            text( bundle.getString( "report.l10n.list1" ) );
            sink.listItem_();
            sink.listItem();
            text( bundle.getString( "report.l10n.list2" ) );
            sink.listItem_();
            sink.listItem();
            text( bundle.getString( "report.l10n.list3" ) );
            sink.listItem_();
            sink.list_();
            sink.paragraph();
            text( bundle.getString( "report.l10n.note" ) );
            sink.paragraph_();
            endSection();

            if ( locales != null )
            {
                Iterator itx = locales.iterator();
                sink.list();
                while ( itx.hasNext() )
                {
                    String x = (String) itx.next();
                    sink.listItem();
                    link( "#" + x, x + " - " + localeDisplayNames.get( x ) );
                    sink.listItem_();
                }
                sink.list_();

                itx = locales.iterator();
                while ( itx.hasNext() )
                {
                    String x = (String) itx.next();
                    startSection( x + " - " + localeDisplayNames.get( x ) );
                    sink.anchor( x );
                    sink.anchor_();
                    startTable();
                    tableCaption( bundle.getString( "report.l10n.locale" ) + " " + localeDisplayNames.get( x ) );
                    tableHeader( new String[]{ bundle.getString( "report.l10n.tableheader1" ),
                                               bundle.getString( "report.l10n.tableheader2" ),
                                               bundle.getString( "report.l10n.tableheader3" ),
                                               bundle.getString( "report.l10n.tableheader4" ) } );
                    Iterator usedIter = usedFiles.iterator();
                    while ( usedIter.hasNext() )
                    {
                        sink.tableRow();
                        Wrapper wr = (Wrapper) usedIter.next();
                        tableCell( wr.getPath() );
                        Properties defs = (Properties) wr.getProperties().get( Wrapper.DEFAULT_LOCALE );
                        Properties locals = (Properties) wr.getProperties().get( x );
                        if ( locals == null )
                        {
                            locals = new Properties();
                        }
                        Set missing = new TreeSet( defs.keySet() );
                        missing.removeAll( locals.keySet() );
                        String cell = "";
                        Iterator ms = missing.iterator();
                        while ( ms.hasNext() )
                        {
                            cell = cell + "<tr><td>" + ms.next() + "</td></tr>";
                        }
                        tableCell( wrapInTable( okLabel, cell ), true );
                        Set additional = new TreeSet( locals.keySet() );
                        additional.removeAll( defs.keySet() );
                        Iterator ex = additional.iterator();
                        cell = "";
                        while ( ex.hasNext() )
                        {
                            cell = cell + "<tr><td>" + ex.next() + "</td></tr>";
                        }
                        tableCell( wrapInTable( okLabel, cell ), true );
                        Set nonTranslated = new TreeSet();
                        Iterator itnt = defs.keySet().iterator();
                        while ( itnt.hasNext() )
                        {
                            String k = (String) itnt.next();
                            String val1 = defs.getProperty( k );
                            String val2 = locals.getProperty( k );
                            if ( val2 != null && val1.equals( val2 ) )
                            {
                                nonTranslated.add( k );
                            }
                        }
                        Iterator nt = nonTranslated.iterator();
                        cell = "";
                        while ( nt.hasNext() )
                        {
                            String n = (String) nt.next();
                            cell = cell + "<tr><td>" + n + "</td><td>\"" + defs.getProperty( n ) + "\"</td></tr>";
                        }
                        tableCell( wrapInTable( okLabel, cell ), true );

                        sink.tableRow_();
                    }
                    endTable();
                    endSection();
                }
            }
            endSection();
        }

        /**
         * Take the supplied locale code, split into its different parts and create a Locale object from it.
         *
         * @param localeCode The code for a locale in the format language[_country[_variant]]
         * @return A suitable Locale object, ot <code>null</code> if the code was in an unknown format
         */
        private Locale createLocale( String localeCode )
        {
            // Split the localeCode into language/country/variant
            String[] localeComponents = StringUtils.split( localeCode, "_" );
            Locale locale = null;
            if ( localeComponents.length == 1 )
            {
                locale = new Locale( localeComponents[0] );
            }
            else if ( localeComponents.length == 2 )
            {
                locale = new Locale( localeComponents[0], localeComponents[1] );
            }
            else if ( localeComponents.length == 3 )
            {
                locale = new Locale( localeComponents[0], localeComponents[1], localeComponents[2] );
            }
            return locale;
        }

        private String wrapInTable( String okLabel, String cell )
        {
            if ( cell.length() == 0 )
            {
                cell = okLabel;
            }
            else
            {
                cell = "<table><tbody>" + cell + "</tbody></table>";
            }
            return cell;
        }
    }

    private static class Wrapper
    {

        private String path;

        private File file;

        private MavenProject proj;

        private Map properties;

        static final String DEFAULT_LOCALE = "Default";

        public Wrapper( String p, File f, MavenProject prj )
        {
            path = p;
            file = f;
            proj = prj;
            properties = new HashMap();
        }

        public File getFile()
        {
            return file;
        }


        public String getPath()
        {
            return path;
        }

        public MavenProject getProject()
        {
            return proj;
        }

        public Map getProperties()
        {
            return properties;
        }

    }

    private static class WrapperComparator
        implements Comparator
    {

        public int compare( Object o1, Object o2 )
        {
            Wrapper wr1 = (Wrapper) o1;
            Wrapper wr2 = (Wrapper) o2;
            int comp1 = wr1.getProject().getBasedir().compareTo( wr2.getProject().getBasedir() );
            if ( comp1 != 0 )
            {
                return comp1;
            }
            return wr1.getFile().compareTo( wr2.getFile() );
        }

    }
}
