package org.codehaus.mojo.solaris;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@codehaus.org">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SimplePrototypeTest
    extends PlexusTestCase
{
    public void testDefaultSettings()
        throws Exception
    {
        File pkgRoot = getTestFile( "src/test/pkgs/pkg-1" );
        File pkgPrototype = getTestFile( "src/test/pkgs/pkg-1.prototype" );
        File generatedPrototype = getTestFile( "target/pkg-1.prototype" );

        PrototypeGenerator generator = (PrototypeGenerator) lookup( PrototypeGenerator.ROLE );

        Iterator iterator = generator.generatePrototype( pkgRoot, null,
                                                         Defaults.directoryDefaults(),
                                                         Defaults.fileDefaults() );

        GeneratePrototypeMojo.writePrototype( generatedPrototype, iterator );

        String expected = FileUtils.fileRead( pkgPrototype );
        String actual = FileUtils.fileRead( generatedPrototype );

        assertEquals( expected, actual );
    }

    public void testSettings()
        throws Exception
    {
        File pkgRoot = getTestFile( "src/test/pkgs/pkg-2" );
        File pkgPrototype = getTestFile( "src/test/pkgs/pkg-2.prototype" );
        File generatedPrototype = getTestFile( "target/pkg-2.prototype" );

        PrototypeGenerator generator = (PrototypeGenerator) lookup( PrototypeGenerator.ROLE );

        List prototypeEntries = new ArrayList();

        prototypeEntries.add( new DirectoryCollection( null, "?", "?", "?", Boolean.FALSE,
                                                       new String[]{"opt", "opt/jb"}, null) );
        prototypeEntries.add( new DirectoryCollection( null, null, "myservice", "myservice", Boolean.FALSE,
                                                       new String[]{"opt/jb/tips-handler/**"}, null ));

        prototypeEntries.add( new FileCollection( null, null, "myservice", "myservice", Boolean.FALSE,
                                                 new String[]{"opt/jb/tips-handler/**"}, null ) );
        prototypeEntries.add( new FileCollection( "conf", null, "myservice", "myservice", Boolean.FALSE,
                                                 new String[]{"opt/jb/tips-handler/etc/*"}, null ) );
        prototypeEntries.add( new FileCollection( null, null, "myservice", "myservice", Boolean.FALSE,
                                                 new String[]{"opt/jb/tips-handler/etc/*.template"}, null ) );
        prototypeEntries.add( new FileCollection( null, "0755", "myservice", "myservice", Boolean.FALSE,
                                                 new String[]{"opt/jb/tips-handler/server/bin/*"}, null ) );

        FileEntry entry = new FileEntry();
        entry.setPath("/opt/jb/tips-handler/server/repo/xpp3/xpp3_min/1.1.3.4.O/xpp3_min-1.1.3.4.O.jar");
        entry.setMode("0555");
        entry.setUser("myservice");
        entry.setGroup("myservice");

        prototypeEntries.add(entry);

        Iterator iterator = generator.generatePrototype( pkgRoot, prototypeEntries,
                                                         Defaults.directoryDefaults(), Defaults.fileDefaults());

        GeneratePrototypeMojo.writePrototype( generatedPrototype, iterator );

        String expected = FileUtils.fileRead( pkgPrototype );
        String actual = FileUtils.fileRead( generatedPrototype );

        assertEquals( expected, actual );
    }
}
