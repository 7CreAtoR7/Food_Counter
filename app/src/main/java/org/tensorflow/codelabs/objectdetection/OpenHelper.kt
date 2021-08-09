package org.tensorflow.codelabs.objectdetection


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class OpenHelper(
    context: Context?,
    name: String? = "Products",
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1
) :
    SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            "CREATE TABLE PRODUCTS (id INTEGER PRIMARY KEY AUTOINCREMENT, product TEXT UNIQUE, img TEXT, proteins TEXT, fats TEXT, carbs TEXT, kcal TEXT);"
        db?.execSQL(query)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS PRODUCTS;")
        onCreate(db)
    }
}