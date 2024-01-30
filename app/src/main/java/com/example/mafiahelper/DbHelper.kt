package com.example.mafiahelper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


//for create database and fill it from start
class DbHelper(val context: Context, val factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "maffHelperDB", factory, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createIconsQuery = context.getString(R.string.creteIconTableQuery)

        val createRolesQuery =
            context.getString(R.string.createRolesTableQuery)

        // create Icons table and fill this
        db!!.execSQL(createIconsQuery)
        fillIconsTableWithEmojis(db)
        // create roles table
        db.execSQL(createRolesQuery)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // drop only immutable tables
        db!!.execSQL("DROP TABLE IF EXISTS icons")
        db.execSQL("DROP TABLE IF EXISTS role")
        onCreate(db)
    }

    private fun fillIconsTableWithEmojis(db: SQLiteDatabase?) {
        val emojis = mutableListOf<String>()

        for (i in 0x1F600..0x1F64F) {
            emojis.add(String(Character.toChars(i)))
        }

        for (i in 0x1F300..0x1F5FF) {
            emojis.add(String(Character.toChars(i)))
        }

        for (i in 0x1F680..0x1F6FF) {
            emojis.add(String(Character.toChars(i)))
        }

        for (i in 0x2600..0x26FF) {
            emojis.add(String(Character.toChars(i)))
        }

        for (i in 0x2700..0x27BF) {
            emojis.add(String(Character.toChars(i)))
        }

        val contentValues = ContentValues()


        for (emoji in emojis) {
            contentValues.put("code", emoji)
            db!!.insert("icons", null, contentValues)
        }
    }

    @SuppressLint("Range")
    fun printAllIcons() {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM icons", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val code = cursor.getString(cursor.getColumnIndex("code"))
                println("ID: $id, Code: $code")
            } while (cursor.moveToNext())
        }

        cursor.close()
    }

}