package org.apache.maven.changes;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

/**
 * An action contained in a {@link Release} of a <tt>changes.xml</tt> file.
 *
 * @author <a href="snicoll@apache.org">Stephane Nicoll</a>
 * @version $Id$
 */
public final class Action
{
    private final String author;

    private final String issueId;

    private final ActionType type;

    private final String description;

    private final String dueTo;

    private final String dueToEmail;

    public Action( String author, String issueId, ActionType type, String description, String dueTo, String dueToEmail )
    {
        this.author = author;
        this.issueId = issueId;
        this.type = type;
        this.description = description;
        this.dueTo = dueTo;
        this.dueToEmail = dueToEmail;
    }

    /**
     * Returns the author of the action.
     *
     * @return the author
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * Returns the issue id in the issue tracker
     * system. Returns <tt>null</tt> if no issue id
     * has been specified.
     *
     * @return the issueId id
     */
    public String getIssueId()
    {
        return issueId;
    }

    /**
     * Returns the {@link ActionType}.
     *
     * @return the action type
     */
    public ActionType getType()
    {
        return type;
    }

    /**
     * Returns the description.
     *
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Returns the contributor of the action. Returns
     * <tt>null</tt> if there is no contributor.
     *
     * @return the contributor or null
     */
    public String getDueTo()
    {
        return dueTo;
    }

    /**
     * Returns the email of the contributor. Returns
     * <tt>null</tt> if there is no contributor or
     * if the contributor's email is not specified
     *
     * @return the contributor's email or null
     */
    public String getDueToEmail()
    {
        return dueToEmail;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( "[" ).append( getType() ).append( "] by[" ).append( getAuthor() ).append( "] :" ).append(
            getDescription() );
        return sb.toString();
    }
}
