package com.vonage.android.kotlin

import android.content.Context
import android.util.Log
import com.opentok.android.Session
import com.opentok.android.Session.SessionOptions

class VonageVideoClient(
    private val context: Context,
//    lifecycle: Lifecycle,
) {

    private var session: Session? = null

    init {
//        lifecycle.addObserver(object : DefaultLifecycleObserver {
//            override fun onPause(owner: LifecycleOwner) {
//                super.onPause(owner)
//                session?.onPause()
//            }
//
//            override fun onResume(owner: LifecycleOwner) {
//                super.onResume(owner)
//                session?.onResume()
//            }
//        })
    }

    fun initializeSession(apiKey: String, sessionId: String, token: String): Call {
        Log.i(TAG, "apiKey: $apiKey")
        Log.i(TAG, "sessionId: $sessionId")
        Log.i(TAG, "token: $token")

        session = Session.Builder(context, apiKey, sessionId)
            .sessionOptions(
                object : SessionOptions() {
                    override fun useTextureViews(): Boolean = true
                }
            ).build()

        return Call(
            context = context,
            token = token,
            session = session!!,
        )
    }

    private companion object {
        const val TAG: String = "VonageVideoClient"
    }
}
