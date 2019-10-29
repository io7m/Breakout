package org.librarysimplified.breakout.app

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Size
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class BreakoutView(
  context: Context,
  attributes: AttributeSet)
  : SurfaceView(context, attributes) {

  private val events: ConcurrentLinkedQueue<BreakoutEvent> = ConcurrentLinkedQueue()
  private val sounds = Sounds(context)
  private val fps = 60.0
  private val deltaMs = 1000.0 / this.fps

  @Volatile
  private var frame = 0

  @Volatile
  private var state: GameStateType =
    GameInitializingState(
      publishEvent = this::publishEvent,
      framesPerSecond = this.fps.toInt(),
      sounds = this.sounds,
      context = context)

  private fun publishEvent(event: BreakoutEvent) {
    this.events.add(event)
  }

  private val logger = LoggerFactory.getLogger(BreakoutView::class.java)

  private val done: AtomicBoolean = AtomicBoolean(false)
  private var executor: ExecutorService? = null

  private fun createExecutor(): ExecutorService {
    return Executors.newFixedThreadPool(1) { runnable ->
      val thread = Thread(runnable)
      thread.name = "org.librarysimplified.breakout[${thread.id}]"
      thread
    }
  }

  private fun recreateExecutor() {
    this.executor?.shutdown()
    this.executor = this.createExecutor()
  }

  init {
    this.holder.addCallback(this.HolderCallbacks())
  }

  inner class HolderCallbacks : SurfaceHolder.Callback {
    override fun surfaceChanged(
      holder: SurfaceHolder?,
      format: Int,
      width: Int,
      height: Int
    ) {
      this@BreakoutView.logger.debug(
        "surface changed: 0x{} {} {}",
        format.toString(16),
        width,
        height)
      this@BreakoutView.events.add(BreakoutEvent.ScreenSizeChanged(Size(width, height)))
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
      this@BreakoutView.logger.debug("surface destroyed")
      this@BreakoutView.done.set(true)
      this@BreakoutView.executor?.shutdown()
      this@BreakoutView.executor = null
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
      this@BreakoutView.logger.debug("surface created")
      this@BreakoutView.done.set(false)
      this@BreakoutView.recreateExecutor()
      this@BreakoutView.executor?.execute { this@BreakoutView.run() }
    }
  }

  private fun run() {
    this.logger.debug("game task started")

    while (!this.done.get()) {
      this.frame += 1
      this.handleEvents()
      this.logic(this.deltaMs)
      this.render(this.deltaMs)
      this.pause(this.deltaMs)
    }

    this.sounds.release()
    this.logger.debug("game task finished")
  }

  private fun handleEvents() {
    while (true) {
      val event = this.events.poll() ?: break
      val newState = this.state.onEvent(this.frame, this.deltaMs, event)
      if (newState != null) {
        this.logger.debug("game state changed to {}", newState.name)
        this.state = newState
      }
    }
  }

  private fun logic(deltaMs: Double) {
    val newState = this.state.onLogic(this.frame, deltaMs)
    if (newState != null) {
      this.logger.debug("game state changed to {}", newState.name)
      this.state = newState
    }
  }

  private fun render(deltaMs: Double) {
    val canvas = this.holder.lockCanvas()
    if (canvas != null) {
      try {
        this.state.onRender(this.frame, deltaMs, canvas)
      } finally {
        this.holder.unlockCanvasAndPost(canvas)
      }
    }
  }

  private fun pause(deltaMs: Double) {
    try {
      Thread.sleep(deltaMs.toLong())
    } catch (e: InterruptedException) {
      Thread.currentThread().interrupt()
    }
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    this.logger.debug("onTouchEvent: {}", event)

    return when (event.action) {
      MotionEvent.ACTION_UP -> {
        this.events.add(BreakoutEvent.CursorReleased(PointF(event.x, event.y)))
        true
      }
      MotionEvent.ACTION_MOVE -> {
        this.events.add(BreakoutEvent.CursorMoved(PointF(event.x, event.y)))
        true
      }
      else -> {
        true
      }
    }
  }
}