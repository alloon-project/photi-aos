package com.example.alloon_aos.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.data.model.request.HashTag
import com.example.alloon_aos.data.model.request.Rule
import com.example.alloon_aos.databinding.ActivityCreateBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.CreateViewModel

class CreateActivity : AppCompatActivity() {
    lateinit var binding : ActivityCreateBinding
    lateinit var navHostFragment : NavHostFragment
    lateinit var navController : NavController
    private val createViewModel : CreateViewModel by viewModels()
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

        val navGraph = navController.navInflater.inflate(R.navigation.nav_create)

        isFromChallenge = intent.getBooleanExtra("IS_FROM_CHALLENGE",false)

        if(isFromChallenge) {
            val modify = intent.getStringExtra("MODIFY")

            when(modify) {
                "title" -> {
                    val titleData = intent.getStringExtra("titleData")
                    titleData?.let { createViewModel.setTitleData(it) }
                    navGraph.setStartDestination(R.id.createNameFragment)
                }
                "photo" -> {
                    setObserver()
                    createViewModel.getExamImg()
                }
                "content" -> {
                    val goalData = intent.getStringExtra("goalData")
                    val timeData = intent.getStringExtra("timeData")
                    val dateData = intent.getStringExtra("dateData")
                    goalData?.let { createViewModel.setGoalData(it) }
                    timeData?.let { createViewModel.setTimeData(it) }
                    dateData?.let { createViewModel.setDateDate(it) }
                    navGraph.setStartDestination(R.id.createContentFragment)
                }
                "rule" -> {
                    val ruleData: List<Rule>? = intent.getParcelableArrayListExtra("ruleData")
                    ruleData?.let { createViewModel.setRuleData(it) }
                    navGraph.setStartDestination(R.id.createRuleFragment)
                }
                "hash" -> {
                    val hashData: List<HashTag>? = intent.getParcelableArrayListExtra("hashData")
                    hashData?.let { createViewModel.setHashData(it) }
                    navGraph.setStartDestination(R.id.createHashtagFragment)
                }
                else -> {
                    navGraph.setStartDestination(R.id.createChallengeFragment)
                }
            }
        } else {
            navGraph.setStartDestination(R.id.createChallengeFragment)
        }

        navController.setGraph(navGraph, null)

        binding.actionBar.setupWithNavController(navController, appBarConfiguration)

        binding.actionBar.setNavigationOnClickListener {
            val currentFragment = navController.currentDestination?.id
            if (isFromChallenge || currentFragment == R.id.createChallengeFragment)
                finish()
            else
                navController.navigateUp()
        }
    }

    fun setObserver() {
        createViewModel.apiResponse.observe(this) { response ->
            when (response.code) {
                "200 OK" -> {
                    val photoData = intent.getStringExtra("photoData")
                    photoData?.let { createViewModel.setImgData(it) }

                    val navGraph = navController.navInflater.inflate(R.navigation.nav_create)
                    navGraph.setStartDestination(R.id.createImageFragment)
                    navController.setGraph(navGraph, null)
                }

                "LOGIN_UNAUTHENTICATED", "DELETED_USER" -> {
                    finish()
                }

                "IO_Exception" -> {
                    CustomToast.createToast(this@CreateActivity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                    finish()
                }

                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                    finish()
                }
            }
        }
    }

    fun modifyName() {
        val resultIntent = Intent()
        resultIntent.putExtra("title",createViewModel.name)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
    fun modifyContent() {
        val resultIntent = Intent()
        resultIntent.putExtra("time",createViewModel.proveTime)
        resultIntent.putExtra("goal",createViewModel.goal)
        resultIntent.putExtra("date",createViewModel.endDate)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
    fun modifyImage() {
        val resultIntent = Intent()
        resultIntent.putExtra("photo",createViewModel.imgFile)
        resultIntent.putExtra("isUri",createViewModel.isUri)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
    fun modifyRule() {
        val resultIntent = Intent()
        resultIntent.putParcelableArrayListExtra("rule", ArrayList(createViewModel.rules))
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
    fun modifyHash() {
        val resultIntent = Intent()
        resultIntent.putParcelableArrayListExtra("hash", ArrayList(createViewModel.hashtags))
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    fun setAppBar() {
        binding.actionBar.setNavigationIcon(R.drawable.ic_back)
    }

    fun setTitle(title : String) {
        binding.title.setText(title)
    }

}