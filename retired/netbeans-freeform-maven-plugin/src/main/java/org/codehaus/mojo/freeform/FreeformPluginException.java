package org.codehaus.mojo.freeform;

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
 * This class is the only exception in the Netbeans Freeform plugin.
 *
 * @author <a href="mailto:raphaelpieroni@gmail.com">Raphaël Piéroni</a>
 */
public class FreeformPluginException
    extends Exception
{
    /**
     * This constructor takes the Exception message as a parameter.
     *
     * @param msg The Exception message.
     */
    public FreeformPluginException( String msg )
    {
        super( msg );
    }

    /**
     * This constructor takes the Exception message and root cause as parameters.
     *
     * @param msg   The Exception message.
     * @param cause The Exception root cause.
     */
    public FreeformPluginException(
        String msg,
        Throwable cause
    )
    {
        super( msg, cause );
    }
}
