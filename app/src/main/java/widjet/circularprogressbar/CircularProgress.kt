package widjet.circularprogressbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View


class CircularProgress : View {

    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0

    private val mStartAngle = 180f      // Always start from top (default is: "3 o'clock on a watch.")
    private var mSweepAngle = 0f              // How long to sweep from mStartAngle
    private val mMaxSweepAngle = 360f         // Max degrees to sweep = full circle
    private var mStrokeWidth = dpToPx(20)             // Width of outline
    private var mMarginAngle = 25
    private val mAnimationDuration = 400       // Animation duration for progress change
    private val mMaxProgress = 100             // Max progress to use
    private var mDrawText = true           // Set to true if progress text should be drawn
    private var mRoundedCorners = true     // Set to true if rounded corners should be applied to outline ends
    private var mProgressColorFirst = Color.GREEN   // Outline color
    private var mProgressColorSecond = Color.BLACK   // Outline color
    private var mTextColor = Color.RED       // Progress text color

    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    // Allocate paint outside onDraw to avoid unnecessary object creation

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val attrCircle = context.obtainStyledAttributes(attrs, R.styleable.CircularProgress)
        val N = attrCircle.indexCount
        for (i in 0 until N) {
            val attr = attrCircle.getIndex(i)
            when (attr) {
                R.styleable.CircularProgress_colorFirst -> {
                    mProgressColorFirst = attrCircle.getColor(attr, Color.GREEN)
                }

                R.styleable.CircularProgress_colorSecond -> {
                    mProgressColorSecond = attrCircle.getColor(attr, Color.BLACK)
                }

                R.styleable.CircularProgress_showProgressText -> {
                    mDrawText = attrCircle.getBoolean(attr, false)
                }

                R.styleable.CircularProgress_progressWidth -> {
                    mStrokeWidth = dpToPx(attrCircle.getDimensionPixelSize(attr, 20))
                }
            }
        }
        attrCircle.recycle()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initMeasurments()
        drawOutlineArcFirst(canvas)
        drawOutlineArcSecond(canvas)
        drawText(canvas)
    }

    private fun initMeasurments() {
        mViewWidth = width
        mViewHeight = height
    }

    private fun drawOutlineArcFirst(canvas: Canvas) {

        val diameter = Math.min(mViewWidth, mViewHeight) - mStrokeWidth

        val outerOval = RectF(mStrokeWidth.toFloat(), mStrokeWidth.toFloat(), diameter.toFloat(), diameter.toFloat())

        mPaint.color = mProgressColorFirst
        mPaint.strokeWidth = mStrokeWidth.toFloat()
        mPaint.isAntiAlias = true
        mPaint.strokeCap = if (mRoundedCorners) Paint.Cap.ROUND else Paint.Cap.BUTT
        mPaint.style = Paint.Style.STROKE
        canvas.drawArc(outerOval, mStartAngle, mSweepAngle - mMarginAngle, false, mPaint)
    }

    private fun drawOutlineArcSecond(canvas: Canvas) {

        val diameter = Math.min(mViewWidth, mViewHeight) - mStrokeWidth

        val outerOval = RectF(mStrokeWidth.toFloat(), mStrokeWidth.toFloat(), diameter.toFloat(), diameter.toFloat())

        mPaint.color = mProgressColorSecond
        mPaint.strokeWidth = mStrokeWidth.toFloat()
        mPaint.isAntiAlias = true
        mPaint.strokeCap = if (mRoundedCorners) Paint.Cap.ROUND else Paint.Cap.BUTT
        mPaint.style = Paint.Style.STROKE
        canvas.drawArc(outerOval, mStartAngle + mSweepAngle, 360f - mSweepAngle - mMarginAngle.toFloat(), false, mPaint)
    }

    fun dpToPx(dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    private fun drawText(canvas: Canvas) {
        mPaint.textSize = Math.min(mViewWidth, mViewHeight) / 3.5f
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        mPaint.strokeWidth = 0f
        mPaint.color = mTextColor

        // Center text
        val xPos = canvas.width / 2
        val yPos = (canvas.height / 2 - (mPaint.descent() + mPaint.ascent()) / 2).toInt()
        canvas.drawText(calcProgressFromSweepAngle(mSweepAngle).toString(), xPos.toFloat(), yPos.toFloat(), mPaint)
    }

    private fun calcSweepAngleFromProgress(progress: Float): Float {
        return mMaxSweepAngle / mMaxProgress * progress
    }

    private fun calcProgressFromSweepAngle(sweepAngle: Float): Int {
        return (sweepAngle * mMaxProgress / mMaxSweepAngle).toInt()
    }

    fun setMarginAngle(marginAngle: Int) {
        this.mMarginAngle = marginAngle
    }

    /**
     * Set progress of the circular progress bar.
     *
     * @param progress progress between 0 and 100.
     */
    fun setProgress(progress: Float) {
        var progress = progress
        if (progress >= 95) {
            progress = 95f
        }
        if (progress <= 5) {
            progress = 5f
        }
        mSweepAngle = calcSweepAngleFromProgress(progress)
        invalidate()
    }

    fun setProgressColor(colorFirst: Int, colorSecond: Int) {
        mProgressColorFirst = colorFirst
        mProgressColorSecond = colorSecond
        invalidate()
    }


    fun setProgressWidth(width: Int) {
        mStrokeWidth = dpToPx(width)

        invalidate()
    }

    fun setTextColor(color: Int) {
        mTextColor = color
        invalidate()
    }

    fun showProgressText(show: Boolean) {
        mDrawText = show
        invalidate()
    }

    /**
     * Toggle this if you don't want rounded corners on progress bar.
     * Default is true.
     *
     * @param roundedCorners true if you want rounded corners of false otherwise.
     */
    fun useRoundedCorners(roundedCorners: Boolean) {
        mRoundedCorners = roundedCorners
        invalidate()
    }
}