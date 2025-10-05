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
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
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
     * 배경(리소스 or URL)을 지정 비율(9:16 또는 9:18)에 맞춰 '센터크롭'하여
     * 정확한 해상도로 만든 뒤, 스티커를 합성해서 단일 이미지로 저장하고 Uri 반환.
     *
     * @param targetAspectW 비율 분자 (예: 9)
     * @param targetAspectH 비율 분모 (예: 16 또는 18)
     * @param minW 최소 출력 폭(기본 720), minH 최소 출력 높이(기본 1280) → 인스타 스펙
     * @param outW/outH 명시 시 그 해상도로 출력하되, 비율은 targetAspect에 맞춰 강제 조정
     */
    suspend fun composeSingleImageBgUriExact(
        context: Context,
        @DrawableRes bgResId: Int? = null,
        bgImageUrl: String? = null,
        stickerImageUrl: String,
        // 원하는 비율 지정: 9:16 or 9:18
        targetAspectW: Int = 9,
        targetAspectH: Int = 16,
        // 출력 해상도(미지정 시 권장값 선택)
        outW: Int? = null,
        outH: Int? = null,
        // 최소치 스펙
        minW: Int = 720,
        minH: Int = 1280,
        // 스티커 UI 옵션 (기존과 동일)
        cornerDp: Float = 16f,
        circle: Boolean = false,
        @ColorRes borderColorRes: Int = R.color.green100,
        paddingPx: Int = 0,
        @DrawableRes overlayResId: Int? = null,
        overlayWidthPx: Int? = 256,
        overlayHeightPx: Int? = 50,
        overlayBottomMarginPx: Int = 94,
        // 스티커 배치 옵션
        stickerTargetWidthPx: Int? = 1080,
        stickerBottomOffsetPx: Int? = null
    ): Uri = withContext(Dispatchers.IO) {

        // 0) 목표 비율 계산 (float)
        val targetAspect = targetAspectW.toFloat() / targetAspectH.toFloat()

        // 1) 출력 해상도 결정 (비율 정확히 맞추면서 최소 크기 보장)
        val (finalW, finalH) = run {
            if (outW != null && outH != null) {
                // 사용자가 직접 지정 → 비율 강제 보정
                val userAspect = outW.toFloat() / outH.toFloat()
                if (kotlin.math.abs(userAspect - targetAspect) < 1e-4) {
                    // 거의 동일 → 그대로 사용(최소치 보정)
                    val w = maxOf(outW, minW)
                    val h = maxOf(outH, minH)
                    w to h
                } else {
                    // 비율 강제: 높이를 기준으로 폭 재계산
                    val h = maxOf(outH, minH)
                    val w = maxOf((h * targetAspect).toInt(), minW)
                    w to h
                }
            } else {
                // 미지정 → 권장값
                val defaultH = if (targetAspectH == 18) 2160 else 1920
                val defaultW = (defaultH * targetAspect).toInt()
                maxOf(defaultW, minW) to maxOf(defaultH, minH)
            }
        }

        // 2) 배경 Bitmap 로드
        val bgSrc: Bitmap = when {
            bgResId != null -> {
                BitmapFactory.decodeResource(context.resources, bgResId)
                    ?: error("Drawable decode failed: $bgResId")
            }
            bgImageUrl != null -> {
                Glide.with(context).asBitmap().load(bgImageUrl).submit().get()
            }
            else -> error("bgResId or bgImageUrl is required")
        }

      //   3) 센터크롭용 srcRect 계산 (원본에서 잘라낼 영역)
        val srcW = bgSrc.width
        val srcH = bgSrc.height
        val srcAspect = srcW.toFloat() / srcH.toFloat()

        val srcRect = if (srcAspect > targetAspect) {
            // 원본이 더 넓음 → 폭을 잘라냄
            val targetCropW = (srcH * targetAspect).toInt()
            val left = (srcW - targetCropW) / 2
            android.graphics.Rect(left, 0, left + targetCropW, srcH)
        } else {
            // 원본이 더 좁음 → 높이를 잘라냄
            val targetCropH = (srcW / targetAspect).toInt()
            val top = (srcH - targetCropH) / 2
            android.graphics.Rect(0, top, srcW, top + targetCropH)
        }

        // 4) 캔버스(정확한 비율/해상도) 생성
        val canvasBmp = Bitmap.createBitmap(finalW, finalH, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(canvasBmp)

        // 배경색은 굳이 칠할 필요 없지만, 디버그용이면 주석 해제
      //   canvas.drawColor(Color.RED)

        // 5) 배경을 캔버스 전체로 센터크롭 그리기
        val dstRect = android.graphics.Rect(0, 0, finalW, finalH)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isFilterBitmap = true
            isDither = true
        }
        canvas.drawBitmap(bgSrc, srcRect, dstRect, paint)

        // 6) 스티커 비트맵(라운드/원형+보더+overlay 포함) 생성
        val stickerBmp = buildRoundedStickerBitmap(
            context = context,
            imageUrl = stickerImageUrl,
            maxWidth = 1080,
            maxHeight = 640,
            cornerDp = cornerDp,
            circle = circle,
            borderColorRes = borderColorRes,
            paddingPx = paddingPx,
            overlayResId = null,           // ← 오버레이는 여기서 그리지 않음
            overlayWidthPx = null,
            overlayHeightPx = null,
            overlayBottomMarginPx = 0
        )

        // 7) 스티커 배치(가운데 기준, 필요 시 아래로 offset)
        val stickerDst = run {
        val targetW = stickerTargetWidthPx ?: stickerBmp.width
        val scale = targetW.toFloat() / stickerBmp.width.toFloat()
        val sw = (stickerBmp.width * scale).toInt()
        val sh = (stickerBmp.height * scale).toInt()

        val left = (finalW - sw) / 2f
        val topCenter = (finalH - sh) / 2f
        val top = stickerBottomOffsetPx?.let { topCenter + it } ?: topCenter

      RectF(left, top, left + sw, top + sh).also { dst ->
            canvas.drawBitmap(stickerBmp, null, dst, Paint(Paint.ANTI_ALIAS_FLAG))
        }
    }

        overlayResId?.let { resId ->
            val d = ContextCompat.getDrawable(context, resId) ?: return@let

            val ow = (overlayWidthPx ?: d.intrinsicWidth.coerceAtLeast(1))
            val oh = (overlayHeightPx ?: d.intrinsicHeight.coerceAtLeast(1))

            val left = (stickerDst.centerX() - ow / 2f).toInt()
            val top  = (stickerDst.bottom - overlayBottomMarginPx - oh).toInt()
            val right = left + ow
            val bottom = top + oh

            // 안전 가드
            val safeLeft = left.coerceAtLeast(0)
            val safeTop = top.coerceAtLeast(0)
            val safeRight = right.coerceAtMost(finalW)
            val safeBottom = bottom.coerceAtMost(finalH)

            d.setBounds(safeLeft, safeTop, safeRight, safeBottom)
            d.draw(canvas)
        }

        run {
            val TEXT_CONTENT = "오늘의 인증 완료!"
            val TEXT_COLOR = Color.WHITE
            val TEXT_SIZE_PX = 48f
            val MARGIN_BELOW_STICKER_PX = 20f

            val tf = ResourcesCompat.getFont(context, R.font.suit_bold)

            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = TEXT_COLOR
                textSize = TEXT_SIZE_PX
                textAlign = Paint.Align.CENTER
                typeface = tf
                isDither = true
            }

            val fm = p.fontMetrics
            val textTop = stickerDst.bottom + MARGIN_BELOW_STICKER_PX
            val baselineY = textTop - fm.ascent
            val centerX = finalW / 2f

            // 하단 넘침 방지(옵션)
            val safeBaselineY = baselineY.coerceAtMost(finalH - (-fm.descent))
            canvas.drawText(TEXT_CONTENT, centerX, safeBaselineY, p)
        }

        // 8) 저장(WebP on 30+, JPEG fallback)
        val dir = File(context.cacheDir, "ig_share").apply { mkdirs() }
        val useWebp = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        val ext = if (useWebp) "webp" else "jpg"
        val key = "ig_single_spec_${(bgResId ?: bgImageUrl.hashCode())}_${stickerImageUrl.hashCode()}_" +
                "${finalW}x${finalH}_asp${targetAspectW}x${targetAspectH}"
        val file = File(dir, "$key.$ext")

        if (!file.exists()) {
            FileOutputStream(file).use { out ->
                if (useWebp) canvasBmp.compress(Bitmap.CompressFormat.WEBP_LOSSY, 90, out)
                else canvasBmp.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }
        }

        // 메모리 정리
        if (!bgSrc.isRecycled) bgSrc.recycle()
        if (!stickerBmp.isRecycled) stickerBmp.recycle()
        if (!canvasBmp.isRecycled) canvasBmp.recycle()

        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    /** 내부: 라운드/원형+보더+overlay 적용된 스티커 비트맵 생성(파일 저장 없이 메모리에서 반환) */
    private suspend fun buildRoundedStickerBitmap(
        context: Context,
        imageUrl: String,
        maxWidth: Int,
        maxHeight: Int,
        cornerDp: Float,
        circle: Boolean,
        borderPx: Int = 24,
        @ColorRes borderColorRes: Int,
        paddingPx: Int,
        @DrawableRes overlayResId: Int?,
        overlayWidthPx: Int?,
        overlayHeightPx: Int?,
        overlayBottomMarginPx: Int
    ): Bitmap = withContext(Dispatchers.IO) {
        val original = Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .submit(maxWidth, maxHeight)
            .get()

        val src = original.copy(Bitmap.Config.ARGB_8888, false)
        val outW = src.width
        val outH = src.height
        val outBmp = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888)

        val r = RectF(paddingPx.toFloat(), paddingPx.toFloat(), (outW - paddingPx).toFloat(), (outH - paddingPx).toFloat())
        val cornerPx = dp(context, cornerDp)
        val strokeInset = borderPx / 2f

        outBmp.applyCanvas {
            val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
            val contentPaint = Paint(Paint.ANTI_ALIAS_FLAG)

            val maskInset = (paddingPx.toFloat() + borderPx.toFloat()).coerceAtLeast(0f)
            val rm = RectF(r).apply { inset(maskInset, maskInset) }
            val maskRadius = (cornerPx + strokeInset).coerceAtLeast(0f)

            val checkpoint = saveLayer(null, null)

            if (circle) drawOval(rm, maskPaint) else drawRoundRect(rm, maskRadius, maskRadius, maskPaint)

            contentPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            drawBitmap(src, null, rm, contentPaint)
            contentPaint.xfermode = null

            restoreToCount(checkpoint)

            if (borderPx > 0) {
                val resolvedBorderColor = ContextCompat.getColor(context, borderColorRes)
                val ringOuter = RectF(rm)
                val ringInner = RectF(rm).apply { inset(borderPx.toFloat(), borderPx.toFloat()) }
                val ringOuterRadius = maskRadius
                val ringInnerRadius = (maskRadius - borderPx).coerceAtLeast(0f)

                val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.FILL
                    color = resolvedBorderColor
                    isDither = true
                    isFilterBitmap = true
                }
                val path = android.graphics.Path().apply {
                    fillType = android.graphics.Path.FillType.EVEN_ODD
                    addRoundRect(ringOuter, ringOuterRadius, ringOuterRadius, android.graphics.Path.Direction.CW)
                    addRoundRect(ringInner, ringInnerRadius, ringInnerRadius, android.graphics.Path.Direction.CW)
                }
                drawPath(path, ringPaint)
            }

            overlayResId?.let { resId ->
                val d = ContextCompat.getDrawable(context, resId)
                if (d != null) {
                    val ow = (overlayWidthPx ?: d.intrinsicWidth.coerceAtLeast(1))
                    val oh = (overlayHeightPx ?: d.intrinsicHeight.coerceAtLeast(1))

                    val left = (rm.centerX() - ow / 2f).toInt()
                    val top = (rm.bottom - overlayBottomMarginPx - oh).toInt()
                    val right = left + ow
                    val bottom = top + oh

                    val safeLeft = left.coerceAtLeast(0)
                    val safeTop = top.coerceAtLeast(0)
                    val safeRight = right.coerceAtMost(outW)
                    val safeBottom = bottom.coerceAtMost(outH)

                    d.setBounds(safeLeft, safeTop, safeRight, safeBottom)
                    d.draw(this)
                }
            }
        }
        if (!src.isRecycled) src.recycle()
        outBmp

    }

    /**
     * 합성된 단일 이미지를 스토리로 공유 (mime 자동 선택)
     */
    suspend fun shareSingleImageBgAndStickerUrl(
        activity: Activity,
        context: Context,
        @DrawableRes bgResId: Int? = null,
        bgImageUrl: String? = null,
        stickerImageUrl: String,
        outW: Int = 1080,
        outH: Int = 1920,
        cornerDp: Float = 16f,
        circle: Boolean = false,
        borderPx: Int = 16,
        @ColorRes borderColorRes: Int = R.color.green100,
        paddingPx: Int = 0,
        @DrawableRes overlayResId: Int? = null,
        overlayWidthPx: Int? = null,
        overlayHeightPx: Int? = null,
        stickerBottomOffsetPx: Int? = null,
        sourceAppId: String? = null
    ) {
        val uri = composeSingleImageBgUriExact(
            context = context,
            bgResId = R.drawable.ig_bg,         // 또는 bgImageUrl = "https://..."
            stickerImageUrl = stickerImageUrl,
            targetAspectW = 9,
            targetAspectH = 16,                  // ← 9:16 강제
            // 출력 해상도를 직접 주고 싶으면:
            // outW = 1080, outH = 1920,
            // (미지정 시 1080x1920(9:16), 1080x2160(9:18) 자동)
            cornerDp = 16f,
            circle = false,
            overlayResId = overlayResId,
            overlayWidthPx = 186,
            overlayHeightPx = 36,
            stickerTargetWidthPx = 860
        )

      shareBackground(
            activity = activity,
            background = uri,
            mimeType = if (Build.VERSION.SDK_INT >= 30) "image/webp" else "image/jpeg",
            sourceAppId = sourceAppId
        )
    }


}
