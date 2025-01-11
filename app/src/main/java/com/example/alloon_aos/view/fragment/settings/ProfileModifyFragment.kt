package com.example.alloon_aos.view.fragment.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentProfileModifyBinding
import com.example.alloon_aos.view.activity.SettingsActivity
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.SettingsViewModel

class ProfileModifyFragment : Fragment() {
    private lateinit var binding: FragmentProfileModifyBinding
    private lateinit var mContext: Context
    private val settingsViewModel by activityViewModels<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile_modify, container, false)
        binding.fragment = this
        binding.viewModel = settingsViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val mActivity = activity as SettingsActivity
        mActivity.setAppBar("프로필 수정")

        settingsViewModel.fetchUserProfile()
        setObserve()
        return binding.root
    }

    private fun setObserve() {
        // API 응답 코드 관찰
        settingsViewModel.code.observe(viewLifecycleOwner) { code ->
            when (code) {
                "200 OK" -> {
                    val url = settingsViewModel.profileImage.value!!
                    Glide.with(binding.userImgImageView.context)
                        .load(url)
                        .transform(CircleCrop())
                        .into(binding.userImgImageView)
                }

                "USER_NOT_FOUND" -> {
                    Log.e("ChallengeFragment", "Error: USER_NOT_FOUND - 존재하지 않는 회원입니다.")
                }

                "TOKEN_UNAUTHENTICATED" -> {
                    Log.e(
                        "ChallengeFragment",
                        "Error: TOKEN_UNAUTHENTICATED - 승인되지 않은 요청입니다. 다시 로그인 해주세요."
                    )
                }

                "TOKEN_UNAUTHORIZED" -> {
                    Log.e(
                        "ChallengeFragment",
                        "Error: TOKEN_UNAUTHORIZED - 권한이 없는 요청입니다. 로그인 후 다시 시도해주세요."
                    )
                }


                "IO_Exception" -> {
                    CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }

                "UNKNOWN_ERROR" -> {
                    Log.e("ChallengeFragment", "Error: UNKNOWN_ERROR - 알 수 없는 오류가 발생했습니다.")
                }

                else -> {
                    Log.e("ChallengeFragment", "Error: $code - 예기치 않은 오류가 발생했습니다.")
                }
            }
        }
    }

    fun moveFlag(i: Int) {
        when (i) {
            1 -> {
                view?.findNavController()
                    ?.navigate(R.id.action_profileModifyFragment_to_profilePasswordFragment)
            }

            2 -> {
                view?.findNavController()
                    ?.navigate(R.id.action_profileModifyFragment_to_unSubscribeFragment)
            }
        }
    }
}