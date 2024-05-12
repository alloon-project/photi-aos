package com.example.alloon_aos.data.repository

import com.example.alloon_aos.data.model.EmailCode
import com.example.alloon_aos.data.model.AuthDTO
import com.example.alloon_aos.data.model.UserData
import com.example.alloon_aos.data.model.NewPwd
import com.example.alloon_aos.data.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface MainRepositoryCallback<T> {
    fun onSuccess(data: T)
    fun onFailure(error: Throwable)
}

class MyRepository(private val apiService: ApiService) {
    private val email : Map<String, String> = mapOf("email" to "byeolstar12@naver.com")


    fun verifyEmailCode(callback: MainRepositoryCallback<AuthDTO>) {
        apiService.patch_verifyEmailCode(EmailCode("byeolstar12@naver.com","NwEkGX")).enqueue(object : Callback<AuthDTO> {
            override fun onResponse(call: Call<AuthDTO>, response: Response<AuthDTO>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    var stringToJson = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(stringToJson))
                }
            }

            override fun onFailure(call: Call<AuthDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }

    fun verifyId(callback: MainRepositoryCallback<AuthDTO>) {
        apiService.get_verifyId("byeolstar12").enqueue(object : Callback<AuthDTO> {
            override fun onResponse(call: Call<AuthDTO>, response: Response<AuthDTO>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    var stringToJson = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(stringToJson))
                }
            }

            override fun onFailure(call: Call<AuthDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }

    fun signUp(callback: MainRepositoryCallback<AuthDTO>) {
        val data = UserData("byeolstar12@naver.com","NwEkGX","hb_hb_hb","password1!","password1!")
        apiService.post_signUp(data).enqueue(object : Callback<AuthDTO> {
            override fun onResponse(call: Call<AuthDTO>, response: Response<AuthDTO>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    var stringToJson = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(stringToJson))
                }

            }

            override fun onFailure(call: Call<AuthDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }

    fun findId(callback: MainRepositoryCallback<AuthDTO>) {
        apiService.post_findId(email).enqueue(object : Callback<AuthDTO> {
            override fun onResponse(call: Call<AuthDTO>, response: Response<AuthDTO>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    var stringToJson = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(stringToJson))
                }
            }

            override fun onFailure(call: Call<AuthDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }

    fun findPwd(callback: MainRepositoryCallback<AuthDTO>) {
        apiService.post_findPwd(UserData(email = "byeolstar12@naver.com", username = "hbhbhb")).enqueue(object : Callback<AuthDTO> {
            override fun onResponse(call: Call<AuthDTO>, response: Response<AuthDTO>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    var stringToJson = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(stringToJson))
                }
            }

            override fun onFailure(call: Call<AuthDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }

    fun login(callback: MainRepositoryCallback<AuthDTO>) {
        apiService.post_login(UserData(username = "hbhbhb", password = "Q9y20Y3q")).enqueue(object : Callback<AuthDTO> {
            override fun onResponse(call: Call<AuthDTO>, response: Response<AuthDTO>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    var stringToJson = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(stringToJson))
                }
            }

            override fun onFailure(call: Call<AuthDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }

    fun modifyPwd(callback: MainRepositoryCallback<AuthDTO>) {
        apiService.patch_modifyPwd(NewPwd("Q9y20Y3q!","password2!","password2!")).enqueue(object : Callback<AuthDTO> {
            override fun onResponse(call: Call<AuthDTO>, response: Response<AuthDTO>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    var stringToJson = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(stringToJson))
                }
            }

            override fun onFailure(call: Call<AuthDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }
}