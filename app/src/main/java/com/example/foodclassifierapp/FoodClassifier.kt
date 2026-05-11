package com.example.foodclassifierapp

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder

class FoodClassifier(context: Context) {

    private val interpreter: Interpreter
    private val labels: List<String>

    private val inputSize = 224
    private val numClasses = 5

    init {
        interpreter = Interpreter(
            FileUtil.loadMappedFile(context, "scottish_food_model_final.tflite")
        )
        labels = loadLabels(context)
    }

    private fun loadLabels(context: Context): List<String> {
        val list = mutableListOf<String>()
        val reader = BufferedReader(InputStreamReader(context.assets.open("labels.txt")))
        reader.useLines { lines ->
            lines.forEach { list.add(it) }
        }
        return list
    }

    fun classify(bitmap: Bitmap): Pair<String, Float> {
        val resized = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val buffer = convertBitmapToByteBuffer(resized)

        val output = Array(1) { FloatArray(numClasses) }
        interpreter.run(buffer, output)

        val probs = output[0]
        val maxIdx = probs.indices.maxByOrNull { probs[it] } ?: 0

        return Pair(labels[maxIdx], probs[maxIdx])
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(inputSize * inputSize)
        bitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in pixels) {
            buffer.putFloat(((pixel shr 16) and 0xFF).toFloat())
            buffer.putFloat(((pixel shr 8) and 0xFF).toFloat())
            buffer.putFloat((pixel and 0xFF).toFloat())
        }

        buffer.rewind()
        return buffer
    }
}