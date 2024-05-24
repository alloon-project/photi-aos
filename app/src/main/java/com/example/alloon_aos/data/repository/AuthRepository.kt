package com.example.alloon_aos.data.repository

import com.example.alloon_aos.data.model.EmailCode
import com.example.alloon_aos.data.model.AuthDTO
import com.example.alloon_aos.data.model.UserData
import com.example.alloon_aos.data.model.NewPwd
import com.example.alloon_aos.data.remote.ApiService
import okhttp3.Headers
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
                    var error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
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
                    var error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
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
                    var error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<AuthDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }

    fun signUp(userData: UserData,callback: MainRepositoryCallback<Pair<AuthDTO, Headers>>) {
        //val data = UserData("byeolstar12@naver.com","NwEkGX","hb_hb_hb","password1!","password1!")
        apiService.post_signUp(userData).enqueue(object : Callback<AuthDTO> {
            override fun onResponse(call: Call<AuthDTO>, response: Response<AuthDTO>) {
                if (response.isSuccessful) {
                    val pair = Pair(response.body()!!, response.headers())
                    callback.onSuccess(pair)
                } else {
                    var error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
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
                    var error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<AuthDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }

    fun sendNewPassword(user: UserData, callback: MainRepositoryCallback<AuthDTO>) {
        apiService.post_findPwd(user).enqueue(object : Callback<AuthDTO> {
            override fun onResponse(call: Call<AuthDTO>, response: Response<AuthDTO>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    var error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<AuthDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }

    fun login(user: UserData,callback: MainRepositoryCallback<Pair<AuthDTO, Headers>>) {
        apiService.post_login(user).enqueue(object : Callback<AuthDTO> {
            override fun onResponse(call: Call<AuthDTO>, response: Response<AuthDTO>) {
                if (response.isSuccessful) {
                    val pair = Pair(response.body()!!, response.headers())
                    callback.onSuccess(pair)
                } else {
                    var error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<AuthDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }

    fun modifyPassword(token : String,newPwd : NewPwd, callback: MainRepositoryCallback<AuthDTO>) {
        apiService.patch_modifyPwd("Bearer $token",newPwd).enqueue(object : Callback<AuthDTO> {
            override fun onResponse(call: Call<AuthDTO>, response: Response<AuthDTO>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    var error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<AuthDTO>, t: Throwable) {
                callback.onFailure(t)

            }
        })
    }
}