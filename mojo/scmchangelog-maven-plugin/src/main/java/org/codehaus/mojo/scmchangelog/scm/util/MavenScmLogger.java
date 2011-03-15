/*
The MIT License

Copyright (c) 2004, The Codehaus

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package org.codehaus.mojo.scmchangelog.scm.util;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.scm.log.ScmLogger;

/**
 * Implementation of ScmLogger using org.apache.maven.plugin.logging.Log.
 * @author ehsavoie
 * @version $Id: JavaScmLogger.java 7652 2008-09-11 07:58:40Z ehsavoie $
 * @see org.apache.maven.scm.log.ScmLogger
 */
public class MavenScmLogger
        implements ScmLogger
{
    /**
     * The 'real' logger implementation.s
     */
    private Log logger;

    /**
     * Create a new MavenScmLogger (ScmLoger that wraps a maven Log)
     * @param logger the 'real' logger to be wrapped as a ScmLogger.
     */
    public MavenScmLogger( Log logger )
    {
        if ( isColorized() )
        {
            this.logger = new ColorConsoleLogger( logger );
        }
        else
        {
            this.logger = logger;
        }        
    }

    /** {@inheritDoc} */
    public boolean isDebugEnabled()
    {
        return logger.isDebugEnabled();
    }

    /** {@inheritDoc} */
    public void debug( String content )
    {
        logger.debug( content );
    }

    /** {@inheritDoc} */
    public void debug( String content, Throwable error )
    {
        logger.debug( content, error );
    }

    /** {@inheritDoc} */
    public void debug( Throwable error )
    {
        logger.debug( error );
    }

    /** {@inheritDoc} */
    public boolean isInfoEnabled()
    {
        return logger.isInfoEnabled();
    }

    /** {@inheritDoc} */
    public void info( String content )
    {
        logger.info( content );
    }

    /** {@inheritDoc} */
    public void info( String content, Throwable error )
    {
        logger.info( content, error );
    }

    /** {@inheritDoc} */
    public void info( Throwable error )
    {
        logger.info( error );
    }

    /** {@inheritDoc} */
    public boolean isWarnEnabled()
    {
        return logger.isWarnEnabled();
    }

    /** {@inheritDoc} */
    public void warn( String content )
    {
        logger.warn( content );
    }

    /** {@inheritDoc} */
    public void warn( String content, Throwable error )
    {
        logger.warn( content, error );
    }

    /** {@inheritDoc} */
    public void warn( Throwable error )
    {
        logger.warn( error );
    }

    /** {@inheritDoc} */
    public boolean isErrorEnabled()
    {
        return logger.isErrorEnabled();
    }

    /** {@inheritDoc} */
    public void error( String content )
    {
        logger.error( content );
    }

    /** {@inheritDoc} */
    public void error( String content, Throwable error )
    {
        logger.error( content, error );
    }

    /** {@inheritDoc} */
    public void error( Throwable error )
    {
        logger.error( error );
    }

    /**
     * Indicates if th logs will be ANSI colorized.
     * @return true if there will be colors - false otherwise.
     */
    private boolean isColorized()
    {
        return System.getProperty( "colorized.console" ) != null;
    }
}