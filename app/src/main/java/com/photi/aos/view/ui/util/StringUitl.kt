package com.photi.aos.view.ui.util
object StringUtil {
    // 모든 공백 제거
    fun removeSpaces(input: String): String {
        return input.replace("\\s".toRegex(), "")
    }
}
