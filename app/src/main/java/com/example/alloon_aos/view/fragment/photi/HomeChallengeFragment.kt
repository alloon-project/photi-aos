package com.example.alloon_aos.view.fragment.photi

import ProofShotHomeTransformer
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.alloon_aos.view.ui.component.dialog.UploadCardDialog
import com.example.alloon_aos.view.ui.component.dialog.UploadCardDialogInterface
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.CameraHelper
import com.example.alloon_aos.viewmodel.PhotiViewModel

class HomeChallengeFragment : UploadCardDialogInterface, Fragment() {
    private lateinit var binding : FragmentHomeChallengeBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private lateinit var proofShotHomeAdapter: ProofShotHomeAdapter
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var photoUri: Uri
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_challenge, container, false)
        binding.fragment = this
        binding.viewModel = photiViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        proofShotHomeAdapter = ProofShotHomeAdapter(photiViewModel, emptyList(), onItemClickListener = { position ->
            CameraHelper.checkPermissions(this) {
                CameraHelper.takePicture(this, takePictureLauncher)
            }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Log.d("CameraHelper", "사진 촬영 성공: $photoUri")
                UploadCardDialog(this, photoUri.toString()).show(parentFragmentManager, "CustomDialog")
            } else {
                Log.e("CameraHelper", "사진 촬영 실패")
            }
        }
    }

    private fun setObserver() {
        photiViewModel.proofItemList.observe(viewLifecycleOwner) {
            proofShotHomeAdapter.updatePhotoItems(it)
        }
    }

    override fun onClickUploadButton() {
        CustomToast.createToast(activity,"인증 완료! 오늘도 수고했어요!")?.show()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CameraHelper.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "카메라 권한 허용됨")
                val photoFile = CameraHelper.createImageFile(this)
                photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.alloon_aos.fileprovider",
                    photoFile
                )
                takePictureLauncher.launch(photoUri)  // 권한 허용 후 카메라 실행
            } else {
                CustomToast.createToast(activity, "카메라 권한이 거부됐습니다")?.show()
            }
        }
    }
}
