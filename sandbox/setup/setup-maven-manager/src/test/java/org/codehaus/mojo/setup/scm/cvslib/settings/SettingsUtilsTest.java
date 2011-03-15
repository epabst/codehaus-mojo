package org.codehaus.mojo.setup.scm.cvslib.settings;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.maven.scm.providers.cvslib.settings.Settings;

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
        assertEquals( 3, isMethods );
        assertEquals( 5, getMethods );
        assertEquals( 8, setMethods );
    }

}
