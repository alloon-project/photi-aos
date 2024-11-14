package com.example.alloon_aos.view.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.repository.TokenManager
import com.example.alloon_aos.databinding.ActivitySearchBinding
import com.example.alloon_aos.databinding.ItemSearchChallengeRecyclerviewBinding
import com.example.alloon_aos.databinding.ItemSearchChipRecyclerviewBinding
import com.example.alloon_aos.viewmodel.SearchViewModel
import com.google.android.material.tabs.TabLayout

class SearchActivity : AppCompatActivity() {
    lateinit var binding : ActivitySearchBinding
    private val searchViewModel : SearchViewModel by viewModels()
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)
    lateinit var hashAdapter: SearchHashAdapter
    lateinit var challengAdapter: SearchChallengeAdpater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.viewModel = searchViewModel

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
    }


    private fun setBeforeLayout() {
        binding.searchBeforeLayout.visibility = View.VISIBLE
        binding.searchAfterLayout.visibility = View.GONE
        binding.searchBtn.visibility = View.VISIBLE
        binding.deleteBtn.visibility = View.GONE
    }

    private fun setAfterLayout() {
        binding.searchBeforeLayout.visibility = View.GONE
        binding.searchAfterLayout.visibility = View.VISIBLE
        binding.searchBtn.visibility = View.GONE
        binding.deleteBtn.visibility = View.VISIBLE
        searchViewModel.addHash(binding.searchEditText.text.toString())
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
        if (searchViewModel._list.size == 0) {
            binding.challengeRecyclerview.visibility = View.GONE
            binding.noChallengeTextview.visibility = View.VISIBLE
        } else {
            binding.challengeRecyclerview.visibility = View.VISIBLE
            binding.noChallengeTextview.visibility = View.GONE
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
                when(p0?.position) {
                    0 -> { searchViewModel.isHash = false }
                    1 -> { searchViewModel.isHash = true }
                }
                challengAdapter.notifyDataSetChanged()
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
//            challengAdapter.notifyDataSetChanged()
//            setChallengeLayout()
        }
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
            with(searchViewModel._list[pos]) {
                binding.titleTextView.text = title
                binding.dateTextView.text = date

                Glide
                    .with(holder.itemView.context)
                    .load(url)
                    .into(binding.imgView)

                when (memberCount) {
                    1 -> {}
                    3 -> {
                        binding.avatarOneLayout.visibility = View.GONE
                        binding.avatarThreeLayout.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.avatarOneLayout.visibility = View.GONE
                        binding.avatarMultipleLayout.visibility = View.VISIBLE
                        binding.countTextView.text = "+"+(memberCount - 2).toString()
                    }
                }

                if(!searchViewModel.isHash){
                    binding.hashLayout.visibility = View.GONE
                } else {
                    binding.hashLayout.visibility = View.VISIBLE

                    binding.hash1Btn.visibility = View.GONE
                    binding.hash2Btn.visibility = View.GONE
                    binding.hash3Btn.visibility = View.GONE

                    hashtag.forEachIndexed { index, hashtag ->
                        when (index) {
                            0 -> {
                                binding.hash1Btn.text = hashtag
                                binding.hash1Btn.visibility = View.VISIBLE
                            }
                            1 -> {
                                binding.hash2Btn.text = hashtag
                                binding.hash2Btn.visibility = View.VISIBLE
                            }
                            2 -> {
                                binding.hash3Btn.text = hashtag
                                binding.hash3Btn.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
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
        override fun getItemCount() = searchViewModel._list.size
    }
}
