package com.sycnos.heyvisitas

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ExpandableListAdapter
import android.widget.Toast
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
import com.sycnos.heyvisitas.data.models.Visits
import com.sycnos.heyvisitas.databinding.ActivityPendingVisitsBinding
import com.sycnos.heyvisitas.util.Mensajes
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPendingVisitsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener { finish() }
        //setupExpandableListView()

        try
        {
            var validado: Boolean = true
            binding.tvVisit.isEnabled = false
            progresoPendingVisits = ProgressDialog(this@PendingVisitsActivity)
            progresoPendingVisits.setMessage("Obteniendo información...")
            progresoPendingVisits.setIndeterminate(false)
            progresoPendingVisits.setCancelable(false)
            progresoPendingVisits.show()

            val sharedPref: SharedPreferences =
                this@PendingVisitsActivity.getSharedPreferences("user", MODE_PRIVATE
                )
            //****obtener json guardado en shared preferences*****///
            val stringJson = sharedPref.getString("user", "")
            val user = JSONObject(stringJson)
            user.length()

            val pasw = sharedPref.getString("password", "")
            // val pasw = JSONObject(stringPass)
            pasw.toString()
            val params = RequestParams()
            params.put("email", user.getJSONObject("user").getString("email"))
            params.put("password",pasw)
            getPendingVisits(params)

        }catch (e :java.lang.Exception)
        {
            e.toString()
        }
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
                            listData.clear()
                            val visits : Visits = Visits()
                            for (i in 0 until jsonObject.getJSONArray("Visitas Pendientes").length())
                            {
                                val visitList = ArrayList<String>()
                                visits.id_visita = jsonObject.getJSONArray("Visitas Pendientes").getJSONObject(i).getString("id_visita")
                                visits.Nombre_Visita = jsonObject.getJSONArray("Visitas Pendientes").getJSONObject(i).getString("Nombre_Visita")
                                visitList.add(visits.id_visita)
                                //visitList.add(visits.Nombre_Visita)
                                listData[visits.Nombre_Visita] = visitList
                                // VariablesGlobales.arrayListDeptos.add(visits)
                            }
                            listData.size
                            setupExpandableListView()
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
        adapter = CustomExpandableListAdapter(this, titleList as ArrayList<String>, listData)
        expandableListView.setAdapter(adapter)

        expandableListView.setOnGroupExpandListener { groupPosition ->

        }

        expandableListView.setOnGroupCollapseListener { groupPosition ->

        }

        expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            listData.get("ghgh")?.get(childPosition)
            false
        }
    }
}