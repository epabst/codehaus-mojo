package org.codehaus.mojo.setup.scm.svnlib.settings;

import org.codehaus.mojo.setup.AbstractSetupManager;
import org.codehaus.mojo.setup.AbstractSetupManagerTest;

public class SvnSetupManagerTest
    extends AbstractSetupManagerTest
{

    @Override
    protected AbstractSetupManager getSetupManager()
    {
        return new SvnSetupManager();
    }
}
