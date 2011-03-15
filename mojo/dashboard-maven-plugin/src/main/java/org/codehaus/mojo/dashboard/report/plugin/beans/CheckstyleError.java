package org.codehaus.mojo.dashboard.report.plugin.beans;

/*
 * Copyright 2006 David Vicente
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

import java.util.regex.Pattern;

/**
 * Checkstyle error statistic class.
 *
 * @author <a href="srivollet@objectif-informatique.fr">Sylvain Rivollet</a>
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 */
public class CheckstyleError implements Cloneable
{
    /** Regexp to find 'xxx' experssion. */
    private static final Pattern REGEXP_QUOTE = Pattern.compile( "'.*?'" );

    /** Regexp to find "is nnn" expression. */
    private static final Pattern REGEXP_IS_NUMBER = Pattern.compile( "is (\\d)+" );

    /** Regexp to find "nnn times" expression. */
    private static final Pattern REGEXP_NUMBER_TIMES = Pattern.compile( "(\\d)+ times" );

    /** Regexp to find "xxx" expression. */
    private static final Pattern REGEXP_DOUBELQUOTE = Pattern.compile( "\".*?\"" );

    /** Regexp to find [xxx] expression. */
    private static final Pattern REGEXP_BRAKET = Pattern.compile( "\\[.*?\\]" );

    /**
     * the Checkstyle class name
     */
    private String nameClass;

    /**
     * nombre de fois qu'apparait le "typeErreurs"
     */
    private int nbIteration;

    /**
     * type de classe de test qui apparait dans le fichier checkstyle-result.xml
     */
    private String type;

    /**
     * message associé à l'erreur
     */
    private String message;

    /**
     * Default constructor
     */
    public CheckstyleError()
    {
        this.nbIteration = 1;
    }

    /**
     * @param type
     * @param nameClass
     * @param message
     * @param nbIteration
     */
    public CheckstyleError( String type, String nameClass, String message, int nbIteration )
    {
        this.type = type;
        this.nameClass = nameClass;
        this.message = message;
        this.nbIteration = nbIteration;
    }

    /**
     * @param object
     */
    public CheckstyleError( CheckstyleError object )
    {
        this.type = object.getType();
        this.nameClass = object.getNameClass();
        this.message = object.getMessage();
        this.nbIteration = object.getNbIteration();
    }

    public String getNameClass()
    {
        return this.nameClass;
    }

    public int getNbIteration()
    {
        return this.nbIteration;
    }

    public String getType()
    {
        return this.type;
    }

    public String getMessage()
    {
        return this.message;
    }

    public void setNameClass( String nameClass )
    {
        this.nameClass = nameClass;
    }

    public void setNbIteration( int nbIteration )
    {
        this.nbIteration = nbIteration;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public void setMessage( String message )
    {
        this.message = this.messageTreatment( message );
    }

    public boolean equals( Object o )
    {
        if ( !( o instanceof CheckstyleError ) )
        {
            return false;
        }
        else
        {
            CheckstyleError checkError = new CheckstyleError( (CheckstyleError) o );
            return ( this.message == null && checkError.getMessage() == null )
                            || ( this.message != null && this.message.equals( checkError.getMessage() ) );
        }
    }

//    public int hashCode()
//    {
//        if ( this.message == null )
//        {
//            return 0;
//        }
//        else
//        {
//            return this.message.hashCode();
//        }
//    }

    public void increment()
    {
        this.nbIteration++;
    }

    public String toString()
    {
        return "[" + this.type + " ; " + this.nameClass + " ; " + this.message + " ; " + this.nbIteration + "]";
    }

    private String messageTreatment( String message )
    {
        String finalMessage = REGEXP_QUOTE.matcher( message ).replaceAll( "'X'" );

        finalMessage = REGEXP_IS_NUMBER.matcher( finalMessage ).replaceFirst( "is 'n'" );

        finalMessage = REGEXP_NUMBER_TIMES.matcher( finalMessage ).replaceFirst( "'n' times" );

        finalMessage = REGEXP_DOUBELQUOTE.matcher( finalMessage ).replaceAll( "\"X\"" );

        finalMessage = REGEXP_BRAKET.matcher( finalMessage ).replaceAll( "[...]" );

        return finalMessage;
    }

    /**
     *
     */
    protected Object clone()
    {
        Object clone = null;
        try
        {
            clone = super.clone();
        }
        catch ( CloneNotSupportedException e )
        {
            System.err.println( "CheckstyleError can't clone" );
        }
        return clone;
    }
}
