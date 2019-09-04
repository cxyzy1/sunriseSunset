package com.cxyzy.library

import android.content.Context
import android.graphics.Paint
import android.view.View
import kotlin.math.abs

object Utils {
    /**
     * 测量 view
     * @param measureSpec
     * @param defaultSize View的默认大小
     * @return
     */
    fun measure(measureSpec: Int, defaultSize: Int): Int {
        var result = defaultSize
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)
        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = result.coerceAtMost(specSize)
        }
        return result
    }

    /**
     * dp 转 px
     * @param context
     * @param dip
     * @return
     */
    fun dipToPx(context: Context, dip: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dip * density + 0.5f * if (dip >= 0) 1 else -1).toInt()
    }


    /**
     * 获取数值精度格式化字符串
     * @param precision
     * @return
     */
    fun getPrecisionFormat(precision: Int): String {
        return "%." + precision + "f"
    }


    /**
     * 测量文字高度
     */
    fun measureTextHeight(paint: Paint): Float {
        val fontMetrics = paint.fontMetrics
        return abs(fontMetrics.ascent) + fontMetrics.descent
    }


}
