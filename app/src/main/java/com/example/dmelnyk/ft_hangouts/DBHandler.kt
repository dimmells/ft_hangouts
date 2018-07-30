package com.example.dmelnyk.ft_hangouts

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

val DATABASE_NAME = "PhoneBookDB"
val TABLE_NAME = "Contacts"
val COL_ID = "id"
val COL_FIRST_NAME = "firstName"
val COL_LAST_NAME = "lastName"
val COL_PHONE_NUMBER = "phoneNumber"
val COL_EMAIL = "email"
val COL_PHOTO = "photo"

class DBHandler(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(p0: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_FIRST_NAME + " VARCHAR(256)," +
                COL_LAST_NAME + " VARCHAR(256)," +
                COL_PHONE_NUMBER + " VARCHAR(256)," +
                COL_EMAIL + " VARCHAR(256)," +
                COL_PHOTO + " VARCHAR(256))"
        p0?.execSQL(createTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun addContactToDb(contact: Contact) : Int {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_FIRST_NAME, contact.first_name)
        cv.put(COL_LAST_NAME, contact.last_name)
        cv.put(COL_PHONE_NUMBER, contact.phone_number)
        cv.put(COL_EMAIL, contact.email)
        cv.put(COL_PHOTO, contact.photo)
        var result = db.insert(TABLE_NAME, null, cv)
        if (result == -1.toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            return 0
        }
        else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            return 1
        }
    }

    fun getAllContact() : MutableList<Contact> {
        var list: MutableList<Contact> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM " + TABLE_NAME
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                var contact = Contact()
                contact.id = result.getInt(result.getColumnIndex(COL_ID))
                contact.first_name = result.getString(result.getColumnIndex(COL_FIRST_NAME))
                contact.last_name = result.getString(result.getColumnIndex(COL_LAST_NAME))
                contact.phone_number = result.getString(result.getColumnIndex(COL_PHONE_NUMBER))
                contact.email= result.getString(result.getColumnIndex(COL_EMAIL))
                contact.photo = result.getString(result.getColumnIndex(COL_PHOTO))
                list.add(contact)
            } while (result.moveToNext())
        }

        result.close()
        db.close()

        return list
    }

    fun updateContact(contactToUpdate: Contact) {
        val db = this.writableDatabase
        val query = "SELECT * FROM " + TABLE_NAME
        val result = db.rawQuery(query, null)

        var cv = ContentValues()
        cv.put(COL_FIRST_NAME, contactToUpdate.first_name)
        cv.put(COL_LAST_NAME, contactToUpdate.last_name)
        cv.put(COL_PHONE_NUMBER, contactToUpdate.phone_number)
        cv.put(COL_EMAIL, contactToUpdate.email)
//        cv.put(COL_PHOTO, contactToUpdate.photo)
        db.update(TABLE_NAME, cv, COL_ID + "=?", arrayOf(contactToUpdate.id.toString()))

        result.close()
        db.close()
    }

    fun deleteContact(id: Int) {
        val db = writableDatabase

        db.delete(TABLE_NAME, COL_ID + "=?", arrayOf(id.toString()))
        db.close()
    }
}