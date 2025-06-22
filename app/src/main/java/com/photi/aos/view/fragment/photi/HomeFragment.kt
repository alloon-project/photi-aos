package com.photi.aos.view.fragment.photi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.photi.aos.MyApplication
import com.photi.aos.R
import com.photi.aos.data.storage.TokenManager
import com.photi.aos.databinding.FragmentHomeBinding
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.viewmodel.PhotiViewModel

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
            photiViewModel.fetchChallengeCount()
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

        photiViewModel.code.observe(viewLifecycleOwner) { code ->
            handleApiError(code)
        }
    }

    private fun handleApiError(code: String) {
        val errorMessages = mapOf(
            "USER_NOT_FOUND" to "존재하지 않는 회원입니다.",
            "TOKEN_UNAUTHENTICATED" to "승인되지 않은 요청입니다. 다시 로그인 해주세요.",
            "TOKEN_UNAUTHORIZED" to "권한이 없는 요청입니다. 로그인 후 다시 시도해주세요.",
            "UNKNOWN_ERROR" to "알 수 없는 오류가 발생했습니다."
        )

        if (code == "200 OK")   return

        if (code == "IO_Exception") {
            CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
        } else {
            val message = errorMessages[code] ?: "예기치 않은 오류가 발생했습니다. ($code)"
            Log.e("HomeFragment", "Error: $message")
        }
    }
}