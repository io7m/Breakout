//********************************************************************************
// Contact2D.java
//
// (C) 2019 TerseWorks. All rights reserved.
// TerseWorks Public Code License (https://www.terseworks.com/licensing).
//
// Written by: John Meschke
// Description: Contains contact information about the result of a collision
//              between shapes in 2D space.
//********************************************************************************

package com.terseworks.math;

public class Contact2D
{
	public final Vector2D normal, tangent;
	public float depth;

	public Contact2D()
	{
		this(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	public Contact2D(Contact2D contact)
	{
		this(contact.normal.x, contact.normal.y, contact.tangent.x, contact.tangent.y, contact.depth);
	}

	public Contact2D(Vector2D normal, Vector2D tangent, float depth)
	{
		this(normal.x, normal.y, tangent.x, tangent.y, depth);
	}

	public Contact2D(float normalX, float normalY, float tangentX, float tangentY, float depth)
	{
		normal = new Vector2D(normalX, normalY);
		tangent = new Vector2D(tangentX, tangentY);
		this.depth = depth;
	}

	/*
	Flips the contact in the opposite direction.
	 */
	public void flip()
	{
		normal.x = -normal.x;
		normal.y = -normal.y;
		tangent.x = -tangent.x;
		tangent.y = -tangent.y;
	}
}
