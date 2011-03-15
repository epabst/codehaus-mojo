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
package org.codehaus.mojo.freeform.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.mojo.freeform.FreeformConstants;
import org.codehaus.mojo.freeform.FreeformPluginException;
import org.codehaus.mojo.freeform.project.CompilationUnit;
import org.codehaus.mojo.freeform.project.FreeformProject;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * This class creates the Ant build file for the custom IDE actions. 
 * @see http://www.netbeans.org/kb/articles/freeform-config-40.html for more
 *      details
 * @author <a href="mailto:gergely.dombi.sp@lhsystems.com">Gergely Dombi</a>
 */
public class IdeFileTargetsWriter {
	
	/**
	 * The maven project name.
	 */
	private String mavenProjectName;
	
	/**
	 * The custom xml file (ide-file-targets.xml) that stores the custom ant
	 * targets that should be linked to IDE actions.
	 */
	private File customFile;

	/**
	 * The netbeans project directory that contains the netbeans spesific files.
	 * (By default: project.xml, project.properties, mavencall.xml, ide-file-targets.xml)
	 */
	private File netbeansProjectDirectory;
	
	/**
	 * The underlying freeform project.
	 */
	private FreeformProject freeformProject;

	
	
	
	/**
	 * The maven plugin logger.
	 */
	private Log log;

	public IdeFileTargetsWriter(final FreeformProject freeformProject,
			final File customFile, final File netbeansProjectDirectory, final Log log, final String mavenProjectName) {
		this.customFile = customFile;
		this.freeformProject = freeformProject;
		this.log = log;
		this.mavenProjectName = mavenProjectName;
		this.netbeansProjectDirectory = netbeansProjectDirectory;
	}

	/**
	 * Writes the ide-file-targets.xml file.
	 */
	public void write() throws FreeformPluginException {
		FileWriter fileWriter = null;

		try {

			try {
				fileWriter = new FileWriter(customFile);
			} catch (IOException ioe) {
				throw new FreeformPluginException(
						"Exception while opening file.", ioe);
			}

			XMLWriter writer = new PrettyPrintXMLWriter(fileWriter);
			
			writer.startElement("project");
			writer.addAttribute("basedir", "..");
			writer.addAttribute("name", mavenProjectName + "-IDE");

			writer.startElement("property");
			writer.addAttribute("name", "ant.script");
			writer.addAttribute("value", this.netbeansProjectDirectory + "/mavencall.xml");
			writer.endElement(); //property			
			
			writer.startElement("property");
			writer.addAttribute("file", this.netbeansProjectDirectory + "/project.properties");
			writer.endElement(); //property
			
			writeCompileSingleInMain(writer);
			writeCompileSingleInTest(writer);
			
			writeRunSingleTargetInMain(writer);
			writeRunSingleTargetInTest(writer);
			
			writeDebugSingleTargetInMain(writer);
			writeDebugSingleTargetInTest(writer);
			
			writer.endElement(); //project
			
			
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (Exception e) {
					// ignore
					log.warn("The closure of " + customFile
							+ " can not be done", e);
				}

			}

		}

	}
	
	/**
	 * Writes the debug.single target for the main source.
	 * @param writer
	 */
	private void writeDebugSingleTargetInMain(XMLWriter writer) {
		final TargetConfig config = new TargetConfig();
		config.setFailUnlessSet("classname");
		config.setOutputDir("target/classes");
		config.setSourceDir("src/main/java");
		config.setTargetName(FreeformConstants.DEBUG_SELECTED_FILES_IN_MAIN);
		
		writeDebugSingleTarget(config, writer);		
	}

	/**
	 * Writes the debug.single target for the test source.
	 * @param writer
	 */	
	private void writeDebugSingleTargetInTest(XMLWriter writer) {
		final TargetConfig config = new TargetConfig();
		config.setFailUnlessSet("classname");
		config.setOutputDir("target/test-classes");
		config.setSourceDir("src/test/java");
		config.setTargetName(FreeformConstants.DEBUG_SELECTED_FILES_IN_TEST);
		
		writeDebugSingleTarget(config, writer);		
	}
	
	
	/**
	 * Writes the run.single target for the main source.
	 * @param writer
	 */
	private void writeRunSingleTargetInMain(XMLWriter writer) {
		final TargetConfig config = new TargetConfig();
		config.setFailUnlessSet("classname");
		config.setOutputDir("target/classes");
		config.setSourceDir("src/main/java");
		config.setTargetName(FreeformConstants.RUN_SELECTED_FILES_IN_MAIN);
		
		writeRunSingleTarget(config, writer);
	}

	/**
	 * Writes the run.single target for the test source.
	 * @param writer
	 */	
	private void writeRunSingleTargetInTest(XMLWriter writer) {
		final TargetConfig config = new TargetConfig();
		config.setFailUnlessSet("classname");
		config.setOutputDir("target/test-classes");
		config.setSourceDir("src/test/java");
		config.setTargetName(FreeformConstants.RUN_SELECTED_FILES_IN_TEST);
		
		writeRunSingleTarget(config, writer);
	}
	
	/**
	 * Writes the compile.single target for the main source.
	 * @param writer
	 */	
	private void writeCompileSingleInMain(XMLWriter writer) {
		final TargetConfig config = new TargetConfig();
		config.setFailUnlessSet("files");
		config.setOutputDir("target/classes");
		config.setSourceDir("src/main/java");
		config.setTargetName(FreeformConstants.COMPILE_SELECTED_FILES_IN_MAIN);
		
		writeCompileSingleTarget(config, writer);
	}

	/**
	 * Writes the compile.single target for the test source.
	 * @param writer
	 */		
	private void writeCompileSingleInTest(XMLWriter writer) {
		final TargetConfig config = new TargetConfig();
		config.setFailUnlessSet("files");
		config.setOutputDir("target/test-classes");
		config.setSourceDir("src/test/java");
		config.setTargetName(FreeformConstants.COMPILE_SELECTED_FILES_IN_TEST);
		
		writeCompileSingleTarget(config, writer);
	}
	
	
	/**
	 * Writes a compile target based on the given <code>TargetConfig</code>
	 * @param config
	 * @param writer
	 */
	private void writeCompileSingleTarget(TargetConfig config, XMLWriter writer) {
		final String targetName = config.getTargetName();
		final String sourceDir = config.getSourceDir();
		final String outputDir = config.getOutputDir();
		final String property = config.getFailUnlessSet();
		final String sourceVersion = config.getSourceVersion();
		
		writer.startElement("target");
		writer.addAttribute("name", targetName);
		
		writer.startElement("fail");
		writer.addAttribute("unless", property);
		writer.writeMarkup("Must set property '" + property + "'");
		writer.endElement(); // fail
		
		writer.startElement("mkdir");
		writer.addAttribute("dir", outputDir);
		writer.endElement(); // mkdir
		
		writer.startElement("javac");
		writer.addAttribute("debug", "true");
		writer.addAttribute("destdir", outputDir);
		writer.addAttribute("includes", "${" + property + "}");
		writer.addAttribute("source", sourceVersion);
		writer.addAttribute("srcdir", sourceDir);
		
		
		writer.startElement("classpath");
		writer.addAttribute("path", getClasspath(sourceDir));
		writer.endElement(); //classapth
		
		
		
		
		writer.endElement(); //javac
		
		writer.endElement(); // target
		
	}

	/**
	 * Writes a run target based on the given <code>TargetConfig</code>
	 * @param config
	 * @param writer
	 */	
	private void writeRunSingleTarget(final TargetConfig config, final XMLWriter writer) {
		final String targetName = config.getTargetName();
		final String sourceDir = config.getSourceDir();
		final String outputDir = config.getOutputDir();
		final String property = config.getFailUnlessSet();
				
		writer.startElement("target");
		writer.addAttribute("name", targetName);
		
		writer.startElement("fail");
		writer.addAttribute("unless", property);
		writer.writeMarkup("Must set property '" + property + "'");
		writer.endElement(); // fail

		writer.startElement("java");
		writer.addAttribute("classname", "${" + property + "}");
		
		writer.startElement("classpath");
		StringBuffer buf = new StringBuffer();
		buf.append(outputDir);
		buf.append(":");
		buf.append(getClasspath(sourceDir));
		writer.addAttribute("path", buf.toString());
		writer.endElement(); //classapth
				
		writer.endElement(); //java
		
		writer.endElement(); // target
		
	}
	
	/**
	 * Writes a debug target based on the given <code>TargetConfig</code>
	 * @param config
	 * @param writer
	 */		
	private void writeDebugSingleTarget(final TargetConfig config, final XMLWriter writer) {
		final String targetName = config.getTargetName();
		final String sourceDir = config.getSourceDir();
		final String outputDir = config.getOutputDir();
		final String property = config.getFailUnlessSet();
				
		writer.startElement("target");
		writer.addAttribute("name", targetName);
		
		writer.startElement("fail");
		writer.addAttribute("unless", property);
		writer.writeMarkup("Must set property '" + property + "'");
		writer.endElement(); // fail

		StringBuffer buf = new StringBuffer();
		buf.append(outputDir);
		buf.append(":");
		buf.append(getClasspath(sourceDir));		
		String classpath = buf.toString();
		
		writer.startElement("nbjpdastart");
		writer.addAttribute("name", "${" + property + "}");
		writer.addAttribute("addressproperty", "jpda.address");
		writer.addAttribute("transport", "dt_socket");
		
		writer.startElement("classpath");
		writer.addAttribute("path", classpath);
		writer.endElement(); //classpath
		
		writer.startElement("sourcepath");
		writer.addAttribute("path", sourceDir);
		writer.endElement(); //sourcepath
		
		writer.endElement(); //nbjpdastart
			
		
		writer.startElement("java");
		writer.addAttribute("classname", "${" + property + "}");
		writer.addAttribute("fork", "true");
		
		writer.startElement("jvmarg");
		writer.addAttribute("value", "-Xdebug");
		writer.endElement(); //jvmarg

		writer.startElement("jvmarg");
		writer.addAttribute("value", "-Xnoagent");
		writer.endElement(); //jvmarg

		writer.startElement("jvmarg");
		writer.addAttribute("value", "-Djava.compiler=none");
		writer.endElement(); //jvmarg

		writer.startElement("jvmarg");
		writer.addAttribute("value", "-Xrunjdwp:transport=dt_socket,address=${jpda.address}");
		writer.endElement(); //jvmarg
		
		
		writer.startElement("classpath");
		writer.addAttribute("path", classpath);
		writer.endElement(); //classapth
				
		writer.endElement(); //java
		
		writer.endElement(); // target
		
	}
	
	
	
	/**
	 * Returns the classpath for the given compilation unit based on its source root directory.
	 * @param sourceDir
	 * @return
	 */
	private String getClasspath(final String sourceDir) {
		List units = freeformProject.getCompilationUnits();
		if(units == null || sourceDir == null){
			return null;
		}
		StringBuffer result = new StringBuffer();
		for(Iterator it = units.iterator(); it.hasNext();) {
			CompilationUnit compilationUnit = (CompilationUnit)it.next();
			
			for(Iterator ii = compilationUnit.getPackageRoot().iterator(); ii.hasNext();){
				if(sourceDir.equals((String)ii.next())){
					
					for(Iterator classpathEntries = compilationUnit.getClasspath().iterator(); classpathEntries.hasNext();){
					
						if(result.length() > 0){
							result.append(":");
						}
						result.append(classpathEntries.next());					
					}
				}
			}
			
		}
		return result.toString();
	}

	public File getCustomFile() {
		return customFile;
	}

	public void setCustomFile(File customFile) {
		this.customFile = customFile;
	}

	public FreeformProject getFreeformProject() {
		return freeformProject;
	}

	public void setFreeformProject(FreeformProject freeformProject) {
		this.freeformProject = freeformProject;
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public String getMavenProjectName() {
		return mavenProjectName;
	}

	public void setMavenProjectName(String mavenProjectName) {
		this.mavenProjectName = mavenProjectName;
	}

	public File getNetbeansProjectDirectory() {
		return netbeansProjectDirectory;
	}

	public void setNetbeansProjectDirectory(File netbeansProjectDirectory) {
		this.netbeansProjectDirectory = netbeansProjectDirectory;
	}
	
	
}
