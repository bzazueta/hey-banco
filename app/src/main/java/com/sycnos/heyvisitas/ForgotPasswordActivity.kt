package com.sycnos.heyvisitas

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.databinding.ActivityForgotPasswordBinding
import com.sycnos.heyvisitas.util.Conexion
import com.sycnos.heyvisitas.util.Mensajes
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var progresoForgotPassword: ProgressDialog
    private var mensajes: Mensajes? = Mensajes()
    private var conexion: Conexion? = Conexion()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener{
          finish()
        }

        binding.btnRecoveryPassword.setOnClickListener(View.OnClickListener {


            var validado :Boolean = true
            binding.btnRecoveryPassword.isEnabled = false
            progresoForgotPassword = ProgressDialog(this@ForgotPasswordActivity)
            progresoForgotPassword.setMessage("Enviando información...")
            progresoForgotPassword.setIndeterminate(false)
            progresoForgotPassword.setCancelable(false)
            progresoForgotPassword.show()
           // binding.etUser.setText("calixto.pinon@hey.inc")

            var conectado : Boolean = false
            conectado = conexion!!.isOnline(this)
            if(conectado)
            {
                if (binding.etUser.text.toString().equals("")) {
                    progresoForgotPassword.dismiss()
                    validado = false
                    binding.btnRecoveryPassword.isEnabled = true
                    basicAlert("Mensaje","Favor de ingresar el usuario")
//                    mensajes!!.mensajeAceptar(
//                        "Mensaje",
//                        "Favor de ingresar el usuario",
//                        this@ForgotPasswordActivity
//                    );                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()
                    // Toast.makeText(this@ForgotPasswordActivity,"Favor de ingresar el usuario", Toast.LENGTH_SHORT).show()
                }

                if (validado) {
                    try {
                        val params = RequestParams()
                        params.put("email", binding.etUser.text.toString())
                        forgotPassword(params)
                    } catch (e: java.lang.Exception) {
                        progresoForgotPassword.dismiss()
                        binding.btnRecoveryPassword.isEnabled = true
                        e.toString()
                    }
                }
            }
            else
            {
                progresoForgotPassword.dismiss()
                binding.btnRecoveryPassword.isEnabled = true
                mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@ForgotPasswordActivity);
            }
        })

    }

    fun basicAlert_(view: View) {
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

    fun basicAlert(titulo: String ,mensaje : String) {


        val builder: AlertDialog.Builder = AlertDialog.Builder(this@ForgotPasswordActivity)
        val inflater = this@ForgotPasswordActivity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.dialog_custom, null, false)

        builder.setView(customView)
        builder.setCancelable(false)
        val lblReglamento =  customView.findViewById<View>(R.id.lblReglamento) as TextView
        val lblAceptar = customView.findViewById<View>(R.id.lblAceptar) as TextView
        val lblTexto = customView.findViewById<View>(R.id.lblTexto) as TextView
        lblReglamento.text = titulo
        lblTexto.text= mensaje
        lblTexto.setMovementMethod(ScrollingMovementMethod())
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
        lblAceptar.setOnClickListener {

            alertDialog.dismiss()

        }
    }


    fun forgotPassword(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(R.string.urlDominio)+"/public/api/forgot-password", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoForgotPassword.dismiss()
                binding.btnRecoveryPassword.isEnabled = true
                //var x = responseString

                mensajes!!.mensajeAceptar("Mensaje",
                    responseString,
                    this@ForgotPasswordActivity)

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
                    progresoForgotPassword.dismiss()
                    binding.btnRecoveryPassword.isEnabled = true
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("status") == "true")
                    {
                        //mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("msg"),this@ForgotPasswordActivity);
                        basicAlert("Actividad",jsonObject.getString("msg"))
                    }
                    if (jsonObject.getString("status") == "false") {
                        binding.btnRecoveryPassword.isEnabled = true
                        basicAlert("Actividad",jsonObject.getString("msg"))
                        //mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("msg"),this@ForgotPasswordActivity);
                    }
                } catch (e: JSONException) {
                    binding.btnRecoveryPassword.isEnabled = true
                    progresoForgotPassword.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }



}