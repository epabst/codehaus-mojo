package org.apache.maven.wagon.providers.cifs;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 */

import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import jcifs.smb.SmbFile;
import jcifs.smb.NtlmPasswordAuthentication;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SmbFileUtils
{
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * Copy bytes from an <code>InputStream</code> to an <code>OutputStream</code>.
     */
    public static void copy( final InputStream input, final OutputStream output )
        throws IOException
    {
        copy( input, output, DEFAULT_BUFFER_SIZE );
    }

    /**
     * Copy bytes from an <code>InputStream</code> to an <code>OutputStream</code>.
     *
     * @param bufferSize Size of internal buffer to use.
     */
    public static void copy( final InputStream input, final OutputStream output, final int bufferSize )
        throws IOException
    {
        final byte[] buffer = new byte[bufferSize];
        int n;
        while ( -1 != ( n = input.read( buffer ) ) )
        {
            output.write( buffer, 0, n );
        }
    }

    /**
     * Copy file from source to destination. The directories up to <code>destination</code> will be
     * created if they don't already exist. <code>destination</code> will be overwritten if it
     * already exists.
     *
     * @param source                     An existing non-directory <code>File</code> to copy bytes from.
     * @param destination                A non-directory <code>File</code> to write bytes to (possibly
     *                                   overwriting).
     * @param ntlmPasswordAuthentication
     * @throws IOException                   if <code>source</code> does not exist, <code>destination</code> cannot be
     *                                       written to, or an IO error occurs during copying.
     * @throws java.io.FileNotFoundException if <code>destination</code> is a directory
     *                                       (use {@link #copyFileToDirectory}).
     */
    public static void copyFile( final File source, final SmbFile destination,
                                 NtlmPasswordAuthentication ntlmPasswordAuthentication )
        throws IOException
    {
        //check source exists
        if ( !source.exists() )
        {
            final String message = "File " + source + " does not exist";
            throw new IOException( message );
        }

        //does destinations directory exist ?
        SmbFile parent = new SmbFile( destination.getParent(), ntlmPasswordAuthentication );
        if ( !parent.exists() )
        {
            parent.mkdirs();
        }

        //make sure we can write to destination
        if ( destination.exists() && !destination.canWrite() )
        {
            final String message = "Unable to open file " + destination + " for writing.";
            throw new IOException( message );
        }

        InputStream input = null;
        OutputStream output = null;
        try
        {
            input = new FileInputStream( source );
            output = destination.getOutputStream();
            IOUtil.copy( input, output );
        }
        finally
        {
            IOUtil.close( input );
            IOUtil.close( output );
        }

        if ( source.length() != destination.length() )
        {
            final String message = "Failed to copy full contents from " + source + " to " + destination;
            throw new IOException( message );
        }
    }

    /**
     * Copy file from source to destination. If <code>destinationDirectory</code> does not exist, it
     * (and any parent directories) will be created. If a file <code>source</code> in
     * <code>destinationDirectory</code> exists, it will be overwritten.
     *
     * @param source                     An existing <code>File</code> to copy.
     * @param destinationDirectory       A directory to copy <code>source</code> into.
     * @param ntlmPasswordAuthentication
     * @throws java.io.FileNotFoundException if <code>source</code> isn't a normal file.
     * @throws IllegalArgumentException      if <code>destinationDirectory</code> isn't a directory.
     * @throws IOException                   if <code>source</code> does not exist, the file in
     *                                       <code>destinationDirectory</code> cannot be written to, or an IO error occurs during copying.
     */
    public static void copyFileToDirectory( final File source, final SmbFile destinationDirectory,
                                            NtlmPasswordAuthentication ntlmPasswordAuthentication )
        throws IOException
    {
        if ( destinationDirectory.exists() && !destinationDirectory.isDirectory() )
        {
            throw new IllegalArgumentException( "Destination is not a directory" );
        }

        copyFile( source, new SmbFile( destinationDirectory, source.getName() ), ntlmPasswordAuthentication );
    }

    /**
     * Copies a entire directory structure.
     * <p/>
     * Note:
     * <ul>
     * <li>It will include empty directories.
     * <li>The <code>sourceDirectory</code> must exists.
     * </ul>
     *
     * @param sourceDirectory
     * @param destinationDirectory
     * @param ntlmPasswordAuthentication
     * @throws java.io.IOException
     */
    public static void copyDirectoryStructure( File sourceDirectory, SmbFile destinationDirectory,
                                               NtlmPasswordAuthentication ntlmPasswordAuthentication )
        throws IOException
    {
        if ( !sourceDirectory.exists() )
        {
            throw new IOException( "Source directory doesn't exists: '" + sourceDirectory.getAbsolutePath() + "'." );
        }

        File[] files = sourceDirectory.listFiles();

        for ( int i = 0; i < files.length; i++ )
        {
            File file = files[i];

            SmbFile destination = new SmbFile( destinationDirectory + "/", file.getName(), ntlmPasswordAuthentication );

            if ( file.isFile() )
            {
                destination = new SmbFile( destination.getParent(), ntlmPasswordAuthentication );

                copyFileToDirectory( file, destination, ntlmPasswordAuthentication );
            }
            else if ( file.isDirectory() )
            {
                if ( !destination.exists() )
                {
                    destination.mkdirs();
                }

                copyDirectoryStructure( file, destination, ntlmPasswordAuthentication );
            }
            else
            {
                throw new IOException( "Unknown file type: " + file.getAbsolutePath() );
            }
        }
    }
}
