package org.codehaus.mojo.fit;

/**
 * Enables the use of a FixtureClassLoader 
 * 
 * @author Mauro Talevi
 */
public interface FixtureClassLoaderEnabled
{

    void enableClassLoader( FixtureClassLoader classLoader );

}
