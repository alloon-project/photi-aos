package com.example.alloon_aos.view.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
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
   lateinit var navHostFragment : NavHostFragment
   lateinit var navController : NavController
   var isFromSettingsActivity = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_auth)

        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(),
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )
        isFromSettingsActivity = intent.getBooleanExtra("IS_FROM_SETTINGS_ACTIVITY",false)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav) as NavHostFragment
        navController = navHostFragment.navController

        setNavGraph(isFromSettingsActivity)

        binding.actionBar.setupWithNavController(navController, appBarConfiguration)

        binding.actionBar.setNavigationOnClickListener {
            val currentFragment = navController.currentDestination?.id
            if (currentFragment == R.id.passwordSendFragment && isFromSettingsActivity) {
                finish()
            } else {
                navController.navigateUp()  // 이전 프래그먼트로 돌아가기
            }
        }

    }

    private fun setNavGraph(isAlreadyLogin: Boolean) {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_auth)
        if (isAlreadyLogin) navGraph.setStartDestination(R.id.passwordSendFragment)
        else navGraph.setStartDestination(R.id.loginFragment)
        navController.setGraph(navGraph, null) //navController에 graph 설정
    }

    fun setAppBar(appTitle : String) {
        binding.actionBar.setNavigationIcon(R.drawable.ic_back)
        binding.title.setText(appTitle)
    }

    fun finishActivity() {
        val resultIntent = Intent().apply {
            putExtra("isFromPasswordChangeFragment", true)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
