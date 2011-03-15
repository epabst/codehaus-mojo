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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Handles the underlying communication with the delicious REST api.
 * @author ashley
 *
 */
public class LiveConnection implements DeliciousConnection {
	public int executeMethod(HttpClient client, GetMethod httpMethod) throws IOException, HttpException {
		return client.executeMethod(httpMethod);
	}
}
