package org.librarysimplified.breakout.app

import android.graphics.PointF

class CircleF(
  var center: PointF,
  var radius: Float) {

  fun directionTo(p: PointF): PointF =
    Vectors.direction(this.center, p)

}