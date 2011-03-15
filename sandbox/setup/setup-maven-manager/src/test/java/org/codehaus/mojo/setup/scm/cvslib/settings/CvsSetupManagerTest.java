package org.codehaus.mojo.setup.scm.cvslib.settings;

import org.codehaus.mojo.setup.AbstractSetupManager;
import org.codehaus.mojo.setup.AbstractSetupManagerTest;

public class CvsSetupManagerTest
    extends AbstractSetupManagerTest
{

    @Override
    protected AbstractSetupManager getSetupManager()
    {
        return new CvsSetupManager();
    }
}
