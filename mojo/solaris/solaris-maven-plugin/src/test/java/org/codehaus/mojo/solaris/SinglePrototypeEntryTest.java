package org.codehaus.mojo.solaris;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SinglePrototypeEntryTest extends TestCase
{
    public void testPathAdjustment()
    {
        // Pattern starts with /, path starts with /
        test( "/opt/yo", Boolean.FALSE, "/opt/yo" );

        // Pattern starts with /, path NOT starts with /
        test( "/opt/yo", Boolean.FALSE, "opt/yo" );

        // Pattern NOT starts with /, path starts with /
        test( "opt/yo", Boolean.TRUE, "/opt/yo" );

        // Pattern NOT starts with /, path NOT starts with /
        test( "opt/yo", Boolean.TRUE, "opt/yo" );
    }

    private void test( String expected, Boolean relative, String path )
    {
        FileEntry entry = new FileEntry();
        entry.setPath( path );
        entry.setRelative( relative );
        assertEquals( expected,  entry.getProcessedPath() );
    }
}
