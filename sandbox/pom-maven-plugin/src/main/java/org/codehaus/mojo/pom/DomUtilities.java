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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author joerg
 * 
 */
final class DomUtilities {

  /**
   * Construction prohibited.
   */
  private DomUtilities() {

  }

  public static Element getChildElement(Element element, String... tagnames)
      throws MojoExecutionException {

    return getChildElement(element, tagnames, tagnames.length);
  }

  public static String getChildElementValue(Element element, String... tagnames)
      throws MojoExecutionException {

    Element childElement = getChildElement(element, tagnames, tagnames.length);
    if (childElement != null) {
      return childElement.getTextContent();
    }
    return null;
  }

  public static List<Element> getChildElements(Element element, String... xmlPath) throws MojoExecutionException {

    Element parent = getChildElement(element, xmlPath, xmlPath.length - 1);
    if (parent == null) {
      return null;
    }
    String tagname = xmlPath[xmlPath.length - 1];
    List<Element> result = new ArrayList<Element>();
    NodeList children = parent.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node childNode = children.item(i);
      if (childNode.getNodeType() == Node.ELEMENT_NODE) {
        Element childElement = (Element) childNode;
        if (childElement.getTagName().equals(tagname)) {
          result.add(childElement);
        }
      }
    }
    return result;
  }

  private static Element getChildElement(Element element, String[] tagnames, int length)
      throws MojoExecutionException {

    for (int xpathIndex = 0; xpathIndex < length; xpathIndex++) {
      String tagname = tagnames[xpathIndex];
      NodeList children = element.getChildNodes();
      Element nextElement = null;
      for (int i = 0; i < children.getLength(); i++) {
        Node childNode = children.item(i);
        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
          Element childElement = (Element) childNode;
          if (childElement.getTagName().equals(tagname)) {
            nextElement = childElement;
            break;
          }
        }
      }
      if (nextElement == null) {
        return null;
      }
      element = nextElement;
    }
    return element;
  }

}
