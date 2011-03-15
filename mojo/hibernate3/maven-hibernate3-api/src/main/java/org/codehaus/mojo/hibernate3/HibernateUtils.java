package org.codehaus.mojo.hibernate3;

/*
* Copyright 2005 Johann Reyes.
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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.hibernate.util.ReflectHelper;

import java.io.File;

/**
 * Utility class.
 *
 * @author <a href="mailto:jreyes@hiberforum.org">Johann Reyes</a>
 * @version $Id$
 */
public final class HibernateUtils {
    /**
     * Returns "1.5" if the java.version system property starts with 1.5 or 1.6. Otherwise returns "1.4".
     *
     * @return String
     */
    public static String getJavaVersion() {
        String version = System.getProperty("java.version");
        return (version.startsWith("1.5") || version.startsWith("1.6")) ? "jdk15" : "jdk14";
    }

    /**
     * Returns an instance of a class or if not found a default one.
     *
     * @param className        class to look for
     * @param defaultClassName default class to return
     * @return Object
     */
    public static Object getClass(String className, String defaultClassName) {
        Object o = getClass(className);
        if (o == null) {
            o = getClass(defaultClassName);
        }
        return o;
    }

    /**
     * Returns an instance of a class or else return a null.
     *
     * @param className class to return
     * @return Object
     */
    public static Object getClass(String className) {
        try {
            return ReflectHelper.classForName(className).newInstance();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Object getClass(String className, Class caller) {
        try {
            return ReflectHelper.classForName(className, caller).newInstance();
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns a File object if the file exists and is a file or a null if it doesn't meet the criteria.
     *
     * @param path     parent path
     * @param filePath file path
     * @return File object if is a valid file or null if it isn't
     */
    public static File getFile(File path, String filePath) {
        File file = new File(path, filePath);
        if (file.exists() && file.isFile()) {
            return file;
        } else {
            return null;
        }
    }

    /**
     * Returns a path of *only* of a file that exists.
     *
     * @param path     parent path
     * @param filePath file path
     * @return Path to the file
     */
    public static String getPath(File path, String filePath) {
        File file = getFile(path, filePath);
        if (file == null)
            return null;
        else
            return file.getPath();
    }

    /**
     * Checks if the directory is in fact a directory path and creates it if it is necessary.
     *
     * @param parent        parent file
     * @param directoryPath child directory path
     * @param parameter     name of the parameter calling this method
     * @return the directory as a File object
     * @throws MojoExecutionException if the directory is not a directory
     */
    public static File prepareDirectory(File parent, String directoryPath, String parameter)
            throws MojoExecutionException {
        return prepareDirectory(new File(parent, directoryPath), parameter);
    }

    /**
     * Checks if the directory is in fact a directory path and creates it if it is necessary.
     *
     * @param directory child directory
     * @param parameter name of the parameter calling this method
     * @return the directory as a File object
     * @throws MojoExecutionException if the directory is not a directory
     */
    public static File prepareDirectory(File directory, String parameter)
            throws MojoExecutionException {
        if (!directory.exists()) {
            FileUtils.mkdir(directory.getPath());
        } else if (!directory.isDirectory()) {
            throw new MojoExecutionException("<" + parameter + "> is not a directory.");
        }
        return directory;
    }

    /**
     * Checks if the file is a file path and creates the directories needed to create the file if it is necessary.
     *
     * @param parent    parent file
     * @param filePath  child file path
     * @param parameter name of the parameter calling this method
     * @return the file as a File object
     * @throws MojoExecutionException if the file is not a file
     */
    public static File prepareFile(File parent, String filePath, String parameter)
            throws MojoExecutionException {
        File file = new File(parent, filePath);
        if (!file.exists()) {
            FileUtils.mkdir(FileUtils.getPath(file.getPath()));
        } else if (!file.isFile()) {
            throw new MojoExecutionException("<" + parameter + "> is not a file.");
        }
        return file;
    }
}
