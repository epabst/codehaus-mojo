package org.codehaus.mojo.syslog;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

/*
 * Copyright 2000-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * Base class of all syslog-maven-plugin's MOJOs
 */
public abstract class AbstractSyslogMojo
    extends AbstractMojo
{

    /**
     * Protocol: tcp,udp
     * @parameter expression="${protocol}" default-value="tcp"
     */
    protected String protocol;

    /**
     * Listener port
     * @parameter expression="${port}" default-value="514"
     */
    protected int port;

    /**
     * Internal
     * 
     * @parameter expression="${project}"
     * @readonly
     * @since 1.0-beta-1
     */
    protected MavenProject project;

}
