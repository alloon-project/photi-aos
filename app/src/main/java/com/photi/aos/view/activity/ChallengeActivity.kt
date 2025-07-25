package com.photi.aos.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.photi.aos.MyApplication
import com.photi.aos.R
import com.photi.aos.data.model.MyData
import com.photi.aos.data.model.request.HashTag
import com.photi.aos.data.model.request.Rule
import com.photi.aos.data.storage.TokenManager
import com.photi.aos.databinding.ActivityChallengeBinding
import com.photi.aos.databinding.ItemRuleChipRecyclerviewBinding
import com.photi.aos.view.ui.component.dialog.JoinGuestDialog
import com.photi.aos.view.ui.component.dialog.JoinGuestDialogInterface
import com.photi.aos.view.ui.component.dialog.PrivateCodeDialog
import com.photi.aos.view.ui.component.dialog.PrivateCodeDialogInterface
import com.photi.aos.view.ui.component.dialog.RuleCardDialog
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.viewmodel.ChallengeViewModel

class ChallengeActivity : PrivateCodeDialogInterface, JoinGuestDialogInterface, AppCompatActivity() {
    lateinit var binding : ActivityChallengeBinding
    lateinit var id : String
    lateinit var isFrom : String
    lateinit var hashAdapter: RuleHashAdapter
    private lateinit var privateCodeDialog: PrivateCodeDialog
    private val challengeViewModel : ChallengeViewModel by viewModels()
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var startForResult: ActivityResultLauncher<Intent>

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

        privateCodeDialog = PrivateCodeDialog(this, challengeViewModel)
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

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getBooleanExtra("IS_FROM_LOGIN",false)
                if(data == true) {
                    val id = result.data?.getStringExtra("id")
                    CustomToast.createToast(this, "${id}님 환영합니다!")?.show()
                }
            }
        }
    }

    private fun setLayout() {
        when(isFrom) {
            "home" -> {
                binding.title.setText(challengeViewModel.name)

                binding.memberImgLayout.visibility = View.VISIBLE
                setMemberImg()

                binding.joinBtn.visibility = View.VISIBLE
                binding.createBtn.visibility = View.GONE
                binding.modifyBtn.visibility = View.GONE

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

    private fun setMemberImg() {
        val cnt = challengeViewModel.currentMemberCnt
        val imgs = challengeViewModel.memberImages

        when (cnt) {
            1 -> {
                binding.avatarOneLayout.visibility = View.VISIBLE
                binding.membernumTextview.text = "1명 합류"
                loadImage(binding.oneUser1ImageView, imgs.getOrNull(0)?.memberImage)
            }
            2 -> {
                binding.avatarTwoLayout.visibility = View.VISIBLE
                binding.membernumTextview.text = "2명 합류"
                loadImage(binding.twoUser1ImageView, imgs.getOrNull(0)?.memberImage)
                loadImage(binding.twoUser2ImageView, imgs.getOrNull(1)?.memberImage)
            }
            3 -> {
                binding.avatarThreeLayout.visibility = View.VISIBLE
                binding.membernumTextview.text = "3명 합류"
                loadImage(binding.threeUser1ImageView, imgs.getOrNull(0)?.memberImage)
                loadImage(binding.threeUser2ImageView, imgs.getOrNull(1)?.memberImage)
                loadImage(binding.threeUser3ImageView, imgs.getOrNull(2)?.memberImage)
            }
            else -> {
                binding.avatarMultipleLayout.visibility = View.VISIBLE
                binding.membernumTextview.text = "${cnt}명 합류"
                loadImage(binding.multipleUser1ImageView, imgs.getOrNull(0)?.memberImage)
                loadImage(binding.multipleUser2ImageView, imgs.getOrNull(1)?.memberImage)
                binding.countTextView.text = "+${(cnt - 2).coerceAtLeast(0)}"
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
                    startGoal()
                else {
                    privateCodeDialog.show(this.supportFragmentManager!!, "CustomDialog")
                }
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
        challengeViewModel.joinResponse.observe(this) { response ->
            when (response.code) {
                "200 OK" -> {
                    if (response.action.equals("true")) {
                        privateCodeDialog.returnSuccess()
                        startGoal()
                    } else {
                        privateCodeDialog.returnFail()
                        CustomToast.createToast(this, "초대코드가 일치하지 않아요.")?.show()
                    }
                }
                "CHALLENGE_NOT_FOUND" -> {
                    privateCodeDialog.returnFail()
                    CustomToast.createToast(this, "존재하지 않는 챌린지입니다.")?.show()
                }
                "IO_Exception" -> {
                    privateCodeDialog.returnFail()
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

    override fun onClickMatchBtn() {
        challengeViewModel.matchInviteCode()
    }

    override fun onClickLoginButton() {
        val intent = Intent(this, AuthActivity::class.java)
        startForResult.launch(intent)
    }

    fun startGoal() { //join
        val intent = Intent(this, GoalActivity::class.java)
        intent.putExtra("ID",challengeViewModel.id)
        intent.putExtra("TITLE",challengeViewModel.name)
        startActivity(intent)
    }

    fun startFeed() { //create
        val intent = Intent(this, FeedActivity::class.java)
        intent.putExtra("CHALLENGE_ID",challengeViewModel.id)
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