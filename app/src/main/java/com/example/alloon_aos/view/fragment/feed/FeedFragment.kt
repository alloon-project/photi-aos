package com.example.alloon_aos.view.fragment.feed

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentFeedBinding
import com.example.alloon_aos.view.adapter.FeedOutAdapter
import com.example.alloon_aos.view.ui.component.bottomsheet.AlignBottomSheet
import com.example.alloon_aos.view.ui.component.bottomsheet.AlignBottomSheetInterface
import com.example.alloon_aos.view.ui.component.dialog.UploadCardDialog
import com.example.alloon_aos.view.ui.component.dialog.UploadCardDialogInterface
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.CameraHelper
import com.example.alloon_aos.view.ui.util.dpToPx
import com.example.alloon_aos.viewmodel.FeedInItem
import com.example.alloon_aos.viewmodel.FeedViewModel

class FeedFragment : Fragment(),AlignBottomSheetInterface,UploadCardDialogInterface {
    private lateinit var binding : FragmentFeedBinding
    private lateinit var mContext: Context
    private lateinit var feedOutAdapter : FeedOutAdapter
    private val feedViewModel by activityViewModels<FeedViewModel>()
    private var selected_order = "first"
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var photoUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)
        binding.fragment = this
        binding.viewModel = feedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        feedOutAdapter = FeedOutAdapter(requireActivity().supportFragmentManager ,feedViewModel)
        binding.feedOutRecyclerView.adapter = feedOutAdapter
        binding.feedOutRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.feedOutRecyclerView.setHasFixedSize(true)

        if(!feedViewModel.isMissionClear) {
            showToastAbove("오늘의 인증이 완료되지 않았어요!")
        }

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
                Log.d("CameraHelper", "사진 촬영 성공: $photoUri")
                UploadCardDialog(this, photoUri.toString()).show(parentFragmentManager, "CustomDialog")
            } else {
                Log.e("CameraHelper", "사진 촬영 실패")
            }
        }
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

    fun showBottomList(){
        AlignBottomSheet(mContext,this,"최신순","인기순",selected_order)
            .show(activity?.supportFragmentManager!!, "bottomList")
    }

    fun doTodayCertify(){
        CameraHelper.checkPermissions(this) {
            CameraHelper.takePicture(this, takePictureLauncher)
        }
    }

    private fun showToastAbove(message:String){
        val inflater = LayoutInflater.from(requireContext())
        val customToastView = inflater.inflate(R.layout.toast_tooltip_under, null)

        customToastView.findViewById<TextView>(R.id.textView).text = message

        val toast = Toast(requireContext())
        val yOffset = requireContext().dpToPx(95f)

        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, yOffset)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = customToastView
        toast.show()
    }

    override fun onClickFirstButton() {
        selected_order = "first"
        //최신순으로 정렬
    }

    override fun onClickSecondButton() {
        selected_order = "second"
        //인기순으로 정렬
    }

    override fun onClickUploadButton() {
        feedViewModel.isMissionClear = true
        binding.fixedImageButton.visibility = View.GONE
        CustomToast.createToast(activity, "인증 완료! 오늘도 수고했어요!")?.show()

        val newFeedInItem= FeedInItem(feedViewModel.id,"방금",photoUri.toString(),false,0)
        feedViewModel.feedOutItems[0].feedInItems.add(0, newFeedInItem)
        feedOutAdapter.notifyItemChanged(0)
    }

}