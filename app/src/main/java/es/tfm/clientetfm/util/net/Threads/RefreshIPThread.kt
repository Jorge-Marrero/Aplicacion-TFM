package es.tfm.clientetfm.util.net.Threads

import android.content.Context
import es.tfm.clientetfm.util.net.NetUtil

class RefreshIPThread(val ctx: Context, val id: String) {
    suspend fun executeInBackground() {
        NetUtil.cargarCertificados(ctx)
        NetUtil.enviarIP("{\"_id\": \"${id}\"}")
    }
}