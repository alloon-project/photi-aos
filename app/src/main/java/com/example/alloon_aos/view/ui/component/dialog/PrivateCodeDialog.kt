package com.example.alloon_aos.view.ui.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.DialogPrivateCodeBinding
import com.example.alloon_aos.viewmodel.ChallengeViewModel

interface PrivateCodeDialogInterface {
    fun onResultSuccess()
    fun onResultFail()
}

class PrivateCodeDialog(val privateCodeDialogInterface: PrivateCodeDialogInterface,
    val challengeViewModel: ChallengeViewModel) : DialogFragment() {
    private var _binding: DialogPrivateCodeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogPrivateCodeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.codeBtn.isEnabled = false

        binding.codeEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty())
                    binding.codeBtn.isEnabled = false
                else
                    binding.codeBtn.isEnabled = true
            }
        })

        binding.codeBtn.setOnClickListener {
            val code = binding.codeEdittext.text.toString()
            if (code == challengeViewModel.invitecode) {
                binding.codeBtn.setBackgroundResource(R.drawable.btn_icon_secondary)
                binding.codeBtn.setImageResource(R.drawable.ic_lock_open)
                privateCodeDialogInterface.onResultSuccess()
                dismiss()
            } else {
                privateCodeDialogInterface.onResultFail()
            }
        }

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val density = displayMetrics.density

            val fullWidth = displayMetrics.widthPixels

            val marginHorizontal = (32 * density).toInt()
            val width = fullWidth - 2 * marginHorizontal

            val params = window.attributes
            params.dimAmount = 0.4f
            window.attributes = params

            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}