package org.codehaus.mojo.mockrepo.utils;

/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import java.util.HashMap;
import java.util.Map;

/**
 * Contains the MIME types for common file types
 *
 * @author connollys
 * @since Sep 1, 2009 4:15:23 PM
 */
public final class MimeRegistry
{
    /**
     * mapping of file extensions to repository-types
     */
    private static final Map map = new HashMap();

    static
    {
        map.put( ".ai", "application/postscript" );
        map.put( ".aif", "audio/x-aiff" );
        map.put( ".aifc", "audio/x-aiff" );
        map.put( ".aiff", "audio/x-aiff" );
        map.put( ".asc", "text/plain" );
        map.put( ".au", "audio/basic" );
        map.put( ".avi", "video/x-msvideo" );
        map.put( ".bin", "application/octet-stream" );
        map.put( ".c", "text/plain" );
        map.put( ".cc", "text/plain" );
        map.put( ".class", "application/octet-stream" );
        map.put( ".cpio", "application/x-cpio" );
        map.put( ".csh", "application/x-csh" );
        map.put( ".css", "text/css" );
        map.put( ".dms", "application/octet-stream" );
        map.put( ".doc", "application/msword" );
        map.put( ".dvi", "application/x-dvi" );
        map.put( ".eps", "application/postscript" );
        map.put( ".exe", "application/octet-stream" );
        map.put( ".f", "text/plain" );
        map.put( ".f90", "text/plain" );
        map.put( ".gif", "image/gif" );
        map.put( ".gtar", "application/x-gtar" );
        map.put( ".gz", "application/x-gzip" );
        map.put( ".h", "text/plain" );
        map.put( ".hh", "text/plain" );
        map.put( ".hqx", "application/mac-binhex40" );
        map.put( ".htm", "text/html" );
        map.put( ".html", "text/html" );
        map.put( ".jpe", "image/jpeg" );
        map.put( ".jpeg", "image/jpeg" );
        map.put( ".jpg", "image/jpeg" );
        map.put( ".js", "application/x-javascript" );
        map.put( ".latex", "application/x-latex" );
        map.put( ".lha", "application/octet-stream" );
        map.put( ".lzh", "application/octet-stream" );
        map.put( ".m", "text/plain" );
        map.put( ".man", "application/x-troff-man" );
        map.put( ".me", "application/x-troff-me" );
        map.put( ".mid", "audio/midi" );
        map.put( ".midi", "audio/midi" );
        map.put( ".mif", "application/vnd.mif" );
        map.put( ".mov", "video/quicktime" );
        map.put( ".mp2", "audio/mpeg" );
        map.put( ".mp3", "audio/mpeg" );
        map.put( ".mpe", "video/mpeg" );
        map.put( ".mpeg", "video/mpeg" );
        map.put( ".mpg", "video/mpeg" );
        map.put( ".mpga", "audio/mpeg" );
        map.put( ".oda", "application/oda" );
        map.put( ".pbm", "image/x-portable-bitmap" );
        map.put( ".pdf", "application/pdf" );
        map.put( ".pgm", "image/x-portable-graymap" );
        map.put( ".png", "image/png" );
        map.put( ".pnm", "image/x-portable-anymap" );
        map.put( ".pot", "application/mspowerpoint" );
        map.put( ".ppm", "image/x-portable-pixmap" );
        map.put( ".pps", "application/mspowerpoint" );
        map.put( ".ppt", "application/mspowerpoint" );
        map.put( ".ppz", "application/mspowerpoint" );
        map.put( ".ps", "application/postscript" );
        map.put( ".qt", "video/quicktime" );
        map.put( ".ra", "audio/x-realaudio" );
        map.put( ".ram", "audio/x-pn-realaudiio" );
        map.put( ".rgb", "image/x-rgb" );
        map.put( ".rm", "audio/x-pn-realaudio" );
        map.put( ".roff", "application/x-troff" );
        map.put( ".rtf", "text/rtf" );
        map.put( ".rtx", "text/richtext" );
        map.put( ".sgm", "text/sgml" );
        map.put( ".sgml", "text/sgml" );
        map.put( ".sh", "application/x-sh" );
        map.put( ".shar", "application/x-shar" );
        map.put( ".silo", "model/mesh" );
        map.put( ".sit", "application/x-stuffit" );
        map.put( ".smi", "application/smil" );
        map.put( ".smil", "application/smil" );
        map.put( ".snd", "audio/basic" );
        map.put( ".swf", "application/x-shockwave-flash" );
        map.put( ".t", "application/x-troff" );
        map.put( ".tar", "application/x-tar" );
        map.put( ".tcl", "application/x-tcl" );
        map.put( ".tex", "application/x-tex" );
        map.put( ".texi", "application/x-texinfo" );
        map.put( ".texinfo", "application/x-texinfo" );
        map.put( ".tif", "image/tiff" );
        map.put( ".tiff", "image/tiff" );
        map.put( ".tr", "application/x-troff" );
        map.put( ".tsv", "text/tab-separated-values" );
        map.put( ".txt", "text/plain" );
        map.put( ".wav", "audio/x-wav" );
        map.put( ".xbm", "image/x-xbitmap" );
        map.put( ".xlc", "application/vnd.ms-excel" );
        map.put( ".xll", "application/vnd.ms-excel" );
        map.put( ".xlm", "application/vnd.ms-excel" );
        map.put( ".xls", "application/vnd.ms-excel" );
        map.put( ".xlw", "application/vnd.ms-excel" );
        map.put( ".xml", "text/xml" );
        map.put( ".xpm", "image/x-xpixmap" );
        map.put( ".xwd", "image/x-xwindowdump" );
        map.put( ".xyz", "chemical/x-pdb" );
        map.put( ".zip", "application/zip" );

        map.put( ".asc", "text/plain" );
        map.put( ".jar", "application/octet-stream" );
        map.put( ".ear", "application/octet-stream" );
        map.put( ".exe", "application/octet-stream" );
        map.put( ".htm", "text/html" );
        map.put( ".html", "text/html" );
        map.put( ".java", "text/plain" );
        map.put( ".md5", "text/plain" );
        map.put( ".par", "application/octet-stream" );
        map.put( ".pom", "text/xml" );
        map.put( ".rar", "application/octet-stream" );
        map.put( ".sar", "application/octet-stream" );
        map.put( ".sha1", "text/plain" );
        map.put( ".tar", "application/x-tar" );
        map.put( ".war", "application/octet-stream" );
        map.put( ".xml", "text/xml" );
        map.put( ".zip", "application/zip" );
    }

    private MimeRegistry()
    {
        throw new IllegalAccessError( "Utility class" );
    }

    public static String lookup( String extension )
    {
        return map.containsKey( extension ) ? (String) map.get( extension ) : "application/octet-stream";
    }

}
