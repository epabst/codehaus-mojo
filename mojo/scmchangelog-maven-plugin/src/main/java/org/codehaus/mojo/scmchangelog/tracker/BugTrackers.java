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
package org.codehaus.mojo.scmchangelog.tracker;

/**
 * Manager for bug trackers.
 * @author ehsavoie
 * @version $Id$
 */
public class BugTrackers
{
  /**
   * Gforge bug tracker.
   */
  public static final BugTrackers SOURCEFORGE = new BugTrackers( "sourceforge" );
  /**
   * Jira bug tracker.
   */
  public static final BugTrackers JIRA = new BugTrackers( "jira" );
  /**
   * Bugzilla bug tracker.
   */
  public static final BugTrackers BUGZILLA = new BugTrackers( "bugzilla" );
  /**
   * XPlanner bug tracker.
   */
  public static final BugTrackers XPLANNER = new BugTrackers( "xplanner" );
  /**
   * Name of the bug tracker.
   */
  private String name;

  /**
   * Instatiate a new BugTracker with the specified name.
   * @param name the name of the bug tracker.
   */
  private BugTrackers( String name )
  {
    this.name = name;
  }

  /**
   * Compute the hashcode.
   * @return the hashcode.
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
    if ( object instanceof BugTrackers )
    {
      BugTrackers operation = ( BugTrackers ) object;

      return this.name.equals( operation.name );
    }

    return false;
  }

  /**
   * Return the bugtracker element matching the specified name. CODEX if no match is found.
   * @param name the name of the required bugtracker element.
   * @return the bugtracker element matching the specified name. CODEX if no match is found.
   */
  public static BugTrackers valueOf( String name )
  {
    if ( JIRA.name.equalsIgnoreCase( name ) )
    {
      return JIRA;
    }
    else if ( BUGZILLA.name.equalsIgnoreCase( name ) )
    {
      return BUGZILLA;
    }
    else if ( XPLANNER.name.equalsIgnoreCase( name ) )
    {
      return XPLANNER;
    }

    return SOURCEFORGE;
  }
}
