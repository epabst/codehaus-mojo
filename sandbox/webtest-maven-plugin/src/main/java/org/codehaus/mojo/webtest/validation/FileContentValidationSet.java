/*
 * Copyright 2007 The Apache Software Foundation.
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
package org.codehaus.mojo.webtest.validation;

import java.io.File;

/**
 * An instance of this class is used to specify a set of files,
 * which are validated against regular expression.
 */
public class FileContentValidationSet
{
    /** the human-readable message to print if the validation fails */
    private String msg;

    /** the directory to validate */
    private File dir;

    /** a list of Ant-style include patterns */
    private String[] includes;

    /** a list of Ant-style exclude patterns */
    private String[] excludes;

    /** the regexp used to check the file content */
    private String regexp;

    /**
     * A human readable description why the validation foiled
     */
    public String getMsg()
    {
        String result;

        if ( this.msg == null )
        {
            result = "The regexp '" + this.regexp + "' was found in one ore more files";
        }
        else
        {
            result = this.msg;
        }

        return result;
    }

    /**
     * Returns a directory, which is scanned for files to validate.
     */
    public File getDir()
    {
        return this.dir;
    }

    /**
     * Returns patterns of files, which are being excluded from
     * the validation set.
     */
    public String[] getExcludes()
    {
        return excludes;
    }

    /**
     * Returns patterns of files, which are being included into
     * the validation set.
     */
    public String[] getIncludes()
    {
        return includes;
    }


    /**
     * Sets a directory, which is scanned for files to validate.
     */
    public void setDir( File dir )
    {
        this.dir = dir;
    }

    /**
     * Sets patterns of files, which are being excluded from
     * the validation set.
     */
    public void setExcludes( String[] excludes )
    {
        this.excludes = excludes;
    }

    /**
     * Sets patterns of files, which are being included into
     * the validation set.
     */
    public void setIncludes( String[] includes )
    {
        this.includes = includes;
    }

    /**
     * Get a regexp to search for a set of files
     */
    public String getRegexp()
    {
        return regexp;
    }

    /**
     * Set a regexp to search for a set of files
     */
    public void setRegexp( String regexp )
    {
        this.regexp = regexp;
    }
}

