package com.sycnos.heyvisitas

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.SeeFilePendingVisitActivity
import com.sycnos.heyvisitas.databinding.ActivityPendingVisitsDetailBinding
import com.sycnos.heyvisitas.util.Conexion
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.VariablesGlobales
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar

class PendingVisitsDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPendingVisitsDetailBinding

    var idVisita = ""
    var imagen = ""
    var qr = ""
    private lateinit var progresoScannerVisit : ProgressDialog
    private lateinit var progresoRegister: ProgressDialog
    private lateinit var progresoCreateVisits: ProgressDialog
    var mensajes : Mensajes = Mensajes()
    private var conexion: Conexion? = Conexion()
    var selectedDate =""
    var selectedDatime =""
    var date : String = ""
    var dateTime : String = ""
    var fechaFinal : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingVisitsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idVisita =if (intent.getStringExtra("idVisita") == null) "" else intent.getStringExtra("idVisita")!!
        qr = if (intent.getStringExtra("qr") == null) "" else intent.getStringExtra("qr")!!
        try
        {
            var validado: Boolean = true

            progresoScannerVisit = ProgressDialog(this@PendingVisitsDetailActivity)
            progresoScannerVisit.setMessage("Registrando Entrada...")
            progresoScannerVisit.setIndeterminate(false)
            progresoScannerVisit.setCancelable(false)
            progresoScannerVisit.show()

            var conectado : Boolean = false
            conectado = conexion!!.isOnline(this)
            if(conectado)
            {
                val params = RequestParams()
                params.put("id_visitas", idVisita)
                params.put("email", VariablesGlobales.getUser())
                params.put("password", VariablesGlobales.getPasw())
                getDetailVisits(params)
            }
            else{
                binding.btnEliminar.isEnabled = true
                progresoScannerVisit.dismiss()
                mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@PendingVisitsDetailActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

            }


        }catch (e : java.lang.Exception)
        {
            progresoScannerVisit.dismiss()
           // binding.btnEnter.isEnabled = true
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnEliminar.setOnClickListener {
            var validado : Boolean = true
            binding.btnEliminar.isEnabled = false


            var conectado : Boolean = false
            conectado = conexion!!.isOnline(this)
            if(conectado)
            {
                alertDelete("ATENCION","")


            }else{
                binding.btnEliminar.isEnabled = true
                progresoCreateVisits.dismiss()
                mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@PendingVisitsDetailActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

            }
        }

        binding.btnId.setOnClickListener {
            var validado :Boolean = true
            if(imagen.equals("") || imagen.equals("null"))
            {
                validado = false
                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    "Este mensaje no contiene identificación",
                    this@PendingVisitsDetailActivity)
            }
            if(validado) {
                val i = Intent(this@PendingVisitsDetailActivity, SeeFilePendingVisitActivity::class.java)
                i.putExtra("id", idVisita)
                i.putExtra("url_archivo", imagen)
                startActivity(i)
            }
        }

        binding.btnRegistro.setOnClickListener {


                var validado : Boolean = true
                binding.btnRegistro.isEnabled = false
                progresoRegister = ProgressDialog(this@PendingVisitsDetailActivity)
                progresoRegister.setMessage("Registrando visita...")
                progresoRegister.setIndeterminate(false)
                progresoRegister.setCancelable(false)
                progresoRegister.show()

                var conectado : Boolean = false
                conectado = conexion!!.isOnline(this)
                if(conectado)
                {
                    progresoRegister.dismiss()
                    binding.btnRegistro.isEnabled = true
                    showDatePickerDialog()
//                    if(binding.txtNombre.text.toString().equals(""))
//                    {
//                        binding.btnEliminar.isEnabled = true
//                        progresoRegister.dismiss()
//                        validado = false
//                        //Toast.makeText(this@VisitsActivity,"Seleccione una fecha", Toast.LENGTH_SHORT).show()
//                        mensajes!!.mensajeAceptar("Mensaje","Ingrese un nombre",this@PendingVisitsDetailActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()
//                    }
//
//                    if(binding.txtTelefono.text.toString().isNullOrEmpty() && binding.e.text.toString().isNullOrEmpty())
//                    {
//                        binding.btnRegister.isEnabled = true
//                        progresoRegister.dismiss()
//                        validado = false
//                        //Toast.makeText(this@VisitsActivity,"Seleccione una fecha", Toast.LENGTH_SHORT).show()
//                        mensajes!!.mensajeAceptar("Mensaje","Ingrese un Teléfono o un Correo",this@RegisterActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()
//                    }
//
//                    if(validado)
//                    {
//                        val params = RequestParams()
//                        params.put("email", VariablesGlobales.getUser())
//                        params.put("password",VariablesGlobales.getPasw())
//                        params.put("nombre",binding.txtNombre.text.toString())
//                       // params.put("departamento_id",arrayListIds.get(binding.spDepartaments.selectedItemPosition))
//                        params.put("asunto",binding.txtAsunto.text.toString())
//                        params.put("tel_contacto",binding.txtTelefono.text.toString())
//                        params.put("email_contacto",binding.txtFecha.text.toString())
//                        crearRegistro(params)
//                    //}
               }
                else{
                    binding.btnRegistro.isEnabled = true
                    progresoRegister.dismiss()
                    mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@PendingVisitsDetailActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

                }

        }

        binding.btnCompartirQrDetail.setOnClickListener {
            shareLinkQr(qr)
        }
    }

    fun getDetailVisits(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.get(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/visitas/qr/${idVisita}", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoScannerVisit.dismiss()
                var x = responseString

                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@PendingVisitsDetailActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                progresoScannerVisit.dismiss()

                var jsonObject: JSONObject? = null
                var jsonArray: JSONArray? = null
                try
                {
                    progresoScannerVisit.dismiss()
                    jsonObject = JSONObject(responseString)

                    var tipoVisita = jsonObject.getString("tipo")
                    binding.txtNombre.text = jsonObject.getString("name")
                    binding.txtFecha.text = jsonObject.getString("fecha_inicio")
                    binding.txtFechaFin.text = jsonObject.getString("fecha_final")
                    binding.txtTelefono.text = jsonObject.getString("tel_contacto")
                    binding.txtAsunto.text = jsonObject.getString("trabajo_realizar")
                    imagen = jsonObject.getString("identificacion")



                } catch (e: JSONException) {

                    progresoScannerVisit.dismiss()
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
                binding.btnRegistro.isEnabled = true
                var x = responseString

                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@PendingVisitsDetailActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                progresoRegister.dismiss()
                binding.btnRegistro.isEnabled = true
                var jsonObject: JSONObject? = null
                try {
                    progresoRegister.dismiss()
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("message") == "Notificación enviada.")
                    {
                        mensajes!!.mensajeAceptarCerrar("Mensaje",jsonObject.getString("message"),this@PendingVisitsDetailActivity);
                    }
                    else{
                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("message"),this@PendingVisitsDetailActivity);
                    }
                } catch (e: JSONException) {
                    progresoRegister.dismiss()
                    binding.btnRegistro.isEnabled = true
                    e.printStackTrace()
                }
            }
        })
    }

    fun deleteVisits(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.get(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/eliminar/${idVisita}", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoCreateVisits.dismiss()
                var x = responseString

                mensajes!!.mensajeAceptarExpandableList(
                    "Mensaje",
                    responseString,
                    this@PendingVisitsDetailActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            )
            {

                var jsonObject: JSONObject? = null
                try
                {
                    progresoCreateVisits.dismiss()
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("message") == "Visita Eliminada.")
                    {
                        basicAlert("Mensaje",jsonObject.getString("message"))
                       // mensajes!!.mensajeAceptarCerrar("Mensaje",jsonObject.getString("message"),this@PendingVisitsDetailActivity);
                    }
                    else {
                        basicAlert("Mensaje",jsonObject.getString("message"))
                        //mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("message"),this@PendingVisitsDetailActivity);
                    }

                } catch (e: JSONException) {
                    progresoCreateVisits.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }

    fun basicAlert(titulo: String ,mensaje : String) {


        val builder: AlertDialog.Builder = AlertDialog.Builder(this@PendingVisitsDetailActivity)
        val inflater = this@PendingVisitsDetailActivity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
            finish()

        }
    }

    fun alertSalida(titulo: String ,mensaje : String) {


        val builder: AlertDialog.Builder = AlertDialog.Builder(this@PendingVisitsDetailActivity)
        val inflater = this@PendingVisitsDetailActivity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.dialog_salida, null, false)

        builder.setView(customView)
        builder.setCancelable(false)
        val lblReglamento =  customView.findViewById<View>(R.id.lblReglamento) as TextView
        val lblAceptar = customView.findViewById<View>(R.id.lblAceptar) as TextView
        val lblCancelar = customView.findViewById<View>(R.id.lblCancelar) as TextView
        val lblTexto = customView.findViewById<View>(R.id.txtSalida) as EditText
        lblReglamento.text = titulo
        lblTexto.setText(fechaFinal)
        //lblTexto.setMovementMethod(ScrollingMovementMethod())
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

        lblTexto.setOnClickListener {
            showDatePickerDialog()
            alertDialog.dismiss()
        }

        lblAceptar.setOnClickListener {
            alertDialog.dismiss()
            val params = RequestParams()
            params.put("email", VariablesGlobales.getUser())
            params.put("password", VariablesGlobales.getPasw())
            params.put("nombre", binding.txtNombre.text.toString())
//                       // params.put("departamento_id",arrayListIds.get(binding.spDepartaments.selectedItemPosition))
            params.put("asunto", binding.txtAsunto.text.toString())
            params.put("tel_contacto", binding.txtTelefono.text.toString())
            params.put("email_contacto", binding.txtFecha.text.toString())
            params.put("hora_salida", fechaFinal)
            crearRegistro(params)


        }

        lblCancelar.setOnClickListener {
            alertDialog.dismiss()

        }
    }

    fun alertDelete(titulo: String ,mensaje : String) {


        val builder: AlertDialog.Builder = AlertDialog.Builder(this@PendingVisitsDetailActivity)
        val inflater = this@PendingVisitsDetailActivity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.dialog_delete, null, false)

        builder.setView(customView)
        builder.setCancelable(false)
        val lblReglamento =  customView.findViewById<View>(R.id.lblReglamento) as TextView
        val lblAceptar = customView.findViewById<View>(R.id.lblAceptar) as TextView
        val lblCancelar = customView.findViewById<View>(R.id.lblCancelar) as TextView
        val lblTexto = customView.findViewById<View>(R.id.txtSalida) as TextView
        lblReglamento.text = titulo
        lblTexto.setText("¿Desea eliminar esta visita?")
        //lblTexto.setMovementMethod(ScrollingMovementMethod())
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

        lblTexto.setOnClickListener {
            showDatePickerDialog()
            alertDialog.dismiss()
        }

        lblAceptar.setOnClickListener {
            alertDialog.dismiss()
            progresoCreateVisits = ProgressDialog(this@PendingVisitsDetailActivity)
            progresoCreateVisits.setMessage("Eliminando visita...")
            progresoCreateVisits.setIndeterminate(false)
            progresoCreateVisits.setCancelable(false)
            progresoCreateVisits.show()
            val params = RequestParams()
            params.put("email", VariablesGlobales.getUser())
            params.put("password", VariablesGlobales.getPasw())
            deleteVisits(params)


        }

        lblCancelar.setOnClickListener {
            alertDialog.dismiss()

        }
    }

    private fun showDatePickerDialog() {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = day.toString() + " / " + (month + 1) + " / " + year
            date = year.toString() + "-" + (month + 1) + "-" + day.toString()
            dateTime =  year.toString() + "-" + (month + 1) + "-" + day.toString() +" "+  Calendar.HOUR_OF_DAY +":"+ Calendar.MINUTE
           // binding.tvDate.setText(selectedDate)
            showDateTimePickerDialog()
        }, year, month, day).show()

    }
    private fun showDateTimePickerDialog() {

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            selectedDatime = String.format(" %02d:%02d", selectedHour, selectedMinute)
            fechaFinal = selectedDate +" "+ selectedDatime
            alertSalida("HORA SALIDA","")
        }, hour, minute, true).show()
    }

    private fun shareLinkQr(qr :String){
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
            // waIntent.putExtra(Intent.EXTRA_SUBJECT, "Visita generada SYGER de acceso residencial")

            this@PendingVisitsDetailActivity.startActivity(Intent.createChooser(waIntent, "Compartir Por"))
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(this@PendingVisitsDetailActivity, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                .show()
        }
    }
}