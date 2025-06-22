package com.photi.aos.view.fragment.create

import android.animation.ObjectAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.photi.aos.R
import com.photi.aos.databinding.FragmentCreateImageBinding
import com.photi.aos.view.activity.CreateActivity
import com.photi.aos.view.adapter.ThumbnailAdapter
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.ui.util.CameraHelper
import com.photi.aos.viewmodel.CreateViewModel

class CreateImageFragment : Fragment() {
    private lateinit var binding : FragmentCreateImageBinding
    private lateinit var mContext: Context
    private lateinit var thumbnailAdapter: ThumbnailAdapter
    private val createViewModel by activityViewModels<CreateViewModel>()
    private lateinit var mActivity: CreateActivity
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_image, container, false)
        binding.fragment = this
        binding.viewModel = createViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mActivity = activity as CreateActivity
        mActivity.setAppBar()

        if (mActivity.isFromChallenge){
            setModifyLayout()
            createViewModel.setCustomImg()
        } else {
            createViewModel.initImage()
        }

        thumbnailAdapter = ThumbnailAdapter(createViewModel, onItemClickListener = { position ->
            CameraHelper.checkPermissions(this) {
                pickImageFromGallery()
            }
        })
        binding.thumbnailRecyclerView.adapter = thumbnailAdapter
        binding.thumbnailRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.thumbnailRecyclerView.setHasFixedSize(true)

        setObserver()
        setListener()

        ObjectAnimator.ofInt(binding.progress, "progress", 40,60)
            .setDuration(500)
            .start()

        return binding.root
    }

    private fun setModifyLayout() {
        mActivity.setTitle("대표 이미지 수정")
        binding.progress.visibility = View.GONE
        binding.nextBtn.setText("저장하기")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun setListener() {
        binding.nextBtn.setOnClickListener {
            if (mActivity.isFromChallenge)
                mActivity.modifyImage()
            else
                view?.findNavController()?.navigate(R.id.action_createImageFragment_to_createRuleFragment)
        }
    }

    fun setObserver() {
        createViewModel._selectURL.observe(viewLifecycleOwner) {
            Glide.with(binding.thumbnailImageview.context)
                .load(it)
                .transform(CenterCrop(), RoundedCorners(32))
                .into(binding.thumbnailImageview)
            thumbnailAdapter.notifyDataSetChanged()

            createViewModel.setImageFile(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                createViewModel.select(4)
                createViewModel.setUriToURL(uri)
            }
        }
    }

    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery()
            } else {
                CustomToast.createToast(activity, "갤러리 권한이 거부됐습니다")?.show()
            }
        }
    }
}