package org.codehaus.mojo.fit;

import fit.ActionFixture;
import fit.Fixture;

/**
 * Extends ActionFixture to allow a custom ClassLoader to be used for loading fixtures
 * 
 * @author Mauro Talevi
 */
public class ClassLoaderActionFixture
    extends ActionFixture
    implements FixtureClassLoaderEnabled
{

    private FixtureClassLoader classLoader;

    public ClassLoaderActionFixture()
    {
        this( new FixtureClassLoader() );
    }

    public ClassLoaderActionFixture( FixtureClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    public void enableClassLoader( FixtureClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    public void start()
        throws Exception
    {
        actor = loadFixture( cells.more.text() );
    }

    public Fixture loadFixture( String fixtureName )
        throws InstantiationException, IllegalAccessException
    {
        return classLoader.newFixture( fixtureName );
    }

}
