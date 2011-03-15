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

package org.codehaus.mojo.pluginsupport.ant;

import java.io.File;
import java.util.Map;
import java.util.Iterator;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.maven.project.MavenProject;

import org.codehaus.mojo.pluginsupport.ComponentSupport;

/**
 * Support for Ant-based Mojos.
 *
 * @version $Id$
 */
public class AntHelper
    extends ComponentSupport
{
    private MavenProject project;

    private Project ant;

    public Project getAnt() {
        if (ant == null) {
            ant = new Project();
            ant.setBaseDir(getProject().getBasedir());

            initAntLogger(ant);

            ant.init();
        }

        return ant;
    }

    public void setProject(final MavenProject project) {
        assert project != null;

        this.project = project;
    }

    private MavenProject getProject() {
        if (project == null) {
            throw new IllegalStateException("Not initialized; missing MavenProject");
        }
        return project;
    }

    public FileSet createFileSet() {
        FileSet set = new FileSet();
        set.setProject(getAnt());
        return set;
    }

    public Task createTask(final String name) throws BuildException {
        assert name != null;

        return getAnt().createTask(name);
    }

    protected void initAntLogger(final Project ant) {
        CommonsLoggingAntLoggerAdapter antLogger = new CommonsLoggingAntLoggerAdapter(log);
        antLogger.setEmacsMode(true);
        antLogger.setOutputPrintStream(System.out);
        antLogger.setErrorPrintStream(System.err);

        if (log.isDebugEnabled()) {
            antLogger.setMessageOutputLevel(Project.MSG_VERBOSE);
        }
        else {
            antLogger.setMessageOutputLevel(Project.MSG_INFO);
        }

        ant.addBuildListener(antLogger);
    }

    protected void setProperty(final String name, Object value) {
        assert name != null;
        assert value != null;

        String valueAsString = String.valueOf(value);

        if (log.isDebugEnabled()) {
            log.debug("Setting property: " + name + "=" + valueAsString);
        }

        Property prop = (Property)createTask("property");
        prop.setName(name);
        prop.setValue(valueAsString);
        prop.execute();
    }

    public void inheritProperties() {
        // Propagate properties
        Map props = getProject().getProperties();
        Iterator iter = props.keySet().iterator();
        while (iter.hasNext()) {
            String name = (String)iter.next();
            String value = String.valueOf(props.get(name));
            setProperty(name, value);
        }

        // Hardcode a few
        setProperty("pom.basedir", getProject().getBasedir());
    }
    
    public void setSystemProperty(final Java java, final String name, final String value) {
        assert java != null;
        assert name != null;
        assert value != null;

        Environment.Variable var = new Environment.Variable();
        var.setKey(name);
        var.setValue(value);
        java.addSysproperty(var);
    }

    public void setSystemProperty(final Java java, final String name, final File value) {
        assert java != null;
        assert name != null;
        assert value != null;

        Environment.Variable var = new Environment.Variable();
        var.setKey(name);
        var.setFile(value);
        java.addSysproperty(var);
    }

    public void mkdir(final File dir) {
        assert dir != null;

        Mkdir mkdir = (Mkdir)createTask("mkdir");
        mkdir.setDir(dir);
        mkdir.execute();
    }

    public void echo(final String msg) {
        assert msg != null;
        
        Echo echo = (Echo)createTask("echo");
        echo.setMessage(msg);
        echo.execute();
    }
}
