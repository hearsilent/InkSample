package com.hearsilent.inksample.libs.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.View
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.strokes.Stroke

/**
 * A custom View that renders finished (dry) strokes using CanvasStrokeRenderer.
 */
class FinishedStrokesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val strokes = mutableListOf<Stroke>()
    private val canvasStrokeRenderer = CanvasStrokeRenderer.Companion.create()
    private val identityMatrix = Matrix()

    /**
     * Add strokes to be rendered.
     */
    fun addStrokes(newStrokes: Collection<Stroke>) {
        strokes.addAll(newStrokes)
        invalidate()
    }

    /**
     * Clear all strokes.
     */
    fun clearStrokes() {
        strokes.clear()
        invalidate()
    }

    /**
     * Get the current list of strokes.
     */
    fun getStrokes(): List<Stroke> = strokes.toList()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        strokes.forEach { stroke ->
            canvasStrokeRenderer.draw(
                canvas = canvas,
                stroke = stroke,
                strokeToScreenTransform = identityMatrix
            )
        }
    }
}