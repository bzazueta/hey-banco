package com.sycnos.heyvisitas

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.databinding.ActivityHomeBinding
import com.sycnos.heyvisitas.util.Mensajes
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var progresoValidaVisita : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvVisit.setOnClickListener{

//            val i = Intent(this@HomeActivity, VisitsActivity::class.java)
//            startActivity(i)
            try
            {
                var validado : Boolean = true
                binding.tvVisit.isEnabled = false
                progresoValidaVisita = ProgressDialog(this@HomeActivity)
                progresoValidaVisita.setMessage("Validando usuario...")
                progresoValidaVisita.setIndeterminate(false)
                progresoValidaVisita.setCancelable(false)
                progresoValidaVisita.show()
                val sharedPref: SharedPreferences =
                    this@HomeActivity.getSharedPreferences("user", MODE_PRIVATE
                    )
                //****obtener json guardado en shared preferences*****///
                val stringJson = sharedPref.getString("user", "")
                val user = JSONObject(stringJson)
                user.length()

                val pasw = sharedPref.getString("password", "")
                // val pasw = JSONObject(stringPass)
                pasw.toString()
                val params = RequestParams()
                params.put("email", user.getJSONObject("user").getString("email"))
                params.put("password",pasw)
                validarUsuario(params)
            }catch (e : Exception)
            {
                  e.toString()
            }

        }
        binding.tvPendingVisit.setOnClickListener{
            val i = Intent(this@HomeActivity, PendingVisitsActivity::class.java)
            startActivity(i)
        }

        binding.tvProviders.setOnClickListener {
            val i = Intent(this@HomeActivity, ProvidersActivity::class.java)
            startActivity(i)
        }
        binding.tvMessage.setOnClickListener {
            val i = Intent(this@HomeActivity, NoticesmessagesActvity::class.java)
            startActivity(i)
        }
    }

    fun validarUsuario(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/visitas", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoValidaVisita.dismiss()
                binding.tvVisit.isEnabled = true
                var x = responseString

                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@HomeActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                progresoValidaVisita.dismiss()
                binding.tvVisit.isEnabled = true
                var jsonObject: JSONObject? = null
                try {
                    progresoValidaVisita.dismiss()
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("message").equals("Datos correctos."))
                    {
                        val i = Intent(this@HomeActivity, VisitsActivity::class.java)
                        startActivity(i)
                    }
                    if (jsonObject.getString("message").equals("Datos Incorrectos.")) {
                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("message"),this@HomeActivity);
                    }
                } catch (e: JSONException) {
                    binding.tvVisit.isEnabled = true
                    progresoValidaVisita.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }

}