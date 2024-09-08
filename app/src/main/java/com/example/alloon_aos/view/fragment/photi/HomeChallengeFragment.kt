package com.example.alloon_aos.view.fragment.photi

import ProofShotHomeTransformer
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.repository.TokenManager
import com.example.alloon_aos.databinding.FragmentHomeChallengeBinding
import com.example.alloon_aos.view.adapter.ChallengeCardAdapter
import com.example.alloon_aos.view.adapter.ProofShotHomeAdapter
import com.example.alloon_aos.view.ui.component.bottomsheet.ProofShotBottomSheet
import com.example.alloon_aos.view.ui.component.bottomsheet.ProofShotBottomSheetInterface
import com.example.alloon_aos.view.ui.component.dialog.UploadCardDialog
import com.example.alloon_aos.view.ui.component.dialog.UploadCardDialogInterface
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.PhotiViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class HomeChallengeFragment : ProofShotBottomSheetInterface, UploadCardDialogInterface, Fragment() {
    private lateinit var binding : FragmentHomeChallengeBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private lateinit var proofShotHomeAdapter: ProofShotHomeAdapter
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)

    private val REQUEST_CAMERA_PERMISSION = 1001
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var photoUri: Uri
    private lateinit var currentPhotoPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_challenge, container, false)
        binding.fragment = this
        binding.viewModel = photiViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        proofShotHomeAdapter = ProofShotHomeAdapter(photiViewModel, emptyList(),
            onItemClickListener = { position ->
                ProofShotBottomSheet(this)
                    .show(activity?.supportFragmentManager!!, "CustomDialog")
            })

        binding.viewPager2.adapter = proofShotHomeAdapter
        binding.viewPager2.offscreenPageLimit = 2
        binding.viewPager2.setPageTransformer(ProofShotHomeTransformer())

        binding.myChallengeRecyclerview.adapter = ChallengeCardAdapter(photiViewModel)
        binding.myChallengeRecyclerview.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.myChallengeRecyclerview.setHasFixedSize(true)

        setObserver()

        return binding.root
    }

    private fun setObserver() {
        photiViewModel.proofItemList.observe(viewLifecycleOwner){
            proofShotHomeAdapter.updatePhotoItems(it)
        }
    }

    override fun onClickPhotoButton() {
        checkPermissions()
    }

    override fun onClickGifButton() {
        UploadCardDialog(this,"https://ifh.cc/g/6HRkxa.jpg")
            .show(activity?.supportFragmentManager!!, "CustomDialog")
    }

    override fun onClickUploadButton() {
        //사진 업로드 api
        CustomToast.createToast(activity,"인증 완료! 오늘도 수고했어요!")?.show()
    }


    //아래부터 카메라 관련
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CAMERA_PERMISSION
                )
            } else {
                dispatchTakePictureIntent()
            }
        } else {
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent()
                } else {
                    // 권한이 거부된 경우 사용자에게 메시지 표시
                    CustomToast.createToast(activity,"카메라 권한이 거부됐습니다")?.show()
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            try {
                val photoFile = createImageFile()
                photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.alloon_aos.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val photoUrl = photoUri.toString()
            UploadCardDialog(this,photoUrl)
                .show(activity?.supportFragmentManager!!, "CustomDialog")
        }
    }

}