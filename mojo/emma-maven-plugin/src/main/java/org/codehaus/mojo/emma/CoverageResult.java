package org.codehaus.mojo.emma;

/*
 * The MIT License
 *
 * Copyright (c) 2007-8, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * Coverage result.
 * 
 * @author <a href="mailto:alexandre.roman@gmail.com">Alexandre ROMAN</a>
 */
class CoverageResult
{
    /**
     * Result type.
     */
    public static class Type
    {
        /**
         * Overall coverage.
         */
        public static final Type ALL = new Type( 0 );

        /**
         * Class coverage.
         */
        public static final Type CLASS = new Type( 2 );

        /**
         * Method coverage.
         */
        public static final Type METHOD = new Type( 3 );

        /**
         * Package coverage.
         */
        public static final Type PACKAGE = new Type( 1 );

        /**
         * The code.
         */
        private final int id;

        /**
         * Creates a new Type instance.
         * 
         * @param id the code.
         */
        private Type( final int id )
        {
            this.id = id;
        }

        /**
         * Compares Type instances.
         * 
         * @param obj the object to compare with.
         * @return <code>true</code> if the object is equal to this instance.
         */
        public boolean equals( Object obj )
        {
            if ( obj == null || !( obj instanceof Type ) )
            {
                return false;
            }
            if ( obj == this )
            {
                return true;
            }
            return id == ( (Type) obj ).id;
        }

        /**
         * Calculates the hashcode of this Type instance.
         * 
         * @return the hashcode of this Type instance.
         */
        public int hashCode()
        {
            return id;
        }

        /**
         * Converts this instance into its String representation.
         * 
         * @return The String representation of this instance.
         */
        public String toString()
        {
            switch ( id )
            {
                case 0:
                    return "all";
                case 1:
                    return "package";
                case 2:
                    return "class";
                case 3:
                    return "method";
                default:
                    return "(unknown type)";
            }
        }
    }

    /**
     * Marker value for when a rate is unknown.
     */
    public static final int UNKNOWN_RATE = -1;

    /**
     * The block rate.
     */
    private int blockRate = UNKNOWN_RATE;

    /**
     * The class rate.
     */
    private int classRate = UNKNOWN_RATE;

    /**
     * The line rate.
     */
    private int lineRate = UNKNOWN_RATE;

    /**
     * The method rate.
     */
    private int methodRate = UNKNOWN_RATE;

    /**
     * The name.
     */
    private final String name;

    /**
     * The type.
     */
    private final Type type;

    /**
     * Creates a new CoverageResult instance.
     */
    public CoverageResult()
    {
        this.type = Type.ALL;
        this.name = "(all classes)";
    }

    /**
     * Creates a new CoverageResult instance.
     * 
     * @param type The type.
     * @param name The name.
     */
    public CoverageResult( final Type type, final String name )
    {
        if ( type == null )
        {
            throw new IllegalArgumentException( "type is required" );
        }
        if ( name == null )
        {
            throw new IllegalArgumentException( "name is required" );
        }
        this.type = type;
        this.name = name;
    }

    /**
     * gets the block rate.
     * 
     * @return the block rate.
     */
    public int getBlockRate()
    {
        return blockRate;
    }

    /**
     * Gets the class rate.
     * 
     * @return the class rate.
     */
    public int getClassRate()
    {
        return classRate;
    }

    /**
     * Gets the line rate.
     * 
     * @return the line rate.
     */
    public int getLineRate()
    {
        return lineRate;
    }

    /**
     * Gets the method rate.
     * 
     * @return the method rate.
     */
    public int getMethodRate()
    {
        return methodRate;
    }

    /**
     * Gets the name.
     * 
     * @return the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the type.
     * 
     * @return the type.
     */
    public Type getType()
    {
        return type;
    }

    /**
     * Sets the block rate.
     * 
     * @param blockRate the block rate.
     */
    public void setBlockRate( int blockRate )
    {
        this.blockRate = blockRate;
    }

    /**
     * Sets the class rate.
     * 
     * @param classRate the class rate.
     */
    public void setClassRate( int classRate )
    {
        this.classRate = classRate;
    }

    /**
     * Sets the line rate.
     * 
     * @param lineRate The line rate.
     */
    public void setLineRate( int lineRate )
    {
        this.lineRate = lineRate;
    }

    /**
     * Sets the method rate.
     * 
     * @param methodRate the method rate.
     */
    public void setMethodRate( int methodRate )
    {
        this.methodRate = methodRate;
    }

    /**
     * Gets the String representation of this result.
     * 
     * @return the String representation of this result.
     */
    public String toString()
    {
        return type + " " + name + "[classRate=" + classRate + ", methodRate=" + methodRate + ", blockRate="
            + blockRate + ", lineRate=" + lineRate + "]";
    }
}
