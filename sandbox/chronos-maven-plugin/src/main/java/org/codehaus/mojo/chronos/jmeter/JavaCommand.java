/*
 * Copyright (C) 2008 Digital Sundhed (SDSD)
 *
 * All source code and information supplied as part of chronos
 * is copyright to its contributers.
 *
 * The source code has been released under a dual license - meaning you can
 * use either licensed version of the library with your code.
 *
 * It is released under the Common Public License 1.0, a copy of which can
 * be found at the link below.
 * http://www.opensource.org/licenses/cpl.php
 *
 * It is released under the LGPL (GNU Lesser General Public License), either
 * version 2.1 of the License, or (at your option) any later version. A copy
 * of which can be found at the link below.
 * http://www.gnu.org/copyleft/lesser.html
 */
package org.codehaus.mojo.chronos.jmeter;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * command line helper for exec'in external java proces.
 * 
 * @author ksr@lakeside.dk
 */
public final class JavaCommand {
    private Commandline commandLine;

    private Log log;

    public JavaCommand(String workingDir, Log log) {
        this.log = log;
        this.commandLine = new Commandline();
        commandLine.setExecutable("java");
        commandLine.setWorkingDirectory(workingDir);
    }

    void addSystemProperty(String name, String value) {
        addArgument("-D" + name + "=" + value);
    }

    void addJvmOption(String name, String value) {
        addArgument("-X" + name + "=" + value);
    }

    void addExtraJvmOption(String name, String value) {
        addArgument("-XX" + name + "=" + value);
    }

    void addArgument(String arg) {
        commandLine.createArgument().setValue(arg);
    }

    int execute() throws CommandLineException {
        StreamConsumer consumer = new StreamConsumer() {
            public void consumeLine(String line) {
                log.info(line);
            }
        };
        return CommandLineUtils.executeCommandLine(commandLine, consumer, consumer);
    }

    public String toString() {
        return commandLine.toString();
    }

}
