package org.codehaus.mojo.commons.attributes;

/*
 * Copyright (c) 2006, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.commons.attributes.Attributes;
import org.apache.maven.plugin.MojoExecutionException;

public class CommonsAttributesMojoTest
    extends TestCase
{

    private ClassLoader classLoader;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        classLoader = this.getClass().getClassLoader();
    }

    public void testExecute()
        throws Exception
    {

        checkAttribute( "org.codehaus.mojo.commons.attributes.include.TestClassMain", "1" );
        checkAttribute( "org.codehaus.mojo.commons.attributes.include.TestClassTest", "2" );
        checkAttribute( "org.codehaus.mojo.commons.attributes.exclude.TestClassMain", null );
        checkAttribute( "org.codehaus.mojo.commons.attributes.exclude.TestClassTest", null );
    }

    private void checkAttribute( String className, String attributeValue )
        throws Exception
    {
        Class c = classLoader.loadClass( className );

        Collection attributes = Attributes.getAttributes( c );

        if ( attributeValue == null )
        {
            assertEquals( "Number of attributes for " + c.getName(), 0, attributes.size() );
        }
        else
        {
            assertEquals( "Number of attributes for " + c.getName(), 1, attributes.size() );
            Iterator it = attributes.iterator();
            TestAttribute attribute = (TestAttribute) it.next();
            assertEquals( "Value of attribute", attributeValue, attribute.getKey() );
        }
    }

}
