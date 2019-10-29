package org.librarysimplified.breakout.app

import android.graphics.PointF
import android.util.Size

sealed class BreakoutEvent {

  data class ScreenSizeChanged(
    val screenSize: Size)
    : BreakoutEvent()

  data class CursorMoved(
    val position: PointF)
    : BreakoutEvent()

  data class CursorReleased(
    val position: PointF)
    : BreakoutEvent()

  object GetReady
    : BreakoutEvent()

  object Go
    : BreakoutEvent()

  data class BallBrokeBlock(
    val block: Block,
    val collisionPoint: PointF,
    val collisionNormal: PointF,
    val collisionReflect: PointF)
    : BreakoutEvent()

  data class BallBouncedPaddle(
    val collisionPoint: PointF,
    val collisionNormal: PointF,
    val collisionReflect: PointF)
    : BreakoutEvent()

  object BallBouncedWall
    : BreakoutEvent()

  object BallFellOut
    : BreakoutEvent()
}