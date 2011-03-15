package org.apache.maven.changes;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Throw when the content of a particular <tt>changes.xml</tt> file
 * is invalid.
 *
 * @author <a href="snicoll@apache.org">Stephane Nicoll</a>
 * @version $Id$
 */
public class InvalidChangesException
    extends Exception
{

    public InvalidChangesException()
    {
    }

    public InvalidChangesException( String message )
    {
        super( message );
    }

    public InvalidChangesException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public InvalidChangesException( Throwable cause )
    {
        super( cause );
    }
}
