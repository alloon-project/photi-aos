package com.photi.aos.view.fragment.photi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.photi.aos.MyApplication
import com.photi.aos.R
import com.photi.aos.data.storage.TokenManager
import com.photi.aos.databinding.FragmentChallengeBinding
import com.photi.aos.view.activity.CreateActivity
import com.photi.aos.view.activity.PhotiActivity
import com.photi.aos.view.activity.SearchActivity
import com.photi.aos.view.ui.component.dialog.JoinGuestDialog
import com.photi.aos.view.ui.component.dialog.JoinGuestDialogInterface
import com.google.android.material.tabs.TabLayout

class ChallengeFragment : Fragment(), JoinGuestDialogInterface {
    private lateinit var binding : FragmentChallengeBinding
    private lateinit var mActivity: PhotiActivity
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)

    lateinit var commendTab: ChallengeCommendFragment
    lateinit var latestTab: ChallengeLatestFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_challenge, container, false)
        binding.fragment = this

        commendTab = ChallengeCommendFragment()
        latestTab = ChallengeLatestFragment()

        mActivity = activity as PhotiActivity
        mActivity.supportFragmentManager.beginTransaction().add(R.id.frame_layout, commendTab).commit()

        setListener()


        return binding.root
    }

    private fun setListener() {

        binding.searchEdittext.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                val fragmentManager = mActivity.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                if (commendTab.isAdded) {
                    fragmentTransaction.remove(commendTab)
                    commendTab = ChallengeCommendFragment()
                }
                if (latestTab.isAdded) {
                    fragmentTransaction.remove(latestTab)
                    commendTab = ChallengeCommendFragment()
                }
                when(p0?.position) {
                    0 -> {
                        fragmentTransaction.add(R.id.frame_layout, commendTab).commit()
                    }
                    1 -> {
                        fragmentTransaction.add(R.id.frame_layout, latestTab).commit()
                    }
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

        })

        binding.createBtn.setOnClickListener {
            if (tokenManager.hasNoTokens()) {
                JoinGuestDialog(this)
                    .show(activity?.supportFragmentManager!!, "CustomDialog")
            } else {
                val intent = Intent(requireContext(), CreateActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onClickLoginButton() {
        mActivity.startAuthActivity()
    }
}