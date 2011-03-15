package org.apache.maven.plugin.jcoverage.report;

/* ====================================================================
 *   Copyright 2001-2004 The Apache Software Foundation.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * ====================================================================
 */

import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class CoverageUnmarshaller
{
    public Coverage parse(Reader reader) throws Exception
    {
        Coverage coverage = new Coverage();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(reader);

        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if (eventType == XmlPullParser.START_TAG)
            {
                if (parser.getName().equals("coverage"))
                {
                    coverage.setSrcDirectory(parser.getAttributeValue("", "src"));
                }
                if (parser.getName().equals("class"))
                {
                    Clazz theClass = new Clazz(parser.getAttributeValue("", "name"));
                    while (parser.nextTag() == XmlPullParser.START_TAG)
                    {
                        if (parser.getName().equals("file"))
                        {
                            String fileName = parser.getAttributeValue("", "name");
                            theClass.setFile(fileName);
                        }
                        else if (parser.getName().equals("line")
                                 && parser.getAttributeCount() == 1)
                        {
                            theClass.setLineRate(
                                parser.getAttributeValue("", "rate"));
                        }
                        else if (parser.getName().equals("line")
                                 && parser.getAttributeCount() == 2)
                        {
                            Line line = new Line();
                            line.setNumLine(
                                Integer.valueOf(
                                    parser.getAttributeValue("", "number")
                                ).intValue());
                            line.setNbHits(
                                Integer.valueOf(
                                    parser.getAttributeValue("", "hits")
                                ).intValue());
                            theClass.addLine(line);
                        }
                        else if (parser.getName().equals("branch"))
                        {
                            theClass.setBranchRate(
                                parser.getAttributeValue("", "rate"));
                        }
                        if (parser.getName().equals("methods"))
                        {
                            while (parser.nextTag() == XmlPullParser.START_TAG)
                            {
                                if (parser.getName().equals("method"))
                                {
                                    while (parser.nextTag() == XmlPullParser.START_TAG)
                                    {
                                        if (parser.getName().equals("line"))
                                        {
                                            //nothing
                                        }
                                        else if (parser.getName().equals("branch"))
                                        {
                                            //nothing
                                        }
                                        parser.next();
                                    }
                                }
                                parser.next();
                            }
                        }
                        else
                        {
                            parser.next();
                        }
                    }
                    coverage.addClass(theClass);
                }
            }

            eventType = parser.next();
        }

        return coverage;
    }
}
