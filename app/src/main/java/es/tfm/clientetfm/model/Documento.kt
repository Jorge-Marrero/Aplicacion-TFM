package es.tfm.clientetfm.model

import com.google.gson.annotations.SerializedName

class Documento {

    @SerializedName("id")
    val id : Int = -1

    @SerializedName("nombre")
    val nombre : String

    @SerializedName("ext")
    val extension: String

    constructor(nombre: String, extension: String){
        this.nombre = nombre
        this.extension = extension
    }
}