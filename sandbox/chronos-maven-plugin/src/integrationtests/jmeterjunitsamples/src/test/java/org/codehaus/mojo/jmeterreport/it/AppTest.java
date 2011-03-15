package org.codehaus.mojo.jmeterreport.it;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest extends TestCase {
    static int count = 0;
    
    protected void setUp() {
        fail("setup");
    }

    public void testApp() {
        long sleepTime = ++count;
        try {
            Thread.sleep(sleepTime/1000 + 50 *  ((int)Math.random()));
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
        }
    }
}
