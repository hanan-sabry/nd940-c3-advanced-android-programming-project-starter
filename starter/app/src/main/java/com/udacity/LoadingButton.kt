package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var sweepAngle: Int = 0
    private var widthSize = 0
    private var heightSize = 0
    private var buttonText = resources.getString(R.string.button_name);

    private val loadingCirclePaint: Paint = Paint()
    private val buttonPaint: Paint = Paint()
    private val loadingBarPaint: Paint = Paint()
    private val textPaint: Paint = Paint()
    private val textBounds = Rect()

    var loadingDuration: Long = 1000;

    private val valueAnimator = ValueAnimator.ofInt(0, 360).apply {
        duration = loadingDuration
        addUpdateListener { valueAnimator ->
            sweepAngle = valueAnimator.animatedValue as Int
            invalidate()
        }
        repeatCount = ValueAnimator.INFINITE
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                buttonText = resources.getString(R.string.button_loading)
                valueAnimator.start()
                invalidate()
            }
            ButtonState.Completed -> {
                buttonText = resources.getString(R.string.button_name)
                valueAnimator.cancel()
                sweepAngle = 0
                invalidate()
            }
            else -> invalidate()
        }
    }


    init {
        loadingCirclePaint.style = Paint.Style.FILL
        loadingCirclePaint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        loadingCirclePaint.isAntiAlias = true

        loadingBarPaint.style = Paint.Style.FILL
        loadingBarPaint.color = ContextCompat.getColor(context, R.color.colorPrimary)

//        textPaint.textSize = 75f / resources.displayMetrics.density
        textPaint.textAlign = Paint.Align.CENTER

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            val backgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            buttonPaint.style = Paint.Style.FILL
            buttonPaint.color = backgroundColor

            val buttonTextColor = getColor(R.styleable.LoadingButton_textColor, 0)
            val buttonTextSize = getDimensionPixelSize(R.styleable.LoadingButton_textSize, 0)
            textPaint.color = buttonTextColor
            textPaint.textSize = buttonTextSize.toFloat()
        }
    }

    private val rect: RectF = RectF(0f, 0f, 0f, 0f)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //draw the button
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), buttonPaint)

        //draw the loading bar
        canvas?.drawRect(
            0f,
            0f,
            widthSize.toFloat() * sweepAngle / 360,
            heightSize.toFloat(),
            loadingBarPaint
        )


        textPaint.getTextBounds(buttonText, 0, buttonText.length, textBounds)
        val textHeight = textBounds.height()
        val textWidth = textBounds.width()

        canvas?.drawText(
            buttonText,
            widthSize.toFloat() / 2,
            (heightSize.toFloat() / 2) + (textHeight / 2),
            textPaint
        )

        val circleRadius = 75f
//        val circleLeft = (widthSize.toFloat() - widthSize / 5)
        val circleLeft = widthSize.toFloat() - textWidth/2
        val circleRight = circleLeft + circleRadius
        val circleTop = (heightSize - circleRadius) / 2
        val circleBottom = circleTop + circleRadius
        canvas?.drawArc(
            circleLeft,
            circleTop,
            circleRight,
            circleBottom,
            0f,
            sweepAngle.toFloat(),
            true,
            loadingCirclePaint
        )

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
    }

}