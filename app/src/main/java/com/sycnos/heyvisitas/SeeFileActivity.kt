package com.sycnos.heyvisitas

import android.R.string
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.sycnos.heyvisitas.databinding.ActivitySeeFileBinding
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.VariablesGlobales
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SeeFileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeeFileBinding
    private var id     : String = ""
    var nameArchivo : String = ""
    private var urlArchivo     : String = ""
    private lateinit var progresoFile : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeeFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        urlArchivo =if (intent.getStringExtra("url_archivo") == null) "" else intent.getStringExtra("url_archivo")!!
        id =if (intent.getStringExtra("id") == null) "" else intent.getStringExtra("id")!!

        val stringArray: List<String> = urlArchivo.split("/")
         nameArchivo = stringArray[1]
        checkStoragePermission()

        progresoFile = ProgressDialog(this@SeeFileActivity)
        progresoFile.setMessage("Cargando archivo...")
        progresoFile.setIndeterminate(false)
        progresoFile.setCancelable(false)
        progresoFile.show()

        binding.btnBack.setOnClickListener { finish() }
        binding.webview.getSettings().setJavaScriptEnabled(true);
        var archivo = this.getString(R.string.urlArchivo)+urlArchivo

        if(VariablesGlobales.getUrlArchivo().toString().contains(".pdf"))
        {
            try {
                binding.webview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + archivo)
                binding.webview.visibility = View.VISIBLE
                binding.webview.setWebViewClient(object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        // do your stuff here
                        progresoFile.dismiss()
                    }
                })
            }catch (e: java.lang.Exception)
            {
                progresoFile.dismiss()
            }
        }
        else {
            Glide.with(this)
                .load(archivo)
                //.placeholder(R.drawable.residencialcasa)
                .into(binding.imageView)
            progresoFile.dismiss()
        }

        binding.btnDownload.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {

                // do your Coroutine Stuff here, i.e. call a suspend fun:
                //val url ="https://sycnos.com/heybanco_qa/public/storage/fmensajes/yOYWhlub9AqCsCFiD4c0bSfWBmuOVfNSKG8SIuDz.png"
                //val downloadedFile = downloadFile(this@SpinnerActivity,url,"image.png")
                val downloadedFile = downloadImage(this@SeeFileActivity,archivo,nameArchivo)

                if (downloadedFile != null) {
                    //Toast.makeText(this@SpinnerActivity, "File downloaded to: ${downloadedFile.absolutePath}", Toast.LENGTH_LONG).show()
                    runOnUiThread {
                        toast()
                    }

                } else {
                    runOnUiThread {
                        toastFailed()
                    }
                }

            }
        }

    }

    fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this@SeeFileActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@SeeFileActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

        }
    }

    fun downloadImage(context: Context, url: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Downloading image...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager

        downloadManager.enqueue(request)
        runOnUiThread {
            Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()
        }
    }

    fun toast(){
        //Toast.makeText(this@SpinnerActivity, "File downloaded to: ${downloadedFile.absolutePath}", Toast.LENGTH_LONG).show()
        Toast.makeText(this@SeeFileActivity, "File downloaded ", Toast.LENGTH_LONG).show()

    }

    fun toastFailed(){
        Toast.makeText(this@SeeFileActivity, "Download failed", Toast.LENGTH_LONG).show()

    }

}