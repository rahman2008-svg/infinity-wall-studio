package com.example

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder

class InteractiveLiveWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return LiveEngine()
    }

    inner class LiveEngine : Engine() {
        private var visible = false
        private val handler = android.os.Handler(android.os.Looper.getMainLooper())
        private val drawRunner = Runnable { draw() }

        // Touch ripple animation variables
        private var touchX = -1f
        private var touchY = -1f
        private var rippleRadius = 0f
        private var maxRippleRadius = 350f

        // Stars variables
        private val starX = floatArrayOf(0.15f, 0.75f, 0.25f, 0.85f, 0.45f, 0.60f, 0.35f, 0.90f)
        private val starY = floatArrayOf(0.10f, 0.20f, 0.55f, 0.45f, 0.70f, 0.85f, 0.30f, 0.75f)
        private val starRadius = floatArrayOf(4f, 6f, 3f, 5f, 7f, 4f, 5f, 4f)
        private var starAlphaPhase = 0.0f

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                handler.post(drawRunner)
            } else {
                handler.removeCallbacks(drawRunner)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            this.visible = false
            handler.removeCallbacks(drawRunner)
        }

        override fun onTouchEvent(event: MotionEvent) {
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                touchX = event.x
                touchY = event.y
                rippleRadius = 10f
            }
            super.onTouchEvent(event)
        }

        private fun draw() {
            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    val width = canvas.width.toFloat()
                    val height = canvas.height.toFloat()

                    // Draw deep celestial space background gradient
                    val backgroundPaint = Paint().apply {
                        shader = RadialGradient(
                            width / 2f, height / 2f, height * 0.8f,
                            intArrayOf(Color.parseColor("#1E1B4B"), Color.parseColor("#090514")), // Deep cosmic violet-indigo to pitch dark
                            null, Shader.TileMode.CLAMP
                        )
                    }
                    canvas.drawRect(0f, 0f, width, height, backgroundPaint)

                    // Draw warm neon glowing nebula in center
                    val nebulaPaint = Paint().apply {
                        isAntiAlias = true
                        shader = RadialGradient(
                            width * 0.5f, height * 0.4f, width * 0.6f,
                            intArrayOf(Color.parseColor("#25134F"), Color.parseColor("#00000000")), // Subtle cosmic mist
                            null, Shader.TileMode.CLAMP
                        )
                    }
                    canvas.drawCircle(width * 0.5f, height * 0.4f, width * 0.6f, nebulaPaint)

                    // Draw animating stars
                    starAlphaPhase += 0.05f
                    for (i in starX.indices) {
                        val currentAlpha = (127 + (128 * Math.sin((starAlphaPhase + i * 2).toDouble()))).toInt()
                        val starPaint = Paint().apply {
                            color = Color.WHITE
                            alpha = currentAlpha.coerceIn(0, 255)
                            isAntiAlias = true
                        }
                        canvas.drawCircle(width * starX[i], height * starY[i], starRadius[i], starPaint)
                    }

                    // If user touched, draw fluid wave expansion
                    if (touchX >= 0f && touchY >= 0f && rippleRadius < maxRippleRadius) {
                        val progress = rippleRadius / maxRippleRadius
                        val alphaVal = (255 * (1.0f - progress)).toInt().coerceIn(0, 255)
                        
                        val touchPaint = Paint().apply {
                            color = Color.parseColor("#6366F1") // Indigo-500
                            alpha = alphaVal
                            style = Paint.Style.STROKE
                            strokeWidth = 6f * (1.0f - progress) + 2f
                            isAntiAlias = true
                        }
                        canvas.drawCircle(touchX, touchY, rippleRadius, touchPaint)

                        val innerTouchPaint = Paint().apply {
                            color = Color.parseColor("#38BDF8") // Sky-400
                            alpha = (alphaVal * 0.6f).toInt()
                            style = Paint.Style.FILL
                            isAntiAlias = true
                        }
                        canvas.drawCircle(touchX, touchY, rippleRadius * 0.4f, innerTouchPaint)

                        rippleRadius += 10f
                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }

            if (visible) {
                handler.postDelayed(drawRunner, 32) // Render at ~30 FPS
            }
        }
    }
}
