package com.sycnos.heyvisitas

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.databinding.ActivityScannerProviderBinding
import com.sycnos.heyvisitas.util.Mensajes
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject

class ScannerProviderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannerProviderBinding
    var id_visita : String =""
    private lateinit var progresoScannerProvider : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScannerProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id_visita = if (intent.getStringExtra("id_visita") == null) "" else intent.getStringExtra("id_visita")!!

        progresoScannerProvider = ProgressDialog(this@ScannerProviderActivity)
        progresoScannerProvider.setMessage("Registrando visita...")
        progresoScannerProvider.setIndeterminate(false)
        progresoScannerProvider.setCancelable(false)
        progresoScannerProvider.show()

        val params = RequestParams()
        params.put("id_visita", id_visita)
        getDetalleProviders(params)

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

    fun getDetalleProviders(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/crear/visitas", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoScannerProvider.dismiss()

                var x = responseString

                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@ScannerProviderActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                progresoScannerProvider.dismiss()

                var jsonObject: JSONObject? = null
                try {
                    progresoScannerProvider.dismiss()
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("status") == "true")
                    {
                        //**guardar json en shared preferences***///

                    }
                    if (jsonObject.getString("status") == "false") {
                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("text"),this@ScannerProviderActivity);
                    }
                } catch (e: JSONException) {
                    progresoScannerProvider.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }
}