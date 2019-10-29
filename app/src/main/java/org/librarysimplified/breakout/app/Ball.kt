package org.librarysimplified.breakout.app

import android.graphics.Bitmap
import android.graphics.PointF

class Ball(
  var isAlive: Boolean,
  var circle: CircleF,
  var direction: PointF,
  val defaultDirection: PointF,
  val speedDefault: Float,
  var speed: Float,
  val bitmap: Bitmap) {

}