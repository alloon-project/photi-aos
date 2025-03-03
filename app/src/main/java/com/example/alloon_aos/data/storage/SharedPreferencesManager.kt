package com.example.alloon_aos.data.storage

import android.util.Log
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(
    private val sharedPreferences: MySharedPreferences
) {

    companion object {
        private const val GOAL_KEY = "myGaol"
        private const val FEED_ID = "feedId"
        private const val USER_NAME = "userName"
    }

    //user name
    fun saveUserName(name: String){
        sharedPreferences.setString(USER_NAME, name)
    }

    fun getUserName() : String?{
        return sharedPreferences.getString(USER_NAME, null)
    }
}
