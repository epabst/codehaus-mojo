package org.codehaus.mojo.fit;

import fit.Fixture;

/**
 * Extends Fixture to allow a FixtureClassLoader to be used for loading fixtures
 * 
 * @author Mauro Talevi
 */
public class ClassLoaderFixture
    extends Fixture
    implements FixtureClassLoaderEnabled
{

    private FixtureClassLoader classLoader;

    public ClassLoaderFixture( FixtureClassLoader classLoader )
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
