import android.content.res.Resources
import android.view.View
import androidx.viewpager2.widget.ViewPager2

class MemberHomeTransformer : ViewPager2.PageTransformer {
    private val scaleFactorMin = 0.8f
    private val pageMarginPx = 160.dpToPx()

    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width

        // 페이지의 크기 조정
        val scaleFactor = Math.max(scaleFactorMin, 1 - Math.abs(position) * (1 - scaleFactorMin))
        page.scaleX = scaleFactor
        page.scaleY = scaleFactor

        // 페이지의 투명도 조정
        val alphaFactor = Math.max(0.5f, 1 - Math.abs(position))
        page.alpha = alphaFactor

        // 페이지의 위치 조정
        page.translationX = -pageWidth * position + pageMarginPx * position

    }
    fun Int.dpToPx(): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (this * density).toInt()
    }
}