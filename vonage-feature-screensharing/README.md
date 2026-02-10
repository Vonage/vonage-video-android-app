# Vonage Feature Screensharing

This module provides screen sharing functionality for Vonage video sessions.

## Overview

The screensharing feature allows participants to share their device screen during a video call. This module handles:

- Starting and stopping screen sharing
- Managing media projection permissions
- Handling foreground service for screen capture
- Managing screen sharing state transitions
- Notification channel creation for screen sharing service

## Key Components

- **VonageScreenSharing**: Interface for managing screen sharing operations (start, stop, notifications)
- **ScreenSharingService**: Foreground service that handles the screen capture lifecycle
- **ScreenSharingState**: States include `IDLE`, `STARTING`, `SHARING`, and `STOPPING`
- **screenSharingAction**: Composable UI component for bottom bar integration
- **MediaProjectionManager**: Android system service for capturing screen content

## Usage

```kotlin
// Inject VonageScreenSharing
@Inject lateinit var vonageScreenSharing: VonageScreenSharing

// Create notification channel (required for Android O+)
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    vonageScreenSharing.createNotificationChannel(notificationManager)
}

// Start screen sharing with MediaProjection intent
// (obtained from MediaProjectionManager.createScreenCaptureIntent())
vonageScreenSharing.startScreenSharing(
    intent = mediaProjectionIntent,
    call = callFacade,
    onStarted = {
        // Screen sharing started successfully
    },
    onStopped = {
        // Screen sharing stopped
    }
)

// Stop screen sharing
vonageScreenSharing.stopSharingScreen()
```

## UI Integration

The module provides a Compose UI action for bottom bar integration:

```kotlin
val action = screenSharingAction(
    onStartScreenSharing = { /* Request media projection */ },
    onStopScreenSharing = { vonageScreenSharing.stopSharingScreen() },
    startScreenSharingLabel = "Share Screen",
    stopScreenSharingLabel = "Stop Sharing",
    screenSharingState = currentState
)
```

## Build Variants

This module supports feature toggles with `enabled` and `disabled` source sets for conditional compilation.
