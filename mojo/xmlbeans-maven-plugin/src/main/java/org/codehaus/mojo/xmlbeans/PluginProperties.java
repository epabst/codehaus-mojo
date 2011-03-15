package org.codehaus.mojo.xmlbeans;

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

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.xml.sax.EntityResolver;

/**
 * An object which isolates the properties the XML Bean plugin uses to operate.
 * If you pass this object a handle to the <code>MavenProject</code>, it will
 * return an instance of itself populated to reflect the project.
 *
 * @author <a href="mailto:kris.bravo@corridor-software.us">Kris Bravo</a>
 * @version $Id$
 */
interface PluginProperties
{
    /**
     * Returns configuration files identified in the xmlConfigs string passed
     * by the project configuration.
     *
     * @return An array of configuration files.
     */
    File[] getConfigFiles() throws XmlBeansException;

    /**
     * Returns a file array of xsd files to translate to object models.
     *
     * @return An array of schema files to be parsed by the schema compiler.
     */
    File[] getXsdFiles() throws XmlBeansException;

    /**
     * Returns the directory where the schemas are located. Note that this is
     * the base directory of the schema compiler, not the maven project.
     *
     * @return The schema directory.
     */
    File getBaseDir();

    /**
     * Returns the location of the output jar file should one be produced.
     *
     * @return The jar file location.
     */
    File getOutputJar();

    /**
     * The name of the package that the TypeSystemHolder class should be
     * generated in.  Normally this should be left unspecified. None of the
     * XMLBeans are generated in this package. Use .xsdconfig files to modify
     * XMLBean package or class names.
     *
     * @return The name of the package.
     */
    String getName();

    /**
     * Returns null. Currently the compiler preference isn't passwed to the xml beans
     * compiler.
     *
     * @return null.
     */
    String getCompiler();

    /**
     * Returns an array of the WSDL files used for the schema object generation.
     *
     * @return A file array of wsdl files.
     */
    File[] getWsdlFiles() throws XmlBeansException;

    /**
     * Returns the java source files to compile.
     *
     * @return An array of java files.
     */
    File[] getJavaFiles();

    /**
     * Returns true if the catalog file exists.
     *
     * @return The existence of the catalog file.
     * @number MXMLBEANS-3
     */
    boolean hasCatalogFile();

    /**
     * Returns the name of the file used to resolve xml entities.
     *
     * @return The entity resolver catalog file location.
     * @number MXMLBEANS-3
     */
    String getCatalogFile();

    /**
     * Returns a classpath for the compiler made up of artifacts from the project.
     *
     * @return Array of classpath entries.
     * @throws DependencyResolutionRequiredException
     *          Maven Dependencies weren't resolved.
     */
    File[] getClasspath()
            throws DependencyResolutionRequiredException;

    /**
     * Returns the directory for saving generated source files.
     *
     * @return The generated=sources directory.
     */
    File getGeneratedSourceDirectory();

    /**
     * Returns the class directory of the project.
     *
     * @return The project build classes directory.
     */
    File getGeneratedClassesDirectory();

    /**
     * Returns an empty collection the compiler will store error message Strings
     * in.
     *
     * @return An empty ArrayList.
     */
    Collection getErrorListeners();

    /**
     * Returns a null entity resolver.
     *
     * @return entityResolver set to null.
     */
    EntityResolver getEntityResolver();

    /**
     * Returns the initial size of the memory allocation for the schema compile process.
     *
     * @return The initial memory size value.
     */
    String getMemoryInitialSize();

    /**
     * Returns the maximum size of the memory allocation for the schema compile process.
     *
     * @return The max memory size value.
     */
    String getMemoryMaximumSize();

    /**
     * Todo: Not certain of the purpose of this.
     *
     * @return null at this time.
     */
    String getRepackage();

    /**
     * Todo: Not certain of the purpose of this.
     *
     * @return null at this time.
     */
    List getExtensions();

    /**
     * Returns null at this time. Passed to the schema compiler.
     *
     * @return null.
     */
    Set getMdefNamespaces();

    /**
     * Returns the javasource parameter which specifies an option to
     * the XmlBeans code generator.
     *
     * @return null.
     */
    String getJavaSource();

    /**
     * Returns True if generated source files are not to be compiled.
     *
     * @return true if no compiling should occur.
     */
    boolean isNoJavac();

    /**
     * Returns true if the schema compiler should reduce verbosity.
     *
     * @return true if message suppression is on.
     */
    boolean isQuiet();

    /**
     * Returns true if the schema compiler should increase verbosity.
     *
     * @return true if verbose mode is on.
     */
    boolean isVerbose();

    /**
     * Returns true if dependencies are to be downloaded by the schema compiler.
     *
     * @return true if resources should be downloaded.
     */
    boolean isDownload();

    /**
     * If true, do not enforce the unique particle attribution rule.
     *
     * @return particle attibution enforcement
     */
    boolean isNoUpa();

    /**
     * Do not enforce the particle valid (restriction) rule if true.
     *
     * @return true if no enforcement should occur.
     */
    boolean isNoPvr();

    /**
     * Returns the debug flag setting.
     *
     * @return True if debugging is turned on.
     */
    boolean isDebug();

    /**
     * Returns the jaxb setting.
     *
     * @return True if the jaxb flag is set.
     */
    boolean isJaxb();

    /**
     * If true, annotations in the schema are ignored
     *
     * @return true if annotations in the source schema are to be ignored
     */
    boolean isNoAnn();

    /**
     * If true, documentation elements in the source schema are ignored.
     *
     * @return
     */
    boolean isNoVDoc();

    /**
     * Validate all of the fields for proper usage.
     *
     * @throws XmlBeansException Validation failed.
     */
    void validate()
            throws XmlBeansException;
}
