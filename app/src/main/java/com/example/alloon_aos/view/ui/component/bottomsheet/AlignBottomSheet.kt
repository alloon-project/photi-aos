package com.example.alloon_aos.view.ui.component.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.BottomsheetAlignBinding
import com.example.alloon_aos.databinding.BottomsheetListBinding
import com.example.alloon_aos.databinding.DialogOneBtnBinding
import com.example.alloon_aos.view.ui.component.dialog.CustomOneButtonDialogInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
interface AlignBottomSheetInterface {
    fun onClickFirstButton()
    fun onClickSecondButton()
}

class AlignBottomSheet(val mContext: Context, val alignBottomSheetInterface: AlignBottomSheetInterface, val first_text: String, val second_text: String, val selected_order : String ) : BottomSheetDialogFragment()  {
    private var _binding: BottomsheetAlignBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomsheetAlignBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.firstBtn.text = first_text
        binding.secondBtn.text = second_text

        when(selected_order){
            "first" -> {
                binding.firstBtn.setTextColor(mContext.getColor(R.color.blue500))
            }
            "second" -> {
                binding.secondBtn.setTextColor(mContext.getColor(R.color.blue500))
            }
        }

        binding.firstBtn.setOnClickListener {
            alignBottomSheetInterface.onClickFirstButton()
            dismiss()
        }

        binding.secondBtn.setOnClickListener{
            alignBottomSheetInterface.onClickSecondButton()
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