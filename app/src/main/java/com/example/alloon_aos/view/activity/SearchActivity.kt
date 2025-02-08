package com.example.alloon_aos.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ActivitySearchBinding
import com.example.alloon_aos.databinding.ItemSearchChallengeRecyclerviewBinding
import com.example.alloon_aos.databinding.ItemSearchChipRecyclerviewBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.SearchViewModel
import com.google.android.material.tabs.TabLayout

class SearchActivity : AppCompatActivity() {
    lateinit var binding : ActivitySearchBinding
    private val searchViewModel : SearchViewModel by viewModels()
    lateinit var hashAdapter: SearchHashAdapter
    lateinit var challengAdapter: SearchChallengeAdpater
    private lateinit var nestedScrollView: NestedScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.viewModel = searchViewModel

        searchViewModel.resetApiResponseValue()
        searchViewModel.resetSearchList()

        hashAdapter = SearchHashAdapter()
        binding.hashRecyclerview.adapter = hashAdapter
        binding.hashRecyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.hashRecyclerview.setHasFixedSize(true)

        challengAdapter = SearchChallengeAdpater()
        binding.challengeRecyclerview.adapter = challengAdapter
        binding.challengeRecyclerview.layoutManager = GridLayoutManager(this, 2)
        binding.challengeRecyclerview.setHasFixedSize(true)

        setListener()
        setObserve()

//        nestedScrollView = binding.scrollView
//        nestedScrollView.setOnScrollChangeListener { v, _, scrollY, _, _ ->
//            if (scrollY == nestedScrollView.getChildAt(0).measuredHeight - v.measuredHeight){
//                if (!searchViewModel.latestPage)
//                    if (searchViewModel.isHash)
//                        searchViewModel.getSearchHash()
//                    else
//                        searchViewModel.getSearchName()
//            }
//        }
    }


    private fun setBeforeLayout() {
        binding.searchBeforeLayout.visibility = View.VISIBLE
        binding.searchAfterLayout.visibility = View.GONE
        binding.searchBtn.visibility = View.VISIBLE
        binding.deleteBtn.visibility = View.GONE
        searchViewModel.resetSearchList()
    }

    private fun setAfterLayout() {
        binding.searchBeforeLayout.visibility = View.GONE
        binding.searchAfterLayout.visibility = View.VISIBLE
        binding.searchBtn.visibility = View.GONE
        binding.deleteBtn.visibility = View.VISIBLE
        searchViewModel.search = binding.searchEditText.text.toString()
        searchViewModel.addHash()
        searchViewModel.getSearchName()
    }

    private fun setHashLayout() {
        if (searchViewModel._hashs.size == 0) {
            binding.hashRecyclerview.visibility = View.GONE
            binding.noSearchTextview.visibility = View.VISIBLE
        } else {
            binding.hashRecyclerview.visibility = View.VISIBLE
            binding.noSearchTextview.visibility = View.GONE
        }
    }

    private fun setChallengeLayout() {
        val size = searchViewModel.searchList.size
        if (size == 0) {
            binding.challengeRecyclerview.visibility = View.GONE
            binding.noChallengeTextview.visibility = View.VISIBLE
        } else {
            binding.challengeRecyclerview.visibility = View.VISIBLE
            binding.noChallengeTextview.visibility = View.GONE

//            if (size < 10)
//                searchViewModel.latestPage = true
//            searchViewModel.page ++
        }
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun setListener() {
        binding.searchBtn.setOnClickListener {
            hideKeyboard()
            if (binding.searchEditText.text.isEmpty())
                setBeforeLayout()
            else
                setAfterLayout()
        }
        binding.backImgBtn.setOnClickListener {
            finish()
        }
        binding.deleteAllBtn.setOnClickListener {
            searchViewModel.deleteAllHash()
        }
        binding.deleteBtn.setOnClickListener {
            binding.searchEditText.text = null
        }

        binding.root.setOnClickListener {
            if (currentFocus != null) {
                hideKeyboard()
            }
        }

        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                if (binding.searchEditText.text.isEmpty())
                    setBeforeLayout()
                else
                    setAfterLayout()
                true
            } else {
                false
            }
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setBeforeLayout()
            }
        })

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                searchViewModel.resetSearchList()
                when(p0?.position) {
                    0 -> { searchViewModel.getSearchName() }
                    1 -> { searchViewModel.getSearchHash() }
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

        })
    }

    private fun setObserve() {
        searchViewModel.hashs.observe(this) {
            hashAdapter.notifyDataSetChanged()
            setHashLayout()
        }

        searchViewModel.searchText.observe(this) {
            binding.searchEditText.setText(it)
            setAfterLayout()
        }

        searchViewModel._searchList.observe(this) {
            challengAdapter.notifyDataSetChanged()
        }

        searchViewModel.apiResponse.observe(this) { response ->
            when (response.code) {
                "200 OK" -> {
                    setChallengeLayout()
                }
                "CHALLENGE_MEMBER_NOT_FOUND" -> {
                    CustomToast.createToast(this, "존재하지 않는 챌린지 파티원입니다.")?.show()
                }
                "TOKEN_UNAUTHENTICATED" -> {
                    CustomToast.createToast(this, "승인되지 않은 요청입니다. 다시 로그인 해주세요.")?.show()
                }
                "TOKEN_UNAUTHORIZED" -> {
                    CustomToast.createToast(this, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요.")?.show()
                }
                "IO_Exception" -> {
                    CustomToast.createToast(this, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }
                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                }
            }
        }

        searchViewModel.challengeResponse.observe(this) { response ->
            when (response.code) {
                "200 OK" -> {
                    startChallenge()
                }
                "TOKEN_UNAUTHENTICATED" -> {
                    CustomToast.createToast(this, "승인되지 않은 요청입니다. 다시 로그인 해주세요.")?.show()
                }
                "TOKEN_UNAUTHORIZED" -> {
                    CustomToast.createToast(this, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요.")?.show()
                }
                "CHALLENGE_NOT_FOUND" -> {
                    CustomToast.createToast(this, "존재하지 않는 챌린지입니다.", "circle")?.show()
                }
                "IO_Exception" -> {
                    CustomToast.createToast(this, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }
                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                }
            }
        }
    }

    private fun startChallenge() {
        val intent = Intent(this, ChallengeActivity::class.java)
        intent.putExtra("IS_FROM_HOME",true)
        intent.putExtra("ID",searchViewModel.id)
        intent.putExtra("data", searchViewModel.getData())
        intent.putExtra("image", searchViewModel.imgFile)
        startActivity(intent)
    }


    //before adapter
    inner class HashViewHolder(private val binding: ItemSearchChipRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(pos: Int) {
            with (searchViewModel._hashs[pos]) {
                binding.hashtagTextveiw.text = this
                binding.deleteBtn.setOnClickListener {
                    searchViewModel.deleteHash(this)
                }
                binding.root.setOnClickListener {
                    searchViewModel.setSearchText(this)
                }
            }
        }
    }

    inner class SearchHashAdapter() : RecyclerView.Adapter<HashViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HashViewHolder {
            val binding = ItemSearchChipRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return HashViewHolder(binding)
        }
        override fun onBindViewHolder(viewHolder: HashViewHolder, position: Int) {
            viewHolder.setContents(position)
        }
        override fun getItemCount() = searchViewModel._hashs.size

    }


    // after adapter
    inner class ChallengeViewHolder(var binding: ItemSearchChallengeRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setContents(holder: ChallengeViewHolder, pos: Int) {
            with(searchViewModel.searchList[pos]) {
                binding.titleTextView.text = name
                binding.dateTextView.text = endDate

                Glide
                    .with(holder.itemView.context)
                    .load(imageUrl)
                    .into(binding.imgView)

                val cnt = currentMemberCnt
                val imgs = memberImages

                when (cnt) {
                    1 -> {
                        binding.avatarOneLayout.visibility = View.VISIBLE
                        loadImage(binding.oneUser1ImageView, imgs.getOrNull(0)?.memberImage)
                    }
                    2 -> {
                        binding.avatarTwoLayout.visibility = View.VISIBLE
                        loadImage(binding.twoUser1ImageView, imgs.getOrNull(0)?.memberImage)
                        loadImage(binding.twoUser2ImageView, imgs.getOrNull(1)?.memberImage)
                    }
                    3 -> {
                        binding.avatarThreeLayout.visibility = View.VISIBLE
                        loadImage(binding.threeUser1ImageView, imgs.getOrNull(0)?.memberImage)
                        loadImage(binding.threeUser2ImageView, imgs.getOrNull(1)?.memberImage)
                        loadImage(binding.threeUser3ImageView, imgs.getOrNull(2)?.memberImage)
                    }
                    else -> {
                        binding.avatarMultipleLayout.visibility = View.VISIBLE
                        loadImage(binding.multipleUser1ImageView, imgs.getOrNull(0)?.memberImage)
                        loadImage(binding.multipleUser2ImageView, imgs.getOrNull(1)?.memberImage)
                        binding.countTextView.text = "+${(cnt - 2).coerceAtLeast(0)}"
                    }
                }

                if(!searchViewModel.isHash){
                    binding.hashLayout.visibility = View.GONE
                } else {
                    binding.hashLayout.visibility = View.VISIBLE

                    binding.hash1Btn.visibility = View.GONE
                    binding.hash2Btn.visibility = View.GONE
                    binding.hash3Btn.visibility = View.GONE

                    hashtags.forEachIndexed { index, hashtag ->
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
                }

                binding.root.setOnClickListener {
                    searchViewModel.id = id
                    searchViewModel.getChallenge()
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

    inner class SearchChallengeAdpater(): RecyclerView.Adapter<ChallengeViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
            val view = ItemSearchChallengeRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ChallengeViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ChallengeViewHolder, position: Int) {
            viewHolder.setContents(viewHolder, position)
        }
        override fun getItemCount() = searchViewModel.searchList.size
    }
}
