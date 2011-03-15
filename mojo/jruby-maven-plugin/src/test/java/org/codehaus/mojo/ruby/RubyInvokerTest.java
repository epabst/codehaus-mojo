package org.codehaus.mojo.ruby;

import org.codehaus.plexus.PlexusTestCase;

public class RubyInvokerTest
    extends PlexusTestCase
{
    public void test1()
        throws Exception
    {
        RubyMojo mojo = (RubyMojo)lookup( RubyMojo.ROLE );

        mojo.set( "msg", new Integer(5) );

        mojo.execute();
    }
}
