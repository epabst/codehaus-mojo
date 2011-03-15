package org.codehaus.mojo.ship;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.IOUtil;

import java.io.*;
import java.nio.charset.Charset;

/**
 * An output stream that sends its output one line at a time to
 */
public class LogOutputStream extends OutputStream {
    private final Log log;
    private final PipedOutputStream pos;
    private final PipedInputStream pis;
    private final Thread logThread;

    public LogOutputStream(Log destination, boolean error) throws IOException {
        this.log = destination;
        pos = new PipedOutputStream();
        pis = new PipedInputStream(pos);
        logThread = new Thread(new LogRunnable(error));
        logThread.setDaemon(true);
        logThread.start();
    }

    public void write(int b) throws IOException {
        pos.write(b);
    }

    public void close() throws IOException {
        pos.close();
        try {
            logThread.join();
        } catch (InterruptedException e) {
            // ignore
        }
        super.close();
    }

    private class LogRunnable implements Runnable {
        private final Reader reader;
        private final BufferedReader bufferedReader;
        private boolean error;

        private LogRunnable(Reader reader, boolean error) {
            this.reader = reader;
            bufferedReader = new BufferedReader(reader);
            this.error = error;
        }

        public LogRunnable(Charset cs, boolean error) {
            this(new InputStreamReader(pis, cs), error);
        }

        public LogRunnable(boolean error) {
            this(new InputStreamReader(pis), error);
        }

        public void run() {
            try {
                String line;
                while (null != (line = bufferedReader.readLine())) {
                    if (error) {
                        LogOutputStream.this.log.error(line);
                    } else {
                        LogOutputStream.this.log.info(line);
                    }
                }
            } catch (IOException e) {
                // ignore
            } finally {
                IOUtil.close(bufferedReader);
                IOUtil.close(reader);
                IOUtil.close(pis);
            }
        }
    }
}
