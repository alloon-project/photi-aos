package com.example.alloon_aos.data.repository

import android.util.Log
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(
    private val sharedPreferences: MySharedPreferences
) {

    companion object {
        private const val GOAL_KEY = "myGaol"
        private const val CHALLENGE_ID = "challengeId"
        private const val FEED_ID = "feedId"
    }

    //목표
    fun saveMyGoal(token: String){
        sharedPreferences.setString(GOAL_KEY, token)
        Log.d("test","set myGoal : "+getMyGoal())
    }

    fun getMyGoal(): String?{
        return sharedPreferences.getString(GOAL_KEY,null)
    }

    fun saveChallengeId(id: String){
        sharedPreferences.setString(CHALLENGE_ID, id)
        Log.d("test","set challengeId : "+getChallengeId())
    }

    fun getChallengeId(): String? {
        return sharedPreferences.getString(CHALLENGE_ID, null)
    }
}
