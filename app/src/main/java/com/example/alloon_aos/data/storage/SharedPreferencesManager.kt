package com.example.alloon_aos.data.storage

import android.util.Log
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(
    private val sharedPreferences: MySharedPreferences
) {

    companion object {
        private const val GOAL_KEY = "myGaol"
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
}
