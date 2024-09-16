package com.example.alloon_aos.view.fragment.feed

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentIntroduceBinding
import com.example.alloon_aos.databinding.FragmentPartyMemberBinding
import com.example.alloon_aos.databinding.ItemFeedPartyBinding
import com.example.alloon_aos.viewmodel.FeedViewModel

class PartyMemberFragment : Fragment() {
    private lateinit var binding : FragmentPartyMemberBinding
    private lateinit var mContext: Context
    private val feedViewModel by activityViewModels<FeedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_party_member, container, false)
        binding.fragment = this
        binding.viewModel = feedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.partyRecyclerView.adapter = PartyCardAdapter()
        binding.partyRecyclerView.layoutManager = LinearLayoutManager(mContext)
        binding.partyRecyclerView.setHasFixedSize(true)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    inner class ViewHolder(var binding : ItemFeedPartyBinding) : RecyclerView.ViewHolder(binding.root){
        fun setContents(holder: ViewHolder ,pos: Int) {
            with (feedViewModel.paryItem[pos]) {
                binding.idTextView.text = id
                binding.timeTextView.text = time+"일 째 활동중"
                if(text.isNotEmpty()){
                    binding.textTextView.text = text
                    binding.textTextView.setTextColor(mContext.getColor(R.color.gray600))
                }

                if(isMe)
                    binding.editImgBtn.visibility = View.VISIBLE

                binding.editImgBtn.setOnClickListener {
                    Toast.makeText(requireContext(),"목표 수정 ㄱㄱ", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    inner class PartyCardAdapter() : RecyclerView.Adapter<ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view = ItemFeedPartyBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ViewHolder(view)
        }


        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.setContents(viewHolder,position)
        }

        override fun getItemCount() = feedViewModel.paryItem.size

    }
}