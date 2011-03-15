package org.codehaus.mojo.cis.core;

import java.io.File;


/**
 * A bean for copying the license key file.
 */
public class LicenseKeyBean extends AbstractCisBean {
    private File licenseFile;
    private File targetFile;

    /**
     * Returns the target file location.
     */
    public File getTargetFile() {
        return targetFile;
    }

    /**
     * Sets the target file location.
     */
    public void setTargetFile(File pTargetFile) {
        targetFile = pTargetFile;
    }

    /**
     * Returns the license files location. The license
     * file is being copied to the target location.
     */
    public File getLicenseFile() {
        return licenseFile;
    }

    /**
     * Sets the license files location. The license
     * file is being copied to the target location.
     */
    public void setLicenseFile(File pLicenseFile) {
        licenseFile = pLicenseFile;
    }
    
    public void execute() throws CisCoreException {
        final CisUtils cisUtils = getCisUtils();
        final File s = getLicenseFile();
        if (s == null) {
            throw new CisCoreErrorMessage("The license file is not set.");
        }
        final File t = getTargetFile();
        if (t == null) {
            throw new CisCoreErrorMessage("The license files target location is not set.");
        }
        if (!cisUtils.isUpToDate(new DefaultResource(s), new DefaultResource(t), true)) {
            cisUtils.makeDirOf( t );
            cisUtils.copy( s, t );
        }
    }
}
