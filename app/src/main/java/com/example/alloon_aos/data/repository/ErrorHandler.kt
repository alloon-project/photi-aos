package com.example.alloon_aos.data.repository

import android.util.Log
import org.json.JSONObject
import java.io.IOException

class ErrorHandler {
    companion object {
        private const val TAG = "ErrorHandler"

        fun handle(error: Throwable): String {
            return when (error) {
                is IOException -> {
                    Log.e(TAG, "IO Exception occurred: ${error.message}")
                    "IO_Exception"
                }
                else -> {
                    try {
                        val jObjError = JSONObject(error.message.toString())
                        val errorCode = jObjError.getString("code")
                        Log.e(TAG, "API Error: $errorCode")
                        errorCode
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing error response: ${e.message}")
                        "UNKNOWN_ERROR"
                    }
                }
            }
        }
    }
}
