package es.tfm.clientetfm

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import es.tfm.clientetfm.model.Usuario
import es.tfm.clientetfm.util.net.Threads.InicioSesionThread

class InicioSesionActivity : AppCompatActivity(){

    lateinit var pD : ProgressDialog
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : Editor

    val TAG = InicioSesionActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_sesion)
        val registrarse = findViewById<Button>(R.id.registrarse_boton)
        registrarse.setOnClickListener{
            val intent : Intent = Intent(this@InicioSesionActivity, CrearUsuarioActivity::class.java)
            startActivity(intent)
        }

        val iniciarSesion = findViewById<Button>(R.id.inicio_sesion_boton)
        val tlf = findViewById<TextView>(R.id.usuario_inicio_sesion).text
        val pref = findViewById<TextView>(R.id.prefijo_inicio_sesion).text
        val passwd = findViewById<TextView>(R.id.contraseña_inicio_sesion).text

        iniciarSesion.setOnClickListener{
            val iST = InicioSesionThread(this@InicioSesionActivity, tlf.toString(), pref.toString(), passwd.toString())
            val thread = Thread(iST)
            thread.start()
            val intent = Intent(this@InicioSesionActivity, MainActivity::class.java)
            startActivity(intent)
        }

        sharedPreferences =  this@InicioSesionActivity.getSharedPreferences("Usuario", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun prepareUIStartownload() {
        pD = ProgressDialog(this@InicioSesionActivity)
        pD.setMessage("Iniciando sesión")
        pD.setCancelable(false)
        pD.show()
    }

    fun createListOnUI(usuario: Usuario?) {
        if (usuario != null) {
            editor.putInt("_id", usuario._id)
            editor.putString("nombre", usuario.nombre)
            editor.putString("tlf", usuario.tlf)
            editor.putString("prefix", usuario.pref)
            editor.putString("contraseña", usuario.contraseña)
            editor.apply()
        }
    }

    fun prepareUIFinishDownload() {
        pD.dismiss()
    }


}

