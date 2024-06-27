package es.tfm.clientetfm.util.bd

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BDHelper(ctx: Context): SQLiteOpenHelper(ctx, "bdClienteTFM", null, 1) {

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("""CREATE TABLE usuarios( 
                _id INT PRIMARY KEY,
                tlf TEXT,
                nombre TEXT,
                prefix TEXT);""")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

}