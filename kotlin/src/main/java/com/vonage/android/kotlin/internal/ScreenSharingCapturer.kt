package com.vonage.android.kotlin.internal

import android.content.Context
import android.graphics.PixelFormat
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.view.WindowManager
import com.opentok.android.BaseVideoCapturer

@Suppress("EmptyFunctionBlock")
class ScreenSharingCapturer(
    context: Context,
    private val mediaProjection: MediaProjection,
) : BaseVideoCapturer() {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private lateinit var imageReader: ImageReader
    private var virtualDisplay: VirtualDisplay? = null
    private var backgroundHandler: Handler? = null
    private var backgroundThread: HandlerThread? = null
    private var capturing = false
    private val density: Int by lazy {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.densityDpi
    }

    override fun init() {
        imageReader = ImageReader.newInstance(WIDTH, HEIGHT, IMAGE_READER_PIXEL_FORMAT, 1)
        createVirtualDisplay()
        startBackgroundThread()
    }

    override fun startCapture(): Int {
        capturing = true
        return 0
    }

    override fun stopCapture(): Int {
        capturing = false
        virtualDisplay?.release()
        mediaProjection.stop()
        stopBackgroundThread()
        return 0
    }

    override fun destroy() {

    }

    override fun isCaptureStarted(): Boolean = capturing

    override fun getCaptureSettings(): CaptureSettings =
        CaptureSettings().apply {
            fps = FPS
            width = WIDTH
            height = HEIGHT
            format = SDK_PIXEL_FORMAT
        }

    override fun onPause() {

    }

    override fun onResume() {

    }

    private fun createVirtualDisplay() {
        // this register is empty intentionally
        // having a MediaProjection.Callback is mandatory for creating virtual displays
        mediaProjection.registerCallback(object : MediaProjection.Callback() {}, null)
        // create a virtual display with default values
        virtualDisplay = mediaProjection.createVirtualDisplay(
            VIRTUAL_SCREEN_NAME,
            WIDTH,
            HEIGHT,
            density,
            0,
            imageReader.surface,
            null,
            backgroundHandler
        )
        // send to the SDK every frame
        imageReader.setOnImageAvailableListener({ reader: ImageReader ->
            reader.acquireLatestImage()?.let { frame ->
                if (frame.planes.isNotEmpty()) {
                    provideBufferFrame(
                        frame.planes[0].buffer,
                        SDK_PIXEL_FORMAT,
                        WIDTH,
                        HEIGHT,
                        0, // rotation
                        false // mirror image
                    )
                    frame.close()
                }
            }
        }, backgroundHandler)
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread(SCREEN_CAPTURE_THREAD_NAME)
        backgroundThread?.apply {
            start()
            backgroundHandler = Handler(getLooper())
        }
    }

    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        backgroundThread?.join()
        backgroundThread = null
        backgroundHandler = null
    }

    private companion object {
        const val VIRTUAL_SCREEN_NAME = "ScreenSharingCapturer"
        const val SCREEN_CAPTURE_THREAD_NAME = "$VIRTUAL_SCREEN_NAME-Thread"
        const val SDK_PIXEL_FORMAT = ABGR
        const val IMAGE_READER_PIXEL_FORMAT = PixelFormat.RGBA_8888
        const val FPS = 15
        const val WIDTH = 1280
        const val HEIGHT = 720
    }
}
