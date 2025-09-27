package com.photi.aos.view.ui.util

import android.app.Activity
import android.util.Log
import com.photi.aos.view.ui.component.dialog.LoadingDialog

object LoadingDialogManager {
    private var dialog: LoadingDialog? = null

    fun show(activity: Activity) {
        if (activity.isFinishing || activity.isDestroyed) return
        Log.i("loading","show LoadingDialog")
        dialog = LoadingDialog(activity)
        dialog?.startAnimation()
    }

    fun hide() {
        if (dialog == null) return
        Log.i("loading","hide LoadingDialog")
        dialog?.stopAnimation()
        dialog = null
    }
}
