package com.example.alloon_aos.view.ui.component.bottomsheet

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.BottomsheetRuleBinding
import com.example.alloon_aos.view.ui.util.KeyboardListener
import com.example.alloon_aos.view.ui.util.OnKeyboardVisibilityListener
import com.example.alloon_aos.viewmodel.CreateViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RuleBottomSheet (val mContext: Context, val createViewModel: CreateViewModel) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetRuleBinding? = null
    private val binding get() = _binding!!

    private var rule: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomsheetRuleBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.root.setOnClickListener {
            if (activity != null && requireActivity().currentFocus != null) {
                val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible)    binding.ruleEdittext.background = mContext.getDrawable(R.drawable.input_line_focus)
                else    binding.ruleEdittext.background = mContext.getDrawable(R.drawable.input_line_default)
            }
        })

        binding.ruleEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.numTextview.setText("${s!!.length}/30")
                binding.addBtn.isEnabled = true
                rule = binding.ruleEdittext.text.toString()
            }
        })

        binding.addBtn.setOnClickListener {
            createViewModel.addRule(rule!!)
            dismiss()
        }

        binding.closeBtn.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window?.let { window ->
            val params = window.attributes
            params.dimAmount = 0.4f
            window.attributes = params
        }
    }

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

}