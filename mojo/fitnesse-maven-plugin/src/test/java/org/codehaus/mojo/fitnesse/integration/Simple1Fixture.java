package org.codehaus.mojo.fitnesse.integration;

import fit.ColumnFixture;

/***************************************************************************
 * Copyright 2005 Philippe Kernevez All rights reserved.                   *
 * Please look at license.txt for more license detail.                     *
 **************************************************************************/

public class Simple1Fixture extends ColumnFixture
{
    
    public int value;
    
    public boolean check()
    {
        Simple1 tVar = new Simple1();
        tVar.i = value;
        tVar.someMethod2();
        return tVar.i>value;
    }

}
