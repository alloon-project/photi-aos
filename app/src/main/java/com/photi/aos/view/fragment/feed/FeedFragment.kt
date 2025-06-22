package com.photi.aos.view.fragment.feed

import android.animation.ValueAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ProgressBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.photi.aos.R
import com.photi.aos.databinding.FragmentFeedBinding
import com.photi.aos.view.adapter.FeedAdapter
import com.photi.aos.view.ui.component.bottomsheet.AlignBottomSheet
import com.photi.aos.view.ui.component.bottomsheet.AlignBottomSheetInterface
import com.photi.aos.view.ui.component.dialog.UploadCardDialog
import com.photi.aos.view.ui.component.dialog.UploadCardDialogInterface
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.ui.util.CameraHelper
import com.photi.aos.viewmodel.FeedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class FeedFragment : Fragment(),AlignBottomSheetInterface,UploadCardDialogInterface {
    private lateinit var binding : FragmentFeedBinding
    private lateinit var mContext: Context
    private lateinit var feedAdapter : FeedAdapter
    private val feedViewModel by activityViewModels<FeedViewModel>()
    private var selected_order = "first"
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var photoUri: Uri
    private lateinit var progressBar: ProgressBar
    private lateinit var tag : ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)
        binding.fragment = this
        binding.viewModel = feedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        feedAdapter = FeedAdapter(requireActivity().supportFragmentManager,feedViewModel,  lifecycle = viewLifecycleOwner.lifecycle
            )


        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (feedAdapter.getItemViewType(position)) {
                    FeedAdapter.VIEW_TYPE_HEADER -> 2
                    else -> 1
                }
            }
        }
        binding.feedOutRecyclerView.layoutManager =layoutManager
        binding.feedOutRecyclerView.setHasFixedSize(true)
        binding.feedOutRecyclerView.adapter = feedAdapter

        setObserve()

        feedViewModel.fetchChallengeFeeds()
        feedViewModel.fetchVerifiedMemberCount()
        feedViewModel.fetchIsVerifiedFeedExist()

        progressBar = binding.feedProgress
        tag = binding.constraintlayoutTag

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri = CameraHelper.getPhotoUri()
                UploadCardDialog(this, photoUri).show(parentFragmentManager, "CustomDialog")
            } else {
                CustomToast.createToast(activity, "사진 촬영 실패")?.show()
            }
        }
    }


    override fun onDestroyView() {
        feedViewModel.clearEndedChallengeData()
        super.onDestroyView()
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



    private fun setObserve() {
        feedViewModel.code.observe(viewLifecycleOwner) { code ->
            handleApiError(code)
        }

        feedViewModel.deleteFeedCode.observe(viewLifecycleOwner) { deletedId ->
            deletedId?.let {
                feedAdapter.removeFeedById(it)
                CustomToast.createToast(activity, "피드가 삭제됐어요. 내일 또 인증해주세요!")?.show()
            }
        }

        feedViewModel.isVerifiedFeedExistence.observe(viewLifecycleOwner){ isFeedExist ->
            isFeedExist?.let {
                if (!it) {
                    binding.noVerifiedUserView.visibility = View.VISIBLE
                    binding.feedOutRecyclerView.visibility = View.GONE
                } else {
                    binding.noVerifiedUserView.visibility = View.GONE
                    binding.feedOutRecyclerView.visibility = View.VISIBLE
                }
            }
        }

        feedViewModel.feedVerifiedUserCount.observe(viewLifecycleOwner) { verifiedCount ->
            val totalCount = feedViewModel.challengeMembers.value?.size ?: 0
            val verified = verifiedCount ?: 0

            val percentage = if (totalCount == 0) 0 else (verified * 100) / totalCount

            Log.d("FeedFragment", "total: $totalCount, verified: $verified, percentage: $percentage%")
            binding.memberCntTextView.text = "오늘 ${verified}명 인증!"

            if (verified == 0 || totalCount == 0)   return@observe
            progressBar.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    progressBar.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    animateProgressBar(progressBar, tag, 100, percentage)
                }
            })
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                feedViewModel.challengeFeeds.collectLatest { pagingData ->
                    feedAdapter.submitData(pagingData)
                }
            }
        }

    }

    private fun animateProgressBar(progressBar: ProgressBar, layout: View, max: Int, progress: Int) {
        val scaleFactor = if (max >= 100) 1 else (100 / max)

        progressBar.max = max * scaleFactor
        progressBar.progress = 0

        val startX = progressBar.left.toFloat()

        val animator = ValueAnimator.ofInt(0, progress * scaleFactor)
        animator.duration = 1000
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            progressBar.progress = animatedValue

            val progressRatio = animatedValue.toFloat() / progressBar.max
            val progressBarWidth = progressBar.width

            val progressX = startX + (progressRatio * progressBarWidth)

            val layoutCenterOffset = layout.width / 2

            layout.translationX = progressX - layoutCenterOffset
        }
        animator.start()
    }

    private fun handleApiError(code: String) {
        val errorMessages = mapOf(
            "TOKEN_UNAUTHENTICATED" to "승인되지 않은 요청입니다. 다시 로그인 해주세요.",
            "TOKEN_UNAUTHORIZED" to "권한이 없는 요청입니다. 로그인 후 다시 시도해주세요.",
            "UNKNOWN_ERROR" to "알 수 없는 오류가 발생했습니다."
        )

        if (code == "200 OK")
            CustomToast.createToast(activity, "피드가 삭제됐어요. 내일 또 인증해주세요!.")?.show()


        if (code == "IO_Exception") {
            CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
        } else {
            val message = errorMessages[code] ?: "예기치 않은 오류가 발생했습니다. ($code)"
            Log.e("IntroduceFragment", "Error: $message")
        }
    }

    fun showBottomList(){
        AlignBottomSheet(mContext,this,"최신순","인기순",selected_order)
            .show(activity?.supportFragmentManager!!, "bottomList")
    }

    fun doTodayCertify(){
        CameraHelper.checkPermissions(this) {
            CameraHelper.takePicture(this, takePictureLauncher)
        }
    }

    override fun onClickFirstButton() {
        selected_order = "first"

        binding.sortingButton.text = "최신순"
        feedViewModel.fetchChallengeFeeds("LATEST")
    }

    override fun onClickSecondButton() {
        selected_order = "second"

        binding.sortingButton.text = "인기순"
        feedViewModel.fetchChallengeFeeds("POPULAR")
    }

    override fun onClickUploadButton() {
        val imageFile = createMultipartFromUri(photoUri)
        feedViewModel.fetchChallengeFeed(imageFile)
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

}