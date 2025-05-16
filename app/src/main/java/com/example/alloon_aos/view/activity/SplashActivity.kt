package com.example.alloon_aos.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import com.example.alloon_aos.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed(Runnable {
            val i = Intent(this@SplashActivity,PhotiActivity::class.java)
            startActivity(i)
            finish()
        }, 2000)
    }
}