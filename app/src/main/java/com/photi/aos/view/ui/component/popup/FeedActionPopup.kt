package com.photi.aos.view.ui.component.popup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.photi.aos.databinding.CustomPopupMenuBinding

/**
 * 피드 액션(공유 / 삭제 / 신고)용 팝업.
 *
 * @param isMyFeed  true → '공유·삭제' / false → '신고'
 * @param listener  선택 결과 콜백
 */
class FeedActionPopup(
    private val context: Context,
    private val isMyFeed: Boolean,
    private val listener: Listener
) {

    interface Listener {
        fun onShare()        // 내 피드: 공유
        fun onDelete()       // 내 피드: 삭제
        fun onReport()       // 타인 피드: 신고
    }

    /** anchorView 위치 기준으로 팝업 표시 */
    fun show(anchorView: View) {
        val binding = CustomPopupMenuBinding.inflate(LayoutInflater.from(context))
        val popup = PopupWindow(
            binding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // ───── UI 구성 ─────
        if (isMyFeed) {
            setupMyFeed(binding, popup)
        } else {
            setupOtherFeed(binding, popup)
        }

        // ───── 위치 계산 ─────
        binding.root.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val xOffset = anchorView.width - binding.root.measuredWidth
        val yOffset = (8 * anchorView.resources.displayMetrics.density).toInt()

        popup.showAsDropDown(anchorView, xOffset, yOffset)
    }

    // ─────────────────────────────────────────── private ──
    private fun setupMyFeed(binding: CustomPopupMenuBinding, popup: PopupWindow) = with(binding) {
        optionOne.text = "공유하기"
        optionTwo.text = "피드 삭제하기"

        optionOne.setOnClickListener {
            listener.onShare()
            popup.dismiss()
        }
        optionTwo.setOnClickListener {
            listener.onDelete()
            popup.dismiss()
        }
    }

    private fun setupOtherFeed(binding: CustomPopupMenuBinding, popup: PopupWindow) = with(binding) {
        divider.visibility = View.GONE
        optionTwo.visibility = View.GONE

        optionOne.text = "피드 신고하기"
        optionOne.setOnClickListener {
            listener.onReport()
            popup.dismiss()
        }
    }
}
