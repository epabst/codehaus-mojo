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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class Clazz
{
    private String packageName = "";
    private String name;
    private String file;
    private String lineRate;
    private String branchRate;
    private Map lines;

    public Clazz()
    {
        lines = new HashMap();
    }

    public Clazz(String longName)
    {
        this();
        int pos = longName.lastIndexOf(".");
        if (pos > 0)
        {
            packageName = longName.substring(0, pos);
            name = longName.substring(pos + 1);
        }
        else
        {
            name = longName;
        }
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setFile(String file)
    {
        this.file = file;
    }

    public String getFile()
    {
        return file;
    }

    public void setLineRate(String lineRate)
    {
        this.lineRate = lineRate;
    }

    public String getLineRate()
    {
        return lineRate;
    }

    public void setBranchRate(String branchRate)
    {
        this.branchRate = branchRate;
    }

    public String getBranchRate()
    {
        try
        {
            if (new Double(lineRate).doubleValue() == 0.0d)
            {
                return new String("0.0");
            }
        } catch (NumberFormatException nfe)
        {
            // could happen if the coverage.xml format changes.
            return new String("0");
        }

        return branchRate;
    }

    public void setLines(Collection lines)
    {
        for (Iterator iter = lines.iterator(); iter.hasNext(); )
        {
            Line line = (Line) iter.next();
            this.lines.put(new Integer(line.getNumLine()), line);
        }
    }

    public Collection getLines()
    {
        return lines.values();
    }

    public void addLine(Line line)
    {
        lines.put(new Integer(line.getNumLine()), line);
    }

    public Map getLinesMap()
    {
        return lines;
    }
}
