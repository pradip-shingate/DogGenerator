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
        // Gets the data repository in write mode
        val db = dbHelper?.writableDatabase

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val img: ByteArray = bos.toByteArray()

// Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(DogReaderDbHelper.FeedReaderContract.FeedEntry.COLUMN_NAME_URL, url)
            put(DogReaderDbHelper.FeedReaderContract.FeedEntry.COLUMN_NAME_BITMAP, img)
        }

// Insert the new row, returning the primary key value of the new row
        val newRowId =
            db?.insert(DogReaderDbHelper.FeedReaderContract.FeedEntry.TABLE_NAME, null, values)

        db?.close()
    }

    @SuppressLint("Range")
    fun getFromDB(lru: CacheLru) {
        val db = dbHelper?.readableDatabase

        val cursor = db?.query(
            DogReaderDbHelper.FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
            null,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            null               // The sort order
        )

        cursor?.let {

            if (it.moveToFirst()) {
                do {
                    val str =
                        it.getString(it.getColumnIndex(DogReaderDbHelper.FeedReaderContract.FeedEntry.COLUMN_NAME_URL))
                    val blob =
                        it.getBlob(it.getColumnIndex(DogReaderDbHelper.FeedReaderContract.FeedEntry.COLUMN_NAME_BITMAP))
                    val map = BitmapFactory.decodeByteArray(blob, 0, blob.size);
                    lru.getLruCache()?.put(str, map)
                } while (it.moveToNext())
            }
        }

        cursor?.close()
    }

    fun deleteData() {
        val db = dbHelper?.readableDatabase
        db?.delete(DogReaderDbHelper.FeedReaderContract.FeedEntry.TABLE_NAME, null, null)
        db?.close()
    }

}