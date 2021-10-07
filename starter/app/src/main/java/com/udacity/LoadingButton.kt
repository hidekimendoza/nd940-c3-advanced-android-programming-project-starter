package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlin.math.min
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var padding = 0f

    private var currentText: String = ""

    private val valueAnimator = ValueAnimator()


    private val messageRect = Rect()

    private val backgroundRect = Rect()

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val loadingArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.YELLOW
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        Log.i("LoadingButton", buttonState.toString())
        when (new) {
            ButtonState.Loading -> {
                currentText = context.getString(R.string.downloading)

            }
            ButtonState.Completed -> {
                currentText = context.getString(R.string.download)

            }
            else -> {
                currentText = "blah"
            }
        }
        invalidate()
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }


    init {
        isClickable = true
    }


    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)
        // Draw background
        canvas?.drawRect(backgroundRect, backgroundPaint)
        // Draw text
        canvas?.drawText(
            currentText,
            widthSize / 2f,
            ((heightSize + textPaint.textSize) / 2f),
            textPaint
        )

        if (buttonState == ButtonState.Loading) {
            // Draw arc
            textPaint.getTextBounds(currentText, 0, currentText.length, messageRect)
            canvas?.drawArc(
                widthSize / 2f + messageRect.width() / 2f + padding,
                padding,
                widthSize / 2f + messageRect.width() / 2f + padding + min(widthSize, heightSize),
                heightSize.toFloat() - padding,
                0f,
                360f,
                true,
                loadingArcPaint
            )
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
        backgroundRect.set(0, 0, widthSize, heightSize)
        textPaint.textSize = heightSize * .4f
        padding = if (heightSize < widthSize) heightSize * 0.1f else widthSize * 0.1f

    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        invalidate()
        return true
    }
}