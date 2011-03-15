package org.codehaus.mojo.hibernate2;

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

import net.sf.hibernate.tool.hbm2java.CodeGenerator;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.hibernate2.beans.CommonOperationsBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @goal code-generate
 *
 * @requiresDependencyResolution
 *
 * @description A Maven 2.0 Hibernate plugin for schema export
 *
 * @phase generate-sources
 *
 * @version $Id$
 */
public class CodeGenerateMojo
    extends CommonOperationsBean
{
    /**
     * @parameter default-value="src/main/generated-sources"
     * @required
     */
    private String outputDirectory;

    public void execute()
        throws MojoExecutionException
    {
        ArrayList argList = new ArrayList();

        argList.add( "--output=" + outputDirectory );

        File hbmFiles[] = getIncludeFiles();

        argList.addAll( getFilenames( hbmFiles ) );

        CodeGenerator.main( (String[]) argList.toArray( new String[0] ) );
    }

    protected List getFilenames( File files[] )
    {
        ArrayList list = new ArrayList();

        for ( int i = 0; i < files.length; i++ )
        {
            File f = files[ i ];
            list.add( f.getAbsolutePath() );
        }

        return list;
    }
}
