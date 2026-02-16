package com.vonage.android.util

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.requestUpdateFlow
import com.vonage.logger.vonageLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest

class InAppUpdates(
    private val activity: ComponentActivity,
) {

    private var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private var updateFlow: Flow<AppUpdateResult>

    init {
        activityResultLauncher = activity.registerForActivityResult(
            contract = StartIntentSenderForResult()
        ) { result: ActivityResult ->
            handleActivityResult(result)
        }
        updateFlow = AppUpdateManagerFactory
            .create(activity)
            .requestUpdateFlow()
            .catch { emit(AppUpdateResult.NotAvailable) }
    }

    suspend fun register() {
        updateFlow.collectLatest { appUpdateResult ->
            when (appUpdateResult) {
                is AppUpdateResult.Available -> {
                    vonageLogger.d(TAG, "Update available! Starting update...")
                    appUpdateResult.startImmediateUpdate(activityResultLauncher)
                }

                is AppUpdateResult.Downloaded -> vonageLogger.d(TAG, "Update downloaded")
                is AppUpdateResult.InProgress -> vonageLogger.d(TAG, "Update in progress...")
                is AppUpdateResult.NotAvailable -> vonageLogger.d(TAG, "Update not available")
            }
        }
    }

    private fun handleActivityResult(result: ActivityResult) {
        when (result.resultCode) {
            RESULT_OK -> showToast("Update Success!")
            RESULT_CANCELED -> showToast("Update cancelled")
            RESULT_IN_APP_UPDATE_FAILED -> showToast("Update failed, please try again")
        }
    }

    private fun showToast(userMessage: String) =
        Toast.makeText(activity, userMessage, Toast.LENGTH_LONG).show()

    private companion object {
        const val TAG = "InAppUpdates"
    }
}
