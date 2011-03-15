package org.codehaus.mojo.commons.attributes;

import junit.framework.TestCase;

import org.apache.commons.attributes.Attributes;
import org.apache.maven.plugin.MojoExecutionException;

public class CommonsAttributesMojoTest
    extends TestCase
{

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /*
     * Test method for 'org.codehaus.mojo.commons.attributes.CommonsAttributesMojo.execute()'
     */
    public void testExecute() throws MojoExecutionException, ClassNotFoundException
    {
        Class testClass = this.getClass().getClassLoader().loadClass ("org.codehaus.mojo.commons.attributes.TestClass");        
        assertEquals ( 0, Attributes.getAttributes(testClass).size() );
    }

}
