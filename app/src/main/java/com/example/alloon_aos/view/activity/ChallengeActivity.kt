package com.example.alloon_aos.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.model.MyData
import com.example.alloon_aos.data.model.request.HashTag
import com.example.alloon_aos.data.model.request.Rule
import com.example.alloon_aos.data.storage.TokenManager
import com.example.alloon_aos.databinding.ActivityChallengeBinding
import com.example.alloon_aos.databinding.ItemRuleChipRecyclerviewBinding
import com.example.alloon_aos.view.ui.component.dialog.JoinGuestDialog
import com.example.alloon_aos.view.ui.component.dialog.JoinGuestDialogInterface
import com.example.alloon_aos.view.ui.component.dialog.PrivateCodeDialog
import com.example.alloon_aos.view.ui.component.dialog.PrivateCodeDialogInterface
import com.example.alloon_aos.view.ui.component.dialog.RuleCardDialog
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.ChallengeViewModel

class ChallengeActivity : PrivateCodeDialogInterface, JoinGuestDialogInterface, AppCompatActivity() {
    lateinit var binding : ActivityChallengeBinding
    lateinit var id : String
    lateinit var isFrom : String
    lateinit var hashAdapter: RuleHashAdapter
    private val challengeViewModel : ChallengeViewModel by viewModels()
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_challenge)
        binding.viewModel = challengeViewModel
        challengeViewModel.resetApiResponseValue()

        val isFromHome = intent.getBooleanExtra("IS_FROM_HOME", false)
        if (isFromHome)
            isFrom = "home"

        val isFromCreate = intent.getBooleanExtra("IS_FROM_CREATE", false)
        if (isFromCreate)
            isFrom = "create"

        val isFromFeed = intent.getBooleanExtra("IS_FROM_FEED", false)
        if (isFromFeed)
            isFrom = "feed"

        val challengeId = intent.getIntExtra("ID",-1)
        val challengeData = intent.getParcelableExtra<MyData>("data")
        val imageFile = intent.getStringExtra("image")
        val isUri = intent.getBooleanExtra("isUri",false)

        challengeId?.let {
            challengeViewModel.setChallengeId(it)
        }
        challengeData?.let {
            challengeViewModel.setChallengeData(it)
        }
        imageFile?.let {
            challengeViewModel.setImgData(it)
        }
        isUri.let {
            challengeViewModel.setIsUri(it)
        }

        hashAdapter = RuleHashAdapter()
        binding.hashRecyclerview.adapter = hashAdapter
        binding.hashRecyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.hashRecyclerview.setHasFixedSize(true)

        setLayout()
        setListener()

        binding.actionBar.setNavigationIcon(R.drawable.ic_back)

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                val title = data?.getStringExtra("title")
                val photo = data?.getStringExtra("photo")
                val time = data?.getStringExtra("time")
                val goal = data?.getStringExtra("goal")
                val date = data?.getStringExtra("date")
                val rule = data?.getParcelableArrayListExtra<Rule>("rule")
                val hash = data?.getParcelableArrayListExtra<HashTag>("hash")
                val isURI = data?.getBooleanExtra("isUri",false)

                title?.let {
                    challengeViewModel.setTitleData(it)
                    binding.titleTextview.setText(it)
                }
                photo?.let { challengeViewModel.setImgData(it) }
                time?.let {
                    challengeViewModel.setTimeData(it)
                    binding.timeTextview.setText(it)
                }
                goal?.let {
                    challengeViewModel.setGoalData(it)
                    binding.goalTextview.setText(it)
                }
                date?.let {
                    challengeViewModel.setDateDate(it)
                    binding.dateTextview.setText(it)
                }
                rule?.let {
                    challengeViewModel.setRuleData(it)
                    setRuleLayout()
                }
                hash?.let {
                    challengeViewModel.setHashData(it)
                    hashAdapter.notifyDataSetChanged()
                }
                isURI?.let { challengeViewModel.setIsUri(it) }
            }
        }
    }

    private fun setLayout() {
        when(isFrom) {
            "home" -> {
                binding.title.setText(challengeViewModel.name)

                binding.memberImgLayout.visibility = View.VISIBLE
                binding.joinBtn.visibility = View.VISIBLE
                binding.createBtn.visibility = View.GONE
                binding.modifyBtn.visibility = View.GONE

                if (!challengeViewModel.isPublic)
                    challengeViewModel.getInviteCode()

                setJoinObserve()
            }
            "create" -> {
                CustomToast.createToast(this,"완성된 챌린지를 확인해볼까요? 찰칵~")?.show()

                binding.joinBtn.visibility = View.GONE
                binding.createBtn.visibility = View.VISIBLE
                binding.modifyBtn.visibility = View.GONE

                setCreateObserve()
            }
            "feed" -> {
                setModifyClick()
                binding.title.setText("수정하기")

                binding.joinBtn.visibility = View.GONE
                binding.createBtn.visibility = View.GONE
                binding.modifyBtn.visibility = View.VISIBLE

                setModifyObserve()
            }
        }
        setRuleLayout()
    }

    private fun setRuleLayout() {
        val rules = challengeViewModel.rules
        if (rules.isNotEmpty()) {
            rules.forEachIndexed { index, rule ->
                when (index) {
                    0 -> {
                        binding.ruleTextview.text = rule.rule
                        binding.divider.visibility = View.GONE
                        binding.rule2Textview.visibility = View.GONE
                        binding.allButton.visibility = View.GONE
                    }
                    1 -> {
                        binding.rule2Textview.text = rule.rule
                        binding.rule2Textview.visibility = View.VISIBLE
                        binding.divider.visibility = View.VISIBLE
                        binding.allButton.visibility = View.GONE
                    }
                    2 -> {
                        binding.allButton.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setModifyClick() {
        binding.titleTextview.setOnClickListener { startModifyTitle() }
        binding.photoLayout.setOnClickListener { startModifyPhoto() }
        binding.timeLayout.setOnClickListener { startModifyContent() }
        binding.goalLayout.setOnClickListener { startModifyContent() }
        binding.dateLayout.setOnClickListener { startModifyContent() }
        binding.ruleLayout.setOnClickListener { startModifyRule() }
    }

    private fun setListener() {
        challengeViewModel._img.observe(this) {
            Glide.with(binding.photoImageview.context)
                .load(it)
                .transform(CenterCrop(), RoundedCorners(20))
                .into(binding.photoImageview)
        }

        binding.actionBar.setNavigationOnClickListener {
            finish()
        }

        binding.allButton.setOnClickListener {
            RuleCardDialog(challengeViewModel)
                .show(this.supportFragmentManager, "CustomDialog")
        }

        binding.joinBtn.setOnClickListener {
            if(tokenManager.hasNoTokens()) {
                JoinGuestDialog(this)
                    .show(this.supportFragmentManager!!, "CustomDialog")
            } else {
                if (challengeViewModel.isPublic)
                    startGoal() //목표설정
                else
                    PrivateCodeDialog(this, challengeViewModel)
                        .show(this.supportFragmentManager!!, "CustomDialog")
            }
        }

        binding.createBtn.setOnClickListener {
            challengeViewModel.createChallenge(this)
        }

        binding.modifyBtn.setOnClickListener {
            challengeViewModel.modifyChallenge(this)
        }
    }

    fun setJoinObserve() {
        //참여하기 api
        challengeViewModel.apiResponse.observe(this) { response ->
            when (response.code) {
                "200 OK" -> {
                    CustomToast.createToast(this, "뭔가를 성공했어요.")?.show()
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

    fun setCreateObserve() {
        challengeViewModel.apiResponse.observe(this) { response ->
            when (response.code) {
                "201 CREATED" -> {
                    startFeed()
                }

                "EMPTY_FILE_INVALID" -> {
                    CustomToast.createToast(this, "비어있는 파일은 저장할 수 없습니다.")?.show()
                }

                "TOKEN_UNAUTHENTICATED" -> {
                    CustomToast.createToast(this, "승인되지 않은 요청입니다. 다시 로그인 해주세요.")?.show()
                }

                "TOKEN_UNAUTHORIZED" -> {
                    CustomToast.createToast(this, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요.")?.show()
                }

                "USER_NOT_FOUND" -> {
                    CustomToast.createToast(this, "존재하지 않는 회원입니다.")?.show()
                }

                "FILE_SIZE_EXCEED" -> {
                    CustomToast.createToast(this, "파일 사이즈는 8MB 이하만 가능합니다.")?.show()
                }

                "IMAGE_TYPE_UNSUPPORTED" -> {
                    CustomToast.createToast(this, "이미지는 '.jpeg', '.jpg', '.png', '.gif' 타입만 가능합니다.")?.show()
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

    fun setModifyObserve() {
        challengeViewModel.apiResponse.observe(this) { response ->
            when (response.code) {
                "200 OK" -> {
                    returnFeed()
                }

                "TOKEN_UNAUTHENTICATED" -> {
                    CustomToast.createToast(this, "승인되지 않은 요청입니다. 다시 로그인 해주세요.")?.show()
                }

                "TOKEN_UNAUTHORIZED" -> {
                    CustomToast.createToast(this, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요.")?.show()
                }

                "CHALLENGE_CREATOR_FORBIDDEN" -> {
                    CustomToast.createToast(this, "챌린지 파티장 권한이 없습니다.")?.show()
                }

                "CHALLENGE_MEMBER_NOT_FOUND" -> {
                    CustomToast.createToast(this, "존재하지 않는 챌린지 파티원입니다.")?.show()
                }

                "CHALLENGE_NOT_FOUND" -> {
                    CustomToast.createToast(this, "존재하지 않는 챌린지입니다.")?.show()
                }

                "FILE_SIZE_EXCEED" -> {
                    CustomToast.createToast(this, "파일 사이즈는 8MB 이하만 가능합니다.")?.show()
                }

                "IMAGE_TYPE_UNSUPPORTED" -> {
                    CustomToast.createToast(this, "이미지는 '.jpeg', '.jpg', '.png', '.gif' 타입만 가능합니다.")?.show()
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

    override fun onResultSuccess() {
        startGoal()
    }
    override fun onResultFail() {
        CustomToast.createToast(this, "초대코드가 일치하지 않아요")?.show()
    }

    override fun onClickLoginButton() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
    }

    fun startGoal() { //join
        val intent = Intent(this, GoalActivity::class.java)
        intent.putExtra("ID",challengeViewModel.id)
        intent.putExtra("TITLE",challengeViewModel.name)
        startActivity(intent)
    }

    fun startFeed() { //create
        val intent = Intent(this, FeedActivity::class.java)
        intent.putExtra("ID",challengeViewModel.id)
        intent.putExtra("data",challengeViewModel.getData())
        intent.putExtra("image",challengeViewModel.imageFile)
        startActivity(intent)
        finishAffinity()
    }

    fun returnFeed() { //modify
        val resultIntent = Intent()
        resultIntent.putExtra("ID",challengeViewModel.id)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    fun startModifyTitle() {
        val intent = Intent(this, CreateActivity::class.java)
        intent.putExtra("IS_FROM_CHALLENGE",true)
        intent.putExtra("MODIFY","title")
        intent.putExtra("titleData",challengeViewModel.name)
        activityResultLauncher.launch(intent)
    }

    fun startModifyPhoto() {
        val intent = Intent(this, CreateActivity::class.java)
        intent.putExtra("IS_FROM_CHALLENGE",true)
        intent.putExtra("MODIFY","photo")
        intent.putExtra("photoData",challengeViewModel.imageFile)
        activityResultLauncher.launch(intent)
    }

    fun startModifyContent() {
        val intent = Intent(this, CreateActivity::class.java)
        intent.putExtra("IS_FROM_CHALLENGE",true)
        intent.putExtra("MODIFY","content")
        intent.putExtra("goalData",challengeViewModel.goal)
        intent.putExtra("timeData",challengeViewModel.proveTime)
        intent.putExtra("dateData",challengeViewModel.endDate)
        activityResultLauncher.launch(intent)
    }

    fun startModifyRule() {
        val intent = Intent(this, CreateActivity::class.java)
        intent.putExtra("IS_FROM_CHALLENGE",true)
        intent.putExtra("MODIFY","rule")
        intent.putParcelableArrayListExtra("ruleData",ArrayList(challengeViewModel.rules))
        activityResultLauncher.launch(intent)
    }

    fun startModifyHash() {
        val intent = Intent(this, CreateActivity::class.java)
        intent.putExtra("IS_FROM_CHALLENGE",true)
        intent.putExtra("MODIFY","hash")
        intent.putParcelableArrayListExtra("hashData",ArrayList(challengeViewModel.hashs))
        activityResultLauncher.launch(intent)
    }


    inner class ViewHolder(private val binding: ItemRuleChipRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(pos: Int) {
            with (challengeViewModel.hashs[pos]) {
                binding.hashBtn.text = hashtag

                if (isFrom == "feed") {
                    binding.hashBtn.setOnClickListener {
                        startModifyHash()
                    }
                }
            }
        }
    }

    inner class RuleHashAdapter() : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemRuleChipRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.setContents(position)
        }

        override fun getItemCount() = challengeViewModel.hashs.size

    }
}