package com.shape;

import java.io.File;
import java.lang.Math;

public class Circle    extends Point
{
    public final float PI = 3.1415926535897932385f;
    public float Circumference;    public int Diameter;
    private int Radius;

    public float getCircumference(  )    {
        Circumference = 2 * ( Radius * PI );

        return Circumference;    }

    public int getDiameter(  )    {
        Diameter = Radius * 2;

        return Diameter;    }

    public void setRadius( int intRadius )
    {
        this.Radius = intRadius;    }

    public int getRadius(  )
    {        return this.Radius;
    }

    public static void main( String[] args )    {
        System.out.println( "Main Program for Junit Sample triggered!" );    }
}
