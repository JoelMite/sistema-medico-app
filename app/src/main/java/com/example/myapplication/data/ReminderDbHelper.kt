package com.example.myapplication.data

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.ui.ReminderMedicine

class ReminderDbHelper : SQLiteOpenHelper(ReminderMedicine.instance, DATABASE_NAME, null, DATABASE_VERSION) {

    // Los companion object funcionan como las variables
    // estaticas de java, la novedad es que pueden ser accedidas
    // desde cualquier clase, sin la necesidad de crear una instancia
    // (objeto).

    companion object {
        const val DATABASE_NAME = "ReminderDatabase.db"
        const val DATABASE_VERSION = 2
    }

    // Crea la tabla una vez la aplicacion haya sido instalada y iniciada.
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ReminderContract.CREATE_TABLE)
    }

    // Simplemente actualiza la base de datos. Ojo revisar bien esta parte
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + ReminderContract.TABLE_NAME)
        onCreate(db)
    }
}