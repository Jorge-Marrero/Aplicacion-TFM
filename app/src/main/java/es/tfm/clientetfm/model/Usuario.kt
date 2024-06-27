package es.tfm.clientetfm.model

import com.google.gson.annotations.SerializedName

class Usuario(
    @SerializedName("_id") var _id: Int,
    @SerializedName("nombre") var nombre: String,
    @SerializedName("prefix") var pref: String,
    @SerializedName("tlf") var tlf: String,
    @SerializedName("passwd") var contraseña: String
) {

}