package org.codehaus.mojo.minijar.resource;

/*
 * Copyright 2005 The Apache Software Foundation.
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.commons.io.IOUtils;
import org.vafer.dependency.resources.ResourceHandler;
import org.vafer.dependency.resources.Version;
import org.vafer.dependency.utils.Jar;

public final class LicenseHandler implements ResourceHandler
{
	private File licensesFile;
	private FileOutputStream licensesOutputStream;
	
	public void onStartProcessing( JarOutputStream pOutput )
		throws IOException
	{
	}
	
	public void onStartJar( Jar pJar, JarOutputStream pOutput )
		throws IOException
	{		
	}
	
	public InputStream onResource(Jar jar, String oldName, String newName, Version[] versions, InputStream inputStream )
		throws IOException
	{
		final String s = oldName.toLowerCase();

		if ( "meta-inf/license.txt".equals(s) || "meta-inf/license".equals(s) || "meta-inf/notice.txt".equals(s) || "meta-inf/notice".equals(s))
		{
			System.out.println(this + " found resource " + oldName);

			if (licensesFile == null)
			{
				licensesFile = File.createTempFile("minijar", "license");
				licensesFile.deleteOnExit();
			}
			
			if (licensesOutputStream == null)
			{
				licensesOutputStream = new FileOutputStream( licensesFile );				
			}
			
			return new FilterInputStream( inputStream ) {

				public int read()
					throws IOException
				{
					int r = super.read();
					if ( r > 0 )
					{
						licensesOutputStream.write(r);
					}
					return r;
				}

				public int read( byte[] b, int off, int len )
					throws IOException
				{
					int r = super.read(b, off, len);
					if ( r > 0 )
					{
						licensesOutputStream.write(b, off, r);						
					}
					return r;
				}

				public int read( byte[] b ) throws IOException
				{
					int r = super.read(b);
					if ( r > 0 )
					{
						licensesOutputStream.write( b, 0, r );
					}
					return r;
				}				
			};
		}

		return inputStream;
	}

	public void onStopJar( Jar pJar, JarOutputStream pOutput )
		throws IOException
	{		
	}

	public void onStopProcessing( JarOutputStream pOutput )
		throws IOException
	{
		if ( licensesOutputStream == null )
		{
			// no license information aggregated
			return;
		}

		IOUtils.closeQuietly( licensesOutputStream );
		
		
		// insert aggregated license information into new jar
		
		final FileInputStream licensesInputStream = new FileInputStream( licensesFile );
		
		pOutput.putNextEntry( new JarEntry("LICENSE.txt") );
		
		IOUtils.copy( licensesInputStream, pOutput );
		
		IOUtils.closeQuietly( licensesInputStream );
		
	}

}
