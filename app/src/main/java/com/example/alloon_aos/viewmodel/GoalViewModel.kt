package com.example.alloon_aos.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alloon_aos.data.model.ActionApiResponse
import com.example.alloon_aos.data.model.request.Goal
import com.example.alloon_aos.data.model.request.JoinData
import com.example.alloon_aos.data.model.response.MessageResponse
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.ChallengeRepository
import com.example.alloon_aos.data.repository.ChallengeRepositoryCallback
import com.example.alloon_aos.data.repository.ErrorHandler

class GoalViewModel : ViewModel() {
    companion object {
        private const val TAG = "GOAL"
    }

    private val apiService = RetrofitClient.challengeService
    private val repository = ChallengeRepository(apiService)

    val apiResponse = MutableLiveData<ActionApiResponse>()
    val joinResponse = MutableLiveData<ActionApiResponse>()

    var id = -1
    var goal = ""
    var code = ""

    fun resetApiResponseValue() {
        apiResponse.value = ActionApiResponse()
        joinResponse.value = ActionApiResponse()
    }

    fun setGoal() {
        repository.setGoal(id, Goal(goal), object : ChallengeRepositoryCallback<MessageResponse> {
            override fun onSuccess(data: MessageResponse) {
                val result = data.code
                val mes = data.message
                apiResponse.value = ActionApiResponse(result)
                Log.d(TAG, "setGoal: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                val errorCode = ErrorHandler.handle(error)
                apiResponse.value = ActionApiResponse(errorCode)
            }
        })
    }

    fun joinPublicChallenge() {
        repository.joinPublicChallenge(id, Goal(goal), object  : ChallengeRepositoryCallback<MessageResponse> {
            override fun onSuccess(data: MessageResponse) {
                val result = data.code
                val mes = data.message
                joinResponse.value = ActionApiResponse(result)
                Log.d(TAG, "joinPublicChallenge: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                val errorCode = ErrorHandler.handle(error)
                joinResponse.value = ActionApiResponse(errorCode)
            }
        })
    }

    fun joinPrivateChallenge() {
        repository.joinPrivateChallenge(id, JoinData(code, goal), object  : ChallengeRepositoryCallback<MessageResponse> {
            override fun onSuccess(data: MessageResponse) {
                val result = data.code
                val mes = data.message
                joinResponse.value = ActionApiResponse(result)
                Log.d(TAG, "joinPrivateChallenge: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                val errorCode = ErrorHandler.handle(error)
                joinResponse.value = ActionApiResponse(errorCode)
            }
        })
    }
}