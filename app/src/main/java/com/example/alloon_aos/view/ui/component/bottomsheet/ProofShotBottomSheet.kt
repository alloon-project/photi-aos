package com.example.alloon_aos.view.ui.component.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.BottomsheetListBinding
import com.example.alloon_aos.databinding.BottomsheetProofshotBinding
import com.example.alloon_aos.databinding.DialogOneBtnBinding
import com.example.alloon_aos.view.ui.component.dialog.CustomOneButtonDialogInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
interface ProofShotBottomSheetInterface {
    fun onClickPhotoButton()
    fun onClickGifButton()

}

class ProofShotBottomSheet(val proofShotBottomSheetInterface: ProofShotBottomSheetInterface) : BottomSheetDialogFragment()  {
    private var _binding: BottomsheetProofshotBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomsheetProofshotBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.exitButton.setOnClickListener {
            dismiss()
        }

        binding.photoButton.setOnClickListener {
            proofShotBottomSheetInterface.onClickPhotoButton()
            dismiss()
        }

        binding.gifButton.setOnClickListener{
            proofShotBottomSheetInterface.onClickGifButton()
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