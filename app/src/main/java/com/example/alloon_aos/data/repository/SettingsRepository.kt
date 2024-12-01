package com.example.alloon_aos.data.repository

import com.example.alloon_aos.data.model.request.NewPwd
import com.example.alloon_aos.data.model.response.AuthResponse
import com.example.alloon_aos.data.remote.ApiService
import com.example.alloon_aos.data.remote.RetrofitClient.apiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsRepository(private val apiService: ApiService)  {
    fun deleteUser(pwd: Map<String, String>, callback: MainRepositoryCallback<AuthResponse>) {
        apiService.patch_deleteUser(pwd).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }
}