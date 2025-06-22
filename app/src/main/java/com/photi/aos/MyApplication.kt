package com.photi.aos

import android.app.Application
import android.content.Intent
import com.photi.aos.data.storage.MySharedPreferences
import com.photi.aos.view.activity.PhotiActivity

/**
 * 이 곳에 SharedPreferences를 선언하는 이유는
 * Context 참조 및 모든 곳에서 사용할 수 있게하기 위함이다.
 */
class MyApplication: Application() {

    companion object {
        lateinit var mySharedPreferences: MySharedPreferences
        lateinit var instance: MyApplication
            private set
        var isTokenExpired = false
    }

    override fun onCreate() {
        super.onCreate()
        mySharedPreferences = MySharedPreferences(applicationContext)
        instance = this
    }

    fun tokenExpired() {
        isTokenExpired = true
        val intent = Intent(instance.applicationContext, PhotiActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        instance.applicationContext.startActivity(intent)
    }
}