package com.example.shoppingg.db

import android.content.Context
import com.example.shoppingg.db.MyDatabase

class UserDAO(private val context: Context) {

    private val dbHelper = MyDatabase(context)

    fun insertUser(name: String, email: String): Long {
        val db = dbHelper.writableDatabase
        val sql = "INSERT INTO users(name, email) VALUES(?, ?)"
        val stmt = db.compileStatement(sql)
        stmt.bindString(1, name)
        stmt.bindString(2, email)
        val id = stmt.executeInsert()
        db.close()
        return id
    }

    fun getAllUsers(): ArrayList<UserModel> {
        val list = ArrayList<UserModel>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                list.add(UserModel(id, name, email))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list
    }

    fun updateUser(id: Int, name: String, email: String): Int {
        val db = dbHelper.writableDatabase
        val sql = "UPDATE users SET name=?, email=? WHERE id=?"
        val stmt = db.compileStatement(sql)
        stmt.bindString(1, name)
        stmt.bindString(2, email)
        stmt.bindLong(3, id.toLong())
        val rows = stmt.executeUpdateDelete()
        db.close()
        return rows
    }

    fun deleteUser(id: Int): Int {
        val db = dbHelper.writableDatabase
        val sql = "DELETE FROM users WHERE id=?"
        val stmt = db.compileStatement(sql)
        stmt.bindLong(1, id.toLong())
        val rows = stmt.executeUpdateDelete()
        db.close()
        return rows
    }
}
