package com.example.alloon_aos.view.fragment.userprofile
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentProfileBinding
import com.example.alloon_aos.view.activity.PhotiActivity
import com.example.alloon_aos.view.ui.util.EventDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView


class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private lateinit var materialCalendarView: MaterialCalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.fragment = this
        val mActivity = activity as PhotiActivity

        materialCalendarView = binding.calendarview
        materialCalendarView.setTopbarVisible(false)

        materialCalendarView.state().edit()
            .setMinimumDate(CalendarDay.from(2024, 7, 3))
            .setMaximumDate(CalendarDay.from(2024, 9, 30))
            .commit()

        val calendar = CalendarDay.from(2024,9,1)
        val calendar1 = CalendarDay.from(2024,9,11)
        //해당 날짜 여러개 선택되어 있게 해줌
//        materialCalendarView.setDateSelected(calendar,true)
//        materialCalendarView.setDateSelected(calendar1,true)

//        materialCalendarView.setSelectedDate( //처음 선택되어 있는 날자
//            LocalDate.of(2024,9,8))

        //해당 날짜 미리 선택되있음
        val calendarList = ArrayList<CalendarDay>()
        calendarList.add(calendar)
        calendarList.add(calendar1)


        val eventDecorator : EventDecorator = EventDecorator(
            requireContext(),
            calendarList,
        )

        // 캘린더에 보여지는 Month가 변경된 경우
        materialCalendarView.setOnMonthChangedListener { widget, date ->
            materialCalendarView.addDecorators(EventDecorator(requireContext(),calendarList))
            println("date : ${date.year}.${date.month}.${date.day} : ${date.date} : ${date}")
      }

        materialCalendarView.setOnDateChangedListener { widget, date, selected -> //ListView starts here
            println(date.date)
        }

        //달력 옮김
        materialCalendarView.setCurrentDate(CalendarDay.from(2024,9,30))

        //지금달력 출력
        println("${materialCalendarView.currentDate}")

        //getSelectedDate도 있d음




        materialCalendarView.setOnDateChangedListener { widget, date, selected ->
            println(date.date)
        }
        return binding.root
    }

}