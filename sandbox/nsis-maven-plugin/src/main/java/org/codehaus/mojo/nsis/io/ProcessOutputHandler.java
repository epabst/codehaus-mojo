/*
 * Copyright 2008 Codehaus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.mojo.nsis.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.codehaus.plexus.util.IOUtil;

/**
 * Variation on the StreamPumper theme.
 * 
 * @author <a href="mailto:joakime@apache.org">Joakim Erdfelt</a>
 * @version $Id$
 */
public class ProcessOutputHandler implements Runnable {
    private static final int SIZE = 1024;

    /**
     * The flag indicating if the handler is done or not.
     * Can be set true to force handler to be done.
     */
    private boolean done;

    private BufferedReader in;

    private ProcessOutputConsumer consumer = null;

    private PrintWriter out = null;

    public ProcessOutputHandler(InputStream in) {
        this.in = new BufferedReader(new InputStreamReader(in), SIZE);
    }

    public ProcessOutputHandler(InputStream in, PrintWriter writer) {
        this(in);

        out = writer;
    }

    public ProcessOutputHandler(InputStream in, PrintWriter writer,
            ProcessOutputConsumer consumer) {
        this(in);
        this.out = writer;
        this.consumer = consumer;
    }

    public ProcessOutputHandler(InputStream in, ProcessOutputConsumer consumer) {
        this(in);

        this.consumer = consumer;
    }

    public void close() {
        IOUtil.close(out);
    }

    public void flush() {
        if (out != null) {
            out.flush();
        }
    }

    public boolean isDone() {
        return done;
    }

    public void run() {
        try {
            String s = in.readLine();

            while (s != null) {
                consumeLine(s);

                if (out != null) {
                    out.println(s);

                    out.flush();
                }

                s = in.readLine();
            }
        } catch (IOException e) {
            // Catch IOException blindly.
        } finally {
            IOUtil.close(in);

            done = true;

            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    public void startThread() {
        Thread thread = new Thread(this, "ProcessOutputHandler");
        thread.start();
    }

    private void consumeLine(String line) {
        if (consumer != null) {
            consumer.consumeOutputLine(line);
        }
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
