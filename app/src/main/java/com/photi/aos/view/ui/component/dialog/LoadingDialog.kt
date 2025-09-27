package com.photi.aos.view.ui.component.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.photi.aos.R

class LoadingDialog(context: Context) : Dialog(context){
    private val animationView: LottieAnimationView

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_loading)

        animationView = findViewById(R.id.lotti)
        animationView.repeatCount = LottieDrawable.INFINITE
    }

    fun startAnimation() {
        if (!isShowing) show()
        animationView.playAnimation()
    }

    fun stopAnimation() {
        if (isShowing) {
            animationView.cancelAnimation()
            dismiss()
        }
    }
}