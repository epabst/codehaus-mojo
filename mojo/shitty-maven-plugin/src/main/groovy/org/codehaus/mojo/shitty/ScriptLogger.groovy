/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance doWith the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * doWithOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.mojo.shitty

import org.apache.maven.plugin.logging.Log
import org.codehaus.groovy.maven.common.StreamPair

/**
 * Adapter for script logging, so that it dosen't get hijacked.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
class ScriptLogger
    implements Log
{
    private final PrintStream out
    
    private final PrintStream err
    
    ScriptLogger(final PrintStream out, final PrintStream err) {
        assert out
        assert err
        
        this.out = out
        this.err = err
    }
    
    ScriptLogger(final StreamPair streams) {
        assert streams
        
        this.out = streams.out
        this.err = streams.err
    }
    
    ScriptLogger() {
        this(System.out, System.err)
    }
    
    private void print(PrintStream out, String prefix, CharSequence content) {
        out.println("[${prefix}] $content")
    }

    private void print(PrintStream out, String prefix, Throwable cause) {
        out.println("[${prefix}] $cause")
        
        cause.printStackTrace(out)
    }

    private void print(PrintStream out, String prefix, CharSequence content, Throwable cause) {
        out.println("[${prefix}] $content")
        
        cause.printStackTrace(out)
    }
    
    boolean isDebugEnabled() {
        return false
    }

    boolean isInfoEnabled() {
        return true
    }

    boolean isWarnEnabled() {
        return true
    }

    boolean isErrorEnabled() {
        return true
    }
    
    void debug(CharSequence content) {
        print(out, 'DEBUG', content)
    }

    void debug(CharSequence content, Throwable cause) {
        print(out, 'DEBUG', content, cause)
    }
    
    void debug(Throwable cause) {
        print(out, 'DEBUG', cause)
    }

    void info(CharSequence content) {
        print(out, 'INFO', content)
    }

    void info(CharSequence content, Throwable cause) {
        print(out, 'INFO', content, cause)
    }
    
    void info(Throwable cause) {
        print(out, 'INFO', cause)
    }

    void warn(CharSequence content) {
        print(out, 'WARN', content)
    }

    void warn(CharSequence content, Throwable cause) {
        print(out, 'WARN', content, cause)
    }

    void warn(Throwable cause) {
        print(out, 'WARN', cause)
    }

    void error(CharSequence content) {
        print(err, 'ERROR', content)
    }

    void error(CharSequence content, Throwable cause) {
        print(err, 'ERROR', content, cause)
    }

    void error(Throwable cause) {
        print(err, 'ERROR', cause)
    }
}
