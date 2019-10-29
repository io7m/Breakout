package org.librarysimplified.breakout.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PointF
import android.util.Size

class GameLoadingState(
  private val publishEvent: (BreakoutEvent) -> Unit,
  private val framesPerSecond: Int,
  private val sounds: Sounds,
  private val context: Context,
  private val screenSize: Size)
  : GameStateType {

  private val playField: PlayField

  init {
    val blockSize = this.screenSize.width / 13
    val ballSize = this.screenSize.width / 16
    val paddleWidth = ballSize * 2
    val paddleHeight = ballSize / 2.0f

    val puddingBase =
      BitmapFactory.decodeResource(this.context.resources, R.drawable.pudding)
    val pudding =
      Bitmap.createScaledBitmap(puddingBase, blockSize, blockSize, true)
    puddingBase.recycle()

    val baubleBase =
      BitmapFactory.decodeResource(this.context.resources, R.drawable.bauble)
    val bauble =
      Bitmap.createScaledBitmap(baubleBase, ballSize, ballSize, true)
    baubleBase.recycle()

    val paddleCenter =
      PointF(this.screenSize.width / 2.0f, (this.screenSize.height - paddleHeight * 4.0f))
    val paddle =
      Paddle(
        center = paddleCenter,
        radius = paddleWidth.toFloat(),
        height = paddleHeight
      )

    val blocks = mutableListOf<Block>()
    for (y in 0 until 6) {
      for (x in 0 until 12) {
        val blockY = blockSize + (y * blockSize)
        val blockX = blockSize + (x * blockSize)

        val blockCircle = CircleF(
          center = PointF(blockX.toFloat(), blockY.toFloat()),
          radius = blockSize / 2.0f
        )

        val block =
          Block(
            circle = blockCircle,
            bitmap = pudding,
            alive = true
          )
        blocks.add(block)
      }
    }

    val ballDirection = PointF(Math.random().toFloat(), -1.0f)
    Vectors.normalize(ballDirection)
    val ballDefaultDirection = PointF(ballDirection.x, ballDirection.y)

    val ballCircle = CircleF(
      center = PointF(this.screenSize.width / 2.0f, paddle.rectangle().min.y - ballSize),
      radius = ballSize / 2.0f
    )

    val speedDefault = 1.0f
    val ball =
      Ball(
        isAlive = true,
        circle = ballCircle,
        bitmap = bauble,
        defaultDirection = ballDefaultDirection,
        direction = ballDirection,
        speedDefault = speedDefault,
        speed = speedDefault)

    this.playField = PlayField(
      publishEvent = this.publishEvent,
      screenSize = this.screenSize,
      blocks = blocks.toList(),
      ball = ball,
      paddle = paddle
    )
  }

  override val name: String = "loading"

  override fun onLogic(
    frame: Int,
    deltaMs: Double
  ): GameStateType? {
    return GamePlayingState(
      sounds = this.sounds,
      publishEvent = this.publishEvent,
      framesPerSecond = this.framesPerSecond,
      playField = this.playField
    )
  }

  override fun onEvent(
    frame: Int,
    deltaMs: Double,
    event: BreakoutEvent
  ): GameStateType? {
    return null
  }

  override fun onRender(
    frame: Int,
    deltaMs: Double,
    canvas: Canvas
  ) {
    canvas.drawARGB(0xff, 0x80, 0x0, 0x0)
  }
}