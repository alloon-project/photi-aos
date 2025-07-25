package com.photi.aos.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.photi.aos.MyApplication
import com.photi.aos.R
import com.photi.aos.data.storage.TokenManager
import com.photi.aos.databinding.ActivityPhotiBinding
import com.photi.aos.view.fragment.photi.ChallengeFragment
import com.photi.aos.view.fragment.photi.HomeFragment
import com.photi.aos.view.fragment.photi.MyPageFragment
import com.photi.aos.view.ui.component.dialog.CustomTwoButtonDialog
import com.photi.aos.view.ui.component.dialog.CustomTwoButtonDialogInterface
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.viewmodel.PhotiViewModel

private const val TAG_CHALLENGE = "challenge_fragment"
private const val TAG_HOME = "home_fragment"
private const val TAG_PROFILE = "profile_fragment"

class PhotiActivity : AppCompatActivity(),CustomTwoButtonDialogInterface {
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)
    lateinit var binding : ActivityPhotiBinding
    private lateinit var mContext: Context
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private val photiViewModel : PhotiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photi)
        mContext = this

        setBottomNavigation(TAG_HOME)
        setListener()
        setObserver()
        //setDeepLink()

        val isFrom = intent.getStringExtra("IS_FROM")
        when(isFrom){
            "LOGOUT" -> {
                CustomToast.createToast(this, "로그아웃이 완료됐어요")?.show()
            }
            "UNSUBSCRIBE" -> {
                CustomToast.createToast(this, "탈퇴가 완료됐어요. 다음에 또 만나요!")?.show()
            }
            "LEAVE" -> {
                CustomToast.createToast(this, "챌린지 탈퇴가 완료됐어요.")?.show()
            }
        }

        photiViewModel.resetAllResponseValue()

//        val challengeId = intent.getStringExtra("challengeId")
//        if (!challengeId.isNullOrEmpty()) {
//            photiViewModel.id = challengeId.toInt()
//            photiViewModel.getChallenge()
//        }

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getBooleanExtra("IS_FROM_LOGIN",false)
                if(data == true) {
                    val id = result.data?.getStringExtra("id")
                    CustomToast.createToast(this, "${id}님 환영합니다!")?.show()
                }
            }
        }
    }

    private fun setDeepLink() {
        val intent = intent
        val action = intent.action
        val data: Uri? = intent.data

        if (Intent.ACTION_VIEW == action && data != null) {
            val inviteCode = data.getQueryParameter("inviteCode")
            if (inviteCode != null) {
                val intent = Intent(this, ChallengeActivity::class.java)
                intent.putExtra("IS_FROM_HOME",true)
                intent.putExtra("code", inviteCode)
                startActivity(intent)
            }
        }
    }

    private fun setListener(){
        binding.homeIcon.setOnClickListener {
            setBottomNavigation(TAG_HOME)
        }

        binding.challengeIcon.setOnClickListener {
            setBottomNavigation(TAG_CHALLENGE)
        }

        binding.mypageIcon.setOnClickListener {
            if(tokenManager.hasNoTokens())
                CustomTwoButtonDialog(this,"로그인하고 다양한 챌린지에\n 참여해보세요!","","나중에 하기","로그인하기")
                    .show(supportFragmentManager, "CustomDialog")
            else
                setBottomNavigation(TAG_PROFILE)
        }
    }

    private fun setObserver() {
        photiViewModel.apiResponse.observe(this) { response ->
            when (response.code) {
                "200 OK" -> {
                    startChallengeActivity()
                }
                "TOKEN_UNAUTHENTICATED" -> {
                    CustomToast.createToast(this@PhotiActivity, "승인되지 않은 요청입니다. 다시 로그인 해주세요.")?.show()
                }
                "TOKEN_UNAUTHORIZED" -> {
                    CustomToast.createToast(this@PhotiActivity, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요.")?.show()
                }
                "CHALLENGE_NOT_FOUND" -> {
                    CustomToast.createToast(this@PhotiActivity, "존재하지 않는 챌린지입니다.", "circle")?.show()
                }
                "IO_Exception" -> {
                    CustomToast.createToast(this@PhotiActivity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }
                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                }
            }
        }
    }

    private fun setBottomNavigation(tag: String) {
        when(tag) {
            TAG_HOME -> {
                setFragment(tag, HomeFragment())
                binding.homeImageview.setImageResource(R.drawable.ic_home_blue)
                binding.homeTextview.setTextColor(mContext.getColor(R.color.blue500))
                binding.challengeImageview.setImageResource(R.drawable.ic_postit)
                binding.challengeTextview.setTextColor(mContext.getColor(R.color.gray400))
                binding.mypageImageview.setImageResource(R.drawable.ic_user_gray)
                binding.mypageTextview.setTextColor(mContext.getColor(R.color.gray400))
            }
            TAG_CHALLENGE -> {
                setFragment(tag, ChallengeFragment())
                binding.homeImageview.setImageResource(R.drawable.ic_home)
                binding.homeTextview.setTextColor(mContext.getColor(R.color.gray400))
                binding.challengeImageview.setImageResource(R.drawable.ic_postit_blue)
                binding.challengeTextview.setTextColor(mContext.getColor(R.color.blue500))
                binding.mypageImageview.setImageResource(R.drawable.ic_user_gray)
                binding.mypageTextview.setTextColor(mContext.getColor(R.color.gray400))
            }
            TAG_PROFILE -> {
                setFragment(tag, MyPageFragment())
                binding.homeImageview.setImageResource(R.drawable.ic_home)
                binding.homeTextview.setTextColor(mContext.getColor(R.color.gray400))
                binding.challengeImageview.setImageResource(R.drawable.ic_postit)
                binding.challengeTextview.setTextColor(mContext.getColor(R.color.gray400))
                binding.mypageImageview.setImageResource(R.drawable.ic_user_blue)
                binding.mypageTextview.setTextColor(mContext.getColor(R.color.blue500))
            }
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.frameLayout, fragment, tag)
        }

        val home = manager.findFragmentByTag(TAG_HOME)
        val challenge = manager.findFragmentByTag(TAG_CHALLENGE)
        val profile = manager.findFragmentByTag(TAG_PROFILE)

        if (home != null){
            fragTransaction.hide(home)
        }

        if (challenge != null){
            fragTransaction.hide(challenge)
        }

        if (profile != null) {
            fragTransaction.hide(profile)
        }

        if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        }
        else if (tag == TAG_CHALLENGE) {
            if (challenge!=null){
                fragTransaction.show(challenge)
            }
        }
        else if (tag == TAG_PROFILE){
            if (profile != null){
                fragTransaction.show(profile)
            }
        }

        fragTransaction.commitAllowingStateLoss()
    }


    override fun onClickFisrtButton() {
    }

    override fun onClickSecondButton() {
        startAuthActivity()
    }


    fun startAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startForResult.launch(intent)
    }
    fun startChallengeActivity() {
        val intent = Intent(this, ChallengeActivity::class.java)
        intent.putExtra("IS_FROM_HOME",true)
        intent.putExtra("ID", photiViewModel.id)
        intent.putExtra("data", photiViewModel.getData())
        intent.putExtra("image", photiViewModel.imgFile)
        startActivity(intent)
    }
    fun startFeedActivity() {
        val intent = Intent(this, FeedActivity::class.java)
        intent.putExtra("CHALLENGE_ID", photiViewModel.id)
        startActivity(intent)
    }

}