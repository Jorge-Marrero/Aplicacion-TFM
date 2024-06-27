package es.tfm.clientetfm.util.job

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.SharedPreferences
import es.tfm.clientetfm.model.Usuario
import es.tfm.clientetfm.util.net.Threads.RefreshIPThread
import kotlinx.coroutines.*

class JobRefreshIp : JobService() {

    private val jobScope = CoroutineScope(Dispatchers.IO)
    lateinit var sharedPreferences : SharedPreferences
    var miUsuario : Usuario = Usuario(-1,"", "", "", "")

    private fun checkRegisteredUser(ctx : Context) {
        sharedPreferences = ctx.getSharedPreferences("Usuario", Context.MODE_PRIVATE)
        val _id : Int = sharedPreferences.getInt("_id", -1)
        val name : String? = sharedPreferences.getString("nombre", null)
        val tlf : String? = sharedPreferences.getString("tlf", null)
        val prefix : String? = sharedPreferences.getString("prefix", null)
        val passwd : String? = sharedPreferences.getString("contraseña", null)
        if(_id != -1 && name != null && tlf != null && prefix != null && passwd != null){
            miUsuario = Usuario(_id, name, prefix, tlf, passwd)
        }else{
            return
        }

    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        val ctx =  applicationContext
        checkRegisteredUser(ctx)
        jobScope.launch {
            val rIT = RefreshIPThread(ctx, miUsuario._id.toString())
            rIT.executeInBackground()
        }
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        jobScope.cancel()
        return true
    }
}