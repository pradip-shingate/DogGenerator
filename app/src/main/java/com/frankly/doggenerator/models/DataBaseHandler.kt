package com.frankly.doggenerator.models

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream


class DataBaseHandler(context: Context) {

    private val dbHelper: DogReaderDbHelper? = DogReaderDbHelper(context)

    fun putInDB(url: String, bitmap: Bitmap) {
        val db = dbHelper?.writableDatabase
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val img: ByteArray = bos.toByteArray()

        val values = ContentValues().apply {
            put(DogReaderDbHelper.FeedReaderContract.COLUMN_NAME_URL, url)
            put(DogReaderDbHelper.FeedReaderContract.COLUMN_NAME_BITMAP, img)
        }
        db?.insert(DogReaderDbHelper.FeedReaderContract.TABLE_NAME, null, values)
        db?.close()
    }

    @SuppressLint("Range")
    fun getFromDB(lru: CacheLru) {
        val db = dbHelper?.readableDatabase

        val cursor = db?.query(
            DogReaderDbHelper.FeedReaderContract.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.let {

            if (it.moveToFirst()) {
                do {
                    val str =
                        it.getString(it.getColumnIndex(DogReaderDbHelper.FeedReaderContract.COLUMN_NAME_URL))
                    val blob =
                        it.getBlob(it.getColumnIndex(DogReaderDbHelper.FeedReaderContract.COLUMN_NAME_BITMAP))
                    val map = BitmapFactory.decodeByteArray(blob, 0, blob.size);
                    lru.getLruCache()?.put(str, map)
                } while (it.moveToNext())
            }
        }

        cursor?.close()
    }

    fun deleteData() {
        val db = dbHelper?.readableDatabase
        db?.delete(DogReaderDbHelper.FeedReaderContract.TABLE_NAME, null, null)
        db?.close()
    }

}