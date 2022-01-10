package dev.aidealvarado.galeriadefotos

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import dev.aidealvarado.galeriadefotos.databinding.ImageDetailBinding

private val TAG = GalleryAdapter::class.java.simpleName
class GalleryAdapter: Adapter<GalleryAdapter.ViewHolder>() {
    var imageList: MutableList<Imagenes> = ArrayList()
    lateinit var  context: Context

    fun GalleryAdapter(miImageList: MutableList<Imagenes>,ctx: Context){
        this.imageList = miImageList
        this.context = ctx
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val binding = ImageDetailBinding.bind(view)

        fun bind(image:Imagenes, context: Context){
            Log.d(TAG,"Cargando imagen $image")
            binding.imageSmall.setImageURI(image.fileName.toUri())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ImageDetailBinding.inflate(layoutInflater, parent,false).root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = imageList[position]
        holder.bind(item, context)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}