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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class CoverageReportGenerator
{
    private String dataFile;
    private String outputDir;

    public void setDataFile(String dataFile)
    {
        this.dataFile = dataFile;
    }

    public String getDataFile()
    {
        return dataFile;
    }

    public void setOutputDir(String dir)
    {
        this.outputDir = dir;
    }

    public String getOutputDir()
    {
        return outputDir;
    }

    public void execute() throws Exception
    {
        System.out.println("Generate report for " + dataFile + " file.");
        System.out.println("OutputDir = " + outputDir);
        try
        {
            prepareFile();
            FileReader fr = new FileReader(dataFile);
            CoverageUnmarshaller cum = new CoverageUnmarshaller();
            Coverage coverage = cum.parse(fr);

            CoverageReport cr = new CoverageReport(coverage);
            cr.generate(outputDir);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
     * Rewrite all lines of coverage.xml file for obtain a xml well formed
     */
    private void prepareFile() throws Exception
    {
        File fSrc = new File(dataFile);
        File fOld = new File(dataFile + ".old");
        fSrc.renameTo(fOld);
        BufferedReader br = new BufferedReader(new FileReader(dataFile + ".old"));
        PrintWriter pw = new PrintWriter(new FileOutputStream(dataFile));
        String line;
        while ((line = br.readLine()) != null)
        {
            line = replace(line, "<init>", "&lt;init&gt;", 0);
            line = replace(line, "<clinit>", "&lt;clinit&gt;", 0);
            line = replace(line, "<Unknown>", "[Unknown]", 0);
            pw.println(line);
        }
        pw.close();
        br.close();
    }

    private String replace( String line, String oldString, String newString, int startAt )
    {
        int i = startAt;
        while ((i = line.indexOf(oldString, i)) >= 0)
        {
            line = (new StringBuffer().append(line.substring(0, i))
                                      .append(newString)
                                      .append(line.substring(i + oldString.length()))).toString();
            i += newString.length();
        }
        return line;
    }
}
