package org.codehaus.mojo.ship;

import junit.framework.TestCase;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

public class ScriptEngineManagerTest extends TestCase {

    public void testScriptEngineDetectionBeanShell() throws Exception {
        URL resource = getClass().getResource("/ReturnsArray.bsh");
        File file;
        try {
            file = new File(resource.toURI());
        } catch (URISyntaxException e) {
            file = new File(resource.getPath());
        }
        assertEquals(Arrays.asList(new String[]{"This is BeanShell"}),
                Arrays.asList((String[]) (new ScriptEngineManager().eval(file,
                        Collections.emptyMap(), null))));
    }

    public void testScriptEngineDetectionGroovy() throws Exception {
        URL resource = getClass().getResource("/ReturnsArray.groovy");
        File file;
        try {
            file = new File(resource.toURI());
        } catch (URISyntaxException e) {
            file = new File(resource.getPath());
        }
        assertEquals(Arrays.asList(new String[]{"This is groovy"}),
                (new ScriptEngineManager().eval(file,
                        Collections.emptyMap(), null)));
    }
}
