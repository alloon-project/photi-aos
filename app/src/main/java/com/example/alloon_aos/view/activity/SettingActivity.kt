package com.example.alloon_aos.view.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ActivityAuthBinding
import com.example.alloon_aos.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    lateinit var binding : ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_setting)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(),
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )


        binding.actionBar.setupWithNavController(navController, appBarConfiguration)
    }

    fun setAppBar(appTitle : String) {
        binding.actionBar.setNavigationIcon(R.drawable.ic_back)
        binding.title.setText(appTitle)
    }
}