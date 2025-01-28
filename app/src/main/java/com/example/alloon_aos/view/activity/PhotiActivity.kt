package com.example.alloon_aos.view.activity

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
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.storage.TokenManager
import com.example.alloon_aos.databinding.ActivityPhotiBinding
import com.example.alloon_aos.view.fragment.photi.ChallengeFragment
import com.example.alloon_aos.view.fragment.photi.HomeFragment
import com.example.alloon_aos.view.fragment.photi.MyPageFragment
import com.example.alloon_aos.view.ui.component.dialog.CustomTwoButtonDialog
import com.example.alloon_aos.view.ui.component.dialog.CustomTwoButtonDialogInterface
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.PhotiViewModel

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
        setDeepLink()
        setObserver()

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
                setBottomNavigation(TAG_PROFILE)
            }
            "LOGIN" -> {
                CustomToast.createToast(this, "photi님 환영합니다!")?.show()
            }
        }

        photiViewModel.resetAllResponseValue()
        photiViewModel.getChallengePopular()
        photiViewModel.getChallengeLatest()
        //photiViewModel.getChallengeHashtag()
        //photiViewModel.getHashList()

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getBooleanExtra("IS_FROM_LOGIN",false)
                if(data == true)
                    setBottomNavigation(TAG_PROFILE)
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

    private fun setObserver() {
        photiViewModel.popularResponse.observe(this) { response ->
            when (response.code) {
                "200 OK" -> {
                }
                "IO_Exception" -> {
                    CustomToast.createToast(this@PhotiActivity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }
                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                }
            }
        }

        photiViewModel.hashListResponse.observe(this) { response ->
            when (response.code) {
                "200 OK" -> {
                    photiViewModel.getChallengeHashtag()
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
        val intent = Intent(this, AuthActivity::class.java)
        startForResult.launch(intent)
    }
}