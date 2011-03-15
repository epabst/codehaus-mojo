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
package org.codehaus.mojo.chronos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

import org.codehaus.mojo.chronos.gc.GCSamples;
import org.jfree.data.time.Millisecond;

/**
 * Utility class primarily for handling files.
 * 
 */
public class Utils {

    private static final int IGNORED_YEAR = 1970;

    public static Millisecond createMS(long millisecond) {
        return new Millisecond((int)millisecond, 0, 0, 0, 1, 1, IGNORED_YEAR);
    }

    public static final ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle("chronos", locale, Utils.class.getClassLoader());
    }

    public static File getChronosDir(File baseDir) {
        File target = new File(baseDir, "target");
        File chronos = new File(target, "chronos");
        ensureParentDir(chronos);
        if(!chronos.exists()) {
            chronos.mkdir();
        }
        return chronos;
    }

    public static File getGcSamplesSer(File baseDir, String id) {
        File chronosDir = getChronosDir(baseDir);
        return new File(chronosDir, "gc-" + id + ".ser");
    }

    public static File getPerformanceSamplesSer(File baseDir, String id) {
        File chronosDir = getChronosDir(baseDir);
        return new File(chronosDir, "perf-" + id + ".ser");
    }

    public static void writeObject(Serializable samples, File outputFile) throws IOException {
        ensureParentDir(outputFile);
        // outputFile.createNewFile();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFile));
        try {
            out.writeObject(samples);
        } finally {
            out.close();
        }
    }

    public static Serializable readObject(File ser) throws IOException {
        ObjectInputStream input = new ObjectInputStream(new FileInputStream(ser));
        try {
            return (Serializable)input.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            input.close();
        }
    }

    private static void ensureParentDir(File file) {
        if(file.getParentFile().exists()) {
            return;
        }
        ensureParentDir(file.getParentFile());
        file.getParentFile().mkdir();
    }

    public static GCSamples readGCSamples(File baseDir, String dataId) throws IOException {
        File gcSer = Utils.getGcSamplesSer(baseDir, dataId);
        if(!gcSer.exists()) {
            return null;
        }
        return (GCSamples)Utils.readObject(gcSer);
    }

}