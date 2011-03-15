package org.codehaus.mojo.dbupgrade.generic;

import java.sql.SQLException;

import org.codehaus.mojo.dbupgrade.DBUpgradeException;
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

public class DBUpgradeUsingSQL
    extends AbstractDBUpgrade
{
    private String sqlResouceName;

    public DBUpgradeUsingSQL( String sqlResourceName )
    {
        this.sqlResouceName = sqlResourceName;
    }

    public void upgradeDB( SQLExec sqlexec, String dialect )
        throws DBUpgradeException
    {
        try
        {
            sqlexec.execute( DBUpgradeUsingSQL.class.getClassLoader().getResourceAsStream( this.sqlResouceName ) );
        }
        catch ( SQLException e )
        {
            throw new DBUpgradeException( "Unable to upgrade database type: " + dialect, e );
        }
    }

    public String toString()
    {
        return "DBUpgradeUsingSQL:" + sqlResouceName;
    }
}
