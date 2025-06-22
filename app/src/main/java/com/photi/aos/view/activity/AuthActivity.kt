package com.photi.aos.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.photi.aos.R
import com.photi.aos.databinding.ActivityAuthBinding
import com.photi.aos.viewmodel.AuthViewModel

class AuthActivity : AppCompatActivity() {
    lateinit var binding : ActivityAuthBinding
    lateinit var navHostFragment : NavHostFragment
    lateinit var navController : NavController
    var isFromSettingsActivity = false
    private val authViewModel : AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_auth)
        binding.viewModel = authViewModel

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
            if (currentFragment == R.id.passwordSendFragment && isFromSettingsActivity)
                finish()
            else if (currentFragment == R.id.loginFragment)
                finish()
            else
                navController.navigateUp()
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
            putExtra("IS_FROM_LOGIN", true)
            putExtra("id", authViewModel.id )
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
