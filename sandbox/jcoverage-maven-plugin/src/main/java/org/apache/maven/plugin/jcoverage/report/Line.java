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

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class Line
{
    private int numLine;
    private int nbHits;

    public void setNumLine(int num)
    {
        this.numLine = num;
    }

    public int getNumLine()
    {
        return numLine;
    }

    public void setNbHits(int nb)
    {
        this.nbHits = nb;
    }

    public int getNbHits()
    {
        return nbHits;
    }
}
