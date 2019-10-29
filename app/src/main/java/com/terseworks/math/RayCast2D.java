//********************************************************************************
// RayCast2D.java
//
// (C) 2019 TerseWorks. All rights reserved.
// TerseWorks Public Code License (https://www.terseworks.com/licensing).
//
// Written by: John Meschke
// Description: Methods used for ray casting on 2D geometry. All methods assume
//              valid shapes (example: unit normals, positive radii, etc.).
//********************************************************************************

package com.terseworks.math;

public class RayCast2D
{
	private RayCast2D()
	{
		// Do not instantiate.
	}

	/*
	Returns true if the ray intersects with a normalized plane and gives the point of contact.
	The contactPoint argument will be modified.
	 */
	public static boolean rayInPlane(Vector2D rayFrom, Vector2D rayTo, Plane2D plane, Vector2D contactPoint)
	{
		float directionX = rayTo.x - rayFrom.x;
		float directionY = rayTo.x - rayFrom.x;
		float distance = (rayFrom.x * plane.normal.x) + (rayFrom.y * plane.normal.y) + plane.offset;
		float projection = (directionX * plane.normal.x) + (directionY * plane.normal.y);

		float t = (distance != 0.0F) ? -distance / projection : 0.0F;
		boolean result = (t >= 0.0F && t <= 1.0F);

		if (result)
		{
			contactPoint.x = rayFrom.x + (directionX * t);
			contactPoint.y = rayFrom.y + (directionY * t);
		}
		else
		{
			contactPoint.x = rayTo.x;
			contactPoint.y = rayTo.y;
		}

		return result;
	}

	/*
	Returns true if the ray intersects with a normalized halfspace and gives the point of contact.
	The contactPoint argument will be modified.
	 */
	public static boolean rayInHalfspace(Vector2D rayFrom, Vector2D rayTo, Plane2D halfspace, Vector2D contactPoint)
	{
		float directionX = rayTo.x - rayFrom.x;
		float directionY = rayTo.x - rayFrom.x;
		float distance = (rayFrom.x * halfspace.normal.x) + (rayFrom.y * halfspace.normal.y) + halfspace.offset;
		float projection = (directionX * halfspace.normal.x) + (directionY * halfspace.normal.y);

		distance = Math.max(0.0F, distance);
		float t = (distance != 0.0F) ? -distance / projection : 0.0F;
		boolean result = (t >= 0.0F && t <= 1.0F);

		if (result)
		{
			contactPoint.x = rayFrom.x + (directionX * t);
			contactPoint.y = rayFrom.y + (directionY * t);
		}
		else
		{
			contactPoint.x = rayTo.x;
			contactPoint.y = rayTo.y;
		}

		return result;
	}

	/*
	Returns true if the ray intersects with a circle and gives the point of contact.
	The contactPoint argument will be modified.
	 */
	public static boolean rayInCircle(Vector2D rayFrom, Vector2D rayTo, Circle2D circle, Vector2D contactPoint)
	{
		float directionX = rayTo.x - rayFrom.x;
		float directionY = rayTo.y - rayFrom.y;
		float distanceX = rayFrom.x - circle.center.x;
		float distanceY = rayFrom.y - circle.center.y;
		float projection = (directionX * distanceX) + (directionY * distanceY);
		float projectionSquared = projection * projection;
		float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
		float directionSquared = (directionX * directionX) + (directionY * directionY);
		float radiusSquared = circle.radius * circle.radius;
		float discriminant = projectionSquared - (directionSquared * (distanceSquared - radiusSquared));

		float t = (distanceSquared <= radiusSquared) ? 0.0F : Float.MAX_VALUE;
		if (discriminant >= 0.0F && directionSquared != 0.0F)
		{
			t = -(projection + (float) Math.sqrt(discriminant)) / directionSquared;
			t = Math.max(0.0F, t);
		}
		boolean result = (t >= 0.0F && t <= 1.0F);

		if (result)
		{
			contactPoint.x = rayFrom.x + (directionX * t);
			contactPoint.y = rayFrom.y + (directionY * t);
		}
		else
		{
			contactPoint.x = rayTo.x;
			contactPoint.y = rayTo.y;
		}

		return result;
	}

	/*
	Returns true if the ray intersects with a rectangle and gives the point of contact.
	The contactPoint argument will be modified.
	 */
	public static boolean rayInRectangle(Vector2D rayFrom, Vector2D rayTo, Rectangle2D rectangle, Vector2D contactPoint)
	{
		float directionX = rayTo.x - rayFrom.x;
		float directionY = rayTo.y - rayFrom.y;
		float tMin = 0.0F;
		float tMax = 1.0F;
		contactPoint.x = rayTo.x;
		contactPoint.y = rayTo.y;

		if (directionX == 0.0F)
		{
			if (rayFrom.x < rectangle.min.x || rayFrom.x > rectangle.max.x) return false;
		}
		else
		{
			float t1, t2;
			float denominator = 1.0F / directionX;
			if (denominator > 0.0F)
			{
				t1 = (rectangle.min.x - rayFrom.x) * denominator;
				t2 = (rectangle.max.x - rayFrom.x) * denominator;
			}
			else
			{
				t1 = (rectangle.max.x - rayFrom.x) * denominator;
				t2 = (rectangle.min.x - rayFrom.x) * denominator;
			}

			tMin = Math.max(tMin, t1);
			tMax = Math.min(tMax, t2);
			if (tMin > tMax) return false;
		}

		if (directionY == 0.0F)
		{
			if (rayFrom.y < rectangle.min.y || rayFrom.y > rectangle.max.y) return false;
		}
		else
		{
			float t1, t2;
			float denominator = 1.0F / directionY;
			if (denominator > 0.0F)
			{
				t1 = (rectangle.min.y - rayFrom.y) * denominator;
				t2 = (rectangle.max.y - rayFrom.y) * denominator;
			}
			else
			{
				t1 = (rectangle.max.y - rayFrom.y) * denominator;
				t2 = (rectangle.min.y - rayFrom.y) * denominator;
			}

			tMin = Math.max(tMin, t1);
			tMax = Math.min(tMax, t2);
			if (tMin > tMax) return false;
		}

		contactPoint.x = rayFrom.x + (directionX * tMin);
		contactPoint.y = rayFrom.y + (directionY * tMin);

		return true;
	}
}