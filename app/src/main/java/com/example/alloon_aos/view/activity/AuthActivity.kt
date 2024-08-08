package com.example.alloon_aos.view.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.alloon_aos.MyApplication.Companion.mySharedPreferences
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ActivityAuthBinding
import com.example.alloon_aos.view.fragment.auth.PasswordChangeFragment
import com.example.alloon_aos.view.fragment.auth.PasswordSendFragment

class AuthActivity : AppCompatActivity() {
   lateinit var binding : ActivityAuthBinding
   var isFromSettingsActivity = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_auth)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(),
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )

        binding.actionBar.setupWithNavController(navController, appBarConfiguration)


        isFromSettingsActivity = intent.getBooleanExtra("IS_FROM_SETTINGS_ACTIVITY",false)
        if(isFromSettingsActivity){
            supportFragmentManager.beginTransaction().replace(R.id.nav, PasswordSendFragment()).commit()
            PasswordChangeFragment().apply{
                arguments = Bundle().apply {
                    putBoolean("IS_FROM_SETTINGS_ACTIVITY",isFromSettingsActivity)
                }
            }
        }

    }

    fun setAppBar(appTitle : String) {
        binding.actionBar.setNavigationIcon(R.drawable.ic_back)
        binding.title.setText(appTitle)
    }
}
