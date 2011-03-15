package org.codehaus.mojo.setup.scm.starteam.settings;

import org.codehaus.mojo.setup.AbstractSetupManager;
import org.codehaus.mojo.setup.AbstractSetupManagerTest;

public class StarteamSetupManagerTest
    extends AbstractSetupManagerTest
{

    @Override
    protected AbstractSetupManager getSetupManager()
    {
        return new StarteamSetupManager();
    }
}
