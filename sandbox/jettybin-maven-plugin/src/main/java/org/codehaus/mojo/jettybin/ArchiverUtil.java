package org.codehaus.mojo.jettybin;

/*
 * Copyright 2001-2006 The Codehaus.
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

import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.tar.TarArchiver;
import org.codehaus.plexus.archiver.tar.TarLongFileMode;
import org.codehaus.plexus.archiver.war.WarArchiver;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;

/**
 * ArchiverUtil 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class ArchiverUtil
{
    /**
     * Creates the necessary archiver to build the distribution file.
     * 
     * @param archiverManager the archiver manager to use.
     * @param file the file to glean the format off of.
     * @return archiver Archiver generated
     * @throws org.codehaus.plexus.archiver.ArchiverException
     * @throws org.codehaus.plexus.archiver.manager.NoSuchArchiverException
     */
    protected static Archiver createArchiver( ArchiverManager archiverManager, File file )
        throws ArchiverException, NoSuchArchiverException
    {
        String format = getFileExtention( file );

        Archiver archiver;
        if ( format.startsWith( "tar" ) )
        {
            archiver = createTarArchiver( archiverManager, format, "gnu" );
        }
        else if ( "war".equals( format ) )
        {
            archiver = createWarArchiver( archiverManager );
        }
        else
        {
            archiver = archiverManager.getArchiver( format );
        }

        return archiver;
    }

    public static String getFileExtention( File file )
    {
        String path = file.getAbsolutePath();

        String archiveExt = FileUtils.getExtension( path ).toLowerCase();

        if ( "gz".equals( archiveExt ) || "bz2".equals( archiveExt ) )
        {
            String[] tokens = StringUtils.split( path, "." );

            if ( tokens.length > 2 && "tar".equals( tokens[tokens.length - 2].toLowerCase() ) )
            {
                archiveExt = "tar." + archiveExt;
            }
        }

        return archiveExt;

    }

    private static Archiver createWarArchiver( ArchiverManager archiverManager )
        throws NoSuchArchiverException
    {
        WarArchiver warArchiver = (WarArchiver) archiverManager.getArchiver( "war" );
        warArchiver.setIgnoreWebxml( false ); // See MNG-1274

        return warArchiver;
    }

    private static Archiver createTarArchiver( ArchiverManager archiverManager, String format, String tarLongFileMode )
        throws NoSuchArchiverException, ArchiverException
    {
        TarArchiver tarArchiver = (TarArchiver) archiverManager.getArchiver( "tar" );
        int index = format.indexOf( '.' );
        if ( index >= 0 )
        {
            // TODO: this needs a cleanup in plexus archiver - use a real typesafe enum
            TarArchiver.TarCompressionMethod tarCompressionMethod = new TarArchiver.TarCompressionMethod();
            // TODO: this should accept gz and bz2 as well so we can skip over the switch
            String compression = format.substring( index + 1 );
            if ( "gz".equals( compression ) )
            {
                tarCompressionMethod.setValue( "gzip" );
            }
            else if ( "bz2".equals( compression ) )
            {
                tarCompressionMethod.setValue( "bzip2" );
            }
            else
            {
                // TODO: better handling
                throw new IllegalArgumentException( "Unknown compression format: " + compression );
            }
            tarArchiver.setCompression( tarCompressionMethod );
        }

        TarLongFileMode tarFileMode = new TarLongFileMode();

        tarFileMode.setValue( tarLongFileMode );

        tarArchiver.setLongfile( tarFileMode );

        return tarArchiver;
    }
}
