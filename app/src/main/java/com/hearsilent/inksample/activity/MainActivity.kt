package com.hearsilent.inksample.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.ink.authoring.InProgressStrokeId
import androidx.ink.authoring.InProgressStrokesFinishedListener
import androidx.ink.authoring.InProgressStrokesView
import androidx.ink.brush.Brush
import androidx.ink.brush.StockBrushes
import androidx.ink.strokes.Stroke
import com.hearsilent.inksample.libs.views.FinishedStrokesView
import com.hearsilent.inksample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var inProgressStrokesView: InProgressStrokesView
    private lateinit var finishedStrokesView: FinishedStrokesView

    // Current brush settings
    @ColorInt
    private var currentColor: Int = Color.BLACK
    private var currentStrokeSize: Float = 5f

    // Track pointer IDs for multi-touch support
    private val pointerIdToStrokeId = mutableMapOf<Int, InProgressStrokeId>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
        setupColorPalette()
        setupStrokeSlider()
        setupClearButton()
        setupDrawingTouch()
        setupInProgressStrokesListener()
    }

    private fun setupViews() {
        inProgressStrokesView = binding.inProgressStrokesView
        finishedStrokesView = binding.finishedStrokesView
    }

    private fun setupColorPalette() {
        binding.colorBlack.setOnClickListener { setCurrentColor(Color.BLACK) }
        binding.colorRed.setOnClickListener { setCurrentColor(0xFFE53935.toInt()) }
        binding.colorGreen.setOnClickListener { setCurrentColor(0xFF43A047.toInt()) }
        binding.colorBlue.setOnClickListener { setCurrentColor(0xFF1E88E5.toInt()) }
        binding.colorYellow.setOnClickListener { setCurrentColor(0xFFFDD835.toInt()) }
    }

    private fun setCurrentColor(@ColorInt color: Int) {
        currentColor = color
    }

    private fun setupStrokeSlider() {
        binding.strokeSizeSlider.addOnChangeListener { _, value, _ ->
            currentStrokeSize = value
        }
    }

    private fun setupClearButton() {
        binding.clearButton.setOnClickListener {
            finishedStrokesView.clearStrokes()
        }
    }

    private fun createBrush(): Brush {
        return Brush.Companion.createWithColorIntArgb(
            family = StockBrushes.marker(),
            colorIntArgb = currentColor,
            size = currentStrokeSize,
            epsilon = 0.1f
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDrawingTouch() {
        binding.drawingContainer.setOnTouchListener { _, event ->
            handleTouchEvent(event)
            true
        }
    }

    private fun handleTouchEvent(event: MotionEvent) {
        val action = event.actionMasked
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                // Start a new stroke
                val strokeId = inProgressStrokesView.startStroke(
                    event = event,
                    pointerId = pointerId,
                    brush = createBrush()
                )
                pointerIdToStrokeId[pointerId] = strokeId
            }

            MotionEvent.ACTION_MOVE -> {
                // Add points to all active strokes
                for (i in 0 until event.pointerCount) {
                    val pid = event.getPointerId(i)
                    if (pointerIdToStrokeId.containsKey(pid)) {
                        inProgressStrokesView.addToStroke(
                            event = event,
                            pointerId = pid,
                            prediction = null
                        )
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                // Finish the stroke for this pointer
                if (pointerIdToStrokeId.containsKey(pointerId)) {
                    inProgressStrokesView.finishStroke(
                        event = event,
                        pointerId = pointerId
                    )
                    pointerIdToStrokeId.remove(pointerId)
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                // Cancel all strokes
                inProgressStrokesView.cancelStroke(
                    event = event,
                    pointerId = pointerId
                )
                pointerIdToStrokeId.remove(pointerId)
            }
        }
    }

    private fun setupInProgressStrokesListener() {
        inProgressStrokesView.addFinishedStrokesListener(object :
            InProgressStrokesFinishedListener {
            override fun onStrokesFinished(strokes: Map<InProgressStrokeId, Stroke>) {
                // Move finished strokes to the finished strokes view
                finishedStrokesView.addStrokes(strokes.values)
                // Remove the finished strokes from InProgressStrokesView
                inProgressStrokesView.removeFinishedStrokes(strokes.keys)
            }
        })
    }
}