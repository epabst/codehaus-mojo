/**
 * 
 */
package org.codehaus.mojo.dbupgrade.generic;

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


public interface DBUpgrade
{
    /**
     * Incremental upgrade interface
     * @param sqlExec. The implementation uses this param to have access to SQLExec utils and connection
     * @param dialect. The implementation uses this param to locate the database specific resource
     * @throws DBUpgradeException
     */
    void upgradeDB( SQLExec sqlExec, String dialect ) throws DBUpgradeException;
}
