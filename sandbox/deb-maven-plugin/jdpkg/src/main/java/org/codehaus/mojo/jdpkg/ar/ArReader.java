package org.codehaus.mojo.deb.jdpkg.ar;

import static org.codehaus.mojo.deb.jdpkg.ar.ArUtil.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:trygve.laugstol@arktekk.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
class ArReader implements CloseableIterable<ReadableArFile> {
    private InputStream is;

    public ArReader(File file) throws IOException {
        is = new FileInputStream(file);

        String magic = new String(readBytes(is, 8), US_ASCII);

        if (!magic.equals(AR_ARCHIVE_MAGIC)) {
            throw new InvalidArchiveMagicException();
        }
    }

    public Iterator<ReadableArFile> iterator() {
        return new ArFileIterator();
    }

    public void close() {
        closeSilent(is);
    }

    public ReadableArFile readFile() throws IOException {
        byte[] bytes = readBytes(is, 60);

        if (bytes == null) {
            return null;
        }

        ReadableArFile arFile = new ReadableArFile(is);
        arFile.name = convertString(bytes, 0, 16);
        arFile.lastModified = Long.parseLong(convertString(bytes, 16, 12));
        arFile.ownerId = Integer.parseInt(convertString(bytes, 28, 6));
        arFile.groupId = Integer.parseInt(convertString(bytes, 34, 6));
        arFile.mode = Integer.parseInt(convertString(bytes, 40, 8), 8);
        arFile.size = Long.parseLong(convertString(bytes, 48, 10));

        String fileMagic = convertString(bytes, 58, 2);

        if (!fileMagic.equals(AR_FILE_MAGIC)) {
            throw new InvalidFileMagicException();
        }

        return arFile;
    }

    public class ArFileIterator implements Iterator<ReadableArFile> {

        private boolean used;
        private boolean atEnd;
        private ReadableArFile file;

        public boolean hasNext() {
            updateNext();
            return file != null;
        }

        public ReadableArFile next() {
            updateNext();

            if (file == null) {
                throw new NoSuchElementException();
            }

            used = true;

            return file;
        }

        private void updateNext() {
            try {
                if (used) {
                    file.close();
                    file = null;
                    used = false;
                }

                // There already is an element ready
                if (file != null) {
                    return;
                }

                // If we're at the end, don't call readFile() anymore as that will throw an IOException
                if (atEnd) {
                    return;
                }

                file = readFile();
                atEnd = file == null;

                if (atEnd) {
                    close();
                }
            }
            catch (IOException ex) {
                // ignore
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("Not implemented.");
        }
    }
}
