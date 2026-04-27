package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null
    private val imageRect = RectF()

    private var isPlaying = false

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            0, 0
        ).apply {
            try {
                val playResId = getResourceId(R.styleable.PlaybackButtonView_playImageRes, 0)
                val pauseResId = getResourceId(R.styleable.PlaybackButtonView_pauseImageRes, 0)

                playBitmap = AppCompatResources.getDrawable(context, playResId)?.toBitmap()
                pauseBitmap = AppCompatResources.getDrawable(context, pauseResId)?.toBitmap()
            } finally {
                recycle()
            }
        }
        isClickable = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val playWidth = playBitmap?.width ?: 0
        val pauseWidth = pauseBitmap?.width ?: 0
        val playHeight = playBitmap?.height ?: 0
        val pauseHeight = pauseBitmap?.height ?: 0

        val width = resolveSize(maxOf(playWidth, pauseWidth), widthMeasureSpec)
        val height = resolveSize(maxOf(playHeight, pauseHeight), heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bitmap = if (isPlaying) pauseBitmap else playBitmap
        bitmap?.let {
            canvas.drawBitmap(it, null, imageRect, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        isPlaying = !isPlaying
        invalidate()
        return true
    }

    fun setState(playing: Boolean) {
        if (isPlaying != playing) {
            isPlaying = playing
            invalidate()
        }
    }
}
