package com.example.alloon_aos.view.fragment.report

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentReportMemberBinding
import com.example.alloon_aos.view.CustomToast
import com.example.alloon_aos.view.KeyboardListener
import com.example.alloon_aos.view.OnKeyboardVisibilityListener
import com.example.alloon_aos.view.activity.ReportActivity

class ReportMemberFragment : Fragment() {

    private lateinit var binding : FragmentReportMemberBinding
    private lateinit var radioTag: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_member, container, false)
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        val mActivity = activity as ReportActivity
        mActivity.setAppBar(" ")

        setListener()
        return binding.root
    }
    fun setListener(){
        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible)    binding.reportEdittext.background = resources.getDrawable(R.drawable.textarea_line_focus)
                else    binding.reportEdittext.background = resources.getDrawable(R.drawable.textarea_line_default)
            }
        })

        radioTag = binding.reportRadiogroup.getCheckedRadioButton()?.text.toString()

        if (radioTag == null) {
            binding.contentLayout.visibility = View.GONE
        } else {
            binding.contentLayout.visibility = View.VISIBLE
        }

        binding.reportEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.numTextview.setText("${s!!.length}/120")

                if (radioTag.equals(binding.writeRadio) && s.isEmpty()) binding.nextBtn.isEnabled = false
                else binding.nextBtn.isEnabled = true
            }
        })
    }

    fun click(){
        view?.findNavController()?.navigate(R.id.action_reportMemberFragment_to_reportPageFragment)
        CustomToast.createToast(getActivity(),"신고가 완료됐어요. 꼼꼼히 확인하고,\n" +
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
}