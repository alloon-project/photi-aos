package com.photi.aos.view.fragment.photi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.photi.aos.MyApplication
import com.photi.aos.R
import com.photi.aos.databinding.FragmentHomeGuestBinding
import com.photi.aos.view.activity.PhotiActivity
import com.photi.aos.view.ui.component.dialog.CustomTwoButtonDialog
import com.photi.aos.view.ui.component.dialog.CustomTwoButtonDialogInterface
import com.photi.aos.viewmodel.PhotiViewModel

class HomeGuestFragment : Fragment(), CustomTwoButtonDialogInterface {
    private lateinit var binding : FragmentHomeGuestBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private lateinit var mActivity: PhotiActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_guest, container, false)
        binding.fragment = this
        binding.viewModel = photiViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mActivity = activity as PhotiActivity

        if (MyApplication.isTokenExpired) {
            CustomTwoButtonDialog(this,"재로그인이 필요해요","보안을 위해 자동 로그아웃 됐어요.\n" +
                    "다시 로그인해주세요.","나중에 할게요","로그인하기")
                .show(activity?.supportFragmentManager!!,"CustomDialog")
            MyApplication.isTokenExpired = false
        }

        return binding.root
    }

    fun redirectToLogin() {
        mActivity.startAuthActivity()
    }

    override fun onClickFisrtButton() {}

    override fun onClickSecondButton() {
        redirectToLogin()
    }

}