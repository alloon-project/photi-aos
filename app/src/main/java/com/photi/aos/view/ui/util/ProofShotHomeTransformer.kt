import android.content.res.Resources
import android.view.View
import androidx.viewpager2.widget.ViewPager2

class ProofShotHomeTransformer : ViewPager2.PageTransformer {
    private val pageMarginPx = 296.dpToPx()

    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width

        // 페이지의 위치 조정
        page.translationX = -pageWidth * position + pageMarginPx * position

    }
    fun Int.dpToPx(): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (this * density).toInt()
    }
}