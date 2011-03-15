package org.codehaus.mojo.springbeandoc;

/*
   The MIT License
   .
   Copyright (c) 2009, Markus Knittig
   .
   Permission is hereby granted, free of charge, to any person obtaining a copy of
   this software and associated documentation files (the "Software"), to deal in
   the Software without restriction, including without limitation the rights to
   use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
   of the Software, and to permit persons to whom the Software is furnished to do
   so, subject to the following conditions:
   .
   The above copyright notice and this permission notice shall be included in all
   copies or substantial portions of the Software.
   .
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
   SOFTWARE.
*/

/**
 * Combination of a bean name or class pattern and its colour.
 *
 * @author Markus Knittig
 * @version 27 June 2009
 */
public class ColourBean {

    /**
     * One or more proxy beans defined by a Regular Expressions that matches
     * either id/name or class attributes.
     */
    private String pattern;

    /**
     * Sets the colour of the matching beans (e.g. #f0f080 or blue)
     */
    private String colour;

    /**
     * Gets pattern property.
     *
     * @return Returns the pattern.
     */
    public String getPattern() {
        return this.pattern;
    }

    /**
     * Gets colour property.
     *
     * @return Returns the colour.
     */
    public String getColour() {
        return this.colour;
    }

    /**
     * Sets pattern property.
     *
     * @param v The pattern to set.
     */
    public void setPattern(final String v) {
        this.pattern = v;
    }

    /**
     * Sets colour property.
     *
     * @param v The colour to set.
     */
    public void setColour(final String v) {
        this.colour = v;
	}

}
