package es.tfm.clientetfm

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.tfm.clientetfm.model.Usuario
import es.tfm.clientetfm.util.net.Threads.CrearUsuarioThread

class CrearUsuarioActivity : AppCompatActivity() {

    lateinit var pD : ProgressDialog
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor: Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_usuario_nuevo)

        sharedPreferences = this@CrearUsuarioActivity.getSharedPreferences("Usuario", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val boton = findViewById<Button>(R.id.boton_crear)
        boton.setOnClickListener{

            val nombre = findViewById<TextView>(R.id.nombre_crear)
            val pref = findViewById<TextView>(R.id.pref_crear)
            val tlf = findViewById<TextView>(R.id.tlf_crear)
            val passwd = findViewById<TextView>(R.id.contraseña_crear)

            if(nombre.text != "" && pref.text != "" && tlf.text != "" && passwd.text != ""){
                val usuario = Usuario(-1, nombre.text.toString(), pref.text.toString(), tlf.text.toString(), passwd.text.toString())
                val cUT = CrearUsuarioThread(this@CrearUsuarioActivity, usuario)
                val thread = Thread(cUT)
                thread.start()
                val intent = Intent(this@CrearUsuarioActivity, MainActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this@CrearUsuarioActivity, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun prepareUIStartownload() {
        pD = ProgressDialog(this@CrearUsuarioActivity)
        pD.setMessage("Creando Usuario")
        pD.setCancelable(false)
        pD.show()
    }

    fun updateListOnUI(usuario: Usuario, respuesta: String) {
        if(respuesta == ""){
            Toast.makeText(this@CrearUsuarioActivity, "No se ha podido crear el usuario", Toast.LENGTH_SHORT).show()
            return
        }
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