import android.content.res.Resources
import android.view.View
import androidx.viewpager2.widget.ViewPager2

class ProofShotHomeTransformer : ViewPager2.PageTransformer {
    private val pageMarginPx = 16.dpToPx()

    override fun transformPage(page: View, position: Float) {
        page.translationX = -pageMarginPx * position
    }

    fun Int.dpToPx(): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (this * density).toInt()
    }
}