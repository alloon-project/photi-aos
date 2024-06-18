package com.example.alloon_aos.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alloon_aos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.authButton.setOnClickListener {
            finish()
            startActivity(Intent(this,AuthActivity::class.java))
        }

        binding.settingButton.setOnClickListener{
            finish()
            startActivity(Intent(this,SettingActivity::class.java))
        }

        binding.reportButton.setOnClickListener {
            finish()
            startActivity(Intent(this,ReportActivity::class.java))
        }
    }
}