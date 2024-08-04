package com.example.alloon_aos.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ActivityPhotiBinding
import com.example.alloon_aos.view.fragment.photi.ChallengeFragment
import com.example.alloon_aos.view.fragment.photi.HomeFragment
import com.example.alloon_aos.view.fragment.photi.MyPageFragment

private const val TAG_CHALLENGE = "challenge_fragment"
private const val TAG_HOME = "home_fragment"
private const val TAG_PROFILE = "profile_fragment"
class PhotiActivity : AppCompatActivity(){

    lateinit var binding : ActivityPhotiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photi)

        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.challengeFragment -> setFragment(TAG_CHALLENGE, ChallengeFragment())
                R.id.profileFragment-> setFragment(TAG_PROFILE, MyPageFragment())
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
}