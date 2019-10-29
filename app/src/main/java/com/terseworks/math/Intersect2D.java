//********************************************************************************
// Intersect2D.java
//
// (C) 2019 TerseWorks. All rights reserved.
// TerseWorks Public Code License (https://www.terseworks.com/licensing).
//
// Written by: John Meschke
// Description: Methods used to test intersection of 2D geometry. All methods
//              assume valid shapes (example: unit normals, positive radii, etc.).
//********************************************************************************

package com.terseworks.math;

public class Intersect2D
{
	private Intersect2D()
	{
		// Do not instantiate.
	}

	/*
	Returns true if the point and normalized plane intersect.
	 */
	public static boolean pointInPlane(Vector2D point, Plane2D plane)
	{
		float distance = (point.x * plane.normal.x) + (point.y * plane.normal.y) + plane.offset;

		return (distance == 0.0F);
	}

	/*
	Returns true if the point and normalized halfspace intersect.
	 */
	public static boolean pointInHalfspace(Vector2D point, Plane2D halfspace)
	{
		float distance = (point.x * halfspace.normal.x) + (point.y * halfspace.normal.y) + halfspace.offset;

		return (distance <= 0.0F);
	}

	/*
	Returns true if the point and circle intersect.
	 */
	public static boolean pointInCircle(Vector2D point, Circle2D circle)
	{
		float distanceX = point.x - circle.center.x;
		float distanceY = point.y - circle.center.y;
		float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
		float radiusSquared = circle.radius * circle.radius;

		return (distanceSquared <= radiusSquared);
	}

	/*
	Returns true if the point and rectangle intersect.
	 */
	public static boolean pointInRectangle(Vector2D point, Rectangle2D rectangle)
	{
		return (point.x >= rectangle.min.x && point.y >= rectangle.min.y && point.x <= rectangle.max.x && point.y <= rectangle.max.y);
	}

	/*
	Returns true if the segment and normalized plane intersect.
	 */
	public static boolean segmentInPlane(Vector2D pointA, Vector2D pointB, Plane2D plane)
	{
		float distanceA = (pointA.x * plane.normal.x) + (pointA.y * plane.normal.y) + plane.offset;
		float distanceB = (pointB.x * plane.normal.x) + (pointB.y * plane.normal.y) + plane.offset;

		return ((distanceA <= 0.0F && distanceB >= 0.0F) || (distanceA >= 0.0F && distanceB <= 0.0F));
	}

	/*
	Returns true if the segment and normalized halfspace intersect.
	 */
	public static boolean segmentInHalfspace(Vector2D pointA, Vector2D pointB, Plane2D halfspace)
	{
		float distanceA = (pointA.x * halfspace.normal.x) + (pointA.y * halfspace.normal.y) + halfspace.offset;
		float distanceB = (pointB.x * halfspace.normal.x) + (pointB.y * halfspace.normal.y) + halfspace.offset;

		return (distanceA <= 0.0F || distanceB <= 0.0F);
	}

	/*
	Returns true if the segment and circle intersect.
	 */
	public static boolean segmentInCircle(Vector2D pointA, Vector2D pointB, Circle2D circle)
	{
		float directionX = pointB.x - pointA.x;
		float directionY = pointB.y - pointA.y;
		float directionSquared = (directionX * directionX) + (directionY + directionY);
		float distanceX = circle.center.x - pointA.x;
		float distanceY = circle.center.y - pointA.y;
		float projection = (distanceX * directionX) + (distanceY * directionY);

		float t = projection / directionSquared;
		if (t < 0.0F) t = 0.0F;
		if (t > 1.0F) t = 1.0F;
		float closestX = pointA.x + (directionX * t);
		float closestY = pointA.y + (directionY * t);

		float closestDistanceX = closestX - circle.center.x;
		float closestDistanceY = closestY - circle.center.y;
		float distanceSquared = (closestDistanceX * closestDistanceX) + (closestDistanceY * closestDistanceY);
		float radiusSquared = circle.radius * circle.radius;

		return (distanceSquared <= radiusSquared);
	}

	/*
	Returns true if the segment and rectangle intersect.
	 */
	public static boolean segmentInRectangle(Vector2D pointA, Vector2D pointB, Rectangle2D rectangle)
	{
		float directionX = pointB.x - pointA.x;
		float directionY = pointB.y - pointA.y;
		float tMin = 0.0F;
		float tMax = 1.0F;

		if (directionX == 0.0F)
		{
			if (pointA.x < rectangle.min.x || pointA.x > rectangle.max.x) return false;
		}
		else
		{
			float t1, t2;
			float denominator = 1.0F / directionX;
			if (denominator > 0.0F)
			{
				t1 = (rectangle.min.x - pointA.x) * denominator;
				t2 = (rectangle.max.x - pointA.x) * denominator;
			}
			else
			{
				t1 = (rectangle.max.x - pointA.x) * denominator;
				t2 = (rectangle.min.x - pointA.x) * denominator;
			}

			tMin = Math.max(tMin, t1);
			tMax = Math.min(tMax, t2);
			if (tMin > tMax) return false;
		}

		if (directionY == 0.0F)
		{
			if (pointA.y < rectangle.min.y || pointA.y > rectangle.max.y) return false;
		}
		else
		{
			float t1, t2;
			float denominator = 1.0F / directionY;
			if (denominator > 0.0F)
			{
				t1 = (rectangle.min.y - pointA.y) * denominator;
				t2 = (rectangle.max.y - pointA.y) * denominator;
			}
			else
			{
				t1 = (rectangle.max.y - pointA.y) * denominator;
				t2 = (rectangle.min.y - pointA.y) * denominator;
			}

			tMin = Math.max(tMin, t1);
			tMax = Math.min(tMax, t2);
			if (tMin > tMax) return false;
		}

		return true;
	}

	/*
	Returns true if the circle and normalized plane intersect.
	 */
	public static boolean circleInPlane(Circle2D circle, Plane2D plane)
	{
		float distance = (circle.center.x * plane.normal.x) + (circle.center.y * plane.normal.y) + plane.offset;

		return (Math.abs(distance) <= circle.radius);
	}

	/*
	Returns true if the circle and normalized halfspace intersect.
	 */
	public static boolean circleInHalfspace(Circle2D circle, Plane2D halfspace)
	{
		float distance = (circle.center.x * halfspace.normal.x) + (circle.center.y * halfspace.normal.y) + halfspace.offset;

		return (distance <= circle.radius);
	}

	/*
	Returns true if the two circles intersect.
	 */
	public static boolean circleInCircle(Circle2D circle1, Circle2D circle2)
	{
		float distanceX = circle2.center.x - circle1.center.x;
		float distanceY = circle2.center.y - circle1.center.y;
		float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
		float radiusSquared = (circle1.radius + circle2.radius) * (circle1.radius + circle2.radius);

		return (distanceSquared <= radiusSquared);
	}

	/*
	Returns true if the circle and rectangle intersect.
	 */
	public static boolean circleInRectangle(Circle2D circle, Rectangle2D rectangle)
	{
		float closestX = circle.center.x;
		float closestY = circle.center.y;
		if (closestX < rectangle.min.x) closestX = rectangle.min.x;
		if (closestY < rectangle.min.y) closestY = rectangle.min.y;
		if (closestX > rectangle.max.x) closestX = rectangle.max.x;
		if (closestY > rectangle.max.y) closestY = rectangle.max.y;

		float distanceX = closestX - circle.center.x;
		float distanceY = closestY - circle.center.y;
		float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
		float radiusSquared = circle.radius * circle.radius;

		return (distanceSquared <= radiusSquared);
	}

	/*
	Returns true if the rectangle and normalized plane intersect.
	 */
	public static boolean rectangleInPlane(Rectangle2D rectangle, Plane2D plane)
	{
		float rectangleExtentX = (rectangle.max.x - rectangle.min.x) * 0.5F;
		float rectangleExtentY = (rectangle.max.y - rectangle.min.y) * 0.5F;
		float rectangleCenterX = rectangle.min.x + rectangleExtentX;
		float rectangleCenterY = rectangle.min.y + rectangleExtentY;

		float projection = (rectangleExtentX * Math.abs(plane.normal.x)) + (rectangleExtentY * Math.abs(plane.normal.y));
		float distance = (rectangleCenterX * plane.normal.x) + (rectangleCenterY * plane.normal.y) + plane.offset;

		return (Math.abs(distance) <= projection);
	}

	/*
	Returns true if the rectangle and normalized halfspace intersect.
	 */
	public static boolean rectangleInHalfspace(Rectangle2D rectangle, Plane2D halfspace)
	{
		float rectangleExtentX = (rectangle.max.x - rectangle.min.x) * 0.5F;
		float rectangleExtentY = (rectangle.max.y - rectangle.min.y) * 0.5F;
		float rectangleCenterX = rectangle.min.x + rectangleExtentX;
		float rectangleCenterY = rectangle.min.y + rectangleExtentY;

		float projection = (rectangleExtentX * Math.abs(halfspace.normal.x)) + (rectangleExtentY * Math.abs(halfspace.normal.y));
		float distance = (rectangleCenterX * halfspace.normal.x) + (rectangleCenterY * halfspace.normal.y) + halfspace.offset;

		return (distance <= projection);
	}

	/*
	Returns true if the two rectangles intersect.
	 */
	public static boolean rectangleInRectangle(Rectangle2D rectangle1, Rectangle2D rectangle2)
	{
		if (rectangle1.max.x < rectangle2.min.x || rectangle1.min.x > rectangle2.max.x) return false;
		if (rectangle1.max.y < rectangle2.min.y || rectangle1.min.y > rectangle2.max.y) return false;

		return true;
	}
}