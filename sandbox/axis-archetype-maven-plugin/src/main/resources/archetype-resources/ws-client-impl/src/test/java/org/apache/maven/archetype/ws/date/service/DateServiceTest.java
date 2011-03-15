/*
 Copyright 2006 The Mojo Team (mojo.codehaus.org)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package org.apache.maven.archetype.ws.date.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;
import javax.xml.rpc.ServiceException;

/**
 *
 * @author rafale
 */
public class DateServiceTest extends junit.framework.TestCase
{
  
  public DateServiceTest (String testName)
  {
    super (testName);
  }
  
  public void testStub() throws MalformedURLException, ServiceException, RemoteException, InterruptedException
  {
    Thread.sleep (15000);
    URL url = new URL("http://localhost:9999/${artifactId}-webapp-0.1/services/DateServicePort");
    DateServiceLocator locator = new DateServiceLocator();
    org.apache.maven.archetype.ws.date.service.Date service = locator.getDateServicePort(url);
    long date = service.getCurrentDate ();
    System.err.println("date="+new Date(date));
    System.err.println("date="+service.formatDate (date,"yyyy-mm-dd"));
  }
  
}
