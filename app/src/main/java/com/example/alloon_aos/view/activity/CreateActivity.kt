package com.example.alloon_aos.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ActivityCreateBinding

class CreateActivity : AppCompatActivity() {
    lateinit var binding : ActivityCreateBinding
    lateinit var navHostFragment : NavHostFragment
    lateinit var navController : NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create)

        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(),
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav) as NavHostFragment
        navController = navHostFragment.navController

        binding.actionBar.setupWithNavController(navController, appBarConfiguration)

    }

    fun setAppBar() {
        binding.actionBar.setNavigationIcon(R.drawable.ic_back)
    }

}