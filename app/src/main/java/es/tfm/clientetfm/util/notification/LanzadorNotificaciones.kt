package es.tfm.clientetfm.util.notification

import android.content.Context
import android.util.Log
import es.tfm.clientetfm.model.Firma

class LanzadorNotificaciones(val ctx : Context) {

    private val TAG = LanzadorNotificaciones::class.simpleName
    private val nH = NotificationHandler(ctx)

    fun gestionarNotificaciones(firma: Firma){
        when(firma.opcion){
            1 -> lanzarNotificacionUsuarioAgregado(firma.mensaje)
            2 -> lanzarNotificacionDocumentoCompartido(firma.mensaje)
            3 -> lanzarNotificacionDescargaDocumento(firma.mensaje)
        }
    }

    private fun lanzarNotificacionDescargaDocumento(mensaje: String) {
        val not = nH.createNotification("Descarga documento", mensaje)
        nH.getManagerSingle().notify(3, not.notification)
        nH.publishGroup()
    }

    private fun lanzarNotificacionDocumentoCompartido(mensaje: String) {
        val not = nH.createNotification("Documento nuevo", mensaje)
        nH.getManagerSingle().notify(2, not.notification)
        nH.publishGroup()
    }

    private fun lanzarNotificacionUsuarioAgregado(mensaje: String){
        val not = nH.createNotification("Nuevo usuario", mensaje)
        nH.getManagerSingle().notify(1, not.notification)
        nH.publishGroup()
    }
}