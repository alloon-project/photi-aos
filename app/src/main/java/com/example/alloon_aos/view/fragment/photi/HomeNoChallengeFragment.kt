package com.example.alloon_aos.view.fragment.photi

import MemberHomeTransformer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentHomeNochallengeBinding
import com.example.alloon_aos.view.activity.ChallengeActivity
import com.example.alloon_aos.view.activity.CreateActivity
import com.example.alloon_aos.view.adapter.MemberHomeAdapter
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.PhotiViewModel

class HomeNoChallengeFragment : Fragment() {
    private lateinit var binding : FragmentHomeNochallengeBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private lateinit var memberHomeAdapter: MemberHomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_nochallenge, container, false)
        binding.fragment = this
        binding.viewModel = photiViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //binding.membernumTextview.text = ""

        memberHomeAdapter = MemberHomeAdapter(photiViewModel)
        binding.viewPager.adapter = memberHomeAdapter
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.setPageTransformer(MemberHomeTransformer())

        photiViewModel.resetApiResponseValue()
        setObserver()

        return binding.root
    }

    private fun setObserver() {
        photiViewModel.hotItemsListData.observe(viewLifecycleOwner){
            memberHomeAdapter.notifyDataSetChanged()
        }

        photiViewModel._photoItem.observe(viewLifecycleOwner){
            binding.titleTextview.setText(it.name)
            binding.contentTextview.setText(it.goal)
            binding.dateTextview.setText(it.endDate)
            binding.timeTextview.setText(it.proveTime)

            binding.hash1Btn.visibility = View.GONE
            binding.hash2Btn.visibility = View.GONE
            binding.hash3Btn.visibility = View.GONE

            it.hashtags.forEachIndexed { index, hashtag ->
                when (index) {
                    0 -> {
                        binding.hash1Btn.text = hashtag.hashtag
                        binding.hash1Btn.visibility = View.VISIBLE
                    }
                    1 -> {
                        binding.hash2Btn.text = hashtag.hashtag
                        binding.hash2Btn.visibility = View.VISIBLE
                    }
                    2 -> {
                        binding.hash3Btn.text = hashtag.hashtag
                        binding.hash3Btn.visibility = View.VISIBLE
                    }
                }
            }

            val cnt = it.currentMemberCnt
            val imgs = it.memberImages

            when (cnt) {
                1 -> {
                    binding.avatarOneLayout.visibility = View.VISIBLE
                    binding.membernumTextview.text = "1명 도전 중"
                    loadImage(binding.oneUser1ImageView, imgs.getOrNull(0)?.memberImage)
                }
                2 -> {
                    binding.avatarTwoLayout.visibility = View.VISIBLE
                    binding.membernumTextview.text = "2명 도전 중"
                    loadImage(binding.twoUser1ImageView, imgs.getOrNull(0)?.memberImage)
                    loadImage(binding.twoUser2ImageView, imgs.getOrNull(1)?.memberImage)
                }
                3 -> {
                    binding.avatarThreeLayout.visibility = View.VISIBLE
                    binding.membernumTextview.text = "3명 도전 중"
                    loadImage(binding.threeUser1ImageView, imgs.getOrNull(0)?.memberImage)
                    loadImage(binding.threeUser2ImageView, imgs.getOrNull(1)?.memberImage)
                    loadImage(binding.threeUser3ImageView, imgs.getOrNull(2)?.memberImage)
                }
                else -> {
                    binding.avatarMultipleLayout.visibility = View.VISIBLE
                    //binding.avatarThreeLayout.visibility = View.VISIBLE
                    binding.membernumTextview.text = "${cnt}명 도전 중"
                    loadImage(binding.multipleUser1ImageView, imgs.getOrNull(0)?.memberImage)
                    loadImage(binding.multipleUser2ImageView, imgs.getOrNull(1)?.memberImage)
                    binding.countTextView.text = "+${(cnt - 2).coerceAtLeast(0)}"
                }
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == photiViewModel.hotItems.size) {
                    binding.photoLayout.visibility = View.GONE
                    binding.addLayout.visibility = View.VISIBLE
                } else {
                    val selectedPhoto = photiViewModel.hotItems.get(position)
                    if (selectedPhoto != null) {
                        photiViewModel.setCurrentPhoto(selectedPhoto)
                    }
                    binding.addLayout.visibility = View.GONE
                    binding.photoLayout.visibility = View.VISIBLE
                }
            }
        })

        photiViewModel.apiResponse.observe(viewLifecycleOwner) { response ->
            when (response.code) {
                "200 OK" -> {
                    startChallenge()
                }
                "TOKEN_UNAUTHENTICATED" -> {
                    CustomToast.createToast(activity, "승인되지 않은 요청입니다. 다시 로그인 해주세요.")?.show()
                }
                "TOKEN_UNAUTHORIZED" -> {
                    CustomToast.createToast(activity, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요.")?.show()
                }
                "CHALLENGE_NOT_FOUND" -> {
                    CustomToast.createToast(activity, "존재하지 않는 챌린지입니다.", "circle")?.show()
                }
                "IO_Exception" -> {
                    CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }
                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                }
            }
        }
    }

    private fun loadImage(imageView: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            Glide.with(imageView.context)
                .load(url)
                .circleCrop()
                .into(imageView)
        }
    }

    fun joinChallenge() {
        photiViewModel.getChallenge()
    }

    fun createChallenge() {
        val intent = Intent(requireContext(), CreateActivity::class.java)
        startActivity(intent)
    }

    private fun startChallenge() {
        val intent = Intent(requireContext(), ChallengeActivity::class.java)
        intent.putExtra("IS_FROM_HOME",true)
        intent.putExtra("ID",photiViewModel.id)
        intent.putExtra("data", photiViewModel.getData())
        intent.putExtra("image", photiViewModel.imgFile)
        startActivity(intent)
    }

}