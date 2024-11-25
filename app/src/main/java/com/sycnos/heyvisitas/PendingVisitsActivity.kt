package com.sycnos.heyvisitas

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ExpandableListAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.data.models.Visits
import com.sycnos.heyvisitas.databinding.ActivityPendingVisitsBinding
import com.sycnos.heyvisitas.util.Conexion
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.SharedPref
import com.sycnos.heyvisitas.util.VariablesGlobales
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject

class PendingVisitsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPendingVisitsBinding
    private lateinit var progresoPendingVisits : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    private var adapter: ExpandableListAdapter? = null
    private var titleList: List<String>? = null
    val listData = HashMap<String, List<String>>()
    var sharedPref : SharedPref = SharedPref()
    private var conexion: Conexion? = Conexion()
    val qrListActivas = ArrayList<String>()
    val qrListPendientes = ArrayList<String>()
    val spinnerList = ArrayList<String>()
    val spinnerListPendientes = ArrayList<String>()
    val listPendientes = ArrayList<String>()
    val listactivos = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPendingVisitsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener { finish() }
        //setupExpandableListView()






    }

    override fun onResume() {
        spinnerList.clear()
        spinnerListPendientes.clear()
        try
        {
            var validado: Boolean = true
            binding.tvVisit.isEnabled = false
            progresoPendingVisits = ProgressDialog(this@PendingVisitsActivity)
            progresoPendingVisits.setMessage("Obteniendo información...")
            progresoPendingVisits.setIndeterminate(false)
            progresoPendingVisits.setCancelable(false)
            progresoPendingVisits.show()

//            var user  = sharedPref.getUsuario(this@PendingVisitsActivity)
//            var pasw  = sharedPref.getPass(this@PendingVisitsActivity)
            var conectado : Boolean = false
            conectado = conexion!!.isOnline(this)
            if(conectado) {
                val params = RequestParams()
                params.put("email", VariablesGlobales.getUser())
                params.put("password", VariablesGlobales.getPasw())
                getPendingVisits(params)
            }else{
                progresoPendingVisits.dismiss()
                mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@PendingVisitsActivity);

            }

        }catch (e :java.lang.Exception)
        {
            e.toString()
        }
        super.onResume()
    }

    fun getPendingVisits(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/visitas/pendientes", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoPendingVisits.dismiss()
                binding.tvVisit.isEnabled = true
                var x = responseString

                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@PendingVisitsActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                progresoPendingVisits.dismiss()
                binding.tvVisit.isEnabled = true
                var jsonObject: JSONObject? = null
                try {
                    progresoPendingVisits.dismiss()
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.has("status")) {
                        if (jsonObject.getString("status").equals("true"))
                        {
                            qrListActivas.clear()
                            qrListPendientes.clear()
                            listData.clear()
                            val visits : Visits = Visits()
                            spinnerList.add("VISITAS ACTIVAS")
                            listactivos.add("VISITAS ACTIVAS")
                            qrListActivas.add("VISITAS ACTIVAS")
                             if(jsonObject.has("Visitas Activas")){
                                 for (i in 0 until jsonObject.getJSONArray("Visitas Activas").length())
                                 {
                                     val visitList = ArrayList<String>()
                                     visits.id_visita = jsonObject.getJSONArray("Visitas Activas").getJSONObject(i).getString("id_visita")
                                     visits.Nombre_Visita = jsonObject.getJSONArray("Visitas Activas").getJSONObject(i).getString("Nombre_Visita")
                                     visits.qrLink= getString(R.string.urlDominio) +"/public/api/qr/"+ jsonObject.getJSONArray("Visitas Activas").getJSONObject(i).getString("qrlink")
                                     qrListActivas.add(visits.qrLink)
                                     visitList.add(visits.id_visita +"|"+ visits.qrLink)
                                     listactivos.add(visits.id_visita)
                                     //visitList.add(visits.Nombre_Visita)
                                     listData[visits.Nombre_Visita] = visitList
                                     // VariablesGlobales.arrayListDeptos.add(visits)
                                     spinnerList.add(visits.Nombre_Visita)

                                 }
                             }

                            spinnerListPendientes.add("VISITAS PENDIENTES")
                            listPendientes.add("VISITAS PENDIENTES")
                            qrListPendientes.add("VISITAS PENDIENTES")
                            if(jsonObject.has("Visitas Pendientes")){
                                for (i in 0 until jsonObject.getJSONArray("Visitas Pendientes").length())
                                {
                                    val visitList = ArrayList<String>()
                                    visits.id_visita = jsonObject.getJSONArray("Visitas Pendientes").getJSONObject(i).getString("id_visita")
                                    visits.Nombre_Visita = jsonObject.getJSONArray("Visitas Pendientes").getJSONObject(i).getString("Nombre_Visita")
                                    visits.qrLink= getString(R.string.urlDominio) +"/public/api/qr/"+ jsonObject.getJSONArray("Visitas Pendientes").getJSONObject(i).getString("qrlink")
                                    qrListPendientes.add(visits.qrLink)
                                    visitList.add(visits.id_visita +"|"+ visits.qrLink)
                                    listPendientes.add(visits.id_visita)
                                    //visitList.add(visits.Nombre_Visita)
                                    listData[visits.Nombre_Visita] = visitList
                                    // VariablesGlobales.arrayListDeptos.add(visits)
                                    spinnerListPendientes.add(visits.Nombre_Visita)

                                }
                            }

                            listData.size

                            val adapter = ArrayAdapter(this@PendingVisitsActivity, android.R.layout.simple_spinner_item, spinnerList)

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.spinner.adapter = adapter

                            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                    // Obtén el elemento seleccionado
                                    val selectedItem = listactivos.get(position).toString()
                                    val selectedQr = qrListActivas.get(position).toString()
                                    //Toast.makeText(this@PendingVisitsActivity, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
                                    if(!selectedItem.equals("VISITAS ACTIVAS")){
                                        val intent  = Intent(this@PendingVisitsActivity,PendingVisitsDetailActivity::class.java)
                                        intent.putExtra("idVisita",selectedItem)
                                        intent.putExtra("qr",selectedQr)
                                        startActivity(intent)
                                    }

                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {
                                    // Maneja el caso donde nada es seleccionado
                                }
                            }

                            val adapter2 = ArrayAdapter(this@PendingVisitsActivity, android.R.layout.simple_spinner_item, spinnerListPendientes)

                            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.spinnerVisitas.adapter = adapter2

                            binding.spinnerVisitas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                    // Obtén el elemento seleccionado
                                    val selectedItem =  listPendientes.get(position).toString()
                                    val selectedQr = qrListPendientes.get(position).toString()
                                    //Toast.makeText(this@PendingVisitsActivity, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
                                    if(!selectedItem.equals("VISITAS PENDIENTES")){
                                        val intent  = Intent(this@PendingVisitsActivity,PendingVisitsDetailActivity::class.java)
                                        intent.putExtra("idVisita",selectedItem)
                                        intent.putExtra("qr",selectedQr)
                                        startActivity(intent)
                                    }

                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {
                                    // Maneja el caso donde nada es seleccionado
                                }
                            }
                            //setupExpandableListView()
                        }
                    }
                    else{
                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("message"),this@PendingVisitsActivity);
                    }
                } catch (e: JSONException) {
                    binding.tvVisit.isEnabled = true
                    progresoPendingVisits.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }

    private fun setupExpandableListView() {
        val expandableListView = binding.expandableListView
        val listData_ = listData
        titleList = ArrayList(listData_.keys)
        adapter = CustomExpandableListAdapter(this@PendingVisitsActivity, titleList as ArrayList<String>, listData,qrListActivas)
        expandableListView.setAdapter(adapter)

        expandableListView.setOnGroupExpandListener { groupPosition ->

        }

        expandableListView.setOnGroupCollapseListener { groupPosition ->

        }

        expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            //listData.get("ghgh")?.get(childPosition)
            false
        }
    }
}