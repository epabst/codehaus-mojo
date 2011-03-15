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
package org.codehaus.mojo.scmchangelog.changelog.log;

/**
 * Type of operation supported
 * Currently there are 4 types :
 * <ul><li>add</li><li>remove</li><li>fix</li><li>update</li></ul>
 * @author ehsavoie
 * @version $Id$
 */
public class OperationTypeEnum
{
  /**
   * The ADD operation.
   */
  public static final OperationTypeEnum ADD = new OperationTypeEnum( "ADD" );
  /**
   * The UPDATE operation.
   */
  public static final OperationTypeEnum UPDATE = new OperationTypeEnum(
      "UPDATE" );
  /**
   * The FIX operation.
   */
  public static final OperationTypeEnum FIX = new OperationTypeEnum( "FIX" );
  /**
   * The REMOVE operation.
   */
  public static final OperationTypeEnum REMOVE = new OperationTypeEnum(
      "REMOVE" );
  /**
   * The name of the operation element.
   */
  private String name;

  /**
   * Insttiates a new operation element.
   * @param name the name of the operation.
   */
  private OperationTypeEnum( String name )
  {
    this.name = name;
  }

  /**
   * Return the hashcode for this operation element.
   * @return the hashcode for this operation element.
   */
  public int hashCode()
  {
    return this.name.hashCode();
  }

  /**
   * Equality with another object.
   * @param object the object to be checked for equality against.
   * @return true if object equals this - false otherwise.
   */
  public boolean equals( Object object )
  {
    if ( object instanceof OperationTypeEnum )
    {
      OperationTypeEnum operation = ( OperationTypeEnum ) object;

      return this.name.equals( operation.name );
    }

    return false;
  }
}
