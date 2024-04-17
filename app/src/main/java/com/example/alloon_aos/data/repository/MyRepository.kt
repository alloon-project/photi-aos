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
    private val email : Map<String, String> = mapOf("email" to "tester@alloon.com")

    fun sendEmailCode(callback: MainRepositoryCallback<AuthDTO>) {
        apiService.post_sendEmailCode(email).enqueue(object : Callback<AuthDTO> {
            override fun onResponse(call: Call<AuthDTO>, response: Response<AuthDTO>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    //Exception 400 : ex.이메일 인증을 먼저 해주세요.
                    var stringToJson = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(stringToJson))
                }
            }

            override fun onFailure(call: Call<AuthDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }

    fun verifyEmailCode(callback: MainRepositoryCallback<AuthDTO>) {
        apiService.patch_verifyEmailCode(EmailCode("tester@alloon.com","000000")).enqueue(object : Callback<AuthDTO> {
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
        apiService.get_verifyId("tester").enqueue(object : Callback<AuthDTO> {
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
        val data = UserData("tester@alloon.com","000000","tester","password1!","password1!")
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
        apiService.post_findPwd(UserData(email = "tester@alloon.com", username = "tester")).enqueue(object : Callback<AuthDTO> {
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
        apiService.post_login(UserData(username = "tester", password = "password1!")).enqueue(object : Callback<AuthDTO> {
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
        apiService.patch_modifyPwd(NewPwd("password1!","password2!","password2!")).enqueue(object : Callback<AuthDTO> {
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