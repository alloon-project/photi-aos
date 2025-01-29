package com.example.alloon_aos.view.fragment.feed

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.model.response.ChallengeMember
import com.example.alloon_aos.data.storage.SharedPreferencesManager
import com.example.alloon_aos.databinding.FragmentPartyMemberBinding
import com.example.alloon_aos.databinding.ItemFeedPartyBinding
import com.example.alloon_aos.view.activity.GoalActivity
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.FeedViewModel

class PartyMemberFragment : Fragment() {
    private lateinit var binding : FragmentPartyMemberBinding
    private lateinit var mContext: Context
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var partyCardAdapter: PartyCardAdapter
    private val sharedPreferencesManager = SharedPreferencesManager(MyApplication.mySharedPreferences)
    private lateinit var myName : String
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

        setObserve()
        feedViewModel.fetchChallengeMembers()

        myName = sharedPreferencesManager.getUserName() ?: ""
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun setObserve() {
        feedViewModel.code.observe(viewLifecycleOwner) { code ->
            handleApiError(code)
        }

        feedViewModel.challengeMembers.observe(viewLifecycleOwner) { data ->
            if (data != null ) {
                binding.totalCountTextView.text = "파티원 ${data.size}명"
                partyCardAdapter.updateMembers(data)
            }
        }
    }
    private fun handleApiError(code: String) {
        val errorMessages = mapOf(
            "TOKEN_UNAUTHENTICATED" to "승인되지 않은 요청입니다. 다시 로그인 해주세요.",
            "TOKEN_UNAUTHORIZED" to "권한이 없는 요청입니다. 로그인 후 다시 시도해주세요.",
            "UNKNOWN_ERROR" to "알 수 없는 오류가 발생했습니다."
        )

        if (code == "200 OK")   return

        if (code == "IO_Exception") {
            CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
        } else {
            val message = errorMessages[code] ?: "예기치 않은 오류가 발생했습니다. ($code)"
            Log.e("IntroduceFragment", "Error: $message")
        }
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

    fun changeMyGoal(goal : String){
        val intent = Intent(requireContext(), GoalActivity::class.java)
        intent.putExtra("IS_FROM_FEED_ACTIVITY", true)
        intent.putExtra("ID", feedViewModel.challengeId)
        intent.putExtra("TITLE", feedViewModel.name)
        intent.putExtra("GOAL", goal)
        activityResultLauncher.launch(intent)
    }

    fun receiveGoal(str : String){
        val index = feedViewModel.challengeMembers.value?.indexOfFirst { it.isCreator } ?: -1
        if (index != -1) {
            feedViewModel.challengeMembers.value?.get(index)?.let {
                feedViewModel.updateGoal(str)
                partyCardAdapter.notifyItemChanged(index)
                CustomToast.createToast(activity, "수정 완료! 새로운 목표까지 화이팅이에요!")?.show()
            }
        }
    }

    inner class ViewHolder(var binding : ItemFeedPartyBinding) : RecyclerView.ViewHolder(binding.root){
        fun setContents(member: ChallengeMember) {
            with (member) {
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(binding.imageView6.context)
                        .load(imageUrl)
                        .transform(CircleCrop())
                        .into(binding.imageView6)
                }

                binding.idTextView.text = username
                binding.timeTextView.text = "${duration}일 째 활동중"
                if(goal != null){
                    binding.goalTextView.text = goal
                    binding.goalTextView.setTextColor(mContext.getColor(R.color.gray600))
                }

                if(isCreator){
                   binding.isCreatorTextView.visibility = View.VISIBLE
                    binding.divider.visibility = View.GONE
                }


                if(username == myName){
                    binding.editImgBtn.visibility = View.VISIBLE
                    binding.editImgBtn.setOnClickListener { changeMyGoal(goal ?: "") }
                }


            }
        }
    }

    inner class PartyCardAdapter() : RecyclerView.Adapter<ViewHolder>(){

        private var members: List<ChallengeMember> = emptyList()

        fun updateMembers(newMembers: List<ChallengeMember>) {
            members = newMembers
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view = ItemFeedPartyBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.setContents(members[position])
        }

        override fun getItemCount() = members.size

    }

}
