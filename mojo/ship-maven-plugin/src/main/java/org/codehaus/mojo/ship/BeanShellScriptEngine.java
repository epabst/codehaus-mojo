package org.codehaus.mojo.ship;

import bsh.Capabilities;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.TargetError;

import java.util.Iterator;
import java.util.Map;

/**
 * Bean Shell script engine.
 */
public class BeanShellScriptEngine implements ScriptEngine {
    public String getExtension() {
        return "bsh";
    }

    public Object eval(String script, Map globalVariables) throws ScriptException {
        Interpreter engine = new Interpreter();

        if (!Capabilities.haveAccessibility()) {
            try {
                Capabilities.setAccessibility(true);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (globalVariables != null) {
            for (Iterator it = globalVariables.keySet().iterator(); it.hasNext();) {
                String variable = (String) it.next();
                Object value = globalVariables.get(variable);
                try {
                    engine.set(variable, value);
                }
                catch (EvalError e) {
                    throw new RuntimeException(e);
                }
            }
        }

        try {
            return engine.eval(script);
        } catch (TargetError e) {
            throw new ScriptException(e.getTarget());
        } catch (ThreadDeath e) {
            throw e;
        } catch (Throwable e) {
            throw new ScriptException(e);
        }
    }
}
