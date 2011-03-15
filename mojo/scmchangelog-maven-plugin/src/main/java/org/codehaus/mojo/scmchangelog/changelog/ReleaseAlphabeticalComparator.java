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
package org.codehaus.mojo.scmchangelog.changelog;

import java.util.Comparator;

/**
 * Comparator to order releases alphabetically.
 * @author ehsavoie
 * @version $Id: Release.java 7652 2008-09-11 07:58:40Z ehsavoie $
 *
 * @see org.codehaus.mojo.scmchangelog.changelog.Release
 */
public class ReleaseAlphabeticalComparator implements Comparator
{

  /**
   * Compare two Release alphabetically.
   * @param o1 the first release to be compared.
   * @param o2 the second release to be compared.
   * @return a positive int if o1 after o2 - negative otherwise.
   */
  public int compare( Object o1, Object o2 )
  {
    Release release1 = ( Release ) o1;
    Release release2 = ( Release ) o2;
    return release2.getTag().getTitle().compareTo( release1.getTag().getTitle() );
  }
}
