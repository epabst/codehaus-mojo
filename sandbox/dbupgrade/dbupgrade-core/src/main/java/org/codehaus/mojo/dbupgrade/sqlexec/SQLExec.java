package org.codehaus.mojo.dbupgrade.sqlexec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

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

public interface SQLExec
{
    enum DelimiterType {
        NORMAL,

        ROW
    }

    
    Connection getConnection()
        throws SQLException;

    /**
     * throws RuntimeException
     */
    void rollback();

    void commit() throws SQLException;
    
    void rollbackQuietly();

    void close();

    void execute( String sqlCommand )
        throws SQLException;

    void execute( File[] srcFiles )
        throws SQLException;

    void execute( FileSet fileset )
        throws SQLException;

    void execute( File sqlFile )
        throws SQLException;
    
    void execute( File sqlFile, boolean disableSQLParser )    
        throws SQLException, IOException;
    
    void execute( String sqlCommand, File[] srcFiles, FileSet fileset )
        throws SQLException;

    public void execute( InputStream istream )
        throws SQLException;

    public void execute( Reader reader )
        throws SQLException;

}
