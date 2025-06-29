package com.photi.aos.view.fragment.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.photi.aos.BuildConfig
import com.photi.aos.R
import com.photi.aos.databinding.FragmentMainSettingsBinding
import com.photi.aos.view.activity.SettingsActivity
import com.photi.aos.view.ui.component.dialog.CustomTwoButtonDialog
import com.photi.aos.view.ui.component.dialog.CustomTwoButtonDialogInterface
class MainSettingsFragment : Fragment(), CustomTwoButtonDialogInterface {
    private lateinit var binding: FragmentMainSettingsBinding

    // private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_main_settings, container, false)
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        binding.versionTextView.text =  BuildConfig.VERSION_NAME
        val mActivity = activity as SettingsActivity
        mActivity.setAppBar("설정")
        return binding.root
    }

    fun moveFrag(i: Int) {
        when (i) {
            1 -> {//프로필 수정
                view?.findNavController()
                    ?.navigate(R.id.action_mainSettingsFragment_to_profileModifyFragment)
            }

            2 -> {//문의하기
                view?.findNavController()
                    ?.navigate(R.id.action_mainSettingsFragment_to_inquireFragment)
            }

            3 -> {//서비스 이용약관
                val notionPageUrl = "https://www.notion.so/f1dc17026f884c2ebe90437b0ee9fa63"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(notionPageUrl))
                startActivity(intent)
            }

            4 -> {//개인정보 처리방침
                val notionPageUrl = "https://www.notion.so/16c7071e9b43802fac6beedbac719400"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(notionPageUrl))
                startActivity(intent)
            }

            6 -> {//로그아웃
                logoutAndBack()
            }
        }
        //탈
//view?.findNavController()?.navigate(R.id.action_mainSettings_to_unSubscribeFragment)

    }

    fun logoutAndBack() {
        CustomTwoButtonDialog(this, "정말 로그아웃 하시겠어요?", "", "취소할게요", "로그아웃할게요")
            .show(activity?.supportFragmentManager!!, "tag")
    }

    override fun onClickFisrtButton() {}

    override fun onClickSecondButton() {
        val mActivity = activity as SettingsActivity
        mActivity.logout()
    }

}