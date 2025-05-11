package com.example.alloon_aos.data.repository

import android.util.Log
import com.example.alloon_aos.data.model.response.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
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

suspend fun <T> handleApiCall(
    call: suspend () -> Response<ApiResponse<T>>,
    onSuccess: (T?) -> Unit,
    onFailure: (String) -> Unit
) {
    try {
        val response = withContext(Dispatchers.IO) { call() }
        if (response.isSuccessful) {
            val body = response.body()
            Log.d("handleApiCall", "Response body: $body")

            if (body != null) {
                if (body.code.trim().startsWith("200", ignoreCase = true) ||
                    body.code.trim().startsWith("201", ignoreCase = true)) {
                    Log.d("handleApiCall", "Success: ${body.data}")
                    onSuccess(body.data)
                } else {
                    Log.e("handleApiCall", "Unexpected code: ${body.code}")
                    onFailure(body.code)
                }

            } else {
                Log.e("handleApiCall", "Response body is null!")
                onFailure("UNKNOWN_ERROR")
            }
        } else {
            Log.e("handleApiCall", "HTTP Error: ${response.code()}")
            onFailure("HTTP_${response.code()}")
        }

    } catch (e: IOException) {
        Log.e("handleApiCall", "IO Exception: ${e.message}")
        onFailure("IO_Exception")
    } catch (e: Exception) {
        Log.e("handleApiCall", "Exception: ${e.message}")
        onFailure("UNKNOWN_ERROR")
    }
}



