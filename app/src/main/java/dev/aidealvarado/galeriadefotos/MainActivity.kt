package dev.aidealvarado.galeriadefotos

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import dev.aidealvarado.galeriadefotos.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    val TAG = "Main Actity"
    var fileUri: Uri? = null
    private lateinit var gallery: Button
    private lateinit var takePhoto: Button
    private lateinit var imageView: ImageView
    private var heroBitmap: Bitmap? = null
    private var pictureFullPath = ""   // Path donde guarderomos la foto

    private val getContent =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && pictureFullPath.isNotEmpty()) {
                heroBitmap = BitmapFactory.decodeFile(pictureFullPath)
                if (heroBitmap != null) {
                    imageView.setImageBitmap(heroBitmap)
                }

            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // autorizaciones que el code analisis nos dice que hay que atualizar
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        // inicializamos los elementos del view.
        gallery = binding.gallery
        takePhoto = binding.takePhoto
        imageView = binding.imageView

        gallery.setOnClickListener {
            pickPhotoFromGallery()
        }

        //lanzamos la foto
        takePhoto.setOnClickListener {
            openCamera()
            saveMediaToStorage(heroBitmap!!)
        }

    }



    private fun pickPhotoFromGallery() {
        val picImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(picImageIntent, AppConstants.PICK_PHOTO_REQUEST)
    }

    private fun openCamera() {
        val imageFile = createImageFile()
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", imageFile)
        getContent.launch(uri)
    }

    private fun createImageFile(): File {
        // genearamos el nombre de archivo para guardar la foto con el timestamp de la foto tomada.
        val imageFile = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile(imageFile, ".jpg", storageDir)
        pictureFullPath = file.absolutePath
        Log.d(TAG,"Picture $pictureFullPath")
        return file
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode== Activity.RESULT_OK && requestCode == AppConstants.PICK_PHOTO_REQUEST){
            fileUri = data?.data

            heroBitmap = BitmapFactory.decodeFile(pictureFullPath)
            if (heroBitmap != null) {
                imageView.setImageBitmap(heroBitmap)
            }
        }
        else {
        super.onActivityResult(requestCode, resultCode, data)}
    }

    // Funcion para grabar una imagen (bitmap) en el storage.
    //URL: https://www.geeksforgeeks.org/how-to-capture-screenshot-of-a-view-and-save-it-to-gallery-in-android/
    private fun saveMediaToStorage(bitmap: Bitmap) {
        // crear un nombre de archivo
        val filename = "${System.currentTimeMillis()}.jpg"

        // Stream de salida
        var fos: OutputStream? = null

        if( Build.VERSION.SDK_INT >Build.VERSION_CODES.Q) {
            // obtener el contentResolver
            this.contentResolver?.also {
                resolver -> val contentValues = ContentValues().apply {
                    // poner la informacion del fichero en los content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES)
            }
            //Insertar los contentValues en contentResolver y obtener la URI
            val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                // creamos un outputStream con el URI que hemos obtenido
                fos = imageUri?.let{
                    resolver.openOutputStream(it)
                }
            }
        } else {
            // Para dispositivos con Android menor que Q
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            // escribimos el bitmap al output stream que hemos abierto
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,it)
            Toast.makeText(this,"Foto guardada en la galería",Toast.LENGTH_LONG).show()
        }
    }

}