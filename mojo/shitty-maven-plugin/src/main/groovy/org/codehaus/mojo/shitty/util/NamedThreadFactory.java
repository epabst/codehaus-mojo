/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.mojo.shitty.util;

import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicLong;

/**
 * A {@link ThreadFactory} which automatically generates thread names based off of a
 * pre-configured basename passed in during construction and a unique index.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class NamedThreadFactory
    implements ThreadFactory
{
    private final String baseName;

    private final ThreadGroup group;

    private final AtomicLong counter = new AtomicLong(0);

    public NamedThreadFactory(final String baseName, final ThreadGroup group) {
        assert baseName != null;
        assert group != null;

        this.baseName = baseName;
        this.group = group;
    }

    public NamedThreadFactory(final String baseName) {
        this(baseName, Thread.currentThread().getThreadGroup());
    }

    /**
     * For Java 1.4 compat, since there is no Class.getSimpleName() :-(
     */
    private static String simpleName(final Class type) {
        assert type != null;

        String name = type.getName();

        return name.substring(name.lastIndexOf(".") + 1);
    }

    public NamedThreadFactory(final Class type) {
        this(simpleName(type));
    }

    public NamedThreadFactory(final Class type, final String suffix) {
        this(simpleName(type) + "-" + suffix);
    }

    public String getBaseName() {
        return baseName;
    }

    public ThreadGroup getGroup() {
        return group;
    }

    public long current() {
        return counter.get();
    }

    //
    // ThreadFactory
    //

    public Thread newThread(final Runnable task) {
        assert task != null;

        Thread t = new Thread(group, task, createName());
        
        configure(t);

        return t;
    }

    protected String createName() {
        return baseName + "-" + counter.getAndIncrement();
    }

    protected void configure(final Thread t) {
        t.setDaemon(true);
    }
}
