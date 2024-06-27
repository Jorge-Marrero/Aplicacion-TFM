package es.tfm.clientetfm.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Firma(
    @SerializedName("hash") val hash : String,
    @SerializedName("mensaje") val mensaje : String,
    @SerializedName("opcion") val opcion: Int
) : Serializable {
}