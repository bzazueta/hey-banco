package com.sycnos.heyvisitas

import android.app.ProgressDialog
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.adapters.NoticesAdapter
import com.sycnos.heyvisitas.data.models.Notices
import com.sycnos.heyvisitas.databinding.ActivitySeeFileBinding
import com.sycnos.heyvisitas.util.Mensajes
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SeeFileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeeFileBinding
    private var id     : String = ""
    private lateinit var progresoFile : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeeFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id =if (intent.getStringExtra("id") == null) "" else intent.getStringExtra("id")!!

        progresoFile = ProgressDialog(this@SeeFileActivity)
        progresoFile.setMessage("Cargando noticias...")
        progresoFile.setIndeterminate(false)
        progresoFile.setCancelable(false)
        progresoFile.show()
        val params = RequestParams()
        getFile(params)

        binding.btnBack.setOnClickListener { finish() }

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
                    this@SeeFileActivity)

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

                    if (jsonObject.getString("message").equals("Datos Incorrectos.")) {
                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("message"),this@SeeFileActivity);
                    }
                } catch (e: JSONException) {

                    progresoFile.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }

}