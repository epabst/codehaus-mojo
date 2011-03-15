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

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class Package
{
    private List classes;
    private String name = "";
    private String directory;

    public Package(String name)
    {
        this.name = name;
        classes = new ArrayList();
    }

    public void setName(String name)
    {
        if (name == null)
        {
            System.out.println("Package == null");
        }
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setDirectory(String directory)
    {
        this.directory = directory;
    }

    public String getDirectory()
    {
        return directory;
    }

    public boolean contains(Clazz theClass)
    {
        return classes.contains(theClass);
    }

    public void addClass(Clazz theClass)
    {
        classes.add(theClass);
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

    public String getCoveredPercentLine()
    {
        return String.valueOf(getLineCoverage());
    }

    public String getCoveredPercentBranch()
    {
    	double pckgLines = 0.00d;
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
                pckgLines += classLines;
            }

            total /= pckgLines;
        }

        return String.valueOf(total);
    }

    private double getLineCoverage()
    {
        double pckgLines = 0.00d;
        double pckgTestedLines = 0.00d;        
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
            pckgTestedLines += (rate * classLines);
            pckgLines += classLines;
        }

        return (pckgTestedLines / pckgLines);
    }
}
