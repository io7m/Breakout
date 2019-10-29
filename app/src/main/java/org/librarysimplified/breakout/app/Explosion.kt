package org.librarysimplified.breakout.app

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import androidx.annotation.ColorInt

class Explosion(
  val center: PointF,
  val sparkRadius: Float,
  val sparkCount: Int,
  @ColorInt val baseColor: Int)
  : GameEntityType {

  private var sparks: MutableList<Spark> = mutableListOf()

  private class Spark(
    val direction: PointF,
    var speed: Float,
    val gravity: Float,
    val lifeMax: Int,
    var center: PointF,
    var radius: Float,
    val baseColor: Int): GameEntityType {

    private val sparkPaint = Paint()
    var life: Int = this.lifeMax

    init {
      this.sparkPaint.style = Paint.Style.FILL
    }

    override fun onRender(
      canvas: Canvas,
      deltaMs: Double) {

      val bright = this.life.toDouble() / this.lifeMax.toDouble()
      val r = Color.red(this.baseColor) * bright
      val g = Color.green(this.baseColor) * bright
      val b = Color.blue(this.baseColor) * bright
      this.sparkPaint.color = Color.argb(0xff, r.toInt(), g.toInt(), b.toInt())
      canvas.drawCircle(this.center.x, this.center.y, this.radius, this.sparkPaint)
    }

    override fun onLogic(deltaMs: Double) {
      this.direction.y += (this.gravity * deltaMs).toFloat()
      Vectors.normalize(this.direction)

      val speedDelta = (this.speed * deltaMs).toFloat()
      this.center.x += this.direction.x * speedDelta
      this.center.y += this.direction.y * speedDelta

      this.life = Math.max(0, this.life - 1)
    }
  }

  init {
    for (i in 0 until this.sparkCount) {
      val rx = (Math.random() * 2.0) - 1.0
      val ry = (Math.random() * 2.0) - 1.0

      val sparkDirection = PointF(rx.toFloat(), ry.toFloat())
      Vectors.normalize(sparkDirection)

      val sparkSpeed = Math.random() * 8.0
      val sparkCenter = PointF(this.center.x, this.center.y)

      val spark = Spark(
        direction = sparkDirection,
        speed = sparkSpeed.toFloat(),
        gravity = 0.01f,
        lifeMax = 30,
        center = sparkCenter,
        radius = this.sparkRadius,
        baseColor = this.baseColor)

      this.sparks.add(spark)
    }
  }

  override fun onLogic(
    deltaMs: Double) {

    for (spark in this.sparks) {
      spark.onLogic(deltaMs)
    }
  }

  override fun onRender(
    canvas: Canvas,
    deltaMs: Double) {

    for (spark in this.sparks) {
      spark.onRender(canvas, deltaMs)
    }
  }

  val isAlive: Boolean
    get() = this.sparks.any { spark -> spark.life > 0.0f }

}