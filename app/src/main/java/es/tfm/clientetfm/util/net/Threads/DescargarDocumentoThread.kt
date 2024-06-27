package es.tfm.clientetfm.util.net.Threads

import android.content.Context
import es.tfm.clientetfm.DocumentosCompartidosActivity
import es.tfm.clientetfm.model.Contacto
import es.tfm.clientetfm.model.Documento
import es.tfm.clientetfm.util.net.NetUtil

class DescargarDocumentoThread(val ctx : Context, val doc : Documento, val user : String, val contacto : Contacto?) : Runnable {

    override fun run() {
        (ctx as DocumentosCompartidosActivity).runOnUiThread{
            ctx.prepareUIStartSafe()
        }

         val bytesDoc : ByteArray = NetUtil.descargarDocumento(
             user,
             "{\"_id\":\"${contacto?._id.toString()}\"}",
             doc.nombre + doc.extension
         )

        (ctx).runOnUiThread{
            ctx.guardarDocumento(bytesDoc, doc)
        }

        (ctx).runOnUiThread{
            ctx.endUISafe()
        }
    }
}