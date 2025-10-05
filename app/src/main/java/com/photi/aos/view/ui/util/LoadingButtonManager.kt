package com.photi.aos.view.ui.util

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.photi.aos.R

object LoadingButtonManager {
    private val LOADING_TAG = R.id.lottie_loading_view_tag
    private val ORIGINAL_TEXT_TAG = R.id.lottie_original_text_tag

    fun Button.showLoading() {
        val parentLayout = this.parent as? ConstraintLayout ?: return
        if (getTag(LOADING_TAG) != null) return

        val lottie = LottieAnimationView(context).apply {
            id = View.generateViewId()
            setAnimation(R.raw.loading_btn)
            repeatCount = LottieDrawable.INFINITE
            layoutParams = ConstraintLayout.LayoutParams(32.dp(), 32.dp())
            visibility = View.VISIBLE
            playAnimation()
        }

        if (lottie.parent == null) {
            parentLayout.addView(lottie)
        }

        setTag(ORIGINAL_TEXT_TAG, this.text)
        this.text = ""
        this.isEnabled = false

        this.post {
            ConstraintSet().apply {
                clone(parentLayout)
                connect(lottie.id, ConstraintSet.TOP, this@showLoading.id, ConstraintSet.TOP)
                connect(lottie.id, ConstraintSet.BOTTOM, this@showLoading.id, ConstraintSet.BOTTOM)
                connect(lottie.id, ConstraintSet.START, this@showLoading.id, ConstraintSet.START)
                connect(lottie.id, ConstraintSet.END, this@showLoading.id, ConstraintSet.END)
                applyTo(parentLayout)
            }
        }

        setTag(LOADING_TAG, lottie)
    }

    fun Button.hideLoading() {
        val parent = this.parent as? ViewGroup ?: return
        val lottie = getTag(LOADING_TAG) as? View ?: return

        parent.removeView(lottie)
        text = getTag(ORIGINAL_TEXT_TAG) as? CharSequence ?: ""
        isEnabled = true

        setTag(LOADING_TAG, null)
        setTag(ORIGINAL_TEXT_TAG, null)
    }

    private fun Int.dp(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()
}

