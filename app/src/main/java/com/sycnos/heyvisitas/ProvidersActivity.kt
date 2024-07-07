package com.sycnos.heyvisitas

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
import androidx.core.view.isVisible
import androidx.navigation.ui.AppBarConfiguration
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.databinding.ActivityProvidersBinding
import com.sycnos.heyvisitas.util.*
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException

class ProvidersActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityProvidersBinding
    private lateinit var progresoProviders: ProgressDialog
    var mensajes : Mensajes = Mensajes()
    var date : String = ""
    var qr : String = ""
    var formatoFechas : FormatoFechas = FormatoFechas()
    var arrayListDescripcion : ArrayList<String> = ArrayList()
    var arrayListIds : ArrayList<String> = ArrayList()
    var sharedPref : SharedPref = SharedPref()
    private var conexion: Conexion? = Conexion()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProvidersBinding.inflate(layoutInflater)
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
            android.R.layout.simple_spinner_item,
            arrayListDescripcion
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spDepartaments.setAdapter(adapter)

        binding.chkBoxFrecuenteProviders.setOnClickListener{
            if (!binding.chkBoxFrecuenteProviders.isChecked) {
                binding.tvWeek.isVisible = false
                binding.spWeeksProviders.isVisible = false
            }
            if (binding.chkBoxFrecuenteProviders.isChecked) {
                binding.tvWeek.isVisible = true
                binding.spWeeksProviders.isVisible = true
            }
        }

        binding.btnBack.setOnClickListener {
            VariablesGlobales.setImagen(null)
            finish() }

        binding.tvDate.setOnClickListener{
            showDatePickerDialog()
        }

        binding.btnSelect.setOnClickListener(View.OnClickListener {

            val i = Intent(this@ProvidersActivity, PickIdentificationProvidersActivity::class.java)
            i.putExtra("date","")
            i.putExtra("deparment","")
            i.putExtra("name","")
            i.putExtra("placas","")
            i.putExtra("frecuently","")
            startActivity(i)
        })

        binding.btnAdd.setOnClickListener(View.OnClickListener {

            var validado :Boolean = true
            binding.btnAdd.isEnabled = false
            progresoProviders = ProgressDialog(this@ProvidersActivity)
            progresoProviders.setMessage("Validando información...")
            progresoProviders.setIndeterminate(false)
            progresoProviders.setCancelable(false)
            progresoProviders.show()

            var conectado : Boolean = false
            conectado = conexion!!.isOnline(this)
            if(conectado)
            {


                if (binding.tvDate.text.toString().isNullOrEmpty()) {
                    progresoProviders.dismiss()
                    validado = false
                    binding.btnAdd.isEnabled = true
                    mensajes!!.mensajeAceptar(
                        "Mensaje",
                        "Favor de seleccionar una fecha",
                        this@ProvidersActivity
                    );
                }

                if (validado) {
                    if (binding.spDepartaments.selectedItem.toString().equals("Seleccionar")) {
                        progresoProviders.dismiss()
                        validado = false
                        binding.btnAdd.isEnabled = true
                        mensajes!!.mensajeAceptar(
                            "Mensaje",
                            "Seleccione un departamento",
                            this@ProvidersActivity
                        );
                    }
                }

                if (validado) {
                    if (binding.etName.text.toString().equals("")) {
                        progresoProviders.dismiss()
                        validado = false
                        binding.btnAdd.isEnabled = true
                        mensajes!!.mensajeAceptar(
                            "Mensaje",
                            "Ingrese un nombre",
                            this@ProvidersActivity
                        );
                    }
                }

                if (validado) {
                    if (binding.etBussines.text.toString().equals("")) {
                        progresoProviders.dismiss()
                        validado = false
                        binding.btnAdd.isEnabled = true
                        mensajes!!.mensajeAceptar(
                            "Mensaje",
                            "Ingrese una empresa",
                            this@ProvidersActivity
                        );
                        //Toast.makeText(this@ProvidersActivity,"Ingrese una empresa...", Toast.LENGTH_SHORT).show()
                    }
                }

//                if (validado) {
//                    if (VariablesGlobales.getImagen() == null) {
//                        progresoProviders.dismiss()
//                        validado = false
//                        binding.btnAdd.isEnabled = true
//                        mensajes!!.mensajeAceptar(
//                            "Mensaje",
//                            "Seleccione una INE o GAFETE",
//                            this@ProvidersActivity
//                        );
//                        //Toast.makeText(this@ProvidersActivity,"Ingrese una empresa...", Toast.LENGTH_SHORT).show()
//                    }
//                }

                if (validado) {
                    if (binding.etResponsable.text.toString().equals("")) {
                        progresoProviders.dismiss()
                        validado = false
                        binding.btnAdd.isEnabled = true
                        mensajes!!.mensajeAceptar(
                            "Mensaje",
                            "Ingrese las placas",
                            this@ProvidersActivity
                        );
                        //Toast.makeText(this@ProvidersActivity,"Ingrese una empresa...", Toast.LENGTH_SHORT).show()
                    }
                }

                if (validado) {
                    if (binding.etTicket.text.toString().equals("")) {
                        progresoProviders.dismiss()
                        validado = false
                        binding.btnAdd.isEnabled = true
                        mensajes!!.mensajeAceptar(
                            "Mensaje",
                            "Ingrese un ticket",
                            this@ProvidersActivity
                        );
                        //Toast.makeText(this@ProvidersActivity,"Ingrese una empresa...", Toast.LENGTH_SHORT).show()
                    }
                }

                if (validado) {
                    if (binding.etTel.text.toString().equals("")) {
                        progresoProviders.dismiss()
                        validado = false
                        binding.btnAdd.isEnabled = true
                        mensajes!!.mensajeAceptar(
                            "Mensaje",
                            "Ingrese un número de contacto",
                            this@ProvidersActivity
                        );
                        //Toast.makeText(this@ProvidersActivity,"Ingrese una empresa...", Toast.LENGTH_SHORT).show()
                    }
                }

                if (validado) {
                    if (binding.etWork.text.toString().equals("")) {
                        progresoProviders.dismiss()
                        validado = false
                        binding.btnAdd.isEnabled = true
                        mensajes!!.mensajeAceptar(
                            "Mensaje",
                            "Ingrese el trabajo a realizar",
                            this@ProvidersActivity
                        );
                        //Toast.makeText(this@ProvidersActivity,"Ingrese una empresa...", Toast.LENGTH_SHORT).show()
                    }
                }



                if (validado) {
                    try {
                        val params = RequestParams()
                        params.put("email", VariablesGlobales.getUser())
                        params.put("password", VariablesGlobales.getPasw())
                        params.put(
                            "departamento_id",
                            arrayListIds.get(binding.spDepartaments.selectedItemPosition)
                        )//binding.spDepartaments.selectedItem.toString()
                        params.put("fecha_registro", formatoFechas.formatoFechatoyyyymmdd(date))
                        params.put("nombre", binding.etName.text.toString())
                        params.put("empresa", binding.etBussines.text.toString())
                        if (VariablesGlobales.getImagen() != null) {
                            params.put("identificacion", VariablesGlobales.getImagen())
                        }
                        if (VariablesGlobales.getImagen() == null) {
                            params.put("identificacion", "")
                        }
                        params.put("responsable", binding.etResponsable.text.toString())
                        params.put("ticket", binding.etTicket.text.toString())
                        params.put("tel_contacto", binding.etTel.text.toString())
                        params.put("frecuencia", "0")
                        params.put("trabajo_realizar", binding.etWork.text.toString())
                        if(!binding.chkBoxFrecuenteProviders.isChecked){
                            params.put("frecuente", "0")
                        }
                        else if(binding.spWeeksProviders.selectedItem.toString().equals("1 Semana"))
                        {
                            params.put("frecuente", "1")
                        }
                        else if(binding.spWeeksProviders.selectedItem.toString().equals("2 Semanas")){
                            params.put("frecuente", "2")
                        }
                        else if(binding.spWeeksProviders.selectedItem.toString().equals("3 Semanas")){
                            params.put("frecuente", "3")
                        }
                        else if(binding.spWeeksProviders.selectedItem.toString().equals("4 Semanas")){
                            params.put("frecuente", "4")
                        }

                        createProviders(params)
                    } catch (e: FileNotFoundException) {
                        progresoProviders.dismiss()
                        binding.btnAdd.isEnabled = true
                        e.toString();
                    } catch (e: java.lang.Exception) {
                        progresoProviders.dismiss()
                        binding.btnAdd.isEnabled = true
                        Toast.makeText(this@ProvidersActivity, e.toString(), Toast.LENGTH_SHORT)
                            .show()

                    }
                }
            }
            else{
                progresoProviders.dismiss()
                binding.btnAdd.isEnabled = true
                mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@ProvidersActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

            }

        })
    }

    override fun onResume() {
        super.onResume()
        val sharedPrefRoles: SharedPreferences =
            this@ProvidersActivity.getSharedPreferences(
                "usuario", MODE_PRIVATE
            )
        val sharedPrefTemp: SharedPreferences =
            this@ProvidersActivity.getSharedPreferences(
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
                    qr=""
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("message") == "Datos correctos.")
                    {
                        var proveedor : String = jsonObject.getJSONObject("proveedor").getString("name")
                        qr = jsonObject.getString("qr")
                        //mensajes!!.mensajeAceptarCerrar("Mensaje",jsonObject.getString("message"),this@ProvidersActivity);
                        VariablesGlobales.setImagen(null)
                        mensajeCompartirAceptar("Mensaje",jsonObject.getString("message"),this@ProvidersActivity);


                    }
                   else{
                        progresoProviders.dismiss()
                        binding.btnAdd.isEnabled = true
                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("message"),this@ProvidersActivity);
                    }
//                    if (jsonObject.getString("message") == "Agrega 2 o mas registros de identidicacion :  (Nombre,Empresa,Identificacion)") {
//                        progresoProviders.dismiss()
//                        binding.btnAdd.isEnabled = true
//                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("text"),this@ProvidersActivity);
//                    }
                } catch (e: JSONException) {
                    binding.btnAdd.isEnabled = true
                    progresoProviders.dismiss()
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
            val text = "${qr}"
            // val info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA)
            //Check if package exists or not. If not then code
            //in catch block will be called
            //waIntent.setPackage("com.whatsapp")
            waIntent.putExtra(Intent.EXTRA_TEXT, text)
            //waIntent.putExtra(Intent.EXTRA_SUBJECT, "Visita generada SYGER de acceso residencial")

            startActivity(Intent.createChooser(waIntent, "Compartir Por"))
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(this@ProvidersActivity, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                .show()
        }
    }
}