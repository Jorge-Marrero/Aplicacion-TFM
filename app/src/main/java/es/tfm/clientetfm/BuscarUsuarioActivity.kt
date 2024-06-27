package es.tfm.clientetfm

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import es.tfm.clientetfm.model.Contacto
import es.tfm.clientetfm.model.Usuario
import es.tfm.clientetfm.util.adapter.UsuariosDisponiblesAdapter
import es.tfm.clientetfm.util.bd.BDHelper
import es.tfm.clientetfm.util.bd.UsuarioBD
import es.tfm.clientetfm.util.net.Threads.EnviarContactoNuevoThread
import es.tfm.clientetfm.util.net.Threads.NewContactoThread

class BuscarUsuarioActivity :AppCompatActivity() {

    private val TAG = BuscarUsuarioActivity::class.simpleName

    lateinit var pD : ProgressDialog
    lateinit var sharedPreferences : SharedPreferences
    var miUsuario : Usuario = Usuario(-1,"", "", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.buscar_usuario_nuevo)

        checkRegisteredUser()

        val retroceso : ImageButton = findViewById(R.id.volver_buscar_usuario)
        retroceso.setImageResource(R.drawable.backjpeg)
        retroceso.scaleType = ImageView.ScaleType.FIT_CENTER
        retroceso.setOnClickListener{
            val i : Intent = Intent(this@BuscarUsuarioActivity, MainActivity::class.java)
            startActivity(i)
        }

        val texto : EditText = findViewById(R.id.search_list_text_new_user)
        val buscar : Button = findViewById(R.id.search_list_new_user)

        buscar.setOnClickListener{
            Log.i(TAG, texto.text.toString());
            val nCT : NewContactoThread = NewContactoThread(this@BuscarUsuarioActivity, getUsuario(), texto.text.toString())
            val thread : Thread = Thread(nCT)
            thread.start()
        }

        val lv : ListView = findViewById(R.id.buscar_usuario_lista)
        lv.setOnItemClickListener{ parent, view, position, id ->
            val bDH : BDHelper = BDHelper(this@BuscarUsuarioActivity)
            val uBD : UsuarioBD = UsuarioBD(bDH)
            val c : Contacto = parent.getItemAtPosition(position) as Contacto
            uBD.saveUsuario(c)

            val eNCT : EnviarContactoNuevoThread = EnviarContactoNuevoThread(this@BuscarUsuarioActivity, getUsuarioid(), c)
            val thread : Thread = Thread(eNCT)
            thread.start()

            val intent = Intent(this@BuscarUsuarioActivity, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getUsuario(): String {
        val jsonUsuario = "{\"nombre\":\"${miUsuario.nombre}\",\"tlf\":\"${miUsuario.tlf}\",\"prefix\":\"${miUsuario.pref}\"}"
        return jsonUsuario
    }

    private fun getUsuarioid() : String {
        val jsonId = "{\"_id\":\"${miUsuario._id}\",\"nombre\":\"${miUsuario.nombre}\"}"
        return jsonId
    }

    private fun checkRegisteredUser() {
        sharedPreferences = this@BuscarUsuarioActivity.getSharedPreferences("Usuario", Context.MODE_PRIVATE)
        val _id : Int = sharedPreferences.getInt("_id", -1)
        val name : String? = sharedPreferences.getString("nombre", null)
        val tlf : String? = sharedPreferences.getString("tlf", null)
        val prefix : String? = sharedPreferences.getString("prefix", null)
        val passwd : String? = sharedPreferences.getString("contraseña", null)
        if(_id != -1&& name != null && tlf != null && prefix != null && passwd != null){
            Log.i(TAG, "si")
            miUsuario = Usuario(_id, name, prefix, tlf, passwd)
        }else{
            Log.i(TAG, "no")
            val intent = Intent(this@BuscarUsuarioActivity, InicioSesionActivity::class.java)
            startActivity(intent)
        }

    }

    fun prepareUIStartownload() {
        pD = ProgressDialog(this@BuscarUsuarioActivity)
        pD.setMessage("Cargando")
        pD.setCancelable(false)
        pD.show()
    }

    fun createListOnUI(contactos: List<Contacto>?) {
        val lv : ListView = findViewById(R.id.buscar_usuario_lista)
        val adapter : UsuariosDisponiblesAdapter = UsuariosDisponiblesAdapter(this@BuscarUsuarioActivity, contactos)
        lv.adapter = adapter
    }
    
    fun updateListOnUI(contacto: Contacto){
        val lv : ListView = findViewById(R.id.buscar_usuario_lista)
        val adapter : UsuariosDisponiblesAdapter = lv.adapter as UsuariosDisponiblesAdapter
        val contactos : ArrayList<Contacto>? = adapter.getList() as ArrayList<Contacto>
        if (contactos != null){
            //contactos.remove(contacto)
        }
        val newAdapter : UsuariosDisponiblesAdapter = UsuariosDisponiblesAdapter(this@BuscarUsuarioActivity, contactos)
        lv.adapter = newAdapter
    }

    fun prepareUIFinishDownload() {
        pD.dismiss()
    }
}