package com.example.alloon_aos.view.fragment.feed

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentIntroduceBinding
import com.example.alloon_aos.databinding.FragmentPartyMemberBinding
import com.example.alloon_aos.databinding.ItemFeedPartyBinding
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.view.activity.JoinActivity
import com.example.alloon_aos.view.ui.component.dialog.CustomOneButtonDialog
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.FeedViewModel
import com.example.alloon_aos.viewmodel.JoinViewModel

class PartyMemberFragment : Fragment() {
    private lateinit var binding : FragmentPartyMemberBinding
    private lateinit var mContext: Context
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var partyCardAdapter: PartyCardAdapter
    private val feedViewModel by activityViewModels<FeedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_party_member, container, false)
        binding.fragment = this
        binding.viewModel = feedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        partyCardAdapter = PartyCardAdapter()
        binding.partyRecyclerView.adapter = partyCardAdapter
        binding.partyRecyclerView.layoutManager = LinearLayoutManager(mContext)
        binding.partyRecyclerView.setHasFixedSize(true)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val resultValue = data?.getStringExtra("myGoal")
                resultValue?.let {
                    receiveGoal(it)
                }
            }
        }
    }

    fun changeMyGoal(){
        val intent = Intent(requireContext(), JoinActivity::class.java)
        intent.putExtra("IS_FROM_FEED_ACTIVITY", true)
        activityResultLauncher.launch(intent)
    }

    fun receiveGoal(str : String){
        val index = feedViewModel.paryItem.indexOfFirst { it.isMe }
        if (index != -1) {
            feedViewModel.paryItem[index].text = str
            partyCardAdapter.notifyItemChanged(index)
            CustomToast.createToast(activity, "수정 완료! 새로운 목표까지 화이팅이에요!")?.show()
        }
    }

    inner class ViewHolder(var binding : ItemFeedPartyBinding) : RecyclerView.ViewHolder(binding.root){
        fun setContents(holder: ViewHolder ,pos: Int) {
            with (feedViewModel.paryItem[pos]) {
                binding.idTextView.text = id
                binding.timeTextView.text = time+"일 째 활동중"
                if(text.isNotEmpty()){
                    binding.goalTextView.text = text
                    binding.goalTextView.setTextColor(mContext.getColor(R.color.gray600))
                }

                if(isMe){
                    binding.editImgBtn.visibility = View.VISIBLE
                    binding.editImgBtn.setOnClickListener { changeMyGoal() }
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