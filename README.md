# Ink Sample

A sample Android application demonstrating the usage of the [androidx.ink](https://developer.android.com/jetpack/androidx/releases/ink) library for low-latency digital ink and stylus input.

## Features

- **Low-latency Drawing**: Uses `InProgressStrokesView` for smooth, responsive drawing experience
- **Pressure Sensitivity**: Supports stylus pressure for variable stroke width
- **Color Palette**: Choose from 5 different colors (Black, Red, Green, Blue, Yellow)
- **Adjustable Stroke Size**: Slider control to adjust stroke width from 1 to 30
- **Clear Canvas**: Button to clear all drawn strokes
- **Multi-touch Support**: Draw with multiple fingers simultaneously

## Screenshots

<img height="800" src="https://github.com/user-attachments/assets/df3bc759-1661-4968-8647-7f8dce91468b" />

## Requirements

- Android SDK 29 (Android 10) or higher
- Android Studio Iguana or newer

## Dependencies

This sample uses the following androidx.ink modules:

```kotlin
implementation("androidx.ink:ink-authoring:1.0.0")
implementation("androidx.ink:ink-brush:1.0.0")
implementation("androidx.ink:ink-geometry:1.0.0")
implementation("androidx.ink:ink-nativeloader:1.0.0")
implementation("androidx.ink:ink-rendering:1.0.0")
implementation("androidx.ink:ink-strokes:1.0.0")
```

## Architecture

The sample consists of:

1. **MainActivity**: Handles touch events and coordinates between the ink components
2. **InProgressStrokesView**: AndroidX Ink component that renders "wet ink" (strokes being drawn)
3. **FinishedStrokesView**: Custom View that renders completed "dry" strokes using `CanvasStrokeRenderer`

### How It Works

1. When the user touches the screen, `MainActivity` starts a new stroke via `InProgressStrokesView.startStroke()`
2. As the user moves their finger/stylus, touch events are fed to `addToStroke()`
3. When the touch ends, `finishStroke()` is called
4. `InProgressStrokesView` notifies the listener when strokes are finished
5. Finished strokes are moved to `FinishedStrokesView` for permanent rendering

## Key APIs

### Creating a Brush

```kotlin
val brush = Brush.createWithColorIntArgb(
    family = StockBrushes.marker(),  // or StockBrushes.pressurePen()
    colorIntArgb = Color.BLACK,
    size = 5f,
    epsilon = 0.1f
)
```

### Starting a Stroke

```kotlin
val strokeId = inProgressStrokesView.startStroke(
    event = motionEvent,
    pointerId = pointerId,
    brush = brush
)
```

### Adding Points to a Stroke

```kotlin
inProgressStrokesView.addToStroke(
    event = motionEvent,
    pointerId = pointerId,
    prediction = null
)
```

### Finishing a Stroke

```kotlin
inProgressStrokesView.finishStroke(
    event = motionEvent,
    pointerId = pointerId
)
```

### Listening for Finished Strokes

```kotlin
inProgressStrokesView.addFinishedStrokesListener(object : InProgressStrokesFinishedListener {
    override fun onStrokesFinished(strokes: Map<InProgressStrokeId, Stroke>) {
        // Handle finished strokes
        finishedStrokesView.addStrokes(strokes.values)
        inProgressStrokesView.removeFinishedStrokes(strokes.keys)
    }
})
```

### Rendering Finished Strokes

```kotlin
val canvasStrokeRenderer = CanvasStrokeRenderer.create()
val identityMatrix = Matrix()

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
```

## Resources

- [Ink API Documentation](https://developer.android.com/develop/ui/compose/touch-input/stylus-input/about-ink-api)
- [Ink API Release Notes](https://developer.android.com/jetpack/androidx/releases/ink)
- [Introducing Ink API Blog Post](https://android-developers.googleblog.com/2024/10/introducing-ink-api-jetpack-library.html)

## License

```
MIT License

Copyright (c) 2026 HearSilent

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
