package com.photi.aos.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.photi.aos.R
import com.photi.aos.data.enum.CategoryType
import com.photi.aos.databinding.ActivityReportBinding
import com.photi.aos.view.fragment.report.ReportFeedFragment
import com.photi.aos.view.fragment.report.ReportMemberFragment
import com.photi.aos.view.fragment.report.ReportMissionFragment
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.viewmodel.ReportViewModel

class ReportActivity : AppCompatActivity() {
    lateinit var binding : ActivityReportBinding
    private val reportViewModel : ReportViewModel by viewModels()
    lateinit var category : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_report)

        val id = intent.getIntExtra("Id", -1)
        reportViewModel.targetId = id

        val type = intent.getStringExtra("Category")
        if (type != null)
            category = type
        else finish()

        setLayout()
        setListener()

        binding.actionBar.setNavigationIcon(R.drawable.ic_back)
    }

    fun setLayout() {
        when(category) {
            "Challenge" -> {
                reportViewModel.category = CategoryType.CHALLENGE
                supportFragmentManager.beginTransaction()
                    .replace(R.id.report_framelayout, ReportMissionFragment())
                    .commit()
            }
            "Member" -> {
                reportViewModel.category = CategoryType.CHALLENGE_MEMBER
                supportFragmentManager.beginTransaction()
                    .replace(R.id.report_framelayout, ReportMemberFragment())
                    .commit()
            }
            "Feed" -> {
                reportViewModel.category = CategoryType.FEED
                supportFragmentManager.beginTransaction()
                    .replace(R.id.report_framelayout, ReportFeedFragment())
                    .commit()
            }
            else -> {
                finish()
            }
        }
    }

    fun setListener() {
        binding.actionBar.setNavigationOnClickListener {
            finish()
        }

        reportViewModel.code.observe(this) {code ->
            when(code) {
                "201 CREATED" -> {
                    val resultIntent = Intent()
                    resultIntent.putExtra("IS_FROM_REPORT",true)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
                else -> {
                    handleApiError(code)
                }
            }
        }
    }

    private fun handleApiError(code: String) {
        val errorMessages = mapOf(
            "TOKEN_UNAUTHENTICATED" to "승인되지 않은 요청입니다. 다시 로그인 해주세요.",
            "TOKEN_UNAUTHORIZED" to "권한이 없는 요청입니다. 로그인 후 다시 시도해주세요.",
            "USER_NOT_FOUND" to "존재하지 않는 회원입니다.",
            "CHALLENGE_NOT_FOUND" to "존재하지 않는 챌린지입니다.",
            "CHALLENGE_MEMBER_NOT_FOUND" to "존재하지 않는 챌린지 파티원입니다.",
            "FEED_NOT_FOUND" to "존재하지 않는 피드입니다.",
            "UNKNOWN_ERROR" to "알 수 없는 오류가 발생했습니다."
        )

        if (code == "IO_Exception") {
            CustomToast.createToast(this, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
        } else {
            val message = errorMessages[code] ?: "예기치 않은 오류가 발생했습니다. ($code)"
            Log.e("IntroduceFragment", "Error: $message")
        }
    }
}