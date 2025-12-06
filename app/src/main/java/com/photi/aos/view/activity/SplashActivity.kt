package com.photi.aos.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import com.photi.aos.R
import com.photi.aos.BuildConfig
import com.photi.aos.view.ui.component.dialog.CustomTwoButtonDialog
import com.photi.aos.view.ui.component.dialog.CustomTwoButtonDialogInterface
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.viewmodel.AuthViewModel

class SplashActivity : AppCompatActivity(), CustomTwoButtonDialogInterface {
    private val authViewModel : AuthViewModel by viewModels()
    private var isFromRouter: Boolean = false
    private var deepLinkId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkFromRouter(intent)

        authViewModel.needUpdate.observe(this) { needUpdate ->
            if (needUpdate) {
                CustomTwoButtonDialog(this@SplashActivity,
                    "업데이트가 필요해요",
                    "최신 버전 업데이트를 위해\n" + "스토어로 이동합니다.",
                    "닫기","스토어 바로가기")
                    .show(supportFragmentManager,"CustomDialog")
            } else {
                Handler().postDelayed(Runnable {
                    val i = Intent(this@SplashActivity,PhotiActivity::class.java)
                    if (isFromRouter) {
                        i.putExtra("fromRouter", true)
                        i.putExtra("deepLinkId", deepLinkId)
                    }
                    startActivity(i)
                    finish()
                }, 500)
            }
        }

        authViewModel.splashResponse.observe(this) { response ->
            when (response.code) {
                "OS_POLICY_NOT_FOUND" -> {
                    CustomToast.createToast(this@SplashActivity, "존재하지 않는 OS 정책입니다.", "circle")?.show()
                }
                "IO_Exception" -> {
                    CustomToast.createToast(this@SplashActivity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }
                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                }
            }
        }
    }

    private fun checkFromRouter(intent: Intent) {
        isFromRouter = intent.getBooleanExtra("fromRouter", false)
        deepLinkId = intent.getIntExtra("deepLinkId", -1)

        authViewModel.checkUpdate(BuildConfig.VERSION_NAME)
    }


    override fun onClickFisrtButton() {
        finishAffinity()
    }

    override fun onClickSecondButton() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=$packageName")
            setPackage("com.android.vending")
        }
        startActivity(intent)
        finishAffinity()
    }
}