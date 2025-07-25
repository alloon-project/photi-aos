package com.photi.aos.view.ui.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class RoundedCornersTransformation(
    private val radius: Float,
    private val bottomRightRadius: Float
) : BitmapTransformation() {

    private val radiusArray = FloatArray(8)

    init {
        radiusArray[0] = radius // Top-left
        radiusArray[1] = radius // Top-left
        radiusArray[2] = radius // Top-right
        radiusArray[3] = radius // Top-right
        radiusArray[4] = bottomRightRadius // Bottom-right
        radiusArray[5] = bottomRightRadius // Bottom-right
        radiusArray[6] = radius // Bottom-left
        radiusArray[7] = radius // Bottom-left
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(radius.toString().toByteArray())
        messageDigest.update(bottomRightRadius.toString().toByteArray())
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val bitmap = pool.get(toTransform.width, toTransform.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL

        val path = Path()
        val rect = RectF(0f, 0f, toTransform.width.toFloat(), toTransform.height.toFloat())

        path.addRoundRect(rect, radiusArray, Path.Direction.CW)
        canvas.clipPath(path)

        canvas.drawBitmap(toTransform, 0f, 0f, paint)

        return bitmap
    }
}
