package es.tfm.clientetfm.util.net.Threads

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import es.tfm.clientetfm.MainActivity
import es.tfm.clientetfm.model.Contacto
import es.tfm.clientetfm.util.bd.BDHelper
import es.tfm.clientetfm.util.bd.UsuarioBD
import es.tfm.clientetfm.util.net.NetUtil

class ContactosDownloaderThread(val ctx: Context, val usuario: String) : Runnable {

    private val TAG = ContactosDownloaderThread::class.simpleName

    override fun run() {
        (ctx as MainActivity).runOnUiThread{
            ctx.prepareUIStartownload()
        }

        val response : String
        val gsonBuilder : GsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a")
        val gson : Gson = Gson()

        var contactos : List<Contacto>? = null

        try {
            response = NetUtil.descargarUsuariosGuardados(usuario)
            Log.i(TAG, response)
            contactos = gson.fromJson(response, Array<Contacto>::class.java).toList()
        }catch (e : Exception){
            Log.e(TAG, e.toString())
        }

        val bDH : BDHelper = BDHelper(this.ctx)
        val uBD : UsuarioBD = UsuarioBD(bDH)
        uBD.dropAll()
        if (contactos != null) {
            for (c in contactos){
                try{
                    uBD.saveUsuario(c)
                }catch(e: SQLiteConstraintException){
                    Log.i(TAG, "Usuario ya guardado")
                }
            }
        }

        (ctx as MainActivity).runOnUiThread{
            ctx.createListOnUI(contactos)
        }

        (ctx as MainActivity).runOnUiThread{
            ctx.prepareUIFinishDownload()
        }
    }
}