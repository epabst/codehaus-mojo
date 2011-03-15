package org.codehaus.mojo.chronos.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadHelper {

    private static final int BUFFER = 2048;

    public static void downloadJMeter(String fileLoc, String destination) throws IOException {
        URL u = new URL(fileLoc);
        URLConnection uc = u.openConnection();
        int contentLength = uc.getContentLength();

        InputStream is = new BufferedInputStream(uc.getInputStream());
        byte[] inData = new byte[contentLength];
        int bytesRead = 0;
        int offset = 0;
        while (offset < contentLength) {
            bytesRead = is.read(inData, offset, inData.length - offset);
            if(bytesRead == -1)
                break;
            offset += bytesRead;
        }
        is.close();

        if(offset != contentLength) {
            throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
        }

        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(inData)));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            OutputStream fos = new BufferedOutputStream(new FileOutputStream(new File(destination, entry.getName())),
                    BUFFER);

            int length;
            byte[] outData = new byte[BUFFER];
            while ((length = zis.read(outData, 0, BUFFER)) != -1) {
                fos.write(outData, 0, length);
            }
            fos.flush();
            fos.close();
        }
        zis.close();
    }
}