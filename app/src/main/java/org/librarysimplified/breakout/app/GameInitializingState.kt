package org.librarysimplified.breakout.app

import android.content.Context
import android.graphics.Canvas

class GameInitializingState(
  private val publishEvent: (BreakoutEvent) -> Unit,
  private val framesPerSecond: Int,
  private val sounds: Sounds,
  private val context: Context)
  : GameStateType {

  override val name: String = "initializing"

  override fun onLogic(
    frame: Int,
    deltaMs: Double
  ): GameStateType? {
    return null
  }

  override fun onEvent(
    frame: Int,
    deltaMs: Double,
    event: BreakoutEvent
  ): GameStateType? {
    return when (event) {
      is BreakoutEvent.ScreenSizeChanged ->
        GameLoadingState(
          publishEvent = this.publishEvent,
          framesPerSecond = this.framesPerSecond,
          sounds = this.sounds,
          context = this.context,
          screenSize = event.screenSize
        )
      is BreakoutEvent.CursorMoved -> null
      is BreakoutEvent.CursorReleased -> null
      is BreakoutEvent.BallBrokeBlock -> null
      is BreakoutEvent.BallBouncedPaddle -> null
      BreakoutEvent.BallBouncedWall -> null
      BreakoutEvent.BallFellOut -> null
      BreakoutEvent.GetReady -> null
      BreakoutEvent.Go -> null
    }
  }

  override fun onRender(
    frame: Int,
    deltaMs: Double,
    canvas: Canvas
  ) {
    canvas.drawARGB(0xff, 0x0, 0x30, 0x30)
  }
}