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
import com.sycnos.heyvisitas.databinding.ActivityPendingVisitsBinding
import com.sycnos.heyvisitas.util.Mensajes
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject

class PendingVisitsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPendingVisitsBinding
    private lateinit var progresoPendingVisits : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    private var adapter: ExpandableListAdapter? = null
    private var titleList: List<String>? = null
    val data: HashMap<String, List<String>>
        get() {
            val listData = HashMap<String, List<String>>()

            val visitList = ArrayList<String>()
            visitList.add("Hora de salida")

            val visitList2 = ArrayList<String>()
            visitList2.add("Hora de salida")

            val visitList3 = ArrayList<String>()
            visitList3.add("Hora de salida")

            val visitList4 = ArrayList<String>()
            visitList4.add("Hora de salida")

            val visitList5 = ArrayList<String>()
            visitList5.add("Hora de salida")

            val visitList6 = ArrayList<String>()
            visitList6.add("Hora de salida")

            val visitList7 = ArrayList<String>()
            visitList7.add("Hora de salida")

            val visitList8 = ArrayList<String>()
            visitList8.add("Hora de salida")


            listData["Visita 1"] = visitList
            listData["Visita 2"] = visitList2
            listData["Visita 3"] = visitList3
            listData["Visita 4"] = visitList4
            listData["Visita 5"] = visitList5
            listData["Visita 6"] = visitList6
            listData["Visita 7"] = visitList7
            listData["Visita 8"] = visitList8


            return listData
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPendingVisitsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener { finish() }
        setupExpandableListView()

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
    private fun setupExpandableListView() {
        val expandableListView = binding.expandableListView
        val listData = data
        titleList = ArrayList(listData.keys)
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
//                    if (jsonObject.getString("message").equals("Datos correctos."))
//                    {
//
//                    }
//                    if (jsonObject.getString("message").equals("Datos Incorrectos.")) {
//                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("message"),this@HomeActivity);
//                    }
                } catch (e: JSONException) {
                    binding.tvVisit.isEnabled = true
                    progresoPendingVisits.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }
}