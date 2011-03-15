/*
 * Copyright (C) 2008 Digital Sundhed (SDSD)
 *
 * All source code and information supplied as part of chronos
 * is copyright to its contributers.
 *
 * The source code has been released under a dual license - meaning you can
 * use either licensed version of the library with your code.
 *
 * It is released under the Common Public License 1.0, a copy of which can
 * be found at the link below.
 * http://www.opensource.org/licenses/cpl.php
 *
 * It is released under the LGPL (GNU Lesser General Public License), either
 * version 2.1 of the License, or (at your option) any later version. A copy
 * of which can be found at the link below.
 * http://www.gnu.org/copyleft/lesser.html
 */
package org.codehaus.mojo.chronos.history;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.codehaus.mojo.chronos.Utils;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;

/**
 * Holder to handle historic results and calculate statistics.
 * 
 * @author ksr@lakeside.dk
 */
public final class HistoricSamples {
    private List groupNames = new ArrayList();

    private List samples = new ArrayList();

    public void load(File dataDirectory) throws IOException {
        File[] historyFiles = dataDirectory.listFiles();
        if(historyFiles != null) {
            Arrays.sort(historyFiles);
            for (int i = 0; i < historyFiles.length; i++) {
                if(historyFiles[i].isFile() && historyFiles[i].getName().startsWith("history-")
                        && historyFiles[i].getName().endsWith(".ser")) {
                    HistoricSample sample = (HistoricSample)Utils.readObject(historyFiles[i]);
                    addHistoricSample(sample);
                }
            }
        }
    }

    void addHistoricSample(HistoricSample sample) {
        samples.add(sample);
        for (Iterator it = sample.getGroupNames().iterator(); it.hasNext();) {
            String name = (String)it.next();
            if(!groupNames.contains(name)) {
                groupNames.add(name);
            }
        }
    }

    public String[] getGroupNames() {
        return (String[])groupNames.toArray(new String[groupNames.size()]);
    }

    public TimeSeries getAverageTime(String name) {
        return visitAll(name, new HistoricSampleExtractor() {
            public double extract(HistoricSample sample) {
                return sample.getResponsetimeAverage();
            }

            public boolean accept(HistoricSample sample) {
                return true;
            }
        });
    }

    public TimeSeries getAverageTime(String name, final String groupName) {
        return visitAll(name, new HistoricSampleExtractor() {
            public double extract(HistoricSample sample) {
                return sample.getResponsetimeAverage(groupName);
            }

            public boolean accept(HistoricSample sample) {
                return sample.getGroupNames().contains(groupName);
            }
        });
    }

    public TimeSeries getpercentile95(String name) {
        return visitAll(name, new HistoricSampleExtractor() {
            public double extract(HistoricSample sample) {
                return sample.getResponsetime95Percentile();
            }

            public boolean accept(HistoricSample sample) {
                return true;
            }
        });
    }

    public TimeSeries getPercentile95(String name, final String groupName) {
        return visitAll(name, new HistoricSampleExtractor() {
            public double extract(HistoricSample sample) {
                return sample.getResponsetimePercentiles(groupName);
            }

            public boolean accept(HistoricSample sample) {
                return sample.getGroupNames().contains(groupName);
            }
        });
    }

    public TimeSeries getThroughput(String name) {
        return visitAll(name, new HistoricSampleExtractor() {
            public double extract(HistoricSample sample) {
                return sample.getMaxAverageThroughput();
            }

            public boolean accept(HistoricSample sample) {
                return true;
            }
        });
    }

    public TimeSeries getGcRatio(String name) {
        return visitAll(name, new HistoricSampleExtractor() {
            public double extract(HistoricSample sample) {
                return sample.getGcRatio();
            }

            public boolean accept(HistoricSample sample) {
                return true;
            }
        });
    }

    public TimeSeries getKbCollectedPrSecond(String name) {
        return visitAll(name, new HistoricSampleExtractor() {
            public double extract(HistoricSample sample) {
                return sample.getCollectedPrSecond();
            }

            public boolean accept(HistoricSample sample) {
                return true;
            }
        });
    }

    private TimeSeries visitAll(String name, HistoricSampleExtractor visitor) {
        TimeSeries series = new TimeSeries(name);
        Iterator it = samples.iterator();
        while (it.hasNext()) {
            HistoricSample sample = (HistoricSample)it.next();
            if(visitor.accept(sample)) {
                Millisecond timestamp = new Millisecond(new Date(sample.getTimestamp()));
                double value = visitor.extract(sample);
                series.addOrUpdate(timestamp, value);
            }
        }
        return series;
    }

    /**
     * Base interface for extracting statistics from historic results.
     * 
     * @author kent (creator)
     * @author $LastChangedBy$ $LastChangedDate$
     * @version $Revision$
     */
    interface HistoricSampleExtractor {
        double extract(HistoricSample sample);

        boolean accept(HistoricSample sample);
    }
}
