package com.photi.aos.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.photi.aos.data.storage.MyChallengeList
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.viewmodel.PhotiViewModel

class RouterActivity : AppCompatActivity() {
    private val photiViewModel : PhotiViewModel by viewModels()
    private var pageId : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photiViewModel.resetAllResponseValue()
        setObserver()

        val isFromRouter = intent.getBooleanExtra("fromRouter", false)

        if (isFromRouter) handleIntent()
        else handleDeepLink(intent)
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleIntent() {
        val deepLinkId = intent.getIntExtra("deepLinkId", -1)

        if (deepLinkId != -1) checkMyList(deepLinkId)
        else pageIsNull()
    }
    private fun handleDeepLink(intent: Intent) {
        val data = intent.data ?: return
        val pageId = data.getQueryParameter("page")?.toIntOrNull() ?: -1

        if (isTaskRoot) checkTaskRoot(pageId)

        if (pageId != -1) checkMyList(pageId)
        else pageIsNull()
    }

    private fun checkTaskRoot(pageId: Int) {
        startActivity(
            Intent(this, SplashActivity::class.java)
                .putExtra("fromRouter", true)
                .putExtra("deepLinkId", pageId)
        )
        finish()
    }

    private fun checkMyList(id: Int) {
        pageId = id
        photiViewModel.fetchMyChallenges()
    }

    private fun setObserver() {
        photiViewModel.code.observe(this) { code ->
            if (!code.equals("200 OK")) {
                photiViewModel.id = pageId
                photiViewModel.getChallenge()
            }
        }
        photiViewModel.myChallengeList.observe(this) {
            photiViewModel.id = pageId
            if (MyChallengeList.checkUserInChallenge(photiViewModel.id))
                startFeedActivity()
            else
                photiViewModel.getChallenge()
        }
        photiViewModel.apiResponse.observe(this) { response ->
            when (response.code) {
                "200 OK" -> {
                    startChallengeActivity()
                }
                "UNKNOWN" -> {
                    Log.d("Observer", "Unhandled response code: UNKNOWN")
                }
                else -> {
                    CustomToast.createToast(this, "Unhandled response code: ${response.code}", "circle")?.show()
                    finish()
                }
            }
        }
    }

    private fun startChallengeActivity() {
        val intent = Intent(this, ChallengeActivity::class.java)
        intent.putExtra("IS_FROM_HOME",true)
        intent.putExtra("ID", photiViewModel.id)
        intent.putExtra("data", photiViewModel.getData())
        intent.putExtra("image", photiViewModel.imgFile)
        startActivity(intent)
        finish()
    }
    private fun startFeedActivity() {
        val intent = Intent(this, FeedActivity::class.java)
        intent.putExtra("CHALLENGE_ID", photiViewModel.id)
        startActivity(intent)
        finish()
    }

    private fun pageIsNull() {
        Log.d("Router", "Deep Link Page is null")
        finish()
    }
}
