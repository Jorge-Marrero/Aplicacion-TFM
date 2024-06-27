package es.tfm.clientetfm.util.bd

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import es.tfm.clientetfm.model.Contacto

class UsuarioBD(private val helper : BDHelper) {

    private val TAG = UsuarioBD::class.simpleName

    fun getAllUsuarios() : List<Contacto> {
        val bd : SQLiteDatabase = helper.readableDatabase
        val query : String = "SELECT * FROM usuarios;"
        val cursor : Cursor = bd.rawQuery(query, arrayOf())
        val l : ArrayList<Contacto> = ArrayList<Contacto>()
        while (cursor.moveToNext()){
            var _id : Int = cursor.getInt(0)
            var nombre : String = cursor.getString(2)
            var tlf : String = cursor.getString(1)
            var prefix : String = cursor.getString(3)
            var c : Contacto = Contacto(_id, nombre, tlf, prefix)
            l.add(c)
        }
        cursor.close()
        return l
    }

    fun saveUsuario(contacto: Contacto) : Contacto {
        try{
            val bd : SQLiteDatabase = helper.readableDatabase
            val values : ContentValues = ContentValues()
            values.put("tlf", contacto.tlf)
            values.put("nombre", contacto.nombre)
            values.put("prefix", contacto.prefix)
            bd.insert("usuarios", null, values)
        }catch (e: SQLiteConstraintException){
            Log.i(TAG, "Usuario ya ingresado en la bd")
        }
        return contacto
    }

    fun dropAll() {
        val bd : SQLiteDatabase = helper.readableDatabase
        bd.delete("usuarios", null, null)
    }

}