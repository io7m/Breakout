//********************************************************************************
// Vector2D.java
//
// (C) 2019 TerseWorks. All rights reserved.
// TerseWorks Public Code License (https://www.terseworks.com/licensing).
//
// Written by: John Meschke
// Description: A floating point vector or point in 2D space.
//********************************************************************************

package com.terseworks.math;

public class Vector2D
{
	public float x, y;

	public Vector2D()
	{
		this(0.0F, 0.0F);
	}

	public Vector2D(Vector2D vector)
	{
		this(vector.x, vector.y);
	}

	public Vector2D(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public void normalize()
	{
		double temp = (x * x) + (y * y);
		if (temp != 0.0)
		{
			temp = 1.0 / Math.sqrt(temp);
			x *= temp;
			y *= temp;
		}
		else
		{
			x = 1.0F;
			y = 0.0F;
		}
	}

	public float magnitude()
	{
		double temp = (x * x) + (y * y);
		temp = Math.sqrt(temp);

		return (float) temp;
	}
}
