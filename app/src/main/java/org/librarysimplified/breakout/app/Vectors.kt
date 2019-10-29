package org.librarysimplified.breakout.app

import android.graphics.PointF
import kotlin.math.pow
import kotlin.math.sqrt

object Vectors {

  fun distanceBetween(
    p0: PointF,
    p1: PointF
  ): Float {
    val xSquare = (p0.x - p1.x).toDouble().pow(2.0)
    val ySquare = (p0.y - p1.y).toDouble().pow(2.0)
    return sqrt(xSquare + ySquare).toFloat()
  }

  fun dotProduct(
    p0: PointF,
    p1: PointF
  ): Float {
    val dx = p0.x * p1.x
    val dy = p0.y * p1.y
    return dx + dy
  }

  fun scale(p: PointF, s: Float): PointF {
    return PointF(
      p.x * s,
      p.y * s
    )
  }

  fun direction(p0: PointF, p1: PointF): PointF {
    val direction =
      PointF(
        p0.x - p1.x,
        p0.y - p1.y)

    normalize(direction)
    return direction
  }

  fun normalize(p: PointF) {
    p.set(
      p.x / p.length(),
      p.y / p.length())
  }

  fun reflect(v: PointF, n: PointF): PointF {
    val dot = dotProduct(n, v) * 2.0f
    val nScaled = scale(n, dot)
    val r = PointF(
      v.x - nScaled.x,
      v.y - nScaled.y
    )
    normalize(r)
    return r
  }
}