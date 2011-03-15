package org.apache.maven.diagrams.connectors.classes.model;

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
import java.util.EnumSet;

import org.objectweb.asm.Opcodes;

/**
 * The enum represents access modifier to the class/field/method
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public enum ModifierModel
{
    STATIC, PUBLIC, PROTECTED, PRIVATE;

    /**
     * Transforms maps of com.sun.org.apache.bcel.internal.Opcodes.ACC_... modifiers into the EnumSet.
     * 
     * @param access -
     *            combination of modifier's Opcodes
     * @return
     */
    public static EnumSet<ModifierModel> accessContantsToModifiers( int access )
    {
        EnumSet<ModifierModel> result = EnumSet.noneOf( ModifierModel.class );

        if ( ( access & Opcodes.ACC_PRIVATE ) == Opcodes.ACC_PRIVATE )
            result.add( PRIVATE );
        if ( ( access & Opcodes.ACC_PUBLIC ) == Opcodes.ACC_PUBLIC )
            result.add( PUBLIC );
        if ( ( access & Opcodes.ACC_PROTECTED ) == Opcodes.ACC_PROTECTED )
            result.add( PROTECTED );
        if ( ( access & Opcodes.ACC_STATIC ) == Opcodes.ACC_STATIC )
            result.add( STATIC );

        return result;
    }
}
