package com.sycnos.heyvisitas

import android.R
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.databinding.ActivityVisitsBinding
import com.sycnos.heyvisitas.util.*
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.ArrayList


class VisitsActivity : AppCompatActivity() {

    var qr : String = ""
    private lateinit var binding: ActivityVisitsBinding
    private lateinit var progresoCrearVisita : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    var date : String = ""
    var formatoFechas : FormatoFechas = FormatoFechas()
    var identificacion: File? = null
    var arrayListDescripcion : ArrayList<String> = ArrayList()
    var arrayListIds : ArrayList<String> = ArrayList()
    var sharedPref : SharedPref = SharedPref()
    private var conexion: Conexion? = Conexion()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVisitsBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            this,
            R.layout.simple_spinner_item,
            arrayListDescripcion
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spDepartaments.setAdapter(adapter)

        binding.tvDate.setOnClickListener{
            showDatePickerDialog()
        }

        binding.btnBack.setOnClickListener{
            VariablesGlobales.setImagen(null)
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

            var conectado : Boolean = false
            conectado = conexion!!.isOnline(this)
            if(conectado)
            {
                if (binding.tvDate.text.toString().equals("")) {
                    binding.btnAdd.isEnabled = true
                    progresoCrearVisita.dismiss()
                    validado = false
                    //Toast.makeText(this@VisitsActivity,"Seleccione una fecha", Toast.LENGTH_SHORT).show()
                    mensajes!!.mensajeAceptar(
                        "Mensaje",
                        "Seleccione una fecha",
                        this@VisitsActivity
                    );                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

                }
                if (validado) {
                    if (binding.spDepartaments.selectedItem.toString().equals("Seleccionar")) {
                        binding.btnAdd.isEnabled = true
                        progresoCrearVisita.dismiss()
                        validado = false
                        mensajes!!.mensajeAceptar(
                            "Mensaje",
                            "Seleccione un departamento",
                            this@VisitsActivity
                        );                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()
                    }
                }

                if (validado) {
                    if (binding.etName.text.toString().isNullOrEmpty()) {
                        binding.btnAdd.isEnabled = true
                        progresoCrearVisita.dismiss()
                        validado = false
                        mensajes!!.mensajeAceptar(
                            "Mensaje",
                            "Ingrese un nombre",
                            this@VisitsActivity
                        );
                    }
                }

                if (validado) {
                    if (binding.etPlacas.text.toString().isNullOrEmpty()) {
                        binding.btnAdd.isEnabled = true
                        progresoCrearVisita.dismiss()
                        validado = false
                        mensajes!!.mensajeAceptar(
                            "Mensaje",
                            "Ingrese una placa ",
                            this@VisitsActivity
                        );
                    }
                }

                if (validado) {
                    if (VariablesGlobales.getImagen() == null) {
                        binding.btnAdd.isEnabled = true
                        progresoCrearVisita.dismiss()
                        validado = false
                        mensajes!!.mensajeAceptar(
                            "Mensaje",
                            "Ingrese una indentificación ",
                            this@VisitsActivity
                        );
                    }
                }

                if (validado) {
                    try {
                        var frecuently: String = ""
                        if (!binding.cbFrecuently.isChecked) {
                            frecuently = "0"
                        }
                        if (binding.cbFrecuently.isChecked) {
                            frecuently = "1"
                        }

                        val params = RequestParams()
                        params.put("email", VariablesGlobales.getUser())
                        params.put("password", VariablesGlobales.getPasw())
                        params.put(
                            "placas",
                            if (binding.etPlacas.text.toString()
                                    .equals("")
                            ) "" else binding.etPlacas.text.toString()
                        )
                        params.put("fecha_registro", formatoFechas.formatoFechatoyyyymmdd(date))
                        params.put(
                            "departamento_id",
                            arrayListIds.get(binding.spDepartaments.selectedItemPosition)
                        )
                        params.put(
                            "nombre",
                            if (binding.etName.text.toString()
                                    .equals("")
                            ) "" else binding.etName.text.toString()
                        )
                        params.put("frecuencia", frecuently)
                        if (VariablesGlobales.getImagen() != null) {
                            params.put("identificacion", VariablesGlobales.getImagen())
                        }
                        if (VariablesGlobales.getImagen() == null) {
                            params.put("identificacion", "")
                        }
                        crearVisita(params)
                    } catch (e: FileNotFoundException) {
                        mensajes!!.mensajeAceptar("Mensaje",e.toString(),this@VisitsActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

                        progresoCrearVisita.dismiss()
                        binding.btnAdd.isEnabled = true
                        e.toString();
                    } catch (ex: Exception) {
                        progresoCrearVisita.dismiss()
                        binding.btnAdd.isEnabled = true
                        ex.toString();
                    }
                }
            }
            else{

                binding.btnAdd.isEnabled = true
                progresoCrearVisita.dismiss()
                mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@VisitsActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

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
                    qr=""
                    progresoCrearVisita.dismiss()
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("message") == "Datos correctos.")
                    {
//                        binding.etName.setText("")
//                        binding.etPlacas.setText("")
//                        binding.tvDate.setText("")
                        qr = jsonObject.getString("qr")

                        //mensajes!!.mensajeAceptarCerrar("Mensaje",jsonObject.getString("message"),this@VisitsActivity);
                        mensajeCompartirAceptar("Mensaje",jsonObject.getString("message"),this@VisitsActivity)
                    }
                    else{
//                        qr = jsonObject.getString("message")
//                        mensajeCompartirAceptar("Mensaje",jsonObject.getString("message"),this@VisitsActivity)

                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("message"),this@VisitsActivity);
                    }
                } catch (e: JSONException) {
                    binding.btnAdd.isEnabled = true
                    progresoCrearVisita.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }

    fun mensajeCompartirAceptar(titulo: String ,mensaje : String,activity : Activity) {
        val builder = AlertDialog.Builder(activity)
        with(builder)
        {
            setTitle(titulo)
            setCancelable(false);
            val message = setMessage(mensaje)
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener {
                        dialog, id ->
                    activity.finish()
                })
            show()
            val message2 = setMessage(mensaje)
                .setNegativeButton("Compartir Link", DialogInterface.OnClickListener {
                        dialog, id ->
                    shareLinkQr()
                })
            show()
        }
    }

    private fun shareLinkQr(){
        // val pm: PackageManager = getPackageManager()
        try {
            val waIntent = Intent(Intent.ACTION_SEND)
            waIntent.type = "text/plain"
            val text = " ${qr}"
            // val info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA)
            //Check if package exists or not. If not then code
            //in catch block will be called
            //waIntent.setPackage("com.whatsapp")
            waIntent.putExtra(Intent.EXTRA_TEXT, text)
            //waIntent.putExtra(Intent.EXTRA_SUBJECT, "Visita generada SYGER de acceso residencial")

            startActivity(Intent.createChooser(waIntent, "Compartir Por"))
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(this@VisitsActivity, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                .show()
        }
    }
}