package org.apache.maven.diagrams.connector_api;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Base exception to handle all connector's faults
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public class ConnectorException extends Exception
{
    private static final long serialVersionUID = 1L;

    public ConnectorException()
    {
        super();
    }

    public ConnectorException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public ConnectorException( String message )
    {
        super( message );
    }

    public ConnectorException( Throwable cause )
    {
        super( cause );
    }

}
