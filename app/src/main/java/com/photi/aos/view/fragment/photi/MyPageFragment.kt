package com.photi.aos.view.fragment.photi

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
import com.photi.aos.R
import com.photi.aos.databinding.FragmentMyPageBinding
import com.photi.aos.view.activity.PhotiActivity
import com.photi.aos.view.activity.SettingsActivity
import com.photi.aos.view.ui.component.dialog.EndedChallengesDialog
import com.photi.aos.view.ui.component.dialog.ProofShotByDateDialog
import com.photi.aos.view.ui.component.dialog.FeedHistoryDialog
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.ui.util.EventDecorator
import com.photi.aos.view.ui.util.TodayDecorator
import com.photi.aos.viewmodel.PhotiViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.Locale


class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private lateinit var materialCalendarView: MaterialCalendarView
    private lateinit var todayDecorator: TodayDecorator
    private var calendarList : List<CalendarDay> = emptyList()
    private var year : Int = 0
    private var month : Int = 0
    private var day : Int = 0
    private var feedCnt : Int = 0
    private var endedChallengeCnt : Int = 0


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
        setCalendarDecorator()
        setLisetener()

        setObserve()
        return binding.root
    }

    override fun onResume() {
        photiViewModel.fetchChallengeHistory()
        photiViewModel.fetchCalendarData()
        super.onResume()
    }

    private fun setObserve() {
        // API 응답 코드 관찰
        photiViewModel.code.observe(viewLifecycleOwner) { code ->
            handleApiError(code)
        }

        photiViewModel.feedCalendarData.observe(viewLifecycleOwner) {
                feedDate ->
            calendarList =  feedDate ?: emptyList()
            setupCalendarDecorators()
        }

        photiViewModel.challengeRecodData.observe(viewLifecycleOwner) {
                data ->
           if(data != null){
               binding.idTextView.text = data.username

               if(data.imageUrl != ""){
                   Glide.with(binding.userImgImageView.context)
                       .load(data.imageUrl)
                       .transform(CircleCrop())
                       .into(binding.userImgImageView)
               }

               endedChallengeCnt = data.endedChallengeCnt
               feedCnt = data.feedCnt

               binding.verifyCountTextView.text = feedCnt.toString()
               binding.challengeEndedTextView.text = endedChallengeCnt.toString()
           }

        }
        photiViewModel.feedsByDateData.observe(viewLifecycleOwner) {
                data ->
                    if(data != null)
                            ProofShotByDateDialog(data,"${year}년 ${month}월 ${day}일")
                    .show(parentFragmentManager, "ProofShotByDateDialog")

        }
    }

    private fun handleApiError(code: String) {
        val errorMessages = mapOf(
            "USER_NOT_FOUND" to "존재하지 않는 회원입니다.",
            "TOKEN_UNAUTHENTICATED" to "승인되지 않은 요청입니다. 다시 로그인 해주세요.",
            "TOKEN_UNAUTHORIZED" to "권한이 없는 요청입니다. 로그인 후 다시 시도해주세요.",
            "UNKNOWN_ERROR" to "알 수 없는 오류가 발생했습니다."
        )

        if (code == "200 OK")   return

        if (code == "IO_Exception") {
            CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
        } else {
            val message = errorMessages[code] ?: "예기치 않은 오류가 발생했습니다. ($code)"
            Log.e("MyPageFragment", "Error: $message")
        }
    }

    private fun setupCalendarDecorators() {
        val eventDecorator = EventDecorator(requireContext(), month, calendarList)
        materialCalendarView.addDecorators(todayDecorator, eventDecorator)
    }


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
            setCalendarDecorator()
        }

        materialCalendarView.setOnDateChangedListener { widget, date, selected ->

            if (materialCalendarView.currentDate.month == date.month && calendarList.contains(date)) {
                val formattedDate = String.format(Locale.US,"%04d-%02d-%02d", date.year, date.month, date.day)
                Log.d("calendar","클릭!! $formattedDate")
                day = date.day
                photiViewModel.fetchFeedsByDate(formattedDate)

            }
        }

    }

    private fun setCalendarDecorator() {
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
        setupCalendarDecorators()
    }

    fun showProofShotsDialog() {
    //   Log.d("showProofShotsDialog","feedCnt : $feedCnt")
        if(feedCnt == 0)    return

        val dialog = FeedHistoryDialog(feedCnt)
        dialog.show(activity?.supportFragmentManager!!, "ChallengeCheckInDialog")
    }

    fun showEndedChallengesDialog() {
        if(endedChallengeCnt == 0) return

        val dialog = EndedChallengesDialog(endedChallengeCnt)
        dialog.show(activity?.supportFragmentManager!!, "ChallengeCheckInDialog")
    }

    fun moveToSettingsActivity() {
        startActivity(Intent(activity, SettingsActivity::class.java))
    }
}