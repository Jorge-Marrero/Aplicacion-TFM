package es.tfm.clientetfm.util.net.Threads

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import es.tfm.clientetfm.BuscarUsuarioActivity
import es.tfm.clientetfm.model.Contacto
import es.tfm.clientetfm.util.net.NetUtil

class NewContactoThread : Runnable {

    val ctx : Context
    val busqueda : String
    val usuario: String
    private val TAG = NewContactoThread::class.simpleName

    constructor(ctx : Context, usuario: String, busqueda : String){
        this.ctx = ctx
        this.busqueda = busqueda
        this.usuario = usuario
    }

    override fun run() {
        (ctx as BuscarUsuarioActivity).runOnUiThread{
            ctx.prepareUIStartownload()
        }

        var response : String
        val gsonBuilder : GsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a")
        val gson : Gson = Gson()

        var contactos : List<Contacto>? = null

        try {
            response = NetUtil.buscarUsuariosNoRegistrados(usuario, busqueda)
            contactos = gson.fromJson(response, Array<Contacto>::class.java).toList()
        }catch (e : Exception){
            Log.e(TAG, e.toString())
        }

        (ctx as BuscarUsuarioActivity).runOnUiThread{
            ctx.createListOnUI(contactos)
        }

        (ctx as BuscarUsuarioActivity).runOnUiThread{
            ctx.prepareUIFinishDownload()
        }

    }
}