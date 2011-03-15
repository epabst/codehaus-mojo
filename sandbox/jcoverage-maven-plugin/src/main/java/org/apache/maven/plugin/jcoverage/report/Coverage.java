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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class Coverage
{
    private List classes;
    private String srcDirectory;
    private Map packageMap;

    public Coverage()
    {
        classes = new ArrayList();
        packageMap = new HashMap();
    }

    public void setClasses(List classes)
    {
        this.classes = classes;
    }

    public void addClass(Clazz theClass)
    {
        classes.add(theClass);
        Package pkg;
        String packageName = theClass.getPackageName();
        if (!packageMap.containsKey(packageName))
        {
            pkg = new Package(packageName);
        }
        else
        {
            pkg = (Package) packageMap.get(packageName);
        }
        if (!pkg.contains(theClass))
        {
            pkg.setDirectory(theClass.getFile().substring(0, theClass.getFile().lastIndexOf("/")));
            pkg.addClass(theClass);
            packageMap.put(packageName, pkg);
        }
    }

    public List getClasses()
    {
        return classes;
    }

    public List getClassesSortedByName()
    {
        ClazzComparator comp = new ClazzComparator();
        Collections.sort(classes, comp);
        return classes;
    }

    public void setSrcDirectory(String srcDirectory)
    {
        this.srcDirectory = srcDirectory;
    }

    public String getSrcDirectory()
    {
        return srcDirectory;
    }

    public List getPackages()
    {
        return new ArrayList(packageMap.values());
    }

    public List getPackagesSortedByName()
    {
        PackageComparator comp = new PackageComparator();
        List packages = getPackages();
        Collections.sort(packages, comp);
        return packages;
    }

    public List getSubPackage(Package thePackage)
    {
        ArrayList subPkgList = new ArrayList();
        for (Iterator iter = getPackagesSortedByName().iterator(); iter.hasNext(); )
        {
            Package pkg = (Package) iter.next();
            if (pkg.getName().startsWith(thePackage.getName())
                && !pkg.getName().equals(thePackage.getName()))
            {
                subPkgList.add(pkg);
            }
        }
        return subPkgList;
    }

    public String getCoveredPercentLine()
    {
        return String.valueOf(getLineCoverage());
    }

    public String getCoveredPercentBranch()
    {
    	double totalLines = 0.00d;
    	double total = 0.00d;

        if (getLineCoverage() > 0.00d)
        {
            for (Iterator iter = getClasses().iterator(); iter.hasNext(); )
            {
                Clazz theClass = (Clazz) iter.next();
                int classLines = theClass.getLines().size();
                double rate = 0;
                try
                {
                    rate = new Double(theClass.getBranchRate()).floatValue();
                }
                catch(NumberFormatException e)
                {
                    rate = 0;
                }
                total += (rate*classLines);
                totalLines += classLines;
            }

            total /= totalLines;
        }

        return String.valueOf(total);
    }
    
    private double getLineCoverage()
    {
        double totalLines = 0.00d;
        double totalTestedLines = 0.00d;        
        for (Iterator iter = getClasses().iterator(); iter.hasNext(); )
        {
            Clazz theClass = (Clazz) iter.next();
            int classLines = theClass.getLines().size();
            double rate = 0;
            try
			{
                rate = new Double(theClass.getLineRate()).floatValue();
            }
            catch(NumberFormatException e)
            {
                rate = 0;
            }
            totalTestedLines += (rate * classLines);
            totalLines += classLines;
        }

        return (totalTestedLines / totalLines);

    }
}
