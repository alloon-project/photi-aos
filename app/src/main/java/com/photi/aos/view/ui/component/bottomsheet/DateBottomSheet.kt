package com.photi.aos.view.ui.component.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.photi.aos.R
import com.photi.aos.databinding.BottomsheetDateBinding
import com.photi.aos.view.ui.util.SelectDecorator
import com.photi.aos.view.ui.util.TodayDecorator
import com.photi.aos.viewmodel.CreateViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

interface DateBottomSheetInterface {
    fun onClickSelectDateButton(date: CalendarDay)
}

class DateBottomSheet (val mContext: Context, val createViewModel: CreateViewModel, val dateBottomSheetInterface: DateBottomSheetInterface) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetDateBinding? = null
    private val binding get() = _binding!!

    private lateinit var materialCalendarView: MaterialCalendarView
    private lateinit var todayDecorator: TodayDecorator
    private lateinit var selectDecorator: SelectDecorator

    private var selectDate: CalendarDay? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomsheetDateBinding.inflate(inflater, container, false)
        val view = binding.root

        setCalendarView()
        setListener()
        upDateCalendar()

        return view
    }

    private fun setListener() {
        binding.backImgBtn.setOnClickListener {
            changeMonth(1)
        }

        binding.forwardImgBtn.setOnClickListener {
            changeMonth(2)
        }

        materialCalendarView.setOnMonthChangedListener { widget, date ->
            upDateCalendar()
        }

        materialCalendarView.setOnDateChangedListener { widget, date, selected ->
            if (selectDate != null)
                materialCalendarView.removeDecorator(selectDecorator)

            selectDate = date

            selectDecorator = SelectDecorator(requireContext(), date)
            materialCalendarView.addDecorators(selectDecorator)

            binding.selectBtn.isEnabled = true
            binding.selectBtn.setText("종료일 고르기")
        }

        binding.selectBtn.setOnClickListener {
            dateBottomSheetInterface.onClickSelectDateButton(selectDate!!)
            dismiss()
        }

        binding.closeBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun setCalendarView() {
        materialCalendarView = binding.calendarview
        materialCalendarView.setTopbarVisible(false)

        materialCalendarView.state().edit()
            .setMinimumDate(CalendarDay.today())
            .commit()
    }

    private fun upDateCalendar() {
        materialCalendarView.removeDecorators()
        selectDate = null
        binding.selectBtn.isEnabled = false

        val year = materialCalendarView.currentDate.year
        val month = materialCalendarView.currentDate.month

        todayDecorator = TodayDecorator(requireContext(),month)

        materialCalendarView.addDecorators(todayDecorator)
        binding.monthTextView.setText("${year}년 ${month}월")
    }

    fun changeMonth(flag : Int) {
        //1 : 다음달
        //2 : 이전달
        var currentDate = materialCalendarView.currentDate.date

        if(flag == 1)
            currentDate = currentDate.minusMonths(1)

        else
            currentDate = currentDate.plusMonths(1)

        materialCalendarView.setCurrentDate(currentDate)
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window?.let { window ->
            val params = window.attributes
            params.dimAmount = 0.4f
            window.attributes = params
        }
    }

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

}