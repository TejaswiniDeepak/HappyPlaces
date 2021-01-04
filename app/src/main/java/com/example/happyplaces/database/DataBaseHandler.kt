package com.example.happyplaces.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.happyplaces.models.HappyPlaceModel

class DataBaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "HappyPlacesDatabase"
        private const val DATABASE_VERSION = 1
        private const val TABLE_HAPPY_PLACE = "HappyPlacesTable"
        private const val TABLE_NEW = "testtable"

        private const val KEY_ID="_id"
        private const val KEY_TITLE="title"
        private const val KEY_IMAGE="image"
        private const val KEY_DESCRIPTION="description"
        private const val KEY_DATE="date"
        private const val KEY_LOCATION="location"
        private const val KEY_LONGITUDE="longitude"
        private const val KEY_LATITUDE="latitude"

        private const val KEY_NAME="name"
        private const val KEY_NUM="id"
    }

    /**override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_HAPPY_PLACES_TABLE=("CREATE TABLE" + TABLE_HAPPY_PLACE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION +" TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)")
        db?.execSQL(CREATE_HAPPY_PLACES_TABLE)

//        TODO("Not yet implemented")
    }*/
    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_HAPPY_PLACE_TABLE = ("CREATE TABLE " + TABLE_HAPPY_PLACE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)")
        db?.execSQL(CREATE_HAPPY_PLACE_TABLE)


     val CREATE_TABLE = ("CREATE TABLE " + TABLE_NEW + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT)")
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //TODO("Not yet implemented")
        val DROP_TABLE="DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE"
        db?.execSQL(DROP_TABLE)

        val DROP_TABLE_NEW="DROP TABLE IF EXISTS $TABLE_NEW"
        db?.execSQL(DROP_TABLE_NEW)
        onCreate(db)
    }

    fun addHappyPlaces(happyPlace:HappyPlaceModel):Long{
        val db=this.writableDatabase
        val contentValues=ContentValues()

        contentValues.put(KEY_DESCRIPTION,happyPlace.description)
        contentValues.put(KEY_TITLE,happyPlace.title)
        contentValues.put(KEY_IMAGE,happyPlace.image)
        contentValues.put(KEY_DATE,happyPlace.date)
        contentValues.put(KEY_LOCATION,happyPlace.location)
        contentValues.put(KEY_LATITUDE,happyPlace.latitude)
        contentValues.put(KEY_LONGITUDE,happyPlace.longitude)

        val result=db.insert(TABLE_HAPPY_PLACE,null,contentValues)
        db.close()
        return result
    }
    fun tablenewadd(id:Int,nam:String):Long{
        val db=this.writableDatabase
        val contentValues=ContentValues()

        contentValues.put(KEY_NUM,id)
        contentValues.put(KEY_NAME,nam)

        val res=db.insert(TABLE_NEW,null,contentValues)
        db.close()
        return res
    }
}