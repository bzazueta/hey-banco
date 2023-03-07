package com.sycnos.heyvisitas

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sycnos.heyvisitas.databinding.ActivitySeeFileBinding
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.VariablesGlobales


class SeeFileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeeFileBinding
    private var id     : String = ""
    private var urlArchivo     : String = ""
    private lateinit var progresoFile : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeeFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        urlArchivo =if (intent.getStringExtra("url_archivo") == null) "" else intent.getStringExtra("url_archivo")!!
        id =if (intent.getStringExtra("id") == null) "" else intent.getStringExtra("id")!!

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

    }



}