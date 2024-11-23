package com.example.alloon_aos.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alloon_aos.data.model.ApiResponse
import com.example.alloon_aos.data.model.AuthDTO
import com.example.alloon_aos.data.model.EmailCode
import com.example.alloon_aos.data.model.NewPwd
import com.example.alloon_aos.data.model.UserData
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.MyRepository
import com.example.alloon_aos.data.repository.MainRepositoryCallback
import com.example.alloon_aos.view.ui.util.StringUtil
import com.google.android.material.tabs.TabLayout.TabGravity
import okio.IOException
import org.json.JSONObject

class  AuthViewModel : ViewModel() {

    companion object {
        private const val TAG = "AUTH"
    }

    private val apiService = RetrofitClient.apiService
    private val repository = MyRepository(apiService)

    val apiResponse = MutableLiveData<ApiResponse>()
    var email = ""
    var email_code = ""
    var id = ""
    var password = ""
    var newPassword = ""
    var checkPassword = ""

    // init Code
    fun resetAllValue() {
        apiResponse.value = ApiResponse()
        email = ""
        email_code = ""
        id = ""
        password = ""
        newPassword = ""
    }
    fun resetApiResponseValue() {
        apiResponse.value = ApiResponse()
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
/*
*
*
* 회원가입
*
**/
    fun sendEmailCode(){
        email = StringUtil.removeSpaces(email)
        repository.sendEmailCode(mapOf("email" to email),object :
            MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                apiResponse.value = ApiResponse(result, "sendEmailCode")
                Log.d(TAG,"sendEmailCode: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                when(error){
                    is IOException -> {
                        apiResponse.value = ApiResponse("IO_Exception")
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        apiResponse.value = ApiResponse(jObjError.getString("code"))
                        Log.d(TAG, "sendEamilCode error: ${apiResponse.value!!.code}")
                    }
                }
            }
        })
    }

    fun verifyEmailCode() {
        repository.verifyEmailCode(EmailCode(email, email_code), object :
            MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                apiResponse.value = ApiResponse(result) // action은 기본값
                Log.d(TAG, "verifyEmailCode: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                when (error) {
                    is IOException -> {
                        apiResponse.value = ApiResponse("IO_Exception")
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        apiResponse.value = ApiResponse(jObjError.getString("code"))
                        Log.d(TAG, "verifyEmailCode error: ${apiResponse.value!!.code}")
                    }
                }
            }
        })
    }

    fun verifyId() {
        repository.verifyId(id, object : MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                apiResponse.value = ApiResponse(result) // action은 기본값
                Log.d(TAG, "verifyId: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                when (error) {
                    is IOException -> {
                        apiResponse.value = ApiResponse("IO_Exception")
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        apiResponse.value = ApiResponse(jObjError.getString("code"))
                        Log.d(TAG, "verifyId error: ${apiResponse.value!!.code}")
                    }
                }
            }
        })
    }

    fun signUp() {
        repository.signUp(UserData(email, email_code, id, password, checkPassword), object :
            MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                apiResponse.value = ApiResponse(result) // action은 기본값
                Log.d(TAG, "signUp: $id $mes $result")
            }

            override fun onFailure(error: Throwable) {
                when (error) {
                    is IOException -> {
                        apiResponse.value = ApiResponse("IO_Exception")
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        apiResponse.value = ApiResponse(jObjError.getString("code"))
                        Log.d(TAG, "signUp error: ${apiResponse.value!!.code}")
                    }
                }
            }
        })
    }

    fun login() {
        val user = UserData(username = id, password = password)
        repository.login(user, object : MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                apiResponse.value = ApiResponse(result,"login")
                Log.d(TAG, "login: $id $result")
            }

            override fun onFailure(error: Throwable) {
                when (error) {
                    is IOException -> {
                        apiResponse.value = ApiResponse("IO_Exception")
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        apiResponse.value = ApiResponse(jObjError.getString("code"))
                        Log.d(TAG, "login error: ${apiResponse.value!!.code}")
                    }
                }
            }
        })
    }

    fun checkSignedUp() {
        repository.findId(mapOf("email" to email), object : MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                apiResponse.value = ApiResponse(result) // action은 기본값
                Log.d(TAG, "checkSignedUp: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                when (error) {
                    is IOException -> {
                        apiResponse.value = ApiResponse("IO_Exception")
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        apiResponse.value = ApiResponse(jObjError.getString("code"))
                        Log.d(TAG, "checkSignedUp error: ${apiResponse.value!!.code}")
                    }
                }
            }
        })
    }

    fun sendNewPassword() {
        repository.sendNewPassword(UserData(email = email, username = id), object :
            MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                apiResponse.value = ApiResponse(result) // action은 기본값
                Log.d(TAG, "sendNewPassword: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                when (error) {
                    is IOException -> {
                        apiResponse.value = ApiResponse("IO_Exception")
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        apiResponse.value = ApiResponse(jObjError.getString("code"))
                        Log.d(TAG, "sendNewPassword error: ${apiResponse.value!!.code}")
                    }
                }
            }
        })
    }

    fun modifyPassword() {
        val newPwd = NewPwd(password, newPassword, newPassword)
        repository.modifyPassword(newPwd, object : MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                apiResponse.value = ApiResponse(result) // action은 기본값
                Log.d(TAG, "modifyPassword: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                when (error) {
                    is IOException -> {
                        apiResponse.value = ApiResponse("IO_Exception")
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        apiResponse.value = ApiResponse(jObjError.getString("code"))
                        Log.d(TAG, "modifyPassword error: ${apiResponse.value!!.code}")
                    }
                }
            }
        })
    }
}