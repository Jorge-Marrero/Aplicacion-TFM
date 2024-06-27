package es.tfm.clientetfm.util.net.Threads

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import es.tfm.clientetfm.CrearUsuarioActivity
import es.tfm.clientetfm.model.Usuario
import es.tfm.clientetfm.util.net.NetUtil

class CrearUsuarioThread(val ctx: Context, val usuario : Usuario) : Runnable {

    private val TAG = CrearUsuarioActivity::class.simpleName

    override fun run() {
        (ctx as CrearUsuarioActivity).runOnUiThread{
            ctx.prepareUIStartownload()
        }

        val gson : Gson = Gson()
        val json : String = gson.toJson(usuario)
        val respuesta = NetUtil.crearNuevoUsuario(ctx, json)
        val usuarioCreado = gson.fromJson(respuesta, Usuario::class.java)

        (ctx).runOnUiThread{
            ctx.updateListOnUI(usuarioCreado, respuesta)
        }

        (ctx).runOnUiThread{
            ctx.prepareUIFinishDownload()
        }
    }
}