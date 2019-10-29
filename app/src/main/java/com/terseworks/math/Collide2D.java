//********************************************************************************
// Collide2D.java
//
// (C) 2019 TerseWorks. All rights reserved.
// TerseWorks Public Code License (https://www.terseworks.com/licensing).
//
// Written by: John Meschke
// Description: Methods used to calculate contact information as a result of the
//              collision between two shapes. All methods assume valid shapes
//              (example: unit normals, positive radii, etc.).
//********************************************************************************

package com.terseworks.math;

public class Collide2D
{
	private Collide2D()
	{
		// Do not instantiate.
	}

	/*
	Calculate the contact information between a point and normalized halfspace.
	The contact argument will be modified.
	 */
	public static void pointInPlane(Vector2D point, Plane2D plane, Contact2D contact)
	{
		float distance = (point.x * plane.normal.x) + (point.y * plane.normal.y) + plane.offset;
		if (distance < 0.0F)
		{
			contact.normal.x = -plane.normal.x;
			contact.normal.y = -plane.normal.y;
		}
		else
		{
			contact.normal.x = plane.normal.x;
			contact.normal.y = plane.normal.y;
		}

		contact.tangent.x = contact.normal.y;
		contact.tangent.y = -contact.normal.x;
		contact.depth = 0.0F;
	}

	/*
	Calculate the contact information between a point and normalized halfspace.
	The contact argument will be modified.
	 */
	public static void pointInHalfspace(Vector2D point, Plane2D halfspace, Contact2D contact)
	{
		contact.normal.x = halfspace.normal.x;
		contact.normal.y = halfspace.normal.y;
		contact.tangent.x = contact.normal.y;
		contact.tangent.y = -contact.normal.x;

		float distance = (point.x * halfspace.normal.x) + (point.y * halfspace.normal.y) + halfspace.offset;
		contact.depth = Math.max(0.0F, -distance);
	}

	/*
	Calculate the contact information between a point and circle.
	The contact argument will be modified.
	 */
	public static void pointInCircle(Vector2D point, Circle2D circle, Contact2D contact)
	{
		float distanceX = point.x - circle.center.x;
		float distanceY = point.y - circle.center.y;
		contact.normal.x = distanceX;
		contact.normal.y = distanceY;
		contact.normal.normalize();
		contact.tangent.x = contact.normal.y;
		contact.tangent.y = -contact.normal.x;

		float distance = (contact.normal.x * distanceX) + (contact.normal.y * distanceY);
		contact.depth = Math.max(0.0F, circle.radius - distance);
	}

	/*
	Calculate the contact information between a point and rectangle.
	The contact argument will be modified.
	 */
	public static void pointInRectangle(Vector2D point, Rectangle2D rectangle, Contact2D contact)
	{
		float distanceX1 = point.x - rectangle.min.x;
		float distanceY1 = point.y - rectangle.min.y;
		float distanceX2 = rectangle.max.x - point.x;
		float distanceY2 = rectangle.max.y - point.y;

		float distanceX = Math.min(distanceX1, distanceX2);
		float distanceY = Math.min(distanceY1, distanceY2);
		float distance = Math.min(distanceX, distanceY);
		
		if (distanceX < distanceY)
		{
			contact.normal.x = (distanceX1 < distanceX2) ? -1.0F : 1.0F;
			contact.normal.y = 0.0F;
		}
		else
		{
			contact.normal.x = 0.0F;
			contact.normal.y = (distanceY1 < distanceY2) ? -1.0F : 1.0F;
		}
		
		contact.tangent.x = contact.normal.y;
		contact.tangent.y = -contact.normal.x;
		contact.depth = Math.max(0.0F, distance);
	}

	/*
	Calculate the contact information between a circle and normalized plane.
	The contact argument will be modified.
	 */
	public static void circleInPlane(Circle2D circle, Plane2D plane, Contact2D contact)
	{
		float distance = (circle.center.x * plane.normal.x) + (circle.center.y * plane.normal.y) + plane.offset;
		if (distance < 0.0F)
		{
			contact.normal.x = -plane.normal.x;
			contact.normal.y = -plane.normal.y;
			contact.depth = Math.max(0.0F, circle.radius + distance);
		}
		else
		{
			contact.normal.x = plane.normal.x;
			contact.normal.y = plane.normal.y;
			contact.depth = Math.max(0.0F, circle.radius - distance);
		}

		contact.tangent.x = contact.normal.y;
		contact.tangent.y = -contact.normal.x;
	}

	/*
	Calculate the contact information between a circle and normalized halfspace.
	The contact argument will be modified.
	 */
	public static void circleInHalfspace(Circle2D circle, Plane2D halfspace, Contact2D contact)
	{
		contact.normal.x = halfspace.normal.x;
		contact.normal.y = halfspace.normal.y;
		contact.tangent.x = contact.normal.y;
		contact.tangent.y = -contact.normal.x;

		float distance = (circle.center.x * halfspace.normal.x) + (circle.center.y * halfspace.normal.y) + halfspace.offset;
		contact.depth = Math.max(0.0F, circle.radius - distance);
	}

	/*
	Calculate the contact information between a circle and circle.
	The contact argument will be modified.
	 */
	public static void circleInCircle(Circle2D circle1, Circle2D circle2, Contact2D contact)
	{
		float distanceX = circle1.center.x - circle2.center.x;
		float distanceY = circle1.center.y - circle2.center.y;
		contact.normal.x = distanceX;
		contact.normal.y = distanceY;
		contact.normal.normalize();
		contact.tangent.x = contact.normal.y;
		contact.tangent.y = -contact.normal.x;

		float distance = (contact.normal.x * distanceX) + (contact.normal.y * distanceY);
		contact.depth = Math.max(0.0F, (circle1.radius + circle2.radius) - distance);
	}

	/*
	Calculate the contact information between a circle and rectangle.
	The contact argument will be modified.
	 */
	public static void circleInRectangle(Circle2D circle, Rectangle2D rectangle, Contact2D contact)
	{
		boolean xTest = (circle.center.x < rectangle.min.x || circle.center.x > rectangle.max.x);
		boolean yTest = (circle.center.y < rectangle.min.y || circle.center.y > rectangle.max.y);
		if (xTest && yTest)
		{
			float closestX = circle.center.x;
			float closestY = circle.center.y;
			if (closestX < rectangle.min.x) closestX = rectangle.min.x;
			if (closestY < rectangle.min.y) closestY = rectangle.min.y;
			if (closestX > rectangle.max.x) closestX = rectangle.max.x;
			if (closestY > rectangle.max.y) closestY = rectangle.max.y;

			float distanceX = circle.center.x - closestX;
			float distanceY = circle.center.y - closestY;
			contact.normal.x = distanceX;
			contact.normal.y = distanceY;
			contact.normal.normalize();

			float distance = (contact.normal.x * distanceX) + (contact.normal.y * distanceY);
			contact.depth = Math.max(0.0F, circle.radius - distance);
		}
		else
		{
			float distanceX1 = (circle.center.x + circle.radius) - rectangle.min.x;
			float distanceY1 = (circle.center.y + circle.radius) - rectangle.min.y;
			float distanceX2 = rectangle.max.x - (circle.center.x - circle.radius);
			float distanceY2 = rectangle.max.y - (circle.center.y - circle.radius);
			
			float distanceX = Math.min(distanceX1, distanceX2);
			float distanceY = Math.min(distanceY1, distanceY2);
			float distance = Math.min(distanceX, distanceY);

			if (distanceX < distanceY)
			{
				contact.normal.x = (distanceX1 < distanceX2) ? -1.0F : 1.0F;
				contact.normal.y = 0.0F;
			}
			else
			{
				contact.normal.x = 0.0F;
				contact.normal.y = (distanceY1 < distanceY2) ? -1.0F : 1.0F;
			}
			contact.depth = Math.max(0.0F, distance);
		}

		contact.tangent.x = contact.normal.y;
		contact.tangent.y = -contact.normal.x;
	}

	/*
	Calculate the contact information between a rectangle and normalized plane.
	The contact argument will be modified.
	 */
	public static void rectangleInPlane(Rectangle2D rectangle, Plane2D plane, Contact2D contact)
	{
		float rectangleExtentX = (rectangle.max.x - rectangle.min.x) * 0.5F;
		float rectangleExtentY = (rectangle.max.y - rectangle.min.y) * 0.5F;
		float rectangleCenterX = rectangle.min.x + rectangleExtentX;
		float rectangleCenterY = rectangle.min.y + rectangleExtentY;

		float projection = (rectangleExtentX * Math.abs(plane.normal.x)) + (rectangleExtentY * Math.abs(plane.normal.y));
		float distance = (rectangleCenterX * plane.normal.x) + (rectangleCenterY * plane.normal.y) + plane.offset;
		float absoluteDistance = Math.abs(distance);
		
		if (distance < 0.0F)
		{
			contact.normal.x = -plane.normal.x;
			contact.normal.y = -plane.normal.y;
		}
		else
		{
			contact.normal.x = plane.normal.x;
			contact.normal.y = plane.normal.y;
		}
		
		contact.tangent.x = contact.normal.y;
		contact.tangent.y = -contact.normal.x;
		contact.depth = Math.max(0.0F, projection - absoluteDistance);
	}

	/*
	Calculate the contact information between a rectangle and normalized halfspace.
	The contact argument will be modified.
	 */
	public static void rectangleInHalfspace(Rectangle2D rectangle, Plane2D halfspace, Contact2D contact)
	{
		float rectangleExtentX = (rectangle.max.x - rectangle.min.x) * 0.5F;
		float rectangleExtentY = (rectangle.max.y - rectangle.min.y) * 0.5F;
		float rectangleCenterX = rectangle.min.x + rectangleExtentX;
		float rectangleCenterY = rectangle.min.y + rectangleExtentY;

		float projection = (rectangleExtentX * Math.abs(halfspace.normal.x)) + (rectangleExtentY * Math.abs(halfspace.normal.y));
		float distance = (rectangleCenterX * halfspace.normal.x) + (rectangleCenterY * halfspace.normal.y) + halfspace.offset;
		
		contact.normal.x = halfspace.normal.x;
		contact.normal.y = halfspace.normal.y;
		contact.tangent.x = contact.normal.y;
		contact.tangent.y = -contact.normal.x;
		contact.depth = Math.max(0.0F, projection - distance);
	}

	/*
	Calculate the contact information between a rectangle and rectangle.
	The contact argument will be modified.
	 */
	public static void rectangleInRectangle(Rectangle2D rectangle1, Rectangle2D rectangle2, Contact2D contact)
	{
		float distanceX1 = rectangle1.max.x - rectangle2.min.x;
		float distanceY1 = rectangle1.max.y - rectangle2.min.y;
		float distanceX2 = rectangle2.max.x - rectangle1.min.x;
		float distanceY2 = rectangle2.max.y - rectangle1.min.y;

		float distanceX = Math.min(distanceX1, distanceX2);
		float distanceY = Math.min(distanceY1, distanceY2);
		float distance = Math.min(distanceX, distanceY);
		
		if (distanceX < distanceY)
		{
			contact.normal.x = (distanceX1 < distanceX2) ? -1.0F : 1.0F;
			contact.normal.y = 0.0F;
		}
		else
		{
			contact.normal.x = 0.0F;
			contact.normal.y = (distanceY1 < distanceY2) ? -1.0F : 1.0F;
		}
		
		contact.tangent.x = contact.normal.y;
		contact.tangent.y = -contact.normal.x;
		contact.depth = Math.max(0.0F, distance);
	}
}
