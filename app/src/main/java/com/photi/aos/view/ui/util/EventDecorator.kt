package com.photi.aos.view.ui.util

import android.content.Context
import android.text.style.ForegroundColorSpan
import com.photi.aos.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class EventDecorator(context: Context, val month : Int, dates: Collection<CalendarDay>): DayViewDecorator {
    private val drawble = context.getDrawable(R.drawable.calendar_btn_selected_end)
    private val color = context.getColor(R.color.white)

    private var dates: HashSet<CalendarDay> = HashSet(dates)

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return month == day?.month && dates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        if (drawble != null && color != null ) {
            view?.setBackgroundDrawable(drawble)
            view?.addSpan(ForegroundColorSpan(color))
        }
    }
}

class TodayDecorator(context: Context, val month : Int): DayViewDecorator {
    private var date = CalendarDay.today()
    private val drawble = context.getDrawable(R.drawable.calendar_btn_selected_today)
    private val color = context.getColor(R.color.gray500)

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return month == day?.month && day?.equals(date)!!
    }

    override fun decorate(view: DayViewFacade?) {
        if (drawble != null && color != null ) {
            view?.setBackgroundDrawable(drawble)
            view?.addSpan(ForegroundColorSpan(color))
        }
    }
}

class SelectDecorator(context: Context, date: CalendarDay): DayViewDecorator {
    private val drawble = context.getDrawable(R.drawable.calendar_btn_selected_end)
    private val color = context.getColor(R.color.white)
    private val date = date

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day == date
    }

    override fun decorate(view: DayViewFacade?) {
        if (drawble != null && color != null ) {
            view?.setBackgroundDrawable(drawble)
            view?.addSpan(ForegroundColorSpan(color))
        }
    }
}