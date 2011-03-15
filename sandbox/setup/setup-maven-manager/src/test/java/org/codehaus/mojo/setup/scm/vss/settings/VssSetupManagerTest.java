package org.codehaus.mojo.setup.scm.vss.settings;

import org.codehaus.mojo.setup.AbstractSetupManager;
import org.codehaus.mojo.setup.AbstractSetupManagerTest;

public class VssSetupManagerTest
    extends AbstractSetupManagerTest
{

    @Override
    protected AbstractSetupManager getSetupManager()
    {
        return new VssSetupManager();
    }
}
