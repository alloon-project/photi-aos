package com.example.alloon_aos.view.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.DialogOneBtnBinding

interface CustomDialogInterface {
    fun onClickYesButton()
}

class CustomDialog(val customDialogInterface: CustomDialogInterface,val title: String, val message: String, val buttonText: String) : DialogFragment() {
    private var _binding: DialogOneBtnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogOneBtnBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.titleTextView.text = title
        binding.messageTextView.text = message
        binding.dialogBtn.text = buttonText

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(false)

        // 레이아웃 배경을 투명하게 해줌
        //dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.dialogBtn.setOnClickListener {
           customDialogInterface.onClickYesButton()
            dismiss()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun getTheme(): Int = R.style.OnBoardingDialog
}