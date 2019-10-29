package org.librarysimplified.breakout.app

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import org.librarysimplified.breakout.app.GamePlayingState.AnnouncementKind.COUNTDOWN
import org.librarysimplified.breakout.app.GamePlayingState.AnnouncementKind.FINISHED
import org.librarysimplified.breakout.app.GamePlayingState.AnnouncementKind.GO
import kotlin.math.max

class GamePlayingState(
  private val sounds: Sounds,
  private val publishEvent: (BreakoutEvent) -> Unit,
  private val framesPerSecond: Int,
  private val playField: PlayField) : GameStateType {

  private var status: Status =
    this.createGetReadyStatus(10, 3)

  private val uiPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val statistics =
    Statistics(
      ballSpeedMax = 0f,
      paddleBounces = 0,
      wallBounces = 0
    )

  init {
    this.uiPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    this.uiPaint.color = Color.WHITE
    this.uiPaint.style = Paint.Style.FILL
  }

  private fun createGetReadyStatus(
    frameStart: Int,
    lives: Int
  ): Status.GetReady {
    val perMessage = (this.framesPerSecond / 4) * 3
    return Status.GetReady(
      timeStarted = frameStart,
      announcements = mutableListOf(
        Announcement(frameStart + perMessage, "3...", COUNTDOWN),
        Announcement(frameStart + (perMessage * 2), "2...", COUNTDOWN),
        Announcement(frameStart + (perMessage * 3), "1...", COUNTDOWN),
        Announcement(frameStart + (perMessage * 4), "GO!", GO),
        Announcement(frameStart + (perMessage * 5), "", FINISHED)
      ),
      lives = lives)
  }

  enum class AnnouncementKind {
    COUNTDOWN,
    GO,
    FINISHED
  }

  private data class Announcement(
    val time: Int,
    val text: String,
    val kind: AnnouncementKind)

  private data class Statistics(
    var ballSpeedMax: Float,
    var paddleBounces: Int,
    var wallBounces: Int)

  private sealed class Status {

    abstract val lives: Int

    data class GetReady(
      val timeStarted: Int,
      val announcements: MutableList<Announcement>,
      override val lives: Int)
      : Status()

    data class Won(
      override val lives: Int)
      : Status()

    data class Lost(
      override val lives: Int)
      : Status()

    data class Playing(
      override val lives: Int)
      : Status()
  }

  override fun onEvent(
    frame: Int,
    deltaMs: Double,
    event: BreakoutEvent
  ): GameStateType? {
    this.playField.onEvent(event, deltaMs)

    return when (event) {
      is BreakoutEvent.ScreenSizeChanged -> null

      is BreakoutEvent.CursorMoved -> null

      is BreakoutEvent.CursorReleased -> null

      is BreakoutEvent.BallBrokeBlock -> {
        if (!this.playField.blocks.any { block -> block.alive }) {
          this.status = Status.Won(this.status.lives)
        }

        this.statistics.ballSpeedMax =
          max(this.statistics.ballSpeedMax, this.playField.ball.speed)
        this.sounds.playRandomRate(this.sounds.blockBreak)
        null
      }

      is BreakoutEvent.BallBouncedPaddle -> {
        this.statistics.paddleBounces += 1
        this.sounds.playRandomRate(this.sounds.tak)
        null
      }

      BreakoutEvent.BallBouncedWall -> {
        this.statistics.wallBounces += 1
        this.sounds.playRandomRate(this.sounds.bounce)
        null
      }

      BreakoutEvent.BallFellOut -> {
        val lives = this.status.lives
        if (lives == 1) {
          this.status = Status.Lost(0)
        } else {
          this.status =
            this.createGetReadyStatus(
              frameStart = frame + this.framesPerSecond,
              lives = Math.max(0, this.status.lives - 1))
        }
        this.sounds.play(this.sounds.failure, 1.0)
        null
      }

      BreakoutEvent.GetReady -> {
        this.sounds.play(this.sounds.ready, 1.0)
        null
      }

      BreakoutEvent.Go -> {
        this.sounds.play(this.sounds.go, 1.0)
        null
      }
    }
  }

  private fun findCurrentAnnouncement(status: Status.GetReady, frame: Int): Announcement? {
    var current: Announcement? = null
    for (announcement in status.announcements) {
      if (frame >= announcement.time) {
        current = announcement
      }
    }
    return current
  }

  override fun onLogic(
    frame: Int,
    deltaMs: Double
  ): GameStateType? {
    return when (val currentStatus = this.status) {
      is Status.GetReady -> {
        val announcement =
          this.findCurrentAnnouncement(currentStatus, frame) ?: return null

        when (announcement.kind) {
          COUNTDOWN ->
            if (announcement.time == frame) {
              this.publishEvent.invoke(BreakoutEvent.GetReady)
              return null
            }

          GO ->
            if (announcement.time == frame) {
              this.publishEvent.invoke(BreakoutEvent.Go)
              return null
            }

          FINISHED -> {
            this.status = Status.Playing(currentStatus.lives)
            return null
          }
        }
        null
      }

      is Status.Playing -> {
        this.playField.onLogic(deltaMs)
        null
      }

      is Status.Won -> {
        null
      }
      is Status.Lost -> {
        null
      }
    }
  }

  override fun onRender(
    frame: Int,
    deltaMs: Double,
    canvas: Canvas
  ) {
    canvas.drawARGB(0xff, 0x20, 0x20, 0x20)
    this.playField.onRender(canvas, deltaMs)

    when (val currentStatus = this.status) {
      is Status.GetReady -> {
        val announcement =
          this.findCurrentAnnouncement(currentStatus, frame)

        if (announcement != null) {
          this.uiPaint.textSize = 96.0f
          val textWidth = this.uiPaint.measureText(announcement.text)

          canvas.drawText(
            announcement.text,
            (this.playField.screenSize.width / 2.0f) - (textWidth / 2.0f),
            this.playField.screenSize.height / 2.0f,
            this.uiPaint
          )
        }

        this.renderLivesText(canvas)
      }

      is Status.Playing -> {
        this.renderLivesText(canvas)
      }

      is Status.Won -> {
        this.uiPaint.textSize = 96.0f
        val textWidth = this.uiPaint.measureText("YOU WIN!")

        canvas.drawText(
          "YOU WIN!",
          (this.playField.screenSize.width / 2.0f) - (textWidth / 2.0f),
          this.playField.screenSize.height / 2.0f,
          this.uiPaint)

        this.renderStatsText(canvas)
      }

      is Status.Lost -> {
        this.uiPaint.textSize = 96.0f
        val textWidth = this.uiPaint.measureText("GAME OVER")

        canvas.drawText(
          "GAME OVER",
          (this.playField.screenSize.width / 2.0f) - (textWidth / 2.0f),
          this.playField.screenSize.height / 2.0f,
          this.uiPaint)

        this.renderStatsText(canvas)
      }
    }
  }

  private fun renderStatsText(canvas: Canvas) {
    this.uiPaint.textSize = 14.0f
    val ballRadius = this.playField.ball.circle.radius
    val ballY = this.playField.screenSize.height - ballRadius
    canvas.drawText(
      "Lives: ${this.status.lives}, " +
        "Wall bounces: ${this.statistics.wallBounces}, " +
        "Paddle bounces: ${this.statistics.paddleBounces}, " +
        "Max speed: ${this.statistics.ballSpeedMax}",
      ballRadius,
      ballY,
      this.uiPaint)
  }

  private fun renderLivesText(canvas: Canvas) {
    this.uiPaint.textSize = 24.0f
    val ballRadius = this.playField.ball.circle.radius
    val ballY = this.playField.screenSize.height - ballRadius
    canvas.drawText("Lives: ${this.status.lives}", ballRadius, ballY, this.uiPaint)
  }

  override val name: String = "playing"
}