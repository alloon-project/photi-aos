package com.example.alloon_aos.view.fragment.setting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentInquireBinding
import com.example.alloon_aos.view.CustomToast
import com.example.alloon_aos.view.activity.SettingActivity

class InquireFragment : Fragment() {

    private lateinit var binding : FragmentInquireBinding
   // private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inquire, container, false)
        binding.fragment = this
       // binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val mActivity = activity as SettingActivity
        mActivity.setAppBar(" ")

        setListener()
        return binding.root
    }


    fun setListener(){
        binding.contentsEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.countTextView.setText("${s!!.length}/120")
                    if(s.isEmpty())
                        binding.nextButton.isEnabled = false
                    else
                        binding.nextButton.isEnabled = true
            }
        })
    }

    fun Click(){
        view?.findNavController()?.navigate(R.id.action_inquireFragment_to_myInfoFragment)
        println("문의사항: " + binding.contentsEditText.text)
        CustomToast.createToast(getActivity(),"접수가 완료됐어요. 꼼꼼히 확인하고,\n" +
                "회원님의 이메일로 답변을 보내드릴게요.")?.show()
    }

}