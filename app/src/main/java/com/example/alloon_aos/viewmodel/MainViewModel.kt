package com.example.alloon_aos.viewmodel

import android.net.http.NetworkException
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alloon_aos.data.model.AuthDTO
import com.example.alloon_aos.data.model.UserData
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.MyRepository
import com.example.alloon_aos.data.repository.MainRepositoryCallback
import okhttp3.Headers
import okio.IOException
import org.json.JSONObject

class MainViewModel : ViewModel() {
    private val apiService = RetrofitClient.apiService
    private val repository = MyRepository(apiService)

    var toast_message = MutableLiveData("")
    var code =  MutableLiveData("") //observer가 필요한 경우만 Mutable
    var email = ""
    var id = ""
    var password = ""
    var hello = ""


    fun doIt(){
        val email : Map<String, String> = mapOf("email" to "jse05150@naver.com")
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

//        repository.verifyEmailCode(EmailCode("jse05150@naver.com","3jMba2"),object :
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
//        repository.verifyId("seulnaver",object :
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

//        repository.signUp(UserData("jse05150@naver.com","3jMba2","seulnaver","password1!","password1!")
//            ,object : MainRepositoryCallback<Pair<AuthDTO,Headers>> {
//            override fun onSuccess(data: Pair<AuthDTO,Headers>) {
//                val (_data,header) = data
//                val result = _data.code //ex."USERNAME_SENT"
//                val mes = _data.message
//                val access_token = header.get("Authorization").toString()
//                val refresh_token = header.get("Refresh-Token").toString()
//                code.value = result
//                Log.d("TAG","login: $id $result")
//                Log.d("TAG","a_token:  $access_token")
//                Log.d("TAG","r_token:  $refresh_token")
//                Log.d("TAG","signUp: $mes $result")
//            }
//
//            override fun onFailure(error: Throwable) {
//                Log.d("TAG","signUp" + error.message.toString())
//            }
//        })


//        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyIiwiaWF0IjoxNzE1ODg3ODE4LCJleHAiOjE3MTU4ODk2MTgsImlzcyI6Im5vZ2Ftc3VuZy5jb20iLCJyb2xlcyI6WyJVU0VSIl19.JDIzJw8zNSSIKbtPracux3XZapuQ520iblHzLQcWsr8"
//        val newPwd = NewPwd("password2!","password3!","password3!")
//        repository.modifyPwd(token,newPwd,object :
//            MainRepositoryCallback<AuthDTO> {
//            override fun onSuccess(data: AuthDTO) {
//                val result = data.code
//                val mes = data.message
//                Log.d("TAG","modifyPwd: $mes $result")
//            }
//
//            override fun onFailure(error: Throwable) {
//                Log.d("TAG","modifyPwd"+ error.message.toString())
//            }
//        })
    }


    fun login() {
        val user  = UserData(username = id, password = password)
        repository.login(user,object :
            MainRepositoryCallback<Pair<AuthDTO, Headers>> {
            override fun onSuccess(data: Pair<AuthDTO, Headers>) {
                val (_data,header) = data
                val result = _data.code //ex."USERNAME_SENT"
                val mes = _data.message
                val access_token = header.get("Authorization").toString()
                val refresh_token = header.get("Refresh-Token").toString()
                code.value = result
                Log.d("TAG","login: $id $result")
                Log.d("TAG","1~~  $access_token")
                Log.d("TAG","2~~ $refresh_token")
            }

            override fun onFailure(error: Throwable) {
                Log.d("TAG","onFailure : ${error.toString()} " )
                when(error){
                    is IOException -> {
                        toast_message.value = "인터넷이나 서버을 연결 확인해주세요"
                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        code.value = jObjError.getString("code") //ex."USER_NOT_FOUND"
                        Log.d("TAG","login id : $id pwd: $password response: " +code.value )
                    }
                }
            }
        })
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
                when(error){
                    is IOException -> {
                        toast_message.value = "인터넷이나 서버 연결을 확인해주세요"

                    }
                    else -> {
                        val jObjError = JSONObject(error.message.toString())
                        code.value = jObjError.getString("code")
                        Log.d("TAG","error: " + code.value)
                    }
                }
            }
        })
    }


}