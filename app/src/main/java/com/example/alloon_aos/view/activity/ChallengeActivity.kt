package com.example.alloon_aos.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.repository.TokenManager
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
    private val challengeViewModel : ChallengeViewModel by viewModels()
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_challenge)
        binding.viewModel = challengeViewModel

        val isFromHome = intent.getBooleanExtra("IS_FROM_HOME", false)
        if (isFromHome)
            isFrom = "home"

        val isFromCreate = intent.getBooleanExtra("IS_FROM_CREATE", false)
        if (isFromCreate)
            isFrom = "create"

        val isFromFeed = intent.getBooleanExtra("IS_FROM_FEED", false)
        if (isFromFeed)
            isFrom = "feed"

        binding.actionBar.setNavigationIcon(R.drawable.ic_back)

        binding.hashRecyclerview.adapter = RuleHashAdapter()
        binding.hashRecyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.hashRecyclerview.setHasFixedSize(true)

        setLayout()
        setListener()
    }

    private fun setLayout() {
        when(isFrom) {
            "home" -> {
                binding.title.setText("")
                id = intent.getStringExtra("ID").toString()

                binding.joinBtn.visibility = View.VISIBLE
                binding.createBtn.visibility = View.GONE
                binding.modifyBtn.visibility = View.GONE
            }
            "create" -> {
                // 정보 다 받아오기
                CustomToast.createToast(this,"완성된 챌린지를 확인해볼까요? 찰칵~")?.show()

                binding.joinBtn.visibility = View.GONE
                binding.createBtn.visibility = View.VISIBLE
                binding.modifyBtn.visibility = View.GONE
            }
            "feed" -> {
                setModifyClick()

                binding.title.setText("수정하기")
                id = intent.getStringExtra("ID").toString()

                binding.joinBtn.visibility = View.GONE
                binding.createBtn.visibility = View.GONE
                binding.modifyBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun setModifyClick() {
        binding.titleTextview.setOnClickListener { startModify("title") }
        binding.photoLayout.setOnClickListener { startModify("photo") }
        binding.timeLayout.setOnClickListener { startModify("time") }
        binding.goalLayout.setOnClickListener { startModify("goal") }
        binding.ruleLayout.setOnClickListener { startModify("rule") }
        binding.dateLayout.setOnClickListener { startModify("date") }
    }

    private fun setListener() {
        binding.actionBar.setNavigationOnClickListener {
            finish()
        }

        binding.allButton.setOnClickListener {
            RuleCardDialog(challengeViewModel)
                .show(this.supportFragmentManager, "CustomDialog")
        }

        binding.joinBtn.setOnClickListener {

            JoinGuestDialog(this)
                .show(this.supportFragmentManager!!, "CustomDialog")

//            PrivateCodeDialog(this)
//                .show(this.supportFragmentManager!!, "CustomDialog")

            //startGoal()

        }

        binding.createBtn.setOnClickListener {
            startFeed()
        }

        binding.modifyBtn.setOnClickListener {
            returnFeed()
        }
    }

    override fun onClickYesButton() {
        startGoal()
    }

    override fun onClickLoginButton() {
        val intent = Intent(this, AuthActivity::class.java)
        //intent.putExtra("IS_FROM_SETTINGS_ACTIVITY",true)
        startActivity(intent)
    }

    fun startGoal() { //join
        val intent = Intent(this, GoalActivity::class.java)
        intent.putExtra("TITLE","title")
        startActivity(intent)
    }

    fun startFeed() { //create
        startActivity(Intent(this,FeedActivity::class.java))
        finishAffinity()
    }

    fun returnFeed() { //modify
        val resultIntent = Intent()
        resultIntent.putExtra("ID","id")
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    fun startModify(value: String) {
        val intent = Intent(this, CreateActivity::class.java)
        intent.putExtra("IS_FROM_CHALLENGE",true)
        intent.putExtra("MODIFY",value)
        startActivity(intent)
    }


    inner class ViewHolder(private val binding: ItemRuleChipRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(pos: Int) {
            with (challengeViewModel.hashs[pos]) {
                binding.hashBtn.text = chip

                if (isFrom == "feed") {
                    binding.hashBtn.setOnClickListener {
                        startModify("hash")
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