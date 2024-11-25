package com.sycnos.heyvisitas

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.SeeFilePendingVisitActivity
import com.sycnos.heyvisitas.databinding.ActivityScannerProviderBinding
import com.sycnos.heyvisitas.util.Conexion
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.VariablesGlobales
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject

class ScannerProviderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannerProviderBinding
    var id_visita : String =""
    private lateinit var progresoScannerProvider : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    var id : String = ""
    var nombre : String = ""
    var responsable : String = ""
    var departamento : String = ""
    var empresa : String = ""
    var ticket : String = ""
    var telContacto : String = ""
    var trabajo: String = ""
    var fecha_final : String = ""
    var identificacion : String = ""
    var placas : String = ""
    var imagen = ""
    private var conexion: Conexion? = Conexion()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScannerProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id =if (intent.getStringExtra("id_visita") == null) "" else intent.getStringExtra("id_visita")!!
        nombre =if (intent.getStringExtra("nombre") == null) "" else intent.getStringExtra("nombre")!!
        departamento =if (intent.getStringExtra("departamento") == null) "" else intent.getStringExtra("departamento")!!
        empresa =if (intent.getStringExtra("empresa") == null) "" else intent.getStringExtra("empresa")!!
        responsable =if (intent.getStringExtra("responsable") == null) "" else intent.getStringExtra("responsable")!!
        ticket =if (intent.getStringExtra("ticket") == null) "" else intent.getStringExtra("ticket")!!
        telContacto =if (intent.getStringExtra("tel_contacto") == null) "" else intent.getStringExtra("tel_contacto")!!
        trabajo =if (intent.getStringExtra("trabajo") == null) "" else intent.getStringExtra("trabajo")!!
        fecha_final =if (intent.getStringExtra("fecha_final") == null) "" else intent.getStringExtra("fecha_final")!!
        imagen =if (intent.getStringExtra("identificacion") == null) "" else intent.getStringExtra("identificacion")!!

        binding.txtNombre.setText(nombre)
        binding.txtDepto.setText(departamento)
        binding.txtEmpresa.setText(empresa)
        binding.txtResponsable.setText(responsable)
        binding.txtTicket.setText(ticket)
        binding.txtValido.setText(fecha_final)
        binding.txtAsunto.setText(trabajo)

        binding.btnEnter.setOnClickListener(View.OnClickListener {

            try
            {
                var validado: Boolean = true
                binding.btnEnter.isEnabled = false
                progresoScannerProvider = ProgressDialog(this@ScannerProviderActivity)
                progresoScannerProvider.setMessage("Registrando Entrada...")
                progresoScannerProvider.setIndeterminate(false)
                progresoScannerProvider.setCancelable(false)
                progresoScannerProvider.show()

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
                    progresoScannerProvider.dismiss()
                    mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@ScannerProviderActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

                }

            }catch (e : java.lang.Exception)
            {
                progresoScannerProvider.dismiss()
                binding.btnEnter.isEnabled = true
            }

        })

        binding.btnExit.setOnClickListener(View.OnClickListener {

            try
            {
                var validado: Boolean = true
                binding.btnEnter.isEnabled = false
                progresoScannerProvider = ProgressDialog(this@ScannerProviderActivity)
                progresoScannerProvider.setMessage("Registrando Salida...")
                progresoScannerProvider.setIndeterminate(false)
                progresoScannerProvider.setCancelable(false)
                progresoScannerProvider.show()

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
                    progresoScannerProvider.dismiss()
                    mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@ScannerProviderActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

                }


            }catch (e : java.lang.Exception)
            {
                progresoScannerProvider.dismiss()
                binding.btnEnter.isEnabled = true
            }

        })


        binding.btnBack.setOnClickListener(View.OnClickListener {

            finish()
        })

        binding.btnId.setOnClickListener {
            var validado :Boolean = true
            if(imagen.equals("") || imagen.equals("null"))
            {
                validado = false
                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    "Este mensaje no contiene identificación",
                    this@ScannerProviderActivity)
            }
            if(validado) {
                val i = Intent(this@ScannerProviderActivity, SeeFilePendingVisitActivity::class.java)
                i.putExtra("id", id)
                i.putExtra("url_archivo", imagen)
                startActivity(i)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPrefRoles: SharedPreferences =
            this@ScannerProviderActivity.getSharedPreferences(
                "usuario", MODE_PRIVATE
            )
        val sharedPrefTemp: SharedPreferences =
            this@ScannerProviderActivity.getSharedPreferences(
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
                progresoScannerProvider.dismiss()
                binding.btnEnter.isEnabled = true
                //var x = responseString

                mensajes!!.mensajeAceptar("Mensaje",
                    responseString,
                    this@ScannerProviderActivity)

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
                    progresoScannerProvider.dismiss()
                    binding.btnEnter.isEnabled = true
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("message") == "Entrada Registrada.")
                    {
                        mensajes!!.mensajeAceptarCerrar("Mensaje",
                            jsonObject.getString("message"),
                            this@ScannerProviderActivity)
                    }
                    else
                    {
                        mensajes!!.mensajeAceptarCerrar("Mensaje",
                            jsonObject.getString("message"),
                            this@ScannerProviderActivity)
                    }
                } catch (e: JSONException) {
                    progresoScannerProvider.dismiss()
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
                progresoScannerProvider.dismiss()
                binding.btnEnter.isEnabled = true
                //var x = responseString

                mensajes!!.mensajeAceptar("Mensaje",
                    responseString,
                    this@ScannerProviderActivity)

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
                    progresoScannerProvider.dismiss()
                    binding.btnEnter.isEnabled = true
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("message") == "Salida correctamente.")
                    {
                        mensajes!!.mensajeAceptarCerrar("Mensaje",
                            jsonObject.getString("message"),
                            this@ScannerProviderActivity)
                    }
                    else
                    {
                        mensajes!!.mensajeAceptarCerrar("Mensaje",
                            jsonObject.getString("message"),
                            this@ScannerProviderActivity)
                    }
                } catch (e: JSONException) {
                    progresoScannerProvider.dismiss()
                    binding.btnEnter.isEnabled = true
                    e.printStackTrace()
                }
            }
        })
    }

}