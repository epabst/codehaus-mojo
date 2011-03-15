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
 * Check configuration, used by <code>check</code> goal.
 * 
 * @author <a href="mailto:alexandre.roman@gmail.com">Alexandre ROMAN</a>
 * @see EmmaCheckMojo
 */
public class CheckConfiguration
{
    /**
     * The lineRate.
     */
    private int lineRate;

    /**
     * The blockRate.
     */
    private int blockRate;

    /**
     * The methodRate.
     */
    private int methodRate;

    /**
     * The classRate.
     */
    private int classRate;

    /**
     * should we halt if the targets are not met.
     */
    private boolean haltOnFailure = true;

    /**
     * The regexes.
     */
    private Regex[] regexes = new Regex[0];

    /**
     * A regex.
     */
    public static class Regex
    {
        /**
         * The pattern.
         */
        private String pattern;

        /**
         * The lineRate.
         */
        private int lineRate;

        /**
         * The blockRate.
         */
        private int blockRate;

        /**
         * The methodRate.
         */
        private int methodRate;

        /**
         * The classRate.
         */
        private int classRate;

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
         * Sets the block rate.
         * 
         * @param blockRate the block rate.
         */
        public void setBlockRate( int blockRate )
        {
            this.blockRate = blockRate;
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
         * Sets the class rate.
         * 
         * @param classRate the class rate.
         */
        public void setClassRate( int classRate )
        {
            this.classRate = classRate;
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
         * Sets the line rate.
         * 
         * @param lineRate The line rate.
         */
        public void setLineRate( int lineRate )
        {
            this.lineRate = lineRate;
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
         * Sets the method rate.
         * 
         * @param methodRate the method rate.
         */
        public void setMethodRate( int methodRate )
        {
            this.methodRate = methodRate;
        }

        /**
         * gets the pattern.
         * 
         * @return the pattern.
         */
        public String getPattern()
        {
            return pattern;
        }

        /**
         * Sets the pattern.
         * 
         * @param pattern the pattern.
         */
        public void setPattern( String pattern )
        {
            this.pattern = pattern;
        }
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
     * Sets the block rate.
     * 
     * @param blockRate the block rate.
     */
    public void setBlockRate( int blockRate )
    {
        this.blockRate = blockRate;
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
     * Sets the class rate.
     * 
     * @param classRate the class rate.
     */
    public void setClassRate( int classRate )
    {
        this.classRate = classRate;
    }

    /**
     * Gets the halt on failure mode..
     * 
     * @return <code>true</code> if the build should be failed on failure.
     */
    public boolean isHaltOnFailure()
    {
        return haltOnFailure;
    }

    /**
     * Sets the halt on failure mode.
     * 
     * @param haltOnFailure the halt on failure mode.
     */
    public void setHaltOnFailure( boolean haltOnFailure )
    {
        this.haltOnFailure = haltOnFailure;
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
     * Sets the line rate.
     * 
     * @param lineRate The line rate.
     */
    public void setLineRate( int lineRate )
    {
        this.lineRate = lineRate;
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
     * Sets the method rate.
     * 
     * @param methodRate the method rate.
     */
    public void setMethodRate( int methodRate )
    {
        this.methodRate = methodRate;
    }

    /**
     * Gets the regexes.
     * 
     * @return the regexes.
     */
    public Regex[] getRegexes()
    {
        return regexes;
    }

    /**
     * Sets the regexes.
     * 
     * @param regexes The regexes.
     */
    public void setRegexes( Regex[] regexes )
    {
        this.regexes = regexes;
    }

}
