package org.apache.maven.diagrams.connectors.classes.graph;

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
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.diagrams.connectors.classes.model.FieldModel;
import org.apache.maven.diagrams.connectors.classes.model.MethodModel;
import org.apache.maven.diagrams.graph_api.Node;

/**
 * The class represents single Class as graph node (implementing Graph-api Node interface)
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public class ClassNode implements Node
{
    private String fullName;

    private boolean interface_;

    private List<MethodModel> methods;

    private List<FieldModel> fields;

    private List<FieldModel> properties;

    private String superclassName;

    private List<String> interfaceNames;

    public ClassNode()
    {
        interfaceNames = new LinkedList<String>();
        fields = new LinkedList<FieldModel>();
        methods = new LinkedList<MethodModel>();
        properties = new LinkedList<FieldModel>();
    }

    public ClassNode( String a_fullName )
    {
        fullName = a_fullName;
        interfaceNames = new LinkedList<String>();
        fields = new LinkedList<FieldModel>();
        methods = new LinkedList<MethodModel>();
        properties = new LinkedList<FieldModel>();
    }

    public String getId()
    {
        return getFull_name();
    }

    /*----------------------- Class name ----------------------*/

    public String getFull_name()
    {
        return fullName;
    }

    public void setClass_name( String fullName )
    {
        this.fullName = fullName;
    }

    public String getPackageName()
    {
        return packageName( fullName );
    }

    public String getSimpleName()
    {
        return simpleClassName( fullName );
    }

    /* ----------------------- superclass name ---------------------- */

    public String getSuperclassName()
    {
        return superclassName;
    }

    public void setSuperclassName( String superclassName )
    {
        this.superclassName = superclassName;
    }

    public String getSimpleSuperclassName()
    {
        return simpleClassName( superclassName );
    }

    /* ------------------------ interfaces --------------------------- */

    public List<String> getInterfaceNames()
    {
        return interfaceNames;
    }

    public void setInterfaceNames( List<String> interfaceNames )
    {
        this.interfaceNames = interfaceNames;
    }

    /* ----------------------- methods ----------------------------- */

    public List<MethodModel> getMethods()
    {
        return methods;
    }

    public void setMethods( List<MethodModel> methods )
    {
        this.methods = methods;
    }

    /* ------------------------- fields ------------------------------ */

    public List<FieldModel> getFields()
    {
        return fields;
    }

    public void setFields( List<FieldModel> fields )
    {
        this.fields = fields;
    }

    /* ---------------------- properties --------------------------- */

    public List<FieldModel> getProperties()
    {
        return properties;
    }

    public void setProperties( List<FieldModel> properties )
    {
        this.properties = properties;
    }

    /* ----------------------- interface ---------------------------- */

    public boolean isInterface()
    {
        return interface_;
    }

    public void setInterface( boolean interface_ )
    {
        this.interface_ = interface_;
    }

    /* ====================== helpers ================================ */

    /*
     * TODO: Move to single place
     */
    private static String packageName( String name )
    {
        int last = name.lastIndexOf( "." );
        return last > 0 ? name.substring( 0, last ) : "";
    }

    /*
     * TODO: Move to single place
     */
    private static String simpleClassName( String name )
    {
        int last = name.lastIndexOf( "." );
        return name.substring( last + 1 );
    }

    private String getEOL()
    {
        String sp = System.getProperty( "line.separator" );
        return sp == null ? "\n" : sp;
    }

    public String toString()
    {
        String EOL = getEOL();

        StringBuffer sb = new StringBuffer();

        sb.append( getFull_name() + " extends " + getSuperclassName() + " implemments " + getInterfaceNames() + EOL );

        sb.append( "PROPERTIES:" + EOL );
        for ( FieldModel fm : properties )
        {
            sb.append( " - " + fm.toString() + EOL );
        }
        sb.append( "FIELDS:" + EOL );
        for ( FieldModel fm : fields )
        {
            sb.append( " - " + fm.toString() + EOL );
        }
        sb.append( "METHODS:" + EOL );
        for ( MethodModel fm : methods )
        {
            sb.append( " - " + fm.toString() + EOL );
        }

        return sb.toString();
    }

    public void print( PrintStream printStream )
    {
        printStream.println( this.toString() );
    }
}
