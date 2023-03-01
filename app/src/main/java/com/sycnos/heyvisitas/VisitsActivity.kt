package com.sycnos.heyvisitas

import android.R
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.databinding.ActivityVisitsBinding
import com.sycnos.heyvisitas.util.FormatoFechas
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.VariablesGlobales
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.util.*


class VisitsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisitsBinding
    private lateinit var progresoCrearVisita : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    var date : String = ""
    var formatoFechas : FormatoFechas = FormatoFechas()
    var identificacion: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVisitsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val adapter = ArrayAdapter<String>(
//            this,
//            R.layout.simple_spinner_item,
//            resources.getStringArray(R.array.array_departaments)
//        )
      //  adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
      //  binding.spDepartaments.setAdapter(adapter)

        binding.tvDate.setOnClickListener{
            showDatePickerDialog()
        }

        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnSelect.setOnClickListener {
            var frecuently : String = ""
            if (!binding.cbFrecuently.isChecked) {
                frecuently = "false"
            }
            if (binding.cbFrecuently.isChecked) {
                frecuently = "true"
            }
            val i = Intent(this@VisitsActivity, PickIdentification::class.java)
            i.putExtra("date",date)
            i.putExtra("deparment",binding.spDepartaments.selectedItem.toString())
            i.putExtra("name",binding.etName.text.toString())
            i.putExtra("placas",binding.etPlacas.text.toString())
            i.putExtra("frecuently",frecuently)
            startActivity(i)
        }


        binding.btnAdd.setOnClickListener(View.OnClickListener {

            var validado : Boolean = true
            binding.btnAdd.isEnabled = false
            progresoCrearVisita = ProgressDialog(this@VisitsActivity)
            progresoCrearVisita.setMessage("Registrando visita...")
            progresoCrearVisita.setIndeterminate(false)
            progresoCrearVisita.setCancelable(false)
            progresoCrearVisita.show()

            if(binding.tvDate.text.toString().equals(""))
            {
                binding.btnAdd.isEnabled = true
                progresoCrearVisita.dismiss()
                validado = false
                Toast.makeText(this@VisitsActivity,"Seleccione una fecha", Toast.LENGTH_SHORT).show()

            }
            if(validado) {
                if (binding.spDepartaments.selectedItem.toString().equals("Seleccionar")) {
                    binding.btnAdd.isEnabled = true
                    progresoCrearVisita.dismiss()
                    validado = false
                    Toast.makeText(
                        this@VisitsActivity,
                        "Seleccione un departamento",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            if(validado) {
                if (binding.etName.text.toString().equals("")) {
                    binding.btnAdd.isEnabled = true
                    progresoCrearVisita.dismiss()
                    validado = false
                    Toast.makeText(this@VisitsActivity, "Ingrese un nombre", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            if(validado==false) {
                if (!binding.etName.text.toString().equals("")) {
                    if (binding.etPlacas.text.toString().equals("")) {
                        binding.btnAdd.isEnabled = true
                        progresoCrearVisita.dismiss()
                        validado = false
                        Toast.makeText(
                            this@VisitsActivity,
                            "Ingrese las placas",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
            if(validado==false) {
                if (VariablesGlobales.getImagen().equals("null")) {
                    binding.btnAdd.isEnabled = true
                    progresoCrearVisita.dismiss()
                    validado = false
                    Toast.makeText(
                        this@VisitsActivity,
                        "Seleccione una identificación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            if(validado)
            {
                try
                {
                    var frecuently : String = ""
                    if (!binding.cbFrecuently.isChecked) {
                        frecuently = "0"
                    }
                    if (binding.cbFrecuently.isChecked) {
                        frecuently = "4"
                    }
                    val sharedPref: SharedPreferences =
                    this@VisitsActivity.getSharedPreferences("user", MODE_PRIVATE
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
                    params.put("password",pasw)
                    params.put("placas",if( binding.etPlacas.text.toString().equals(""))"" else binding.etPlacas.text.toString())
                    params.put("fecha_registro", formatoFechas.formatoFechatoyyyymmdd(date))
                    params.put("departamento_id", 1)
                    params.put("nombre", if( binding.etName.text.toString().equals(""))"" else binding.etName.text.toString())
                    params.put("frecuencia", frecuently)
                    if(VariablesGlobales.getImagen() != null)
                    {
                        params.put("identificacion", VariablesGlobales.getImagen())
                    }
                    if(VariablesGlobales.getImagen() == null)
                    {
                        params.put("identificacion", "")
                    }
                    crearVisita(params)
               } catch(e : FileNotFoundException) {
                   progresoCrearVisita.dismiss()
                  binding.btnAdd.isEnabled = true
                   e.toString();
               } catch(ex : Exception) {
                    progresoCrearVisita.dismiss()
                    binding.btnAdd.isEnabled = true
                 ex.toString();
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

    fun crearVisita(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/crear/visitas", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoCrearVisita.dismiss()
                binding.btnAdd.isEnabled = true
                var x = responseString

                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@VisitsActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                progresoCrearVisita.dismiss()
                binding.btnAdd.isEnabled = true
                var jsonObject: JSONObject? = null
                try {
                    progresoCrearVisita.dismiss()
                    jsonObject = JSONObject(responseString)
                   // if (jsonObject.getString("status") == "true")
                    //{
                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("message"),this@VisitsActivity);


//                    }
//                    if (jsonObject.getString("status") == "false") {
//                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("text"),this@VisitsActivity);
//                    }
                } catch (e: JSONException) {
                    binding.btnAdd.isEnabled = true
                    progresoCrearVisita.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }

}