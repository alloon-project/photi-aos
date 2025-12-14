package com.photi.aos.viewmodel

import android.net.Uri
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class SettingsViewModel : ViewModel() {
    companion object {
        private const val TAG = "Settings"
    }

    private val apiService = RetrofitClient.authService

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

     fun sendProfileImage(file: File, contentType: String) {
         viewModelScope.launch {
             try{
                 val presignedUrl  =  getPresignedUrl(file.name)


                 withContext(Dispatchers.IO) {
                     uploadPut(presignedUrl, file, contentType)
                 }

                 updateUserProfileImage(presignedUrl)

                 _code.value = "200 OK"
             }catch (e : Exception){
                 _code.value = e.message ?: "UNKNOWN_ERROR"
             }finally {

             }

         }

    }

    private suspend fun getPresignedUrl(imageName: String) : String{
        val response = settingsRepository.postUserImagePresignedUrl(imageName) // Response<ApiResponse<...>>
        if (!response.isSuccessful) throw IOException("Presigned API failed: ${response.code()}")
        val body = response.body() ?: throw IOException("Empty body")
        return body.data.preSignedUrl
    }

    private fun uploadPut(url: String, file: File, contentType: String) {
        val client = OkHttpClient()
        val req = Request.Builder()
            .url(url)
            .put(file.asRequestBody(contentType.toMediaType()))
            .header("Content-Type", contentType)
            .build()

        client.newCall(req).execute().use { res ->
            if (!res.isSuccessful) throw IOException("Upload failed: ${res.code}")
        }
    }

    private suspend fun updateUserProfileImage(presignedUrl : String){
        val response = settingsRepository.patchUserProfileImage(presignedUrl)

        if (!response.isSuccessful) throw IOException("Profile PATCH failed: ${response.code()}")
        val body = response.body() ?: throw IOException("Empty body")
    }



    fun handleFailure(error: Throwable) {
        val errorCode = ErrorHandler.handle(error)
        actionApiResponse.value = ActionApiResponse(errorCode)
    }
}
