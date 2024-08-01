package com.example.alloon_aos.view.ui.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.alloon_aos.databinding.DialogTwoBtnBinding

interface CustomTwoButtonDialogInterface {
    fun onClickFisrtButton()
    fun onClickSecondButton()
}

class CustomTwoButtonDialog(val customDialogInterface: CustomTwoButtonDialogInterface, val title: String, val message: String, val buttonText1: String, val buttonText2: String) : DialogFragment() {
    private var _binding: DialogTwoBtnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogTwoBtnBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.titleTextView.text = title
        binding.messageTextView.text = message
        binding.dialogBtn1.text = buttonText1
        binding.dialogBtn1.text = buttonText2

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)


        binding.dialogBtn1.setOnClickListener {
            customDialogInterface.onClickFisrtButton()
            dismiss()
        }
        binding.dialogBtn2.setOnClickListener {
            customDialogInterface.onClickSecondButton()
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val density = displayMetrics.density

            // 전체 화면의 너비
            val fullWidth = displayMetrics.widthPixels

            // 양옆 32dp를 제외한 너비
            val marginHorizontal = (32 * density).toInt()
            val width = fullWidth - 2 * marginHorizontal

            // 다이얼로그의 레이아웃 속성 설정
            val params = window.attributes
            params.dimAmount = 0.4f // 배경의 투명도 설정 (0.0f부터 1.0f까지)
            window.attributes = params

            // 다이얼로그의 크기 설정
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}