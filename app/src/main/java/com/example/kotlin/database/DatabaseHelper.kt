package com.example.kotlin.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :  SQLiteOpenHelper (context, DB_NAME, null, DB_VERSION){
    companion object{
        private val DB_NAME = "event"
        private val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        TODO("Not yet implemented")
        val CREATE_DATABASE = "CREATE DATABASE $"
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}