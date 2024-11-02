package com.example.alloon_aos.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.repository.TokenManager
import com.example.alloon_aos.databinding.ActivityPhotiBinding
import com.example.alloon_aos.view.fragment.photi.ChallengeFragment
import com.example.alloon_aos.view.fragment.photi.HomeFragment
import com.example.alloon_aos.view.fragment.photi.MyPageFragment
import com.example.alloon_aos.view.ui.component.dialog.CustomTwoButtonDialog
import com.example.alloon_aos.view.ui.component.dialog.CustomTwoButtonDialogInterface
import com.example.alloon_aos.view.ui.component.toast.CustomToast

private const val TAG_CHALLENGE = "challenge_fragment"
private const val TAG_HOME = "home_fragment"
private const val TAG_PROFILE = "profile_fragment"
class PhotiActivity : AppCompatActivity(),CustomTwoButtonDialogInterface {
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)
    private var previousSelectedItemId: Int = R.id.homeFragment
    lateinit var binding : ActivityPhotiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photi)

        setFragment(TAG_HOME, HomeFragment())
        setListener()

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
                binding.navigationView.id = R.id.challengeFragment
            }
        }
    }

    private fun setListener(){
        binding.navigationView.setOnItemSelectedListener { item ->
            if (item.itemId != R.id.myPageFragment) {
                previousSelectedItemId = item.itemId
            }
            when(item.itemId) {
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.challengeFragment -> setFragment(TAG_CHALLENGE, ChallengeFragment())
                R.id.myPageFragment-> {
                    if(tokenManager.getAccessToken() == null && tokenManager.getRefreshToken() == null){
                        CustomTwoButtonDialog(this,"로그인하고 다양한 챌린지에\n 참여해보세요!","","나중에 하기","로그인하기")
                            .show(supportFragmentManager, "CustomDialog")
                    }
                    else
                        setFragment(TAG_PROFILE, MyPageFragment())
                }
            }
            true
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
        binding.navigationView.selectedItemId = previousSelectedItemId
    }

    override fun onClickSecondButton() {
        println("로그인하기 페이지로 이동")
    }
}