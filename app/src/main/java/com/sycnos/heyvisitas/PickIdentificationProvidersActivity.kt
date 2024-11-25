package com.sycnos.heyvisitas

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.ext.SdkExtensions
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.sycnos.heyvisitas.databinding.ActivityPickIdentificationBinding
import com.sycnos.heyvisitas.databinding.ActivityPickIdentificationProvidersBinding
import com.sycnos.heyvisitas.util.FormatoFechas
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.VariablesGlobales
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class PickIdentificationProvidersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPickIdentificationProvidersBinding
    private val REQUEST_PERMISSION = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val IMAGE_CHOOSE = 1000;
    private val REQUEST_PICK_IMAGE = 2
    var mensajes : Mensajes = Mensajes()
    var formatoFechas : FormatoFechas = FormatoFechas()
    var identificacion: File? = null
    var selectedImageURI: Uri? = null
    var flujo: String = ""
    private val SELECT_PICTURE = 1
    private val ANDROID_R_REQUIRED_EXTENSION_VERSION = 2
    private lateinit var filePath: Uri
    var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickIdentificationProvidersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkCameraPermission()



        binding.btnBack.setOnClickListener { finish() }

        binding.btnPick.setOnClickListener {
            openCamera()
        }
        binding.btnGalery.setOnClickListener {
            binding.btnGalery.isEnabled=false
            checkCameraPermission()
            openGallery()
        }

        binding.btnAdd.setOnClickListener(View.OnClickListener {

            finish()

        })
    }

//    override fun onResume() {
//        super.onResume()
//
//    }
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION)

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION)
        }
    }

    private fun openCamera() {

        try {
            flujo = "camara"
//            val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(camera_intent, 123)
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(this@PickIdentificationProvidersActivity.packageManager) != null) {
                // Crear el archivo donde se guardará la foto
                photoFile = createImageFile(this@PickIdentificationProvidersActivity)

                // Continuar solo si el archivo fue creado correctamente
                photoFile?.also {
                    filePath = getUriFromFile(this@PickIdentificationProvidersActivity, it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath)
                    startActivityForResult(takePictureIntent, SELECT_PICTURE)
                }
            }
        }catch(e : Exception)
        {
            e.toString()
        }
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
//            intent.resolveActivity(packageManager)?.also {
//                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
//            }
//        }
    }
    private fun openGallery() {

        try
        {

            flujo="galeria"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if(isPhotoPickerAvailable()==true)
                {
                    val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
                    startActivityForResult(intent, 1)
                }
            } else {

                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    SELECT_PICTURE
                )
            }


//            Toast.makeText(this,"entro openGallery",Toast.LENGTH_LONG).show()

//            val intent = Intent(Intent.ACTION_PICK)
//            intent.type = "image/*"
//            startActivityForResult(intent, IMAGE_CHOOSE)

//           val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
//            startActivityForResult(intent, 1)



        }catch (e :Exception)
        {
            binding.btnGalery.isEnabled = true
//
//                        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
//                intent.type = "image/*"
//                intent.resolveActivity(packageManager)?.also {
//                    startActivityForResult(intent, REQUEST_PICK_IMAGE)
//                }
//            }

            Toast.makeText(this,"error entro a exception " +e.toString(), Toast.LENGTH_LONG).show()
            e.toString()
        }
    }

    fun createImageFile(context: Context): File {
        val storageDir: File? = this@PickIdentificationProvidersActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!storageDir?.exists()!! == true) {
            storageDir.mkdirs()
        }
        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    fun getUriFromFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}",
            file
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if(flujo.equals("camara"))
            {
                if (requestCode == 1) {
//                    val bitmap = data?.extras?.get("data") as Bitmap
//                    binding.ivImage.setImageBitmap(bitmap)
                    //  val bitmap_: Bitmap = Utils.decodeBase64(bitmap)
                    try {
                        binding.ivImage.setImageURI(filePath)
                        identificacion = File(filePath.path )
                        VariablesGlobales.setImagen(photoFile)
//                        val storageDir =
//                            getExternalFilesDir(Environment.DIRECTORY_DCIM + "/HeyVisitas/Archivos/Imagenes/")
//                        identificacion = File.createTempFile(
//                            "imagenhey",  /* prefix */
//                            ".jpeg",  /* suffix */
//                            storageDir /* directory */
//                        )
//                        VariablesGlobales.setImagen(identificacion)
//                        //Convert bitmap to byte array
//                        val bos = ByteArrayOutputStream()
//                        bitmap.compress(
//                            Bitmap.CompressFormat.PNG,
//                            0,
//                            bos
//                        ) // YOU can also save it in JPEG
//                        val bitmapdata = bos.toByteArray()
//
//                        //write the bytes in file
//                        val fos = FileOutputStream(identificacion)
//                        fos.write(bitmapdata)
//                        fos.flush()
//                        fos.close()

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        Log.i(null, "Save file error!")

                    }
                }
            }
            if(flujo.equals("galeria"))
            {
                try {
                    selectedImageURI = data!!.data
                    var picturePath : String? = getPath(this@PickIdentificationProvidersActivity, selectedImageURI!!)
                    val uri = data?.getData()
                    assert(picturePath != null)
                    identificacion = File(picturePath)
                   // VariablesGlobales.setImagen(identificacion)
                    binding.ivImage.setImageURI(uri)
                    binding.btnGalery.isEnabled = true
                    val selectedImageUri = data.data
                    val selectedImageBitmap: Bitmap
                    try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                selectedImageUri
                            )

                            val calendar = Calendar.getInstance()
                            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val strdate = simpleDateFormat.format(calendar.time)
                            val fyh = strdate.toString()
                            val storageDir =
                                getExternalFilesDir(Environment.DIRECTORY_DCIM+ "/HeyVisitas/Archivos/Imagenes/")
                            identificacion = File.createTempFile(
                                "imagenhey"+fyh,  /* prefix */
                                ".jpeg",  /* suffix */
                                storageDir /* directory */
                            )
                            VariablesGlobales.setImagen(identificacion)
                            //Convert bitmap to byte array
                            val bos = ByteArrayOutputStream()
                            selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
                            val bitmapdata = bos.toByteArray()

                            //write the bytes in file
                            val fos = FileOutputStream(identificacion)
                            fos.write(bitmapdata)
                            fos.flush()
                            fos.close()

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            Log.i(null, "Save file error!")

                        }
                }catch (e : Exception)
                {
                    e.toString()
                    binding.btnGalery.isEnabled = true
                }

            }
        }
        else
        {
            binding.btnGalery.isEnabled=true
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun isPhotoPickerAvailable(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> true
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= ANDROID_R_REQUIRED_EXTENSION_VERSION
            }
            else -> false
        }
    }
    fun basicAlert(view: View) {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("Atención")
            val message = setMessage("Mensaje personalizado se puede personalizar desde la plataforma")
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener {
                        dialog, id ->
                })
            show()
        }
    }

    fun getPath(context: Context, uri: Uri): String? {

        // check here to KITKAT or new version
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return (Environment.getExternalStorageDirectory().toString() + "/"
                            + split[1])
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(
                    context, contentUri, selection,
                    selectionArgs
                )
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     * The context.
     * @param uri
     * The Uri to query.
     * @param selection
     * (Optional) Filter used in the query.
     * @param selectionArgs
     * (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?,
        selection: String?, selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection,
                selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri
            .authority
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri
            .authority
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri
            .authority
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri
            .authority
    }
}