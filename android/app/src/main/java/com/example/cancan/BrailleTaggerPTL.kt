//package com.example.cancan
//
//import android.content.Context
//import android.graphics.Bitmap
//import org.pytorch.IValue
//import org.pytorch.Module
//import org.pytorch.Tensor
//import org.pytorch.torchvision.TensorImageUtils
//import kotlin.math.roundToInt
//import android.util.Log
//
//class BrailleTaggerPTL(private val context: Context, modelPath: String) {
//
//    private val module: Module = Module.load(assetFilePath(context, modelPath))
//
//    fun predict(bitmap: Bitmap, boxes: List<BoundingBox>): List<Pair<BoundingBox, String>> {
//        val results = mutableListOf<Pair<BoundingBox, String>>()
//
//        for ((i, box) in boxes.withIndex()) {
//            val rect = box.toRectF()
//            val left = rect.left.coerceAtLeast(0f).roundToInt()
//            val top = rect.top.coerceAtLeast(0f).roundToInt()
//            val width = rect.width().coerceAtMost((bitmap.width - left).toFloat()).roundToInt()
//            val height = rect.height().coerceAtMost((bitmap.height - top).toFloat()).roundToInt()
//
//            val cropped = Bitmap.createBitmap(bitmap, left, top, width, height)
//            Log.d("TAGGER", "📐 box[$i] left=$left top=$top width=$width height=$height")
//
//            val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
//                Bitmap.createScaledBitmap(cropped, 25, 40, true),
//                floatArrayOf(0.61567986f, 0.6217983f, 0.60718144f),
//                floatArrayOf(0.13666723f, 0.13178031f, 0.13231377f)
//            )
//
//            val output = module.forward(IValue.from(inputTensor)).toTensor()
//            val outputArray = output.dataAsFloatArray
//            val binary = outputArray.map { if (it > 0.5f) 1 else 0 }.toIntArray()
//            val vecStr = binary.joinToString("")
//            Log.d("TAGGER", "📦 box[$i] vector=$vecStr")
//            val brailleChar = convertToBrailleUnicode(vecStr, context)
//            Log.d("TAGGER", "🔤 box[$i] unicode='$brailleChar'")
//
//            results.add(box to brailleChar)
//        }
//
//        return results
//    }
//
//    private fun assetFilePath(context: Context, assetName: String): String {
//        val file = java.io.File(context.filesDir, assetName)
//        if (file.exists() && file.length() > 0) return file.absolutePath
//
//        context.assets.open(assetName).use { input ->
//            java.io.FileOutputStream(file).use { output ->
//                val buffer = ByteArray(4 * 1024)
//                var read: Int
//                while (input.read(buffer).also { read = it } != -1) {
//                    output.write(buffer, 0, read)
//                }
//                output.flush()
//            }
//        }
//        return file.absolutePath
//    }
//}


package com.example.cancan

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import kotlin.math.roundToInt

class BrailleTaggerPTL(private val context: Context, modelPath: String) {

    private val module: Module = Module.load(assetFilePath(context, modelPath))

    fun predict(bitmap: Bitmap, boxes: List<BoundingBox>): List<Pair<BoundingBox, String>> {
        val results = mutableListOf<Pair<BoundingBox, String>>()

        for ((i, box) in boxes.withIndex()) {
            val rect = box.toRectF()
            val left = rect.left.coerceAtLeast(0f).roundToInt()
            val top = rect.top.coerceAtLeast(0f).roundToInt()
            val width = rect.width().coerceAtMost((bitmap.width - left).toFloat()).roundToInt()
            val height = rect.height().coerceAtMost((bitmap.height - top).toFloat()).roundToInt()

            val cropped = Bitmap.createBitmap(bitmap, left, top, width, height)
            val resized = Bitmap.createScaledBitmap(cropped, 25, 40, true)

            Log.d("TAGGER", "📐 box[$i] left=$left top=$top width=$width height=$height")

            val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                resized,
                floatArrayOf(0.61567986f, 0.6217983f, 0.60718144f),
                floatArrayOf(0.13666723f, 0.13178031f, 0.13231377f)
            )

            Log.d("TAGGER", "📤 box[$i] inputTensor shape=${inputTensor.shape().contentToString()}")

            val output = module.forward(IValue.from(inputTensor)).toTensor()
            val outputArray = output.dataAsFloatArray

            Log.d("TAGGER", "📊 box[$i] rawOutput=${outputArray.joinToString(limit=6)}")

            val binary = outputArray.map { if (it > 0.5f) 1 else 0 }.toIntArray()
            val vecStr = binary.joinToString("")

            Log.d("TAGGER", "📦 box[$i] vector=$vecStr")

            val brailleChar = convertToBrailleUnicode(vecStr, context)
            Log.d("TAGGER", "🔤 box[$i] unicode='$brailleChar'")

            results.add(box to brailleChar)
        }

        return results
    }

    private fun assetFilePath(context: Context, assetName: String): String {
        val file = java.io.File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) return file.absolutePath

        context.assets.open(assetName).use { input ->
            java.io.FileOutputStream(file).use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
        return file.absolutePath
    }
}
