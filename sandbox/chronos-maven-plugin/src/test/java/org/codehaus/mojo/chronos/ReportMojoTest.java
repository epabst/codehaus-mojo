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
 */package org.codehaus.mojo.chronos;

import java.io.File;

import junit.framework.TestCase;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.doxia.site.renderer.DefaultSiteRenderer;
import org.codehaus.mojo.chronos.gc.GCLogParser;
import org.codehaus.mojo.chronos.gc.GCSamples;
import org.codehaus.mojo.chronos.jmeter.JMeterLogParser;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples;

public class ReportMojoTest extends TestCase {
    public void setUp() {
    }

    private void deleteRecursive(File file) {
        if(file.isDirectory()) {
            File[] elements = file.listFiles();
            for (int i = 0; i < elements.length; i++) {
                deleteRecursive(elements[i]);
            }
        }
        file.delete();
    }

    public void testSimple() throws Exception {
        performReport("src/test/resources/test1-junitsamples.jtl", "src/test/resources/test1-gc.txt", "test1");
    }

    public void testJtl22Combined2() throws Exception {
        performReport("src/test/resources/combinedtest-jtl22-summaryreport.jtl", null, "test5");
    }

    public void testOutputName() {
        ReportMojo mojo = new ReportMojo();
        mojo.reportid = "out";
        assertEquals("out", mojo.getOutputName());
    }

    public void testGc() {
        ReportMojo mojo = new ReportMojo();
        mojo.project = newMavenProject();
        assertFalse(mojo.getConfig().isShowgc());
    }

    public void testId() {
        ReportMojo mojo = new ReportMojo();
        mojo.reportid = "xx";
        assertEquals("xx", mojo.getConfig().getId());
        mojo.reportid = "yy";
        assertEquals("yy", mojo.getConfig().getId());
        mojo.reportid = null;
        mojo.dataid = "zz";
        assertEquals("zz", mojo.getConfig().getId());
    }

    private void performReport(String jtlFile, String gcFile, final String id) throws Exception {
        long start = System.currentTimeMillis();
        File file = new File("target/chronos/" + id);
        if(file.exists()) {
            deleteRecursive(file);
        }

        ReportMojo mojo = new ReportMojo();
        mojo.dataid = id;
        mojo.title = "title";
        mojo.description = "here is my description";
        mojo.siteRenderer = new DefaultSiteRenderer();
        mojo.outputDirectory = "target/chronos/" + id;
        mojo.project = newMavenProject();
        mojo.showdetails = false;
        mojo.showhistogram = false;
        mojo.showresponse = false;

        ResponsetimeSamples rs = JMeterLogParser.parseJMeterLog(jtlFile);
        File performanceSamplesSer = Utils.getPerformanceSamplesSer(new File("."), id);
        Utils.writeObject(rs, performanceSamplesSer);
        assertTrue(performanceSamplesSer.exists());
        System.out.println(performanceSamplesSer.getPath());
        if(gcFile != null) {
            GCSamples gs = new GCLogParser().parseGCLog(gcFile);
            Utils.writeObject(gs, Utils.getGcSamplesSer(new File("."), id));
        }
        mojo.execute();
        System.out.println(System.currentTimeMillis() - start);
    }

    private MavenProject newMavenProject() {
        Model model = new Model();
        model.setName("test");
        model.setUrl("url");
        MavenProject project = new MavenProject(model);
        project.setFile(new File("pom.xml"));
        return project;
    }

}
