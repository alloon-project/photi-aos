package com.example.alloon_aos.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alloon_aos.data.model.ApiResponse
import com.example.alloon_aos.data.model.request.NewPwd
import com.example.alloon_aos.data.model.response.AuthResponse
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.ErrorHandler
import com.example.alloon_aos.data.repository.MainRepositoryCallback
import com.example.alloon_aos.data.repository.SettingsRepository
import com.example.alloon_aos.view.ui.util.StringUtil

class SettingsViewModel: ViewModel() {
    companion object {
        private const val TAG = "Settings"
    }

    private val apiService = RetrofitClient.apiService
    private val repository = SettingsRepository(apiService)
    val apiResponse = MutableLiveData<ApiResponse>()

    var password = ""

    fun deleteUser() {
        password = StringUtil.removeSpaces(password)
        repository.deleteUser(mapOf("password" to password), object : MainRepositoryCallback<AuthResponse> {
            override fun onSuccess(data: AuthResponse) {
                val result = data.code
                val mes = data.message
                apiResponse.value = ApiResponse(result)
                Log.d(TAG, "deleteUser: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                handleFailure(error)
            }
        })
    }

    fun handleFailure(error: Throwable) {
        val errorCode = ErrorHandler.handle(error)
        apiResponse.value = ApiResponse(errorCode)
    }
}