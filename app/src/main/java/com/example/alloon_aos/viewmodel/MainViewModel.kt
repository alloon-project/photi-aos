package com.example.alloon_aos.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alloon_aos.data.model.AuthDTO
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.MyRepository
import com.example.alloon_aos.data.repository.MainRepositoryCallback

class MainViewModel : ViewModel() {
    private val apiService = RetrofitClient.apiService
    private val repository = MyRepository(apiService)
//    var email = MutableLiveData("")
//    var password = MutableLiveData("")

    fun findId(){
                repository.findId(object :
            MainRepositoryCallback<AuthDTO> {
            override fun onSuccess(data: AuthDTO) {
                val result = data.code
                val mes = data.message
                Log.d("TAG","findId: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                Log.d("TAG","error: " + error.message.toString())
            }
        })
    }

}