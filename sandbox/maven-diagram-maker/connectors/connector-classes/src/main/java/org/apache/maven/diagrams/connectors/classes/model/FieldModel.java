package org.apache.maven.diagrams.connectors.classes.model;

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
import java.util.EnumSet;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class FieldModel
{
    /**
     * Set of field (access) modifiers
     */
    private EnumSet<ModifierModel> modifiers;

    /**
     * Field name
     */
    private String name;

    /**
     * Field data type
     */
    private String type;

    /**
     * Set of field (access) modifiers
     */
    public EnumSet<ModifierModel> getModifiers()
    {
        return modifiers;
    }

    /**
     * Set of field (access) modifiers
     */
    public void setModifiers( EnumSet<ModifierModel> modifiers )
    {
        this.modifiers = modifiers;
    }

    /**
     * Gets the field name
     * 
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the field name
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * Gets the field data type
     * 
     * @return
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the field data type.
     * 
     * @param type
     */
    public void setType( String type )
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return modifiers + " " + type + " " + name;
    }

    /**
     * Translates the MethodModel into UML string representation
     * 
     * @param short_ -
     *            use short (simple / not qualified) class names
     * @return
     */
    public String toUMLString( boolean short_ )
    {
        StringBuffer res = new StringBuffer();
        if ( modifiers.contains( ModifierModel.PRIVATE ) )
            res.append( "- " );
        else if ( modifiers.contains( ModifierModel.PUBLIC ) )
            res.append( "+ " );
        else if ( modifiers.contains( ModifierModel.PROTECTED ) )
            res.append( "# " );
        else
            res.append( "~ " );

        res.append( name );
        res.append( ":" );
        if ( short_ )
            res.append( shortClassName( type ) );
        else
            res.append( type );

        return res.toString();
    }

    /**
     * TODO: Move to common implementation
     * 
     * @param className
     * @return
     */
    private String shortClassName( String className )
    {
        int last = className.lastIndexOf( '.' );
        return last >= 0 ? className.substring( last + 1 ) : className;
    }
}
