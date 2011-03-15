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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.chronos.Utils;
import org.codehaus.mojo.chronos.gc.GCSamples;
import org.codehaus.mojo.chronos.responsetime.GroupedResponsetimeSamples;

/**
 * Save a snapshot of the currently executed test to enable later historic reports.
 * 
 * @goal savehistory
 * @phase post-integration-test
 */
public class SaveHistoryMojo extends AbstractMojo {
    /**
     * The current maven project.
     * 
     * @parameter expression="${project}"
     */
    private MavenProject project;

    /**
     * The directory where historic data are stored.
     * 
     * @parameter expression="${basedir}/target/chronos/history"
     */
    private File historydir;

    /**
     * The id of the currently executed performancetest.
     * 
     * @parameter default-value="performancetest"
     */
    private String dataid;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if(!historydir.exists()) {
            historydir.mkdir();
        }
        File dataDirectory = new File(historydir, dataid);
        if(!dataDirectory.exists()) {
            dataDirectory.mkdir();
        }
        GroupedResponsetimeSamples responseSamples = getResponsetimeSamples();
        GCSamples gcSamples = getGcSamples();
        long firstTimestamp = responseSamples.getFirstTimestamp();
        String fileName = "history-" + firstTimestamp + ".ser";
        File historyFile = new File(dataDirectory, fileName);
        if(historyFile.exists()) {
            historyFile.delete();
        }
        HistoricSample history = new HistoricSample(responseSamples, gcSamples);
        try {
            Utils.writeObject(history, historyFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to write historydata " + historyFile.getAbsolutePath());
        }

    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }

    public void setHistorydir(File historydir) {
        this.historydir = historydir;
    }

    private GCSamples getGcSamples() throws MojoExecutionException {
        try {
            return Utils.readGCSamples(project.getBasedir(), dataid);
        } catch (IOException e) {
            throw new MojoExecutionException("unable to find gcsamples with dataid=" + dataid);
        }
    }

    private GroupedResponsetimeSamples getResponsetimeSamples() throws MojoExecutionException {
        File responsetimeSamples = Utils.getPerformanceSamplesSer(project.getBasedir(), dataid);
        if(!responsetimeSamples.exists()) {
            throw new MojoExecutionException("File " + responsetimeSamples.getAbsolutePath() + " not found");
        }
        try {
            return (GroupedResponsetimeSamples)Utils.readObject(responsetimeSamples);
        } catch (IOException e) {
            throw new MojoExecutionException("unable to read responsetimesamples "
                    + responsetimeSamples.getAbsolutePath(), e);
        }
    }
}
