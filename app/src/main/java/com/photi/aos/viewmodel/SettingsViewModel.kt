package com.photi.aos.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photi.aos.data.enum.InquiryType
import com.photi.aos.data.model.ActionApiResponse
import com.photi.aos.data.model.request.InquiryRequest
import com.photi.aos.data.model.response.AuthResponse
import com.photi.aos.data.remote.RetrofitClient
import com.photi.aos.data.repository.ErrorHandler
import com.photi.aos.data.repository.MainRepositoryCallback
import com.photi.aos.data.repository.SettingsRepository
import com.photi.aos.data.repository.handleApiCall
import com.photi.aos.view.ui.util.StringUtil
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class SettingsViewModel : ViewModel() {
    companion object {
        private const val TAG = "Settings"
    }

    private val apiService = RetrofitClient.apiService

    private val settingsRepository = SettingsRepository(apiService)

    val actionApiResponse = MutableLiveData<ActionApiResponse>()

    var password = ""

    private val _code = MutableLiveData<String>("")
    val code: LiveData<String> get() = _code

    private val _email = MutableLiveData<String>("")
    val email: LiveData<String> get() = _email

    private val _id = MutableLiveData<String>("")
    val id: LiveData<String> get() = _id

    val inquiryMessage = MutableLiveData<String>("")

    private val _profileImage = MutableLiveData<String?>(null)
    val profileImage: LiveData<String?> = _profileImage

    fun resetCode() {
        _code.value = ""
    }

    fun deleteUser() {
        password = StringUtil.removeSpaces(password)
        settingsRepository.deleteUser(
            mapOf("password" to password),
            object : MainRepositoryCallback<AuthResponse> {
                override fun onSuccess(data: AuthResponse) {
                    val result = data.code
                    val mes = data.message
                    actionApiResponse.value = ActionApiResponse(result)
                    Log.d(TAG, "deleteUser: $mes $result")
                }

                override fun onFailure(error: Throwable) {
                    handleFailure(error)
                }
            })
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            handleApiCall(
                call = { settingsRepository.getUsers() },
                onSuccess = { data ->
                    _id.value = data!!.username
                    _email.value = data.email
                    _profileImage.value = data.imageUrl
                    _code.value = "200 OK"
                },
                onFailure = { errorCode ->
                    _code.value = errorCode
                }
            )
        }
    }

    fun sendInquiries(type: InquiryType) {
        viewModelScope.launch {
            handleApiCall(
                call = {
                    settingsRepository.postInquiries(
                        InquiryRequest(
                            type = type,
                            content = inquiryMessage.value!!
                        )
                    )
                },
                onSuccess = { data ->
                    _code.value = "201 CREATED"
                },
                onFailure = { errorCode ->
                    _code.value = errorCode
                }
            )
        }
    }

    fun sendProfileImage(imageFile : MultipartBody.Part) {
        viewModelScope.launch {
            handleApiCall(
                call = {
                    settingsRepository.postImage(imageFile)
                },
                onSuccess = { data ->
                    _code.value = "200 OK"
                },
                onFailure = { errorCode ->
                    _code.value = errorCode
                }
            )
        }
    }



    fun handleFailure(error: Throwable) {
        val errorCode = ErrorHandler.handle(error)
        actionApiResponse.value = ActionApiResponse(errorCode)
    }
}
