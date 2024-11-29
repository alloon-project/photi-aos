package com.example.alloon_aos.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ActivityCreateBinding
import com.example.alloon_aos.view.fragment.create.CreateChallengeFragment
import com.example.alloon_aos.view.fragment.create.CreateContentFragment
import com.example.alloon_aos.view.fragment.create.CreateHashtagFragment
import com.example.alloon_aos.view.fragment.create.CreateImageFragment
import com.example.alloon_aos.view.fragment.create.CreateNameFragment
import com.example.alloon_aos.view.fragment.create.CreateRuleFragment

class CreateActivity : AppCompatActivity() {
    lateinit var binding : ActivityCreateBinding
    lateinit var navHostFragment : NavHostFragment
    lateinit var navController : NavController
    var isFromChallenge = false

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

        isFromChallenge = intent.getBooleanExtra("IS_FROM_CHALLENGE",false)

        if(isFromChallenge) {
            val modify = intent.getStringExtra("MODIFY")
            val fragment: Fragment

            when(modify) {
                "title" -> {
                    fragment = CreateNameFragment()
                }
                "hash" -> {
                    fragment = CreateHashtagFragment()
                }
                "photo" -> {
                    fragment = CreateImageFragment()
                }
                "time" -> {
                    fragment = CreateContentFragment()
                }
                "goal" -> {
                    fragment = CreateContentFragment()
                }
                "rule" -> {
                    fragment = CreateRuleFragment()
                }
                "date" -> {
                    fragment = CreateContentFragment()
                }
                else -> {
                    fragment = CreateChallengeFragment()
                }
            }

            val fragmentManager: FragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.nav, fragment)
                .commit()
        }

        binding.actionBar.setNavigationOnClickListener {
            val currentFragment = navController.currentDestination?.id
            if (isFromChallenge || currentFragment == R.id.createChallengeFragment)
                finish()
            else
                navController.navigateUp()
        }

    }

    fun setAppBar() {
        binding.actionBar.setNavigationIcon(R.drawable.ic_back)
    }

    fun setTitle(title : String) {
        binding.title.setText(title)
    }

}