package com.photi.aos.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.photi.aos.MyApplication
import com.photi.aos.R
import com.photi.aos.data.model.MyData
import com.photi.aos.data.model.response.ChallengeMember
import com.photi.aos.data.model.response.FeedChallengeData
import com.photi.aos.data.storage.SharedPreferencesManager
import com.photi.aos.databinding.ActivityFeedBinding
import com.photi.aos.databinding.CustomPopupMenuBinding
import com.photi.aos.view.fragment.feed.IntroduceFragment
import com.photi.aos.view.fragment.feed.PartyMemberFragment
import com.photi.aos.view.fragment.feed.FeedFragment
import com.photi.aos.view.ui.component.dialog.CustomTwoButtonDialog
import com.photi.aos.view.ui.component.dialog.CustomTwoButtonDialogInterface
import com.photi.aos.view.ui.component.dialog.FeedDetailDialog
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.ui.util.dpToPx
import com.photi.aos.viewmodel.FeedViewModel
import com.google.android.material.tabs.TabLayout

class FeedActivity : AppCompatActivity(), CustomTwoButtonDialogInterface {
    lateinit var binding : ActivityFeedBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var reportForResult: ActivityResultLauncher<Intent>
    private val feedViewModel : FeedViewModel by viewModels()
    private lateinit var feedChallengeData : FeedChallengeData
    private var isLeader = false
    private val sharedPreferencesManager = SharedPreferencesManager(MyApplication.mySharedPreferences)

    private lateinit var tabLayout: TabLayout
    private lateinit var fixedButton: ImageButton
    private lateinit var fixedView: View

    private val feedFragment = FeedFragment()
    private val introduceFragment = IntroduceFragment()
    private val partyFragment = PartyMemberFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_feed)
        binding.activity = this

        tabLayout = binding.tabLayout
        fixedButton = binding.fixedImageButton
        fixedView = binding.fixedBgView

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_layout, feedFragment)
                .commit()
        }

        feedViewModel.resetResponse()

        val challengeId = intent.getIntExtra("CHALLENGE_ID", -1)
        val feedId = intent.getIntExtra("FEED_ID", -1)
        if(feedId != -1){
            val dialog = FeedDetailDialog(feedId = feedId)
            dialog.show(supportFragmentManager, "FeedDetailDialog")
        }

        feedViewModel.challengeId = challengeId

        if(challengeId != -1){
            feedViewModel.fetchChallenge()
            feedViewModel.fetchChallengeInfo()
            feedViewModel.fetchChallengeMembers()
            feedViewModel.fetchIsUserVerifiedToday()
        }

        setObserve()

        binding.shareImgBtn.setOnClickListener {
            if(!feedChallengeData.isPublic)
                feedViewModel.getInviteCode()
            else
                sendInviteMsg()
        }

        binding.ellipsisImgBtn.setOnClickListener { view ->
            setCustomPopUp(view)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()

                when(tab?.position){
                    0 ->{
                        fragmentTransaction.replace(R.id.frag_layout,feedFragment).commit()
                        if(feedViewModel.isUserVerifiedToday.value == true) {
                            fixedButton.visibility = View.GONE
                            fixedView.visibility = View.GONE
                        } else {
                            fixedButton.visibility = View.VISIBLE
                            fixedView.visibility = View.VISIBLE
                        }
                    }
                    1 -> {
                        fragmentTransaction.replace(R.id.frag_layout,introduceFragment ).commit()
                        fixedButton.visibility = View.GONE
                        fixedView.visibility = View.GONE
                    }
                    2 -> {
                        fragmentTransaction.replace(R.id.frag_layout,partyFragment).commit()
                        fixedButton.visibility = View.GONE
                        fixedView.visibility = View.GONE

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //updateTabView(tab, isSelected = false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val resultValue = data?.getIntExtra("ID", -1)
                resultValue?.let {
                    feedViewModel.challengeId = it
                    feedViewModel.fetchChallenge()
                    feedViewModel.fetchChallengeInfo()
                    CustomToast.createToast(this,"챌린지 수정이 완료됐어요.")?.show()
                }
            }
        }

        reportForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getBooleanExtra("IS_FROM_REPORT", false)
                if (data == true)
                    CustomToast.createToast(this,"신고가 완료됐어요. 꼼꼼히 확인하고, " +
                "회원님의 이메일로 답변을 보내드릴게요.")?.show()
            }
        }
    }

    private fun sendInviteMsg() {
//        val challengeId = feedViewModel.challengeId
//        val inviteCode = feedViewModel.invitecode
//        val challengeTitle = feedChallengeData.name
//
//        // 딥링크 URL 구성
//        val appLink = "https://photi.test/invite?challengeId=$challengeId"
//
//        // 메시지 구성
//        val message = if (!feedChallengeData.isPublic) {
//            "[Photi] '$challengeTitle' 챌린지에 함께 참여해 보세요!\n* 초대코드: $inviteCode\n\n$appLink"
//        } else {
//            "[Photi] '$challengeTitle' 챌린지에 함께 참여해 보세요!\n\n$appLink"
//        }
//
//        val sendIntent = Intent(Intent.ACTION_SEND).apply {
//            type = "text/plain"
//            putExtra(Intent.EXTRA_TEXT, message)
//        }
//
//        val chooser = Intent.createChooser(sendIntent, challengeTitle)
//        startActivity(chooser)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun applyBlurEffect(view: View?) {
        val blurEffect = RenderEffect.createBlurEffect(10f, 10f, Shader.TileMode.CLAMP)
        view?.setRenderEffect(blurEffect)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun removeBlurEffect(view: View?) {
        view?.setRenderEffect(null)  // 블러 효과 제거
    }

    private fun updateTabView(tab: TabLayout.Tab?, isSelected: Boolean) {
        val view = tab?.customView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (isSelected) {
                removeBlurEffect(view)
            } else {
                applyBlurEffect(view)
            }
        }
    }

    private fun setCustomPopUp(view: View) {
        val popupViewBinding = CustomPopupMenuBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(popupViewBinding.root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        with(popupViewBinding){
            if (isLeader)
                optionOne.text = "챌린지 수정하기"
            else
                optionOne.text = "챌린지 신고하기"

            optionTwo.text = "챌린지 탈퇴하기"

            optionOne.setOnClickListener {
                if (isLeader) {
                    val intent = Intent(this@FeedActivity, ChallengeActivity::class.java)
                    intent.putExtra("IS_FROM_FEED",true)
                    intent.putExtra("ID", feedViewModel.challengeId)
                    intent.putExtra("data", MyData(feedChallengeData.name, feedChallengeData.isPublic, feedChallengeData.goal, feedChallengeData.proveTime, feedChallengeData.endDate, feedChallengeData.rules, feedChallengeData.hashtags))
                    intent.putExtra("image", feedChallengeData.imageUrl)
                    activityResultLauncher.launch(intent)
                } else {
                    val intent = Intent(this@FeedActivity, ReportActivity::class.java)
                    intent.putExtra("Id",feedViewModel.challengeId)
                    intent.putExtra("Category", "Challenge")
                    reportForResult.launch(intent)
                }

                popupWindow.dismiss()
            }

            optionTwo.setOnClickListener {
                if (feedChallengeData.currentMemberCnt <= 1) {
                    CustomTwoButtonDialog(this@FeedActivity,"챌린지를 탈퇴할까요?","회원님은 이 챌린지의 마지막 파티원이에요.\n" +
                            "지금 탈퇴하면 챌린지가 삭제돼요.\n" +
                            "삭제된 챌린지는 복구할 수 없어요.\n" +
                            "정말 탈퇴하시겠어요? ","취소할게요","탈퇴할게요")
                        .show(binding.activity?.supportFragmentManager!!,"CustomDialog")
                } else {
                    CustomTwoButtonDialog(this@FeedActivity,"챌린지를 탈퇴할까요?","탈퇴해도 기록은 남아있어요.\n" +
                            "탈퇴하시기 전에 직접 지워주세요.","취소할게요","탈퇴할게요")
                        .show(binding.activity?.supportFragmentManager!!,"CustomDialog")
                }
                popupWindow.dismiss()
            }
        }
        popupWindow.showAsDropDown(view, 0, 0, Gravity.CENTER)
    }

    override fun onClickFisrtButton() {}

    override fun onClickSecondButton() {
        feedViewModel.deleteChallenge()
    }

    private fun setObserve() {
        feedViewModel.apiResponse.observe(this) { response ->
            when (response.code) {
                "200 OK" -> {
                    finishAffinity()
                    val intent = Intent(this, PhotiActivity::class.java).apply {
                        putExtra("IS_FROM","LEAVE")
                    }
                    startActivity(intent)
                }
                "CHALLENGE_NOT_FOUND" -> {
                    CustomToast.createToast(this, "존재하지 않는 챌린지입니다.")?.show()
                }
                "CHALLENGE_MEMBER_NOT_FOUND" -> {
                    CustomToast.createToast(this, "존재하지 않는 챌린지 파티원입니다.")?.show()
                }
                "TOKEN_UNAUTHENTICATED" -> {
                    CustomToast.createToast(this, "승인되지 않은 요청입니다. 다시 로그인 해주세요.")?.show()
                }
                "TOKEN_UNAUTHORIZED" -> {
                    CustomToast.createToast(this, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요.")?.show()
                }
                "IO_Exception" -> {
                    CustomToast.createToast(this, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }
                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                }
            }
        }

        feedViewModel.codeResponse.observe(this) { response ->
            when (response.code) {
                "200 OK" -> {
                    sendInviteMsg()
                }
                "CHALLENGE_NOT_FOUND" -> {
                    CustomToast.createToast(this, "존재하지 않는 챌린지입니다.")?.show()
                }
                "CHALLENGE_MEMBER_NOT_FOUND" -> {
                    CustomToast.createToast(this, "존재하지 않는 챌린지 파티원입니다.")?.show()
                }
                "TOKEN_UNAUTHENTICATED" -> {
                    CustomToast.createToast(this, "승인되지 않은 요청입니다. 다시 로그인 해주세요.")?.show()
                }
                "TOKEN_UNAUTHORIZED" -> {
                    CustomToast.createToast(this, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요.")?.show()
                }
                "IO_Exception" -> {
                    CustomToast.createToast(this, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }
                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                }
            }
        }

        feedViewModel.challengeMembers.observe(this) { data ->
            if (data != null) {
                val myName = sharedPreferencesManager.getUserName() ?: ""
                val creator: ChallengeMember? = data.find { it.isCreator }

                isLeader = if (creator != null) {
                    myName == creator.username
                } else {
                    //TODO()
                    Log.w("DEBUG", "Warning: Creator is null!")
                    false
                }
            }

        }

        feedViewModel.code.observe(this) { code ->
            when (code) {
                "200 OK" -> {
                }

                "CHALLENGE_NOT_FOUND" -> {
                    Log.e("FeedActivity", "Error: CHALLENGE_NOT_FOUND - 존재하지 않는 챌린지입니다.")
                }

                else -> {
                    Log.e("FeedActivity", "Error: $code - 예기치 않은 오류가 발생했습니다.")
                }
            }
        }

        feedViewModel.challenge.observe(this) { data ->
            if (data != null) {
                feedChallengeData = data

                Glide.with(binding.feedImgView.context)
                    .load(data.imageUrl)
                    .transform(CenterCrop())
                    .into(binding.feedImgView)

                binding.feedNameTextView.text = data.name


                val hashtags = data.hashtags.map { hashtag -> hashtag.hashtag } // Hashtag 이름 리스트 추출
                when (hashtags.size) {
                    1 -> {
                        binding.chip1Btn.text = hashtags[0]
                        binding.chip1Btn.visibility = View.VISIBLE
                        binding.chip2Btn.visibility = View.GONE
                    }

                    2 -> {
                        binding.chip1Btn.text = hashtags[0]
                        binding.chip2Btn.text = hashtags[1]
                        binding.chip1Btn.visibility = View.VISIBLE
                        binding.chip2Btn.visibility = View.VISIBLE
                    }

                    3 -> {
                        binding.chip1Btn.text = hashtags[0]
                        binding.chip2Btn.text = hashtags[1]
                        binding.chip3Btn.text = hashtags[2]
                        binding.chip1Btn.visibility = View.VISIBLE
                        binding.chip2Btn.visibility = View.VISIBLE
                        binding.chip3Btn.visibility = View.VISIBLE
                    }
                }
            }

        }

        feedViewModel.isUserVerifiedToday.observe(this) { isProve ->
            isProve?.let {
                if (it) {
                    fixedButton.visibility = View.GONE
                    fixedView.visibility = View.GONE
                } else {
                    fixedButton.visibility = View.VISIBLE
                    fixedView.visibility = View.VISIBLE
                    showToastAbove("오늘의 인증이 완료되지 않았어요!")
                }
            }
        }

        feedViewModel.feedUploadPhoto.observe(this) {isSuccess ->
            isSuccess?.let {
                if (it) {
                    showToastAbove("인증 완료! 오늘도 수고했어요!")
                    fetchFeed()
                }
            }
        }

        feedViewModel.deleteFeedCode.observe(this) { deletedId ->
            deletedId?.let {
                showToastAbove("피드가 삭제됐어요")
                fetchFeed()
            }
        }
    }

    fun fetchFeed() {
        feedViewModel.fetchIsUserVerifiedToday()
        feedViewModel.fetchChallengeFeeds()
        feedViewModel.fetchVerifiedMemberCount()
        feedViewModel.fetchIsVerifiedFeedExist()
    }

    private fun showToastAbove(message:String){
        val inflater = LayoutInflater.from(this)
        val customToastView = inflater.inflate(R.layout.toast_tooltip_under, null)

        customToastView.findViewById<TextView>(R.id.textView).text = message

        val toast = Toast(this)
        val yOffset = this.dpToPx(95f)

        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, yOffset)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = customToastView
        toast.show()
    }

    fun onClickImageButton() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frag_layout)
        if (currentFragment is FeedFragment) {
            feedFragment.doTodayCertify()
        }
    }

    fun reportFeed() {
        val intent = Intent(this@FeedActivity, ReportActivity::class.java)
        intent.putExtra("Id",feedViewModel.feedId)
        intent.putExtra("Category", "Feed")
        reportForResult.launch(intent)
    }

    fun reportMember() {
        val intent = Intent(this@FeedActivity, ReportActivity::class.java)
        intent.putExtra("Id",feedViewModel.memberId)
        intent.putExtra("Category", "Member")
        reportForResult.launch(intent)
    }


    fun finishActivity() {
        if (isTaskRoot) { // 최상위 스택에 있으면 홈으로
            val intent = Intent(this, PhotiActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

    override fun onBackPressed() {
        finishActivity()
        super.onBackPressed()
    }
}