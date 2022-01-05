package dev.aidealvarado.galeriadefotos

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import dev.aidealvarado.galeriadefotos.databinding.ActivityGaleriaDeFotosBinding
import java.io.File

class GaleriaDeFotos : AppCompatActivity() {
    private lateinit var binding: ActivityGaleriaDeFotosBinding
    private val myAdapter: GalleryAdapter = GalleryAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGaleriaDeFotosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.myImage.setHasFixedSize(true)
        binding.myImage.layoutManager = GridLayoutManager(this, 2)
        myAdapter.GalleryAdapter(getImages(), this)
        binding.myImage.adapter = myAdapter
    }

    private fun getImages(): MutableList<Imagenes> {
        val imageList: MutableList<Imagenes> = arrayListOf()
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val miPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.toURI()
        for (file in File(miPath).walk()) {
            Log.d("Recycler", "$file  ${file.length()}")
            if (file.length() > 0 && file.isFile == true) {
                imageList.add(Imagenes(file.toString()))
            }
        }
        return imageList
    }
}