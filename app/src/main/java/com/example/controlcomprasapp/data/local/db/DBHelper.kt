package com.example.controlcomprasapp.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) :
    SQLiteOpenHelper(context, "tickets.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableTicket = """
            CREATE TABLE ticket (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha TEXT,
                id_local INTEGER,
                id_archivo INTEGER,
                tipo TEXT NOT NULL DEFAULT 'COMPRA',
                banco TEXT,
                marca TEXT,
                fecha_vencimiento TEXT,
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
                seccion TEXT,
                fecha_consumo TEXT,
                cuotas_total INTEGER,
                cuota_actual INTEGER,
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

        val createTableGastosFijos = """
            CREATE TABLE gastos_fijos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                monto REAL NOT NULL DEFAULT 0
            )""".trimIndent()

        val createTableGastosMensuales = """
            CREATE TABLE gastos_mensuales (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_gasto_fijo INTEGER,
                nombre TEXT NOT NULL,
                monto REAL NOT NULL,
                mes INTEGER NOT NULL,
                anio INTEGER NOT NULL,
                es_fijo INTEGER NOT NULL DEFAULT 0,
                pagado INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(id_gasto_fijo) REFERENCES gastos_fijos(id)
            )""".trimIndent()

        val createTableIngresos = """
            CREATE TABLE ingresos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                monto REAL NOT NULL,
                mes INTEGER NOT NULL,
                anio INTEGER NOT NULL
            )""".trimIndent()

        db.execSQL(createTableTicket)
        db.execSQL(createTableItemTicket)
        db.execSQL(createTableLocales)
        db.execSQL(createTableDescuentos)
        db.execSQL(createTableArchivos)
        db.execSQL(createTableGastosFijos)
        db.execSQL(createTableGastosMensuales)
        db.execSQL(createTableIngresos)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        if (oldV < 2) {
            db.execSQL("ALTER TABLE ticket ADD COLUMN tipo TEXT NOT NULL DEFAULT 'COMPRA'")
            db.execSQL("ALTER TABLE ticket ADD COLUMN banco TEXT")
            db.execSQL("ALTER TABLE ticket ADD COLUMN marca TEXT")
            db.execSQL("ALTER TABLE ticket ADD COLUMN fecha_vencimiento TEXT")
            db.execSQL("ALTER TABLE ticket_item ADD COLUMN fecha_consumo TEXT")
            db.execSQL("ALTER TABLE ticket_item ADD COLUMN cuotas_total INTEGER")
            db.execSQL("ALTER TABLE ticket_item ADD COLUMN cuota_actual INTEGER")
        }
    }
}