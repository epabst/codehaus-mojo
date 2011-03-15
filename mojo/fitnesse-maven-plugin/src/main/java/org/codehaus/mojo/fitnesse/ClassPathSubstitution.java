package org.codehaus.mojo.fitnesse;

/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

public class ClassPathSubstitution
{

    /**
     * String to search.
     * 
     * @parameter
     * @required
     */
    private String search;

    /**
     * String to replace.
     * 
     * @parameter
     * @required
     */
    private String replaceWith;

    public ClassPathSubstitution()
    {
    }

    public ClassPathSubstitution( String search, String replaceWith )
    {
        super();
        this.search = search;
        this.replaceWith = replaceWith;
    }

    public String getReplaceWith()
    {
        return replaceWith;
    }

    public String getSearch()
    {
        return search;
    }

}
