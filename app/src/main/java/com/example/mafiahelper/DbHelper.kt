package com.example.mafiahelper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Icon(
    val id: UInt,
    val code: String
)
data class RoleDTO(
    val id: UInt,
    val name: String,
    val isBaseRole: Boolean,
    val isDoNight: Boolean,
    val isCanDie: Boolean,
    val team: Short, // 0 - white, 1 - red, 2 - another
    val actFrequency: Short,
    val icon: Int?,
    val isDoKill: Boolean,
    val isDoSave: Boolean,
    val code: String?
)

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
        fillRolesTableWithStandard(db)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // drop only immutable tables
        db!!.execSQL("DROP TABLE IF EXISTS icons")
        db.execSQL("DROP TABLE IF EXISTS roles")
        onCreate(db)
    }

    fun checkAndResetDataBase(): Boolean {
        this.onUpgrade(this.writableDatabase, 1, 1)
        return true
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

    private fun fillRolesTableWithStandard(db: SQLiteDatabase?) {
        try {
            db!!.execSQL("insert into roles values(NULL, 'Мирный', 1, 0, 1, 0, 0, 439, 0, 0), " +
                    "(NULL, 'Мафия', 1, 1, 1, 1, 1, 459, 1, 0), " +
                    "(NULL, 'Шериф', 1, 1, 1, 0, 1, 231, 0, 0), " +
                    "(NULL, 'Доктор', 1, 1, 1, 0, 1, 1262, 0, 1)")
        }
        catch (e: Exception) {
            throw Exception("Incorrect database link")
        }
    }

    @SuppressLint("Range")
    fun getAllIcons(): List<Icon> {
        val icons = mutableListOf<Icon>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM icons", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val code = cursor.getString(cursor.getColumnIndex("code"))
                icons.add(Icon(id.toUInt(), code))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return icons
    }


    fun getCurrentRole(_id: UInt): RoleDTO? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM roles where id = $_id", null)
        var role: RoleDTO? = null

        try {
            if (cursor.moveToFirst()) {
                do {
                    role = cursorToRoleDTO(cursor)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            return null;
        } finally {
            cursor.close()
        }

        return role
    }

    @SuppressLint("Range")
    private fun cursorToRoleDTO(cursor: Cursor): RoleDTO {
        val id = cursor.getInt(cursor.getColumnIndex("id")).toUInt()
        val name = cursor.getString(cursor.getColumnIndex("name"))
        val isBaseRole = cursor.getInt(cursor.getColumnIndex("isBaseRole")) > 0
        val isDoNight = cursor.getInt(cursor.getColumnIndex("isDoNight")) > 0
        val isCanDie = cursor.getInt(cursor.getColumnIndex("isCanDie")) > 0
        val team = cursor.getShort(cursor.getColumnIndex("team"))
        val actFrequency = cursor.getShort(cursor.getColumnIndex("actFrequency"))
        val icon = cursor.getInt(cursor.getColumnIndex("icon"))
        val isDoKill = cursor.getInt(cursor.getColumnIndex("isDoKill")) > 0
        val isDoSave = cursor.getInt(cursor.getColumnIndex("isDoSave")) > 0
        val code = cursor.getString(cursor.getColumnIndex("code"))

        return RoleDTO(id, name, isBaseRole, isDoNight, isCanDie, team, actFrequency, icon,isDoKill, isDoSave, code)
    }

    fun getBaseRoles(context: Context): List<Role>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT roles.*, icons.code FROM roles, icons where isBaseRole = 1 and icons.id = roles.icon", null)
        val roles = mutableListOf<Role>()

        try {
            if (cursor.moveToFirst()) {
                do {
                    roles.add(Role(context, cursorToRoleDTO(cursor)))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            return null;
        } finally {
            cursor.close()
        }

        return roles
    }

}