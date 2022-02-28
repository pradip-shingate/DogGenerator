package com.frankly.doggenerator.models

import android.graphics.Bitmap
import android.util.LruCache
import java.util.*

class CacheLru(val size: Int) :Observable() {

    private var memoryCache: LruCache<String, Bitmap>? = object : LruCache<String, Bitmap>(size) {
        override fun entryRemoved(
            evicted: Boolean,
            key: String?,
            oldValue: Bitmap?,
            newValue: Bitmap?
        ) {
            super.entryRemoved(evicted, key, oldValue, newValue)
            //remove from DB
            if (evicted) {
                setChanged()
                notifyObservers(key)
            }
        }
    }

    fun getLruCache(): LruCache<String, Bitmap>? {
        return memoryCache
    }
}