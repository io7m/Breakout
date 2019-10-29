//********************************************************************************
// Closest2D.java
//
// (C) 2019 TerseWorks. All rights reserved.
// TerseWorks Public Code License (https://www.terseworks.com/licensing).
//
// Written by: John Meschke
// Description: Closest point methods for 2D geometry. All methods assume valid
//              shapes (example: unit normals, positive radii, etc.).
//********************************************************************************

package com.terseworks.math;

public class Closest2D
{
	private Closest2D()
	{
		// Do not instantiate.
	}

	/*
	Get the closest point to the line segment using the given point.
	The closestPoint argument will be modified.
	 */
	public static void pointInSegment(Vector2D point, Vector2D segmentA, Vector2D segmentB, Vector2D closestPoint)
	{
		float directionX = segmentB.x - segmentA.x;
		float directionY = segmentB.y - segmentA.y;
		float directionSquared = (directionX * directionX) + (directionY + directionY);
		float distanceX = point.x - segmentA.x;
		float distanceY = point.y - segmentA.y;
		float projection = (distanceX * directionX) + (distanceY * directionY);

		float t = projection / directionSquared;
		if (t < 0.0F) t = 0.0F;
		if (t > 1.0F) t = 1.0F;

		closestPoint.x = segmentA.x + (directionX * t);
		closestPoint.y = segmentA.y + (directionY * t);
	}

	/*
	Get the closest point to the normalized plane using the given point.
	The closestPoint argument will be modified.
	 */
	public static void pointInPlane(Vector2D point, Plane2D plane, Vector2D closestPoint)
	{
		float t = (point.x * plane.normal.x) + (point.y * plane.normal.y) + plane.offset;

		closestPoint.x = point.x - (plane.normal.x * t);
		closestPoint.y = point.y - (plane.normal.y * t);
	}

	/*
	Get the closest point to the normalized halfspace using the given point.
	The closestPoint argument will be modified.
	 */
	public static void pointInHalfspace(Vector2D point, Plane2D halfspace, Vector2D closestPoint)
	{
		float t = (point.x * halfspace.normal.x) + (point.y * halfspace.normal.y) + halfspace.offset;

		if (t <= 0.0F)
		{
			closestPoint.x = point.x;
			closestPoint.y = point.y;
		}
		else
		{
			closestPoint.x = point.x - (halfspace.normal.x * t);
			closestPoint.y = point.y - (halfspace.normal.y * t);
		}
	}

	/*
	Get the closest point to the circle using the given point.
	The closestPoint argument will be modified.
	 */
	public static void pointInCircle(Vector2D point, Circle2D circle, Vector2D closestPoint)
	{
		float distanceX = point.x - circle.center.x;
		float distanceY = point.y - circle.center.y;
		float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
		float radiusSquared = circle.radius * circle.radius;

		if (distanceSquared <= radiusSquared)
		{
			closestPoint.x = point.x;
			closestPoint.y = point.y;
		}
		else
		{
			float discriminant = (distanceSquared != 0.0F) ? radiusSquared / distanceSquared : 0.0F;
			float t = (float) Math.sqrt(discriminant);

			closestPoint.x = circle.center.x + (distanceX * t);
			closestPoint.y = circle.center.y + (distanceY * t);
		}
	}

	/*
	Get the closest point to the rectangle using the given point.
	The closestPoint argument will be modified.
	 */
	public static void pointInRectangle(Vector2D point, Rectangle2D rectangle, Vector2D closestPoint)
	{
		closestPoint.x = point.x;
		closestPoint.y = point.y;
		if (closestPoint.x < rectangle.min.x) closestPoint.x = rectangle.min.x;
		if (closestPoint.y < rectangle.min.y) closestPoint.y = rectangle.min.y;
		if (closestPoint.x > rectangle.max.x) closestPoint.x = rectangle.max.x;
		if (closestPoint.y > rectangle.max.y) closestPoint.y = rectangle.max.y;
	}
}