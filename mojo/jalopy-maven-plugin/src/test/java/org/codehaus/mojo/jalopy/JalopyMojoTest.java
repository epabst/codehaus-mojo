package org.codehaus.mojo.jalopy;

/*
 * Copyright 2001-2005 The Codehaus.
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

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @description Test case for JalopyMojo.java
 * @author <a href="mailto:jruiz@exist.com">Johnny R. Ruiz III</a>
 * @version $Id$
 */
public class JalopyMojoTest
    extends AbstractMojoTestCase
{
    JalopyMojo jalopy;

    String baseDir = System.getProperty( "basedir" );

    protected void setUp()
        throws Exception
    {
    	super.setUp();
    	File config = new File( getBasedir(), "src/test/plugin-configs/jalopy-plugin-config.xml" );
        jalopy = (JalopyMojo) lookupMojo("format", config);
        baseDir = PlexusTestCase.getBasedir();
    }

    public void testIfInputFilesIsNotYetJalopyFormatted()
        throws IOException
    {
        recopyInputFiles();

        assertTrue( compareTextFiles( new File( baseDir, "src/test/resources/input/test/Circle.java" ),
                                      new File( baseDir, "target/test-classes/input-bak/Circle.java" ) ) );

        assertTrue( compareTextFiles( new File( baseDir, "src/test/resources/input/src/Rectangle.java" ),
                                      new File( baseDir, "target/test-classes/input-bak/Rectangle.java" ) ) );

        assertTrue( compareTextFiles( new File( baseDir, "src/test/resources/input/test/Point.java" ),
                                      new File( baseDir, "target/test-classes/input-bak/Point.java" ) ) );

        assertTrue( compareTextFiles( new File( baseDir, "src/test/resources/input/src/Point.java" ),
                                      new File( baseDir, "target/test-classes/input-bak/Point.java" ) ) );
    }

    public void testExecute()
    {
        jalopy.setFailOnError( true );

        jalopy.setFileFormat( "auto" );

        jalopy.setSourceDirectory( new File( baseDir, "src/test/resources/input/src" ) );

        jalopy.setSrcExcludesPattern( "Point.java" );

        jalopy.setSrcIncludesPattern( "Rectangle.java" );

        jalopy.setTestSourceDirectory( new File( baseDir, "src/test/resources/input/test" ) );

        jalopy.setTestExcludesPattern( "" );

        jalopy.setTestIncludesPattern( "*.java" );

        jalopy.setHistory( "none" );

        try
        {
            jalopy.execute();

            assertFalse( compareTextFiles( new File( baseDir, "src/test/resources/input/test/Circle.java" ),
                                           new File( baseDir, "target/test-classes/input-bak/Circle.java" ) ) );

            assertFalse( compareTextFiles( new File( baseDir, "src/test/resources/input/src/Rectangle.java" ),
                                           new File( baseDir, "target/test-classes/input-bak/Rectangle.java" ) ) );

            assertFalse( compareTextFiles( new File( baseDir, "src/test/resources/input/test/Point.java" ),
                                           new File( baseDir, "target/test-classes/input-bak/Point.java" ) ) );

            assertTrue( compareTextFiles( new File( baseDir, "src/test/resources/input/src/Point.java" ),
                                          new File( baseDir, "target/test-classes/input-bak/Point.java" ) ) );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public void testIfSourceInputFilesWasFormatted()
        throws IOException
    {
        assertTrue( compareTextFiles( new File( baseDir, "src/test/resources/validator/Maven1-Jalopy-Converted-Rectangle.java" ),
                                      new File( baseDir,
                                                "target/test-classes/validator/Maven1-Jalopy-Converted-Rectangle.java" ) ) );
    }

    public void testIfExcludedFilesNotFormatted()
        throws IOException
    {
        assertTrue( compareTextFiles( new File( baseDir, "src/test/resources/input/src/Point.java" ),
                                      new File( baseDir, "target/test-classes/input-bak/Point.java" ) ) );
    }

    public void testIfTestInputFilesAllFormatted()
        throws IOException
    {
        assertTrue( compareTextFiles( new File( baseDir, "src/test/resources/validator/Maven1-Jalopy-Converted-Circle.java" ),
                                      new File( baseDir,
                                                "target/test-classes/validator/Maven1-Jalopy-Converted-Circle.java" ) ) );

        assertTrue( compareTextFiles( new File( baseDir, "src/test/resources/validator/Maven1-Jalopy-Converted-Point.java" ),
                                      new File( baseDir,
                                                "target/test-classes/validator/Maven1-Jalopy-Converted-Point.java" ) ) );

        assertTrue( compareTextFiles( new File( baseDir, "src/test/resources/validator/Maven1-Jalopy-Converted-Rectangle.java" ),
                                      new File( baseDir,
                                                "target/test-classes/validator/Maven1-Jalopy-Converted-Rectangle.java" ) ) );

        deleteInputFiles();

    }

    public void testSetGetFileFormat()
    {
        jalopy.setFileFormat( "fileFormat" );

        assertEquals( "fileFormat", jalopy.getFileFormat() );
    }

    public void testSetIsFailOnError()
    {
        jalopy.setFailOnError( true );

        assertEquals( true, jalopy.isFailOnError() );
    }

    public void testSetGetSourceDirectory()
    {
        jalopy.setSourceDirectory( new File( "src/test" ) );

        assertEquals( new File( "src/test" ), jalopy.getSourceDirectory() );
    }

    public void testSetGetSrcIncludesPattern()
    {
        jalopy.setSrcIncludesPattern( "**.*" );

        assertEquals( "**.*", jalopy.getSrcIncludesPattern() );
    }

    public void testSetGetSrcExcludesPattern()
    {
        jalopy.setSrcExcludesPattern( "**.pro" );

        assertEquals( "**.pro", jalopy.getSrcExcludesPattern() );
    }

    public void testSetGetHistory()
    {
        jalopy.setHistory( "history" );

        assertEquals( "history", jalopy.getHistory() );
    }

    private boolean compareTextFiles( File f1, File f2 )
        throws FileNotFoundException, IOException
    {
        String text1 = getTextContents( f1 );

        String text2 = getTextContents( f2 );

        StringTokenizer tokenizer1 = new StringTokenizer( text1, "\n" );

        StringTokenizer tokenizer2 = new StringTokenizer( text2, "\n" );

        if ( tokenizer1.countTokens() != tokenizer2.countTokens() )
        {
            return false;
        }

        while ( tokenizer1.hasMoreTokens() )
        {
            if ( !tokenizer1.nextToken().equalsIgnoreCase( tokenizer2.nextToken() ) )
            {
                return false;
            }
        }

        return true;
    }

    private String getTextContents( File f )
        throws FileNotFoundException, IOException
    {
        FileInputStream fIn = new FileInputStream( f );

        byte[] fBytes = new byte[fIn.available()];

        fIn.read( fBytes );

        fIn.close();

        return new String( fBytes );
    }

    private void recopyInputFiles()
        throws IOException
    {
        FileUtils.copyDirectory( new File( baseDir, "src/test/resources/input-bak/" ),
                                 new File( baseDir, "src/test/resources/input/src" ) );

        FileUtils.copyDirectory( new File( baseDir, "src/test/resources/input-bak/" ),
                                 new File( baseDir, "src/test/resources/input/test" ) );

        List fileList = FileUtils.getFiles( new File( baseDir, "src/test/resources/input-bak/" ), "*.java", "" );

        for ( int ctr = 0; ctr < fileList.size(); ctr++ )
        {
            break;
        }
    }

    private void deleteInputFiles()
        throws IOException
    {
        FileUtils.deleteDirectory( baseDir + "/src/test/resources/input" );
    }
}
