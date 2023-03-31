package com.sycnos.heyvisitas

import android.R
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
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
import com.sycnos.heyvisitas.data.models.Deparments
import com.sycnos.heyvisitas.databinding.ActivityRegisterBinding
import com.sycnos.heyvisitas.util.Conexion
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.VariablesGlobales
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var progresoRegister : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    var arrayListDescripcion : ArrayList<String> = ArrayList()
    var arrayListIds : ArrayList<String> = ArrayList()
    private var conexion: Conexion? = Conexion()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progresoRegister = ProgressDialog(this@RegisterActivity)
        progresoRegister.setMessage("Cargando información...")
        progresoRegister.setIndeterminate(false)
        progresoRegister.setCancelable(false)
        progresoRegister.show()

        var conectado : Boolean = false
        conectado = conexion!!.isOnline(this)
        if(conectado)
        {
            val params = RequestParams()
            params.put("email", VariablesGlobales.getUser())
            params.put("password", VariablesGlobales.getPasw())
            getDepartments(params)
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnRegister.setOnClickListener(View.OnClickListener {

            var validado : Boolean = true
            binding.btnRegister.isEnabled = false
            progresoRegister = ProgressDialog(this@RegisterActivity)
            progresoRegister.setMessage("Registrando visita...")
            progresoRegister.setIndeterminate(false)
            progresoRegister.setCancelable(false)
            progresoRegister.show()

            var conectado : Boolean = false
            conectado = conexion!!.isOnline(this)
            if(conectado)
            {
                if(binding.tvName.text.toString().equals(""))
                {
                    binding.btnRegister.isEnabled = true
                    progresoRegister.dismiss()
                    validado = false
                    //Toast.makeText(this@VisitsActivity,"Seleccione una fecha", Toast.LENGTH_SHORT).show()
                    mensajes!!.mensajeAceptar("Mensaje","Ingrese un nombre",this@RegisterActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()
                }

                if(validado)
                {
                    val params = RequestParams()
                    params.put("email", VariablesGlobales.getUser())
                    params.put("password",VariablesGlobales.getPasw())
                    params.put("nombre",binding.etName.text.toString())
                    params.put("departamento_id",arrayListIds.get(binding.spDepartaments.selectedItemPosition))
                    params.put("asunto",binding.etAsunto.text.toString())
                    crearRegistro(params)
                }
            }
            else{
                binding.btnRegister.isEnabled = true
                progresoRegister.dismiss()
                mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@RegisterActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

            }
        })


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

    fun getDepartments(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/visitas/departamentos", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoRegister.dismiss()
                binding.tvVisit.isEnabled = true
                var x = responseString

                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@RegisterActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                progresoRegister.dismiss()
                binding.tvVisit.isEnabled = true
                var jsonObject: JSONObject? = null
                var jsonArray: JSONArray? = null
                try {
                    progresoRegister.dismiss()
                    jsonObject = JSONObject(responseString)
                    VariablesGlobales.arrayListDeptos.clear()
                    if (jsonObject.has("departamentos"))
                    {
                        //jsonArray = JSONArray(jsonObject.getJSONObject("departamentos"))
                        for(i in 0 until jsonObject.getJSONArray("departamentos").length())
                        {
                            val deptos : Deparments = Deparments()
                            deptos.id = jsonObject.getJSONArray("departamentos").getJSONObject(i).getString("id")
                            deptos.descripcion  = jsonObject.getJSONArray("departamentos").getJSONObject(i).getString("descripcion")
                            VariablesGlobales.arrayListDeptos.add(deptos)
                            VariablesGlobales.arrayListDeptos.size
                        }

                        arrayListIds.add("Seleccionar")
                        arrayListDescripcion.add("Seleccionar")
                        for (i in 0 until VariablesGlobales.arrayListDeptos.size)
                        {
                            var descripcion = VariablesGlobales.arrayListDeptos.get(i).descripcion
                            var id = VariablesGlobales.arrayListDeptos.get(i).id
                            arrayListIds.add(id)
                            arrayListDescripcion.add(descripcion)
                        }

                        val adapter = ArrayAdapter<String>(
                            this@RegisterActivity,
                            R.layout.simple_spinner_item,
                            arrayListDescripcion
                        )
                        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        binding.spDepartaments.setAdapter(adapter)

//                        val i = Intent(this@RegisterActivity, ProvidersActivity::class.java)
//                        startActivity(i)
                    }
                   else {
                        mensajes!!.mensajeAceptar("Mensaje",responseString,this@RegisterActivity);
                    }
                } catch (e: JSONException) {
                    binding.tvVisit.isEnabled = true
                    progresoRegister.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }

    fun crearRegistro(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/visitas/registra", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoRegister.dismiss()
                binding.btnRegister.isEnabled = true
                var x = responseString

                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@RegisterActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                progresoRegister.dismiss()
                binding.btnRegister.isEnabled = true
                var jsonObject: JSONObject? = null
                try {
                    progresoRegister.dismiss()
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("message") == "Notificación enviada.")
                    {
                        mensajes!!.mensajeAceptarCerrar("Mensaje",jsonObject.getString("message"),this@RegisterActivity);
                    }
                    else{
                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("message"),this@RegisterActivity);
                    }
                } catch (e: JSONException) {
                    progresoRegister.dismiss()
                    binding.btnRegister.isEnabled = true
                    e.printStackTrace()
                }
            }
        })
    }

}