package com.photi.aos.view.ui.component.toast

import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.photi.aos.R
import com.photi.aos.databinding.ToastCustomBinding

object CustomToast {
    fun createToast(context: FragmentActivity?, message: String = "~~",icon : String = "bulb"): Toast? {
        val inflater = LayoutInflater.from(context)
        val binding: ToastCustomBinding =
            DataBindingUtil.inflate(inflater, R.layout.toast_custom ,null, false)

        binding.message.text = message

        when(icon){
            "check" -> {
                binding.icon.setImageResource(R.drawable.ic_check_grey)
            }
            "circle" -> {
                binding.icon.setImageResource(R.drawable.ic_close_circle_red)
            }
        }
        return Toast(context).apply {
            //setGravity(Gravity.TOP or Gravity.LEFT, 100, 0)
            duration = Toast.LENGTH_SHORT
            view = binding.root
        }
    }

}