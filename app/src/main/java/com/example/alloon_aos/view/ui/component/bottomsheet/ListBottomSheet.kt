package com.example.alloon_aos.view.ui.component.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.BottomsheetListBinding
import com.example.alloon_aos.databinding.DialogOneBtnBinding
import com.example.alloon_aos.view.ui.component.dialog.CustomOneButtonDialogInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
interface ListBottomSheetInterface {
    fun onClickImgButton1()
    fun onClickImgButton2()
    fun onClickButton()
}
// ModalBottomSheet.kt
class ListBottomSheet(val listBottomSheetInterface: ListBottomSheetInterface, val title: String, val text1: String, val text2: String, val buttonText : String) : BottomSheetDialogFragment()  {
    private var _binding: BottomsheetListBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomsheetListBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.title.text = title
        binding.textView1.text = text1
        binding.textView2.text = text2
        binding.btn.text = buttonText

        binding.btn.setOnClickListener {
            listBottomSheetInterface.onClickButton()
            dismiss()
        }

        binding.imgBtn1.setOnClickListener{
            listBottomSheetInterface.onClickImgButton1()
        }

        binding.imgBtn2.setOnClickListener{
            listBottomSheetInterface.onClickImgButton2()
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