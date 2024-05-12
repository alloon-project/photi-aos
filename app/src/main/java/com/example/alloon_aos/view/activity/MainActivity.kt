package com.example.alloon_aos.view.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ActivityMainBinding
import com.example.alloon_aos.viewmodel.AuthViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        authViewModel.getData()

        setFrag(1)
    }
    //프래그먼트를 교체해주는 함수
    public fun setFrag(fragNum : Int) {
        //프래그먼트를 관리하는 클래스
        val ft = supportFragmentManager.beginTransaction()
        when(fragNum){
            1 -> {
                ft.replace(R.id.main_frame  , LoginFragment()).commit()
            }
            2 -> {
                ft.replace(R.id.main_frame , SignUpFragment()).commit()
            }
            3 -> {
                println("!!")
            }
        }
    }
}