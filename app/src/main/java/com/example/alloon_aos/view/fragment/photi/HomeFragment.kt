package com.example.alloon_aos.view.fragment.photi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.storage.TokenManager
import com.example.alloon_aos.databinding.FragmentHomeBinding
import com.example.alloon_aos.view.activity.PhotiActivity
import com.example.alloon_aos.viewmodel.PhotiViewModel

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)

    lateinit var guestHome: HomeGuestFragment
    lateinit var noChallengeHome : HomeNoChallengeFragment
    lateinit var challengHome : HomeChallengeFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.fragment = this
        binding.viewModel = photiViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        guestHome = HomeGuestFragment()
        noChallengeHome = HomeNoChallengeFragment()
        challengHome = HomeChallengeFragment()

        setObserve()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        
        if (tokenManager.hasNoTokens()) {
            childFragmentManager.beginTransaction().replace(R.id.home_frameLayout, guestHome).commit()
        } else {
           // photiViewModel.fetchChallengeCount()
        }
    }

    private fun setObserve() {
        photiViewModel.challengeCount.observe(viewLifecycleOwner) { data ->
            if(data != null) {
                if (data.challengeCnt > 0)
                    childFragmentManager.beginTransaction().replace(R.id.home_frameLayout, challengHome).commit()
                else
                    childFragmentManager.beginTransaction().replace(R.id.home_frameLayout, noChallengeHome).commit()
            }
        }
    }
}