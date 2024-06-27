package es.tfm.clientetfm.util.net.Threads

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import es.tfm.clientetfm.DocumentosCompartidosActivity
import es.tfm.clientetfm.model.Contacto
import es.tfm.clientetfm.model.Documento
import es.tfm.clientetfm.util.net.NetUtil

class DocumentosEnviadosThread : Runnable {

    val ctx : Context
    val usuario : String
    val contacto : Contacto?

    private val TAG = DocumentosEnviadosThread::class.simpleName

    constructor(ctx : Context, usuario : String, contacto: Contacto?){
        this.ctx = ctx
        this.usuario = usuario
        this.contacto = contacto
    }

    override fun run() {
        (ctx as DocumentosCompartidosActivity).runOnUiThread{
            ctx.prepareUIStartownload()
        }

        var response : String
        val gsonBuilder : GsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a")
        val gson : Gson = Gson()

        var documentos : List<Documento>? = null
        val contId = "{\"_id\":\"${contacto?._id}\"}"
        try {
            response = NetUtil.obtenerDocusCompartidos(usuario, contId)
            documentos = gson.fromJson(response, Array<Documento>::class.java).toList()
        }catch (e : Exception){
            Log.e(TAG, e.toString())
        }

        (ctx as DocumentosCompartidosActivity).runOnUiThread{
            ctx.createListOnUI(documentos)
        }

        (ctx as DocumentosCompartidosActivity).runOnUiThread{
            ctx.prepareUIFinishDownload()
        }
    }
}