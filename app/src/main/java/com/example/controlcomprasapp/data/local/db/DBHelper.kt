package com.example.controlcomprasapp.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) :
    SQLiteOpenHelper(context, "tickets.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableTicket = """
            CREATE TABLE ticket (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha TEXT,
                id_local INTEGER,
                id_archivo INTEGER,
                FOREIGN KEY(id_local) REFERENCES locales(id),
                FOREIGN KEY(id_archivo) REFERENCES archivos(id)
            )""".trimIndent()

        val createTableItemTicket = """
            CREATE TABLE ticket_item (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ticket_id INTEGER,
                nombre TEXT,
                cantidad INTEGER,
                precio REAL,
                total REAL,
                FOREIGN KEY(ticket_id) REFERENCES ticket(id)
            )""".trimIndent()

        val createTableLocales = """
            CREATE TABLE locales (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT unique
            )""".trimIndent()

        val createTableDescuentos = """
            CREATE TABLE descuentos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ticket_id INTEGER,
                nombre TEXT,
                total REAL,
                id_archivo INTEGER,
                FOREIGN KEY(id_archivo) REFERENCES Archivos(id),
                FOREIGN KEY(ticket_id) REFERENCES ticket(id)
            )""".trimIndent()

        val createTableArchivos = """
            CREATE TABLE archivos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT unique
            )""".trimIndent()

        db.execSQL(createTableTicket)
        db.execSQL(createTableItemTicket)
        db.execSQL(createTableLocales)
        db.execSQL(createTableDescuentos)
        db.execSQL(createTableArchivos)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        db.execSQL("DROP TABLE IF EXISTS ticket_item")
        onCreate(db)
    }
}