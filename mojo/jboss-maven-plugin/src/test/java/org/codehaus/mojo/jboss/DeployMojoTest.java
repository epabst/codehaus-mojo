/*
 * DeployMojoTest.java
 *
 * $license$
 *
 * $version$
 */

package org.codehaus.mojo.jboss;

import java.net.URLEncoder;
import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * DeployMojoTest
 *
 * @author <a href="mailto:david.siefert@taylorandfrancis.com">David Siefert</a>
 */
public class DeployMojoTest extends TestCase {
    
    public DeployMojoTest(String test) {
        super(test);
    }

    private DeployMojo mojo;

    private String doURLResult;
    private String hostname = "localhost";
    private int port = 8080;
    private String deployUrlPath = "/deployUrlPath&jar=";

    public void setUp() throws Exception {
        // This obviously won't work for multiple files--it is only for this test.
        mojo = new DeployMojo() {
            protected void doURL(String url) throws MojoExecutionException {
                doURLResult = url;
            }
        };
        mojo.hostName = hostname;
        mojo.port = port;
        mojo.deployUrlPath = deployUrlPath;
    }

    public void tearDown() throws Exception {
        mojo = null;
        doURLResult = null;
    }

    public void testEncodingFileNameWithSpace() throws Exception {
        String testFile = "C:\\Documents and Settings\\maven\\project\\somefile.jar";
        mojo.fileNames = new ArrayList();
        mojo.fileNames.add(testFile);
        mojo.execute();

        Assert.assertEquals("http://" + hostname + ":" + port + deployUrlPath + URLEncoder.encode(testFile), doURLResult);
    }
}
