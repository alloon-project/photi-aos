package com.example.alloon_aos.view.ui.util

import android.content.Context
import android.util.TypedValue

// 확장 함수로 dp -> px 변환
fun Context.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics
    ).toInt()
}