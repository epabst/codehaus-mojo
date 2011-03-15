package org.codehaus.mojo.ship;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: stephenc
 * Date: Nov 24, 2010
 * Time: 3:54:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScriptEngineManager {
    private final Set engines;

    public ScriptEngineManager() {
        engines = new LinkedHashSet();
        engines.add(new BeanShellScriptEngine());
        engines.add(new GroovyScriptEngine());
    }

    public Object eval(File script, Map globalVariables, Log log) throws MojoExecutionException {
        ScriptEngine engine = null;
        Iterator i = engines.iterator();
        while (i.hasNext()) {
            ScriptEngine e = (ScriptEngine) i.next();
            if (script.getName().endsWith("." + e.getExtension())) {
                engine = e;
                break;
            }
        }
        if (engine == null) {
            throw new MojoExecutionException("Could not find a script engine to execute " + script);
        }
        try {
            return engine.eval(FileUtils.fileRead(script), globalVariables);
        } catch (ScriptException e) {
            throw new MojoExecutionException("Ship script failed", e);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }

}
