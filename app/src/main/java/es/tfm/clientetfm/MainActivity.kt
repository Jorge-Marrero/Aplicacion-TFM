package es.tfm.clientetfm

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import es.tfm.clientetfm.model.Contacto
import es.tfm.clientetfm.model.Usuario
import es.tfm.clientetfm.util.adapter.UsuariosGuardadosAdapter
import es.tfm.clientetfm.util.bd.BDHelper
import es.tfm.clientetfm.util.bd.UsuarioBD
import es.tfm.clientetfm.util.job.scheduleJob
import es.tfm.clientetfm.util.net.NetUtil
import es.tfm.clientetfm.util.net.Threads.ContactosDownloaderThread
import es.tfm.clientetfm.util.notification.NotificationHandler
import io.socket.client.Socket

class MainActivity : AppCompatActivity() {

    lateinit var pD : ProgressDialog

    lateinit var sharedPreferences : SharedPreferences
    var miUsuario : Usuario = Usuario(-1,"", "", "", "")

    private val TAG = MainActivity::class.simpleName

    private lateinit var socket : Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NetUtil.cargarCertificados(this@MainActivity)
        checkRegisteredUser()
        scheduleJob(this@MainActivity)
        NetUtil.lanzarListener(this@MainActivity)

        val cDT: ContactosDownloaderThread = ContactosDownloaderThread(this, getUsuario())
        val th : Thread = Thread(cDT)
        th.start()

        val lv : ListView = findViewById(R.id.contactos_guardados_lv)

        val buscarLista = findViewById<Button>(R.id.search_list)

        buscarLista.setOnClickListener{
            val eT : EditText = findViewById(R.id.search_list_text)
            val filtro : String = eT.text.toString()
            val adapter : UsuariosGuardadosAdapter = lv.adapter as UsuariosGuardadosAdapter
            val size : Int = adapter.count
            val contactosFiltrados : ArrayList<Contacto>? = ArrayList<Contacto>()
            for (i in 0..size-1){
                var contacto : Contacto? = adapter.getItem(i)
                if(contacto?.nombre?.contains(filtro) == true) contactosFiltrados?.add(contacto)
            }
            val newAdapter : UsuariosGuardadosAdapter = UsuariosGuardadosAdapter(this@MainActivity, contactosFiltrados)
            lv.adapter = newAdapter
        }

        val buscarNuevo : Button = findViewById(R.id.search_user)
        buscarNuevo.setOnClickListener{
            val intent : Intent = Intent(this@MainActivity, BuscarUsuarioActivity::class.java)
            startActivity(intent)
        }

        lv.setOnItemClickListener{ parent, view, position, id ->
            val c : Contacto = parent.getItemAtPosition(position) as Contacto
            val intent : Intent = Intent(this@MainActivity, DocumentosCompartidosActivity::class.java)
            intent.putExtra("contacto", c)
            startActivity(intent)
        }
    }


    private fun checkRegisteredUser() {
        sharedPreferences = this@MainActivity.getSharedPreferences("Usuario", Context.MODE_PRIVATE)
        val _id : Int = sharedPreferences.getInt("_id", -1)
        val name : String? = sharedPreferences.getString("nombre", null)
        val tlf : String? = sharedPreferences.getString("tlf", null)
        val prefix : String? = sharedPreferences.getString("prefix", null)
        val passwd : String? = sharedPreferences.getString("contraseña", null)
        if(_id != -1 && name != null && tlf != null && prefix != null && passwd != null){
            Log.i(TAG, "si")
            miUsuario = Usuario(_id, name, prefix, tlf, passwd)
        }else{
            Log.i(TAG, "no")
            val intent = Intent(this@MainActivity, InicioSesionActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        val bDH : BDHelper = BDHelper(this@MainActivity)
        val uBD : UsuarioBD = UsuarioBD(bDH)
        val l : List<Contacto> = uBD.getAllUsuarios()
        val lv : ListView = findViewById(R.id.contactos_guardados_lv)
        val adapter : UsuariosGuardadosAdapter = UsuariosGuardadosAdapter(this@MainActivity, l)
        lv.adapter = adapter
    }

    fun prepareUIStartownload() {
        pD = ProgressDialog(this@MainActivity)
        pD.setMessage("Cargando Contactos")
        pD.setCancelable(false)
        pD.show()
    }

    fun createListOnUI(contactos: List<Contacto>?) {
        val lv : ListView = findViewById(R.id.contactos_guardados_lv)
        val adapter : UsuariosGuardadosAdapter = UsuariosGuardadosAdapter(this@MainActivity, contactos)
        lv.adapter = adapter
    }

    fun prepareUIFinishDownload() {
        pD.dismiss()
    }

    private fun getUsuario(): String {
        return "{\"_id\": \"${miUsuario._id}\"}"
    }
}