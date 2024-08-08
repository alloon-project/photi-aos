package com.example.alloon_aos.view.fragment.photi
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentMyPageBinding
import com.example.alloon_aos.view.activity.PhotiActivity
import com.example.alloon_aos.view.activity.SettingsActivity
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.EventDecorator
import com.example.alloon_aos.view.ui.util.TodayDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView


class MyPageFragment : Fragment() {
    private lateinit var binding : FragmentMyPageBinding
    private lateinit var materialCalendarView: MaterialCalendarView
    private lateinit var eventDecorator: EventDecorator
    private lateinit var todayDecorator: TodayDecorator
    var calendarYearMonth  = ObservableField<String>("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_page, container, false)
        binding.fragment = this
        val mActivity = activity as PhotiActivity

        setCalendarView()
        setLisetener()
        upDateCalendar()


        return binding.root
    }

    private fun setCalendarView(){
        materialCalendarView = binding.calendarview
        materialCalendarView.setTopbarVisible(false)


        materialCalendarView.state().edit()
//            .setMinimumDate(CalendarDay.from(2024, 7, 3))
//            .setMaximumDate(CalendarDay.from(2024, 9, 30))
            .commit()
    }

    private fun setLisetener(){
        materialCalendarView.setOnMonthChangedListener { widget, date ->
           upDateCalendar()
        }

        materialCalendarView.setOnDateChangedListener { widget, date, selected ->
            //CustomToast.createToast(activity,date.date.toString())?.show()

            if(materialCalendarView.currentDate.month == date.month && calendarList.contains(date))
                CustomToast.createToast(activity,"$date 가 포함되어있음")?.show()
        }

    }

    private fun upDateCalendar(){
        materialCalendarView.removeDecorators()

        val year = materialCalendarView.currentDate.year
        val month = materialCalendarView.currentDate.month

        eventDecorator = EventDecorator(requireContext(),month, calendarList)
        todayDecorator = TodayDecorator(requireContext(),month)

        materialCalendarView.addDecorators(todayDecorator,eventDecorator)
        calendarYearMonth.set("${year}년 ${month}월")
    }

    fun changeMonth(flag : Int){
        //1 : 다음달
        //2 : 이전달
        var currentDate = materialCalendarView.currentDate.date

        if(flag == 1)
            currentDate = currentDate.minusMonths(1)

        else
           currentDate = currentDate.plusMonths(1)

        materialCalendarView.setCurrentDate(currentDate)
    }

    fun moveToSettingsActivity(){
        activity?.finish()
        startActivity(Intent(activity, SettingsActivity::class.java))
    }

    companion object {
        // 사용자 피드 정보
        // 해당 날짜 미리 선택되있음
        val calendarList: HashSet<CalendarDay> = HashSet(listOf(
            CalendarDay.from(2024, 8, 14),
            CalendarDay.from(2024, 9, 1),
            CalendarDay.from(2024, 9, 11),
            CalendarDay.from(2024, 9, 12)
        ))
    }
}