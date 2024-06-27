package es.tfm.clientetfm.util.net

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import es.tfm.clientetfm.util.notification.LanzadorNotificaciones
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.io.File
import es.tfm.clientetfm.R
import es.tfm.clientetfm.model.Firma
import java.io.InputStream
import java.security.PublicKey

object NetUtil {

    val baseURL : String = "https://192.168.190.165:9999"
    val TAG : String? =  NetUtil::class.simpleName
    private lateinit var client : OkHttpClient
    private val JSON = "application/json; charset=utf-8".toMediaType()

    private lateinit var webSocket: WebSocket
    private lateinit var lNot : LanzadorNotificaciones

    private val gson = Gson()

    private lateinit var publicKey: PublicKey

    private var listener = object : WebSocketListener(){
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.i(TAG, "Conexión WebSocket establecida")
            webSocket.send("enviarCertificado")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val jsonObject = gson.fromJson(text, JsonObject::class.java)
            if(!jsonObject.has("event")){
                gestionarNotificaciones(text)
            }else{
                val event = jsonObject.get("event").asString
                when (event) {
                    "enviarCertificado" -> gestionarCertificado(jsonObject.get("pk").toString())
                    else -> Log.e(TAG,"TAG, Evento no reconocido")
                }
            }
        }

        private fun gestionarNotificaciones(text: String){
            val firma : Firma = gson.fromJson(text,  Firma::class.java)
            Log.i(TAG, "Mensaje: ${firma.mensaje}")
            Log.i(TAG, "Hash: ${firma.hash}")
            if(LoadCertificates.verificarFirma(publicKey, firma.mensaje, firma.hash)){
                lNot.gestionarNotificaciones(firma)
                Log.i(TAG, "Mensaje recibido")
            }else{
                Log.e(TAG, "Notificación firmada mal")
            }
        }

        private fun gestionarCertificado(cp: String){
            publicKey = LoadCertificates.leerClavePública(cp)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {

            Log.i(TAG, "Servidor cerrado")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, t.message.toString())
        }
    }

    fun cargarCertificados(ctx:Context){
        val inputStream: InputStream = ctx.resources.openRawResource(R.raw.cacertificado)
        val certCLient : InputStream = ctx.resources.openRawResource(R.raw.cert)
        client = LoadCertificates.cargarCetificado(inputStream, certCLient);
        inputStream.close();
        certCLient.close();
    }

    fun lanzarListener(ctx: Context){
        lNot = LanzadorNotificaciones(ctx)

        val request = Request.Builder()
            .url(baseURL)
            .build()

        webSocket = client.newWebSocket(request, listener)
    }

    fun descargarUsuariosGuardados(usuario: String): String{
        val body = usuario.toRequestBody(JSON)
        val request = Request.Builder()
            .url(baseURL + "/obtenerContactos")
            .post(body)
            .build()

        try{
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw RuntimeException("Failed: HTTP error code: ${response.code}")
                }
                val jsonRes = response.body?.string()?.let { JSONObject(it) }
                if (jsonRes != null) {
                    return jsonRes.getString("message")
                }
            }
        }catch(e : Exception){
            Log.e(TAG, e.toString())
            return ""
        }
        return ""
    }

    fun buscarUsuariosNoRegistrados(usuario: String, busqueda: String) : String{
        val busquedaBody = "{\"usuario\":${usuario},\"busqueda\":\"$busqueda\"}"
        val body = busquedaBody.toRequestBody(JSON)
        val request = Request.Builder()
            .url(baseURL + "/buscarContactoNuevo")
            .post(body)
            .build()

        try{
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw RuntimeException("Failed: HTTP error code: ${response.code}")
                }
                val jsonRes = response.body?.string()?.let { JSONObject(it) }
                if (jsonRes != null) {
                    return jsonRes.getString("message")
                }
            }
        }catch(e : Exception){
            Log.e(TAG, e.toString())
            return ""
        }
        return ""
    }

    fun guardarNuevoContacto(contacto: String, usuario: String) : Boolean{
        val datos = "{\"contacto\":$contacto,\"usuario\":${usuario}}"
        val body = datos.toRequestBody(JSON)
        val request = Request.Builder()
            .url(baseURL + "/agregarContactoNuevo")
            .post(body)
            .build()
        try{
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw RuntimeException("Failed: HTTP error code: ${response.code}")
                }
                println("Response body: ${response.body?.string()}")
                return true
            }
        }catch(e: RuntimeException) {
            Log.e(TAG, e.toString())
            return false
        }
    }

    fun obtenerDocusCompartidos(usuario: String, contacto: String) : String{
        val datos = "{\"contacto\":$contacto,\"usuario\":${usuario}}"
        val body = datos.toRequestBody(JSON)
        val request = Request.Builder()
            .url(baseURL + "/recuperarDocumentos")
            .post(body)
            .build()
        try{
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw RuntimeException("Failed: HTTP error code: ${response.code}")
                }
                val jsonRes = response.body?.string()?.let { JSONObject(it) }
                if (jsonRes != null) {
                    return jsonRes.getString("archivosCompartidos")
                }
            }
        }catch(e: RuntimeException) {
            Log.e(TAG, e.toString())
            return ""
        }
        return ""
    }

    fun descargarUsuario(ctx: Context, tlf: String, prefix: String, contraseña: String): String? {
        val datos = "{\"tlf\":\"$tlf\",\"prefix\":\"$prefix\",\"passwd\":\"$contraseña\"}"
        val body = datos.toRequestBody(JSON)

        val request = Request.Builder()
            .url(baseURL + "/iniciarSesion")
            .post(body)
            .build()

        try{
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw RuntimeException("Failed: HTTP error code: ${response.code}")
                }
                val jsonRes = response.body?.string()?.let { JSONObject(it) }
                if (jsonRes != null) {
                    return jsonRes.getString("message")
                }
            }
        }catch(e : Exception){
            Log.e(TAG, e.toString())
            return ""
        }
        return ""
    }

    fun crearNuevoUsuario(ctx: Context, json: String) : String{
        val body = json.toRequestBody(JSON)

        val request = Request.Builder()
            .url(baseURL + "/crearUsuario")
            .post(body)
            .build()

        try{
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw RuntimeException("Failed: HTTP error code: ${response.code}")
                }
                val jsonRes = response.body?.string()?.let { JSONObject(it) }
                if (jsonRes != null) {
                    println("Response body: ${jsonRes.getString("message")}")
                }
                if (jsonRes != null) {
                    return jsonRes.getString("message")
                }
            }
        }catch(e: RuntimeException) {
            Log.e(TAG, e.toString())
            return ""
        }
        return ""
    }

    fun descargarDocumento(usuario: String, contacto: String, documento: String): ByteArray{
        val datos = "{\"contacto\":$contacto,\"usuario\":${usuario}}"
        val body = datos.toRequestBody(JSON)
        val request = Request.Builder()
            .url("$baseURL/descargarDocumento/$documento")
            .post(body)
            .build()
        try{
            client.newCall(request).execute().use { response->
                if(!response.isSuccessful){
                    throw RuntimeException("Failed: HTTP error code: ${response.code}")
                }
                val byteBody = response.body?.bytes()
                if(byteBody != null){
                    return byteBody
                }
            }
        }catch(e: Exception){
            Log.e(TAG, e.toString())
            return ByteArray(0)
        }
        return ByteArray(0);
    }

    fun enviarDocumento(usuario: String, contactoId: String, documento: File) : Boolean{
        val datos = "{\"contacto\":{\"_id\":\"$contactoId\"},\"usuario\":${usuario}}"
        val filePart = RequestBody.create("application/octet-stream".toMediaType(), documento)
        val dataPart = RequestBody.create("application/json".toMediaType(), datos)

        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("datos", null, dataPart)
            .addFormDataPart("file", documento.name, filePart)
            .build()

        val request = Request.Builder()
            .url("$baseURL/subirDocumento")
            .post(body)
            .build()

        try{
            client.newCall(request).execute().use { response ->
                return true
            }
        }catch (e: Exception){
            Log.e(TAG, e.toString())
        }
        return false
    }

    fun enviarIP(usuario: String) : Boolean{
        val body = usuario.toRequestBody(JSON)
        val request = Request.Builder()
            .url("$baseURL/actualizarIP")
            .post(body)
            .build()

        try{
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw RuntimeException("Failed: HTTP error code: ${response.code}")
                }
                println("Response body: ${response.body?.string()}")
                return true
            }
        }catch (e: RuntimeException) {
            Log.e(TAG, e.toString())
            return false
        }
    }
}