package com.example.alloon_aos.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ActivityInquiryBinding

class InquiryActivity : AppCompatActivity() {
    lateinit var binding : ActivityInquiryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inquiry)

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