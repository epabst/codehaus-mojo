package org.codehaus.mojo.virtualization;

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

import net.java.dev.vcc.api.Log;
import net.java.dev.vcc.spi.AbstractLog;
import net.java.dev.vcc.spi.AbstractLogFactory;

import java.text.MessageFormat;

/**
 * Created by IntelliJ IDEA.
 *
 * @author connollys
 * @since Aug 11, 2009 2:18:50 PM
 */
public class MavenLogFactory
    extends AbstractLogFactory
{
    private static org.apache.maven.plugin.logging.Log log;

    protected Log newLog( String s, String s1 )
    {
        final org.apache.maven.plugin.logging.Log delegate;
        synchronized ( MavenLogFactory.class )
        {
            delegate = log;
        }
        return new AbstractLog()
        {
            @Override
            protected void debug( String s, Throwable throwable, String s1, Object[] objects )
            {
                delegate.debug( MessageFormat.format(s1, objects ), throwable);
            }

            @Override
            protected void info( String s, Throwable throwable, String s1, Object[] objects )
            {
                delegate.info( MessageFormat.format( s1, objects ), throwable );
            }

            @Override
            protected void warn( String s, Throwable throwable, String s1, Object[] objects )
            {
                delegate.warn( MessageFormat.format( s1, objects ), throwable );
            }

            @Override
            protected void error( String s, Throwable throwable, String s1, Object[] objects )
            {
                delegate.error( MessageFormat.format( s1, objects ), throwable );
            }

            @Override
            protected void fatal( String s, Throwable throwable, String s1, Object[] objects )
            {
                delegate.error( MessageFormat.format( s1, objects ), throwable );
            }

            public boolean isDebugEnabled()
            {
                return delegate.isDebugEnabled();
            }

            public boolean isInfoEnabled()
            {
                return delegate.isInfoEnabled();
            }

            public boolean isWarnEnabled()
            {
                return delegate.isWarnEnabled();
            }

            public boolean isErrorEnabled()
            {
                return delegate.isErrorEnabled();
            }

            public boolean isFatalEnabled()
            {
                return delegate.isErrorEnabled();
            }
        };
    }

    public static synchronized void setLog( org.apache.maven.plugin.logging.Log log )
    {
        MavenLogFactory.log = log;
    }
}
