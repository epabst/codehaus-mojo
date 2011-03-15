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
package org.codehaus.mojo.scmchangelog;

import java.io.StringWriter;

import org.apache.maven.scm.log.ScmLogger;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * Abstract consumer for the return stream from the svn commands.
 * @author ehsavoie
 * @version $Id: AbstractBufferedConsumer.java 7623 2008-09-09 08:46:14Z ehsavoie $
 */
public abstract class AbstractBufferedConsumer
    implements StreamConsumer
{

  /**
   * A buffer of the output.
   */
  private StringWriter buffer = new StringWriter();
  /**
   * The logger.
   */
  private ScmLogger logger;

  /**
   * @return a logger
   * @see org.apache.maven.scm.command.Command#getLogger()
   */
  public final ScmLogger getLogger()
  {
    return logger;
  }

  /**
   * Setter for the logger.
   * @param scmLogger the logger.
   * @see org.apache.maven.scm.command.Command#setLogger(org.apache.maven.scm.log.ScmLogger)
   */
  public final void setLogger( ScmLogger scmLogger )
  {
    this.logger = scmLogger;
  }

  // ----------------------------------------------------------------------
  // StreamConsumer Implementation
  // ----------------------------------------------------------------------
  /**
   * Consume the output of a process and store it in a buffer.
   * @param line the line to be consumed and stored in the buffer.
   */
  public void consumeLine( String line )
  {
    this.buffer.write( line );
  }

  /**
   * Returns the output.
   * @return the output.
   */
  protected String getOutput()
  {
    return this.buffer.toString();
  }

  /**
   * Parses the output of the command and returns a list of elements.
   * @return a list of elements.
   */
  public abstract java.util.List analyse();
}
