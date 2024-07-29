package com.example.alloon_aos.view.ui.component.toast

import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ToastCustomBinding

object CustomToast {
    fun createToast(context: FragmentActivity?, message: String = "~~"): Toast? {
        val inflater = LayoutInflater.from(context)
        val binding: ToastCustomBinding =
            DataBindingUtil.inflate(inflater, R.layout.toast_custom ,null, false)

        binding.message.text = message

        return Toast(context).apply {
            //setGravity(Gravity.TOP or Gravity.LEFT, 100, 0)
            duration = Toast.LENGTH_SHORT
            view = binding.root
        }
    }

}