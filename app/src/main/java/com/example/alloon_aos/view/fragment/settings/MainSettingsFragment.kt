package com.example.alloon_aos.view.fragment.settings

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.repository.TokenManager
import com.example.alloon_aos.databinding.FragmentMainSettingsBinding
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.view.activity.SettingsActivity
import com.example.alloon_aos.view.ui.component.dialog.CustomTwoButtonDialog
import com.example.alloon_aos.view.ui.component.dialog.CustomTwoButtonDialogInterface
import com.example.alloon_aos.view.ui.component.toast.CustomToast

class MainSettingsFragment : Fragment(),CustomTwoButtonDialogInterface{
    private lateinit var binding : FragmentMainSettingsBinding
    // private val authViewModel by activityViewModels<AuthViewModel>()
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_settings, container, false)
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        val mActivity = activity as SettingsActivity
        mActivity.setAppBar("설정")
        return binding.root
    }

    fun moveFrag(i : Int){
        when(i){
            1 -> {//프로필 수정
                view?.findNavController()?.navigate(R.id.action_mainSettingsFragment_to_profileModifyFragment)
            }
            2 -> {//문의하기
                view?.findNavController()?.navigate(R.id.action_mainSettingsFragment_to_inquireFragment)
            }
            3 -> {//서비스 이용약관

            }
            4 ->{//개인정보 처리방침

            }
            6 -> {//로그아웃
                logoutAndBack()
            }
        }
        //탈
//view?.findNavController()?.navigate(R.id.action_mainSettings_to_unSubscribeFragment)

    }

    fun logoutAndBack(){
        CustomTwoButtonDialog(this,"정말 로그아웃 하시겠어요?","","취소할게요","로그아웃할게요")
            .show(activity?.supportFragmentManager!!,"tag")
    }

    override fun onClickFisrtButton() {}

    override fun onClickSecondButton() {
        tokenManager.deleteAccessToken()
        tokenManager.deleteRefreshToken()
        val mActivity = activity as SettingsActivity
        mActivity.logout()
    }

}