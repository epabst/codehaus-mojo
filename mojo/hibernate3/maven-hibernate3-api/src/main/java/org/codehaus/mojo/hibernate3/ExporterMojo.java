package org.codehaus.mojo.hibernate3;

/*
 * Copyright 2005 Johann Reyes.
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

import org.apache.maven.plugin.Mojo;
import org.apache.maven.project.MavenProject;

/**
 * Interface for the different types of exporters found in hibernate-tools.
 *
 * @author <a href="mailto:jreyes@hiberforum.org">Johann Reyes</a>
 * @version $Id$
 */
public interface ExporterMojo
    extends Mojo
{
    /**
     * Returns the value from the <i>componentProperties</i> element which key is the one being passed.
     *
     * @param key Key
     * @return String
     */
    String getComponentProperty( String key );

    /**
     * Returns the value from the <i>componentProperties</i> element which key is the one being passed, or if
     * is null or empty then returns the default value being specified.
     *
     * @param key          Key
     * @param defaultValue Default value
     * @return String
     */
    String getComponentProperty( String key, String defaultValue );

    /**
     * Returns the value from the <i>componentProperties</i> element as a boolean value, or if a value is not found
     * returns the default value.
     *
     * @param key          Key
     * @param defaultValue Default value
     * @return boolean
     */
    boolean getComponentProperty( String key, boolean defaultValue );

    /**
     * Name of the goal being invoked.
     *
     * @return String goal's name
     */
    String getName();

    /**
     * Returns the MavenProject object.
     *
     * @return MavenProject
     */
    MavenProject getProject();
}
