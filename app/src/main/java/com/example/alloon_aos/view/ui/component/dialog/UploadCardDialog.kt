package com.example.alloon_aos.view.ui.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.alloon_aos.databinding.DialogUploadCardBinding

interface UploadCardDialogInterface {
    fun onClickUploadButton()
}

class UploadCardDialog(val uploadCardDialogInterface: UploadCardDialogInterface, val url: Uri) : DialogFragment() {
    private var _binding: DialogUploadCardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogUploadCardBinding.inflate(inflater, container, false)
        val view = binding.root

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)

        val multiOption = MultiTransformation(
            CenterCrop(),
            RoundedCorners(32),
        )

        Glide
            .with(view.context)
            .load(url)
            .apply(RequestOptions.bitmapTransform(multiOption))
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(a_resource: Drawable, a_transition: Transition<in Drawable>?) {
                    view.background = a_resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding.uploadBtn.setOnClickListener {
            uploadCardDialogInterface.onClickUploadButton()
            dismiss()
        }

        return view
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val density = displayMetrics.density

            val fullWidth = displayMetrics.widthPixels
            val fullHeight = displayMetrics.heightPixels

            val marginHorizontal = (24 * density).toInt()
            val width = fullWidth - 2 * marginHorizontal

            val marginVertical = (111 * density).toInt()
            val height = fullHeight - 2 * marginVertical

            val params = window.attributes
            params.dimAmount = 0.4f
            window.attributes = params

            window.setLayout(width, height)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}