package com.sycnos.heyvisitas

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.adapters.MessageAdapter
import com.sycnos.heyvisitas.adapters.NoticesAdapter
import com.sycnos.heyvisitas.data.models.Messages
import com.sycnos.heyvisitas.data.models.Notices
import com.sycnos.heyvisitas.databinding.ActivityMessageBinding
import com.sycnos.heyvisitas.desing.RecyclerTouchListener
import com.sycnos.heyvisitas.util.Mensajes
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding
    private lateinit var progresoMessage : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    private var arrayMessages : ArrayList<Messages> = ArrayList()
    private var mAdapter: MessageAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progresoMessage = ProgressDialog(this@MessageActivity)
        progresoMessage.setMessage("Cargando mensajes...")
        progresoMessage.setIndeterminate(false)
        progresoMessage.setCancelable(false)
        progresoMessage.show()
        val params = RequestParams()
        getMessages(params)

        binding.recycler.addOnItemTouchListener(
            RecyclerTouchListener(
                this,
                binding.recycler,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {

                        val  messages : Messages = arrayMessages.get(position)
                        messages.titulo
                        val i = Intent(this@MessageActivity, MessageHistoryActivity::class.java)
                        i.putExtra("titulo",messages.titulo)
                        i.putExtra("cuerpo",messages.cuerpo)
                        i.putExtra("id",messages.id)
                        startActivity(i)
                    }

                    override fun onLongClick(view: View?, position: Int) {


                    }
                })
        )
//        binding.btnSee.setOnClickListener{
//            val i = Intent(this@MessageActivity, MessageHistoryActivity::class.java)
//            startActivity(i)
//        }
//        binding.tvMessageSolved.setOnClickListener {
//            val i = Intent(this@MessageActivity, MessageSolvedActivity::class.java)
//            startActivity(i)
//        }
        binding.btnBack.setOnClickListener { finish() }
    }

    fun getMessages(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.get(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/mensajes/listado", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoMessage.dismiss()
                var x = responseString

                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@MessageActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                progresoMessage.dismiss()
                binding.tvVisit.isEnabled = true
                var jsonObject: JSONObject? = null
                var jsonArray: JSONArray? = null
                try {
                    progresoMessage.dismiss()
                    //jsonObject = JSONObject(responseString)
                    jsonArray = JSONArray(responseString)

                    if (jsonArray.length() > 0)
                    {

                        //jsonArray = JSONArray(jsonObject.getJSONArray("Departamentos"))
                        for (i in 0 until jsonArray.length())
                        {
                            val messages : Messages = Messages()
                            messages.id = jsonArray.getJSONObject(i).getString("id")
                            messages.titulo = jsonArray.getJSONObject(i).getString("titulo")
                            messages.cuerpo = jsonArray.getJSONObject(i).getString("cuerpo")
                            messages.activo = jsonArray.getJSONObject(i).getString("activo")
                            messages.estatus = jsonArray.getJSONObject(i).getString("estatus")
                            arrayMessages.add(messages)
                        }
                        arrayMessages.size
                        mAdapter =
                            MessageAdapter(this@MessageActivity, arrayMessages )
                        binding.recycler.setAdapter(mAdapter)
                        binding.recycler.setHasFixedSize(true)
                        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@MessageActivity)
                        binding.recycler.setLayoutManager(mLayoutManager)
                        binding.recycler.setItemAnimator(DefaultItemAnimator())

                        // mAdapter!!.notifyDataSetChanged()


                    }
//                    if (jsonObject.getString("message").equals("Datos Incorrectos.")) {
//                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("message"),this@NoticesActivity);
//                    }
                } catch (e: JSONException) {
                    binding.tvVisit.isEnabled = true
                    progresoMessage.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }
}