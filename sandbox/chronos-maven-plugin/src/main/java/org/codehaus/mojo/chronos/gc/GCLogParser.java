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
package org.codehaus.mojo.chronos.gc;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for parsing garbage collection logs.
 * 
 * @author ksr@lakeside.dk
 */
public final class GCLogParser {

    /**
     * Parses the garbage collection log.
     * 
     * @param gcFilePath
     *            The path to the garbage collection log
     * @return {@link GCSamples}
     * @throws IOException
     *             if the logfile could not be parsed
     */
    public GCSamples parseGCLog(String gcFilePath) throws IOException {
        File gcFile = new File(gcFilePath);
        return parseGCLog(gcFile);
    }

    /**
     * Parses the garbage collection log.
     * 
     * @param gcFile
     *            The garbage collection logfile
     * @return {@link GCSamples}
     * @throws IOException
     *             if the logfile could not be parsed
     */
    public GCSamples parseGCLog(File gcFile) throws IOException {
        FileReader fileReader = new FileReader(gcFile);
        GCSamples samples = new GCSamples();
        LineNumberReader reader = new LineNumberReader(fileReader);

        String line;
        StringBuffer concatLines = new StringBuffer();
        try {
            while ((line = reader.readLine()) != null) {
                concatLines.append(line);
                if(line.indexOf("]") > -1) { // end of the logentry
                    samples.add(parseGCLogItem(concatLines.toString()));
                    concatLines.setLength(0);
                }
            }
            return samples;
        } finally {
            reader.close();
        }
    }

    /**
     * Runs a regular expression on source.<br />
     * Puts the info in a {@link GCSample} and adds that to samples The log entries might be JDK1.4 or JDK5
     * 
     * @param source
     * @param samples
     * @return {@link GCSamples}
     */
    private GCSample parseGCLogItem(String source) {
        String timeinstant = null, heapbefore = null, heapafter = null, totalheap = null, processingtime = null;

        Pattern pattern = Pattern.compile("[0-9]*\\.[0-9]*:|[0-9]*K|[0-9]*\\.[0-9]*");
        Matcher matcher = pattern.matcher(source);
        int index = 0;
        while (matcher.find()) {
            if(matcher.group().length() > 0) {
                switch (index) {
                case 0:
                    timeinstant = matcher.group();
                    timeinstant = timeinstant.substring(0, timeinstant.length() - 1);
                    break;
                case 1:
                    heapbefore = matcher.group();
                    heapbefore = heapbefore.substring(0, heapbefore.length() - 1);
                    break;
                case 2:
                    heapafter = matcher.group();
                    heapafter = heapafter.substring(0, heapafter.length() - 1);
                    break;
                case 3:
                    totalheap = matcher.group();
                    totalheap = totalheap.substring(0, totalheap.length() - 1);
                    break;
                case 4:
                    processingtime = matcher.group();
                    processingtime = matcher.group();
                    break;
                default:
                    throw new IllegalArgumentException();
                }
                index++;
            }
        }

        return new GCSample(Double.parseDouble(timeinstant), Integer.parseInt(heapbefore), Integer.parseInt(heapafter),
                Integer.parseInt(totalheap), Double.parseDouble(processingtime));
    }

}
