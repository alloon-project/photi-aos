package com.example.alloon_aos.view.fragment.photi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentChallengeBinding
import com.example.alloon_aos.view.activity.PhotiActivity
import com.google.android.material.tabs.TabLayout

class ChallengeFragment : Fragment() {
    private lateinit var binding : FragmentChallengeBinding
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

        val mActivity = activity as PhotiActivity
        mActivity.supportFragmentManager.beginTransaction().add(R.id.frame_layout, commendTab).commit()

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

        return binding.root
    }
}