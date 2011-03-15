package org.codehaus.mojo.springide;

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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author <a href="mailto:nicolas@apache.org">nicolas de loof</a>
 * @goal generate
 */
public class SpringIDEMojo
    extends AbstractMojo
{
    /**
     * @parameter default-value="xml"
     * @alias configExtensions
     */
    private String configSuffixes;

    /**
     * @parameter default-value="**\/applicationContext*.xml, META-INF/spring/**\/*.xml"
     */
    private String includes;

    /**
     * @parameter
     */
    private String excludes;

    /**
     * @parameter default-value="true"
     */
    private boolean allowBeanDefinitionOverriding;

    /**
     * @parameter default-value="true"
     */
    private boolean enableImports;

    /**
     * @parameter default-value="true"
     */
    private boolean incomplete;

    /**
     * @parameter default-value="${project.artifactId}"
     */
    private String name;

    /**
     * The project whose project files to create.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * @parameter expression="${springide.skip}"
     */
    private boolean skip;

    /**
     * {@inheritDoc}
     *
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( skip )
        {
            getLog().info( "SpringIDE configuration is skipped." );
            return;
        }
        if ( "pom".equals( project.getPackaging() ) )
        {
            return;
        }

        List configs = new LinkedList();

        String src = project.getBuild().getSourceDirectory();
        scan( configs, new File( src ) );

        for ( Iterator iterator = project.getBuild().getResources().iterator(); iterator.hasNext(); )
        {
            Resource resource = (Resource) iterator.next();
            scan( configs, new File( resource.getDirectory() ) );
        }

        String[] generated = new File( project.getBuild().getOutputDirectory() ).list( new FilenameFilter()
        {
            public boolean accept( File dir, String name )
            {
                return name.startsWith( "generated-" );
            }
        } );

        if (generated != null)
        {
            for ( int i = 0; i < generated.length; i++ )
            {
                scan( configs, new File( generated[i] ) );
            }
        }

        if ( configs.size() > 0 )
        {
            forceSpringNature();
            Map context = new HashMap();
            context.put( "configs", configs );
            context.put( "configSuffixes", configSuffixes.split( "," ) );
            context.put( "allowBeanDefinitionOverriding", String.valueOf( allowBeanDefinitionOverriding ) );
            context.put( "incomplete", String.valueOf( incomplete ) );
            context.put( "enableImports", String.valueOf( enableImports ) );
            context.put( "name", name );
            getLog().info( "create SpringIDE configuration for " + name );

            File dotSpringBeans = new File( project.getBasedir(), ".springBeans" );
            applyTemplate( context, dotSpringBeans, "springBeans.fm" );

            File prefs = new File( project.getBasedir(), ".settings/org.springframework.ide.eclipse.core.prefs" );
            applyTemplate( context, prefs, "prefs.fm" );
        }
        else
        {
            getLog().info( "No spring context file found in project" );
        }
    }

    private void scan( List configs, File src )
    {
        if ( !src.exists() )
        {
            return;
        }
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir( src );
        scanner.setIncludes( includes.split( "," ) );
        if (excludes != null)
        {
            scanner.setExcludes( excludes.split( "," ) );
        }
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        for ( int i = 0; i < files.length; i++ )
        {
            configs.add( src.getAbsolutePath().substring( project.getBasedir().getAbsolutePath().length() ) + "/" + files[i] );
        }
        return;
    }

    protected void applyTemplate( Map context, File out, String template )
        throws MojoExecutionException
    {
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading( getClass(), "" );

        out.getParentFile().mkdirs();
        try
        {
            Writer configWriter = new FileWriter( out );
            Template tpl = cfg.getTemplate( template );
            tpl.process( context, configWriter );
            configWriter.flush();
            configWriter.close();
            getLog().debug( "Write SpringIDE configuration to: " + out.getAbsolutePath() );
        }
        catch ( IOException ioe )
        {
            throw new MojoExecutionException( "Unable to write SpringIDE configuration file", ioe );
        }
        catch ( TemplateException te )
        {
            throw new MojoExecutionException( "Unable to merge freemarker template", te );
        }
    }

    protected void forceSpringNature()
    {
        try
        {
            File dotProject = new File( project.getBasedir(), ".project" );
            String content = FileUtils.readFileToString( dotProject, null );
            if ( content.indexOf( "<nature>org.springframework.ide.eclipse.core.springnature</nature>" ) < 0 )
            {
                getLog().info( "Add spring nature to the eclipse .project file" );
                try
                {
                    Xpp3Dom dom = Xpp3DomBuilder.build( new FileReader( dotProject ) );
                    Xpp3Dom nature = new Xpp3Dom( "nature" );
                    nature.setValue( "org.springframework.ide.eclipse.core.springnature" );
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
        }
        catch ( IOException e )
        {
            getLog().info( "Failed to retrieve the Eclipse .project file" );
        }
    }

}
