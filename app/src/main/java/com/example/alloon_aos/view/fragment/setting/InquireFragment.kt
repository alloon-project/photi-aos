package com.example.alloon_aos.view.fragment.setting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.children
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentInquireBinding
import com.example.alloon_aos.view.CustomToast
import com.example.alloon_aos.view.KeyboardListener
import com.example.alloon_aos.view.OnKeyboardVisibilityListener
import com.example.alloon_aos.view.activity.SettingActivity


class InquireFragment : Fragment() {

    private lateinit var binding : FragmentInquireBinding
   // private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var radioTag: String

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
        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible)    binding.contentsEditText.background = resources.getDrawable(R.drawable.input_line_focus)
                else    binding.contentsEditText.background = resources.getDrawable(R.drawable.input_line_default)

                binding.contentsEditText.updatePadding(convertDPtoPX(18),convertDPtoPX(18),convertDPtoPX(18),convertDPtoPX(18))
            }
        })

        binding.contentsEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.countTextView.setText("${s!!.length}/120")
                    if(s.isEmpty())
                        binding.nextButton.isEnabled = false
                    else{
                         radioTag = binding.selectRadiogroup.getCheckedRadioButton()?.text.toString()
                            //tag ?: "태그"
                        if(tag != null) binding.nextButton.isEnabled = true
                    }

            }
        })
    }


    fun Click(){
        view?.findNavController()?.navigate(R.id.action_inquireFragment_to_myInfoFragment)
        println("$radioTag: " + binding.contentsEditText.text)
        CustomToast.createToast(getActivity(),"접수가 완료됐어요. 꼼꼼히 확인하고,\n" +
                "회원님의 이메일로 답변을 보내드릴게요.")?.show()
    }


    fun RadioGroup.getCheckedRadioButton(): RadioButton? {
        var checkedRadioButton: RadioButton? = null
        this.children.forEach {
            if((it as RadioButton).isChecked)
                checkedRadioButton = it
        }
        return checkedRadioButton
    }

    fun convertDPtoPX(dp: Int): Int {
        val density = context?.resources?.displayMetrics?.density
        return Math.round(dp.toFloat() * density!!)
    }
}