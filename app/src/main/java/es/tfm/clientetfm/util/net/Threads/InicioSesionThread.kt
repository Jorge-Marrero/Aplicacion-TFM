package es.tfm.clientetfm.util.net.Threads

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import es.tfm.clientetfm.InicioSesionActivity
import es.tfm.clientetfm.model.Usuario
import es.tfm.clientetfm.util.net.NetUtil


class InicioSesionThread(val ctx: Context, val tlf: String, val prefix : String, val contraseña : String) : Runnable{

    private val TAG = InicioSesionActivity::class.simpleName

    override fun run() {
        (ctx as InicioSesionActivity).runOnUiThread{
            ctx.prepareUIStartownload()
        }

        val response : String
        val gsonBuilder : GsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a")
        val gson : Gson = Gson()

        lateinit var usuario : Usuario

        try {
            response = NetUtil.descargarUsuario(ctx, tlf, prefix, contraseña).toString()
            usuario = gson.fromJson(response, Usuario::class.java)
        }catch (e : Exception){
            Log.e(TAG,e.toString() )
        }
        (ctx).runOnUiThread{
            ctx.createListOnUI(usuario)
        }

        (ctx).runOnUiThread{
            ctx.prepareUIFinishDownload()
        }
    }
}