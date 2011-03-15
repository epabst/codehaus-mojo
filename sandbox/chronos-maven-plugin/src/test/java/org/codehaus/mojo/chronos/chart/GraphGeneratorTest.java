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
package org.codehaus.mojo.chronos.chart;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.apache.maven.reporting.MavenReportException;
import org.codehaus.mojo.chronos.ReportConfigStub;
import org.codehaus.mojo.chronos.Utils;
import org.codehaus.mojo.chronos.jmeter.JMeterLogParser;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples;

/**
 * Testclass for {@link GraphGenerator}
 * 
 * @author ksr@lakeside.dk
 */
public class GraphGeneratorTest extends TestCase {

    ResourceBundle bundle;
    private ChartRendererStub renderer;

    protected void setUp() throws Exception {
        bundle = Utils.getBundle(Locale.getDefault());
        renderer = new ChartRendererStub();
    }

    /**
     * Tests if charts can be generated without errors.
     * 
     * @throws Exception
     * @throws MavenReportException
     */
    public void testDoGenerateReport() throws Exception {
        ResponsetimeSamples jmeterSamples = JMeterLogParser.parseJMeterLog("src/test/resources/test2-junitsamples.jtl");
        List plugins = Collections.singletonList(new ChronosHistogramPlugin(jmeterSamples));
        GraphGenerator gen = new GraphGenerator(plugins);
        gen.generateGraphs(renderer, bundle, new ReportConfigStub());
        assertEquals(6, renderer.charts.size());
    }
}
