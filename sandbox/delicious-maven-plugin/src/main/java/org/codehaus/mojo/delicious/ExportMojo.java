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

import java.io.IOException;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

/**
 * Exports the links in the configured html page to the configured delicious account.
 * This is an easy way to transfer links from a browser to delicious - simply use
 * the 'export bookmarks...' option in your browser to create the links page and
 * supply it as a configuration parameter to this goal.
 * <p>
 * Note that each post takes at least one second to complete as a courtesy to the
 * delicious server.
 * <p>
 * Example standalone useage:<br>
 * mvn org.codehaus.mojo:delicious-maven-plugin:export\<br>
 * -Dserver=delicious -Durl=file:///users/jim/bookmarks.html -Dreplace=true
 * 
 * @goal export
 * @requiresProject false
 */
public class ExportMojo
    extends AbstractMojo
{
    /**
     * The Maven Wagon manager to use when obtaining server authentication details.
     *
     * @parameter expression = "${component.org.apache.maven.artifact.manager.WagonManager}"
     * @required
     * @readonly
     */
    private WagonManager wagonManager;

    /**
     * The server id to use when authenticating with the delicious server.
     * In your settings.xml file insert a server tag similar to the following:
     * <br>
     * <server>
     *   <id>delicious</id>
     *   <username>david</username>
     *   <password>pencil</password>
     * </server>
     *
     * @parameter expression="${server}"
     * @required
     */
    private String server;

    /**
     * The html page whose links are to be added to the delicious account.
     * Can be an http:// or file:// URL.
     * For example http://acme/page.html or file:///Users/dev/page.html
     * 
     * @parameter expression="${url}"
     */
    private String url;

    /**
     * Specify true if delicious posts with the same name should be replaced, false otherwise.
     * This is intended as a required parameter so that the account holder is always aware
     * of making this choice.
     * 
     * @parameter expression="${replace}"
     * @required
     */
    private Boolean replace;

    /**
     * The delicious service that carries out the commands.
     */
    private DeliciousService service;

    public ExportMojo()
    {
        this.service = null;
    }

    /**
     * Gets the authenticated delicious service.
     * @return
     */
    public DeliciousService getService()
    {
        if ( service == null )
        {
            service = new DeliciousService();
            AuthenticationInfo authInfo = wagonManager.getAuthenticationInfo( server );
            service.setUser( authInfo.getUserName(), authInfo.getPassword() );
        }

        return service;
    }

    /**
     * Exports the configured bookmarks to the delicious account.
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            getService().addBookmarks( createBookmarks(), replace );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "goal failed", e );
        }
        catch ( InterruptedException e )
        {
            throw new MojoExecutionException( "goal failed", e );
        }
    }

    private BookmarkGroup createBookmarks() throws IOException
    {
        BookmarkGroup group = new BookmarkGroup();
        group.addLinks(url);
        return group;
    }
}
