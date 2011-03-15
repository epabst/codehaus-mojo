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

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.maven.plugin.logging.Log;

/**
 * Logger Wrapper that uses the ANSI Console Commands to add some colors.
 * @author ehsavoie
 * @version $Id$
 */
public class ColorConsoleLogger implements Log
{
  private static final int ATTR_DIM = 2;
  private static final int FG_RED = 31;
  private static final int FG_GREEN = 32;
  //private static final int FG_BLUE = 34;
  private static final int FG_MAGENTA = 35;
  private static final int FG_CYAN = 36;
  private static final String PREFIX = "\u001b[";
  private static final String SUFFIX = "m";
  private static final char SEPARATOR = ';';
  private static final String END_COLOR = PREFIX + SUFFIX;
  private String errColor = PREFIX + ATTR_DIM + SEPARATOR + FG_RED + SUFFIX;
  private String warnColor = PREFIX + ATTR_DIM + SEPARATOR + FG_MAGENTA + SUFFIX;
  private String infoColor = PREFIX + ATTR_DIM + SEPARATOR + FG_CYAN + SUFFIX;
  private String debugColor = PREFIX + ATTR_DIM + SEPARATOR + FG_GREEN + SUFFIX;
  /**
   * The 'real' logger implementation.s
   */
  private Log realLogger;

 /**
  * Create a new ColorConsoleLogger that wraps a maven Log.
  * @param logger the 'real' logger to be wrapped.
  **/
  public ColorConsoleLogger( Log realLogger )
  {
    this.realLogger = realLogger;
  }

  /** {@inheritDoc} */
  public boolean isDebugEnabled()
  {
    return this.realLogger.isDebugEnabled();
  }

  /** {@inheritDoc} */
  public void debug( CharSequence content )
  {
    this.realLogger.debug( debugColor + content + END_COLOR );
  }

  /** {@inheritDoc} */
  public void debug( CharSequence content, Throwable error )
  {
    this.debug( content + "\n\n" + formatError( error ) );
  }

  /** {@inheritDoc} */
  public void debug( Throwable error )
  {
    this.debug( formatError( error ) );
  }

  /** {@inheritDoc} */
  public boolean isInfoEnabled()
  {
    return this.realLogger.isInfoEnabled();
  }

  /** {@inheritDoc} */
  public void info( CharSequence content )
  {
    this.realLogger.info( infoColor + content + END_COLOR );
  }

  /** {@inheritDoc} */
  public void info( CharSequence content, Throwable error )
  {
    this.info( content + "\n\n" + formatError( error ) );
  }

  /** {@inheritDoc} */
  public void info( Throwable error )
  {
    this.info( formatError( error ) );
  }

  /** {@inheritDoc} */
  public boolean isWarnEnabled()
  {
    return this.realLogger.isWarnEnabled();
  }

  /** {@inheritDoc} */
  public void warn( CharSequence content )
  {
    this.realLogger.warn( warnColor + content + END_COLOR );
  }

  /** {@inheritDoc} */
  public void warn( CharSequence content, Throwable error )
  {
    this.warn( content + "\n\n" + formatError( error ) );
  }

  /** {@inheritDoc} */
  public void warn( Throwable error )
  {
    this.warn( formatError( error ) );
  }

  /** {@inheritDoc} */
  public boolean isErrorEnabled()
  {
    return this.realLogger.isErrorEnabled();
  }

  /** {@inheritDoc} */
  public void error( CharSequence content )
  {
    this.realLogger.error( errColor + content + END_COLOR );
  }

  /** {@inheritDoc} */
  public void error( CharSequence content, Throwable error )
  {
    this.error( content + "\n\n" + formatError( error ) );
  }

  /** {@inheritDoc} */
  public void error( Throwable error )
  {
    this.error( formatError( error ) );
  }

  /**
   * Format the exception to a readable message.
   * @param error the exception to be formatted.
   * @return a readable message for the specified exception.
   */
  private String formatError( Throwable error )
  {
    StringWriter sWriter = new StringWriter();
    PrintWriter pWriter = new PrintWriter( sWriter );
    error.printStackTrace( pWriter );
    return sWriter.toString();
  }
}
