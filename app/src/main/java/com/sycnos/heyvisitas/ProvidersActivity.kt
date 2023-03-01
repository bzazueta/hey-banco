package com.sycnos.heyvisitas

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.databinding.ActivityProvidersBinding
import com.sycnos.heyvisitas.util.FormatoFechas
import com.sycnos.heyvisitas.util.Mensajes
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject

class ProvidersActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityProvidersBinding
    private lateinit var progresoProviders: ProgressDialog
    var mensajes : Mensajes = Mensajes()
    var date : String = ""
    var formatoFechas : FormatoFechas = FormatoFechas()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProvidersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener { finish() }

        binding.tvDate.setOnClickListener{
            showDatePickerDialog()
        }

        binding.btnAdd.setOnClickListener(View.OnClickListener {

            var validado :Boolean = true
            binding.btnAdd.isEnabled = false
            progresoProviders = ProgressDialog(this@ProvidersActivity)
            progresoProviders.setMessage("Validando información...")
            progresoProviders.setIndeterminate(false)
            progresoProviders.setCancelable(false)
            progresoProviders.show()

            if(binding.etDate.text.toString().equals(""))
            {
                progresoProviders.dismiss()
                validado=false
                binding.btnAdd.isEnabled = true
                Toast.makeText(this@ProvidersActivity,"Favor de seleccionar una fecha...", Toast.LENGTH_SHORT).show()
            }

            if(validado)
            {
                if(binding.spDepartaments.selectedItem.toString().equals(""))
                {
                    progresoProviders.dismiss()
                    validado=false
                    binding.btnAdd.isEnabled = true
                    Toast.makeText(this@ProvidersActivity,"Seleccione un departamento...", Toast.LENGTH_SHORT).show()
                }
            }

            if(validado)
            {
                if(binding.etName.toString().equals(""))
                {
                    progresoProviders.dismiss()
                    validado=false
                    binding.btnAdd.isEnabled = true
                    Toast.makeText(this@ProvidersActivity,"Ingrese un nombre...", Toast.LENGTH_SHORT).show()
                }
            }

            if(validado)
            {
                if(binding.etBussines.toString().equals(""))
                {
                    progresoProviders.dismiss()
                    validado=false
                    binding.btnAdd.isEnabled = true
                    Toast.makeText(this@ProvidersActivity,"Ingrese una empresa...", Toast.LENGTH_SHORT).show()
                }
            }

            if(validado)
            {
                try
                {
                    val sharedPref: SharedPreferences =
                        this@ProvidersActivity.getSharedPreferences("user", MODE_PRIVATE
                        )
                    //****obtener json guardado en shared preferences*****///
                    val stringJson = sharedPref.getString("user", "")
                    val json = JSONObject(stringJson)
                    json.length()

                    val pasw = sharedPref.getString("password", "")
                    // val pasw = JSONObject(stringPass)
                    pasw.toString()
                    val params = RequestParams()
                    params.put("email", json.getJSONObject("user").getString("email"))
                    params.put("password", pasw)
                    params.put("departamento_id","1" )//binding.spDepartaments.selectedItem.toString()
                    params.put("fecha_registro", formatoFechas.formatoFechatoyyyymmdd(date))
                    params.put("nombre", binding.etName.text.toString())
                    params.put("empresa", binding.etBussines.text.toString())
                    params.put("identificacion", "")
                    params.put("responsable", binding.etResponsable.text.toString())
                    params.put("ticket", binding.etTicket.text.toString())
                    params.put("tel_contacto", binding.etTel.text.toString())
                    params.put("frecuencia", "0")
                    params.put("trabajo_realizar", binding.etWork.text.toString())
                    createProviders(params)
                }catch (e :java.lang.Exception)
                {
                    progresoProviders.dismiss()
                    binding.btnAdd.isEnabled = true
                    Toast.makeText(this@ProvidersActivity,e.toString(), Toast.LENGTH_SHORT).show()

                }
            }

        })
    }
    private fun showDatePickerDialog() {

        val newFragment = DatePickerDialogFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 because January is zero
            val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
            date = year.toString() + "-" + (month + 1) + "-" + day.toString()
            binding.tvDate.setText(selectedDate)
        })

        newFragment.show(supportFragmentManager, "datePicker")
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

    fun createProviders(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(R.string.urlDominio)+"/public/api/crear/proveedor", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoProviders.dismiss()
                binding.btnAdd.isEnabled = true
                //var x = responseString

                mensajes!!.mensajeAceptar("Mensaje",
                    responseString,
                    this@ProvidersActivity)

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
                    progresoProviders.dismiss()
                    binding.btnAdd.isEnabled = true
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("message") == "Datos correctos.")
                    {
                        var proveedor : String = jsonObject.getJSONObject("proveedor").getString("name")
                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("message")+ proveedor,this@ProvidersActivity);

                    }
                    if (jsonObject.getString("message") == "Email o Password Incorrectos.") {
                        progresoProviders.dismiss()
                        binding.btnAdd.isEnabled = true
                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("text"),this@ProvidersActivity);
                    }
                } catch (e: JSONException) {
                    binding.btnAdd.isEnabled = true
                    progresoProviders.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }
}