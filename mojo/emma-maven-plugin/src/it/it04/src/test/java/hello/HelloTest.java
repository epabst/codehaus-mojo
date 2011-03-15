package hello;

import junit.framework.TestCase;

public class HelloTest extends TestCase
{
    public void testHello()
    {
        final Hello hello = new Hello();
        assertEquals( "Hello EMMA!", hello.hello( "EMMA" ) );
    }
}
