package com.example.cancan

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.media.ExifInterface
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.util.*
import kotlin.math.*

class CameraActivity : AppCompatActivity() {

    private lateinit var imageBitmap: Bitmap
    private lateinit var overlayView: ImageView
    private lateinit var textResult: TextView
    private lateinit var tts: TextToSpeech
    private var ttsReady = false

    private val LOPSIDED_SET = setOf("⠠", "⠐", "⠰", "⠈", "⠨", "⠘", "⠸", "⠄", "⠂",
                                    "⠆", "⠁", "⠅", "⠃", "⠇", "⠤", "⠥", "⠬", "⠒", "⠉")

    private lateinit var brailleMap: Map<String, String>

    private val DUPLICATE_DISTANCE_THRESH = 15f
    private val DUPLICATE_HAMMING_THRESH = 1

    private fun initializeBrailleMap(context: Context) {
        try {
            val json = context.assets.open("braille_map.json").bufferedReader().use { it.readText() }
            val obj = JSONObject(json)
            brailleMap = obj.keys().asSequence().associateWith { obj.getString(it) }
        } catch (e: Exception) {
            Log.e("CameraActivity", "Failed to load braille_map.json", e)
            brailleMap = emptyMap()
        }
    }

    private fun rotateIfRequired(bitmap: Bitmap, imageUri: Uri): Bitmap {
        val inputStream = contentResolver.openInputStream(imageUri)
        val exif = ExifInterface(inputStream!!)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        overlayView = findViewById(R.id.result_overlay)
        textResult = findViewById(R.id.textResult)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.KOREAN)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    ttsReady = true
                }
            } else {
                Log.e("CameraActivity", "TTS 초기화 실패")
            }
        }

        val uriStr = intent.getStringExtra("image_uri") ?: return
        val imageUri = Uri.parse(uriStr)
        val rawBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        imageBitmap = rotateIfRequired(rawBitmap, imageUri)

        initializeBrailleMap(this)
        runObjectDetectionAndBrailleTagging(imageBitmap)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    private fun runObjectDetectionAndBrailleTagging(bitmap: Bitmap) {
        val detector = BrailleYoloDetector(this, "yolov5_braille-fp16.tflite")
        val tagger = BrailleTaggerPTL(this, "braille_tagger.ptl")

        val detections = detector.detect(bitmap)
        if (detections.isEmpty()) {
            textResult.text = "감지된 점자 없음"
            val ttsText = "점자 감지에 실패했습니다. 다시 시도해주세요."
            if (ttsReady) {
                tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (ttsReady) {
                        tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
                    } else {
                        Log.w("CameraActivity", "TTS still not ready after delay")
                    }
                }, 1000)
            }
            return
        }


        val invalidBrailleSet = setOf("\uFFFD", "000000", " ", "?", "")
        val rawPredictions = tagger.predict(bitmap, detections)

        val initialPredictions = mutableListOf<Pair<BoundingBox, String>>()
        for ((box, char) in rawPredictions) {
            if (char == "?") {
                Log.d("FILTER", "❌ 제외됨 (Unicode '?'): $box → $char")
                continue
            }
            initialPredictions.add(box to char)
        }



        if (initialPredictions.isEmpty()) {
            textResult.text = "점자 예측 실패"
            val ttsText = "점자 번역에 실패하였습니다. 다시 시도해주세요."
            if (ttsReady) {
                tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (ttsReady) {
                        tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
                    } else {
                        Log.w("CameraActivity", "TTS still not ready after delay")
                    }
                }, 1000)
            }
            return
        }

        val boxes = initialPredictions.map { it.first.toRectF() }
        val preds = initialPredictions.map { it.second }

        val keptIndices = removeDuplicateVectors(boxes, preds)
        val filteredBoxes = keptIndices.map { boxes[it] }
        val filteredPreds = keptIndices.map { preds[it] }

        val (adjustedBoxes, movedIndices) = correctLopsidedPositions(filteredBoxes, filteredPreds, keptIndices)
        val uniformBoxes = adjustBoxSizes(adjustedBoxes)

        val finalBoxes = uniformBoxes.map {
            BoundingBox(it.centerX(), it.centerY(), it.width(), it.height(), 1f, 0)
        }
        val finalPredictions = tagger.predict(bitmap, finalBoxes)
        val sortedFinal = finalPredictions.sortedBy { it.first.cx }
        val brailleChars = sortedFinal.map { it.second }

        drawResults(bitmap, uniformBoxes, brailleChars, movedIndices)
        val brailleToKor = BrailleToKor()
        val brailleString = brailleChars.joinToString("")
        val translatedText = brailleToKor.translation(brailleString)

        // 최종 결과에 ?가 포함되어 있으면 번역 실패
        if (brailleString.contains("?")) {
            textResult.text = "번역 실패: 점자 인식에 오류가 있습니다."
            val ttsText = "점자 번역에 실패하였습니다. 다시 시도해주세요."
            if (ttsReady) {
                tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (ttsReady) {
                        tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
                    } else {
                        Log.w("CameraActivity", "TTS still not ready after delay")
                    }
                }, 1000)
            }
            return
        }

        textResult.text = "번역된 텍스트: $translatedText"

        val ttsText = "번역 결과는 $translatedText 입니다."
        if (ttsReady) {
            tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                if (ttsReady) {
                    tts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
                } else {
                    Log.w("CameraActivity", "TTS still not ready after delay")
                }
            }, 1000)
        }
    }

    private fun removeDuplicateVectors(
        boxes: List<RectF>,
        predictions: List<String>,
        distanceThresh: Float = DUPLICATE_DISTANCE_THRESH,
        hammingThresh: Int = DUPLICATE_HAMMING_THRESH
    ): List<Int> {
        val kept = mutableListOf<Int>()
        val used = mutableSetOf<Int>()
        for (i in boxes.indices) {
            if (i in used) continue
            kept.add(i)
            for (j in i + 1 until boxes.size) {
                if (j in used) continue
                val dx = boxes[i].centerX() - boxes[j].centerX()
                val dy = boxes[i].centerY() - boxes[j].centerY()
                val dist = sqrt(dx * dx + dy * dy)
                val hamming = predictions[i].zip(predictions[j]).count { it.first != it.second }
                if (dist < distanceThresh && hamming <= hammingThresh) used.add(j)
            }
        }
        return kept
    }

    private fun correctLopsidedPositions(
        boxes: List<RectF>,
        predictions: List<String>,
        originalIndices: List<Int>,
        lopsidedSet: Set<String> = LOPSIDED_SET
    ): Pair<List<RectF>, List<Int>> {
        val sortedIdx = boxes.withIndex().sortedBy { it.value.centerX() }.map { it.index }
        val cxList = sortedIdx.map { boxes[it].centerX() }.toMutableList()
        val cyList = sortedIdx.map { boxes[it].centerY() }
        val predsSorted = sortedIdx.map { predictions[it] }

        val nonLopsided = cxList.indices.filter { predsSorted[it] !in lopsidedSet }

        val maxW = boxes.maxOf { it.width() }
        val maxH = boxes.maxOf { it.height() }

        // 박스 간 여백 spacing 계산
        val spacings = mutableListOf<Float>()
        for (i in 0 until nonLopsided.size - 1) {
            val leftIdx = nonLopsided[i]
            val rightIdx = nonLopsided[i + 1]
            val delta = rightIdx - leftIdx
            val gap = cxList[rightIdx] - cxList[leftIdx]
            val spacing = (gap - maxW - (delta - 1) * maxW) / delta
            spacings.add(spacing)
        }
        val avgSpacing = if (spacings.isNotEmpty()) spacings.average().toFloat() else maxW * 1.5f
        val desiredGap = avgSpacing + maxW

        val movedIndices = mutableListOf<Int>()
        val correctedCxList = cxList.toMutableList()

        // 🔁 1차 순회: 왼 → 오 (왼쪽 > 오른쪽 우선 순위 기준 보정)
        for (i in correctedCxList.indices) {
            if (predsSorted[i] !in lopsidedSet) continue

            val original = correctedCxList[i]

            val leftIsFixed = (i - 1 >= 0 &&
                    (predsSorted[i - 1] !in lopsidedSet || movedIndices.contains(i - 1)))
            val rightIsFixed = (i + 1 < correctedCxList.size &&
                    (predsSorted[i + 1] !in lopsidedSet || movedIndices.contains(i + 1)))

            correctedCxList[i] = when {
                leftIsFixed -> correctedCxList[i - 1] + desiredGap
                rightIsFixed -> correctedCxList[i + 1] - desiredGap
                else -> correctedCxList[i] // 기준 없으면 그대로
            }

            if (correctedCxList[i] != original) {
                Log.d("BRAILLE_CORRECT", "📍 Box $i (1st pass) moved: $original → ${correctedCxList[i]}")
                movedIndices.add(i)
            }
        }


        // 🔁 2차 순회: 오 → 왼 (오른쪽 박스 기준 보정, 남은 것만)
        for (i in correctedCxList.size - 2 downTo 0) {
            if (predsSorted[i] !in lopsidedSet || movedIndices.contains(i)) continue

            val rightIsFixed = (predsSorted[i + 1] !in lopsidedSet || movedIndices.contains(i + 1))

            if (rightIsFixed) {
                val original = correctedCxList[i]
                correctedCxList[i] = correctedCxList[i + 1] - desiredGap
                if (correctedCxList[i] != original) {
                    Log.d("BRAILLE_CORRECT", "📍 Box $i (reverse pass) moved: $original → ${correctedCxList[i]}")
                    movedIndices.add(i)
                }
            }
        }

        // 보정된 박스 생성
        val newBoxes = correctedCxList.indices.map { i ->
            RectF(
                correctedCxList[i] - maxW / 2,
                cyList[i] - maxH / 2,
                correctedCxList[i] + maxW / 2,
                cyList[i] + maxH / 2
            )
        }

        return Pair(newBoxes, movedIndices)
    }





    private fun adjustBoxSizes(boxes: List<RectF>): List<RectF> {
        val maxW = boxes.maxOf { it.width() }
        val maxH = boxes.maxOf { it.height() }
        return boxes.map {
            RectF(
                it.centerX() - maxW / 2,
                it.centerY() - maxH / 2,
                it.centerX() + maxW / 2,
                it.centerY() + maxH / 2
            )
        }
    }

    private fun drawResults(bitmap: Bitmap, boxes: List<RectF>, chars: List<String>, movedIndices: List<Int>) {
        val resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(resultBitmap)
        val paintBox = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        val paintText = Paint().apply {
            color = Color.BLUE
            textSize = 32f
            typeface = Typeface.DEFAULT_BOLD
        }
        boxes.forEachIndexed { i, rect ->
            if (i < chars.size) {
                canvas.drawRect(rect, paintBox)
                canvas.drawText(chars[i], rect.left, rect.top - 10f, paintText)
            }
        }
        overlayView.setImageBitmap(resultBitmap)
    }

    private fun List<Float>.median(): Float {
        if (isEmpty()) return 0f
        val sorted = sorted()
        return if (size % 2 == 0) (sorted[size / 2 - 1] + sorted[size / 2]) / 2
        else sorted[size / 2]
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }
}
