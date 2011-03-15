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
package org.codehaus.mojo.chronos.gc;

import java.io.IOException;

import junit.framework.TestCase;

import org.jfree.data.time.TimeSeries;

public class GCLogParserTest extends TestCase {
    /**
     * Checks if java 1.5 gc sample log can be parsed
     * 
     * @throws IOException
     */
    public void testParseGCLog_Jdk15() throws IOException {
        GCSamples samples = new GCLogParser().parseGCLog("target/test-classes/test1-gc.txt");
        assertEquals(596, samples.getSampleCount());

        TimeSeries before = new TimeSeries("");
        samples.extractHeapBefore(before);
        TimeSeries after = new TimeSeries("");
        samples.extractHeapAfter(after);
        TimeSeries total = new TimeSeries("");
        samples.extractHeapTotal(total);
        TimeSeries processing = new TimeSeries("");
        samples.extractProcessingTime(processing);

        // 1.636: [Full GC 18909K->917K(249088K), 0.0770344 secs]
        assertEquals(1.636, samples.getTimeStampForSampleAt(0), 0.01);
        assertEquals(18909, before.getDataItem(0).getValue().longValue());
        assertEquals(917, after.getDataItem(0).getValue().longValue());
        assertEquals(249088, total.getDataItem(0).getValue().longValue());
        assertEquals(0.0770344, processing.getDataItem(0).getValue().doubleValue(), 0.01);

        // 4.141: [GC Desired survivor size 6684672 bytes, new threshold 2 (max 2) - age 1:
        // 2291816 bytes, 2291816 total 105877K->3155K(249088K), 0.0192226 secs]
        assertEquals(4.141, samples.getTimeStampForSampleAt(1), 0.01);
        assertEquals(105877, before.getDataItem(1).getValue().longValue());
        assertEquals(3155, after.getDataItem(1).getValue().longValue());
        assertEquals(249088, total.getDataItem(1).getValue().longValue());
        assertEquals(0.0192226, processing.getDataItem(1).getValue().doubleValue(), 0.01);

        long start = 1157615684828l - 16;
        long end = 1157615687171l;
        long totalTime = end - start;
        assertEquals(0.0, samples.getGarbageCollectionRatio(totalTime), 0.001d);
        assertEquals(26.48, samples.getCollectedKBPerSecond(totalTime), 0.01d);
    }

    /**
     * Checks if java 1.4 gc sample log can be parsed
     * 
     * @throws IOException
     */
    public void testParseGCLog_Jdk14() throws IOException {
        GCSamples samples = new GCLogParser().parseGCLog("target/test-classes/test2-gc.txt");
        assertEquals(63, samples.getSampleCount());

        TimeSeries before = new TimeSeries("");
        samples.extractHeapBefore(before);
        TimeSeries after = new TimeSeries("");
        samples.extractHeapAfter(after);
        TimeSeries total = new TimeSeries("");
        samples.extractHeapTotal(total);
        TimeSeries processing = new TimeSeries("");
        samples.extractProcessingTime(processing);

        // 0.000: [Full GC 24263K->834K(249088K), 0.0499446 secs]
        assertEquals(0.000, samples.getTimeStampForSampleAt(0), 0.01);
        assertEquals(24263, before.getDataItem(0).getValue().longValue());
        assertEquals(834, after.getDataItem(0).getValue().longValue());
        assertEquals(249088, total.getDataItem(0).getValue().longValue());
        assertEquals(0.0499446, processing.getDataItem(0).getValue().doubleValue(), 0.01);

        // 3.040: [GC 105794K->4765K(249088K), 0.0415528 secs]
        assertEquals(3.040, samples.getTimeStampForSampleAt(1), 0.01);
        assertEquals(105794, before.getDataItem(1).getValue().longValue());
        assertEquals(4765, after.getDataItem(1).getValue().longValue());
        assertEquals(249088, total.getDataItem(1).getValue().longValue());
        assertEquals(0.0415528, processing.getDataItem(1).getValue().doubleValue(), 0.01);

        long start = 1157615684828l - 16;
        long end = 1157615695281l;
        long totalTime = end - start;
        assertEquals(2.9565e-5, samples.getGarbageCollectionRatio(totalTime), 0.00001d);
        assertEquals(0.62, samples.getCollectedKBPerSecond(totalTime), 0.01d);
    }

}
