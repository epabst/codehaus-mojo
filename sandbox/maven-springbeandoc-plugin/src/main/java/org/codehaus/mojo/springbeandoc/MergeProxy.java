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
 * Combination of a proxy and its target.
 *
 * @author Markus Knittig
 * @version 27 June 2009
 */
public class MergeProxy {

	/**
	 * One or more proxy beans defined by a Regular Expressions that matches
	 * either id/name or class attributes.
	 */
	private String proxy;

	/**
	 * The name of the property that has a reference to its target.
	 */
	private String target;

	/**
	 * Gets proxy property.
	 *
	 * @return Returns the proxy.
	 */
	public String getProxy() {
		return this.proxy;
	}

	/**
	 * Gets target property.
	 *
	 * @return Returns the target.
	 */
	public String getTarget() {
		return this.target;
	}

	/**
	 * Sets proxy property.
	 *
	 * @param v
	 *            The proxy to set.
	 */
	public void setProxy(final String v) {
		this.proxy = v;
	}

	/**
	 * Sets target property.
	 *
	 * @param v
	 *            The target to set.
	 */
	public void setTarget(final String v) {
		this.target = v;
	}

}
