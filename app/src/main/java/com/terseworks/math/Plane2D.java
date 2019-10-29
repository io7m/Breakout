//********************************************************************************
// Plane2D.java
//
// (C) 2019 TerseWorks. All rights reserved.
// TerseWorks Public Code License (https://www.terseworks.com/licensing).
//
// Written by: John Meschke
// Description: A floating point plane or halfspace in 2D space. Takes the form of
//              (normal.x * X) + (normal.y * Y) + offset = 0.
//********************************************************************************

package com.terseworks.math;

public class Plane2D
{
	public final Vector2D normal;
	public float offset;

	public Plane2D()
	{
		this(0.0F, 0.0F, 0.0F);
	}

	public Plane2D(Plane2D plane)
	{
		this(plane.normal.x, plane.normal.y, plane.offset);
	}

	public Plane2D(Vector2D normal, float offset)
	{
		this(normal.x, normal.y, offset);
	}

	public Plane2D(float normalX, float normalY, float offset)
	{
		normal = new Vector2D(normalX, normalY);
		this.offset = offset;
	}

	public void calculatePlaneLeft(Vector2D point1, Vector2D point2)
	{
		normal.x = point1.y - point2.y;
		normal.y = point2.x - point1.x;
		normal.normalize();
		offset = 0.0F - ((normal.x * point1.x) + (normal.y * point1.y));
	}

	public void calculatePlaneRight(Vector2D point1, Vector2D point2)
	{
		normal.x = point2.y - point1.y;
		normal.y = point1.x - point2.x;
		normal.normalize();
		offset = 0.0F - ((normal.x * point1.x) + (normal.y * point1.y));
	}
}
