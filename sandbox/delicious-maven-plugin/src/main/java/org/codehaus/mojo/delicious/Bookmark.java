package org.codehaus.mojo.delicious;

/*
 * Copyright 2005 Ashley Williams.
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

public class Bookmark
{
    private String location;

    private String title;

    private String tags;

    private String comments;

    public Bookmark()
    {
        location = "";
        title = "";
        tags = "";
        comments = "";
    }

    public Bookmark( String location, String title, String tags )
    {
        this.location = location;
        this.title = title;
        this.tags = tags;
    }

    public Bookmark( String title, String location, String tags, String comments )
    {
        this.location = location;
        this.title = title;
        this.tags = tags;
        this.comments = comments;
    }

    public String getTags()
    {
        return tags;
    }

    public void setTags( String tags )
    {
        this.tags = tags;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation( String url )
    {
        this.location = url;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public boolean equals( Object obj )
    {
        boolean isEquals;
        Bookmark bookmark = (Bookmark) obj;
        if ( location.equals( bookmark.getLocation() ) && tags.equals( bookmark.getTags() ) )
        {
            isEquals = true;
        }
        else
        {
            isEquals = false;
        }
        return isEquals;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments( String comments )
    {
        this.comments = comments;
    }

    /**
     * Adds the tag if it doesn't already exist.
     * @param tag
     */
    public void addTag( String tag )
    {
        if ( !containsTag( tag ) )
        {
            tags += " " + tag;
        }
    }

    /**
     * Returns true if the tag is found anywhere in the list of tags,
     * may be surrounded by white space or at start/end of tag list.
     * @param tag
     * @return
     */
    public boolean containsTag( String tag )
    {
        //TODO doesn't work yet - can't use regex for java 1.2, or even contains
        return tags.indexOf(tag) > -1;
    }

    public String toString()
    {
        return location + "\n" + title + "\n" + tags + "\n" + comments;
    }
    
}
