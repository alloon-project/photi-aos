package com.example.alloon_aos.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ActivityJoinBinding
import com.example.alloon_aos.view.fragment.join.JoinChallengeFragment

class JoinActivity : AppCompatActivity() {
    lateinit var binding : ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_Layout, JoinChallengeFragment())
            .commit()
    }

    fun setAppBar(appTitle : String) {
        binding.actionBar.setNavigationIcon(R.drawable.ic_back)
        binding.title.setText(appTitle)

        binding.actionBar.setNavigationOnClickListener {
            supportFragmentManager.popBackStack()
        }
    }

}