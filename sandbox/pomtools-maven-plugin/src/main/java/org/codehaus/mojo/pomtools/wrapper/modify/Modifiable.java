package org.codehaus.mojo.pomtools.wrapper.modify;

/*
 * Copyright 2005-2006 The Apache Software Foundation.
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

/** Interface to represent an object which supports modification.
 * Allows for chaining of Modifiable objects so that a parent object 
 * will know when a child has been modified.
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public interface Modifiable
{
    boolean isModified();
    
    void setModified( boolean modified );
    
    Modifiable addChild( Modifiable chile );
}
