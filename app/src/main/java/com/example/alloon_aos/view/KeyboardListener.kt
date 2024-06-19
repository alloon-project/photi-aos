package com.example.alloon_aos.view

import android.graphics.Rect
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import com.example.alloon_aos.view.fragment.setting.InquireFragment

interface OnKeyboardVisibilityListener {
    fun onVisibilityChanged(visible : Boolean)
}

object KeyboardListener {
     fun setKeyboardVisibilityListener(fragment: Fragment, onKeyboardVisibilityListener: OnKeyboardVisibilityListener) {
         if(fragment.isAdded){
             val parentView = (fragment.view as ViewGroup?)?.getChildAt(0)
             parentView?.viewTreeObserver?.addOnGlobalLayoutListener(object :
                 ViewTreeObserver.OnGlobalLayoutListener {
                 private var alreadyOpen = false
                 private val defaultKeyboardHeightDP = 100
                 private val EstimatedKeyboardDP =
                     defaultKeyboardHeightDP + 48
                 private val rect = Rect()
                 override fun onGlobalLayout() {
                     val estimatedKeyboardHeight = TypedValue.applyDimension(
                         TypedValue.COMPLEX_UNIT_DIP,
                         EstimatedKeyboardDP.toFloat(),
                         parentView.resources.displayMetrics
                     ).toInt()
                     parentView.getWindowVisibleDisplayFrame(rect)
                     val heightDiff = parentView.rootView.height - (rect.bottom - rect.top)
                     val isShown = heightDiff >= estimatedKeyboardHeight
                     if (isShown == alreadyOpen) {
                         Log.i("Keyboard state", "Ignoring global layout change...")
                         return
                     }
                     alreadyOpen = isShown
                     onKeyboardVisibilityListener.onVisibilityChanged(isShown)
                 }
             })
         }
    }
}