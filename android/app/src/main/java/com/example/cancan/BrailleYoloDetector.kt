//package com.example.cancan
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.util.Log
//import org.tensorflow.lite.Interpreter
//import java.io.FileInputStream
//import java.nio.MappedByteBuffer
//import java.nio.channels.FileChannel
//import kotlin.math.max
//import kotlin.math.min
//import android.graphics.RectF
//
//class BrailleYoloDetector(private val context: Context, modelPath: String) {
//
//    private val interpreter: Interpreter
//
//    init {
//        val modelBuffer = loadModelFile(modelPath)
//        interpreter = Interpreter(modelBuffer)
//        Log.d("YOLO", "✅ TFLite 모델 로드 완료")
//    }
//
//    private fun loadModelFile(modelPath: String): MappedByteBuffer {
//        val fileDescriptor = context.assets.openFd(modelPath)
//        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
//        val fileChannel = inputStream.channel
//        return fileChannel.map(
//            FileChannel.MapMode.READ_ONLY,
//            fileDescriptor.startOffset,
//            fileDescriptor.declaredLength
//        )
//    }
//
//    fun detect(bitmap: Bitmap): List<BoundingBox> {
//        val inputSize = 640
//        val scaled = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
//
//        val input = Array(1) { Array(inputSize) { Array(inputSize) { FloatArray(3) } } }
//        for (y in 0 until inputSize) {
//            for (x in 0 until inputSize) {
//                val pixel = scaled.getPixel(x, y)
//                val r = ((pixel shr 16 and 0xFF) / 255.0f)
//                val g = ((pixel shr 8 and 0xFF) / 255.0f)
//                val b = ((pixel and 0xFF) / 255.0f)
//
//                input[0][y][x][0] = b
//                input[0][y][x][1] = g
//                input[0][y][x][2] = r
//            }
//        }
//
//        val output = Array(1) { Array(25200) { FloatArray(69) } }
//        interpreter.run(input, output)
//
//        val rawBoxes = mutableListOf<BoundingBox>()
//        for (i in 0 until 25200) {
//            val row = output[0][i]
//            val conf = row[4]
//            if (conf < 0.15f) continue
//
////            val cx = row[0] * bitmap.width
////            val cy = row[1] * bitmap.height
////            val w = row[2] * bitmap.width
////            val h = row[3] * bitmap.height
//
//            val scaleX = bitmap.width.toFloat() / 640  // YOLO 입력 크기 640
//            val scaleY = bitmap.height.toFloat() / 640
//            val cx = row[0] * 640 * scaleX
//            val cy = row[1] * 640 * scaleY
//            val w = row[2] * 640 * scaleX
//            val h = row[3] * 640 * scaleY
//
//            var maxProb = 0f
//            var classId = 0
//            for (c in 5 until 69) {
//                if (row[c] > maxProb) {
//                    maxProb = row[c]
//                    classId = c - 5
//                }
//            }
//
//            rawBoxes.add(BoundingBox(cx, cy, w, h, conf, classId))
//            Log.d("YOLO", "📦 box[$i] conf=$conf cx=$cx cy=$cy w=$w h=$h")
//        }
//
//        val filtered = applyNms(rawBoxes, 0.3f)
//        Log.d("YOLO", "🎯 conf>0.15 박스 수: ${rawBoxes.size}")
//        Log.d("YOLO", "🧹 NMS 적용 후 박스 수: ${filtered.size}")
//        return filtered
//    }
//
//    private fun applyNms(boxes: List<BoundingBox>, iouThreshold: Float): List<BoundingBox> {
//        val picked = mutableListOf<BoundingBox>()
//        val sorted = boxes.sortedByDescending { it.confidence }.toMutableList()
//
//        while (sorted.isNotEmpty()) {
//            val best = sorted.removeAt(0)
//            picked.add(best)
//            val toRemove = mutableListOf<BoundingBox>()
//
//            for (box in sorted) {
//                if (iou(best, box) > iouThreshold) {
//                    toRemove.add(box)
//                }
//            }
//            sorted.removeAll(toRemove)
//        }
//        return picked
//    }
//
//    private fun iou(box1: BoundingBox, box2: BoundingBox): Float {
//        val a = box1.toRectF()
//        val b = box2.toRectF()
//        val x1 = max(a.left, b.left)
//        val y1 = max(a.top, b.top)
//        val x2 = min(a.right, b.right)
//        val y2 = min(a.bottom, b.bottom)
//        val intersection = max(0f, x2 - x1) * max(0f, y2 - y1)
//        val areaA = (a.right - a.left) * (a.bottom - a.top)
//        val areaB = (b.right - b.left) * (b.bottom - b.top)
//        return intersection / (areaA + areaB - intersection + 1e-6f)
//    }
//}


package com.example.cancan

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.max
import kotlin.math.min
import android.graphics.RectF

class BrailleYoloDetector(private val context: Context, modelPath: String) {

    private val interpreter: Interpreter

    init {
        val modelBuffer = loadModelFile(modelPath)
        interpreter = Interpreter(modelBuffer)
        Log.d("YOLO", "✅ TFLite 모델 로드 완료")
    }

    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }

    fun detect(bitmap: Bitmap): List<BoundingBox> {
        val inputSize = 640
        val scaled = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

        val input = Array(1) { Array(inputSize) { Array(inputSize) { FloatArray(3) } } }
        for (y in 0 until inputSize) {
            for (x in 0 until inputSize) {
                val pixel = scaled.getPixel(x, y)
                val r = ((pixel shr 16 and 0xFF) / 255.0f)
                val g = ((pixel shr 8 and 0xFF) / 255.0f)
                val b = ((pixel and 0xFF) / 255.0f)

                input[0][y][x][0] = b
                input[0][y][x][1] = g
                input[0][y][x][2] = r
            }
        }

        val output = Array(1) { Array(25200) { FloatArray(69) } }
        interpreter.run(input, output)

        val rawBoxes = mutableListOf<BoundingBox>()
        for (i in 0 until 25200) {
            val row = output[0][i]
            val conf = row[4]
            if (conf < 0.16f) continue

            val scaleX = bitmap.width.toFloat() / 640
            val scaleY = bitmap.height.toFloat() / 640
            val cx = row[0] * 640 * scaleX
            val cy = row[1] * 640 * scaleY
            val w = row[2] * 640 * scaleX
            val h = row[3] * 640 * scaleY

            var maxProb = 0f
            var classId = 0
            for (c in 5 until 69) {
                if (row[c] > maxProb) {
                    maxProb = row[c]
                    classId = c - 5
                }
            }

            val box = BoundingBox(cx, cy, w, h, conf, classId)
            rawBoxes.add(box)
            Log.d("YOLO", "📦 box[$i] conf=$conf cx=$cx cy=$cy w=$w h=$h class=$classId")
        }

        val filtered = applyNms(rawBoxes, 0.3f)
        Log.d("YOLO", "🎯 conf>0.15 박스 수: ${rawBoxes.size}")
        Log.d("YOLO", "🧹 NMS 적용 후 박스 수: ${filtered.size}")
        filtered.forEachIndexed { i, box ->
            Log.d("YOLO", "✅ 최종 box[$i]: cx=${box.cx} cy=${box.cy} w=${box.w} h=${box.h} conf=${box.confidence} class=${box.classId}")
        }
        return filtered
    }

    private fun applyNms(boxes: List<BoundingBox>, iouThreshold: Float): List<BoundingBox> {
        val picked = mutableListOf<BoundingBox>()
        val sorted = boxes.sortedByDescending { it.confidence }.toMutableList()

        while (sorted.isNotEmpty()) {
            val best = sorted.removeAt(0)
            picked.add(best)
            val toRemove = mutableListOf<BoundingBox>()

            for (box in sorted) {
                if (iou(best, box) > iouThreshold) {
                    toRemove.add(box)
                }
            }
            sorted.removeAll(toRemove)
        }
        return picked
    }

    private fun iou(box1: BoundingBox, box2: BoundingBox): Float {
        val a = box1.toRectF()
        val b = box2.toRectF()
        val x1 = max(a.left, b.left)
        val y1 = max(a.top, b.top)
        val x2 = min(a.right, b.right)
        val y2 = min(a.bottom, b.bottom)
        val intersection = max(0f, x2 - x1) * max(0f, y2 - y1)
        val areaA = (a.right - a.left) * (a.bottom - a.top)
        val areaB = (b.right - b.left) * (b.bottom - b.top)
        return intersection / (areaA + areaB - intersection + 1e-6f)
    }
}