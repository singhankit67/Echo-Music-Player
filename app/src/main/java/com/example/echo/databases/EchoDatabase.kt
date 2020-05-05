package com.example.echo.databases


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.widget.DialogTitle
import com.example.echo.Songs

import java.lang.Exception

class EchoDatabase : SQLiteOpenHelper {
    var _songList = ArrayList<Songs>()
    object Staticated{
        val DB_VERSION = 1
        val DB_NAME = "FavoriteDatabase"
        val COLUMN_ID = "SongID" // column name
        val COLUMN_SONG_TITLE = "SongTitle"
        val COLUMN_SONG_PATH = "SongPath"
        val COLUMN_SONG_ARTIST = "SongArtist"
        val TABLE_NAME = "FavoriteTable"  // table name

    }
    override fun onCreate(sqliteDatabase: SQLiteDatabase?) {
        sqliteDatabase?.execSQL(
            "CREATE TABLE " + Staticated.TABLE_NAME + "( " + Staticated.COLUMN_ID + " INTEGER," + Staticated.COLUMN_SONG_ARTIST + " STRING," + Staticated.COLUMN_SONG_TITLE + " STRING,"
                    + Staticated.COLUMN_SONG_PATH + " STRING);"
        )
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //this was basically to open the sql database
    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(
        context,
        name,
        factory,
        version
    )
    constructor(context: Context? ) : super(
        context,
        Staticated.DB_NAME,
        null,
        Staticated.DB_VERSION
    )

    fun storeAsFavorite(id: Int?, artist: String?, songTitle: String?, path: String?) {
        val db = this.writableDatabase //to open the database
        var contentValues = ContentValues()  //to store values to be executed by content resolver
            contentValues.put(Staticated.COLUMN_ID, id)
            contentValues.put(Staticated.COLUMN_SONG_ARTIST, artist)
            contentValues.put(Staticated.COLUMN_SONG_TITLE, songTitle)
            contentValues.put(Staticated.COLUMN_SONG_PATH, path)
            db.insert(Staticated.TABLE_NAME, null, contentValues) // to add the values in the database
            db.close()// to close the database

    }
    fun queryDBList(): ArrayList<Songs>? {
        try {
            val db = this.readableDatabase //to read thedatabase
            val query_params = "SELECT * FROM " + Staticated.TABLE_NAME  //queryfired to select every row present in the table
            var cSor = db.rawQuery(query_params, null) //this is done for executing the query
            if (cSor.moveToFirst()) { //to start from first value in the database
                do {
                    var _id =
                        cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID)) //if the given column id exists than return that else throw an error
                    var _artist = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_ARTIST))
                    var _title = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                    var _songPath = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_PATH))
                    _songList.add(
                        Songs(
                            _id.toLong(),
                            _title,
                            _artist,
                            _songPath,
                            0
                        )
                    )  //this is used to store anythin present in the list
                } while (cSor.moveToNext())   //to check that weather we have reached last or not
            } else {//if there is not anything returned inside the list
                return null
            }
        }catch(e: Exception){
            e.printStackTrace()
        }
        return _songList
    }
    fun checkifIdExists(_id:Int): Boolean{
        var storeId = -1090
        val db = this.readableDatabase
        val query_params = "SELECT * FROM " + Staticated.TABLE_NAME + " WHERE SongId = '$_id'"
        val cSor = db.rawQuery(query_params,null)
        if(cSor.moveToFirst())
        {
            do{
                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
            }while(cSor.moveToNext())
        }else{
            return false
        }
        return storeId != -1090
    }
    fun deleteFavourite(_id: Int){
        val db = this.writableDatabase
        db.delete(Staticated.TABLE_NAME,Staticated.COLUMN_ID + "=" + _id,null)
        db.close()
    }
    fun checkSize(): Int{
        var counter = 0
        val db = this.readableDatabase
        var query_params = "SELECT * FROM " + Staticated.TABLE_NAME
        val cSor = db.rawQuery(query_params,null)
        if(cSor.moveToFirst()){
            do{
                counter = counter + 1
            }while(cSor.moveToNext())
        }else{
            return 0
        }
        return counter
    }
}
