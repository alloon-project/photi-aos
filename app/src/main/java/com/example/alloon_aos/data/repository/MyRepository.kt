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
    //private val email : Map<String, String> = mapOf("email" to "ejsong428@gmail.com")

    fun sendEmailCode(email: Map<String, String>,callback: MainRepositoryCallback<AuthDTO>) {
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

    fun verifyEmailCode(emailCode: EmailCode,callback: MainRepositoryCallback<AuthDTO>) {
        apiService.patch_verifyEmailCode(emailCode).enqueue(object : Callback<AuthDTO> {
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

    fun verifyId(name:String, callback: MainRepositoryCallback<AuthDTO>) {
        apiService.get_verifyId(name).enqueue(object : Callback<AuthDTO> {
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

    fun signUp(userData: UserData,callback: MainRepositoryCallback<AuthDTO>) {
        //val data = UserData("byeolstar12@naver.com","NwEkGX","hb_hb_hb","password1!","password1!")
        apiService.post_signUp(userData).enqueue(object : Callback<AuthDTO> {
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

    fun findId(email: Map<String, String>,callback: MainRepositoryCallback<AuthDTO>) {
        apiService.post_findId(email).enqueue(object : Callback<AuthDTO> {
            override fun onResponse(call: Call<AuthDTO>, response: Response<AuthDTO>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                }
                else {
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

    fun login(user: UserData,callback: MainRepositoryCallback<AuthDTO>) {
        apiService.post_login(user).enqueue(object : Callback<AuthDTO> {
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