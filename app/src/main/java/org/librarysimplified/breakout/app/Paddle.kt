package org.librarysimplified.breakout.app

import android.graphics.PointF
import android.graphics.RectF
import com.terseworks.math.Rectangle2D

class Paddle(
  var center: PointF,
  val height: Float,
  val radius: Float) {

  fun rectangle(): Rectangle2D {
    val left = this.center.x - this.radius
    val top = this.center.y - (this.height / 2.0f)
    val right = this.center.x + this.radius
    val bottom = this.center.y + (this.height / 2.0f)
    return Rectangle2D(left, top, right, bottom)
  }
}