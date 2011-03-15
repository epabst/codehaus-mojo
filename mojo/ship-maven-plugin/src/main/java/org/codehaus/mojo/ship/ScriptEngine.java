package org.codehaus.mojo.ship;

import java.util.Map;

/**
 * Abstract script engine.
 */
public interface ScriptEngine {

    String getExtension();

    /**
     * Evaluates the specified script.
     *
     * @param script          the script to evaluate.
     * @param globalVariables a map of global variables keyed by variable name.
     * @return The result of evaluating the script.
     */
    Object eval(String script, Map globalVariables) throws ScriptException;
}
