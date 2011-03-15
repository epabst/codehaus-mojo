package org.codehaus.mojo.setup.scm.clearcase.settings;

import org.codehaus.mojo.setup.AbstractSetupManager;
import org.codehaus.mojo.setup.AbstractSetupManagerTest;

public class ClearcaseSetupManagerTest
    extends AbstractSetupManagerTest
{

    @Override
    protected AbstractSetupManager getSetupManager()
    {
        return new ClearcaseSetupManager();
    }
}
