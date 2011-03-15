/*
   The MIT License
   .
   Copyright (c) 2009, Marat Radchenko
   .
   Permission is hereby granted, free of charge, to any person obtaining a copy of
   this software and associated documentation files (the "Software"), to deal in
   the Software without restriction, including without limitation the rights to
   use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
   of the Software, and to permit persons to whom the Software is furnished to do
   so, subject to the following conditions:
   .
   The above copyright notice and this permission notice shall be included in all
   copies or substantial portions of the Software.
   .
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
   SOFTWARE.
*/
package org.codehaus.mojo.argouml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.argouml.kernel.Project;
import org.argouml.model.Model;
import org.argouml.moduleloader.InitModuleLoader;
import org.argouml.moduleloader.ModuleLoader2;
import org.argouml.notation.InitNotation;
import org.argouml.notation.providers.java.InitNotationJava;
import org.argouml.notation.providers.uml.InitNotationUml;
import org.argouml.persistence.OpenException;
import org.argouml.persistence.PersistenceManager;
import org.argouml.persistence.ProjectFilePersister;
import org.argouml.profile.init.InitProfileSubsystem;
import org.argouml.sequence2.SequenceDiagramModule;
import org.argouml.uml.diagram.ArgoDiagram;
import org.argouml.uml.diagram.activity.ui.InitActivityDiagram;
import org.argouml.uml.diagram.collaboration.ui.InitCollaborationDiagram;
import org.argouml.uml.diagram.deployment.ui.InitDeploymentDiagram;
import org.argouml.uml.diagram.state.ui.InitStateDiagram;
import org.argouml.uml.diagram.static_structure.ui.InitClassDiagram;
import org.argouml.uml.diagram.ui.InitDiagramAppearanceUI;
import org.argouml.uml.diagram.use_case.ui.InitUseCaseDiagram;
import org.argouml.uml.ui.SaveGraphicsManager;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.SaveGraphicsAction;

/**
 * Transforms ArgoUML files into images.
 * @goal transform
 */
public class TranformMojo extends AbstractMojo {
  /**
   * Paths to input files. If any of specified files is a directory then all ArgoUML files in it are used. If this
   * parameter is not specified then <code>${project.build.sourceDirectory}/argouml</code> is used. Supported ArgoUML
   * formats: .zargo, .xmi, .uml, .zip
   * @parameter
   */
  private File[] inputFiles;

  /**
   * Default input directory if no input files were specified.
   * @parameter expression="${project.basedir}/src/argouml"
   * @readonly
   */
  private File defaultInputDirectory;

  /**
   * If <code>inputFiles</code> contains folders and this parameter is set to <code>true</code> then those folders are
   * parsed recursively.
   * @parameter default-value="false"
   */
  private boolean recursive;

  /**
   * Output directory for created images.
   * @parameter default-value="${project.build.directory}/argouml"
   */
  private File outputDirectory;

  /**
   * Output image format. Supported values are: ps, eps, png, gif, svg.
   * @parameter default-value="png"
   */
  private String outputFormat;

  /**
   * Image scaling factor.
   * @parameter default-value="1"
   */
  private int scale;

  public void execute() throws MojoExecutionException, MojoFailureException {
    // Prepare input files.
    if (ArrayUtils.isEmpty(inputFiles)) {
      inputFiles = new File[] { defaultInputDirectory };
    }

    // Prepare output directory
    final File output = outputDirectory;
    if (!output.exists() && !output.mkdirs()) {
      throw new MojoExecutionException("could not create output directory " + output.getAbsolutePath());
    }

    try {
      // final Throwable error = Model.initialise("org.argouml.model.euml.EUMLModelImplementation");
      final Throwable error = Model.initialise("org.argouml.model.mdr.MDRModelImplementation");
      if (error != null) {
        throw error;
      }

      new InitProfileSubsystem().init();
      new InitNotation().init();
      new InitNotationUml().init();
      new InitNotationJava().init();
      new InitDiagramAppearanceUI().init();
      new InitActivityDiagram().init();
      new InitCollaborationDiagram().init();
      new InitDeploymentDiagram().init();
      new InitStateDiagram().init();
      new InitClassDiagram().init();
      new InitUseCaseDiagram().init();

      ModuleLoader2.addClass(SequenceDiagramModule.class.getName());
      new InitModuleLoader().init();

    } catch (Throwable e) {
      throw new MojoExecutionException("Could not init argouml", e);
    }

    // Process files
    for (final File file : inputFiles) {
      process(file);
    }
  }

  private void process(final File fileOrDirectory) throws MojoExecutionException {
    if (!fileOrDirectory.exists()) {
      throw new MojoExecutionException("Input "
          + fileOrDirectory.getAbsolutePath() + " does not exist");
    } else if (fileOrDirectory.isDirectory()) {
      for (final File file : fileOrDirectory.listFiles()) {
        if (recursive) {
          process(file);
        } else if (file.isFile()) {
          convert(file);
        }
      }
    } else if (fileOrDirectory.isFile()) {
      convert(fileOrDirectory);
    }
  }

  /**
   * Perform conversion of single file.
   * @param input input file.
   * @throws MojoExecutionException if there was a error during conversion.
   */
  private void convert(final File input) throws MojoExecutionException {
    final PersistenceManager persistenceManager = PersistenceManager.getInstance();
    final ProjectFilePersister persister = persistenceManager.getPersisterFromFileName(input.getAbsolutePath());
    final Project project;
    if (persister == null) {
      getLog().warn("Could not get argouml persister for " + input);
      return;
    }
    try {
      project = persister.doLoad(input);
    } catch (OpenException e) {
      throw new MojoExecutionException("Could not load file " + input.getAbsolutePath(), e);
    } catch (InterruptedException e) {
      throw new MojoExecutionException("Could not load file " + input.getAbsolutePath(), e);
    }
    for (final ArgoDiagram argoDiagram : project.getDiagramList()) {
      final Diagram dia = (Diagram) argoDiagram;
      Globals.curEditor(new Editor(dia));
      final SaveGraphicsAction cmd = SaveGraphicsManager.getInstance().getSaveActionBySuffix(outputFormat);
      cmd.setScale(scale);
      final String origName = dia.getName();
      final String name;
      if (origName.indexOf('/') > -1 || origName.indexOf('\\') > -1) {
        name = origName.replaceAll("[/\\\\]", "_");
        getLog().warn("Diagram name '" + origName + "' contains illegal symbols. Renamed to '" + name + "'");
      } else {
        name = origName;
      }
      final File file = new File(outputDirectory, name + "." + outputFormat);
      getLog().info("Writing " + file.getAbsolutePath());
      OutputStream os = null;
      try {
        os = new FileOutputStream(file);
        cmd.setStream(os);
        cmd.actionPerformed(null);
      } catch (FileNotFoundException e) {
        throw new MojoExecutionException("Could not write " + file, e);
      } finally {
        if (os != null) {
          try {
            os.close();
          } catch (IOException e) {
            // no-op
          }
        }
      }
    }
  }
}
