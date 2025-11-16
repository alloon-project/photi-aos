package com.photi.aos.data.storage

import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(
    private val sharedPreferences: MySharedPreferences
) {

    companion object {
        private const val USER_NAME = "userName"
        private const val SEARCH_HISTORY = "searchHistory"
    }

    //user name
    fun saveUserName(name: String) {
        sharedPreferences.setString(USER_NAME, name)
    }
    fun getUserName() : String? {
        return sharedPreferences.getString(USER_NAME, null)
    }

    //search history
    fun saveSearchHistory(list: List<String>) {
        val limitedList = list.take(10)
        sharedPreferences.setString(SEARCH_HISTORY, limitedList.joinToString(","))
    }
    fun getSearchHistory(): List<String> {
        val savedList = sharedPreferences.getString(SEARCH_HISTORY, "") ?: ""
        val returnList = if (savedList.isBlank()) emptyList() else savedList.split(",")
        return returnList
    }
}
