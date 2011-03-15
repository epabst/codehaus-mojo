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
package org.codehaus.mojo.chronos.jmeter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.jfree.io.IOUtils;

/**
 * Manages dependencies for the current maven project (making them available for jmeter).
 * 
 * @author ksr@lakeside.dk
 */
public final class DependencyUtil {
    private String jmeterhome;

    private Log log;

    public DependencyUtil(String jmeterHome, Log log) {
        this.jmeterhome = jmeterHome;
        this.log = log;
    }

    public List getDependencies(MavenProject project) {
        List result = new ArrayList();

        Iterator it = project.getAttachedArtifacts().iterator();
        while (it.hasNext()) {
            Artifact artifact = (Artifact)it.next();
            File attachedArtifactFile = artifact.getFile();
            result.add(attachedArtifactFile);
        }
        File artifactFile = project.getArtifact().getFile();
        if(artifactFile == null) {
            log.warn("Artifact not found. Note that if Your JMeter test contains JUnittestcases, "
                    + "You can only invoke this goal through the default lifecycle.");
        } else {
            result.add(artifactFile);
        }
        Set dependencyArtifacts = project.getArtifacts();
        if(dependencyArtifacts != null) {
            Iterator deps = dependencyArtifacts.iterator();
            while (deps.hasNext()) {
                Artifact dependency = (Artifact)deps.next();
                result.add(dependency.getFile());
            }
        }
        return result;
    }

    List copyDependencies(MavenProject project) throws IOException {
        final List copied = new ArrayList();
        Iterator it = getDependencies(project).iterator();
        while (it.hasNext()) {
            File artifactFile = (File)it.next();
            copyFileToDir(artifactFile, copied);
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                cleanUpDependencies(copied);
            }
        });
        return copied;
    }

    void cleanUpDependencies(List copied) {
        for (Iterator iterator = copied.iterator(); iterator.hasNext();) {
            File file = (File)iterator.next();
            if(file.exists()) {
                file.delete();
            }
        }
    }

    void copyFileToDir(File file, List copied) throws IOException {
        File lib = new File(jmeterhome, "lib");
        File junitdir = new File(lib, "junit");
        File target = new File(junitdir, file.getName());
        
        /* Merge from Atlassion */
        int i = 0;
        while (target.exists()) {
            target = new File(junitdir, String.valueOf(i) + "-" + file.getName());
            i++;
        }
        /* End */
        
        target.createNewFile();
        InputStream input = new BufferedInputStream(new FileInputStream(file));
        OutputStream output = new BufferedOutputStream(new FileOutputStream(target));
        IOUtils.getInstance().copyStreams(input, output);
        output.close();
        input.close();
        log.debug("Dependency copied to jmeter distribution at: " + target);
        copied.add(target);
    }
}
