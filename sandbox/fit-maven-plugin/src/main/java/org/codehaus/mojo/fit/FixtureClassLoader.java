package org.codehaus.mojo.fit;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fit.Fixture;

/**
 * Extends URLClassLoader to instantiate Fixture classes.
 * 
 * @author Mauro Talevi
 */
public class FixtureClassLoader
    extends URLClassLoader
{

    public FixtureClassLoader()
    {
        this( new URL[] {} );
    }

    public FixtureClassLoader( List classpathElements ) throws MalformedURLException
    {
        this( toClasspathURLs( classpathElements ) );
    }

    public FixtureClassLoader( URL[] urls )
    {
        this( urls, Fixture.class.getClassLoader() );
    }

    public FixtureClassLoader( URL[] urls, ClassLoader parent )
    {
        super( urls, parent );
    }

    /**
     * Loads and instantiates a Fixture 
     * 
     * @param fixtureName the name of the Fixture
     * @return A new Fixture instance
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Fixture newFixture( String fixtureName )
        throws InstantiationException, IllegalAccessException
    {
        String fixtureNotFound = "The fixture " + fixtureName + " was not found.";
        try
        {
            Fixture fixture = (Fixture) loadClass( fixtureName ).newInstance();
            if ( fixture instanceof FixtureClassLoaderEnabled )
            {
                ( (FixtureClassLoaderEnabled) fixture ).enableClassLoader( this );
            }
            Thread.currentThread().setContextClassLoader( this );
            return fixture;
        }
        catch ( ClassCastException e )
        {
            throw new RuntimeException( fixtureName + " is not a " + Fixture.class.getName(), e );
        }
        catch ( ClassNotFoundException e )
        {
            throw new RuntimeException( fixtureNotFound, e );
        }
        catch ( NoClassDefFoundError e )
        {
            throw new RuntimeException( fixtureNotFound, e );
        }
    }

    protected static URL[] toClasspathURLs( List classpathElements )
        throws MalformedURLException
    {
        List urls = new ArrayList();
        if ( classpathElements != null )
        {
            for ( Iterator i = classpathElements.iterator(); i.hasNext(); )
            {
                String classpathElement = (String) i.next();
                urls.add( new File( classpathElement ).toURL() );
            }
        }
        return (URL[]) urls.toArray( new URL[urls.size()] );
    }

}
