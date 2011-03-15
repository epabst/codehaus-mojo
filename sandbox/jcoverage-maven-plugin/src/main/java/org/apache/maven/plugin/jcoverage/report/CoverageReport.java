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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.oro.text.perl.Perl5Util;

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class CoverageReport
{
    /** coverage data */
    private Coverage coverage;

    /**
     * Create a coverage report
     * @param coverage coverage data
     */
    public CoverageReport(Coverage coverage)
    {
        this.coverage = coverage;
    }

    public void generate(String dir) throws IOException
    {
        File directory = new File(dir);
        directory.mkdirs();

        generateSourceFiles(directory);
        generateFrameset(directory);
        generatePackageList(directory);
        generateClassList(directory);
        generateOverview(directory);
    }

    private void generateFrameset(File dir) throws IOException
    {
        File fsFile = new File(dir, "index.html");
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fsFile)));
        pw.println("<html>");
        pw.println("<head>");
        pw.println("<title>unit tests coverage report</title>");
        pw.println("</head>");
        pw.println("<FRAMESET cols=\"20%,80%\">");
        pw.println("<FRAMESET rows=\"30%,70%\">");
        pw.println("<FRAME src=\"overview-frame.html\" name=\"packageListFrame\" title=\"All Packages\">");
        pw.println("<FRAME src=\"allclasses-frame.html\" name=\"packageFrame\" title=\"All classes and interfaces (except non-static nested types)\">");
        pw.println("</FRAMESET>");
        pw.println("<FRAME src=\"overview-summary.html\" name=\"classFrame\" title=\"Package, class and interface descriptions\" scrolling=\"yes\">");
        pw.println("<NOFRAMES>");
        pw.println("This document is designed to be viewed using the frames feature. If you see this message, you are using a non-frame-capable web client.");
        pw.println("<BR>");
        pw.println("Link to<A HREF=\"overview-summary.html\">Non-frame version.</A>");
        pw.println("</NOFRAMES>");
        pw.println("</FRAMESET>");
        pw.println("</html>");
        pw.close();
    }

    private void generatePackageList(File dir) throws IOException
    {
        File fsFile = new File(dir, "overview-frame.html");
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fsFile)));
        pw.println("<html>");
        pw.println("<head>");
        pw.println("<title>unit tests coverage report</title>");
        pw.println("<link rel =\"stylesheet\" type=\"text/css\" href=\"style.css\" title=\"Style\">");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("<span class=\"title\">Coverage report</span>");
        pw.println("<table>");
        pw.println("<tr>");
        pw.println("<td nowrap=\"nowrap\">");
        pw.println("<a href=\"overview-summary.html\" target=\"classFrame\">Overview</a><br>");
        pw.println("<a href=\"allclasses-frame.html\" target=\"packageFrame\">All classes</a>");
        pw.println("</td>");
        pw.println("</tr>");
        pw.println("</table>");
        pw.println("<p>");
        pw.println("<table>");
        pw.println("<tr>");
        pw.println("<td nowrap=\"nowrap\"><span class=\"title2\">All packages</span></td>");
        pw.println("</tr>");
        pw.println("<tr>");
        pw.println("<td nowrap=\"nowrap\">");

        for (Iterator iter = coverage.getPackagesSortedByName().iterator(); iter.hasNext(); )
        {
            Package pkg = (Package) iter.next();
            String url = pkg.getDirectory() + "/package-frame.html";
            pw.println("<a href=\"" + url + "\" target=\"packageFrame\">" + pkg.getName() + "</a><br>");
        }

        pw.println("</td>");
        pw.println("</tr>");
        pw.println("</table>");
        pw.println("</body>");
        pw.println("</html>");
        pw.close();

        for (Iterator iter = coverage.getPackagesSortedByName().iterator(); iter.hasNext(); )
        {
            Package pkg = (Package) iter.next();
            generateClassList(dir, pkg);
            generateOverview(dir, pkg);
        }
    }

    private void generateClassList(File dir) throws IOException
    {
        generateClassList(dir, null);
    }

    private void generateClassList(File dir, Package pkg) throws IOException
    {
        String filename;
        String rootRef;
        List classes;
        String urlDirectory = "";
        if (pkg == null)
        {
            rootRef = "";
            filename = "allclasses-frame.html";
            classes = coverage.getClassesSortedByName();
        }
        else
        {
            rootRef = getRelativePath(pkg.getName() + "/");
            filename = pkg.getDirectory() + "/package-frame.html";
            classes = pkg.getClassesSortedByName();
            urlDirectory = ".";
        }
        File fsFile = new File(dir, filename);
        fsFile.getParentFile().mkdirs();
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fsFile)));
        pw.println("<html>");
        pw.println("<head>");
        pw.println("<title>unit tests coverage report</title>");
        pw.println("<link rel =\"stylesheet\" type=\"text/css\" href=\"" + rootRef + "style.css\" title=\"Style\">");
        pw.println("</head>");
        pw.println("<body>");
        if (pkg != null)
        {
            pw.println("<a href=\"package-summary.html\" target=\"classFrame\">" + pkg.getName() + "</a><br>");
            pw.println("<p>");
        }
        pw.println("<span class=\"title\">All classes</span>");
        pw.println("<table>");
        pw.println("<tr>");
        pw.println("<td nowrap=\"nowrap\">");

        for (Iterator iter = classes.iterator(); iter.hasNext(); )
        {
            Clazz theClass = (Clazz) iter.next();
            if (pkg == null)
            {
                urlDirectory = theClass.getFile().substring(0, theClass.getFile().lastIndexOf("/"));
            }
            String classFilename = theClass.getName() + ".html";
            if (theClass.getFile().endsWith("/" + theClass.getName() + ".java"))
            {
                pw.println("<a href=\"" + urlDirectory + "/" + classFilename + "\" target=\"classFrame\">" + theClass.getName() + "</a><span class=\"text_italic\">&nbsp;(" + getPercentValue(theClass.getLineRate()) + ")</span><br>");
            }
        }

        pw.println("</td>");
        pw.println("</tr>");
        pw.println("</table>");
        pw.println("</body>");
        pw.println("</html>");
        pw.close();
    }

    private void generateOverview(File dir) throws IOException
    {
        generateOverview(dir, null);
    }

    private void generateOverview(File dir, Package thePackage) throws IOException
    {
        String filename = "overview-summary.html";
        String rootRef;
        if (thePackage != null)
        {
            filename = thePackage.getDirectory() + "/package-summary.html";
            rootRef = getRelativePath(thePackage.getName() + "/");
        }
        else
        {
            rootRef = "";
        }
        File fsFile = new File(dir, filename);
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fsFile)));
        pw.println("<html>");
        pw.println("<head>");
        pw.println("<title>unit tests coverage report</title>");
        pw.println("<link rel =\"stylesheet\" type=\"text/css\" href=\"" + rootRef + "style.css\" title=\"Style\">");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("<span class=\"title\">Coverage report</span>");
        pw.println("<p>");
        pw.println("<table class=\"report\" cellpadding=\"0\" cellspacing=\"0\">");
        pw.println("<tr class=\"report\">");
        pw.println("<th class=\"report\">&nbsp;</th>");
        pw.println("<th class=\"report\">Files</th>");
        pw.println("<th class=\"report\">%line</th>");
        pw.println("<th class=\"report\">%branch</th>");
        pw.println("</tr>");
        pw.println("<tr class=\"report\">");
        if (thePackage != null)
        {
            pw.println("<td class=\"reportText\">" + thePackage.getName() + "</td>");
            pw.println("<td class=\"reportValue\">" + thePackage.getClasses().size() + "</td>");
            pw.println("<td class=\"reportValue\">" + generatePercentResult(getPercentValue(thePackage.getCoveredPercentLine())) + "</td>");
            pw.println("<td class=\"reportValue\">" + generatePercentResult(getPercentValue(thePackage.getCoveredPercentBranch())) + "</td>");
        }
        else
        {
            pw.println("<td class=\"reportText\">Project</td>");
            pw.println("<td class=\"reportValue\">" + coverage.getClasses().size() + "</td>");
            pw.println("<td class=\"reportValue\">" + generatePercentResult(getPercentValue(coverage.getCoveredPercentLine())) + "</td>");
            pw.println("<td class=\"reportValue\">" + generatePercentResult(getPercentValue(coverage.getCoveredPercentBranch())) + "</td>");
        }
        pw.println("</tr>");
        List pkgList = null;
        if (thePackage != null)
        {
            pkgList = coverage.getSubPackage(thePackage);
            if (pkgList.size() > 0)
            {
                pw.println("<tr>");
                pw.println("<td class=\"spacer\" colspan=\"4\"><span class=\"title2\">Packages</span></td>");
                pw.println("</tr>");
                for (Iterator iter = pkgList.iterator(); iter.hasNext(); )
                {
                    Package pkg = (Package) iter.next();
                    pw.println("<tr class=\"report\">");
                    String subPkgDir = pkg.getDirectory().substring(thePackage.getDirectory().length() + 1);
                    pw.println("<td class=\"reportText\"><a href=\"" + subPkgDir + "/package-summary.html\">" + pkg.getName() + "</a></td>");
                    pw.println("<td class=\"reportValue\">" + pkg.getClasses().size() + "</td>");
                    pw.println("<td class=\"reportValue\">" + generatePercentResult(getPercentValue(pkg.getCoveredPercentLine())) + "</td>");
                    pw.println("<td class=\"reportValue\">" + generatePercentResult(getPercentValue(pkg.getCoveredPercentBranch())) + "</td>");
                    pw.println("</tr>");
                }
            }
            if (thePackage.getClasses().size() > 0)
            {
                pw.println("<tr>");
                pw.println("<td class=\"spacer\" colspan=\"4\"><span class=\"title2\">Classes</span></td>");
                pw.println("</tr>");
                for (Iterator it = thePackage.getClassesSortedByName().iterator(); it.hasNext(); )
                {
                    Clazz cl = (Clazz) it.next();
                    if (cl.getFile().indexOf("[Unknown]")<0)
                    {
                        String classFilename = cl.getFile().substring(cl.getFile().lastIndexOf("/")+1, cl.getFile().lastIndexOf(".")) + ".html";
                        pw.println("<tr class=\"report\">");
                        pw.println("<td class=\"reportText\" colspan=\"2\"><a href=\"" + classFilename + "\">" + cl.getName() + "</a></td>");
                        pw.println("<td class=\"reportValue\">" + generatePercentResult(getPercentValue(cl.getLineRate())) + "</td>");
                        pw.println("<td class=\"reportValue\">" + generatePercentResult(getPercentValue(cl.getBranchRate())) + "</td>");
                        pw.println("</tr>");
                    }
                }
            }
        }
        else
        {
            pkgList = coverage.getPackages();
            if (pkgList.size() > 0)
            {
                pw.println("<tr>");
                pw.println("<td class=\"spacer\" colspan=\"4\"><span class=\"title2\">Packages</span></td>");
                pw.println("</tr>");
                for (Iterator iter = coverage.getPackagesSortedByName().iterator(); iter.hasNext(); )
                {
                    Package pkg = (Package) iter.next();
                    pw.println("<tr class=\"report\">");
                    pw.println("<td class=\"reportText\"><a href=\"" + pkg.getDirectory() + "/package-summary.html\">" + pkg.getName() + "</a></td>");
                    pw.println("<td class=\"reportValue\">" + pkg.getClasses().size() + "</td>");
                    pw.println("<td class=\"reportValue\">" + generatePercentResult(getPercentValue(pkg.getCoveredPercentLine())) + "</td>");
                    pw.println("<td class=\"reportValue\">" + generatePercentResult(getPercentValue(pkg.getCoveredPercentBranch())) + "</td>");
                    pw.println("</tr>");
                }
            }
            if (coverage.getClasses().size() > 0)
            {
                List classesList = new ArrayList();
                for (Iterator iter = coverage.getClassesSortedByName().iterator(); iter.hasNext(); )
                {
                    Clazz cl = (Clazz) iter.next();
                    if (cl.getPackageName() == null
                        || cl.getPackageName().equals(""))
                    {
                        classesList.add(cl);
                    }
                }
                if (classesList.size() > 0)
                {
                    pw.println("<tr>");
                    pw.println("<td class=\"spacer\" colspan=\"4\"><span class=\"title2\">Classes</span></td>");
                    pw.println("</tr>");
                    for (Iterator iter = classesList.iterator(); iter.hasNext(); )
                    {
                        Clazz cl = (Clazz) iter.next();
                        if (cl.getFile().indexOf("[Unknown]")<0)
                        {
                            String classFilename = cl.getFile().substring(cl.getFile().lastIndexOf("/")+1, cl.getFile().lastIndexOf(".")) + ".html";
                            pw.println("<tr class=\"report\">");
                            pw.println("<td class=\"reportText\" colspan=\"2\"><a href=\"" + classFilename + "\">" + cl.getName() + "</a></td>");
                            pw.println("<td class=\"reportValue\">" + generatePercentResult(getPercentValue(cl.getLineRate())) + "</td>");
                            pw.println("<td class=\"reportValue\">" + generatePercentResult(getPercentValue(cl.getBranchRate())) + "</td>");
                            pw.println("</tr>");
                        }
                    }
                }
            }
        }
        pw.println("</table>");
        pw.println(generateFooter());
        pw.println("</body>");
        pw.println("</html>");
        pw.close();
    }

    private void generateSourceFiles(File dir) throws IOException
    {
        for (Iterator iter = coverage.getClasses().iterator(); iter.hasNext(); )
        {
            Clazz theClass = (Clazz) iter.next();
            File file = new File(coverage.getSrcDirectory(), theClass.getFile());
            if (file.exists())
            {
                generateSourceFile(dir, theClass);
            }
        }
    }

    private void generateSourceFile(File directory, Clazz theClass) throws IOException
    {
        String srcOutputFilename = theClass.getFile().substring(0, theClass.getFile().lastIndexOf(".")) + ".html";
        File srcOutputFile = new File(directory, srcOutputFilename);
        File dirOutputFile = srcOutputFile.getParentFile();
        if (dirOutputFile != null)
        {
            dirOutputFile.mkdirs();
        }

        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(srcOutputFile)));
        pw.println("<html>");
        pw.println("<head>");
        pw.println("<title>unit tests coverage</title>");
        String rootRef = getRelativePath(theClass.getPackageName());
        pw.println("<link rel =\"stylesheet\" type=\"text/css\" href=\"" + rootRef + "style.css\" title=\"Style\">");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("<span class=\"title\">Coverage report</span>");
        pw.println("<p>");
        pw.println("  <table cellspacing=\"0\" cellpadding=\"0\" class=\"report\">");
        pw.println("  <tr class=\"report\">");
        pw.println("    <th class=\"report\">&nbsp;</th>");
        pw.println("    <th class=\"report\">%line</th>");
        pw.println("    <th class=\"report\">%branch</th>");
        pw.println("  </tr>");
        pw.println("  <tr class=\"report\">");
        pw.println("    <td class=\"reportText\"><span class=\"text\">" + theClass.getPackageName() + "." + theClass.getName() + "</span></td>");
        pw.println("    <td class=\"reportValue\">" + generatePercentResult(getPercentValue(theClass.getLineRate())) + "</td>");
        pw.println("    <td class=\"reportValue\">" + generatePercentResult(getPercentValue(theClass.getBranchRate())) + "</td>");
        pw.println("  </tr>");
        pw.println("  </table>");
        pw.println("  <p>");
        pw.println("  <table cellspacing=\"0\" cellpadding=\"0\" class=\"src\">");

        BufferedReader br = new BufferedReader(new FileReader(new File(coverage.getSrcDirectory(), theClass.getFile())));
        String lineStr;
        int numLigne = 1;
        while ((lineStr = br.readLine()) != null)
        {
            Line theLine = (Line) theClass.getLinesMap().get(new Integer(numLigne));
            int nbHits = 0;
            if (theLine != null)
            {
                nbHits = theLine.getNbHits();
            }
            pw.println("    <tr>");
            if (theLine != null)
            {
                pw.println("      <td class=\"numLineCover\">&nbsp;" + numLigne + "</td>");
                if (nbHits > 0)
                {
                    pw.println("      <td class=\"nbHitsCovered\">&nbsp;" + theLine.getNbHits() + "</td>");
                    pw.println("      <td class=\"src\"><pre class=\"src\">&nbsp;" + JavaToHtml.syntaxHighlight(lineStr) + "</pre></td>");
                }
                else
                {
                    pw.println("      <td class=\"nbHitsUncovered\">&nbsp;" + theLine.getNbHits() + "</td>");
                    pw.println("      <td class=\"src\"><pre class=\"src\"><span class=\"srcUncovered\">&nbsp;"+ JavaToHtml.syntaxHighlight(lineStr) + "</span></pre></td>");
                }
            }
            else
            {
                pw.println("      <td class=\"numLine\">&nbsp;" + numLigne + "</td>");
                pw.println("      <td class=\"nbHits\">&nbsp;</td>");
                pw.println("      <td class=\"src\"><pre class=\"src\">&nbsp;" +  JavaToHtml.syntaxHighlight(lineStr) + "</pre></td>");
            }
            pw.println("    </tr>");
            numLigne++;
        }
        pw.println("  </table>");
        pw.println(generateFooter());
        pw.println("</body>");
        pw.println("</html>");
        pw.close();
    }

    private String generateFooter()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<p>");
        sb.append("<table cellpadding=\"0\" cellspacing=\"0\" class=\"report\">");
        sb.append("  <tr class=\"report\">");
        sb.append("    <td class=\"reportText\"><span class=\"text\">");
        sb.append("    This report is generated by <a href=\"http://www.jcoverage.com\">jcoverage</a>, <a href=\"http://maven.apache.org\">Maven</a> and <a href=\"http://maven.apache.org/reference/plugins/jcoverage/\">Maven JCoverage Plugin</a>.");
        sb.append("    </span></td>");
        sb.append("  </tr>");
        sb.append("</table>");
        return sb.toString();
    }

    private String generatePercentResult(String percentValue)
    {
        if (percentValue.endsWith("%"))
        {
            percentValue = percentValue.substring(0, percentValue.length() - 1);
        }
        double rest = 0;
        try
        {
            rest = 100d - new Double(percentValue).doubleValue();
        }
        catch(NumberFormatException e)
        {
            rest = 0;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("<table class=\"percentGraph\" cellpadding=\"0\" cellspacing=\"0\" align=\"right\">");
        sb.append("<tr>");
        sb.append("<td><span class=\"text\">" + percentValue + "%&nbsp;</span></td>");
        sb.append("<td>");
        sb.append("<table class=\"percentGraph\" cellpadding=\"0\" cellspacing=\"0\">");
        sb.append("<tr>");
        sb.append("<td class=\"percentCovered\" width=\"" + percentValue + "\"></td>");
        sb.append("<td class=\"percentUnCovered\" width=\"" + String.valueOf(rest) + "\"></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("</td>");
        sb.append("</tr>");
        sb.append("</table>");
        return sb.toString();
    }

    private String getRelativePath(String path)
    {
        if (path != null && !path.equals(""))
        {
            return new Perl5Util().substitute("s/[^\\.]*(\\.|$)/..\\//g", path);
        }
        else
        {
            return "";
        }
    }

    private String getPercentValue(String value)
    {
        if (value.endsWith("%"))
        {
            value = value.substring(0, value.length() - 1);
        }
        double percent = 0;
        try
        {
            percent = new Double(value).doubleValue();
        }
        catch(NumberFormatException e)
        {
            percent = 0;
        }
        NumberFormat percentFormatter;

        percentFormatter = NumberFormat.getPercentInstance();
        return percentFormatter.format(percent);
    }
}