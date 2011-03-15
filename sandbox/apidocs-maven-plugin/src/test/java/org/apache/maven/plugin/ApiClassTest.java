package org.apache.maven.plugin;

/*
 * LICENSE
 */

import com.thoughtworks.qdox.model.JavaField;

import java.util.List;

import org.codehaus.plexus.PlexusTestCase;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ApiClassTest
    extends TestCase
{
    private ApiDocsPlugin plugin;

    public void setUp()
        throws Exception
    {
        plugin = new ApiDocsPlugin();

        plugin.setSourceDirectory( PlexusTestCase.getTestFile( "src/test/test-source" ) );
        plugin.setOutputDirectory( PlexusTestCase.getTestFile( "target/test-output" ) );

        plugin.initializeSources();
    }

    public void testName()
        throws Exception
    {
        ApiClass clazz = getClass( "mypackage.FooClass" );

        // name
        assertEquals( "FooClass", clazz.getName() );

        assertEquals( "mypackage.FooClass", clazz.getFullName() );

        assertEquals( "mypackage", clazz.getPackage() );
    }

    public void testParents()
    {
        ApiClass clazz = getClass( "mypackage.FooClass" );

        assertEquals( "TopLevelClass", clazz.getSuperClassName() );

        List parents = clazz.getParents();

        assertEquals( 2, parents.size() );

        assertEquals( "TopLevelClass", ((ApiClass)parents.get( 0 )).getFullName() );

        assertEquals( "java.lang.Object", ((ApiClass)parents.get( 1 )).getFullName() );

        List parentsReversed = clazz.getParentsReversed();

        assertEquals( 2, parentsReversed.size() );

        assertEquals( "java.lang.Object", ((ApiClass)parentsReversed.get( 0 )).getFullName() );

        assertEquals( "TopLevelClass", ((ApiClass)parentsReversed.get( 1 )).getFullName() );
    }

    public void testInterfaces()
    {
        ApiClass clazz = getClass( "mypackage.FooClass" );

        // interfaces
        List interfaces = clazz.getInterfaces();

        assertEquals( 2, interfaces.size() );

        assertEquals( "Interface1", ((ApiClass)interfaces.get( 0 )).getName() );

        assertEquals( "Interface2", ((ApiClass)interfaces.get( 1 )).getName() );

        assertEquals( "Interface1, Interface2", clazz.getInterfacesAsString() );
    }

    public void testClassDocs()
    {
        ApiClass clazz = getClass( "mypackage.FooClass" );

        assertEquals( "Author 1, Author 2, <a href=\"mailto:foo@bar.com\">Email Author</a>", clazz.getAuthorsAsString() );

        assertEquals( "Interface2, TopLevelClass, java.lang.Object", clazz.getSeeAlsoAsString() );
    }

    public void testFields()
    {
        ApiClass clazz = getClass( "mypackage.FooClass" );

        List fields = clazz.getFields();

        assertEquals( 4, fields.size() );

        assertEquals( "privateInt", ((JavaField)fields.get( 0 )).getName() );

        assertEquals( "protectedInt", ((JavaField)fields.get( 1 )).getName() );

        assertEquals( "publicInt", ((JavaField)fields.get( 2 )).getName() );

        assertEquals( "bleh", ((JavaField)fields.get( 3 )).getName() );
    }

    public void testMisc()
    {
        ApiClass clazz = getClass( "mypackage.FooClass" );

        assertFalse( clazz.isInterface() );

        assertTrue( clazz.isPublic() );
    }

    public void testInterface1()
    {
        ApiClass clazz = getClass( "mypackage.Interface1" );

        assertTrue( clazz.isInterface() );

        assertTrue( clazz.isPublic() );
    }

    public void testInterface2()
    {
        ApiClass clazz = getClass( "mypackage.Interface2" );

        assertTrue( clazz.isInterface() );

        assertFalse( clazz.isPublic() );
    }

    public void testJavaLangObject()
    {
        ApiClass clazz = getClass( "java.lang.Object" );

        assertEquals( "", clazz.getSuperClassName() );

        assertEquals( 0, clazz.getInterfaces().size() );

        assertEquals( 0, clazz.getParents().size() );
    }

    private ApiClass getClass( String className )
    {
        ApiClass clazz = plugin.getApiClass( className );

        assertNotNull( clazz );

        return clazz;
    }
}
