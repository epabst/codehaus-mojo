package org.apache.maven.wagon.providers.cifs;

/*
 * Copyright 2005-2006 The Apache Software Foundation.
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

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.InputData;
import org.apache.maven.wagon.OutputData;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.StreamWagon;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.resource.Resource;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class CifsWagon
    extends StreamWagon
{
    private String smbUrl;

    private SmbFile repositoryFile;

    private NtlmPasswordAuthentication ntlmPasswordAuthentication;

    public void fillInputData( InputData inputData )
        throws TransferFailedException, ResourceDoesNotExistException
    {
        Resource resource = inputData.getResource();

        String name = resource.getName();

        try
        {
            SmbFile file = getSmbFile( name );

            if ( !file.isFile() || !file.canRead() )
            {
                throw new ResourceDoesNotExistException( "Could not find file: '" + file.getURL().toExternalForm() + "'" );
            }

            InputStream inputStream = file.getInputStream();

            inputData.setInputStream( inputStream );
        }
        catch ( MalformedURLException e )
        {
            throw new TransferFailedException( "Could not make a abstract file name for the resource '" + name + "'.", e );
        }
        catch ( UnknownHostException e )
        {
            throw new TransferFailedException( "Could not make a abstract file name for the resource '" + name + "'.", e );
        }
        catch ( IOException e )
        {
            throw new TransferFailedException( "Error while opening input stream to the resource.", e );
        }
    }

    public void fillOutputData( OutputData outputData )
        throws TransferFailedException
    {
        Resource resource = outputData.getResource();

        String name = resource.getName();

        try
        {
            SmbFile file = getSmbFile( name );

            SmbFile parent = new SmbFile( file.getParent(), ntlmPasswordAuthentication );

            if ( !parent.exists() )
            {
                parent.mkdirs();
            }

            OutputStream outputStream = file.getOutputStream();

            outputData.setOutputStream( outputStream );
        }
        catch ( MalformedURLException e )
        {
            throw new TransferFailedException( "Could not make a abstract file name for the resource '" + name + "'.", e );
        }
        catch ( UnknownHostException e )
        {
            throw new TransferFailedException( "Could not make a abstract file name for the resource '" + name + "'.", e );
        }
        catch ( IOException e )
        {
            throw new TransferFailedException( "Error while opening output stream to the file.", e );
        }
    }

    public void openConnection()
        throws ConnectionException, AuthenticationException
    {
        // TODO: Possibly get this from the username if the username contains a '\': ITERA\trygve.laugstol
        String domain = null;

        String userName = getAuthenticationInfo().getUserName();

        if ( userName != null )
        {
            int index = userName.indexOf( '\\' );

            if ( index != -1 )
            {
                domain = userName.substring( 0, index );
                userName = userName.substring( index + 1 );
            }

            ntlmPasswordAuthentication = new NtlmPasswordAuthentication( domain,
                                                                         userName,
                                                                         getAuthenticationInfo().getPassword() );
        }

        SmbFile basedir;

        try
        {
            basedir = getSmbFile( "" );

            // This is here just to make sure that we've connected to the server.
            basedir.list();
        }
        catch ( MalformedURLException e )
        {
            throw new ConnectionException( "Error while connecting to the CIFS server.", e );
        }
        catch ( SmbAuthException e )
        {
            throw new AuthenticationException( "Error while logging in.", e );
        }
        catch ( SmbException e )
        {
            throw new ConnectionException( "Error while accessing the repository.", e );
        }
    }

    public void closeConnection()
    {
    }

    public boolean supportsDirectoryCopy()
    {
        return true;
    }

    public void putDirectory( File sourceDirectory, String destinationDirectory )
        throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
    {
        try
        {
            destinationDirectory = StringUtils.replace( destinationDirectory, "\\", "/" );

            SmbFile destinationSmbFile = getSmbFile( destinationDirectory );

            if ( !destinationSmbFile.exists() )
            {
                destinationSmbFile.mkdirs();
            }

            SmbFileUtils.copyDirectoryStructure( sourceDirectory, destinationSmbFile, ntlmPasswordAuthentication );
        }
        catch ( IOException e )
        {
            throw new TransferFailedException( "Error copying directory structure", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private SmbFile getSmbFile( String path )
        throws MalformedURLException
    {
        String host = getRepository().getHost();

        String basedir = getRepository().getBasedir();

        String url = "smb://" + host + basedir + "/" + path;

        return new SmbFile( url, ntlmPasswordAuthentication );
    }
}
