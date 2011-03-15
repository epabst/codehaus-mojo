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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

/**
 * Indexes the generated site links to the configured delicious account.
 * Note that each post takes at least one second to complete as a courtesy to the
 * delicious server.
 * 
 * @goal index
 */
public class IndexMojo
    extends AbstractMojo
{
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Directory containing the generated project sites and report distributions.
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     */
    private File siteDir;

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
     * @parameter expression="delicious"
     * @required
     */
    private String server;

    /**
     * The Maven Wagon manager to use when obtaining server authentication details.
     *
     * @parameter expression = "${component.org.apache.maven.artifact.manager.WagonManager}"
     * @required
     * @readonly
     */
    private WagonManager wagonManager;

    /**
     * The delicious service that carries out the commands.
     */
    private DeliciousService service;

    public IndexMojo()
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
            getService().addBookmarks( createBookmarks(), Boolean.TRUE );
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

    /**
     * Creates the list of bookmarks for the sites pages.
     * @return
     * @throws IOException
     */
    private BookmarkGroup createBookmarks()
        throws IOException
    {
        BookmarkGroup group = new BookmarkGroup();
        File[] pages = findSitePages();
        for ( int i = 0; i < pages.length; i++ )
        {
            File page = pages[i];
            group.addLinks( page );
            group.addTag( project.getGroupId() );
            group.addTag( project.getArtifactId() );
            group.addTag( project.getVersion() );
            group.addTag( "maven" );
        }

        return group;
    }

    /**
     * Returns the html pages under the site directory.
     * @return
     */
    private File[] findSitePages()
    {
        return siteDir.listFiles( new FileFilter()
        {
            public boolean accept( File pathname )
            {
                return pathname.isFile();
            }
        } );
    }

}
