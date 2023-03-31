package com.sycnos.heyvisitas

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.databinding.ActivityContentNoticeBinding
import com.sycnos.heyvisitas.util.Conexion
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.VariablesGlobales
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ContentNoticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContentNoticeBinding
    private var titulo : String = ""
    private var cuerpo : String = ""
    private var id     : String = ""
    private lateinit var progresoFile : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    private var conexion: Conexion? = Conexion()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContentNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        titulo =if (intent.getStringExtra("titulo") == null) "" else intent.getStringExtra("titulo")!!
        cuerpo =if (intent.getStringExtra("cuerpo") == null) "" else intent.getStringExtra("cuerpo")!!
        id =if (intent.getStringExtra("id") == null) "" else intent.getStringExtra("id")!!

        binding.tvMessage.setText(titulo)
        binding.tvMessage2.setText(cuerpo)

        progresoFile = ProgressDialog(this@ContentNoticeActivity)
        progresoFile.setMessage("Cargando información...")
        progresoFile.setIndeterminate(false)
        progresoFile.setCancelable(false)
        progresoFile.show()
        var conectado : Boolean = false
        conectado = conexion!!.isOnline(this)
        if(conectado)
        {
            val params = RequestParams()
            getFile(params)
        }
        else
        {
            progresoFile.dismiss()
            mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@ContentNoticeActivity);
        }

        binding.btnFile.setOnClickListener {

            var validado :Boolean = true
            if(VariablesGlobales.getUrlArchivo().equals("") || VariablesGlobales.getUrlArchivo().equals("null"))
            {
                validado = false
                mensajes!!.mensajeAceptar(
                    "Mensaje",
                      "Este aviso no contiene archivo",
                    this@ContentNoticeActivity)
            }
            if(validado)
            {
                if(VariablesGlobales.getUrlArchivo().toString().contains(".pdf"))
                {
                    val i = Intent(this@ContentNoticeActivity, SeeFileActivity::class.java)
                    i.putExtra("id",id)
                    i.putExtra("url_archivo",VariablesGlobales.getUrlArchivo())
                    startActivity(i)
                }
                else
                {
                    val i = Intent(this@ContentNoticeActivity, SeeFileActivity::class.java)
                    i.putExtra("id",id)
                    i.putExtra("url_archivo",VariablesGlobales.getUrlArchivo())
                    startActivity(i)
                }

            }

        }
        binding.btnBack.setOnClickListener {
            VariablesGlobales.setUrlArchivo("")
            finish()
        }

    }


    fun getFile(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.get(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/avisos/detalle/${id}", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoFile.dismiss()
                var x = responseString

                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@ContentNoticeActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                progresoFile.dismiss()

                var jsonObject: JSONObject? = null
                var jsonArray: JSONArray? = null
                try
                {
                    progresoFile.dismiss()
                    jsonObject = JSONObject(responseString)

                    VariablesGlobales.setUrlArchivo(jsonObject.getString("archivo"))

                } catch (e: JSONException) {

                    progresoFile.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }
}