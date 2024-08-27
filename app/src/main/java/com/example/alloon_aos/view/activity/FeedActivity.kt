package com.example.alloon_aos.view.activity

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ActivityFeedBinding
import com.example.alloon_aos.databinding.ActivitySettingsBinding
import com.example.alloon_aos.view.fragment.feed.ViewFeedFragment
import com.example.alloon_aos.view.fragment.photi.ChallengeCommendFragment
import com.google.android.material.tabs.TabLayout

class FeedActivity : AppCompatActivity() {
    lateinit var binding : ActivityFeedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_feed)
        binding.activity = this

        if (savedInstanceState == null) {
            // 처음에 표시할 프래그먼트
            val initialFragment = ViewFeedFragment()

            // FragmentManager를 통해 프래그먼트 트랜잭션 시작
            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_layout, initialFragment)  // 프래그먼트 컨테이너 뷰의 ID와 프래그먼트 인스턴스
                .commit()
        }

        val tabLayout = binding.tabLayout

        val tabTitles = listOf("피드", "소개", "파티원")

//        tabTitles.forEach { title ->
//            val tab = tabLayout.newTab()
//            tab.customView = createTabView(title)
//            tabLayout.addTab(tab)
//        }

        var viewFeedTab = ViewFeedFragment()
       // val introduceTab = IntroduceFragment()
       // val partyTab = PartyFragment()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()

                if(viewFeedTab.isAdded){
                    fragmentTransaction.remove(viewFeedTab)
                    viewFeedTab = ViewFeedFragment()
                }

                when(tab?.position){
                    0 ->{
                        fragmentTransaction.add(R.id.frag_layout,viewFeedTab).commit()
                    }
//                    1 -> {
//                        fragmentTransaction.add(R.id.frag_layout, introduceTab).commit()
//                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //updateTabView(tab, isSelected = false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }


//    private fun createTabView(title: String): View {
//        val view = LayoutInflater.from(this).inflate(R.layout.custom_tab, null)
//        val textView = view.findViewById<TextView>(R.id.tab_title)
//        textView.text = title
//        return view
//    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun applyBlurEffect(view: View?) {
        val blurEffect = RenderEffect.createBlurEffect(10f, 10f, Shader.TileMode.CLAMP)
        view?.setRenderEffect(blurEffect)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun removeBlurEffect(view: View?) {
        view?.setRenderEffect(null)  // 블러 효과 제거
    }

    private fun updateTabView(tab: TabLayout.Tab?, isSelected: Boolean) {
        val view = tab?.customView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (isSelected) {
                removeBlurEffect(view)
            } else {
                applyBlurEffect(view)
            }
        }
    }

    fun finishActivity(){
        finish()
    }
}