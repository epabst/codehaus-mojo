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

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.codehaus.mojo.chronos.responsetime.GroupedResponsetimeSamples;
import org.xml.sax.SAXException;

/**
 * Responsible for parsing the jmeter log.
 * 
 * @author ksr@lakeside.dk
 */
public final class JMeterLogParser {
    private final SAXParser saxParser;

    public JMeterLogParser() {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            saxParser = saxParserFactory.newSAXParser();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public static GroupedResponsetimeSamples parseJMeterLog(String jtlFilePath) throws Exception {
        File file = new File(jtlFilePath);
        return new JMeterLogParser().parseJMeterLog(file);
    }

    /**
     * Parse the jmeter (jtl) log.
     * 
     * @param file
     *            The file to parse
     * @return The {@link GroupedResponsetimeSamples} obtained by parsing the log
     * @throws SAXException
     *             If there is some XML related error in the logfile
     * @throws IOException
     *             If the JMeter logfile cannot be read
     */
    public GroupedResponsetimeSamples parseJMeterLog(File file) throws SAXException, IOException {
        JMeterSAXFileHandler saxHandler = new JMeterSAXFileHandler();
        saxParser.parse(file, saxHandler);
        return saxHandler.getJMeterSamples();
    }

}
