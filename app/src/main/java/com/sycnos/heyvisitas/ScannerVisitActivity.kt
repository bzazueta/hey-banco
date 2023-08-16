package com.sycnos.heyvisitas

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.databinding.ActivityScannerVisitBinding
import com.sycnos.heyvisitas.util.Conexion
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.VariablesGlobales
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ScannerVisitActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityScannerVisitBinding
    var id : String = ""
    var nombre : String = ""
    var placas : String = ""
    var departamento : String = ""
    private var mensajes: Mensajes? = Mensajes()
    private lateinit var progresoScannerVisit : ProgressDialog
    private var conexion: Conexion? = Conexion()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScannerVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id =if (intent.getStringExtra("id_visita") == null) "" else intent.getStringExtra("id_visita")!!
        nombre =if (intent.getStringExtra("nombre") == null) "" else intent.getStringExtra("nombre")!!
        placas =if (intent.getStringExtra("placas") == null) "" else intent.getStringExtra("placas")!!
        departamento =if (intent.getStringExtra("departamento") == null) "" else intent.getStringExtra("departamento")!!

        binding.etName.setText(nombre)
        binding.etDepartament.setText(departamento)
        binding.etPlacas.setText(placas)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnEnter.setOnClickListener(View.OnClickListener {

            try
            {
                var validado: Boolean = true
                binding.btnEnter.isEnabled = false
                progresoScannerVisit = ProgressDialog(this@ScannerVisitActivity)
                progresoScannerVisit.setMessage("Registrando Entrada...")
                progresoScannerVisit.setIndeterminate(false)
                progresoScannerVisit.setCancelable(false)
                progresoScannerVisit.show()

                var conectado : Boolean = false
                conectado = conexion!!.isOnline(this)
                if(conectado)
                {
                    val params = RequestParams()
                    params.put("id_visitas", id)
                    params.put("email", VariablesGlobales.getUser())
                    params.put("password", VariablesGlobales.getPasw())
                    createEnter(params)
                }
                else{
                    binding.btnEnter.isEnabled = true
                    progresoScannerVisit.dismiss()
                    mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@ScannerVisitActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

                }


            }catch (e : java.lang.Exception)
            {
                progresoScannerVisit.dismiss()
                binding.btnEnter.isEnabled = true
            }

        })

        binding.btnExit.setOnClickListener(View.OnClickListener {

            try
            {
                var validado: Boolean = true
                binding.btnEnter.isEnabled = false
                progresoScannerVisit = ProgressDialog(this@ScannerVisitActivity)
                progresoScannerVisit.setMessage("Registrando Salida...")
                progresoScannerVisit.setIndeterminate(false)
                progresoScannerVisit.setCancelable(false)
                progresoScannerVisit.show()

                var conectado : Boolean = false
                conectado = conexion!!.isOnline(this)
                if(conectado)
                {
                    val params = RequestParams()
                    params.put("id_visitas", id)
                    params.put("email", VariablesGlobales.getUser())
                    params.put("password", VariablesGlobales.getPasw())
                    createExit(params)
                }
                else{
                    binding.btnEnter.isEnabled = true
                    progresoScannerVisit.dismiss()
                    mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@ScannerVisitActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

                }


            }catch (e : java.lang.Exception)
            {
                progresoScannerVisit.dismiss()
                binding.btnEnter.isEnabled = true
            }

        })

    }

    override fun onResume() {
        super.onResume()
        val sharedPrefRoles: SharedPreferences =
            this@ScannerVisitActivity.getSharedPreferences(
                "usuario", MODE_PRIVATE
            )
        val sharedPrefTemp: SharedPreferences =
            this@ScannerVisitActivity.getSharedPreferences(
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

    fun createEnter(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(R.string.urlDominio)+"/public/api/entradas", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoScannerVisit.dismiss()
                binding.btnEnter.isEnabled = true
                //var x = responseString

                mensajes!!.mensajeAceptar("Mensaje",
                    responseString,
                    this@ScannerVisitActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                // progresoCobro.dismiss()
                var jsonObject: JSONObject? = null
                try
                {
                    progresoScannerVisit.dismiss()
                    binding.btnEnter.isEnabled = true
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("message") == "Entrada Registrada.")
                    {
                        mensajes!!.mensajeAceptarCerrar("Mensaje",
                            jsonObject.getString("message"),
                            this@ScannerVisitActivity)
                    }
                    else
                    {
                        mensajes!!.mensajeAceptar("Mensaje",
                            jsonObject.getString("message"),
                            this@ScannerVisitActivity)
                    }
                } catch (e: JSONException) {
                    progresoScannerVisit.dismiss()
                    binding.btnEnter.isEnabled = true
                    e.printStackTrace()
                }
            }
        })
    }

    fun createExit(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(R.string.urlDominio)+"/public/api/salidas", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoScannerVisit.dismiss()
                binding.btnEnter.isEnabled = true
                //var x = responseString

                mensajes!!.mensajeAceptar("Mensaje",
                    responseString,
                    this@ScannerVisitActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                // progresoCobro.dismiss()
                var jsonObject: JSONObject? = null
                try
                {
                    progresoScannerVisit.dismiss()
                    binding.btnEnter.isEnabled = true
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("message") == "Salida correctamente.")
                    {
                        mensajes!!.mensajeAceptarCerrar("Mensaje",
                            jsonObject.getString("message"),
                            this@ScannerVisitActivity)
                    }
                    else
                    {
                        mensajes!!.mensajeAceptar("Mensaje",
                            jsonObject.getString("message"),
                            this@ScannerVisitActivity)
                    }
                } catch (e: JSONException) {
                    progresoScannerVisit.dismiss()
                    binding.btnEnter.isEnabled = true
                    e.printStackTrace()
                }
            }
        })
    }

}