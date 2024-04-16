package com.example.alloon_aos.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.alloon_aos.data.model.EmailCodeDTO
import com.example.alloon_aos.data.model.IdCodeDTO
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.MainRepositoryCallback
import com.example.alloon_aos.data.repository.MyRepository

class AuthViewModel:ViewModel() {
    private val apiService = RetrofitClient.apiService
    private val repository = MyRepository(apiService)

    fun getData() {
        repository.sendEmailCode(object :
            MainRepositoryCallback<EmailCodeDTO> {
            override fun onSuccess(data: EmailCodeDTO) {
                val result = data.code
                val mes = data.message
                Log.d("TAG","$result 는 $mes")
            }

            override fun onFailure(error: Throwable) {
                Log.d("TAG",error.message.toString())
            }
        })
        repository.verifyId(object :
            MainRepositoryCallback<IdCodeDTO> {
            override fun onSuccess(data: IdCodeDTO) {
                val result = data.code
                val mes = data.message
                Log.d("TAG","$result 는 $mes")
            }

            override fun onFailure(error: Throwable) {
                Log.d("TAG",error.message.toString())
            }
        })

    }
}