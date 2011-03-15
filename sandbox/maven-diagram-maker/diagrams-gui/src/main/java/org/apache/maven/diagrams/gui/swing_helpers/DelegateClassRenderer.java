package org.apache.maven.diagrams.gui.swing_helpers;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class DelegateClassRenderer implements ListCellRenderer
{

    private ListCellRenderer lcr;

    private ObjectToStringConverter<Object> objectToStringConverter;

    @SuppressWarnings( "unchecked" )
    public DelegateClassRenderer( ListCellRenderer a_lcr, ObjectToStringConverter<?> obToStringConverter )
    {
        lcr = a_lcr;
        objectToStringConverter = (ObjectToStringConverter<Object>) obToStringConverter;
    }

    public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected,
                                                   boolean cellHasFocus )
    {
        return lcr.getListCellRendererComponent( list, objectToStringConverter.convert( value ), index, isSelected,
                                                 cellHasFocus );
    }
}
