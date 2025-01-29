package com.example.alloon_aos.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.storage.TokenManager
import com.example.alloon_aos.databinding.ActivityGoalBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.KeyboardListener
import com.example.alloon_aos.view.ui.util.OnKeyboardVisibilityListener
import com.example.alloon_aos.viewmodel.GoalViewModel

class GoalActivity : AppCompatActivity() {
    lateinit var binding : ActivityGoalBinding
    lateinit var isFrom : String
    private val goalViewModel : GoalViewModel by viewModels()
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_goal)

        goalViewModel.resetApiResponseValue()

        val isFromFeedActivity = intent.getBooleanExtra("IS_FROM_FEED_ACTIVITY", false)
        if (isFromFeedActivity)
            isFrom = "feed"
        else
            isFrom = "join"

        val id = intent.getIntExtra("ID", -1)
        id.let {
            goalViewModel.id = it
        }
        val goal = intent.getStringExtra("GOAL")
        goal?.let {
            goalViewModel.goal = it
        }
        val title = intent.getStringExtra("TITLE")
        title?.let {
            binding.title.setText(it)
        }
        val code = intent.getStringExtra("Code")
        code?.let {
            goalViewModel.code = it
        }

        binding.actionBar.setNavigationIcon(R.drawable.ic_back)

        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.actionBar.setNavigationOnClickListener {
            finish()
        }

        binding.root.setOnClickListener {
            if (currentFocus != null) {
                val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible)    binding.goalEdittext.background = this@GoalActivity.getDrawable(R.drawable.input_line_focus)
                else    binding.goalEdittext.background = this@GoalActivity.getDrawable(R.drawable.input_line_default)
            }
        })

        binding.goalEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.numTextview.setText("${s!!.length}/16")
                if (s!!.isEmpty()) {
                    binding.nextBtn.setBackgroundResource(R.drawable.btn_round_quaternary)
                    binding.nextBtn.setText(R.string.skip)
                    binding.nextBtn.setTextColor(this@GoalActivity.getColor(R.color.gray600))
                    goalViewModel.goal = ""
                } else {
                    binding.nextBtn.setBackgroundResource(R.drawable.btn_round_primary)
                    binding.nextBtn.setText(R.string.save)
                    binding.nextBtn.setTextColor(this@GoalActivity.getColor(R.color.white))
                    goalViewModel.goal = s!!.toString()
                }
            }
        })

        binding.nextBtn.setOnClickListener {
            if (isFrom == "feed") {
                goalViewModel.setGoal()
            } else {
                if (goalViewModel.code.isEmpty())
                    goalViewModel.joinPublicChallenge()
                else
                    goalViewModel.joinPrivateChallenge()
            }
        }
    }

    fun setObserver() {
        goalViewModel.apiResponse.observe(this) { response ->
            when (response.code) {
                "200 OK" -> {
                    returnResultToActivity()
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

        goalViewModel.joinResponse.observe(this) { response ->
            when (response.code) {
                "200 OK" -> {
                    startToActivity()
                }
                "CHALLENGE_INVITATION_CODE_INVALID" -> {
                    CustomToast.createToast(this, "챌린지 초대 코드가 일치하지 않습니다.")?.show()
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
                "CHALLENGE_NOT_FOUND" -> {
                    CustomToast.createToast(this, "존재하지 않는 챌린지입니다.")?.show()
                }
                "EXISTING_CHALLENGE_MEMBER" -> {
                    CustomToast.createToast(this, "이미 챌린지에 참여한 회원입니다.")?.show()
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

    private fun returnResultToActivity() {
        val resultIntent = Intent()
        resultIntent.putExtra("myGoal", goalViewModel.goal)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun startToActivity() {
        val intent = Intent(this, FeedActivity::class.java)
        intent.putExtra("CHALLENGE_ID", goalViewModel.id)
        startActivity(intent)
        finishAffinity()
    }
}