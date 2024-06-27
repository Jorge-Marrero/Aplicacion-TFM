package es.tfm.clientetfm.util.net.Threads

import android.content.Context
import com.google.gson.Gson
import es.tfm.clientetfm.BuscarUsuarioActivity
import es.tfm.clientetfm.model.Contacto
import es.tfm.clientetfm.util.net.NetUtil

class EnviarContactoNuevoThread(val ctx: Context, val usuario: String, val contacto: Contacto) : Runnable{

    override fun run() {
        (ctx as BuscarUsuarioActivity).runOnUiThread{
            ctx.prepareUIStartownload()
        }

        val gson : Gson = Gson()
        val contactoJson : String = gson.toJson(contacto)

        NetUtil.guardarNuevoContacto(contactoJson, usuario)

        (ctx as BuscarUsuarioActivity).runOnUiThread{
            ctx.updateListOnUI(contacto)
        }

        (ctx as BuscarUsuarioActivity).runOnUiThread{
            ctx.prepareUIFinishDownload()
        }
    }

}