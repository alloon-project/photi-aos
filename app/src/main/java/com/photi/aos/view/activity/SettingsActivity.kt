package com.photi.aos.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.photi.aos.MyApplication
import com.photi.aos.R
import com.photi.aos.data.storage.TokenManager
import com.photi.aos.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    lateinit var binding : ActivitySettingsBinding
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_settings)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(),
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )

        binding.actionBar.setupWithNavController(navController, appBarConfiguration)

        binding.actionBar.setNavigationOnClickListener {
            val currentFragment = navController.currentDestination?.id
            if (currentFragment == R.id.mainSettingsFragment) {
                finish()
            } else {
                navController.navigateUp()  // 이전 프래그먼트로 돌아가기
            }
        }
    }

    fun setAppBar(appTitle : String) {
        binding.actionBar.setNavigationIcon(R.drawable.ic_back)
        binding.title.setText(appTitle)
    }

    // 사용하지 않는 메서드 제거
    override fun onSupportNavigateUp(): Boolean {
        // 기본적으로 액티비티 종료가 처리되므로 필요 없음
        return super.onSupportNavigateUp()
    }

    fun logout(){
        tokenManager.deleteAllToken()
        finishAffinity()
        val intent = Intent(this, PhotiActivity::class.java).apply {
            putExtra("IS_FROM","LOGOUT")
        }
        startActivity(intent)
    }

    fun unsubscribe(){
        tokenManager.deleteAllToken()
        finishAffinity()
        val intent = Intent(this, PhotiActivity::class.java).apply {
            putExtra("IS_FROM","UNSUBSCRIBE")
        }
        startActivity(intent)
    }

}