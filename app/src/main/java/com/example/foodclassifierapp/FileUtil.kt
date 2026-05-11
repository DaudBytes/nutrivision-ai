package com.example.foodclassifierapp

import android.content.Context
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

object FileUtil {
    fun loadMappedFile(context: Context, filename: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel

        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }
}