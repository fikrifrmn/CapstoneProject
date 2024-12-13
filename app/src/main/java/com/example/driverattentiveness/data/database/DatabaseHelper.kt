package com.example.driverattentiveness.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "driver_attentiveness.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "user_distances"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_TOTAL_DISTANCE = "total_distance"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_USER_ID TEXT PRIMARY KEY,
                $COLUMN_TOTAL_DISTANCE REAL
            )
        """
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
            db?.execSQL(dropTableQuery)
            onCreate(db)
        }
    }

    fun saveTotalDistance(userId: String, totalDistance: Double) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_TOTAL_DISTANCE, totalDistance)
        }
        db.replace(TABLE_NAME, null, values)
        db.close()
    }

    fun getTotalDistance(userId: String): Double {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_TOTAL_DISTANCE),
            "$COLUMN_USER_ID = ?",
            arrayOf(userId),
            null, null, null
        )

        val totalDistance = if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN_TOTAL_DISTANCE)
            if (columnIndex != -1) {
                cursor.getDouble(columnIndex)
            } else {
                0.0 // Kolom tidak ditemukan, mengembalikan nilai default
            }
        } else {
            0.0 // Jika tidak ada data ditemukan
        }
        cursor.close()
        db.close()
        return totalDistance
    }

}