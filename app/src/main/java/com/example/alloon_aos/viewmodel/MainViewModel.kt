package com.example.alloon_aos.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alloon_aos.data.model.AuthDTO
import com.example.alloon_aos.data.model.EmailCode
import com.example.alloon_aos.data.model.UserData
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.MyRepository
import com.example.alloon_aos.data.repository.MainRepositoryCallback
import org.json.JSONObject

class MainViewModel : ViewModel() {
    private val apiService = RetrofitClient.apiService
    private val repository = MyRepository(apiService)

    var code =  MutableLiveData("") //observer가 필요한 경우만 Mutable
    var email = ""
    var id = ""
    var password = ""
    var hello = ""

        fun login() {
        //val user  = UserData(username = "seulseul", password = "password1!")
        val user  = UserData(username = id, password = password)
        repository.login(user,object :
            MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                //200
                code.value = result
                Log.d("TAG","login: $id $mes $result")
            }

            override fun onFailure(error: Throwable) {
                //4xx
                val jObjError = JSONObject(error.message.toString())
                code.value = jObjError.getString("code")
                Log.d("TAG","login id : $id pwd: $password response: " +code.value )
            }
        })
    }
    fun doIt(){
        val email : Map<String, String> = mapOf("email" to "ejsong428@gmail.com")
//                repository.sendEmailCode(email,object :
//            MainRepositoryCallback<AuthDTO> {
//            override fun onSuccess(data: AuthDTO) {
//                val result = data.code
//                val mes = data.message
//                Log.d("TAG","sendEmailCode: $mes $result")
//            }
//
//            override fun onFailure(error: Throwable) {
//                Log.d("TAG","sendEmailCode"+ error.message.toString())
//            }
//        })

//        repository.verifyEmailCode(EmailCode("ejsong428@gmail.com","l29jl7"),object :
//            MainRepositoryCallback<AuthDTO> {
//            override fun onSuccess(data: AuthDTO) {
//                val result = data.code
//                val mes = data.message
//                Log.d("TAG","verifyEmailCode: $mes $result")
//            }
//
//            override fun onFailure(error: Throwable) {
//                Log.d("TAG","verfiyEmailCode"+ error.message.toString())
//            }
//        })
//
//        repository.verifyId("seulseul",object :
//            MainRepositoryCallback<AuthDTO> {
//            override fun onSuccess(data: AuthDTO) {
//                val result = data.code
//                val mes = data.message
//                Log.d("TAG","verifyId : $mes $result")
//            }
//
//            override fun onFailure(error: Throwable) {
//                Log.d("TAG","verifyId"+error.message.toString())
//            }
//        })

//        repository.signUp(UserData("ejsong428@gmail.com","l29jl7","seulseul","password1!","password1!")
//            ,object : MainRepositoryCallback<AuthDTO> {
//            override fun onSuccess(data: AuthDTO) {
//                val result = data.code
//                val mes = data.message
//                Log.d("TAG","signUp: $mes $result")
//            }
//
//            override fun onFailure(error: Throwable) {
//                Log.d("TAG","signUp" + error.message.toString())
//            }
//        })

//        val user  = UserData(username = "seulseul", password = "password1!")
//        repository.login(user,object :
//            MainRepositoryCallback<AuthDTO> {
//            override fun onSuccess(data: AuthDTO) {
//                val result = data.code
//                val mes = data.message
//                Log.d("TAG","login: $mes $result")
//            }
//
//            override fun onFailure(error: Throwable) {
//                Log.d("TAG","login" + error.message.toString())
//            }
//        })
    }

    fun checkSignedUp(){
        repository.findId(mapOf("email" to email),object : MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                code.value = result

                Log.d("TAG1","findId: ${mes} $result")
            }

            override fun onFailure(error: Throwable) {
                val jObjError = JSONObject(error.message.toString())
                code.value = jObjError.getString("code")
                Log.d("TAG","error: " + code.value)
            }
        })
    }


}