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

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "org.codehaus.mojo.delicious.messages";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		return RESOURCE_BUNDLE.getString(key);
	}

	public static String getCommandHelp() {
		MessageFormat format = new MessageFormat(getString("DeliciousService.commandHelp"));
		return format.format(new Object[] {getAddLinksCommand()});
	}

	public static String getAddLinksCommand() {
		return getString("DeliciousService.addLinks");
	}

	public static String getDeliciousUrl() {
		return getString("LiveApi.deliciousUrl");
	}

	public static String getDeliciousHost() {
		return getString("LiveApi.deliciousHost");
	}

	public static String getServiceUnavailable() {
		return getString("LiveApi.serviceUnavailable");
	}

	public static String getCommandSuccess() {
		return getString("DeliciousService.commandSuccess");
	}

	public static String getExceptionMessage() {
		return getString("Main.exception");
	}

	public static Long getCourtesyTime() {
		return new Long(getString("DeliciousService.courtesyTime"));
	}
}
