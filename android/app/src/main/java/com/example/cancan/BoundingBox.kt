//package com.example.cancan
//
//import android.graphics.RectF
//
//data class BoundingBox(
//    val cx: Float, val cy: Float, val w: Float, val h: Float,
//    val confidence: Float,
//    val classId: Int
//) {
//    fun toRectF(): RectF {
//        val left = cx - w / 2
//        val top = cy - h / 2
//        val right = cx + w / 2
//        val bottom = cy + h / 2
//        return RectF(left, top, right, bottom)
//    }
//
//    fun toXYXY(): FloatArray {
//        val rect = toRectF()
//        return floatArrayOf(rect.left, rect.top, rect.right, rect.bottom)
//    }
//}


package com.example.cancan

import android.graphics.RectF

data class BoundingBox(
    val cx: Float,
    val cy: Float,
    val w: Float,
    val h: Float,
    val confidence: Float,
    val classId: Int
) {
    val width: Float
        get() = w

    val height: Float
        get() = h

    fun toRectF(): RectF {
        val left = cx - w / 2
        val top = cy - h / 2
        val right = cx + w / 2
        val bottom = cy + h / 2
        return RectF(left, top, right, bottom)
    }

    fun toXYXY(): FloatArray {
        val rect = toRectF()
        return floatArrayOf(rect.left, rect.top, rect.right, rect.bottom)
    }
}
