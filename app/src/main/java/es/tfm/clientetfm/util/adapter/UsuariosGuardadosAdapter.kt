package es.tfm.clientetfm.util.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import es.tfm.clientetfm.R
import es.tfm.clientetfm.model.Contacto

class UsuariosGuardadosAdapter(private val ctx: Context, private val contactos: List<Contacto>?) : BaseAdapter() {

    override fun getCount(): Int {
        if (contactos == null) return 0
        return contactos.size
    }

    override fun getItem(p0: Int): Contacto? {
        if (contactos == null) return null
        return contactos.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view = p1
        if (p1 == null){
            val inflater: LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.contacto_guardado, null)
        }
        if (contactos == null) return view!!
        val contacto : Contacto = contactos.get(p0)

        val nombre: TextView = view!!.findViewById(R.id.nombre_contacto_guardado)
        val tlf: TextView = view.findViewById(R.id.tlf_contacto_guardado)

        nombre.setText("Nombre: ${contacto.nombre}")
        tlf.setText("Telefono: ${contacto.prefix}${contacto.tlf}")

        return view

    }

}