# Architecture Improvement Plan

## Problem Statement

The codebase follows a multi-module structure with good feature isolation, but lacks a proper **domain layer**. Business logic is scattered across ViewModels, large facade classes, and concrete repositories. The primary consequence is that **business logic is untestable without Android or SDK dependencies**.

Key metrics of the current state:
- `Call.kt` — ~800 lines, 6+ responsibilities
- `MeetingRoomViewModel` — ~361 lines, 7+ responsibilities
- No Use Case / Interactor classes
- No repository interfaces (DIP violated)
- `MeetingRoomContainer` is a manual DI container with no abstractions

---

## Goals

**Primary:** Make business logic unit-testable (no Android/SDK in test scope).  
**Secondary:** Align the codebase with Clean Architecture and SOLID principles.  
**Constraint:** Public API surface of `vonage-meeting-room` must not break (sample app stays green).

---

## Scope

Refactoring targets the **`vonage-meeting-room` module** first, then propagates patterns to `vonage-video-core` and `app`. Each phase leaves the codebase in a passing, deployable state.

---

## Phase 1 — Introduce Abstractions (DIP Fix)

**Goal:** Replace all concrete dependency usages with interfaces so that implementations can be swapped for fakes in tests.

### 1.1 SessionRepository Interface

Create `SessionRepository` interface in `vonage-meeting-room/src/main/`.

```kotlin
interface SessionRepository {
    suspend fun getSession(roomName: String): Result<SessionInfo>
}
```

Rename current `MeetingRoomSessionRepository` to implement this interface. Update `MeetingRoomContainer` to declare the type as the interface.

### 1.2 VideoClientFacade Interface

Extract a `VideoClientFacade` interface over `VonageVideoClient`:

```kotlin
interface VideoClientFacade {
    fun initializeSession(apiKey: String, sessionId: String, token: String): CallFacade
}
```

`VonageVideoClient` implements `VideoClientFacade`. `MeetingRoomContainer` declares the field as `VideoClientFacade`.

### 1.3 Sub-component Interfaces for Call.kt Split (Preparatory)

Add interfaces that will be implemented by the classes introduced in Phase 2:

- `SessionManager` — session lifecycle (connect, disconnect, pause, resume)
- `ParticipantManager` — participant tracking, audio levels, pinning
- `SignalHandler` — chat and reaction signal routing
- `SubscriberManager` — stream subscription/unsubscription lifecycle

`CallFacade` is updated to depend on these interfaces instead of monolithic `Call`.

### Acceptance Criteria
- All existing tests pass
- `MeetingRoomContainer` holds only interface types (no concrete classes in field declarations)
- New interfaces live in `src/main` and are package-private (`internal`)

---

## Phase 2 — Split Call.kt

**Goal:** Replace the ~800-line `Call.kt` god class with four focused collaborators, each testable in isolation.

### Breakdown

| New Class | Responsibility | Extracted From |
|---|---|---|
| `SessionLifecycleManager` | connect, disconnect, pause, resume, error forwarding | `Call.connect()`, `Call.disconnect()`, `CallFacade` session events |
| `ParticipantTracker` | add/remove/pin participants, audio level tracking, active speaker detection | `Call` participant map, audio level coroutines (~600 lines) |
| `SignalDispatcher` | route incoming/outgoing chat and reaction signals to plugins | `Call.startListeningSignals()`, plugin invocation |
| `StreamSubscriptionManager` | subscribe/unsubscribe streams, manage `VonageSubscriber` lifecycle | `Call.onStreamReceived()`, `Call.onStreamDropped()` |

### Wiring

`Call` becomes a thin coordinator that owns the four collaborators and delegates:

```kotlin
internal class Call(
    private val sessionManager: SessionManager,
    private val participantTracker: ParticipantTracker,
    private val signalDispatcher: SignalDispatcher,
    private val streamSubscriptionManager: StreamSubscriptionManager,
) : CallFacade
```

`MeetingRoomContainer` constructs each sub-component and injects them.

### Acceptance Criteria
- `Call.kt` does not exceed 150 lines
- Each sub-component has its own file
- Existing integration behavior is unchanged (session connects, participants appear, signals route correctly)

---

## Phase 3 — Introduce Use Cases + Proof-of-Concept Test

**Goal:** Extract orchestration logic from `MeetingRoomViewModel` into use cases. Prove testability by writing a full unit test for `ConnectToSessionUseCase`.

### 3.1 ConnectToSessionUseCase

```kotlin
internal class ConnectToSessionUseCase(
    private val sessionRepository: SessionRepository,
    private val videoClient: VideoClientFacade,
) {
    suspend fun execute(roomName: String): Result<CallFacade> =
        sessionRepository.getSession(roomName)
            .mapCatching { sessionInfo ->
                videoClient.initializeSession(
                    apiKey = sessionInfo.apiKey,
                    sessionId = sessionInfo.sessionId,
                    token = sessionInfo.token,
                )
            }
}
```

`MeetingRoomViewModel.setup()` delegates to `ConnectToSessionUseCase.execute()` instead of orchestrating the two steps itself.

### 3.2 Additional Use Cases (same pass)

| Use Case | Replaces |
|---|---|
| `DisconnectSessionUseCase` | `MeetingRoomViewModel.endCall()` disconnect logic |
| `GetSessionInfoUseCase` | Direct `sessionRepository.getSession()` call in ViewModel |

### 3.3 Proof-of-Concept Unit Test

Write a unit test for `ConnectToSessionUseCase` using fakes (no Mockito required):

```kotlin
class ConnectToSessionUseCaseTest {

    private val fakeRepository = FakeSessionRepository()
    private val fakeVideoClient = FakeVideoClientFacade()
    private val useCase = ConnectToSessionUseCase(fakeRepository, fakeVideoClient)

    @Test
    fun `returns CallFacade on success`() = runTest {
        fakeRepository.result = Result.success(SessionInfo(...))
        val result = useCase.execute("my-room")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        fakeRepository.result = Result.failure(Exception("network error"))
        val result = useCase.execute("my-room")
        assertTrue(result.isFailure)
    }
}
```

### Acceptance Criteria
- `MeetingRoomViewModel` no longer directly calls `sessionRepository` or `videoClient`
- `ConnectToSessionUseCase` has ≥ 2 unit tests with zero Android/SDK imports in the test file
- ViewModel line count reduced by at least 40 lines

---

## What Is Out of Scope (for now)

- Migrating `vonage-meeting-room` to Hilt — the manual container stays, but all its fields become interface-typed
- Runtime feature flags — enabled/disabled build variants are left unchanged
- `app` module refactoring — follows the same patterns in a subsequent cycle after `vonage-meeting-room` is proven

---

## Architectural Diagram (Target State)

```
┌─────────────────────────────────────────────────┐
│  Presentation                                   │
│  MeetingRoomViewModel (thin orchestrator)        │
└──────────────┬──────────────────────────────────┘
               │ delegates to
┌──────────────▼──────────────────────────────────┐
│  Domain (pure Kotlin, no SDK/Android imports)   │
│  ConnectToSessionUseCase                        │
│  DisconnectSessionUseCase                       │
│  GetSessionInfoUseCase                          │
└──────┬────────────────────────────┬─────────────┘
       │ interface                  │ interface
┌──────▼──────────┐      ┌─────────▼────────────┐
│  Data           │      │  SDK Wrapper          │
│  SessionRepo    │      │  VideoClientFacade    │
│  (Retrofit)     │      │  (VonageVideoClient)  │
└─────────────────┘      └──────────────────────┘
                                    │
                         ┌──────────▼──────────────┐
                         │  Call Collaborators      │
                         │  SessionLifecycleManager │
                         │  ParticipantTracker      │
                         │  SignalDispatcher        │
                         │  SubscriberManager       │
                         └─────────────────────────┘
```

---

## Testing Strategy

- **Domain use cases** → pure JUnit + coroutines-test, hand-written fakes
- **Repository implementations** → MockWebServer for HTTP layer
- **Sub-components of Call.kt** → fakes for SDK session/subscriber interfaces
- **ViewModels** → test via use case fakes, no real network or SDK needed

---

## Recommended Execution Order

1. `[ ]` Phase 1: Extract `SessionRepository` interface
2. `[ ]` Phase 1: Extract `VideoClientFacade` interface
3. `[ ]` Phase 1: Extract sub-component interfaces for Call split
4. `[ ]` Phase 2: Implement `SessionLifecycleManager`
5. `[ ]` Phase 2: Implement `ParticipantTracker`
6. `[ ]` Phase 2: Implement `SignalDispatcher`
7. `[ ]` Phase 2: Implement `SubscriberManager`
8. `[ ]` Phase 2: Slim down `Call.kt` to coordinator
9. `[ ]` Phase 3: Introduce `ConnectToSessionUseCase`
10. `[ ]` Phase 3: Introduce `DisconnectSessionUseCase` + `GetSessionInfoUseCase`
11. `[ ]` Phase 3: Write unit tests for `ConnectToSessionUseCase`
12. `[ ]` Phase 3: Slim down `MeetingRoomViewModel`

> Run `./gradlew :vonage-meeting-room:test` after each step to confirm nothing breaks.
