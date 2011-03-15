package org.codehaus.mojo.setup.scm;

import java.io.File;

import org.codehaus.mojo.setup.AbstractSetupMojoTest;

public abstract class AbstractScmSettingsSetupMojoTest
    extends AbstractSetupMojoTest
{

    private File testResourceDefaultScmSettings;
    private File testResourceNoScmSettings;
    private File testResourceMinimumScmSettings;
    
    private File targetFile;

    @Override
    protected void onSetUp()
        throws Exception
    {
        testResourceDefaultScmSettings = getTestFile( "src/test/resources/defaultScmHome" );
        testResourceNoScmSettings = getTestFile( "src/test/resources/emptyScmHome" );
        testResourceMinimumScmSettings = getTestFile( "src/test/resources/minimalScmHome" );
    }
    
    @Override
    protected File getTargetFile()
    {
        return targetFile;
    }
    
    protected void setTargetFile( File targetFile )
    {
        this.targetFile = targetFile;
    }

    @Override
    protected File getDirectoryDefaultTargetFile()
    {
        return testResourceDefaultScmSettings;
    }

    @Override
    protected File getDirectoryMissingTargetFile()
    {
        return testResourceNoScmSettings;
    }
    
    @Override
    protected File getDirectoryMinimumTargetFile()
    {
        return testResourceMinimumScmSettings;
    }
}
