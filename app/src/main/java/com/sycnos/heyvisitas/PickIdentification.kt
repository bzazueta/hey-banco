package com.sycnos.heyvisitas

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ext.SdkExtensions.getExtensionVersion
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.databinding.ActivityPickIdentificationBinding
import com.sycnos.heyvisitas.util.FormatoFechas
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.VariablesGlobales
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class PickIdentification : AppCompatActivity() {

    private val ANDROID_R_REQUIRED_EXTENSION_VERSION = 2
    private val SELECT_PICTURE = 1
    private lateinit var binding: ActivityPickIdentificationBinding
    private val REQUEST_PERMISSION = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    private val IMAGE_CHOOSE = 1000;
    var name :String =""
    var placas :String =""
    var date :String =""
    var deparment :String =""
    var frecuently :String =""
    private lateinit var progresoCrearVisita : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    var formatoFechas : FormatoFechas = FormatoFechas()
    var identificacion: File? = null
    var selectedImageURI: Uri? = null
    var flujo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPickIdentificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        name =if (intent.getStringExtra("name") == null) "" else intent.getStringExtra("name")!!
        placas =if (intent.getStringExtra("placas") == null) "" else intent.getStringExtra("placas")!!
        deparment =if (intent.getStringExtra("deparment") == null) "" else intent.getStringExtra("deparment")!!
        date =if (intent.getStringExtra("date") == null) "" else intent.getStringExtra("date")!!
        frecuently= if (intent.getStringExtra("frecuently") == null) "" else intent.getStringExtra("frecuently")!!

        checkCameraPermission()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnPick.setOnClickListener {
            openCamera()
        }

        binding.btnGalery.setOnClickListener {
            //binding.btnGalery.isEnabled = false
            checkCameraPermission()
            openGallery()
        }

        binding.btnAdd.setOnClickListener(View.OnClickListener {

            finish()
//            try
//            {
//                progresoCrearVisita = ProgressDialog(this@PickIdentification)
//                progresoCrearVisita.setMessage("Registrando visita...")
//                progresoCrearVisita.setIndeterminate(false)
//                progresoCrearVisita.setCancelable(false)
//                progresoCrearVisita.show()
//
//                val sharedPref: SharedPref =
//                    this@PickIdentification.getSharedPreferences("user", MODE_PRIVATE
//                    )
//                //****obtener json guardado en shared preferences*****///
//                val stringJson = sharedPref.getString("user", "")
//                val json = JSONObject(stringJson)
//                json.length()
//
//                val pasw = sharedPref.getString("password", "")
//               // val pasw = JSONObject(stringPass)
//                pasw.toString()
//
//                val params = RequestParams()
//                params.put("email", json.getJSONObject("user").getString("email"))
//                params.put("password",pasw)
//                params.put("placas", placas)
//                params.put("fecha_registro", formatoFechas.formatoFechatoyyyymmdd(date))
//                params.put("departamento_id", deparment)
//                params.put("nombre", name)
//                params.put("frecuencia", frecuently)
//                params.put("identificacion", identificacion)
//                crearVisita(params)
//            } catch(e : FileNotFoundException) {
//
//                e.toString();
//            } catch(ex : Exception) {
//
//                ex.toString();
//            }
        })
    }


//    override fun onResume() {
//        super.onResume()
//        checkCameraPermission()
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
       // Toast.makeText(this,"entro openGallery",Toast.LENGTH_LONG).show()
       try {
           flujo = "camara"
           val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
           // Start the activity with camera_intent, and request pic id
           // Start the activity with camera_intent, and request pic id
           startActivityForResult(camera_intent, 123)
//           Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
//               intent.resolveActivity(packageManager)?.also {
//                   startActivityForResult(intent, 123)
//               }
//           }
       }catch(e : Exception)
           {
           e.toString()
       }
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
           // binding.btnGalery.isEnabled = true
//
//                        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
//                intent.type = "image/*"
//                intent.resolveActivity(packageManager)?.also {
//                    startActivityForResult(intent, REQUEST_PICK_IMAGE)
//                }
//            }

           // Toast.makeText(this,"error entro a exception " +e.toString(),Toast.LENGTH_LONG).show()
            e.toString()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.btnGalery.isEnabled = true
        if (resultCode == RESULT_OK) {

            if(flujo.equals("camara"))
            {
                if (requestCode == 123) {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.ivImage.setImageBitmap(bitmap)
                    //  val bitmap_: Bitmap = Utils.decodeBase64(bitmap)
                    try {
                        val storageDir =
                            getExternalFilesDir(Environment.DIRECTORY_DCIM+ "/HeyVisitas/Archivos/Imagenes/")
                        identificacion = File.createTempFile(
                            "imagenhey",  /* prefix */
                            ".jpeg",  /* suffix */
                            storageDir /* directory */
                        )
                        VariablesGlobales.setImagen(identificacion)
                        //Convert bitmap to byte array
                        val bos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
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
                }
            }
            if(flujo.equals("galeria")) {
                try {
                    selectedImageURI = data!!.data
                    var picturePath : String? = getPath(this@PickIdentification, selectedImageURI!!)
                    val uri = data?.getData()
                    assert(picturePath != null)
                    identificacion = File(picturePath)
                    VariablesGlobales.setImagen(identificacion)
                    binding.ivImage.setImageURI(uri)
                }catch (e : Exception)
                {
                    e.toString()
                }

            }
//            else if (requestCode == IMAGE_CHOOSE) {
//                try {
//                    selectedImageURI = data!!.data
//                    var picturePath : String? = getPath(this@PickIdentification, selectedImageURI!!)
//                    val uri = data?.getData()
//                    assert(picturePath != null)
//                    identificacion = File(picturePath)
//                    VariablesGlobales.setImagen(identificacion)
//                    binding.ivImage.setImageURI(uri)
//                }catch (e : Exception)
//                {
//                    e.toString()
//                }
//
//            }
        }
    }
    fun basicAlert(msj: String) {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("Atención")
            val message = setMessage(msj)

//            val message = setMessage("Mensaje personalizado se puede personalizar desde la plataforma")
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener {
                        dialog, id ->
                })
            show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun isPhotoPickerAvailable(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> true
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                getExtensionVersion(Build.VERSION_CODES.R) >= ANDROID_R_REQUIRED_EXTENSION_VERSION
            }
            else -> false
        }
    }
    fun crearVisita(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/crear/visitas", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoCrearVisita.dismiss()
                binding.btnAdd.isEnabled = true
                var x = responseString

                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@PickIdentification)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                progresoCrearVisita.dismiss()
                binding.btnAdd.isEnabled = true
                var jsonObject: JSONObject? = null
                try {
                    progresoCrearVisita.dismiss()
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("status") == "true")
                    {
                        //**guardar json en shared preferences***///

                    }
                    if (jsonObject.getString("status") == "false") {
                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("text"),this@PickIdentification);
                    }
                } catch (e: JSONException) {
                    binding.btnAdd.isEnabled = true
                    progresoCrearVisita.dismiss()
                    e.printStackTrace()
                }
            }
        })
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