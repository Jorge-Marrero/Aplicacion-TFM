package es.tfm.clientetfm.util.net.Threads

import android.content.Context
import es.tfm.clientetfm.util.net.NetUtil
import java.io.File

class EnviarDocumentoThread(val ctx : Context, val usuario: String, val contactoId :String, val archivo: File) : Runnable{

    private val TAG = EnviarDocumentoThread::class.simpleName

    override fun run() {
        NetUtil.enviarDocumento(usuario, contactoId, archivo)
    }
}