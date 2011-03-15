package org.codehaus.mojo.dbupgrade.generic.test1.hsqldb;

import org.codehaus.mojo.dbupgrade.DBUpgradeException;
import org.codehaus.mojo.dbupgrade.generic.AbstractDBUpgrade;
import org.codehaus.mojo.dbupgrade.sqlexec.SQLExec;

/*
 * Copyright 2000-2010 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

public class DBUpgrade1to2
    extends AbstractDBUpgrade
{
    public void upgradeDB( SQLExec sqlexec, String dialect )
        throws DBUpgradeException
    {
        System.out.println( this.getClass().getName() );
        String sql = "update version set version = 2";
        executeSQL( sqlexec, sql );
    }

}
