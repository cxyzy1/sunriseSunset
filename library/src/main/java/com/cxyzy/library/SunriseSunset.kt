package com.cxyzy.library

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import java.lang.Math.PI
import java.lang.Math.sin

class SunriseSunset constructor(
    private val mContext: Context,
    attrs: AttributeSet
) : View(mContext, attrs) {
    private val DEFAULT_SIZE = 300f
    private val mDefaultSize: Int
    private val sweepAngle = 140
    private val starAngle = 200 - (sweepAngle - 140) / 2
    private val halfRemainAngle = (180 - sweepAngle) / 2 / 180 * PI
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private lateinit var mCenterPoint: Point

    private lateinit var mArcPaint: Paint
    private lateinit var mRectF: RectF
    private var mRadius: Float = 0.toFloat()
    private var lineYLocate: Float = 0.toFloat()
    private lateinit var mAnimator: ObjectAnimator
    private lateinit var mSunPaint: Paint
    private lateinit var mTimePaint: Paint

    private var lineColor: Int = 0
    private var sunColor: Int = 0
    private var timeTextColor: Int = 0
    private var timeTextSize: Float = 0.toFloat()
    private var mSunriseTime = "6:00"
    private var mSunsetTime = "19:00"

    private lateinit var mSunriseBitmap: Bitmap
    private lateinit var mSunsetBitmap: Bitmap
    private var percent = 0f
    private var mTotalTime: Int = 0
    private var isSetTime = false
    private var mNowTime: Int = 0

    init {
        mDefaultSize = dipToPx(mContext, DEFAULT_SIZE)//默认300
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.SunriseSunset)
            lineColor = typedArray.getColor(R.styleable.SunriseSunset_lineColor, Color.WHITE)
            sunColor = typedArray.getColor(R.styleable.SunriseSunset_sunColor, Color.YELLOW)
            timeTextColor = typedArray.getColor(R.styleable.SunriseSunset_timeTextColor, Color.GRAY)
            timeTextSize = typedArray.getDimension(R.styleable.SunriseSunset_timeTextSize, 40f)
            typedArray.recycle()
        }
        mCenterPoint = Point()
        mArcPaint = Paint()
        mArcPaint.isAntiAlias = true
        mArcPaint.color = lineColor
        mArcPaint.style = Paint.Style.STROKE
        mArcPaint.strokeWidth = 3f
        mArcPaint.pathEffect = DashPathEffect(floatArrayOf(20f, 15f), 20f)
        mArcPaint.strokeCap = Paint.Cap.ROUND
        mRectF = RectF()


        mSunPaint = Paint()
        mSunPaint.isAntiAlias = true
        mSunPaint.color = sunColor
        mSunPaint.style = Paint.Style.STROKE
        mSunPaint.strokeWidth = 3f
        mSunPaint.pathEffect = DashPathEffect(floatArrayOf(20f, 15f), 20f)
        mSunriseBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_sunrise)
        mSunsetBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_sunset)
        mSunPaint.colorFilter =
            PorterDuffColorFilter(sunColor, PorterDuff.Mode.SRC_ATOP)//设置后bitmap才会填充颜色

        mTimePaint = Paint()
        mTimePaint.color = timeTextColor
        mTimePaint.textSize = timeTextSize
        mTimePaint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            internalMeasure(widthMeasureSpec, mDefaultSize),
            internalMeasure(heightMeasureSpec, (mDefaultSize * 0.55).toInt())
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w - paddingLeft - paddingRight
        mHeight = h - paddingBottom - paddingTop
        mRadius = ((if (mWidth / 2 < mHeight) mWidth / 2 else mHeight) - dipToPx(
            mContext,
            15f
        )).toFloat()//留出一定空间
        mCenterPoint.x = mWidth / 2
        mCenterPoint.y = mHeight
        mRectF.left = mCenterPoint.x - mRadius
        mRectF.top = mCenterPoint.y - mRadius
        mRectF.right = mCenterPoint.x + mRadius
        mRectF.bottom = mCenterPoint.y + mRadius
        lineYLocate =
            (mCenterPoint.y - mRadius * sin(PI / 180 * (180 - sweepAngle) / 2)).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        drawArc(canvas)
        drawText(canvas)
        canvas.restore()
    }

    private fun drawText(canvas: Canvas) {
        canvas.drawText(
            mSunriseTime,
            mCenterPoint.x - mRadius + timeTextSize,
            lineYLocate + timeTextSize + 15f,
            mTimePaint
        )//y轴可能需要微调一下
        canvas.drawText(
            mSunsetTime,
            mCenterPoint.x + mRadius - timeTextSize,
            lineYLocate + timeTextSize + 15f,
            mTimePaint
        )
        mTimePaint.textSize = 40f
        canvas.drawText(
            "日出日落",
            mCenterPoint.x.toFloat(),
            lineYLocate - timeTextSize,
            mTimePaint
        )
    }

    private fun drawArc(canvas: Canvas) {
        val nowAngle: Float
        if (percent == 0f) {
            nowAngle = 0f
            canvas.drawBitmap(
                mSunsetBitmap,
                (mCenterPoint.x - mRadius * kotlin.math.cos(halfRemainAngle)).toFloat(),
                lineYLocate - mSunsetBitmap.height,
                mSunPaint
            )//太阳
        } else if (percent == 1f) {
            nowAngle = sweepAngle.toFloat()
            canvas.drawBitmap(
                mSunsetBitmap,
                (mCenterPoint.x + mRadius * kotlin.math.cos(halfRemainAngle)).toFloat() - mSunsetBitmap.width,
                lineYLocate - mSunsetBitmap.height,
                mSunPaint
            )
        } else {
            nowAngle = sweepAngle * percent

            canvas.drawBitmap(
                mSunriseBitmap,
                (mCenterPoint.x - mRadius * kotlin.math.cos((nowAngle + 18) * (PI / 180) + halfRemainAngle)).toFloat() - mSunriseBitmap.width / 2,
                (mCenterPoint.y - mRadius * kotlin.math.sin((nowAngle + 18) * (PI / 180) + halfRemainAngle)).toFloat() - mSunriseBitmap.height / 2,
                mSunPaint
            )//18这个数字是我测试出来计算过程中损失的各种度数
        }
        canvas.drawArc(
            mRectF,
            starAngle + nowAngle,
            sweepAngle - nowAngle,
            false,
            mArcPaint
        )//弧
        canvas.drawLine(0f, lineYLocate, mWidth.toFloat(), lineYLocate, mArcPaint)//线
        canvas.drawArc(
            mRectF,
            starAngle.toFloat(),
            nowAngle - 3.5f,
            false,
            mSunPaint
        )//留出3度让小太阳背景空白
    }

    fun startAnimation() {

        if (isSetTime) {
            val nowPercent = mNowTime.toFloat() / mTotalTime.toFloat()//这个float一定要转啊，不然会一致都是0
            if (nowPercent < 0.02) {//如果太阳只升起来一点就不画动画了,没出也不画
                setPercent(0f)
            } else if (nowPercent > 0.98) {
                setPercent(1f)
            } else {
                mAnimator = ObjectAnimator.ofFloat(this, "percent", 0f, nowPercent)
                mAnimator.duration = (2000 + 3000 * nowPercent).toLong()
                mAnimator.interpolator = LinearInterpolator()
                mAnimator.start()

            }
        }
    }

    fun stopAnimator() {
        clearAnimation()
    }

    fun setTime(mSunriseTime: String, mSunsetTime: String, nowTime: String) {
        this.mSunriseTime = mSunriseTime
        this.mSunsetTime = mSunsetTime
        mTotalTime = transToMinuteTime(mSunsetTime) - transToMinuteTime(mSunriseTime)
        this.mNowTime = transToMinuteTime(nowTime) - transToMinuteTime(mSunriseTime)
        isSetTime = true
    }

    fun setPercent(percent: Float) {
        this.percent = percent
        invalidate()
    }

    private fun transToMinuteTime(time: String): Int {//"00:00"
        val s = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1])
    }

    private fun dipToPx(context: Context, dip: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dip * density + 0.5f * if (dip >= 0) 1 else -1).toInt()
    }

    private fun internalMeasure(measureSpec: Int, defaultSize: Int): Int {
        var result = defaultSize
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = result.coerceAtMost(specSize)
        }
        return result
    }

}
