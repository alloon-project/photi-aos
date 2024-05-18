package com.example.alloon_aos.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ActivityMainBinding
import com.example.alloon_aos.view.fragment.Auth.FindIdFragment
import com.example.alloon_aos.view.fragment.Auth.LoginFragment
import com.example.alloon_aos.view.fragment.Auth.SignUpFragment

class MainActivity : AppCompatActivity() {
    private val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.button3.setOnClickListener {
            finish()
            startActivity(Intent(this,AuthActivity::class.java))
        }
    }
}