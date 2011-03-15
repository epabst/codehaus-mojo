package org.codehaus.mojo.sablecc;

/*
 * Copyright 2005 The Codehaus.
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

import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.codehaus.plexus.util.FileUtils;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A plugin for processing grammar files in SableCC.
 *
 * @goal generate
 * @phase generate-sources
 * @description SableCC plugin
 * @author jesse <jesse.mcconnell@gmail.com>
 * @version $Id$
 */
public class SableCCMojo extends AbstractMojo
{
   /**
    * the source directory containing *.grammar files
    *
    * @parameter expression="${basedir}/src/main/sablecc"
    */
   private String sourceDirectory;

   /**
    * the directory to output the generated sources to
    *
    * @parameter expression="${project.build.directory}/generated-sources/sablecc"
    */
   private String outputDirectory;

   /**
    * the directory to store the processed grammars
    *
    * @parameter expression="${basedir}/target"
    */
   private String timestampDirectory;

   /**
    * The granularity in milliseconds of the last modification
    * date for testing whether a source needs recompilation
    *
    * @parameter expression="${lastModGranularityMs}" default-value="0"
    */
   private int staleMillis;

   /**
    * the maven project helper class for adding resources
    *
    * @parameter expression="${component.org.apache.maven.project.MavenProjectHelper}"
    */
   private MavenProjectHelper projectHelper;

   /**
    * @parameter expression="${project}"
    * @required
    */
   private MavenProject project;

   public void execute() throws MojoExecutionException
   {

      if (!FileUtils.fileExists(outputDirectory)) {
         FileUtils.mkdir(outputDirectory);
      }

      Set staleGrammars = computeStaleGrammars();

      if (staleGrammars.isEmpty()) {
         getLog().info( "Nothing to process - all grammars are up to date" );
         projectHelper.addResource( project, outputDirectory, Collections.singletonList("**/**.dat"), new ArrayList() );
         project.addCompileSourceRoot( outputDirectory );
         return;
      }

      for (Iterator i = staleGrammars.iterator(); i.hasNext();) {
         File grammar = (File)i.next();

         try
         {
            org.sablecc.sablecc.SableCC.processGrammar(grammar, new File(outputDirectory));
            // make sure this is after the actual processing,
            //otherwise it if fails the computeStaleGrammars will think it completed.
            FileUtils.copyFileToDirectory(grammar, new File(timestampDirectory));
         }
         catch ( Exception e )
         {
            throw new MojoExecutionException( "SableCC execution failed", e );
         }
         catch ( Throwable t )
         {
            throw new MojoExecutionException( "SableCC execution failed", t  );
         }
      }

      if ( project != null )
      {
         getLog().debug("adding .dat resource");
         projectHelper.addResource( project, outputDirectory, Collections.singletonList("**/**.dat"), new ArrayList() );
         project.addCompileSourceRoot( outputDirectory );
      }
   }


   private Set computeStaleGrammars() throws MojoExecutionException
   {
      SuffixMapping mapping = new SuffixMapping( ".grammar", ".grammar" );

      SourceInclusionScanner scanner = new StaleSourceScanner( staleMillis );

      scanner.addSourceMapping( mapping );

      File outDir = new File( timestampDirectory );

      Set staleSources = new HashSet();

      File sourceDir = new File( sourceDirectory );

      try
      {
         staleSources.addAll( scanner.getIncludedSources( sourceDir, outDir ) );
      }
      catch ( InclusionScanException e )
      {
         throw new MojoExecutionException( "Error scanning source root: \'" + sourceDir + "\' for stale grammars to reprocess.", e );
      }

      return staleSources;
   }

}
