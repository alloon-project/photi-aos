package com.photi.aos.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.photi.aos.data.model.ActionApiResponse
import com.photi.aos.data.model.request.Goal
import com.photi.aos.data.model.response.MessageResponse
import com.photi.aos.data.remote.RetrofitClient
import com.photi.aos.data.repository.ChallengeRepository
import com.photi.aos.data.repository.ChallengeRepositoryCallback
import com.photi.aos.data.repository.ErrorHandler

class GoalViewModel : ViewModel() {
    companion object {
        private const val TAG = "GOAL"
    }

    private val apiService = RetrofitClient.challengeService
    private val repository = ChallengeRepository(apiService)

    val apiResponse = MutableLiveData<ActionApiResponse>()

    var id = -1
    var goal = ""

    fun resetApiResponseValue() {
        apiResponse.value = ActionApiResponse()
    }


    fun joinChallenge() {
        repository.joinChallenge(id, Goal(goal), object  : ChallengeRepositoryCallback<MessageResponse> {
            override fun onSuccess(data: MessageResponse) {
                val result = data.code
                val mes = data.message
                apiResponse.value = ActionApiResponse(result)
                Log.d(TAG, "joinChallenge: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                val errorCode = ErrorHandler.handle(error)
                apiResponse.value = ActionApiResponse(errorCode)
            }
        })
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
}