
package com.shape;


public class Rectangle
{
    private float Hypotenuse;
    private int Area;
    private int Height;
    private int Perimeter;
    private int Width;


    public int getArea(  )
    {
        this.Area = this.Width * this.Height;        return this.Area;    }


    public void setHeight( int height )
    {
        Height = height;    }


    public int getHeight(  )    {
        return this.Height;
    }


    public float getHypotenuse(  )   {
        this.Hypotenuse = (float) Math.sqrt( Math.pow( this.Height, 2.0 ) + Math.pow( this.Width, 2.0 ) );

        return this.Hypotenuse;
    }


    public int getPerimeter(  )
    {
        this.Perimeter = ( 2 * this.Width ) + ( 2 * this.Height );

        return this.Perimeter;    }

    public void setWidth( int width )    {
        Width = width;    }

    public int getWidth(  )    {
        return this.Width;
    }
    public void setWidthHeight( int width, int height )
    {
        setWidth( width );
        setHeight( height );
    }
}
