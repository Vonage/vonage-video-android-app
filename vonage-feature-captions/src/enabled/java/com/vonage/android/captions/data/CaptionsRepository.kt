package com.vonage.android.captions.data

import javax.inject.Inject

class CaptionsRepository @Inject constructor(
    private val apiService: CaptionsApi,
) {

    suspend fun enableCaptions(roomName: String): Result<String> =
        runCatching {
            val response = apiService.enableCaptions(roomName)
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.captionsId)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed enabling captions"))
            }
        }

    suspend fun disableCaptions(roomName: String, captionsId: String): Result<String> =
        runCatching {
            val response = apiService.disableCaptions(roomName, captionsId)
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.disableResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed enabling captions"))
            }
        }
}
