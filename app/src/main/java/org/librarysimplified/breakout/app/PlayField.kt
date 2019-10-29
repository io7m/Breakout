package org.librarysimplified.breakout.app

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.Size
import com.terseworks.math.Circle2D
import com.terseworks.math.Collide2D
import com.terseworks.math.Contact2D
import com.terseworks.math.Vector2D

class PlayField(
  private val publishEvent: (BreakoutEvent) -> Unit,
  val screenSize: Size,
  val blocks: List<Block>,
  val ball: Ball,
  val paddle: Paddle,
  val explosions: MutableList<Explosion> = mutableListOf()
) : GameEntityType {

  private var playPerfectly: Boolean = false
  private var renderDebugHulls = false
  private var renderDebugBounce = false

  private var cursorDown: Boolean = false
  private val cursorPosition = PointF()
  private val cursorPaint = Paint()
  private val paddleFill = Paint()
  private val paddleStroke = Paint()
  private val collisionNormalLast = PointF()
  private val collisionPositionLast = PointF()
  private val collisionReflectLast = PointF()
  private val reflectStroke = Paint()
  private val normalStroke = Paint()

  init {
    this.cursorPaint.color = Color.GRAY
    this.cursorPaint.style = Paint.Style.STROKE
    this.paddleFill.color = Color.RED
    this.paddleFill.style = Paint.Style.FILL
    this.paddleStroke.color = Color.WHITE
    this.paddleStroke.style = Paint.Style.STROKE
    this.reflectStroke.color = Color.GREEN
    this.reflectStroke.style = Paint.Style.STROKE
    this.normalStroke.color = Color.CYAN
    this.normalStroke.style = Paint.Style.STROKE
  }

  override fun onRender(canvas: Canvas, deltaMs: Double) {
    for (block in this.blocks) {
      if (block.alive) {
        canvas.drawBitmap(
          block.bitmap,
          block.circle.center.x - block.circle.radius,
          block.circle.center.y - block.circle.radius,
          null)

        if (this.renderDebugHulls) {
          canvas.drawCircle(
            block.circle.center.x,
            block.circle.center.y,
            block.circle.radius,
            this.paddleStroke
          )
        }
      }
    }

    val ball = this.ball
    canvas.drawBitmap(
      ball.bitmap,
      ball.circle.center.x - ball.circle.radius,
      ball.circle.center.y - ball.circle.radius,
      null)

    if (this.renderDebugHulls) {
      canvas.drawCircle(
        ball.circle.center.x,
        ball.circle.center.y,
        ball.circle.radius,
        this.paddleStroke
      )
    }

    val paddle = this.paddle
    val paddleRect = paddle.rectangle()
    canvas.drawRect(
      paddleRect.min.x, paddleRect.min.y, paddleRect.max.x, paddleRect.max.y, this.paddleFill)
    canvas.drawRect(
      paddleRect.min.x, paddleRect.min.y, paddleRect.max.x, paddleRect.max.y, this.paddleStroke)

    if (this.renderDebugBounce) {
      canvas.drawLine(
        this.collisionPositionLast.x,
        this.collisionPositionLast.y,
        this.collisionPositionLast.x + (this.collisionNormalLast.x * 128.0f),
        this.collisionPositionLast.y + (this.collisionNormalLast.y * 128.0f),
        this.normalStroke
      )

      canvas.drawLine(
        this.collisionPositionLast.x,
        this.collisionPositionLast.y,
        this.collisionPositionLast.x + (this.collisionReflectLast.x * 128.0f),
        this.collisionPositionLast.y + (this.collisionReflectLast.y * 128.0f),
        this.reflectStroke
      )
    }

    if (this.cursorDown) {
      canvas.drawCircle(
        this.cursorPosition.x,
        this.cursorPosition.y,
        8.0f,
        this.cursorPaint
      )
    }

    this.explosions.forEach { explosion -> explosion.onRender(canvas, deltaMs) }
  }

  override fun onEvent(event: BreakoutEvent, deltaMs: Double) {
    return when (event) {
      is BreakoutEvent.ScreenSizeChanged -> {

      }

      is BreakoutEvent.CursorMoved -> {
        this.cursorDown = true
        this.cursorPosition.set(event.position)
      }

      is BreakoutEvent.CursorReleased -> {
        this.cursorDown = false
      }

      is BreakoutEvent.BallBrokeBlock -> {
        this.collisionPositionLast.set(event.collisionPoint)
        this.collisionReflectLast.set(event.collisionReflect)
        this.collisionNormalLast.set(event.collisionNormal)
        this.explosions.add(Explosion(
          center = event.block.circle.center,
          sparkRadius = 4.0f,
          sparkCount = 60,
          baseColor = Color.WHITE))
        Unit
      }

      is BreakoutEvent.BallBouncedPaddle -> {
        this.collisionPositionLast.set(event.collisionPoint)
        this.collisionReflectLast.set(event.collisionReflect)
        this.collisionNormalLast.set(event.collisionNormal)
        this.explosions.add(Explosion(
          center = event.collisionPoint,
          sparkRadius = 2.0f,
          sparkCount = 10,
          baseColor = Color.YELLOW))
        Unit
      }

      BreakoutEvent.BallBouncedWall -> {

      }

      BreakoutEvent.BallFellOut -> {

      }

      BreakoutEvent.GetReady -> {
        this.paddle.center.x =
          this.screenSize.width / 2.0f

        this.ball.direction.set(
          this.ball.defaultDirection.x,
          this.ball.defaultDirection.y)

        this.ball.circle.center.set(
          this.screenSize.width / 2.0f,
          this.paddle.rectangle().min.y - (this.ball.circle.radius * 2.0f))

        this.ball.isAlive = true
      }

      BreakoutEvent.Go -> {

      }
    }
  }

  private fun moveExplosions(deltaMs: Double) {
    val iter = this.explosions.iterator()
    while (iter.hasNext()) {
      val explosion = iter.next()
      explosion.onLogic(deltaMs)
      if (!explosion.isAlive) {
        iter.remove()
      }
    }
  }

  private fun collideBallWithPaddle() {
    val ball = this.ball
    val paddle = this.paddle

    val ballCircle =
      Circle2D(Vector2D(ball.circle.center.x, ball.circle.center.y), ball.circle.radius)
    val paddleRect =
      paddle.rectangle()
    val contact =
      Contact2D()

    Collide2D.circleInRectangle(ballCircle, paddleRect, contact)

    if (contact.depth > 0.0) {
      val contactNormal = PointF(contact.normal.x, contact.normal.y)
      val reflect = Vectors.reflect(ball.direction, contactNormal)
      ball.direction.set(reflect)

      this.publishEvent.invoke(BreakoutEvent.BallBouncedPaddle(
        collisionNormal = contactNormal,
        collisionReflect = reflect,
        collisionPoint = ball.circle.center))
    }
  }

  private fun moveBall(deltaMs: Double) {
    val ball = this.ball
    val speed = deltaMs * ball.speed

    if (ball.isAlive) {
      ball.circle.center.x += (speed * ball.direction.x).toFloat()
      ball.circle.center.y += (speed * ball.direction.y).toFloat()

      val ballTop = ball.circle.center.y - ball.circle.radius
      if (ballTop >= this.screenSize.height) {
        ball.isAlive = false
        ball.speed = ball.speedDefault
        this.publishEvent.invoke(BreakoutEvent.BallFellOut)
      }
    }
  }

  private fun collideBallWithWalls() {
    val ball = this.ball
    var collided = false

    if (ball.circle.center.x + ball.circle.radius >= this.screenSize.width) {
      ball.direction.x = -Math.abs(ball.direction.x)
      collided = true
    }
    if (ball.circle.center.x - ball.circle.radius <= 0.0f) {
      ball.direction.x = Math.abs(ball.direction.x)
      collided = true
    }
    if (ball.circle.center.y - ball.circle.radius <= 0.0f) {
      ball.direction.y = Math.abs(ball.direction.y)
      collided = true
    }

    Vectors.normalize(ball.direction)

    if (collided) {
      this.publishEvent.invoke(BreakoutEvent.BallBouncedWall)
    }
  }

  private fun collisionPoint(
    c0: CircleF,
    c1: CircleF
  ): PointF {
    val collideX =
      (c0.center.x * c1.radius + c1.center.x * c0.radius) / (c0.radius + c1.radius)
    val collideY =
      (c0.center.y * c1.radius + c1.center.y * c0.radius) / (c0.radius + c1.radius)
    return PointF(collideX, collideY)
  }

  private fun collideBallWithBlocks() {
    val ball = this.ball
    for (block in this.blocks) {
      if (block.alive) {
        val radius = block.circle.radius + ball.circle.radius
        if (Vectors.distanceBetween(ball.circle.center, block.circle.center) <= radius) {
          val collision = this.collisionPoint(ball.circle, block.circle)
          val normal = block.circle.directionTo(collision)
          val reflect = Vectors.reflect(ball.direction, normal)
          ball.direction.set(reflect)
          ball.speed = Math.min(ball.speed * 1.025f, 5.0f)
          block.alive = false
          this.publishEvent.invoke(BreakoutEvent.BallBrokeBlock(block, collision, normal, reflect))
        }
      }
    }
  }

  private fun movePaddle() {
    if (this.playPerfectly) {
      this.cursorPosition.x = this.ball.circle.center.x
    }

    var newPaddleX = this.cursorPosition.x
    val paddleLeftX = newPaddleX - this.paddle.radius
    val paddleRightX = newPaddleX + this.paddle.radius

    if (paddleLeftX < 0.0) {
      newPaddleX += Math.abs(paddleLeftX)
    }
    if (paddleRightX > this.screenSize.width) {
      newPaddleX -= Math.abs(paddleRightX - this.screenSize.width)
    }
    this.paddle.center.x = newPaddleX
  }


  override fun onLogic(deltaMs: Double) {
    this.movePaddle()
    this.moveBall(deltaMs)
    this.moveExplosions(deltaMs)

    if (this.ball.isAlive) {
      this.collideBallWithWalls()
      this.collideBallWithBlocks()
      this.collideBallWithPaddle()
    }
  }
}