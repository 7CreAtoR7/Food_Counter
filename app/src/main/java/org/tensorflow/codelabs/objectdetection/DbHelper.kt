package org.tensorflow.codelabs.objectdetection

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class DbHelper(ctx: Context) {
    private var db: SQLiteDatabase

    init {
        val openHelper = OpenHelper(ctx)
        db = openHelper.writableDatabase
    }

    fun insert(
        product: String?,
        img: String?,
        proteins: String?,
        fats: String?,
        carbs: String?,
        kcal: String?
    ) {
        val cv = ContentValues()
        cv.put("product", product)
        cv.put("img", img)
        cv.put("proteins", proteins)
        cv.put("fats", fats)
        cv.put("carbs", carbs)
        cv.put("kcal", kcal)
        db.insertOrThrow("Products", null, cv)
    }


    @SuppressLint("Recycle")
    fun selectImgAboutProduct(product: String): String {
        val cursor: Cursor =
            db.query("Products", arrayOf("img"), "product = ?", arrayOf(product), null, null, null)
        var str = ""
        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            do {
                str = cursor.getString(0)

            } while (cursor.moveToNext())
        }
        return str
    }

    @SuppressLint("Recycle")
    fun selectProteinsAboutProduct(product: String): String {
        val cursor: Cursor = db.query(
            "Products",
            arrayOf("proteins"),
            "product = ?",
            arrayOf(product),
            null,
            null,
            null
        )
        var str = ""
        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            do {
                str = cursor.getString(0)
            } while (cursor.moveToNext())
        }
        return str
    }

    @SuppressLint("Recycle")
    fun selectFatsAboutProduct(product: String): String {
        val cursor: Cursor =
            db.query("Products", arrayOf("fats"), "product = ?", arrayOf(product), null, null, null)
        var str = ""
        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            do {
                str = cursor.getString(0)
            } while (cursor.moveToNext())
        }
        return str
    }

    @SuppressLint("Recycle")
    fun selectCarbsAboutProduct(product: String): String {
        val cursor: Cursor = db.query(
            "Products",
            arrayOf("carbs"),
            "product = ?",
            arrayOf(product),
            null,
            null,
            null
        )
        var str = ""
        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            do {
                str = cursor.getString(0)
            } while (cursor.moveToNext())
        }
        return str
    }

    @SuppressLint("Recycle")
    fun selectKcalAboutProduct(product: String): String {
        val cursor: Cursor =
            db.query("Products", arrayOf("kcal"), "product = ?", arrayOf(product), null, null, null)
        var str = ""
        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            do {
                str = cursor.getString(0)
            } while (cursor.moveToNext())
        }
        return str
    }
}