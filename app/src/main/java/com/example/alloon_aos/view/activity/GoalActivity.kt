package com.example.alloon_aos.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.storage.TokenManager
import com.example.alloon_aos.databinding.ActivityGoalBinding
import com.example.alloon_aos.view.ui.util.KeyboardListener
import com.example.alloon_aos.view.ui.util.OnKeyboardVisibilityListener

class GoalActivity : AppCompatActivity() {
    lateinit var binding : ActivityGoalBinding
    lateinit var title : String
    lateinit var isFrom : String
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_goal)

        var isFromFeedActivity = intent.getBooleanExtra("IS_FROM_FEED_ACTIVITY", false)

        if (isFromFeedActivity) {
            isFrom = "feed"
            title = "소설필사하기"
        } else {
            isFrom = "join"
            title = intent.getStringExtra("TITLE").toString()
        }

        binding.title.setText(title)
        binding.actionBar.setNavigationIcon(R.drawable.ic_back)

        setListener()

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
                binding.nextBtn.setBackgroundResource(R.drawable.btn_round_primary)
                binding.nextBtn.setText(R.string.save)
                binding.nextBtn.setTextColor(this@GoalActivity.getColor(R.color.white))
            }
        })

        binding.nextBtn.setOnClickListener {
            returnResultToActivity()
        }
    }

    fun returnResultToActivity() {
        if (isFrom.equals("feed")){
            val resultIntent = Intent()
            resultIntent.putExtra("myGoal", binding.goalEdittext.text.toString())
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        } else {
            //join
            startActivity(Intent(this,FeedActivity::class.java))
            finishAffinity()
        }
    }

}