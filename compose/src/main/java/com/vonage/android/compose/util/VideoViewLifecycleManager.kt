package com.vonage.android.compose.util

import android.graphics.SurfaceTexture
import android.util.Log
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Utility class to safely manage video view lifecycle and prevent surface crashes
 */
@Suppress("TooGenericExceptionCaught")
object VideoViewLifecycleManager {

    /**
     * Safely attach a video view to a container with lifecycle awareness
     */
    fun attachVideoView(
        container: ViewGroup,
        videoView: View,
        lifecycleOwner: LifecycleOwner
    ) {
        // Only attach if lifecycle is active
        if (!lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            return
        }

        try {
            // Remove from any existing parent
            (videoView.parent as? ViewGroup)?.removeView(videoView)

            // Add to new container if not already there
            if (videoView.parent != container) {
                container.addView(videoView)
            }

            // For TextureView, ensure surface is ready
            if (videoView is TextureView && !videoView.isAvailable) {
                // Surface not ready, defer attachment
                videoView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                        // Surface is now ready for rendering
                    }

                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                        // empty
                    }

                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = false
                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                        // empty
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to attach video view: ${e.message}")
        }
    }

    /**
     * Safely detach a video view from its container
     */
    fun detachVideoView(videoView: View) {
        try {
            (videoView.parent as? ViewGroup)?.removeView(videoView)
            // Clear texture listener if applicable
            if (videoView is TextureView) {
                videoView.surfaceTextureListener = null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to detach video view: ${e.message}")
        }
    }

    /**
     * Create a lifecycle observer that manages video view attachment/detachment
     */
    fun createLifecycleObserver(
        videoView: View,
        onPause: () -> Unit = {},
        onResume: () -> Unit = {}
    ): LifecycleEventObserver {
        return LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    onPause()
                    // Temporarily detach to prevent surface issues
                    detachVideoView(videoView)
                }

                Lifecycle.Event.ON_RESUME -> {
                    onResume()
                    // Reattachment will be handled by the composable
                }

                Lifecycle.Event.ON_DESTROY -> {
                    detachVideoView(videoView)
                }

                else -> { /* no-op */
                }
            }
        }
    }

    const val TAG = "VideoViewLifecycleManager"
}