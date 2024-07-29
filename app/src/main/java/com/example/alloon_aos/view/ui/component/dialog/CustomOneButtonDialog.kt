package com.example.alloon_aos.view.ui.component.dialog

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

class CustomOneButtonDialog(val customDialogInterface: CustomOneButtonDialogInterface, val title: String, val message: String, val buttonText: String) : DialogFragment() {
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
    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val params = window.attributes
            params.dimAmount = 0.4f // 배경의 투명도를 설정 (0.0f부터 1.0f까지)
            window.attributes = params
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}