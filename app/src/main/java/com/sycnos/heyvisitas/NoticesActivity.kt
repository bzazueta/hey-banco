package com.sycnos.heyvisitas

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.adapters.NoticesAdapter
import com.sycnos.heyvisitas.data.models.Notices
import com.sycnos.heyvisitas.databinding.ActivityNoticesBinding
import com.sycnos.heyvisitas.desing.RecyclerTouchListener
import com.sycnos.heyvisitas.util.Conexion
import com.sycnos.heyvisitas.util.Mensajes
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class NoticesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticesBinding
    private var mAdapter: NoticesAdapter? = null
    private var arrayNotices : ArrayList<Notices> = ArrayList()
    private lateinit var progresoNotices : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    private var conexion: Conexion? = Conexion()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoticesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progresoNotices = ProgressDialog(this@NoticesActivity)
        progresoNotices.setMessage("Cargando noticias...")
        progresoNotices.setIndeterminate(false)
        progresoNotices.setCancelable(false)
        progresoNotices.show()

        var conectado : Boolean = false
        conectado = conexion!!.isOnline(this)
        if(conectado) {
            val params = RequestParams()
            getNotices(params)
        }
        else
        {
            progresoNotices.dismiss()
            mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@NoticesActivity);
        }


        // row click listener
        binding.recycler.addOnItemTouchListener(
            RecyclerTouchListener(
                this,
                binding.recycler,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {

                       val  notices : Notices = arrayNotices.get(position)
                        notices.titulo
                        val i = Intent(this@NoticesActivity, ContentNoticeActivity::class.java)
                        i.putExtra("titulo",notices.titulo)
                        i.putExtra("cuerpo",notices.cuerpo)
                        i.putExtra("id",notices.id)
                        startActivity(i)
                    }

                    override fun onLongClick(view: View?, position: Int) {


                    }
                })
        )

        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    fun getNotices(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.get(getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/avisos/listado", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoNotices.dismiss()
                var x = responseString

                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@NoticesActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                progresoNotices.dismiss()
                binding.tvVisit.isEnabled = true
                var jsonObject: JSONObject? = null
                var jsonArray: JSONArray? = null
                try {
                    progresoNotices.dismiss()
                    //jsonObject = JSONObject(responseString)
                    jsonArray = JSONArray(responseString)

                    if (jsonArray.length() > 0)
                    {

                        //jsonArray = JSONArray(jsonObject.getJSONArray("Departamentos"))
                        for (i in 0 until jsonArray.length())
                        {
                           val notices : Notices = Notices()
                           notices.id = jsonArray.getJSONObject(i).getString("id")
                           notices.titulo = jsonArray.getJSONObject(i).getString("titulo")
                           notices.cuerpo = jsonArray.getJSONObject(i).getString("cuerpo")
                           arrayNotices.add(notices)
                        }
                        arrayNotices.size
                        mAdapter =
                            NoticesAdapter(this@NoticesActivity, arrayNotices )
                        binding.recycler.setAdapter(mAdapter)
                        binding.recycler.setHasFixedSize(true)
                        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@NoticesActivity)
                        binding.recycler.setLayoutManager(mLayoutManager)
                        binding.recycler.setItemAnimator(DefaultItemAnimator())

                       // mAdapter!!.notifyDataSetChanged()


                    }
//                    if (jsonObject.getString("message").equals("Datos Incorrectos.")) {
//                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("message"),this@NoticesActivity);
//                    }
                } catch (e: JSONException) {
                    binding.tvVisit.isEnabled = true
                    progresoNotices.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }


}