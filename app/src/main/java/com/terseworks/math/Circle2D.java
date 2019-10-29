//********************************************************************************
// Circle2D.java
//
// (C) 2019 TerseWorks. All rights reserved.
// TerseWorks Public Code License (https://www.terseworks.com/licensing).
//
// Written by: John Meschke
// Description: A floating point circle in 2D space.
//********************************************************************************

package com.terseworks.math;

public class Circle2D
{
	public final Vector2D center;
	public float radius;

	public Circle2D()
	{
		this(0.0F, 0.0F, 0.0F);
	}

	public Circle2D(Circle2D circle)
	{
		this(circle.center.x, circle.center.y, circle.radius);
	}

	public Circle2D(Vector2D center, float radius)
	{
		this(center.x, center.y, radius);
	}

	public Circle2D(float centerX, float centerY, float radius)
	{
		center = new Vector2D(centerX, centerY);
		this.radius = radius;
	}
}
