package com.sycnos.heyvisitas

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.databinding.ActivityScannerQractivityBinding
import com.sycnos.heyvisitas.util.Conexion
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.VariablesGlobales
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ScannerQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannerQractivityBinding
    private lateinit var progresoScannerVisit : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    var idVisita : String = ""
    private var conexion: Conexion? = Conexion()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScannerQractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnAdd.setOnClickListener {

            //val i = Intent(this@ScannerQRActivity, ScannerVisitActivity::class.java)
//            val i = Intent(this@ScannerQRActivity, ScannerProviderActivity::class.java)
//            i.putExtra("id_visita","id")
//            i.putExtra("nombre","name")
//            i.putExtra("empresa","empresa")
//            i.putExtra("responsable","id")
//            i.putExtra("ticket","ticket")
//            i.putExtra("tel_contacto","id")
//            i.putExtra("trabajo","trabajorealizar")
//            i.putExtra("placas","placas")
//            i.putExtra("departamento","departamento")
//            startActivity(i)
            initScanner()
        }

    }
    private fun initScanner(){
        IntentIntegrator(this).initiateScan()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        var resp = "provedor";
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else
            {
                idVisita = result.contents.toString()
                progresoScannerVisit = ProgressDialog(this@ScannerQRActivity)
                progresoScannerVisit.setMessage("Cargando información...")
                progresoScannerVisit.setIndeterminate(false)
                progresoScannerVisit.setCancelable(false)
                progresoScannerVisit.show()

                var conectado : Boolean = false
                conectado = conexion!!.isOnline(this)
                if(conectado)
                {
                    val params = RequestParams()
                    getDetailVisits(params)
                }
                else{
                    progresoScannerVisit.dismiss()
                    mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@ScannerQRActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

                }


//                when(resp){
//                    "provedor" -> {
//                        val i = Intent(this@ScannerQRActivity, ScannerProviderActivity::class.java)
//                        i.putExtra("id_visita",result.contents.toString())
//                        startActivity(i)}
//                    "visita" -> {
//                        val i = Intent(this@ScannerQRActivity, ScannerVisitActivity::class.java)
//                        i.putExtra("id_visita",result.contents.toString())
//                        startActivity(i)
//                    }else -> {
//                    val i = Intent(this@ScannerQRActivity, ScannerVisitActivity::class.java)
//                    i.putExtra("id_visita",result.contents.toString())
//                    startActivity(i)
//                }
//                }
               // Toast.makeText(this, "El valor escaneado es: " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun getDetailVisits(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.get(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/visitas/qr/${idVisita}", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoScannerVisit.dismiss()
                var x = responseString

                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@ScannerQRActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                progresoScannerVisit.dismiss()

                var jsonObject: JSONObject? = null
                var jsonArray: JSONArray? = null
                try
                {
                    progresoScannerVisit.dismiss()
                    jsonObject = JSONObject(responseString)

                    var tipoVisita = jsonObject.getString("tipo")
                    //tipoVisita= 2.toString()
                    when(tipoVisita)
                    {
                        "1" ->
                        {
                            finish()
                            val i = Intent(this@ScannerQRActivity, ScannerProviderActivity::class.java)
                            i.putExtra("id_visita",jsonObject.getString("id"))
                            i.putExtra("nombre",jsonObject.getString("name"))
                            i.putExtra("empresa",jsonObject.getString("empresa"))
                            i.putExtra("ticket",jsonObject.getString("ticket"))
                            i.putExtra("tel_contacto",jsonObject.getString("tel_contacto"))
                            i.putExtra("trabajo",jsonObject.getString("trabajo_realizar"))
                            i.putExtra("placas",jsonObject.getString("placas"))
                            i.putExtra("departamento",jsonObject.getJSONObject("departamento").getString("descripcion"))
                            i.putExtra("identificacion",jsonObject.getString("identificacion"))
                            i.putExtra("fecha_inicio",jsonObject.getString("fecha_inicio"))
                            i.putExtra("fecha_final",jsonObject.getString("fecha_final"))
                            i.putExtra("trabajo_realizar",jsonObject.getString("trabajo_realizar"))
                            i.putExtra("responsable",jsonObject.getString("responsable"))
                            startActivity(i)
                        }
                        "2" ->
                        {
                            finish()
                            //val i = Intent(this@ScannerQRActivity, ScannerProviderActivity::class.java)
                            val i = Intent(this@ScannerQRActivity, ScannerVisitActivity::class.java)
                            i.putExtra("id_visita",jsonObject.getString("id"))
                            i.putExtra("nombre",jsonObject.getString("name"))
                            i.putExtra("empresa",jsonObject.getString("empresa"))
                            i.putExtra("responsable",jsonObject.getString("id"))
                            i.putExtra("ticket",jsonObject.getString("ticket"))
                            i.putExtra("tel_contacto",jsonObject.getString("id"))
                            i.putExtra("trabajo",jsonObject.getString("trabajo_realizar"))
                            i.putExtra("placas",jsonObject.getString("placas"))
                            i.putExtra("departamento",jsonObject.getJSONObject("departamento").getString("descripcion"))
                            i.putExtra("identificacion",jsonObject.getString("identificacion"))
                            i.putExtra("fecha_inicio",jsonObject.getString("fecha_inicio"))
                            i.putExtra("fecha_final",jsonObject.getString("fecha_final"))
                            i.putExtra("trabajo_realizar",jsonObject.getString("trabajo_realizar"))

                            startActivity(i)
                        }else -> {
                            finish()
                            val i = Intent(this@ScannerQRActivity, ScannerVisitActivity::class.java)
                            i.putExtra("id_visita",jsonObject.getString("id"))
                            i.putExtra("nombre",jsonObject.getString("name"))
                            i.putExtra("empresa",jsonObject.getString("empresa"))
                            i.putExtra("responsable",jsonObject.getString("id"))
                            i.putExtra("ticket",jsonObject.getString("ticket"))
                            i.putExtra("tel_contacto",jsonObject.getString("id"))
                            i.putExtra("trabajo",jsonObject.getString("trabajo_realizar"))
                            i.putExtra("placas",jsonObject.getString("placas"))
                            i.putExtra("departamento",jsonObject.getJSONObject("departamento").getString("descripcion"))
                            i.putExtra("identificacion",jsonObject.getString("identificacion"))
                            i.putExtra("fecha_inicio",jsonObject.getString("fecha_inicio"))
                            i.putExtra("fecha_final",jsonObject.getString("fecha_final"))
                            i.putExtra("trabajo_realizar",jsonObject.getString("trabajo_realizar"))

                        startActivity(i)
                       }
                    }

                } catch (e: JSONException) {

                    progresoScannerVisit.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }

}