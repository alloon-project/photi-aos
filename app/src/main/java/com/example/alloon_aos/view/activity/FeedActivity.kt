package com.example.alloon_aos.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alloon_aos.R
import com.example.alloon_aos.data.model.MyData
import com.example.alloon_aos.databinding.ActivityFeedBinding
import com.example.alloon_aos.databinding.CustomPopupMenuBinding
import com.example.alloon_aos.view.fragment.feed.IntroduceFragment
import com.example.alloon_aos.view.fragment.feed.PartyMemberFragment
import com.example.alloon_aos.view.fragment.feed.FeedFragment
import com.example.alloon_aos.view.ui.component.dialog.CustomTwoButtonDialog
import com.example.alloon_aos.view.ui.component.dialog.CustomTwoButtonDialogInterface
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.FeedViewModel
import com.google.android.material.tabs.TabLayout

class FeedActivity : AppCompatActivity(), CustomTwoButtonDialogInterface {
    lateinit var binding : ActivityFeedBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private val feedViewModel : FeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_feed)
        binding.activity = this

        if (savedInstanceState == null) {
            val initialFragment = FeedFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_layout, initialFragment)
                .commit()
        }

        val challengeId = intent.getIntExtra("ID",-1)
        val challengeData = intent.getParcelableExtra<MyData>("data")
        val imageFile = intent.getStringExtra("image")

        challengeId?.let {
            feedViewModel.challengeId = it
        }
        challengeData?.let {
            feedViewModel.setChallengeData(it)
        }
        imageFile?.let {
            feedViewModel.imgFile = it
        }

        binding.shareImgBtn.setOnClickListener {
            val inviteCode = "HSIEJ23Q"
            val appLink = "https://photi.com/challenge/$inviteCode"
            val chooserTitle = "소설 필사하기"
            val message = "[Photi] ‘$chooserTitle' 챌린지에 함께 참여해 보세요!\n* 초대코드 : $inviteCode \n\n$appLink"

            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_TEXT, message)

            val chooser = Intent.createChooser(sendIntent, chooserTitle)
            startActivity(chooser)
        }

        binding.ellipsisImgBtn.setOnClickListener { view ->
            setCustomPopUp(view)
        }

        val tabLayout = binding.tabLayout

        var viewFeedTab = FeedFragment()
        val introduceTab = IntroduceFragment()
        val partyTab = PartyMemberFragment()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()

                when(tab?.position){
                    0 ->{
                        fragmentTransaction.replace(R.id.frag_layout,viewFeedTab).commit()
                    }
                    1 -> {
                        fragmentTransaction.replace(R.id.frag_layout,introduceTab ).commit()
                    }
                    2 -> {
                        fragmentTransaction.replace(R.id.frag_layout,partyTab).commit()
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
                    CustomToast.createToast(this,"챌린지 수정이 완료됐어요.")?.show()
                }
            }
        }
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
            //if(파티장)
                optionOne.text = "챌린지 수정하기"
            //else(파티원
                //optionOne.text = "챌린지 신고하기"
                optionTwo.text = "챌린지 탈퇴하기"

            optionOne.setOnClickListener {
                //if(파티장)
                    //챌린지 신고하기 플로우
                val intent = Intent(this@FeedActivity, ChallengeActivity::class.java)
                intent.putExtra("IS_FROM_FEED",true)
                intent.putExtra("ID", feedViewModel.challengeId)
                intent.putExtra("data", feedViewModel.getData())
                intent.putExtra("image", feedViewModel.imgFile)
                activityResultLauncher.launch(intent)
                popupWindow.dismiss()
            }

            optionTwo.setOnClickListener {
                //if(마지막 파티원인 경우)
                    //챌린지 삭제된다는 다이얼로그 띄움
                //else
                    CustomTwoButtonDialog(this@FeedActivity,"챌린지를 탈퇴할까요?","탈퇴해도 기록은 남아있어요.\n" +
                            "탈퇴하시기 전에 직접 지워주세요.","취소할게요","탈퇴할게요")
                        .show(binding.activity?.supportFragmentManager!!,"CustomDialog")
                popupWindow.dismiss()
            }

        }

        popupWindow.showAsDropDown(view, 0, 0, Gravity.CENTER)
    }

    override fun onClickFisrtButton() {}

    override fun onClickSecondButton() {
        //챌린지 탈퇴
        finish()
        val intent = Intent(this, PhotiActivity::class.java).apply {
            putExtra("IS_FROM","UNSUBSCRIBE")
        }
        startActivity(intent)
    }

    fun finishActivity(){
        if (isTaskRoot) { // 최상위 스택에 있으면 홈으로
            val intent = Intent(this, PhotiActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

}