package com.example.alloon_aos.view.fragment.settings

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.data.enum.InquiryType
import com.example.alloon_aos.databinding.FragmentInquireBinding
import com.example.alloon_aos.view.activity.SettingsActivity
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.KeyboardListener
import com.example.alloon_aos.view.ui.util.OnKeyboardVisibilityListener
import com.example.alloon_aos.viewmodel.SettingsViewModel


class InquireFragment : Fragment() {

    private lateinit var binding: FragmentInquireBinding
    private lateinit var mContext: Context
    private val settingsViewModel by activityViewModels<SettingsViewModel>()
    private var selectedInquiryType: InquiryType? = null
    private var title = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inquire, container, false)
        binding.fragment = this
        binding.viewModel = settingsViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val mActivity = activity as SettingsActivity
        mActivity.setAppBar(" ")

        // settingsViewModel.resetCode()

        setListener()
        setObserver()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun setListener() {
        KeyboardListener.setKeyboardVisibilityListener(binding.root, object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible) binding.contentsEditText.background =
                    mContext.getDrawable(R.drawable.textarea_line_focus)
                else binding.contentsEditText.background =
                    mContext.getDrawable(R.drawable.textarea_line_default)
            }
        })

        binding.contentsEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.countTextView.text = "${s!!.length}/120"

                val checkedId = binding.selectRadiogroup.checkedRadioButtonId
                val selectedRadioButton =
                    if (checkedId != -1) binding.root.findViewById<RadioButton>(checkedId) else null

                selectedInquiryType = selectedRadioButton?.tag?.toString()?.let {
                    InquiryType.valueOf(it)
                }

                binding.nextButton.isEnabled = s.isNotEmpty() && selectedInquiryType != null

            }
        })
    }

    fun setObserver() {
        settingsViewModel.code.observe(viewLifecycleOwner) { code ->
            when (code) {
                "201 CREATED" -> {
                    view?.findNavController()
                        ?.navigate(R.id.action_inquireFragment_to_mainSettingsFragment)
                    CustomToast.createToast(
                        activity, "접수가 완료됐어요. 꼼꼼히 확인 후, " +
                                "회원님의 이메일로 답변을 보내드릴게요."
                    )?.show()
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

    fun Click() {
        if (selectedInquiryType == null) {
            print("라디오버튼클린된거임?")
            return
        }
        settingsViewModel.sendInquiries(selectedInquiryType!!)
    }
}