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
    fun onClickSelectTimeButton(time: Int)
}

class TimeBottomSheet (val mContext: Context, val createViewModel: CreateViewModel, val timeBottomSheetInterface: TimeBottomSheetInterface) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetTimeBinding? = null
    private val binding get() = _binding!!

    private var time = 13

    private val timeArr = arrayListOf("01 : 00", "02 : 00", "03 : 00", "04 : 00", "05 : 00", "06 : 00", "07 : 00",
        "08 : 00", "09 : 00", "10 : 00", "11 : 00", "12 : 00", "13 : 00", "14 : 00", "15 : 00", "16 : 00", "17 : 00",
        "18 : 00", "19 : 00", "20 : 00", "21 : 00", "22 : 00", "23 : 00", "24 : 00")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomsheetTimeBinding.inflate(inflater, container, false)
        val view = binding.root

        setTimePicker()

        binding.selectBtn.setOnClickListener {
            timeBottomSheetInterface.onClickSelectTimeButton(time)
            dismiss()
        }

        return view
    }

    private fun setTimePicker() {
        binding.timePicker.let {
            it.minValue = 1
            it.maxValue = 24
            it.value = time
            it.wrapSelectorWheel = false
            it.displayedValues = timeArr.toTypedArray()
            it.setOnValueChangedListener { picker, oldVal, newVal ->
                time = binding.timePicker.value
            }
        }
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