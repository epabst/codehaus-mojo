package org.apache.maven.plugin.jdiff;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.maven.scm.manager.ScmManager;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.PathTool;



/**
 * @goal jdiff
 *
 * @requiresDependencyResolution
 *
 * @description A Maven 2.0 JDiff plugin to generate an api difference report between SCM versions
 *
 * @phase validate
 *
 */
public class JDiffMojo extends AbstractMavenReport
{
    
    /**
     * @parameter expression="${project.groupId}"
     * @required
     */
    private String packages;
    
    /**
     * @parameter default-value="CURRENT"
     * @required
     */
    private String oldTag;
    
    /**
     * @parameter default-value="CURRENT"
     * @required
     */
    private String newTag;
    
    /**
     * @parameter expression="${jdiff.svnUsername}"
     */
    private String svnUsername;
    
    /**
     * @parameter expression="${jdiff.svnPassword}"
     */
    private String svnPassword;
    
    /**
     * @parameter
     */
    private String svnTagBase;
    
    /**
     * @parameter expression="${project.build.directory}/site/jdiff"
     * @required
     * @readonly
     */
    private String outputDirectory;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @readonly
     */
    private List pluginArtifacts;

    /**
     * @parameter expression="${component.org.codehaus.doxia.site.renderer.SiteRenderer}"
     * @required
     * @readonly
     */
    private SiteRenderer siteRenderer;

    /**
     * @parameter expression="${component.org.apache.maven.scm.manager.ScmManager}"
     * @required
     * @readonly
     */
    private ScmManager scmManager;

    private ScmBean scm;

    
    public void executeReport( Locale locale ) throws MavenReportException
    {
        Init();
        
        String oldSource = getSrcDir( oldTag );
        
        String newSource = getSrcDir( newTag );
        
        generateJDiffXML( oldSource, oldTag );
        
        generateJDiffXML( newSource, newTag );
        
        generateReport( newSource, oldTag, newTag );
        
        generateSite();
    }
    
    private void Init() throws MavenReportException
    {
        scm = new ScmBean( scmManager, getConnection() );
        
        scm.setSvnParams( svnUsername, svnPassword, svnTagBase );
    }
    
    private String getSrcDir( String tag ) throws MavenReportException
    {
        String srcDir;
        
        if ( tag.equals( "CURRENT" ) )
        {
            srcDir = project.getBuild().getSourceDirectory();
        }
        else
        {
            doCheckout( tag, outputDirectory + "/" + tag );

            srcDir = outputDirectory + "/" + tag + "/src/main/java";
        }
        
        return srcDir;
    }
    
    private String getProjectSourceDirectory()
    {
        return PathTool.getRelativePath( project.getBasedir().getAbsolutePath(), project.getBuild().getSourceDirectory() );
    }

    private String getConnection() throws MavenReportException
    {
        if ( project.getScm() == null ) throw new MavenReportException( "SCM Connection is not set in your pom.xml." );
        
        String connection = project.getScm().getConnection();
        
        if ( connection != null )
            if ( connection.length() > 0 ) return connection;
        
        connection = project.getScm().getDeveloperConnection();
        
        if ( connection == null ) throw new MavenReportException( "SCM Connection is not set in your pom.xml." );
        
        if ( connection.length() == 0 ) throw new MavenReportException( "SCM Connection is not set in your pom.xml." );
        
        return connection;
    }
    
    private void doCheckout( String tag, String checkoutDir ) throws MavenReportException
    {
        try
        {
            File dir = new File( checkoutDir );
            
            //@todo remove when scm update is to be used
            if ( dir.exists() ) FileUtils.deleteDirectory( dir );
            
            if ( !dir.exists() )
            {
                dir.mkdirs();
                
                log( "Performing checkout to " + checkoutDir );
                
                scm.checkout( tag, checkoutDir );
            }
            else
            {
                log( "Performing update to " + checkoutDir );
                
                scm.update( tag, checkoutDir );
            }
        }
        catch( Exception ex )
        {
            throw new MavenReportException( "checkout failed.", ex );
        }
    }
    
    private void generateJDiffXML( String srcDir, String tag ) throws MavenReportException
    {
        JavadocBean javadoc = new JavadocBean();
        
        javadoc.addArgumentPair( "doclet", "jdiff.JDiff" );
        
        javadoc.addArgumentPair( "docletpath", getPluginClasspath() );
        
        javadoc.addArgumentPair( "apiname", tag );
        
        javadoc.addArgumentPair( "apidir", outputDirectory );
        
        javadoc.addArgumentPair( "classpath", getProjectClasspath() );
        
        javadoc.addArgumentPair( "sourcepath", srcDir );
        
        javadoc.addArgument( packages );
        
        javadoc.execute( outputDirectory );
    }
    
    private String getProjectClasspath()
    {
        String cp = "";
        
        for( Iterator i=project.getCompileArtifacts().iterator(); i.hasNext(); )
        {
            Artifact artifact = (Artifact) i.next();
            
            String path = artifact.getFile().getAbsolutePath();
            
            cp += ";" + path;
        }
        
        return cp.substring( 1 );
    }
    
    private String getPluginClasspath()
    {
        String cp = "";
        
        for ( Iterator i=pluginArtifacts.iterator(); i.hasNext(); )
        {
            Artifact artifact = (Artifact) i.next();
            
            cp += ";" + artifact.getFile().getAbsolutePath();
        }
        
        return cp.substring( 1 );
    }
    
    private String getJarLocation( String id ) throws MavenReportException
    {
        for ( Iterator i=pluginArtifacts.iterator(); i.hasNext(); )
        {
            Artifact artifact = (Artifact) i.next();
            
            if ( artifact.getArtifactId().equals( id ) ) return artifact.getFile().getAbsolutePath();
        }
        
        throw new MavenReportException( "JDiff jar not found in plugin artifacts." );
    }
    
    private void generateReport( String srcDir, String oldApi, String newApi ) throws MavenReportException
    {
        JavadocBean javadoc = new JavadocBean();
        
        javadoc.addArgument( "-private" );
        
        javadoc.addArgumentPair( "d", outputDirectory );
        
        javadoc.addArgumentPair( "sourcepath", srcDir );
        
        javadoc.addArgumentPair( "classpath", getProjectClasspath() );
        
        javadoc.addArgumentPair( "doclet", "jdiff.JDiff" );
        
        javadoc.addArgumentPair( "docletpath", getPluginClasspath() );
        
        javadoc.addArgumentPair( "oldapi", oldApi );
        
        javadoc.addArgumentPair( "newapi", newApi );
        
        javadoc.addArgument( "-stats" );
        
        javadoc.addArgument( packages );
        
        javadoc.execute( outputDirectory );
    }
    
    private void generateSite()
    {
        Sink sink = getSink();
        
        sink.head();
        sink.title();
        sink.text( "JDiff API Difference Report" );
        sink.title_();
        sink.head_();

        sink.body();
        sink.section1();
        
        sink.sectionTitle1();
        sink.text( "JDiff API Difference Report" );
        sink.sectionTitle1_();

        sink.paragraph();
        sink.text( "The pages generated by JDiff is on a separate page.  It can be found " );
        sink.link( "jdiff/changes.html" );
        sink.text( "here" );
        sink.link_();
        sink.text( "." );
        sink.paragraph_();
        
        sink.section1_();
        sink.body_();
    }
    
    private void log( String message )
    {
        getLog().info( message );
    }
    
    protected MavenProject getProject()
    {
        return project;
    }

    protected SiteRenderer getSiteRenderer()
    {
        return siteRenderer;
    }

    protected String getOutputDirectory()
    {
        return outputDirectory;
    }

    public String getDescription(Locale locale)
    {
        return "Maven 2.0 JDiff Plugin";
    }

    public String getName(Locale locale)
    {
        return "JDiff";
    }

    public String getOutputName()
    {
        return "jdiff";
    }
}