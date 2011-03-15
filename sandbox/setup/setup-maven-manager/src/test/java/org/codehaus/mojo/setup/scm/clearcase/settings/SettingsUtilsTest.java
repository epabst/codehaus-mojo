package org.codehaus.mojo.setup.scm.clearcase.settings;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.maven.scm.providers.clearcase.settings.Settings;

public class SettingsUtilsTest
    extends TestCase
{
    /**
     * simple check to see if we're still matching all options
     * if one is added and one removed, we'd get a compile error, so checking the numbers should be enough
     */
    public void testCountMethods() {
        int isMethods = 0;
        int getMethods = 0;
        int setMethods = 0;
        for(Method method : Settings.class.getDeclaredMethods()) {
            if (Pattern.matches( "^is[A-Z].*", method.getName() )) {
                isMethods++;
            }
            if (Pattern.matches( "^get[A-Z].*", method.getName() )) {
                getMethods++;
            }
            if (Pattern.matches( "^set[A-Z].*", method.getName() )) {
                setMethods++;
            }
        }
        assertEquals( 1, isMethods );
        assertEquals( 4, getMethods );
        assertEquals( 5, setMethods );
    }
}
