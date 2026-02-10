package com.vonage.android.screensharing.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import androidx.core.graphics.toColorInt

/**
 * Service that draws a colored border overlay on the screen while
 * screen sharing is active, to remind the user that the screen is
 * being captured.
 */
class ScreenSharingOverlayService : Service() {

    private var overlayView: View? = null
    private var windowManager: WindowManager? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
        START_NOT_STICKY

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showOverlay()
    }

    override fun onDestroy() {
        removeOverlay()
        super.onDestroy()
    }

    private fun showOverlay() {
        val borderWidth = BORDER_WIDTH_PX

        overlayView = object : View(this) {
            private val paint = Paint().apply {
                color = BORDER_COLOR
                style = Paint.Style.STROKE
                strokeWidth = borderWidth
            }

            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)
                val half = borderWidth / 2f
                canvas.drawRect(
                    half,
                    half,
                    width - half,
                    height - half,
                    paint,
                )
            }
        }.apply {
            setBackgroundColor(Color.TRANSPARENT)
        }

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        )

        windowManager?.addView(overlayView, params)
    }

    private fun removeOverlay() {
        overlayView?.let { view ->
            windowManager?.removeView(view)
            overlayView = null
        }
    }

    companion object {
        private const val BORDER_WIDTH_PX = 8f
        private val BORDER_COLOR = "#00FF00".toColorInt()

        fun intent(context: Context): Intent =
            Intent(context, ScreenSharingOverlayService::class.java)
    }
}
