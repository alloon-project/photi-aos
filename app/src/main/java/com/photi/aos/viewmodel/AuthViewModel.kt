package com.photi.aos.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photi.aos.MyApplication
import com.photi.aos.data.model.ActionApiResponse
import com.photi.aos.data.model.request.AppVersionRequest
import com.photi.aos.data.model.request.Email
import com.photi.aos.data.model.request.EmailCode
import com.photi.aos.data.model.request.NewPwd
import com.photi.aos.data.model.request.UserData
import com.photi.aos.data.model.response.AuthResponse
import com.photi.aos.data.model.response.UpdateOAuthUserNameRequest
import com.photi.aos.data.model.response.UpdateOAuthUserNameResponse
import com.photi.aos.data.remote.RetrofitClient
import com.photi.aos.data.repository.AuthRepository
import com.photi.aos.data.repository.ErrorHandler
import com.photi.aos.data.repository.MainRepositoryCallback
import com.photi.aos.data.repository.handleApiCall
import com.photi.aos.data.storage.SharedPreferencesManager
import com.photi.aos.view.ui.util.StringUtil
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    companion object {
        private const val TAG = "AUTH"
    }

    private val apiService = RetrofitClient.authService
    private val repository = AuthRepository(apiService)

    private val sharedPreferencesManager = SharedPreferencesManager(MyApplication.mySharedPreferences)

    val actionApiResponse = MutableLiveData<ActionApiResponse>()
    val splashResponse = MutableLiveData<ActionApiResponse>()

    var email = ""
    var email_code = ""
    var id = ""
    var password = ""
    var newPassword = ""
    var checkPassword = ""

    private val _date = MutableLiveData<String>("")
    val date: LiveData<String> get() = _date

    private val _needUpdate = MutableLiveData<Boolean>()
    val needUpdate: LiveData<Boolean> get() = _needUpdate

    fun resetAllValue() {
        actionApiResponse.value = ActionApiResponse()
        email = ""
        email_code = ""
        id = ""
        password = ""
        newPassword = ""
        checkPassword = ""
        _date.value = ""
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

    fun resetDateValue() {
        _date.value = ""
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


    fun deletedDate() {
        email = StringUtil.removeSpaces(email)
        viewModelScope.launch {
            handleApiCall(
                call = {
                    repository.deletedDate(
                        Email(email)
                    )
                },
                onSuccess = { data ->
                    _date.value = data!!.deletedDate
                },
                onFailure = { errorCode ->
                    actionApiResponse.value = ActionApiResponse(errorCode)
                }
            )
        }
    }

    fun checkUpdate(version: String) {
        viewModelScope.launch {
            handleApiCall(
                call = {
                    repository.checkUpdate(
                        AppVersionRequest("ANDROID", version)
                    )
                },
                onSuccess = { data ->
                    _needUpdate.value = data!!.forceUpdate
                },
                onFailure = { errorCode ->
                    splashResponse.value = ActionApiResponse(errorCode)
                }
            )
        }
    }

    fun loginOauth(provider: String, idToken : String) {
        viewModelScope.launch {
            handleApiCall(
                call = {
                    repository.loginOauth(provider, idToken)
                },
                onSuccess = { data ->
                    if (data?.username.isNullOrBlank()) {
                        actionApiResponse.value = ActionApiResponse(code = "OAUTH_NEED_NICKNAME")
                    } else {
                        actionApiResponse.value = ActionApiResponse(code = "200 OK")
                    }
                },
                onFailure = { errorCode ->
                    actionApiResponse.value = ActionApiResponse(errorCode)
                }
            )
        }
    }

    fun updateOAuthUserName(username: String) {
        viewModelScope.launch {
            handleApiCall(
                call = {
                    repository.updateOAuthUserName(
                        UpdateOAuthUserNameRequest(username)
                    )
                },
                onSuccess = {
                    actionApiResponse.value = ActionApiResponse(code = "OAUTH_LOGIN_SUCCESS")
                },
                onFailure = { errorCode ->
                    actionApiResponse.value = ActionApiResponse(errorCode)
                }
            )
        }
    }

}
