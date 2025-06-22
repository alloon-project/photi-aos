package com.photi.aos.data.repository

import com.photi.aos.MyApplication
import com.photi.aos.data.model.request.*
import com.photi.aos.data.model.response.AuthResponse
import com.photi.aos.data.remote.ApiService
import com.photi.aos.data.storage.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)

    //GET
    fun verifyId(name: String, callback: MainRepositoryCallback<AuthResponse>) {
        apiService.get_verifyId(name).enqueue(object : Callback<AuthResponse> {
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


    //PATCH
    fun modifyPassword(newPwd: NewPwd, callback: MainRepositoryCallback<AuthResponse>) {
        apiService.patch_modifyPwd(newPwd).enqueue(object : Callback<AuthResponse> {
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

    fun verifyEmailCode(emailCode: EmailCode, callback: MainRepositoryCallback<AuthResponse>) {
        apiService.patch_verifyEmailCode(emailCode).enqueue(object : Callback<AuthResponse> {
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


    //POST
    fun signUp(userData: UserData, callback: MainRepositoryCallback<AuthResponse>) {
        apiService.post_signUp(userData).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val accessToken = response.headers()["authorization"]?.replace("Bearer ", "")
                    val refreshToken = response.headers()["refresh-Token"]
                    if (accessToken != null) {
                        tokenManager.saveAccessToken(accessToken)
                    }
                    if (refreshToken != null) {
                        tokenManager.saveRefreshToken(refreshToken)
                    }
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

    fun login(user: UserData, callback: MainRepositoryCallback<AuthResponse>) {
        apiService.post_login(user).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val accessToken = response.headers()["authorization"]?.replace("Bearer ", "")
                    val refreshToken = response.headers()["refresh-Token"]
                    if (accessToken != null) {
                        tokenManager.saveAccessToken(accessToken)
                    }
                    if (refreshToken != null) {
                        tokenManager.saveRefreshToken(refreshToken)
                    }
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

    fun findId(email: Map<String, String>, callback: MainRepositoryCallback<AuthResponse>) {
        apiService.post_findId(email).enqueue(object : Callback<AuthResponse> {
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

    fun sendNewPassword(user: UserData, callback: MainRepositoryCallback<AuthResponse>) {
        apiService.post_findPwd(user).enqueue(object : Callback<AuthResponse> {
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

    fun sendEmailCode(email: Map<String, String>, callback: MainRepositoryCallback<AuthResponse>) {
        apiService.post_sendEmailCode(email).enqueue(object : Callback<AuthResponse> {
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
