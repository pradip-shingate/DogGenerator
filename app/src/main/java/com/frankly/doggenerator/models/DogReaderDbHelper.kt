package com.frankly.doggenerator.models

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class DogReaderDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "DogReader.db"
    }

    object FeedReaderContract : BaseColumns {
        const val TABLE_NAME = "entry"
        const val COLUMN_NAME_URL = "url"
        const val COLUMN_NAME_BITMAP = "bitmap"
    }

    private val sQLCreateEntries =
        "CREATE TABLE ${FeedReaderContract.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${FeedReaderContract.COLUMN_NAME_URL} TEXT," +
                "${FeedReaderContract.COLUMN_NAME_BITMAP} BLOB)"

    private val sQLDeleteEntries =
        "DROP TABLE IF EXISTS ${FeedReaderContract.TABLE_NAME}"


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(sQLCreateEntries)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(sQLDeleteEntries)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}