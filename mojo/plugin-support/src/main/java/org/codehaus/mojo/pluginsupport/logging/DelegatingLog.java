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

import java.io.Serializable;
import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;

/**
 * Jakarta Commons Logging Log which delegates to another Log type.
 *
 * @version $Id$
 */
public class DelegatingLog
    implements Log, Serializable
{
    private static Constructor factory;

    public static void setDelegateType(final Class type) {
        assert type != null;

        try {
            factory = type.getConstructor(new Class[] { String.class });
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to lookup (String) constructor for type: " + type, e);
        }
    }

    public static Log getDelegate(final String name) {
        // If no factory is set, then use a SimpleLog... so logging always works
        if (factory == null) {
            return new SimpleLog(name);
        }

        try {
            return (Log) factory.newInstance(new Object[] { name });
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to construct delegate logger using factory: " + factory, e);
        }
    }

    private String name;

    public DelegatingLog(final String name) {
        assert name != null;

        this.name = name;
    }

    private Log getLog() {
        return getDelegate(name);
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
        return getLog().isTraceEnabled();
    }

    public boolean isWarnEnabled() {
        return getLog().isWarnEnabled();
    }

    private String createMessage(final Object object) {
        return String.valueOf(object);
    }

    public void trace(final Object object) {
        getLog().trace(object);
    }

    public void trace(final Object object, final Throwable throwable) {
        getLog().trace(object, throwable);
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
