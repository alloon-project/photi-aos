package com.example.alloon_aos.view.fragment.photi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentMyPageBinding
import com.example.alloon_aos.view.activity.PhotiActivity
import com.example.alloon_aos.view.activity.SettingsActivity
import com.example.alloon_aos.view.ui.component.dialog.EndedChallengesDialog
import com.example.alloon_aos.view.ui.component.dialog.ProofShotByDateDialog
import com.example.alloon_aos.view.ui.component.dialog.ProofShotsGalleryDialog
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.EventDecorator
import com.example.alloon_aos.view.ui.util.TodayDecorator
import com.example.alloon_aos.viewmodel.PhotiViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.Locale


class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private lateinit var materialCalendarView: MaterialCalendarView
    private lateinit var eventDecorator: EventDecorator
    private lateinit var todayDecorator: TodayDecorator
    private lateinit var calendarList : List<CalendarDay>
    private var year : Int = 0
    private var month : Int = 0

    var calendarYearMonth = ObservableField<String>("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_page, container, false)
        binding.fragment = this
        binding.viewModel = photiViewModel
        val mActivity = activity as PhotiActivity

        setCalendarView()
        setLisetener()
        initCalendar()

        //observeUserProfile()
        //observeChallenges()
        //observeChallengeRecordData()
        setObserve()
        //photiViewModel.fetchChallengeHistory()
        photiViewModel.fetchCalendarData()
        return binding.root
    }

    private fun observeUserProfile() {
        photiViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                it.data.let { userData ->
                    binding.idTextView.text = userData.username
                    Glide.with(binding.userImgImageView.context)
                        .load(userData.imageUrl)
                        .transform(CircleCrop())
                        .into(binding.userImgImageView)
                }
            }
        }
    }

    private fun setObserve() {
        // API 응답 코드 관찰
        photiViewModel.code.observe(viewLifecycleOwner) { code ->
            when (code) {
                "200 OK" -> {

                }

                "USER_NOT_FOUND" -> {
                    Log.e("MyPageFragment", "Error: USER_NOT_FOUND - 존재하지 않는 회원입니다.")
                }

                "TOKEN_UNAUTHENTICATED" -> {
                    Log.e(
                        "MyPageFragment",
                        "Error: TOKEN_UNAUTHENTICATED - 승인되지 않은 요청입니다. 다시 로그인 해주세요."
                    )
                }

                "TOKEN_UNAUTHORIZED" -> {
                    Log.e(
                        "MyPageFragment",
                        "Error: TOKEN_UNAUTHORIZED - 권한이 없는 요청입니다. 로그인 후 다시 시도해주세요."
                    )
                }


                "IO_Exception" -> {
                    CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }

                "UNKNOWN_ERROR" -> {
                    Log.e("MyPageFragment", "Error: UNKNOWN_ERROR - 알 수 없는 오류가 발생했습니다.")
                }

                else -> {
                    Log.e("MyPageFragment", "Error: $code - 예기치 않은 오류가 발생했습니다.")
                }
            }
        }



        photiViewModel.feedCalendarData.observe(viewLifecycleOwner) {
                feedDate ->
             calendarList = feedDate ?: emptyList()
            eventDecorator = EventDecorator(requireContext(), month, calendarList)
            materialCalendarView.addDecorators(todayDecorator,eventDecorator)
        }

        photiViewModel.challengeRecodData.observe(viewLifecycleOwner) {
                data ->
           if(data != null){
               binding.idTextView.text = data.username

               Glide.with(binding.userImgImageView.context)
                   .load(data.imageUrl)
                   .transform(CircleCrop())
                   .into(binding.userImgImageView)

               binding.verifyCountTextView.text = data.feedCnt.toString()
               binding.challengeEndedTextView.text = data.endedChallengeCnt.toString()
           }

        }
        photiViewModel.feedsByDateData.observe(viewLifecycleOwner) {
                data ->
                    if(data != null)
                            ProofShotByDateDialog(data)
                    .show(parentFragmentManager, "ProofShotByDateDialog")

        }
    }


//    private fun observeChallenges() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                photiViewModel.challenges.collectLatest { challenges ->
//                    challenges?.let {
//
//                        it.data.let { challengeList ->
//                            binding.challengeCountTextView.text =
//                                "Challenges: ${challengeList.size}"
//                        }
//                    }
//                }
//            }
//        }
//    }

    private fun setCalendarView() {
        materialCalendarView = binding.calendarview
        materialCalendarView.setTopbarVisible(false)

        val koWeekDays = listOf("일", "월", "화", "수", "목", "금", "토")
        materialCalendarView.setWeekDayFormatter { dayOfWeek ->
            koWeekDays[dayOfWeek.ordinal]
        }
        materialCalendarView.state().edit()
            // 필요 시 최대 최소 날짜 설정
//            .setMinimumDate(CalendarDay.from(2024, 7, 3))
//            .setMaximumDate(CalendarDay.from(2024, 9, 30))
            .commit()
    }

    private fun setLisetener() {
        materialCalendarView.setOnMonthChangedListener { widget, date ->
            initCalendar()
        }

        materialCalendarView.setOnDateChangedListener { widget, date, selected ->

            if (materialCalendarView.currentDate.month == date.month && calendarList.contains(date)) {


                val formattedDate = String.format(Locale.US,"%04d-%02d-%02d", date.year, date.month, date.day)
                Log.d("calendar","클릭!! $formattedDate")
                photiViewModel.fetchFeedsByDate(formattedDate)

            }
        }

    }

    private fun initCalendar() {
        materialCalendarView.removeDecorators()

         year = materialCalendarView.currentDate.year
         month = materialCalendarView.currentDate.month

        todayDecorator = TodayDecorator(requireContext(), month)

        materialCalendarView.addDecorators(todayDecorator)
        calendarYearMonth.set("${year}년 ${month}월")
    }

    fun changeMonth(flag: Int) {
        //1 : 다음달
        //2 : 이전달
        var currentDate = materialCalendarView.currentDate.date

        if (flag == 1)
            currentDate = currentDate.minusMonths(1)
        else
            currentDate = currentDate.plusMonths(1)

        materialCalendarView.setCurrentDate(currentDate)
    }

    fun showProofShotsDialog() {
        //if(인증 횟수가 0이 아니면)
        //총 인증 횟수와 챌린지 Id 넘겨줌
        val dialog = ProofShotsGalleryDialog(5, 1)
        dialog.show(activity?.supportFragmentManager!!, "ChallengeCheckInDialog")
    }

    fun showEndedChallengesDialog() {
        //if(종료 횟수가 0이 아니면)
        val dialog = EndedChallengesDialog(5, 1)
        dialog.show(activity?.supportFragmentManager!!, "ChallengeCheckInDialog")
    }

    fun moveToSettingsActivity() {
        startActivity(Intent(activity, SettingsActivity::class.java))
    }
}