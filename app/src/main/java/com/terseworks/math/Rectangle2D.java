//********************************************************************************
// Rectangle2D.java
//
// (C) 2019 TerseWorks. All rights reserved.
// TerseWorks Public Code License (https://www.terseworks.com/licensing).
//
// Written by: John Meschke
// Description: A floating point rectangle in 2D space.
//********************************************************************************

package com.terseworks.math;

public class Rectangle2D
{
	public final Vector2D min, max;

	public Rectangle2D()
	{
		this(0.0F, 0.0F, 0.0F, 0.0F);
	}

	public Rectangle2D(Rectangle2D rectangle)
	{
		this(rectangle.min.x, rectangle.min.y, rectangle.max.x, rectangle.max.y);
	}

	public Rectangle2D(Vector2D min, Vector2D max)
	{
		this(min.x, min.y, max.x, max.y);
	}

	public Rectangle2D(float minX, float minY, float maxX, float maxY)
	{
		min = new Vector2D(minX, minY);
		max = new Vector2D(maxX, maxY);
	}
}
