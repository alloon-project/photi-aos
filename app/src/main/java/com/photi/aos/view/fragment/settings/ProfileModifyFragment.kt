package com.photi.aos.view.fragment.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.photi.aos.R
import com.photi.aos.databinding.FragmentProfileModifyBinding
import com.photi.aos.view.activity.SettingsActivity
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.viewmodel.SettingsViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfileModifyFragment : Fragment() {
    private lateinit var binding: FragmentProfileModifyBinding
    private lateinit var mContext: Context
    private val settingsViewModel by activityViewModels<SettingsViewModel>()
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private var isModified = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile_modify, container, false)
        binding.fragment = this
        binding.viewModel = settingsViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val mActivity = activity as SettingsActivity
        mActivity.setAppBar("프로필 수정")

        setObserve()
        settingsViewModel.fetchUserProfile()

        initGalleryLauncher()

        return binding.root
    }

    private fun setObserve() {
        settingsViewModel.code.observe(viewLifecycleOwner) { code ->
            handleApiError(code)
        }

        settingsViewModel.profileImage.observe(viewLifecycleOwner){ url ->
            if (!url.isNullOrBlank()) {
                Glide.with(binding.userImgImageView.context)
                    .load(url)
                    .transform(CircleCrop())
                    .into(binding.userImgImageView)
            }
        }
    }

    private fun handleApiError(code: String) {
        val errorMessages = mapOf(
            "USER_NOT_FOUND" to "존재 하지 않는 회원입니다.",
            "TOKEN_UNAUTHENTICATED" to "승인 되지 않은 요청입니다. 다시 로그인 해주세요.",
            "TOKEN_UNAUTHORIZED" to "권한이 없는 요청입니다. 로그인 후 다시 시도해주세요.",
            "UNKNOWN_ERROR" to "알 수 없는 오류가 발생 했습니다."
        )

        if (code == "200 OK" && isModified)  {
            settingsViewModel.fetchUserProfile()
            isModified = false
            return
        }

        if(code == "200 OK") return

        if (code == "IO_Exception") {
            CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
        } else {
            val message = errorMessages[code] ?: "예기치 않은 오류가 발생했습니다. ($code)"
            Log.e("ProfileModifyFragment", "Error: $message")
        }
    }

    private fun initGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let {
                    val imageFile = createMultipartFromUri(it)
                    settingsViewModel.sendProfileImage(imageFile)
                    isModified = true
                } ?: run {
                    Log.e("Gallery", "이미지 선택에 실패했습니다.")
                }
            }
        }
    }


    private fun createMultipartFromUri(uri: Uri): MultipartBody.Part {
        val contentResolver = requireContext().contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("Invalid URI")
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val tempFile = File(requireContext().cacheDir, fileName)

        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        val requestBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("imageFile", tempFile.name, requestBody)
    }


    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    fun moveFlag(i: Int) {
        when (i) {
            1 -> {
                view?.findNavController()
                    ?.navigate(R.id.action_profileModifyFragment_to_profilePasswordFragment)
            }

            2 -> {
                view?.findNavController()
                    ?.navigate(R.id.action_profileModifyFragment_to_unSubscribeFragment)
            }
        }
    }
}