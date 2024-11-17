package com.example.alloon_aos.view.ui.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.alloon_aos.databinding.DialogJoinGuestBinding


interface JoinGuestDialogInterface {
    fun onClickLoginButton()
}

class JoinGuestDialog(val joinGuestDialogInterface: JoinGuestDialogInterface) : DialogFragment() {
    private var _binding: DialogJoinGuestBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogJoinGuestBinding.inflate(inflater, container, false)
        val view = binding.root

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)

        binding.backImgBtn.setOnClickListener {
            dismiss()
        }

        binding.nextBtn.setOnClickListener {
            joinGuestDialogInterface.onClickLoginButton()
            dismiss()
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}