package com.example.alloon_aos.data.repository

import android.util.Log
import com.example.alloon_aos.data.model.EmailCodeDTO
import com.example.alloon_aos.data.model.IdCodeDTO
import com.example.alloon_aos.data.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface MainRepositoryCallback<T> {
    fun onSuccess(data: T)
    fun onFailure(error: Throwable)
}

class MyRepository(private val apiService: ApiService) {
    private val body : Map<String, String> = mapOf("email" to "tester@alloon.com")

    //인증코드
    fun sendEmailCode(callback: MainRepositoryCallback<EmailCodeDTO>) {
        apiService.sendEmailCode(body).enqueue(object : Callback<EmailCodeDTO> {
            override fun onResponse(call: Call<EmailCodeDTO>, response: Response<EmailCodeDTO>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onFailure(Throwable("Network request failed"))
                    Log.d("TAG", response.code().toString())
                }
            }

            override fun onFailure(call: Call<EmailCodeDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }

    fun verifyId(callback: MainRepositoryCallback<IdCodeDTO>) {
        apiService.verifyId("tester").enqueue(object : Callback<IdCodeDTO> {
            override fun onResponse(call: Call<IdCodeDTO>, response: Response<IdCodeDTO>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onFailure(Throwable("Network request failed"))
                    Log.d("TAG", response.code().toString())
                }
            }

            override fun onFailure(call: Call<IdCodeDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }
}