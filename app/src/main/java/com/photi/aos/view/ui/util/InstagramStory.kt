package com.photi.aos.view.ui.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.applyCanvas
import com.bumptech.glide.Glide
import com.photi.aos.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object InstagramStory {

    private const val IG_PACKAGE = "com.instagram.android"
    private const val ACTION_ADD_TO_STORY = "com.instagram.share.ADD_TO_STORY"

    /** 배경 이미지 1장만 스토리로 공유 */
    fun shareBackground(
        activity: Activity,
        background: Uri,
        mimeType: String = "image/jpeg",
        sourceAppId: String? = null
    ): Boolean {
        val i = Intent(ACTION_ADD_TO_STORY).apply {
            setPackage(IG_PACKAGE)
            setDataAndType(background, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            sourceAppId?.let { putExtra("source_application", it) }
            clipData = ClipData.newRawUri("background", background)
        }
        return startInstagram(activity, i)
    }

    /** 배경 + 스티커(투명 PNG 권장) 공유 */
    fun shareBackgroundWithSticker(
        activity: Activity,
        background: Uri?,                 // null 가능 (배경 없이 스티커만)
        sticker: Uri,
        bgMimeType: String = "image/jpeg",
        sourceAppId: String? = null,
        topColor: String? = null,
        bottomColor: String? = null
    ): Boolean {
        val i = Intent(ACTION_ADD_TO_STORY).apply {
            setPackage(IG_PACKAGE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            sourceAppId?.let { putExtra("source_application", it) }

            background?.let {
                setDataAndType(it, bgMimeType)
                // background도 ClipData로 권한 부여
                clipData = ClipData.newRawUri("background", it)
            } ?: run {
                setDataAndType(null, "image/*")
            }

            putExtra("interactive_asset_uri", sticker)
            topColor?.let { putExtra("top_background_color", it) }
            bottomColor?.let { putExtra("bottom_background_color", it) }
        }

        // 스티커 읽기 권한 명시(일부 OS에서 필요)
        activity.grantUriPermission(IG_PACKAGE, sticker, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return startInstagram(activity, i)
    }

    // ---------- 편의 메서드 (요청하신 시나리오) ----------

    /**
     * drawable(예: R.drawable.ig_bg)을 배경 PNG로,
     * 서버 이미지 URL을 스티커 PNG로 공유.
     */
    suspend fun shareDrawableBgAndStickerUrl(
        activity: Activity,
        context: Context,
        @DrawableRes bgResId: Int,      // R.drawable.ig_bg
        stickerImageUrl: String,
        sourceAppId: String? = null
    ): Boolean {
        // 배경: WEBP(30+) 또는 JPEG (캐시 재사용)
        val bgUri = drawableToWebpOrJpegContentUri(context, bgResId)

        // 스티커: PNG 640x480 (캐시 재사용)
        //val stickerUri = fetchStickerToContentUri(context, stickerImageUrl)
        val stickerUri = fetchStickerRoundedToContentUri(
            context = context,
            imageUrl = stickerImageUrl,
            outSize = 640,        // 필요 시 조절
            cornerDp =16f,       // 둥근 정도
            circle = false,       // true면 완전 원형
            borderPx = 8,         // 테두리 두께(px). 없으면 0
            borderColorRes =  R.color.green100,
            paddingPx = 0
        )

        val bgMime = if (Build.VERSION.SDK_INT >= 30) "image/webp" else "image/jpeg"
        return shareBackgroundWithSticker(
            activity = activity,
            background = bgUri,
            sticker = stickerUri,
            bgMimeType = bgMime,
            sourceAppId = sourceAppId
        )
    }



    // ---------- 저장/변환 유틸 ----------

    /** Bitmap을 캐시에 저장하고 content Uri 반환 */
    fun saveBitmapToCacheAsContentUri(
        context: Context,
        bmp: Bitmap,
        filename: String,
        jpegQuality: Int = 95,
        png: Boolean = false
    ): Uri {
        val dir = File(context.cacheDir, "ig_share").apply { mkdirs() }
        val file = File(dir, filename)
        FileOutputStream(file).use { out ->
            if (png) bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
            else bmp.compress(Bitmap.CompressFormat.JPEG, jpegQuality, out)
        }
        return FileProvider.getUriForFile(context,  "${context.packageName}.fileprovider",  file)
    }

    /** 앱 외부 파일(File) → content Uri */
    fun toContentUri(context: Context, file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    /** 서버 이미지 URL을 PNG로 캐시에 저장 → content Uri */
    suspend fun fetchPngUrlToContentUri(
        context: Context,
        imageUrl: String,
        filename: String = "ig_img_${System.currentTimeMillis()}.png"
    ): Uri = withContext(Dispatchers.IO) {
        val bmp = Glide.with(context).asBitmap().load(imageUrl).submit().get()
        val dir = File(context.cacheDir, "ig_share").apply { mkdirs() }
        val file = File(dir, filename)
        FileOutputStream(file).use { out ->
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    /** drawable 리소스를 PNG로 캐시에 저장 → content Uri */
    suspend fun drawableToPngContentUri(
        context: Context,
        @DrawableRes resId: Int,
        filename: String = "ig_bg_${System.currentTimeMillis()}.png"
    ): Uri = withContext(Dispatchers.IO) {
        // 리소스를 Bitmap으로 디코딩
        val bmp = BitmapFactory.decodeResource(context.resources, resId)
            ?: error("Drawable decode failed: $resId")
        saveBitmapToCacheAsContentUri(
            context = context,
            bmp = bmp,
            filename = filename,
            png = true
        )
    }
    private suspend fun drawableToWebpOrJpegContentUri(
        context: Context,
        @DrawableRes resId: Int,
        outW: Int = 1080,
        outH: Int = 1920
    ): Uri = withContext(Dispatchers.IO) {
        val dir = File(context.cacheDir, "ig_share").apply { mkdirs() }
        val useWebp = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        val ext = if (useWebp) "webp" else "jpg"
        // 동일 resId면 파일 재사용
        val file = File(dir, "ig_bg_res_${resId}.$ext")
        if (!file.exists()) {
            val bmp = BitmapFactory.decodeResource(context.resources, resId)
                ?: error("Drawable decode failed: $resId")
            // 사이즈 다운샘플 (속도/용량 최적화)
            val scaled = Bitmap.createScaledBitmap(bmp, outW, outH, true)
            FileOutputStream(file).use { out ->
                if (useWebp) scaled.compress(Bitmap.CompressFormat.WEBP_LOSSY, 90, out)
                else scaled.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            if (scaled !== bmp) scaled.recycle()
        }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    // 1) 배경: URL -> WEBP(JPEG 대체 가능) 1080x1920로 빠르게 저장
    suspend fun fetchBackgroundToContentUri(
        context: Context,
        imageUrl: String,
        filename: String = "ig_bg_${imageUrl.hashCode()}.webp", // 캐시 재사용
        outW: Int = 1080,
        outH: Int = 1920,
        useWebp: Boolean = true
    ): Uri = withContext(Dispatchers.IO) {
        // Glide로 다운샘플해서 비트맵 획득
        val bmp = Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .submit(outW, outH)
            .get()

        val dir = File(context.cacheDir, "ig_share").apply { mkdirs() }
        val file = File(dir, filename)
        if (!file.exists()) {
            FileOutputStream(file).use { out ->
                if (useWebp && Build.VERSION.SDK_INT >= 30) {
                    bmp.compress(Bitmap.CompressFormat.WEBP_LOSSY, 90, out) // 빠르고 용량 작음
                } else {
                    bmp.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
            }
        }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    // 2) 스티커: URL -> PNG 640x480 (투명 유지)
    suspend fun fetchStickerToContentUri(
        context: Context,
        imageUrl: String,
        filename: String = "ig_st_${imageUrl.hashCode()}.png",
        outW: Int = 640,
        outH: Int = 480
    ): Uri = withContext(Dispatchers.IO) {
        val bmp = Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .submit(outW, outH)
            .get()

        val dir = File(context.cacheDir, "ig_share").apply { mkdirs() }
        val file = File(dir, filename)
        if (!file.exists()) {
            FileOutputStream(file).use { out ->
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out) // PNG는 품질 인자 무시
            }
        }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    // ---------- 내부 ----------

    private fun startInstagram(activity: Activity, intent: Intent): Boolean {
        return try {
            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivity(intent)
                true
            } else {
                openInstagramInStore(activity); false
            }
        } catch (_: ActivityNotFoundException) {
            openInstagramInStore(activity); false
        }
    }

    private fun openInstagramInStore(activity: Activity) {
        try {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$IG_PACKAGE")
                )
            )
        } catch (_: Exception) { /* no-op */ }
    }


    private fun dp(context: Context, v: Float): Float =
        v * context.resources.displayMetrics.density

    /**
     * URL 이미지를 가져와서 둥근 라운드(또는 원형)로 마스킹한 PNG 스티커를 만든 뒤 content:// Uri 반환
     *
     * @param outSize 출력 정사각 스티커 크기(px). 스토리 스티커는 640~720 선 권장
     * @param cornerDp 모서리 반경(dp). circle=true면 무시
     * @param circle 원형으로 만들지 여부 cornerDp: Float = 16f,
     * @param borderPx 외곽선 두께(px). 0이면 없음
     * @param borderColor 외곽선 색
     * @param paddingPx 내용물 패딩(px)
     */
    suspend fun fetchStickerRoundedToContentUri(
        context: Context,
        imageUrl: String,
        outSize: Int = 640,
        cornerDp: Float = 16f,
        circle: Boolean = false,
        borderPx: Int = 0,
        @ColorRes borderColorRes: Int = R.color.green100,
        paddingPx: Int = 0
    ): Uri = withContext(Dispatchers.IO) {

        // 1) 다운샘플된 비트맵 가져오기 (정사각)
        val src = Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .submit(outSize, outSize)
            .get()

        // 2) 출력 비트맵 (투명 배경)
        val outBmp = Bitmap.createBitmap(outSize, outSize, Bitmap.Config.ARGB_8888)

        // 3) 캔버스에 마스크 -> SRC_IN 로 원본 그려서 잘라내기
        val r = RectF(
            paddingPx.toFloat(),
            paddingPx.toFloat(),
            (outSize - paddingPx).toFloat(),
            (outSize - paddingPx).toFloat()
        )
        val cornerPx = dp(context, cornerDp)

        outBmp.applyCanvas {
            val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE}
            val contentPaint = Paint(Paint.ANTI_ALIAS_FLAG)

            // 마스크 그리기
            if (circle) {
                drawOval(r, maskPaint)
            } else {
                drawRoundRect(r, cornerPx, cornerPx, maskPaint)
            }

            // 마스크와 합성 레이어
            val checkpoint = saveLayer(null, null)

            // (레이어 위에 다시 마스크를 그린 뒤)
            if (circle) {
                drawOval(r, maskPaint)
            } else {
                drawRoundRect(r, cornerPx, cornerPx, maskPaint)
            }

            // 합성 모드: SRC_IN -> 마스크 영역 안에만 비트맵 보이게
            contentPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

            // 원본을 r 영역에 맞춰 그림
            val srcRect = Rect(0, 0, src.width, src.height)
            drawBitmap(src, srcRect, r, contentPaint)

            contentPaint.xfermode = null
            restoreToCount(checkpoint)

            // 4) 외곽선(선택)
            if (borderPx > 0) {
                val resolvedBorderColor = ContextCompat.getColor(context, borderColorRes)
                val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    color = resolvedBorderColor
                    strokeWidth = borderPx.toFloat()
                }
                val inset = borderPx / 2f
                val br = RectF(r).apply { inset(inset, inset) }
                if (circle) drawOval(br, stroke) else drawRoundRect(br, cornerPx, cornerPx, stroke)
            }
        }

        // 5) PNG로 저장 후 content Uri 반환
        val dir = File(context.cacheDir, "ig_share").apply { mkdirs() }
        val file = File(dir, "ig_st_round_${imageUrl.hashCode()}_${outSize}.png")
        if (!file.exists()) {
            FileOutputStream(file).use { out ->
                outBmp.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        }
        // 원본 메모리 정리 (선택)
        if (!src.isRecycled) src.recycle()
        if (!outBmp.isRecycled) outBmp.recycle()

        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

}
