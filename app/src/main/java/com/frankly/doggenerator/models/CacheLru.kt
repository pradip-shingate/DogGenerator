package com.frankly.doggenerator.models

import android.graphics.Bitmap
import android.util.LruCache

class CacheLru(val size: Int) {

    private var memoryCache: LruCache<String, Bitmap>? = object : LruCache<String, Bitmap>(size) {

        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    fun getLruCache(): LruCache<String, Bitmap>? {
        return memoryCache
    }
}