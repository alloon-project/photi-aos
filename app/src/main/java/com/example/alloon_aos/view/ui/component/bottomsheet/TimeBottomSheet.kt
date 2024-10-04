package com.example.alloon_aos.view.ui.component.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.BottomsheetTimeBinding
import com.example.alloon_aos.viewmodel.CreateViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

interface TimeBottomSheetInterface {
    fun onClickSelectButton()
}

class TimeBottomSheet (val mContext: Context, val createViewModel: CreateViewModel, val timeBottomSheetInterface: TimeBottomSheetInterface) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetTimeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomsheetTimeBinding.inflate(inflater, container, false)
        val view = binding.root


        binding.selectBtn.setOnClickListener {
            timeBottomSheetInterface.onClickSelectButton()
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val params = window.attributes
            params.dimAmount = 0.4f
            window.attributes = params
        }
    }

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

}