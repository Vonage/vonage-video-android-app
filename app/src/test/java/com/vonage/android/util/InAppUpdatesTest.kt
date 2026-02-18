package com.vonage.android.util

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.requestUpdateFlow
import io.mockk.clearAllMocks
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class InAppUpdatesTest {

    private val mockActivity: ComponentActivity = mockk(relaxed = true)
    private val mockLauncher: ActivityResultLauncher<IntentSenderRequest> = mockk(relaxed = true)
    private val mockAppUpdateManager: AppUpdateManager = mockk(relaxed = true)
    private val mockToast: Toast = mockk(relaxed = true)
    private lateinit var capturedCallback: ActivityResultCallback<ActivityResult>

    @Before
    fun setUp() {
        mockkStatic(AppUpdateManagerFactory::class)
        mockkStatic(AppUpdateManager::requestUpdateFlow)
        mockkStatic(Toast::class)

        every { AppUpdateManagerFactory.create(any()) } returns mockAppUpdateManager
        every { Toast.makeText(any(), any<String>(), any()) } returns mockToast

        every {
            mockActivity.registerForActivityResult(
                any<StartIntentSenderForResult>(),
                any<ActivityResultCallback<ActivityResult>>(),
            )
        } answers {
            @Suppress("UNCHECKED_CAST")
            capturedCallback = secondArg<ActivityResultCallback<ActivityResult>>()
            mockLauncher
        }
    }

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
    }

    private fun createInAppUpdates(updateResult: AppUpdateResult): InAppUpdates {
        every { mockAppUpdateManager.requestUpdateFlow() } returns flowOf(updateResult)
        return InAppUpdates(mockActivity)
    }

    @Test
    fun `register should start immediate update when update is available`() = runTest {
        val available: AppUpdateResult.Available = mockk(relaxed = true)
        val inAppUpdates = createInAppUpdates(available)

        inAppUpdates.register()

        coVerify { available.startImmediateUpdate(mockLauncher) }
    }

    @Test
    fun `register should handle downloaded state without error`() = runTest {
        val downloaded = AppUpdateResult.Downloaded(mockk(relaxed = true))
        val inAppUpdates = createInAppUpdates(downloaded)

        inAppUpdates.register()
    }

    @Test
    fun `register should handle in progress state without error`() = runTest {
        val inProgress = AppUpdateResult.InProgress(mockk(relaxed = true))
        val inAppUpdates = createInAppUpdates(inProgress)

        inAppUpdates.register()
    }

    @Test
    fun `register should handle not available state without error`() = runTest {
        val inAppUpdates = createInAppUpdates(AppUpdateResult.NotAvailable)

        inAppUpdates.register()
    }

    @Test
    fun `handleActivityResult shows success toast on RESULT_OK`() {
        createInAppUpdates(AppUpdateResult.NotAvailable)

        capturedCallback.onActivityResult(ActivityResult(RESULT_OK, null))

        verify { Toast.makeText(mockActivity, "Update Success!", Toast.LENGTH_LONG) }
        verify { mockToast.show() }
    }

    @Test
    fun `handleActivityResult shows cancelled toast on RESULT_CANCELED`() {
        createInAppUpdates(AppUpdateResult.NotAvailable)

        capturedCallback.onActivityResult(ActivityResult(RESULT_CANCELED, null))

        verify { Toast.makeText(mockActivity, "Update cancelled", Toast.LENGTH_LONG) }
        verify { mockToast.show() }
    }

    @Test
    fun `handleActivityResult shows failure toast on RESULT_IN_APP_UPDATE_FAILED`() {
        createInAppUpdates(AppUpdateResult.NotAvailable)

        capturedCallback.onActivityResult(ActivityResult(RESULT_IN_APP_UPDATE_FAILED, null))

        verify { Toast.makeText(mockActivity, "Update failed, please try again", Toast.LENGTH_LONG) }
        verify { mockToast.show() }
    }
}
