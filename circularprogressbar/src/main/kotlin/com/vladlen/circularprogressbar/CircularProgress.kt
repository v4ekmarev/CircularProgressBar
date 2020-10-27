package com.vladlen.circularprogressbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import kotlin.math.min
import kotlin.math.roundToInt


class CircularProgress @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    private val startAngle = 180f      // Always start from top (default is: "3 o'clock on a watch.")
    private var sweepAngle = 0f              // How long to sweep from startAngle
    private val maxSweepAngle = 360f         // Max degrees to sweep = full circle
    private var strokeWidth = dpToPx(20)             // Width of outline
    private var marginAngle = 25
    private val maxProgress = 100             // Max progress to use
    private var isDrawText = true           // Set to true if progress text should be drawn
    private var isRoundedCorners = true     // Set to true if rounded corners should be applied to outline ends
    private var progressColorFirst = Color.GREEN   // Outline color
    private var progressColorSecond = Color.BLACK   // Outline color
    private var textColor = Color.RED       // Progress text color

    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    // Allocate paint outside onDraw to avoid unnecessary object creation

    init {
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.CircularProgress, 0, 0)
        progressColorFirst = ta.getColor(R.styleable.CircularProgress_colorFirst, 0)
        progressColorSecond = ta.getColor(R.styleable.CircularProgress_colorSecond, 0)
        isDrawText = ta.getBoolean(R.styleable.CircularProgress_colorSecond, false)
        strokeWidth = dpToPx(ta.getDimensionPixelSize(R.styleable.CircularProgress_progressWidth, 20))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initMeasurments()
        drawOutlineArcFirst(canvas)
        drawOutlineArcSecond(canvas)
        drawText(canvas)
    }

    private fun initMeasurments() {
        viewWidth = width
        viewHeight = height
    }

    private fun drawOutlineArcFirst(canvas: Canvas) {

        val diameter = min(viewWidth, viewHeight) - strokeWidth

        val outerOval = RectF(
            strokeWidth.toFloat(),
            strokeWidth.toFloat(),
            diameter.toFloat(),
            diameter.toFloat()
        )

        paint.color = progressColorFirst
        paint.strokeWidth = strokeWidth.toFloat()
        paint.isAntiAlias = true
        paint.strokeCap = if (isRoundedCorners) Paint.Cap.ROUND else Paint.Cap.BUTT
        paint.style = Paint.Style.STROKE
        canvas.drawArc(outerOval, startAngle, sweepAngle - marginAngle, false, paint)
    }

    private fun drawOutlineArcSecond(canvas: Canvas) {

        val diameter = min(viewWidth, viewHeight) - strokeWidth

        val outerOval = RectF(
            strokeWidth.toFloat(),
            strokeWidth.toFloat(),
            diameter.toFloat(),
            diameter.toFloat()
        )

        paint.color = progressColorSecond
        paint.strokeWidth = strokeWidth.toFloat()
        paint.isAntiAlias = true
        paint.strokeCap = if (isRoundedCorners) Paint.Cap.ROUND else Paint.Cap.BUTT
        paint.style = Paint.Style.STROKE
        canvas.drawArc(
            outerOval,
            startAngle + sweepAngle,
            360f - sweepAngle - marginAngle.toFloat(),
            false,
            paint
        )
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    private fun drawText(canvas: Canvas) {
        paint.textSize = Math.min(viewWidth, viewHeight) / 3.5f
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.strokeWidth = 0f
        paint.color = textColor

        // Center text
        val xPos = canvas.width / 2
        val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2).toInt()
        canvas.drawText(
            calcProgressFromSweepAngle(sweepAngle).toString(),
            xPos.toFloat(),
            yPos.toFloat(),
            paint
        )
    }

    private fun calcSweepAngleFromProgress(progress: Float): Float {
        return maxSweepAngle / maxProgress * progress
    }

    private fun calcProgressFromSweepAngle(sweepAngle: Float): Int {
        return (sweepAngle * maxProgress / maxSweepAngle).toInt()
    }

    fun setMarginAngle(marginAngle: Int) {
        this.marginAngle = marginAngle
    }

    /**
     * Set progress of the circular progress bar.
     *
     * @param progress progress between 0 and 100.
     */
    fun setProgress(progress: Float) {
        var progressLocal = progress
        if (progress >= 95) {
            progressLocal = 95f
        }
        if (progress <= 5) {
            progressLocal = 5f
        }
        sweepAngle = calcSweepAngleFromProgress(progressLocal)
        invalidate()
    }

    fun setProgressColor(colorFirst: Int, colorSecond: Int) {
        progressColorFirst = colorFirst
        progressColorSecond = colorSecond
        invalidate()
    }


    fun setProgressWidth(width: Int) {
        strokeWidth = dpToPx(width)

        invalidate()
    }

    fun setTextColor(color: Int) {
        textColor = color
        invalidate()
    }

    fun showProgressText(show: Boolean) {
        isDrawText = show
        invalidate()
    }

    /**
     * Toggle this if you don't want rounded corners on progress bar.
     * Default is true.
     *
     * @param roundedCorners true if you want rounded corners of false otherwise.
     */
    fun useRoundedCorners(roundedCorners: Boolean) {
        isRoundedCorners = roundedCorners
        invalidate()
    }
}