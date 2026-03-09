# Vonage Feature Archiving

This module provides call recording and archiving functionality for Vonage video sessions.

## Overview

The archiving feature allows you to record video call sessions and manage their lifecycle. This module handles:

- Starting and stopping archive recordings
- Monitoring archiving state changes
- Managing archive IDs for active sessions

## Key Components

- **VonageArchiving**: Interface for managing archiving operations (start, stop, bind, retrieve)
- **ArchiveRepository**: Data layer for interacting with archiving backend
- **ArchivingState**: States include `Idle`, `Started`, and `Stopped`
- **ArchivingUiState**: UI states for recording flow (`IDLE`, `STARTING`, `RECORDING`, `STOPPING`)
- **Archive**: Model representing a recorded session with metadata
- **ArchiveId**: Unique identifier for active or completed archives

## Usage

```kotlin
// Inject VonageArchiving
@Inject lateinit var vonageArchiving: VonageArchiving

// Bind to a call to listen for archiving state changes
vonageArchiving.bind(call)
    .onEach { state ->
        when (state) {
            is ArchivingState.Started -> // Handle recording started
            is ArchivingState.Stopped -> // Handle recording stopped
            is ArchivingState.Idle -> // Handle idle state
        }
    }
    .collect()

// Start recording
vonageArchiving.startArchive(roomName)
    .onSuccess { archiveId -> 
        // Recording started successfully
    }
    .onFailure { error ->
        // Handle error
    }

// Stop recording
vonageArchiving.stopArchive(roomName)
    .onSuccess {
        // Recording stopped successfully
    }
    .onFailure { error ->
        // Handle error
    }

// Retrieve past recordings
vonageArchiving.getRecordings(roomName)
    .onSuccess { archives ->
        archives.forEach { archive ->
            // Display or process archive
        }
    }
    .onFailure { error ->
        // Handle error
    }
```

## Build Variants

This module supports feature toggles with `enabled` and `disabled` source sets for conditional compilation.
