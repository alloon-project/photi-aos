package com.example.alloon_aos.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.alloon_aos.databinding.DialogOneBtnBinding

interface CustomOneButtonDialogInterface {
    fun onClickYesButton()
}

class CustomDialog(val customDialogInterface: CustomOneButtonDialogInterface, val title: String, val message: String, val buttonText: String) : DialogFragment() {
    private var _binding: DialogOneBtnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogOneBtnBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.titleTextView.text = title
        binding.messageTextView.text = message
        binding.dialogBtn.text = buttonText

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)

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
}