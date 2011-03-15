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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * Various utility methods that glue the classes together.
 * 
 * @author ashley
 */
public class Util {

	/**
	 * Obtains a reader for the given URL. The returned reader should be closed.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static Reader getReader(String url) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        if (connection instanceof HttpURLConnection) {
            ((HttpURLConnection) connection).setRequestMethod("GET");
        }
		connection.connect();
		return new InputStreamReader(connection.getInputStream());
	}

	/**
	 * Obtains a reader for the given already existing file. The returned reader
	 * should be closed.
	 * 
     * @deprecated call getReader with a file:// url
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Reader getFileReader(String fileName)
			throws FileNotFoundException {
		FileReader reader;

		File file = new File(fileName);
		if (file.exists() && file.isFile()) {
			reader = new FileReader(file);
		} else {
			reader = null;
		}

		return reader;
	}

	/**
	 * Gets a reader for a resource on the classpath.
	 * 
	 * @param path
	 * @return
	 */
	public static Reader getResourceReader(String path) {
		return new InputStreamReader(Util.class.getResourceAsStream(path));
	}

	/**
	 * Logs the message contained in the given reader.
	 * @param logger
	 * @param priority
	 * @param reader
	 * @throws IOException
	 */
	public static void logStream(Logger logger, Priority priority, Reader reader) throws IOException {
		BufferedReader buffered = new BufferedReader(reader);
		String line;

		while ((line = buffered.readLine()) != null) {
			logger.log(priority, line);
		}
	}
}
