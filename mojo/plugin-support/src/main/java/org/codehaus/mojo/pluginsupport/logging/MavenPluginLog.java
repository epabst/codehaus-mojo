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

package org.codehaus.mojo.pluginsupport.logging;

import org.apache.maven.plugin.Mojo;
import org.apache.commons.logging.Log;

/**
 * Bridge from the Maven plugin Log to a JCL Log.
 *
 * @version $Id$
 */
public class MavenPluginLog
    implements Log
{
    private static Mojo mojo;

    public static void setMojo(final Mojo mojo) {
        assert mojo != null;

        MavenPluginLog.mojo = mojo;
    }

    private String name;

    public MavenPluginLog(final String name) {
        assert name != null;

        this.name = name;
    }

    private org.apache.maven.plugin.logging.Log getLog() {
        if (mojo == null) {
            throw new RuntimeException("Mojo not set; can not delegate logging");
        }

        return mojo.getLog();
    }

    public boolean isDebugEnabled() {
        return getLog().isDebugEnabled();
    }

    public boolean isErrorEnabled() {
        return getLog().isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return getLog().isErrorEnabled();
    }

    public boolean isInfoEnabled() {
        return getLog().isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        // return getLog().isDebugEnabled();
        return false;
    }

    public boolean isWarnEnabled() {
        return getLog().isWarnEnabled();
    }

    private String createMessage(final Object object) {
        if (isDebugEnabled()) {
            return "(" + name + ") " + object;
        }
        else {
            return String.valueOf(object);
        }
    }

    public void trace(final Object object) {
        if (isTraceEnabled()) {
            debug(object);
        }
    }

    public void trace(final Object object, final Throwable throwable) {
        if (isTraceEnabled()) {
            debug(object, throwable);
        }
    }

    public void debug(final Object object) {
        getLog().debug(createMessage(object));
    }

    public void debug(final Object object, final Throwable throwable) {
        getLog().debug(createMessage(object), throwable);
    }

    public void info(final Object object) {
        getLog().info(createMessage(object));
    }

    public void info(final Object object, final Throwable throwable) {
        getLog().info(createMessage(object), throwable);
    }

    public void warn(final Object object) {
        getLog().warn(createMessage(object));
    }

    public void warn(final Object object, final Throwable throwable) {
        getLog().warn(createMessage(object), throwable);
    }

    public void error(final Object object) {
        getLog().error(createMessage(object));
    }

    public void error(final Object object, final Throwable throwable) {
        getLog().error(createMessage(object), throwable);
    }

    public void fatal(final Object object) {
        error(object);
    }

    public void fatal(final Object object, final Throwable throwable) {
        error(object, throwable);
    }
}