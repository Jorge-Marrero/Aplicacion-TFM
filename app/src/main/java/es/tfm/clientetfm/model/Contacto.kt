package es.tfm.clientetfm.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Contacto(
    @SerializedName("_id") var _id: Int,
    @SerializedName("nombre") var nombre: String,
    @SerializedName("tlf") var tlf: String,
    @SerializedName("prefix") var prefix: String,
) : Serializable {

}