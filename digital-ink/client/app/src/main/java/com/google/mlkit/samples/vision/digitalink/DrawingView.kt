package com.google.mlkit.samples.vision.digitalink

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.google.mlkit.samples.vision.digitalink.StrokeManager.ContentChangedListener
import com.google.mlkit.vision.digitalink.Ink
import com.google.mlkit.vision.digitalink.Ink.Stroke

/**
 * Main view for rendering content.
 *
 *
 * The view accepts touch inputs, renders them on screen, and passes the content to the
 * StrokeManager. The view is also able to draw content from the StrokeManager.
 */
class DrawingView @JvmOverloads constructor(
  context: Context?,
  attributeSet: AttributeSet? = null
) :
  View(context, attributeSet), ContentChangedListener {
  private val recognizedStrokePaint: Paint
  private val textPaint: TextPaint
  private val currentStrokePaint: Paint
  private val canvasPaint: Paint
  private val currentStroke: Path
  private lateinit var drawCanvas: Canvas
  private lateinit var canvasBitmap: Bitmap
  private lateinit var strokeManager: StrokeManager
  fun setStrokeManager(strokeManager: StrokeManager) {
    this.strokeManager = strokeManager
  }

  override fun onSizeChanged(
    width: Int,
    height: Int,
    oldWidth: Int,
    oldHeight: Int
  ) {
    Log.i(TAG, "onSizeChanged")
    canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    drawCanvas = Canvas(canvasBitmap)
    invalidate()
  }

  fun redrawContent() {
    clear()
    val currentInk = strokeManager.currentInk
    drawInk(currentInk, currentStrokePaint)
    val content = strokeManager.getContent()
    for (ri in content) {
      drawInk(ri.ink, recognizedStrokePaint)
    }
    invalidate()
  }

  private fun drawInk(ink: Ink, paint: Paint) {
    for (s in ink.strokes) {
      drawStroke(s, paint)
    }
  }

  private fun drawStroke(s: Stroke, paint: Paint) {
    Log.i(TAG, "drawstroke")
    var path: Path = Path()
    path.moveTo(s.points[0].x, s.points[0].y)
    for (p in s.points.drop(1)) {
      path.lineTo(p.x, p.y)
    }
    drawCanvas.drawPath(path, paint)
  }

  fun clear() {
    currentStroke.reset()
    onSizeChanged(
      canvasBitmap.width,
      canvasBitmap.height,
      canvasBitmap.width,
      canvasBitmap.height
    )
  }

  override fun onDraw(canvas: Canvas) {
    canvas.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)
    canvas.drawPath(currentStroke, currentStrokePaint)
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    val action = event.actionMasked
    val x = event.x
    val y = event.y
    when (action) {
      MotionEvent.ACTION_DOWN -> currentStroke.moveTo(x, y)
      MotionEvent.ACTION_MOVE -> currentStroke.lineTo(x, y)
      MotionEvent.ACTION_UP -> {
        currentStroke.lineTo(x, y)
        drawCanvas.drawPath(currentStroke, currentStrokePaint)
        currentStroke.reset()
      }
      else -> {
      }
    }
    strokeManager.addNewTouchEvent(event)
    invalidate()
    return true
  }

  override fun onContentChanged() {
    redrawContent()
  }

  companion object {
    private const val TAG = "MLKD.DrawingView"
    private const val STROKE_WIDTH_DP = 3
  }

  init {
    currentStrokePaint = Paint()
    currentStrokePaint.color = -0xff01 // pink.
    currentStrokePaint.isAntiAlias = true
    // Set stroke width based on display density.
    currentStrokePaint.strokeWidth = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      STROKE_WIDTH_DP.toFloat(),
      resources.displayMetrics
    )
    currentStrokePaint.style = Paint.Style.STROKE
    currentStrokePaint.strokeJoin = Paint.Join.ROUND
    currentStrokePaint.strokeCap = Paint.Cap.ROUND
    recognizedStrokePaint = Paint(currentStrokePaint)
    recognizedStrokePaint.color = -0x3301 // pale pink.
    textPaint = TextPaint()
    textPaint.color = -0xcc33cd // green.
    currentStroke = Path()
    canvasPaint = Paint(Paint.DITHER_FLAG)
  }
}
