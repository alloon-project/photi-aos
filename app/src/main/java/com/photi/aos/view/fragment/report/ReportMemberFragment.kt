package com.photi.aos.view.fragment.report

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.photi.aos.R
import com.photi.aos.data.enum.ReasonType
import com.photi.aos.databinding.FragmentReportMemberBinding
import com.photi.aos.view.ui.util.KeyboardListener
import com.photi.aos.view.ui.util.OnKeyboardVisibilityListener
import com.photi.aos.viewmodel.ReportViewModel

class ReportMemberFragment : Fragment() {
    private lateinit var binding : FragmentReportMemberBinding
    private lateinit var mContext : Context
    private lateinit var radioTag: String
    private val reportViewModel by activityViewModels<ReportViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_member, container, false)
        binding.fragment = this
        binding.viewModel = reportViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setListener()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    
    fun setListener(){
        binding.root.setOnClickListener {
            if (activity != null && requireActivity().currentFocus != null) {
                val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible)    binding.reportEdittext.background = mContext.getDrawable(R.drawable.textarea_line_focus)
                else    binding.reportEdittext.background = mContext.getDrawable(R.drawable.textarea_line_default)
            }
        })

        binding.reportRadiogroup.setOnCheckedChangeListener { radioGroup, i ->
            when(i) {
                null -> binding.contentsLayout.visibility = View.INVISIBLE
                binding.writeRadio.id -> {
                    reportViewModel.reason = ReasonType.ETC
                    binding.contentsLayout.visibility = View.VISIBLE
                    if(binding.reportEdittext.text.isNotEmpty())
                        binding.nextBtn.isEnabled = true
                    else
                        binding.nextBtn.isEnabled = false
                }
                else -> {
                    val selectedRadioButton =
                        if (i != -1) binding.root.findViewById<RadioButton>(i) else null
                    reportViewModel.reason = selectedRadioButton?.tag.toString().let {
                        ReasonType.valueOf(it)
                    }
                    binding.contentsLayout.visibility = View.VISIBLE
                    binding.nextBtn.isEnabled = true
                }
            }
        }

        binding.reportEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.numTextview.setText("${s!!.length}/120")
                radioTag = binding.reportRadiogroup.getCheckedRadioButton()?.text.toString()

                if (radioTag.equals(binding.writeRadio.text) && s.isEmpty()) binding.nextBtn.isEnabled = false
                else binding.nextBtn.isEnabled = true
            }
        })
    }

    fun click(){
        reportViewModel.sendReport()
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