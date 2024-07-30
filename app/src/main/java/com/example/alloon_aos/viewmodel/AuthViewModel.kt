package com.example.alloon_aos.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alloon_aos.data.model.AuthDTO
import com.example.alloon_aos.data.model.EmailCode
import com.example.alloon_aos.data.model.NewPwd
import com.example.alloon_aos.data.model.UserData
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.MyRepository
import com.example.alloon_aos.data.repository.MainRepositoryCallback
import com.example.alloon_aos.MyApplication.Companion.mySharedPreferences
import okio.IOException
import org.json.JSONObject

class AuthViewModel : ViewModel() {
    private val apiService = RetrofitClient.apiService
    private val repository = MyRepository(apiService)

    var code =  MutableLiveData("") //observer가 필요한 경우만 Mutable
    var email = ""
    var email_code = ""
    var id = ""
    var password = ""
    var newPassword = ""
    var checkPassword = ""

    // init Code
    fun resetAllValue() {
        code.value = ""
        email = ""
        email_code = ""
        id = ""
        password = ""
        newPassword = ""
    }
    fun resetCodeValue() {
        code.value = ""
    }
/*
*
*
* 회원가입
*
**/
    fun sendEmailCode(){
        repository.sendEmailCode(mapOf("email" to email),object :
            MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                code.value = result
                Log.d("TAG","sendEmailCode: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                when(error){
                    is IOException -> {
                        code.value = "IO_Exception"
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        code.value = jObjError.getString("code") //ex."USER_NOT_FOUND"
                        Log.d("TAG","login id : $id pwd: $password response: " +code.value )
                    }
                }
            }
        })
    }

    fun verifyEmailCode(){
        repository.verifyEmailCode(EmailCode(email,email_code),object :
            MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                code.value = result
                Log.d("TAG","verifyEmailCode: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                when(error){
                    is IOException -> {
                        code.value = "IO_Exception"
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        code.value = jObjError.getString("code") //ex."USER_NOT_FOUND"
                        Log.d("TAG","login id : $id pwd: $password response: " +code.value )
                    }
                }
            }
        })
    }

    fun verifyId(){
        repository.verifyId(id,object :
            MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                code.value = result
                Log.d("TAG","verifyId: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                when(error){
                    is IOException -> {
                        code.value = "IO_Exception"
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        code.value = jObjError.getString("code") //ex."USER_NOT_FOUND"
                        Log.d("TAG","login id : $id pwd: $password response: " +code.value )
                    }
                }
            }
        })
    }

    fun signUp(){
        repository.signUp(UserData(email,email_code, id,password,checkPassword)
            ,object : MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code //ex."USERNAME_SENT"
                val mes = data.message
                code.value = result
                Log.d("TAG","signUp: $id $mes $result")
            }

            override fun onFailure(error: Throwable) {
                when(error){
                    is IOException -> {
                        code.value = "IO_Exception"
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        code.value = jObjError.getString("code") //ex."USER_NOT_FOUND"
                        Log.d("TAG","login id : $id pwd: $password response: " +code.value )
                    }
                }
            }
        })
    }

//로그인
    fun login() {
        val user  = UserData(username = id, password = password)
        repository.login(user,object :
            MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code //ex."USERNAME_SENT"
                val mes = data.message
                code.value = result
                //val imgUrl = _data.data.imageUrl
                Log.d("TAG","login: $id $result")
            }

            override fun onFailure(error: Throwable) {
                Log.d("TAG","onFailure : ${error.toString()} " )
                when(error){
                    is IOException -> {
                        code.value = "IO_Exception"
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        val errorCode = jObjError.getString("code")
                        code.value = jObjError.getString("code") //ex."USER_NOT_FOUND"
                        Log.d("TAG","login id : $id pwd: $password response: " +code.value )
                    }
                }
            }
        })
    }

//아이디 찾기
    fun checkSignedUp(){
        repository.findId(mapOf("email" to email),object : MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                code.value = result

                Log.d("TAG1","findId: ${mes} $result")
            }

            override fun onFailure(error: Throwable) {
                when(error){
                    is IOException -> {
                        code.value = "IO_Exception"
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        code.value = jObjError.getString("code")
                        Log.d("TAG","error: " + code.value)
                    }
                }
            }
        })
    }

    //임시 비밀번호 전송
    fun sendNewPassword(){
        repository.sendNewPassword(UserData(email = email,username = id),object : MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                code.value = result
                Log.d("TAG1","findId: ${mes} $result")
            }

            override fun onFailure(error: Throwable) {
                when(error){
                    is IOException -> {
                        code.value = "IO_Exception"
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        code.value = jObjError.getString("code")
                        Log.d("TAG","error: " + code.value)
                    }
                }
            }
        })
    }

    fun modifyPassword(){
        val newPwd = NewPwd(password,newPassword,newPassword)
        val token =  mySharedPreferences.getString("access_token", "토큰이 없습니다.").toString()
        repository.modifyPassword(token,newPwd,object :
            MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                code.value = result
                Log.d("TAG","modifyPwd: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                when(error){
                    is IOException -> {
                        code.value = "IO_Exception"
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        code.value = jObjError.getString("code")
                        Log.d("TAG","error: " + code.value)
                    }
                }
            }
        })
    }


}