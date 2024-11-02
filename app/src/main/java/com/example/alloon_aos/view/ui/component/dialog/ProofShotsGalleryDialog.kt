package com.example.alloon_aos.view.ui.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alloon_aos.databinding.DialogProofShotsGalleryBinding
import com.example.alloon_aos.databinding.ItemProofShotsGalleryBinding
import com.example.alloon_aos.viewmodel.FeedInItem

class ProofShotsGalleryDialog(val count : Int, val challenge_id : Int): DialogFragment() {
    private var _binding: DialogProofShotsGalleryBinding? = null
    private val binding get() = _binding!!
    val feedInItems = arrayListOf<FeedInItem>(
        FeedInItem("photi","방금","https://ifh.cc/g/6HRkxa.jpg",true,2),
        FeedInItem("seul","1분전","https://ifh.cc/g/AA0NMd.jpg",false,5),
        FeedInItem("HB","18분전","https://ifh.cc/g/09y6Mo.jpg",false,10),
        FeedInItem("photi1","30분전","https://ifh.cc/g/KB2Vh1.jpg",false,1),
        FeedInItem("photi2","방금","https://ifh.cc/g/yxgmBH.webp",false),
    )
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogProofShotsGalleryBinding.inflate(inflater, container, false)
        val view = binding.root

        with(binding){
            countTextView.text = count.toString()
            challengeRecyclerview.adapter = ProofShotsGalleryAdapter(feedInItems)
            challengeRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
            challengeRecyclerview.setHasFixedSize(true)
            backImgBtn.setOnClickListener {
                dismiss()
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        // 전체화면 설정
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ProofShotsGalleryAdapter(val comments: ArrayList<FeedInItem>): RecyclerView.Adapter<ProofShotsGalleryAdapter.ViewHolder>() {
        inner class ViewHolder(var binding: ItemProofShotsGalleryBinding) : RecyclerView.ViewHolder(binding.root) {
            fun setContents(holder: ViewHolder, pos: Int) {
                with(comments[pos]) {
                    binding.dateTextView.text = id
                    binding.chipBtn.text = "크로아상 만들기"
                    Glide
                        .with(holder.itemView.context)
                        .load(url)
                        .into(binding.imgView)
                    binding.shortcutImgBtn.setOnClickListener{
                        //공유하기
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ItemProofShotsGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.setContents(viewHolder, position)
        }
        override fun getItemCount() = comments.size
    }
}