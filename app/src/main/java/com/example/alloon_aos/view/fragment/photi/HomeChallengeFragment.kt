package com.example.alloon_aos.view.fragment.photi

import ProofShotHomeTransformer
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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentHomeChallengeBinding
import com.example.alloon_aos.view.activity.FeedActivity
import com.example.alloon_aos.view.adapter.ChallengeCardAdapter
import com.example.alloon_aos.view.adapter.ProofShotHomeAdapter
import com.example.alloon_aos.view.ui.component.dialog.UploadCardDialog
import com.example.alloon_aos.view.ui.component.dialog.UploadCardDialogInterface
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.CameraHelper
import com.example.alloon_aos.viewmodel.PhotiViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class HomeChallengeFragment : UploadCardDialogInterface, Fragment() {
    private lateinit var binding : FragmentHomeChallengeBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private lateinit var viewPager2: ViewPager2
    private lateinit var proofShotHomeAdapter: ProofShotHomeAdapter
    private lateinit var challengeCardAdapter: ChallengeCardAdapter
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

        proofShotHomeAdapter = ProofShotHomeAdapter(photiViewModel, onProofItemClickListener = { position ->
            CameraHelper.checkPermissions(this) {
                CameraHelper.takePicture(this, takePictureLauncher)
            }
        }, onCompleteClickListener = { startFeedDetail() })

        challengeCardAdapter = ChallengeCardAdapter(photiViewModel, onCardClickListener = { startFeedPage(it) })

        viewPager2 = binding.viewPager2
        viewPager2.adapter = proofShotHomeAdapter
        viewPager2.offscreenPageLimit = 2
        viewPager2.setPageTransformer(ProofShotHomeTransformer())

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photiViewModel.currentPos = position
            }
        })

        binding.myChallengeRecyclerview.adapter = challengeCardAdapter
        binding.myChallengeRecyclerview.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.myChallengeRecyclerview.setHasFixedSize(true)

        setObserver()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri = CameraHelper.getPhotoUri()
                Log.d("CameraHelper", "사진 촬영 성공: $photoUri")
                UploadCardDialog(this, photoUri).show(parentFragmentManager, "CustomDialog")
            } else {
                Log.e("CameraHelper", "사진 촬영 실패")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        photiViewModel.fetchMyChallenges()
    }

    private fun setObserver() {
        photiViewModel._allItems.observe(viewLifecycleOwner) {
            proofShotHomeAdapter.notifyDataSetChanged()
            challengeCardAdapter.notifyDataSetChanged()
            viewPager2.setCurrentItem(photiViewModel.currentPos, true)
            viewPager2.post {
                if (!viewPager2.isFakeDragging) {
                    viewPager2.beginFakeDrag()
                    viewPager2.fakeDragBy(-1f)
                    viewPager2.fakeDragBy(1f)
                    viewPager2.endFakeDrag()
                }
            }
        }
        photiViewModel.proofPos.observe(viewLifecycleOwner) {
            proofShotHomeAdapter.notifyDataSetChanged()
            viewPager2.setCurrentItem(it, true)
        }
        photiViewModel.feedUploadPhoto.observe(viewLifecycleOwner) {
            if (it) {
                photiViewModel.completeProof = true
                photiViewModel.fetchMyChallenges()
                CustomToast.createToast(activity,"인증 완료! 오늘도 수고했어요!")?.show()
            }
        }
        photiViewModel.code.observe(viewLifecycleOwner) { code ->
            handleApiError(code)
        }
    }

    private fun startFeedPage(id: Int) {
        val intent = Intent(requireContext(), FeedActivity::class.java)
        intent.putExtra("CHALLENGE_ID", id)
        startActivity(intent)
    }
    private fun startFeedDetail() {
        val intent = Intent(requireContext(), FeedActivity::class.java).apply {
            putExtra("CHALLENGE_ID", photiViewModel.id)
            putExtra("FEED_ID", photiViewModel.feedId)
        }
        startActivity(intent)
    }

    override fun onClickUploadButton() {
        val imageFile = createMultipartFromUri(photoUri)
        photiViewModel.fetchChallengeFeed(imageFile)
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

    private fun handleApiError(code: String) {
        val errorMessages = mapOf(
            "TOKEN_UNAUTHENTICATED" to "승인되지 않은 요청입니다. 다시 로그인 해주세요.",
            "TOKEN_UNAUTHORIZED" to "권한이 없는 요청입니다. 로그인 후 다시 시도해주세요.",
            "UNKNOWN_ERROR" to "알 수 없는 오류가 발생했습니다."
        )

        if (code == "200 OK" || code == "201 CREATED")   return

        if (code == "IO_Exception") {
            CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
        } else {
            val message = errorMessages[code] ?: "예기치 않은 오류가 발생했습니다. ($code)"
            Log.e("HomeFragment", "Error: $message")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CameraHelper.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "카메라 권한 허용됨")
                CameraHelper.takePicture(this, takePictureLauncher)
            } else {
                CustomToast.createToast(activity, "카메라 권한이 거부됐습니다")?.show()
            }
        }
    }
}
