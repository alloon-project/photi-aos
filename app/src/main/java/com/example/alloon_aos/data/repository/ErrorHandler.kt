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
            onSuccess(response.body()?.data)
        } else {
            val errorCode = response.errorBody()?.let { errorBody ->
                try {
                    val errorJson = JSONObject(errorBody.string())
                    errorJson.getString("code")
                } catch (e: Exception) {
                    "UNKNOWN_ERROR"
                }
            } ?: "UNKNOWN_ERROR"
            onFailure(errorCode)
        }
    } catch (e: IOException) {
        onFailure("IO_Exception")
    } catch (e: Exception) {
        onFailure("UNKNOWN_ERROR")
    }
}

