package com.opentok.android

import android.content.Context
import android.view.View

class NoOpVideoRenderer(val context: Context) : BaseVideoRenderer() {

    override fun onFrame(p0: Frame?) {

    }

    override fun setStyle(p0: String?, p1: String?) {

    }

    override fun onVideoPropertiesChanged(p0: Boolean) {

    }

    override fun getView(): View {
        return View(context)
    }

    override fun onPause() {

    }

    override fun onResume() {

    }
}
