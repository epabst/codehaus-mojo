package org.codehaus.mojo;

import org.apache.commons.lang.StringUtils;

/**
 * This is brute nonesense to demonstrate the maven-solaris-plugin.
 *
 * @author Joerg Hohwiller (hohwille at users.sourceforge.net)
 */
public class MyApp
{
    public void run()
    {
        System.out.println( StringUtils.repeat( "Hello World!" + System.getProperty( "line.separator" ), 5 ) );
    }
}
