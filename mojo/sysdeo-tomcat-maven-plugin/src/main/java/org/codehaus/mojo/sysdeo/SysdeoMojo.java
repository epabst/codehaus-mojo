package org.codehaus.mojo.sysdeo;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.sysdeo.ide.AbstractIdeSupportMojo;
import org.codehaus.mojo.sysdeo.ide.IdeDependency;
import org.codehaus.mojo.sysdeo.ide.IdeUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Generates the required configuration files for the eclipse Sysdeo-tomcat plugin
 *
 * @goal generate
 */
public class SysdeoMojo
    extends AbstractIdeSupportMojo
{

    private static final String TOMCAT_PLUGIN = ".tomcatplugin";

    /**
     * Single directory for extra files to include in the WAR.
     *
     * @parameter expression="${basedir}/src/main/webapp"
     * @required
     */
    private File warSourceDirectory;

    /**
     * Application context definition for Tomcat.
     *
     * @parameter expression="${basedir}/src/main/webapp/META-INF/context.xml"
     * @required
     */
    private File contextDefinition;

    /**
     * Web application context path
     *
     * @parameter default-value="${project.build.finalName}"
     */
    private String webPath;

    /**
     * Use the M2_REPO Classpath Variable to point to dependencies in the local repository. MUST be set to false to use
     * this plugin with m2eclipse project import configurator.
     *
     * @parameter default-value="true"
     */
    private boolean useClasspathVariable;

    private List ignoreArtifact;

    /**
     * define artifacts to exclude from the devloader classpath as they create conflicts (LinkageErrors) with Tomcat
     * classes and APIs
     *
     * @parameter
     */
    private String[] ignoredArtifacts;

    public SysdeoMojo()
    {
        ignoreArtifact = new ArrayList();
        ignoreArtifact.add( "jsp-api" );
        ignoreArtifact.add( "el-api" );
        ignoreArtifact.add( "servlet-api" );
        ignoreArtifact.add( "gwt-user" );
    }

    protected boolean getUseProjectReferences()
    {
        return true;
    }

    protected boolean setup()
    {
        if ( ignoredArtifacts != null )
        {
            for ( int i = 0; i < ignoredArtifacts.length; i++ )
            {
                ignoreArtifact.add( ignoredArtifacts[i] );
            }
        }
        if ( !getProject().getPackaging().equals( "war" ) )
        {
            getLog().info( "Not executing sysdeo-tomcat plugin, this is project is not a war package" );
            return false;
        }

        return true;
    }

    protected void writeConfiguration( IdeDependency[] dependencies )
        throws MojoExecutionException
    {

        File webclasspath = new File( warSourceDirectory, ".#webclasspath" );
        if ( webclasspath.exists() )
        {
            webclasspath.delete();
        }

        File projectDir = getProject().getBasedir();

        forceTomcatNature( projectDir );

        List referencedProjects = new ArrayList();
        List jarDependencies = new ArrayList();
        List systemDependencyPaths = new ArrayList();

        IdeDependency thisDependency = new IdeDependency();
        thisDependency.setGroupId( getProject().getGroupId() );
        thisDependency.setArtifactId( getProject().getArtifactId() );
        thisDependency.setVersion( getProject().getVersion() );
        thisDependency = resolveWorkspaceProject( thisDependency );
        thisDependency.setOutputDirectory( getOutputDirectory() );

        referencedProjects.add( thisDependency );

        for ( int i = 0; i < dependencies.length; i++ )
        {
            IdeDependency dependency = dependencies[i];
            if ( dependency.isProvided() || dependency.isTestDependency() )
            {
                // Skip this dependency
                continue;
            }
            if ( dependency.isReferencedProject() || getWorkspaceProjects().contains( dependency ) )
            {
                referencedProjects.add( dependency );
            }
            else if ( dependency.isSystemScoped() )
            {
                String absolutePath = dependency.getFile().getAbsolutePath();
                String osName = System.getProperty( "os.name" );
                if ( osName.toUpperCase().indexOf( "WINDOWS" ) >= 0 )
                {
                    absolutePath = absolutePath.substring( 0, 1 ).toUpperCase() + absolutePath.substring( 1 );
                    absolutePath = absolutePath.replace( '\\', '/' );
                }
                systemDependencyPaths.add( absolutePath );
            }
            else if ( dependency.getType().equalsIgnoreCase( "jar" )
                && !ignoreArtifact.contains( dependency.getArtifactId() ) )
            {
                jarDependencies.add( dependency );
            }
        }

        String extraContext = "";
        try
        {
            String contextString = FileUtils.readFileToString( contextDefinition, "UTF-8" );
            int context = contextString.indexOf( "Context" );
            int start = contextString.indexOf( ">", context );
            int stop = contextString.indexOf( "</Context>" );
            extraContext = contextString.substring( start + 1, stop );
            extraContext = URLEncoder.encode( extraContext );
        }
        catch ( Exception e )
        {
            getLog().info( "No valid context file found at: " + contextDefinition );
        }

        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading( SysdeoMojo.class, "" );

        Map context = new HashMap();
        context.put( "referencedProjects", referencedProjects );
        context.put( "jarDependencies", jarDependencies );
        context.put( "systemDependencyPaths", systemDependencyPaths );
        context.put( "webPath", webPath );
        context.put( "localRepository", useClasspathVariable ? "M2_REPO" : getLocalRepository().getBasedir() );
        context.put( "warSourceDir", IdeUtils.toRelativeAndFixSeparator( getProject().getBasedir(),
            getWarSourceDirectory(), false ) );
        context.put( "extraContext", extraContext );

        File tomcatPluginFile = new File( projectDir, TOMCAT_PLUGIN );
        try
        {
            Writer configWriter = new FileWriter( tomcatPluginFile );
            Template template = cfg.getTemplate( "tomcatplugin.fm" );
            template.process( context, configWriter );
            configWriter.flush();
            configWriter.close();
            getLog().info( "Write tomcat plugin file to: " + tomcatPluginFile.getAbsolutePath() );
        }
        catch ( IOException ioe )
        {
            throw new MojoExecutionException( "Unable to write tomcat plugin config file", ioe );
        }
        catch ( TemplateException te )
        {
            throw new MojoExecutionException( "Unable to merge freemarker template", te );
        }
    }

    protected void forceTomcatNature( File projectDir )
    {
        try
        {
            File dotProject = new File( projectDir, ".project" );
            String content = FileUtils.readFileToString( dotProject, null );
            if ( content.indexOf( "<nature>com.sysdeo.eclipse.tomcat.tomcatnature</nature>" ) < 0 )
            {
                getLog().info( "Add tomcat nature to the eclipse .project file" );
                try
                {
                    Xpp3Dom dom = Xpp3DomBuilder.build( new FileReader( dotProject ) );
                    Xpp3Dom nature = new Xpp3Dom( "nature" );
                    nature.setValue( "com.sysdeo.eclipse.tomcat.tomcatnature" );
                    dom.getChild( "natures" ).addChild( nature );
                    FileWriter writer = new FileWriter( dotProject );
                    Xpp3DomWriter.write( writer, dom );
                    writer.close();
                }
                catch ( Exception e )
                {
                    getLog().warn( "Failed to add missing tomcat nature to the eclipse .project file", e );
                }
            }

            // Required to force devloader classpath refresh on POM changes
            File dotClassPath = new File( projectDir, ".classpath" );
            FileUtils.touch( dotClassPath );
        }
        catch ( IOException e )
        {
            getLog().info( "Failed to retrieve the Eclipse .project file" );
        }
    }

    /**
     * @return
     */
    protected String getOutputDirectory()
    {
        String relative = getProject().getFile().getParent();
        String buildOutputDirectory = getProject().getBuild().getOutputDirectory();
        return buildOutputDirectory.substring( relative.length() + 1 ).replace( '\\', '/' );
    }

    public File getContextDefinition()
    {
        return contextDefinition;
    }

    public void setContextDefinition( File contextDefinition )
    {
        this.contextDefinition = contextDefinition;
    }

    public File getWarSourceDirectory()
    {
        return warSourceDirectory;
    }

    public void setWarSourceDirectory( File warSourceDirectory )
    {
        this.warSourceDirectory = warSourceDirectory;
    }

    /**
     * @param useClasspathVariable the useClasspathVariable to set
     */
    public void setUseClasspathVariable( boolean useClasspathVariable )
    {
        this.useClasspathVariable = useClasspathVariable;
    }
}
