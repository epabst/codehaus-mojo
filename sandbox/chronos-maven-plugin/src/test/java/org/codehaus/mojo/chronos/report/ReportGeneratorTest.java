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
package org.codehaus.mojo.chronos.report;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.mojo.chronos.ReportConfigStub;
import org.codehaus.mojo.chronos.Utils;
import org.codehaus.mojo.chronos.chart.ChronosHistogramPlugin;
import org.codehaus.mojo.chronos.chart.GraphGenerator;
import org.codehaus.mojo.chronos.jmeter.JMeterLogParser;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples;

/**
 * Testclass for {@link GraphGenerator}
 * 
 * @author ksr@lakeside.dk
 */
public class ReportGeneratorTest extends TestCase {

    ResourceBundle bundle;

    private ResponsetimeSamples samples;

    protected void setUp() throws Exception {
        samples = JMeterLogParser.parseJMeterLog("src/test/resources/test1-junitsamples.jtl");
        bundle = Utils.getBundle(Locale.getDefault());
    }

    /**
     * Tests if report can be generated without errors. The {@link SinkStub} stubs the {@link Sink} class.
     * 
     * @throws MavenReportException
     */
    public void testDoGenerateReport() throws MavenReportException {
        ReportConfigStub config = new ReportConfigStub();
        List plugins = Collections.singletonList(new ChronosHistogramPlugin(samples));
        ReportGenerator gen = new ReportGenerator(bundle, config, new GraphGenerator(plugins));
        gen.doGenerateReport(new SinkStub(), samples);
    }
}
