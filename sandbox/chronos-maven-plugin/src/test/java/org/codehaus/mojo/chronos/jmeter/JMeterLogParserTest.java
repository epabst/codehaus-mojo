/*
 * The MIT License
 * 
 * Copyright (c) 2004, The Codehaus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.codehaus.mojo.chronos.jmeter;

import java.util.Iterator;

import junit.framework.TestCase;

import org.codehaus.mojo.chronos.responsetime.GroupedResponsetimeSamples;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSampleGroup;

/**
 * @author ksr@lakeside.dk
 */
public class JMeterLogParserTest extends TestCase {

    /**
     * Checks if the all samples from log is being parsed
     */
    public void testParseJMeterLog() throws Exception {
        GroupedResponsetimeSamples samples = JMeterLogParser.parseJMeterLog("src/test/resources/test1-junitsamples.jtl");
        assertEquals(6, samples.getSampleGroups().size());
        for (Iterator it = samples.getSampleGroups().iterator(); it.hasNext();) {
            ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it.next();
            assertEquals(3, sampleGroup.size());
        }
    }

    public void testParseJmeter23WebLog() throws Exception {
        GroupedResponsetimeSamples samples = JMeterLogParser.parseJMeterLog("src/test/resources/webtest-jmeter22-resulttable.jtl");
        assertEquals(2, samples.getSampleGroups().size());
        for (Iterator it = samples.getSampleGroups().iterator(); it.hasNext();) {
            ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it.next();
            assertEquals(150, sampleGroup.size());
        }
    }

    public void testJtl20Combined() throws Exception {
        GroupedResponsetimeSamples samples = JMeterLogParser.parseJMeterLog("src/test/resources/combinedtest-jtl20-summaryreport.jtl");
        assertEquals(4, samples.getSampleGroups().size());
        for (Iterator it = samples.getSampleGroups().iterator(); it.hasNext();) {
            ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it.next();
            assertEquals(150, sampleGroup.size());
        }
    }

    public void testJtl21Combined() throws Exception {
        GroupedResponsetimeSamples samples = JMeterLogParser.parseJMeterLog("src/test/resources/combinedtest-jtl21-summaryreport.jtl");
        assertEquals(4, samples.getSampleGroups().size());
        for (Iterator it = samples.getSampleGroups().iterator(); it.hasNext();) {
            ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it.next();
            assertEquals(150, sampleGroup.size());
        }
    }

    public void testJtl22Combined2() throws Exception {
        GroupedResponsetimeSamples samples = JMeterLogParser.parseJMeterLog("src/test/resources/combinedtest-jtl22-summaryreport.jtl");
        assertEquals(4, samples.getSampleGroups().size());
        for (Iterator it = samples.getSampleGroups().iterator(); it.hasNext();) {
            ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it.next();
            assertEquals(150, sampleGroup.size());
        }
    }

    /**
     * Nested httpSample elements breaks jmeter performance reporting.<br />
     * See <a href="http://jira.codehaus.org/browse/MOJO-1343 JiraTask">JIRA</a> for more information.
     */
    public void testJtlNestedHttpSample() throws Exception {
        GroupedResponsetimeSamples samples = JMeterLogParser.parseJMeterLog("src/test/resources/jmeter-nested-httpsample.jtl");
        assertEquals(4, samples.getSampleGroups().size());
        for (Iterator it = samples.getSampleGroups().iterator(); it.hasNext();) {
            ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it.next();
            assertEquals(1, sampleGroup.size());
        }
    }

}
