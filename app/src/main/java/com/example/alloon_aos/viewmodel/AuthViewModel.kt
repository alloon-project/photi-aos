package com.example.alloon_aos.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.alloon_aos.data.model.AuthDTO
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.MainRepositoryCallback
import com.example.alloon_aos.data.repository.MyRepository

class AuthViewModel:ViewModel() {
    private val apiService = RetrofitClient.apiService
    private val repository = MyRepository(apiService)

    fun getData() { //모든 api 실행
//        repository.sendEmailCode(object :
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

//        repository.verifyEmailCode(object :
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
//        repository.verifyId(object :
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

//        repository.signUp(object :
//            MainRepositoryCallback<AuthDTO> {
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

//        repository.findId(object :
//            MainRepositoryCallback<AuthDTO> {
//            override fun onSuccess(data: AuthDTO) {
//                val result = data.code
//                val mes = data.message
//                Log.d("TAG","findId: $mes $result")
//            }
//
//            override fun onFailure(error: Throwable) {
//                Log.d("TAG","findId" + error.message.toString())
//            }
//        })
//
//        repository.findPwd(object :
//            MainRepositoryCallback<AuthDTO> {
//            override fun onSuccess(data: AuthDTO) {
//                val result = data.code
//                val mes = data.message
//                Log.d("TAG","findPwd: $mes $result")
//            }
//
//            override fun onFailure(error: Throwable) {
//                Log.d("TAG","findPwd" + error.message.toString())
//            }
//        })
//
//        repository.login(object :
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
//
        repository.modifyPwd(object :
            MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                Log.d("TAG","modifyPwd: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                Log.d("TAG","modifyPwd"+ error.message.toString())
            }
        })
    }
}