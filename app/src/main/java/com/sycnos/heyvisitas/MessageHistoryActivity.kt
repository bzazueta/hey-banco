package com.sycnos.heyvisitas

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
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
import com.sycnos.heyvisitas.databinding.ActivityMessageHistoryBinding
import com.sycnos.heyvisitas.util.Conexion
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.VariablesGlobales
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MessageHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageHistoryBinding
     var id : String =  ""
    var titulo : String =  ""
    var cuerpo : String =  ""
    private lateinit var progresoFile : ProgressDialog
    private lateinit var progresoMessageHistory : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    private var conexion: Conexion? = Conexion()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessageHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = if (intent.getStringExtra("id") == null) "" else intent.getStringExtra("id")!!
        titulo = if (intent.getStringExtra("titulo") == null) "" else intent.getStringExtra("titulo")!!
        cuerpo = if (intent.getStringExtra("cuerpo") == null) "" else intent.getStringExtra("cuerpo")!!

        progresoFile = ProgressDialog(this@MessageHistoryActivity)
        progresoFile.setMessage("Cargando información...")
        progresoFile.setIndeterminate(false)
        progresoFile.setCancelable(false)
        progresoFile.show()

        var conectado : Boolean = false
        conectado = conexion!!.isOnline(this)
        if(conectado) {
            val params = RequestParams()
            getFilemessages(params)
        }
        else
        {
            progresoFile.dismiss()
            mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@MessageHistoryActivity);
        }

        binding.tvMessage.setText(titulo)
        binding.tvMessage2.setText(cuerpo)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {

            try
            {

                binding.btnSend.isEnabled = false
                var validado :Boolean = true
                progresoMessageHistory = ProgressDialog(this@MessageHistoryActivity)
                progresoMessageHistory.setMessage("Enviando mensaje...")
                progresoMessageHistory.setIndeterminate(false)
                progresoMessageHistory.setCancelable(false)
                progresoMessageHistory.show()

                var conectado : Boolean = false
                conectado = conexion!!.isOnline(this)
                if(conectado)
                {
                    if (binding.txtMessage.text.toString().isNullOrEmpty()) {
                        validado = false
                        binding.btnSend.isEnabled = true
                        progresoMessageHistory.dismiss()
                        mensajes!!.mensajeAceptar(
                            "Mensaje",
                            "Favor de ingresar el mensaje",
                            this@MessageHistoryActivity
                        );                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

                    }

                    if (validado) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd")
                        var fecha = sdf.format(Date())

                        val params = RequestParams()
                        params.put("mensaje", id)
                        params.put("usuario", VariablesGlobales.getIdUser())
                        params.put("fecha", fecha)
                        params.put("respuesta", binding.txtMessage.text.toString())
                        crearMessage(params)
                    }
                }
                else
                {
                    binding.btnSend.isEnabled = true
                    progresoMessageHistory.dismiss()
                    mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@MessageHistoryActivity);
                }


            }catch (e : Exception)
            {
                binding.btnSend.isEnabled = true
                progresoMessageHistory.dismiss()
            }
        }

        binding.btnFile.setOnClickListener {

            var validado :Boolean = true
            if(VariablesGlobales.getUrlArchivoMessage().equals("") || VariablesGlobales.getUrlArchivoMessage().equals("null"))
            {
                validado = false
                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    "Este mensaje no contiene archivo",
                    this@MessageHistoryActivity)
            }
            if(validado) {
                val i = Intent(this@MessageHistoryActivity, SeeFileActivity::class.java)
                i.putExtra("id", id)
                i.putExtra("url_archivo", VariablesGlobales.getUrlArchivoMessage())
                startActivity(i)
            }
        }

        binding.btnPick.setOnClickListener {

            var validado :Boolean = true
            if(VariablesGlobales.getUrlFotoMessage().equals("") || VariablesGlobales.getUrlFotoMessage().equals("null"))
            {
                validado = false
                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    "Este mensaje no contiene foto",
                    this@MessageHistoryActivity)
            }
            if(validado) {
                val i = Intent(this@MessageHistoryActivity, SeeFileActivity::class.java)
                i.putExtra("id", id)
                i.putExtra("url_archivo", VariablesGlobales.getUrlFotoMessage())
                startActivity(i)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        val sharedPrefRoles: SharedPreferences =
            this@MessageHistoryActivity.getSharedPreferences(
                "usuario", MODE_PRIVATE
            )
        val sharedPrefTemp: SharedPreferences =
            this@MessageHistoryActivity.getSharedPreferences(
                "temp", MODE_PRIVATE
            )
        var stringJsonTemp = sharedPrefTemp.getString("temp", "")
        val stringJsonUsuario = sharedPrefRoles.getString("usuario", "")
        if(!stringJsonUsuario.equals(""))
        {
            val jsonUsuario = JSONObject(stringJsonUsuario)
            /** seteamos las variables que vamos a ocupar en todas las pantallas*****///
            VariablesGlobales.setIdUser(jsonUsuario.getJSONObject("user").getString("id"))
            VariablesGlobales.setUser(jsonUsuario.getJSONObject("user").getString("email"))
            VariablesGlobales.setPasw(stringJsonTemp)
            /****fin variables*****/
        }

    }

    fun crearMessage(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/mensajes/agregar", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoMessageHistory.dismiss()
                var x = responseString
                binding.btnSend.isEnabled = true
                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@MessageHistoryActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                progresoMessageHistory.dismiss()
                binding.btnSend.isEnabled = true
                var jsonObject: JSONObject? = null
                var jsonArray: JSONArray? = null
                try
                {
                    progresoMessageHistory.dismiss()
                    jsonObject = JSONObject(responseString)
                    if(jsonObject.getString("error").equals("false"))
                    {
                        mensajes!!.mensajeAceptarCerrar("Mensaje",jsonObject.getString("msg"),this@MessageHistoryActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("msg"),this@MessageHistoryActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

                    }
                    VariablesGlobales.setUrlArchivoMessage(jsonObject.getString("archivo"))
                    VariablesGlobales.setUrlFotoMessage(jsonObject.getString("foto"))

                } catch (e: JSONException) {

                    binding.btnSend.isEnabled = true
                    progresoMessageHistory.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }
    fun getFilemessages(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.get(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/mensajes/detalle/${id}", params, object : TextHttpResponseHandler() {
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
                    this@MessageHistoryActivity)

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

                    VariablesGlobales.setUrlArchivoMessage(jsonObject.getString("archivo"))
                    VariablesGlobales.setUrlFotoMessage(jsonObject.getString("foto"))

                } catch (e: JSONException) {

                    progresoFile.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }

}