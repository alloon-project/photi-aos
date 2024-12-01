package com.example.alloon_aos.view.fragment.photi

import android.content.Intent
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
import com.example.alloon_aos.databinding.FragmentHomeGuestBinding
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.view.ui.component.dialog.CustomTwoButtonDialog
import com.example.alloon_aos.view.ui.component.dialog.CustomTwoButtonDialogInterface
import com.example.alloon_aos.viewmodel.PhotiViewModel

class HomeGuestFragment : Fragment(), CustomTwoButtonDialogInterface {
    private lateinit var binding : FragmentHomeGuestBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_guest, container, false)
        binding.fragment = this
        binding.viewModel = photiViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if (MyApplication.isTokenExpired) {
            CustomTwoButtonDialog(this,"재로그인이 필요해요","보안을 위해 자동 로그아웃 됐어요.\n" +
                    "다시 로그인해주세요.","나중에 할게요","로그인하기")
                .show(activity?.supportFragmentManager!!,"CustomDialog")
            MyApplication.isTokenExpired = false
        }

        return binding.root
    }

    fun redirectToLogin() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
    }

    override fun onClickFisrtButton() {}

    override fun onClickSecondButton() {
        redirectToLogin()
    }

}