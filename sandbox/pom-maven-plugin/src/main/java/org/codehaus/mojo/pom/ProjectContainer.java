/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.codehaus.mojo.pom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author joerg
 */
public class ProjectContainer {

  /** The property-prefix {@value} . */
  public static final String PROPERTY_PREFIX_POM = "pom.";

  /** The property-prefix {@value} . */
  public static final String PROPERTY_PREFIX_PROJECT = "project.";

  /** The prefix of a variable expression */
  public static final String PROPERTY_PREFIX = "${";

  /** The suffix of a variable expression */
  public static final String PROPERTY_SUFFIX = "}";

  public static final String XML_TAG_GROUPID = "groupId";

  public static final String XML_TAG_ARTIFACTID = "artifactId";

  public static final String XML_TAG_VERSION = "version";

  public static final String XML_TAG_PACKAGING = "packaging";

  public static final String XML_TAG_TYPE = "type";

  public static final String XML_TAG_PARENT = "parent";

  public static final String XML_TAG_DEPENDENCIES = "dependencies";

  public static final String XML_TAG_DEPENDENCY_MANAGEMENT = "dependencyManagement";

  public static final String XML_TAG_DEPENDENCY = "dependency";

  public static final String XML_TAG_PROPERTIES = "properties";

  public static final String XML_TAG_SCOPE = "scope";

  public static final String XML_TAG_CLASSIFIER = "classifier";

  private static final DocumentBuilder documentBuilder;

  private static final Transformer transformer;

  private static final String DEFAULT_SCOPE = "compile";

  private static final String DEFAULT_TYPE = "jar";

  static {
    try {
      documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException("JAXP missconfigured!", e);
    }
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    } catch (TransformerConfigurationException e) {
      throw new IllegalStateException("JAXP missconfigured!", e);
    } catch (TransformerFactoryConfigurationError e) {
      throw new IllegalStateException("JAXP missconfigured!", e);
    }
  }

  private final Log logger;

  private final ProjectContainer parent;

  private final MavenProject project;

  private final ProjectId id;

  private Document document;

  private List<Element> dependenciesList;

  private List<Element> dependencyManagementList;

  private final Map<String, String> propertyUpdateMap;

  private boolean modified;

  /**
   * The constructor.
   */
  public ProjectContainer(MavenProject project, ProjectContainer parent, Log logger) {

    super();
    this.logger = logger;
    this.id = new ProjectId(project);
    this.parent = parent;
    this.project = project;
    this.modified = false;
    this.propertyUpdateMap = new HashMap<String, String>();
  }

  public void updatePropertyValue(String propertyName, String newValue)
      throws MojoFailureException, MojoExecutionException {

    String replacedValue = this.propertyUpdateMap.get(propertyName);
    if ((replacedValue != null) && !(replacedValue.equals(newValue))) {
      throw new MojoFailureException("Value of property '" + propertyName + "' already set to '"
          + replacedValue + "'. Failed to set to '" + newValue + "'!");
    }
    String oldValue = this.project.getProperties().getProperty(propertyName);
    if (oldValue == null) {
      throw new MojoFailureException("Property '" + propertyName + "' is NOT declared in '"
          + this.id + " - can NOT set to '" + newValue + "'!");
    }
    Element elementProject = getPomDocument().getDocumentElement();
    Element elementProperty = DomUtilities.getChildElement(elementProject, XML_TAG_PROPERTIES,
        propertyName);
    if (elementProperty == null) {
      if (this.parent == null) {
        throw new MojoExecutionException("Internal error - missing parent of '" + this.id + "'!");
      } else {
        this.parent.updatePropertyValue(propertyName, newValue);
      }
    } else {
      String currentValue = elementProperty.getTextContent();
      getLogger().debug(
          "Updating property '" + propertyName + "' from '" + currentValue + "' to '" + newValue
              + "' in project '" + this.id + "'...");
      elementProperty.setTextContent(newValue);
      setModified();
      this.propertyUpdateMap.put(propertyName, newValue);
    }
  }

  /**
   * @return the logger
   */
  public Log getLogger() {

    return this.logger;
  }

  /**
   * @return the id
   */
  public ProjectId getId() {

    return this.id;
  }

  /**
   * @return the parent
   */
  public ProjectContainer getParent() {

    return this.parent;
  }

  /**
   * @return the project
   */
  public MavenProject getProject() {

    return this.project;
  }

  private String getValue(Element dependencyElement, boolean resolveProperties, String tagname)
      throws MojoExecutionException {

    String value = DomUtilities.getChildElementValue(dependencyElement, tagname);
    if (resolveProperties && (value != null) && value.startsWith(ProjectContainer.PROPERTY_PREFIX)
        && value.endsWith(ProjectContainer.PROPERTY_SUFFIX)) {
      String variableName = value.substring(ProjectContainer.PROPERTY_PREFIX.length(), value
          .length()
          - ProjectContainer.PROPERTY_SUFFIX.length());
      String internalProperty = null;
      if (variableName.startsWith(PROPERTY_PREFIX_PROJECT)) {
        internalProperty = variableName.substring(PROPERTY_PREFIX_PROJECT.length());
      } else if (variableName.startsWith(PROPERTY_PREFIX_POM)) {
        internalProperty = variableName.substring(PROPERTY_PREFIX_POM.length());
      }
      String resolvedValue = null;
      if (internalProperty == null) {
        resolvedValue = this.project.getProperties().getProperty(variableName);
      } else {
        if (internalProperty.equals(XML_TAG_GROUPID)) {
          resolvedValue = this.project.getGroupId();
        } else if (internalProperty.equals(XML_TAG_ARTIFACTID)) {
          resolvedValue = this.project.getArtifactId();
        } else if (internalProperty.equals(XML_TAG_VERSION)) {
          resolvedValue = this.project.getVersion();
        } else {
          getLogger().warn(
              "Could NOT resolve internal property '" + internalProperty + "' - ignoring ...");
        }
      }
      if (resolvedValue == null) {
        getLogger().warn("Could NOT resolve property '" + variableName + "' - ignoring ...");
      } else {
        value = resolvedValue;
      }
    }
    return value;
  }

  public DependencyInfo createDependencyInfo(Element dependencyElement, boolean resolveProperties)
      throws MojoExecutionException {

    String groupId = getValue(dependencyElement, resolveProperties,
        ProjectContainer.XML_TAG_GROUPID);
    String artifactId = getValue(dependencyElement, resolveProperties,
        ProjectContainer.XML_TAG_ARTIFACTID);
    String version = getValue(dependencyElement, resolveProperties,
        ProjectContainer.XML_TAG_VERSION);
    String type = getValue(dependencyElement, resolveProperties, ProjectContainer.XML_TAG_TYPE);
    if (type == null) {
      type = DEFAULT_TYPE;
    }
    String scope = getValue(dependencyElement, resolveProperties, ProjectContainer.XML_TAG_SCOPE);
    if (scope == null) {
      scope = DEFAULT_SCOPE;
    }
    String classifier = getValue(dependencyElement, resolveProperties,
        ProjectContainer.XML_TAG_CLASSIFIER);
    return new DependencyInfo(groupId, artifactId, version, type, scope, classifier);
  }

  /**
   * @return the document
   */
  public Document getPomDocument() throws MojoExecutionException {

    if (this.document == null) {
      try {
        this.document = documentBuilder.parse(this.project.getFile());
      } catch (SAXException e) {
        throw new MojoExecutionException(
            "Illegal POM: " + this.project.getFile().getAbsolutePath(), e);
      } catch (IOException e) {
        throw new MojoExecutionException("Error reading POM: "
            + this.project.getFile().getAbsolutePath(), e);
      }
    }
    return this.document;
  }

  /**
   * @return the dependenciesList
   */
  public List<Element> getPomDependenciesList() throws MojoExecutionException {

    if (this.dependenciesList == null) {
      this.dependenciesList = DomUtilities.getChildElements(getPomDocument().getDocumentElement(),
          XML_TAG_DEPENDENCIES, XML_TAG_DEPENDENCY);
      if (this.dependenciesList == null) {
        getLogger().debug("No dependencies found!");
        this.dependenciesList = new ArrayList<Element>();
      } else {
        getLogger().debug("Number of dependencies found: " + this.dependenciesList.size());
      }
    }
    return this.dependenciesList;
  }

  /**
   * @return the dependencyManagementList
   */
  public List<Element> getPomDependencyManagementList() throws MojoExecutionException {

    if (this.dependencyManagementList == null) {
      this.dependencyManagementList = DomUtilities.getChildElements(getPomDocument()
          .getDocumentElement(), XML_TAG_DEPENDENCY_MANAGEMENT, XML_TAG_DEPENDENCY);
      if (this.dependencyManagementList == null) {
        getLogger().debug("No dependency-management found!");
        this.dependencyManagementList = new ArrayList<Element>();
      } else {
        getLogger().debug(
            "Number of dependencies in dependency-management found: "
                + this.dependenciesList.size());
      }
    }
    return this.dependencyManagementList;
  }

  public void setModified() {

    this.modified = true;
  }

  public void save(String encoding, boolean overwrite) throws MojoExecutionException {

    if ((this.document != null) && this.modified) {
      File targetFile;
      if (overwrite) {
        targetFile = this.project.getFile();
      } else {
        targetFile = new File(this.project.getBuild().getDirectory(), "pom-refactored.xml");
      }
      getLogger().info("Writing " + targetFile.getAbsolutePath() + " ...");
      try {
        Source source = new DOMSource(this.document);
        if (!targetFile.exists()) {
          targetFile.getParentFile().mkdirs();
          targetFile.createNewFile();
        }
        OutputStream outStream = new FileOutputStream(targetFile);
        Writer writer = new OutputStreamWriter(outStream, encoding);
        XalanHackWriter hackWriter = new XalanHackWriter(writer);
        PrintWriter pw = new PrintWriter(hackWriter);
        try {
          // Xalan-J sucks!
          // http://www.nabble.com/Output-a-new-line-after-the-XML-declaration-using-indent%3D%22yes%22-td15040090.html
          pw.println("<?xml version='1.0' encoding='" + encoding + "'?>");
          Result result = new StreamResult(pw);
          transformer.transform(source, result);
          pw.println();
        } catch (TransformerException e) {
          throw new MojoExecutionException("Error writing POM: " + targetFile.getAbsolutePath(), e);
        } finally {
          pw.close();
        }
      } catch (IOException e) {
        throw new MojoExecutionException("Error writing POM: " + targetFile.getAbsolutePath(), e);
      }
    }
  }

  private static class XalanHackWriter extends Writer {

    private final Writer delegate;

    private boolean done;

    private static final String COMMENT_OPEN = "<!--";

    private static final String COMMENT_CLOSE = "--><";

    private int commentOpenCount;

    private int commentCloseCount;

    /**
     * The constructor.
     * 
     * @param delegate
     */
    public XalanHackWriter(Writer delegate) {

      super();
      this.delegate = delegate;
      this.done = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {

      this.delegate.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {

      this.delegate.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {

      if (!this.done) {
        int index = off;
        if (this.commentOpenCount < COMMENT_OPEN.length()) {
          for (; index < len; index++) {
            if (cbuf[index] == COMMENT_OPEN.charAt(this.commentOpenCount)) {
              this.commentOpenCount++;
              if (this.commentOpenCount == COMMENT_OPEN.length()) {
                index++;
                break;
              }
            } else {
              if ((this.commentOpenCount == 1) && (cbuf[index] != '?')) {
                // root-tag passed...
                this.done = true;
                break;
              }
              this.commentOpenCount = 0;
            }
          }
        }
        if (this.commentCloseCount < COMMENT_CLOSE.length()) {
          for (; index < len; index++) {
            if (cbuf[index] == COMMENT_CLOSE.charAt(this.commentCloseCount)) {
              this.commentCloseCount++;
              if (this.commentCloseCount == COMMENT_CLOSE.length()) {
                // commentClosed and root-tag in same line (Xalan-J bug)
                int firstLength = index - off;
                if (firstLength > 0) {
                  this.delegate.write(cbuf, off, firstLength);
                }
                this.delegate.write(System.getProperty("line.separator"));
                this.delegate.write(cbuf, index, len - firstLength);
                return;
              }
            } else {
              this.commentCloseCount = 0;
            }
          }
        }
      }
      this.delegate.write(cbuf, off, len);
    }
  }
}
