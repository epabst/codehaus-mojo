package org.codehaus.mojo.dbupgrade.generic;

import org.codehaus.mojo.dbupgrade.DBUpgradeConfiguration;

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


public class GenericDBUpgradeConfiguration
    extends DBUpgradeConfiguration
{
    /**
     * Database type
     */
    private String dialect;
    
    /**
     * Table name to be used to look for version for
     */
    private String versionTableName = "version";
    
    /**
     * Column name in versionTableName to be used to look for version info
     */
    private String versionColumnName = "version";
    
    
    /**
     * Contains resource to perform upgrade
     */
    private String packageName;
    
    /**
     * name of the resource under packageName that contains version=??? property
     */
    private String versionResourceName = "version.properties";
    
    /**
     * Prefix name of sql/java upgrade files
     */
    private String upgraderPrefix = "DBUpgrade";
    
    /**
     * version to be inserted when version table first created
     */
    private int initialVersion = 0;
    
    public void setDialect( String dialect )
    {
        this.dialect = dialect;
    }
    
    public String getDialect( )
    {
        return this.dialect;
    } 
    
    public void setPackageName( String packageName )
    {
        this.packageName = packageName;
    }
    
    public String getPackageName( )
    {
        return this.packageName;
    }    
    
    public String getPackageNameSlashFormat()
    {
        return this.packageName.replace( '.', '/' );
    }
    
    public void setVersionResourceName( String versionResourceName )
    {
        this.versionResourceName = versionResourceName;
    }
    
    public String getVersionResourceName( )
    {
        return this.versionResourceName;
    }      
        
    public void setVersionTableName( String versionTableName )
    {
        this.versionTableName = versionTableName;
    }
    
    public String getVersionTableName( )
    {
        return this.versionTableName;
    }      
    
    public void setVersionColumnName( String versionCollumnName )
    {
        this.versionColumnName = versionCollumnName;
    }
    
    public String getVersionColumnName( )
    {
        return this.versionColumnName;
    }

    public String getUpgraderPrefix()
    {
        return upgraderPrefix;
    }

    public void setUpgraderPrefix( String upgraderPrefix )
    {
        this.upgraderPrefix = upgraderPrefix;
    }

    public int getInitialVersion()
    {
        return initialVersion;
    }

    public void setInitialVersion( int initialVersion )
    {
        this.initialVersion = initialVersion;
    }
    
}
