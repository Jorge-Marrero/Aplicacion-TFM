package es.tfm.clientetfm.util.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import es.tfm.clientetfm.R
import es.tfm.clientetfm.model.Documento

class DocumentosAdapter(private val ctx: Context, private val documentos: List<Documento>?) : BaseAdapter() {
    override fun getCount(): Int {
        if (documentos == null) return 0
        return documentos.size
    }

    override fun getItem(p0: Int): Any? {
        if (documentos == null) return null
        return documentos.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {
        var view = p1
        if (p1 == null){
            val inflater: LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.documento_lista, null)
        }
        if (documentos == null) return view!!
        val documento : Documento = documentos.get(p0)

        val nombre: TextView = view!!.findViewById(R.id.nombre_comp_doc)

        nombre.text = ("${documento.nombre}${documento.extension}")

        val imagen : ImageView = view!!.findViewById(R.id.ext_doc_lista)
        imagen.scaleType = ImageView.ScaleType.FIT_CENTER

        when(documento.extension){
            ".txt" -> imagen.setImageResource(R.drawable.txt)
            ".pdf" -> imagen.setImageResource(R.drawable.pdf)
            ".pptx" -> imagen.setImageResource(R.drawable.pptx)
            ".zip" -> imagen.setImageResource(R.drawable.zip)
            ".docx" -> imagen.setImageResource(R.drawable.docx)
            else -> imagen.setImageResource(R.drawable.generic)

        }
        return view
    }
}