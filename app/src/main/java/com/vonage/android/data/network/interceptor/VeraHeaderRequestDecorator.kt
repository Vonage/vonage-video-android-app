package com.vonage.android.data.network.interceptor

import android.os.Build
import com.vonage.android.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class VeraHeaderRequestDecorator : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
            .newBuilder()
            .header("User-Agent", "VeraNativeAndroid/${BuildConfig.VERSION_NAME} android ${Build.VERSION.RELEASE}")
            .build()
        return chain.proceed(request)
    }
}
