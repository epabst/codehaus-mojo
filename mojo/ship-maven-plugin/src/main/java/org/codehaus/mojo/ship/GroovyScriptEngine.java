package org.codehaus.mojo.ship;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.util.Map;

/**
 * Groovy script engine.
 */
public class GroovyScriptEngine
        implements ScriptEngine {

    public String getExtension() {
        return "groovy";
    }

    /**
     * {@inheritDoc}
     */
    public Object eval(String script, Map globalVariables)
            throws ScriptException {
        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT);

        Binding binding = new Binding(globalVariables);

        GroovyShell interpreter = new GroovyShell(null, binding, config);

        try {
            return interpreter.evaluate(script);
        } catch (ThreadDeath e) {
            throw e;
        } catch (Throwable e) {
            throw new ScriptException(e);
        }
    }

}
