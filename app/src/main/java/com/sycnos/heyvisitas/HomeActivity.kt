package com.sycnos.heyvisitas

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.data.models.Deparments
import com.sycnos.heyvisitas.databinding.ActivityHomeBinding
import com.sycnos.heyvisitas.util.Conexion
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.SharedPref
import com.sycnos.heyvisitas.util.VariablesGlobales
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var progresoValidaVisita : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    var sharedPref : SharedPref = SharedPref()
    private var conexion: Conexion? = Conexion()
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

                //var user  = sharedPref.getUsuario(this@HomeActivity)
                //var pasw  = sharedPref.getPass(this@HomeActivity)
                var conectado : Boolean = false
                conectado = conexion!!.isOnline(this)
                if(conectado)
                {
                    val params = RequestParams()
                    params.put("email", VariablesGlobales.getUser())
                    params.put("password",VariablesGlobales.getPasw())
                    validarUsuario(params)
                }
                else
                {
                    binding.tvVisit.isEnabled = true
                    progresoValidaVisita.dismiss()
                    mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@HomeActivity);
                }

            }catch (e : Exception)
            {
                progresoValidaVisita.dismiss()
                binding.tvVisit.isEnabled = true
                  e.toString()
            }

        }
        binding.tvPendingVisit.setOnClickListener{
            val i = Intent(this@HomeActivity, PendingVisitsActivity::class.java)
            startActivity(i)
        }

        binding.tvProviders.setOnClickListener {

            try
            {
                var validado : Boolean = true
                binding.tvVisit.isEnabled = false
                progresoValidaVisita = ProgressDialog(this@HomeActivity)
                progresoValidaVisita.setMessage("Validando proveedor...")
                progresoValidaVisita.setIndeterminate(false)
                progresoValidaVisita.setCancelable(false)
                progresoValidaVisita.show()

                var conectado : Boolean = false
                conectado = conexion!!.isOnline(this)
                if(conectado)
                {
                    val params = RequestParams()
                    params.put("email", VariablesGlobales.getUser())
                    params.put("password",VariablesGlobales.getPasw())
                    validarProveedor(params)
                }
                else
                {
                    binding.tvVisit.isEnabled = true
                    progresoValidaVisita.dismiss()
                    mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@HomeActivity);
                }
            }catch (e : Exception)
            {
                progresoValidaVisita.dismiss()
                binding.tvVisit.isEnabled = true
                e.toString()
            }
        }

        binding.tvMessage.setOnClickListener {
            val i = Intent(this@HomeActivity, NoticesmessagesActvity::class.java)
            startActivity(i)
        }
    }

    override fun onBackPressed() {
        mensajeCerrarSesion("Mensaje","¿Desea Cerrar Sesión?",this@HomeActivity)

    }

    override fun onResume() {
        super.onResume()
        val sharedPrefRoles: SharedPreferences =
            this@HomeActivity.getSharedPreferences(
                "usuario", MODE_PRIVATE
            )
        val sharedPrefTemp: SharedPreferences =
            this@HomeActivity.getSharedPreferences(
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
                var jsonArray: JSONArray? = null
                try {
                    progresoValidaVisita.dismiss()
                    jsonObject = JSONObject(responseString)
                    VariablesGlobales.arrayListDeptos.clear()
                    if (jsonObject.getString("message").equals("Datos correctos."))
                    {

                        //jsonArray = JSONArray(jsonObject.getJSONArray("Departamentos"))
                        for (i in 0 until jsonObject.getJSONArray("Departamentos").length())
                        {
                            val deptos : Deparments = Deparments()
                            deptos.id = jsonObject.getJSONArray("Departamentos").getJSONObject(i).getString("Id")
                            deptos.descripcion = jsonObject.getJSONArray("Departamentos").getJSONObject(i).getString("Descripcion")
                            VariablesGlobales.arrayListDeptos.add(deptos)
                        }
                        VariablesGlobales.arrayListDeptos.size
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

    fun validarProveedor(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/proveedor", params, object : TextHttpResponseHandler() {
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
                var jsonArray: JSONArray? = null
                try {
                    progresoValidaVisita.dismiss()
                    jsonObject = JSONObject(responseString)
                    VariablesGlobales.arrayListDeptos.clear()
                    if (jsonObject.getString("message").equals("Datos correctos."))
                    {
                        for (i in 0 until jsonObject.getJSONArray("proveedor").length()) {
                            val deptos: Deparments = Deparments()
                            //deptos.id = jsonObject.getJSONObject("proveedor").getString("departamento_id")

                            deptos.id = jsonObject.getJSONArray("proveedor").getJSONObject(i)
                                .getString("Id")

                            deptos.descripcion =
                                jsonObject.getJSONArray("proveedor").getJSONObject(i).getString("Descripcion")
                            VariablesGlobales.arrayListDeptos.add(deptos)
                            VariablesGlobales.arrayListDeptos.size
                        }
                        val i = Intent(this@HomeActivity, ProvidersActivity::class.java)
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

    fun mensajeCerrarSesion(titulo: String ,mensaje : String,activity : Activity) {
        val builder = AlertDialog.Builder(activity)
        with(builder)
        {
            setTitle(titulo)
            val message = setMessage(mensaje)
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener {
                        dialog, id ->
                    val sharedPref: SharedPreferences = this@HomeActivity.getSharedPreferences(
                        "usuario", MODE_PRIVATE
                    )
                    val editor = sharedPref.edit()
                    editor.remove("usuario")
                    editor.remove("temp")
                    editor.commit()
                    val stringJson = sharedPref.getString("usuario", "")
                    stringJson.toString()
                    finish()
                })
                .setNegativeButton("Cancelar", DialogInterface.OnClickListener {
                        dialog, id ->

                })
            show()
        }
    }

}