package org.librarysimplified.breakout.app

import android.graphics.Canvas

interface GameStateType {

  fun onEvent(
    frame: Int,
    deltaMs: Double,
    event: BreakoutEvent
  ): GameStateType?

  fun onRender(
    frame: Int,
    deltaMs: Double,
    canvas: Canvas
  )

  fun onLogic(
    frame: Int,
    deltaMs: Double): GameStateType?

  val name: String
}