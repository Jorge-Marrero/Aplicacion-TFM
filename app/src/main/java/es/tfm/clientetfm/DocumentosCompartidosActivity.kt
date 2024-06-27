package es.tfm.clientetfm

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import es.tfm.clientetfm.model.Contacto
import es.tfm.clientetfm.model.Documento
import es.tfm.clientetfm.model.Usuario
import es.tfm.clientetfm.util.adapter.DocumentosAdapter
import es.tfm.clientetfm.util.net.Threads.DescargarDocumentoThread
import es.tfm.clientetfm.util.net.Threads.DocumentosEnviadosThread
import es.tfm.clientetfm.util.net.Threads.EnviarDocumentoThread
import java.io.File
import java.io.FileOutputStream

class DocumentosCompartidosActivity : AppCompatActivity() {

    lateinit var pD : ProgressDialog
    private val TAG = DocumentosCompartidosActivity::class.simpleName

    private val rutaDescargas = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path

    lateinit var sharedPreferences : SharedPreferences
    var miUsuario : Usuario = Usuario(-1,"", "", "", "")

    private lateinit var contactoId : String

    private fun getUsuario(): String {
        return "{\"_id\":\"${miUsuario._id}\",\"nombre\":\"${miUsuario.nombre}\"}"
    }

    @SuppressLint("Range")
    private fun obtenerNombreArchivo(uri: Uri): String {
        var fileName = "archivo_desconocido"
        val cursor = this@DocumentosCompartidosActivity.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                if (!displayName.isNullOrEmpty()) {
                    fileName = displayName
                }
            }
        }
        return fileName
    }

    fun getFileFromContentUri(contentUri: Uri): File? {
        val inputStream = this@DocumentosCompartidosActivity.contentResolver.openInputStream(contentUri)
        val fileName = obtenerNombreArchivo(contentUri)
        val outputFile = File(this@DocumentosCompartidosActivity.cacheDir, fileName)

        inputStream?.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
            }
        }

        return outputFile
    }

    private val pickFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                val file: File? = getFileFromContentUri(uri)
                if (file != null && file.exists()) {
                    val eDT = EnviarDocumentoThread(this@DocumentosCompartidosActivity, getUsuario(), contactoId, file)
                    val thread = Thread(eDT)
                    thread.start()
                    recreate()
                } else {
                    Log.i(TAG, file?.length().toString())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.documentos_compartidos)

        checkRegisteredUser()

        val volver : ImageButton = findViewById(R.id.boton_regreso_doc_comp)
        volver.scaleType = ImageView.ScaleType.FIT_CENTER
        volver.setOnClickListener{
            val i : Intent = Intent(this@DocumentosCompartidosActivity, MainActivity::class.java)
            startActivity(i)
        }
        val contacto = intent.getSerializableExtra("contacto") as? Contacto
        if (contacto != null) {
            contactoId = contacto._id.toString()
        }
        val dET: DocumentosEnviadosThread = DocumentosEnviadosThread(this@DocumentosCompartidosActivity, getUsuario(), contacto)
        val hilo : Thread = Thread(dET)
        hilo.start()

        val lv : ListView = findViewById(R.id.doc_list_view)

        lv.setOnItemClickListener{ parent, view, position, id ->
            val d : Documento = parent.getItemAtPosition(position) as Documento
            val dDT = DescargarDocumentoThread(this@DocumentosCompartidosActivity, d, getUsuario(), contacto)
            val thread : Thread = Thread(dDT)
            thread.start()
        }

        val enviarButton : Button = findViewById(R.id.enviarDocumento)

        enviarButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            pickFile.launch(intent)
        }
    }

    private fun checkRegisteredUser() {
        sharedPreferences = this@DocumentosCompartidosActivity.getSharedPreferences("Usuario", Context.MODE_PRIVATE)
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
            val intent = Intent(this@DocumentosCompartidosActivity, InicioSesionActivity::class.java)
            startActivity(intent)
        }
    }

    fun prepareUIStartownload() {
        pD = ProgressDialog(this@DocumentosCompartidosActivity)
        pD.setMessage("Cargando Contactos")
        pD.setCancelable(false)
        pD.show()
    }

    fun createListOnUI(documentos: List<Documento>?) {
        val lv : ListView = findViewById(R.id.doc_list_view)
        val adapter : DocumentosAdapter = DocumentosAdapter(this@DocumentosCompartidosActivity, documentos)
        lv.adapter = adapter
    }

    fun prepareUIFinishDownload() {
        pD.dismiss()
    }

    fun prepareUIStartSafe() {
        pD = ProgressDialog(this@DocumentosCompartidosActivity)
        pD.setMessage("Guardando documento")
        pD.setCancelable(false)
        pD.show()
    }

    fun endUISafe() {
        pD.dismiss()
    }

    fun guardarDocumento(docArray : ByteArray, docNombre : Documento) {
        if(docArray.size > 0){
            val fOS = FileOutputStream("${rutaDescargas}/${docNombre.nombre}${docNombre.extension}")
            fOS.write(docArray)
            fOS.close()
            Toast.makeText(this@DocumentosCompartidosActivity,"Documento guardado en descargas", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this@DocumentosCompartidosActivity,"No se ha encontardo el documento", Toast.LENGTH_LONG).show()
        }
    }
}