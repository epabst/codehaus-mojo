package org.codehaus.mojo.springbeandoc;

/*
   The MIT License
   .
   Copyright (c) 2005, Ghent University (UGent)
   .
   Permission is hereby granted, free of charge, to any person obtaining a copy of
   this software and associated documentation files (the "Software"), to deal in
   the Software without restriction, including without limitation the rights to
   use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
   of the Software, and to permit persons to whom the Software is furnished to do
   so, subject to the following conditions:
   .
   The above copyright notice and this permission notice shall be included in all
   copies or substantial portions of the Software.
   .
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
   SOFTWARE.
 */

/**
 * Combination of a packagename and its javadoc location.
 *
 * @author Jurgen De Landsheer
 * @version 27 January 2006
 */
public class JavadocLocation {
  /**
   * Location (http or local - absolute or relative).
   */
  private String location;

  /**
   * Package name.
   */
  private String packagename;

  /**
   * Gets location property.
   *
   * @return Returns the location.
   */
  public String getLocation() {
    return this.location;
  }

  /**
   * Gets packagename property.
   *
   * @return Returns the packagename.
   */
  public String getPackagename() {
    return this.packagename;
  }

  /**
   * Sets location property.
   *
   * @param v The location to set.
   */
  public void setLocation(final String v) {
    this.location = v;
  }

  /**
   * Sets packagename property.
   *
   * @param v The packagename to set.
   */
  public void setPackagename(final String v) {
    this.packagename = v;
  }
}

