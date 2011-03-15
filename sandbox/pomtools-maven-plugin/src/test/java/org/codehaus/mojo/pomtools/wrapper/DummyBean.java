package org.codehaus.mojo.pomtools.wrapper;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import java.util.List;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class DummyBean 
{
    private String stringField;
    private boolean booleanField;
    private List listFields;
    private Object objectField;
    
    private String defaultingField = "defaultValue";
    
    private String privateField;
    private String protectedField;
    
    private String changeMyLabel;
    
    private String ignoreMe;
    
    public DummyBean()
    {
        setPrivateField( null );
    }
    
    public boolean isBooleanField()
    {
        return booleanField;
    }

    public void setBooleanField( boolean booleanField )
    {
        this.booleanField = booleanField;
    }

    public List getListFields()
    {
        return listFields;
    }

    public void setListFields( List listField )
    {
        this.listFields = listField;
    }

    public String getStringField()
    {
        return stringField;
    }

    public void setStringField( String stringField )
    {
        this.stringField = stringField;
    }

    public String getPrivateField()
    {
        return privateField;
    }

    private void setPrivateField( String privateField )
    {
        this.privateField = privateField;
    }

    protected String getProtectedField()
    {
        return protectedField;
    }

    protected void setProtectedField( String protectedField )
    {
        this.protectedField = protectedField;
    }

    public String getChangeMyLabel()
    {
        return changeMyLabel;
    }

    public void setChangeMyLabel( String zzzRenameMe )
    {
        this.changeMyLabel = zzzRenameMe;
    }

    public String getIgnoreMe()
    {
        return ignoreMe;
    }

    public void setIgnoreMe( String ignoreMe )
    {
        this.ignoreMe = ignoreMe;
    }

    public Object getObjectField()
    {
        return objectField;
    }

    public void setObjectField( Object objectField )
    {
        this.objectField = objectField;
    }

    public String getDefaultingField()
    {
        return defaultingField;
    }

    public void setDefaultingField( String defaultingField )
    {
        this.defaultingField = defaultingField;
    }


}