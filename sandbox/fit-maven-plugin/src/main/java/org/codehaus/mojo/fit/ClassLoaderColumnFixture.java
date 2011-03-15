package org.codehaus.mojo.fit;

import fit.ColumnFixture;
import fit.Fixture;

/**
 * Extends ColumnFixture to allow a custom ClassLoader to be used for loading fixtures
 * 
 * @author Mauro Talevi
 */
public class ClassLoaderColumnFixture
    extends ColumnFixture
    implements FixtureClassLoaderEnabled
{

    private FixtureClassLoader classLoader;

    public ClassLoaderColumnFixture()
    {
        this( new FixtureClassLoader() );
    }

    public ClassLoaderColumnFixture( FixtureClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    public void enableClassLoader( FixtureClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    public Fixture loadFixture( String fixtureName )
        throws InstantiationException, IllegalAccessException
    {
        return classLoader.newFixture( fixtureName );
    }
}
