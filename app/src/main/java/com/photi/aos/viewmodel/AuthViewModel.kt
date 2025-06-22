package com.photi.aos.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.photi.aos.MyApplication
import com.photi.aos.data.model.ActionApiResponse
import com.photi.aos.data.model.request.EmailCode
import com.photi.aos.data.model.request.NewPwd
import com.photi.aos.data.model.request.UserData
import com.photi.aos.data.model.response.AuthResponse
import com.photi.aos.data.remote.RetrofitClient
import com.photi.aos.data.repository.AuthRepository
import com.photi.aos.data.repository.ErrorHandler
import com.photi.aos.data.repository.MainRepositoryCallback
import com.photi.aos.data.storage.SharedPreferencesManager
import com.photi.aos.view.ui.util.StringUtil

class AuthViewModel : ViewModel() {

    companion object {
        private const val TAG = "AUTH"
    }

    private val apiService = RetrofitClient.apiService
    private val repository = AuthRepository(apiService)

    private val sharedPreferencesManager = SharedPreferencesManager(MyApplication.mySharedPreferences)

    val actionApiResponse = MutableLiveData<ActionApiResponse>()
    var email = ""
    var email_code = ""
    var id = ""
    var password = ""
    var newPassword = ""
    var checkPassword = ""

    fun resetAllValue() {
        actionApiResponse.value = ActionApiResponse()
        email = ""
        email_code = ""
        id = ""
        password = ""
        newPassword = ""
        checkPassword = ""
    }

    fun resetApiResponseValue() {
        actionApiResponse.value = ActionApiResponse()
    }

    fun resetAuthCodeValue() {
        email_code = ""
    }

    fun resetIdValue() {
        id = ""
    }

    fun resetPwValue() {
        password = ""
        newPassword = ""
    }

    fun sendEmailCode() {
        email = StringUtil.removeSpaces(email)
        repository.sendEmailCode(mapOf("email" to email), object :
            MainRepositoryCallback<AuthResponse> {
            override fun onSuccess(data: AuthResponse) {
                val result = data.code
                val mes = data.message
                actionApiResponse.value = ActionApiResponse(result, "sendEmailCode")
                Log.d(TAG, "sendEmailCode: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                handleFailure(error)
            }
        })
    }

    fun verifyEmailCode() {
        email = StringUtil.removeSpaces(email)
        email_code = StringUtil.removeSpaces(email_code)
        repository.verifyEmailCode(EmailCode(email, email_code), object :
            MainRepositoryCallback<AuthResponse> {
            override fun onSuccess(data: AuthResponse) {
                val result = data.code
                val mes = data.message
                actionApiResponse.value = ActionApiResponse(result)
                Log.d(TAG, "verifyEmailCode: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                handleFailure(error)
            }
        })
    }

    fun verifyId() {
        id = StringUtil.removeSpaces(id)
        repository.verifyId(id, object : MainRepositoryCallback<AuthResponse> {
            override fun onSuccess(data: AuthResponse) {
                val result = data.code
                val mes = data.message
                actionApiResponse.value = ActionApiResponse(result)
                Log.d(TAG, "verifyId: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                handleFailure(error)
            }
        })
    }

    fun signUp() {
        email = StringUtil.removeSpaces(email)
        email_code = StringUtil.removeSpaces(email_code)
        id = StringUtil.removeSpaces(id)
        password = StringUtil.removeSpaces(password)
        checkPassword = StringUtil.removeSpaces(checkPassword)
        repository.signUp(UserData(email, email_code, id, password, checkPassword), object :
            MainRepositoryCallback<AuthResponse> {
            override fun onSuccess(data: AuthResponse) {
                val result = data.code
                val mes = data.message

                val username = data.data.username
                sharedPreferencesManager.saveUserName(username)

                actionApiResponse.value = ActionApiResponse(result)
                Log.d(TAG, "signUp: $id $mes $result")
            }

            override fun onFailure(error: Throwable) {
                handleFailure(error)
            }
        })
    }

    fun login() {
        id = StringUtil.removeSpaces(id)
        password = StringUtil.removeSpaces(password)
        val user = UserData(username = id, password = password)
        repository.login(user, object : MainRepositoryCallback<AuthResponse> {
            override fun onSuccess(data: AuthResponse) {
                val result = data.code
                val mes = data.message

                val username = data.data.username
                sharedPreferencesManager.saveUserName(username)

                actionApiResponse.value = ActionApiResponse(result, "login")
                Log.d(TAG, "login: $id $result")
            }

            override fun onFailure(error: Throwable) {
                handleFailure(error)
            }
        })
    }

    fun checkSignedUp() {
        email = StringUtil.removeSpaces(email)
        repository.findId(mapOf("email" to email), object : MainRepositoryCallback<AuthResponse> {
            override fun onSuccess(data: AuthResponse) {
                val result = data.code
                val mes = data.message
                actionApiResponse.value = ActionApiResponse(result)
                Log.d(TAG, "checkSignedUp: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                handleFailure(error)
            }
        })
    }

    fun sendNewPassword() {
        email = StringUtil.removeSpaces(email)
        id = StringUtil.removeSpaces(id)
        repository.sendNewPassword(UserData(email = email, username = id), object :
            MainRepositoryCallback<AuthResponse> {
            override fun onSuccess(data: AuthResponse) {
                val result = data.code
                val mes = data.message
                actionApiResponse.value = ActionApiResponse(result)
                Log.d(TAG, "sendNewPassword: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                handleFailure(error)
            }
        })
    }

    fun modifyPassword() {
        password = StringUtil.removeSpaces(password)
        newPassword = StringUtil.removeSpaces(newPassword)
        if (password == newPassword) {
            actionApiResponse.value = ActionApiResponse(code = "PASSWORD_DUPLICATE_INVALID")
            return
        }
        val newPwd = NewPwd(password, newPassword, newPassword)
        repository.modifyPassword(newPwd, object : MainRepositoryCallback<AuthResponse> {
            override fun onSuccess(data: AuthResponse) {
                val result = data.code
                val mes = data.message
                actionApiResponse.value = ActionApiResponse(result)
                Log.d(TAG, "modifyPassword: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                handleFailure(error)
            }
        })
    }

    fun handleFailure(error: Throwable) {
        val errorCode = ErrorHandler.handle(error)
        actionApiResponse.value = ActionApiResponse(errorCode)
    }
}
