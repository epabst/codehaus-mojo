package org.codehaus.mojo.hibernate3;

import junit.framework.TestCase;

import java.io.File;

public class HibernateUtilsTest
    extends TestCase
{

    public static final String DIRECTORY_PATH = "org/codehaus/mojo/hibernate3";

    public static final String DUMMY_CLASS = "org.codehaus.mojo.hibernate3.DummyBean";

    public static final String DUMMY_WRONG_CLASS = "org.codehaus.mojo.hibernate3.DummyWrongBean";

    public static final String FILE_PATH = "org/codehaus/mojo/hibernate3/DummyBean.class";

    public static final String PARAMETER_NAME = "test";

    public static final File TARGET_DIRECTORY = new File( "target/test-classes" );

    public static void testGetClass()
    {
        Object o = HibernateUtils.getClass( DUMMY_CLASS );
        assertNotNull( o );
        assertEquals( o.getClass(), DummyBean.class );

        o = HibernateUtils.getClass( DUMMY_WRONG_CLASS, DUMMY_CLASS );
        assertNotNull( o );
        assertEquals( o.getClass(), DummyBean.class );
    }

    public static void testGetWrongClass()
    {
        assertNull( HibernateUtils.getClass( DUMMY_WRONG_CLASS ) );
        assertNull( HibernateUtils.getClass( DUMMY_WRONG_CLASS, DUMMY_WRONG_CLASS ) );
    }

    public void testPrepareDirectory()
    {
        try
        {
            HibernateUtils.prepareDirectory( TARGET_DIRECTORY, DIRECTORY_PATH, PARAMETER_NAME );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void testPrepareDirectoryWithFilePath()
    {
        try
        {
            HibernateUtils.prepareDirectory( TARGET_DIRECTORY, FILE_PATH, PARAMETER_NAME );
            fail( "Didn't threw mojo exception when it was expected to do so." );
        }
        catch ( Exception e )
        {
            // do nothing
        }
    }

    public void testPrepareFile()
    {
        try
        {
            HibernateUtils.prepareFile( TARGET_DIRECTORY, FILE_PATH, PARAMETER_NAME );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void testPrepareFileWithDirectoryPath()
    {
        try
        {
            HibernateUtils.prepareFile( TARGET_DIRECTORY, DIRECTORY_PATH, PARAMETER_NAME );
            fail( "Didn't threw mojo exception when it was expected to do so." );
        }
        catch ( Exception e )
        {
            // do nothing
        }
    }
}
