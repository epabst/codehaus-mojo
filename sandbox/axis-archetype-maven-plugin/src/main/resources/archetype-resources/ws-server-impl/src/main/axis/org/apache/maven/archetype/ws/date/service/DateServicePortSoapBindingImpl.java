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

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateServicePortSoapBindingImpl
implements org.apache.maven.archetype.ws.date.service.Date, org.apache.maven.archetype.ws.date.Date
{
    public java.lang.String formatDate (long in0, java.lang.String in1)
    throws java.rmi.RemoteException
    {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(in1);
        return simpleDateFormat.format (new Date(in0));
    }

    public long getCurrentDate ()
    throws java.rmi.RemoteException
    {
        return new Date().getTime ();
    }
}
