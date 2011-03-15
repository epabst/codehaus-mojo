package org.codehaus.mojo.versions;

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

import java.util.Iterator;
import java.util.Map;
import javax.xml.stream.XMLStreamException;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.versions.api.PomHelper;
import org.codehaus.mojo.versions.api.PropertyVersions;
import org.codehaus.mojo.versions.rewriting.ModifiedPomXMLEventReader;

/**
 * Sets a property to the latest version in a given range of associated artifacts.
 *
 * @author Eric Pabst
 * @goal update-property
 * @requiresProject true
 * @requiresDirectInvocation true
 * @since 1.3
 */
public class UpdatePropertyMojo
    extends AbstractVersionsUpdaterMojo
{

// ------------------------------ FIELDS ------------------------------

    /**
     * A property to update.
     *
     * @parameter expression="${property}"
     * @since 1.3
     */
    private String property = null;

    /**
     * The new version to set the property to (can be a version range to find a version within).
     *
     * @parameter expression="${newVersion}"
     * @since 1.3
     */
    private String newVersion = null;

    /**
     * Whether properties linking versions should be auto-detected or not.
     *
     * @parameter expression="${autoLinkItems}" defaultValue="true"
     * @since 1.0-alpha-2
     */
    private Boolean autoLinkItems;

// -------------------------- STATIC METHODS --------------------------

    // -------------------------- OTHER METHODS --------------------------

    /**
     * @param pom the pom to update.
     * @throws MojoExecutionException when things go wrong
     * @throws MojoFailureException   when things go wrong in a very bad way
     * @throws XMLStreamException     when things go wrong with XML streaming
     * @see AbstractVersionsUpdaterMojo#update(ModifiedPomXMLEventReader)
     * @since 1.0-alpha-1
     */
    protected void update( ModifiedPomXMLEventReader pom )
        throws MojoExecutionException, MojoFailureException, XMLStreamException
    {
        Property propertyConfig = new Property(property);
        propertyConfig.setVersion(newVersion);
        Map propertyVersions =
            this.getHelper().getVersionPropertiesMap( getProject(), new Property[]{propertyConfig}, property, "",
                                                      !Boolean.FALSE.equals( autoLinkItems ) );
        Iterator i = propertyVersions.entrySet().iterator();
        while ( i.hasNext() )
        {
            Map.Entry/*<Property,PropertyVersions>*/ entry = (Map.Entry/*<Property,PropertyVersions>*/) i.next();
            Property property = (Property) entry.getKey();
            PropertyVersions version = (PropertyVersions) entry.getValue();

            final String currentVersion = getProject().getProperties().getProperty( property.getName() );
            if ( currentVersion == null )
            {
                continue;
            }

            ArtifactVersion winner =
                version.getNewestVersion( currentVersion, property, this.allowSnapshots, this.reactorProjects,
                                          this.getHelper() );

            if ( winner == null || currentVersion.equals( winner.toString() ) )
            {
                getLog().info( "Property ${" + property.getName() + "}: Leaving unchanged as " + currentVersion );
            }
            else if ( PomHelper.setPropertyVersion( pom, version.getProfileId(), property.getName(),
                                                    winner.toString() ) )
            {
                getLog().info( "Updated ${" + property.getName() + "} from " + currentVersion + " to " + winner );
            }

        }
    }

}