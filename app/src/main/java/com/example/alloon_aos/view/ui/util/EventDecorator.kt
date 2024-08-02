package com.example.alloon_aos.view.ui.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.style.ForegroundColorSpan
import com.example.alloon_aos.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class EventDecorator(context: Context, dates: Collection<CalendarDay>): DayViewDecorator {
    private var defaultDrawable = context.getDrawable(R.drawable.calendar_btn_selected_end)
    private var defaultColor = context.getColor(R.color.white)

    private var dates: HashSet<CalendarDay> = HashSet(dates)

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        // view에 데코레이터를 적용
        view?.setBackgroundDrawable(defaultDrawable!!)
        view?.addSpan(ForegroundColorSpan(defaultColor!!))
    }
}
