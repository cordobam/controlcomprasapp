package com.example.controlcomprasapp.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) :
    SQLiteOpenHelper(context, "tickets.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE ticket_item (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                cantidad INTEGER,
                precio REAL,
                total REAL
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        db.execSQL("DROP TABLE IF EXISTS ticket_item")
        onCreate(db)
    }
}