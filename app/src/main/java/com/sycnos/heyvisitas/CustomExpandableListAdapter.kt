package com.sycnos.heyvisitas

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.data.models.Visits
import com.sycnos.heyvisitas.databinding.ListGroupBinding
import com.sycnos.heyvisitas.databinding.ListItemBinding
import com.sycnos.heyvisitas.util.Conexion
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.SharedPref
import com.sycnos.heyvisitas.util.VariablesGlobales
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject


class CustomExpandableListAdapter internal constructor(
    private val context: Activity,
    private val titleList: List<String>,
    private val dataList: HashMap<String, List<String>>,
    private val qrList: ArrayList<String>
) : BaseExpandableListAdapter() {

    var sharedPref : SharedPref = SharedPref()
    private lateinit var progresoCreateVisits : ProgressDialog
    var mensajes : Mensajes = Mensajes()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var groupBinding: ListGroupBinding
    private lateinit var itemBinding: ListItemBinding
    private var conexion: Conexion? = Conexion()

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return this.dataList[this.titleList[listPosition]]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        view: View?,
        parent: ViewGroup
    ): View {
        var convertView = view
        val holder: ItemViewHolder
        if (convertView == null) {
            itemBinding = ListItemBinding.inflate(inflater)
            convertView = itemBinding.root
            holder = ItemViewHolder()
            holder.label = itemBinding.expandedListItem
            holder.btnAdd = itemBinding.btnBack
            holder.btnCompartirQr = itemBinding.btnCompartirQr
            holder.spHours = itemBinding.spHours
            holder.spMinutes = itemBinding.spMinutes
            convertView.tag = holder
        } else {
            holder = convertView.tag as ItemViewHolder
        }
        val expandedListText = getChild(listPosition, expandedListPosition) as String
        holder.label!!.text = expandedListText
        holder.label!!.visibility = View.GONE
        holder.btnAdd!!.setOnClickListener(View.OnClickListener {
        try {
            val idVisita = getChild(listPosition, expandedListPosition) as String
            val spnHour = holder.spHours!!.selectedItem.toString()
            val spnMinutes = holder.spMinutes!!.selectedItem.toString()
           // Toast.makeText(context, idVisita + "-" + spnHour + "-" + spnMinutes, Toast.LENGTH_SHORT).show()
            progresoCreateVisits = ProgressDialog(context)
            progresoCreateVisits.setMessage("Registrando salida...")
            progresoCreateVisits.setIndeterminate(false)
            progresoCreateVisits.setCancelable(false)
            progresoCreateVisits.show()

            var conectado : Boolean = false
            conectado = conexion!!.isOnline(context)
            if(conectado) {
                val params = RequestParams()
                params.put("email", VariablesGlobales.getUser())
                params.put("password", VariablesGlobales.getPasw())
                params.put("id_visitas", idVisita)
                createVisits(params)
            }
            else{
                progresoCreateVisits.dismiss()
                mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",context);

            }
        }catch (e : java.lang.Exception)
        {
            e.toString()
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT)
                .show()

        }


        })

        holder.btnCompartirQr?.setOnClickListener(View.OnClickListener {

//            val res = getChild(listPosition, expandedListPosition) as String
//            var qr_split = res.toString().split("|")val qr = qrList.get(listPosition)

            var qr = qrList.get(listPosition)
            shareLinkQr(qr)
        })
        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return this.dataList[this.titleList[listPosition]]!!.size
    }

    override fun getGroup(listPosition: Int): Any {
        return this.titleList[listPosition]
    }

    override fun getGroupCount(): Int {
        return this.titleList.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(
        listPosition: Int,
        isExpanded: Boolean,
        view: View?,
        parent: ViewGroup
    ): View {
        var convertView = view
        val holder: GroupViewHolder
        if (convertView == null) {
            groupBinding = ListGroupBinding.inflate(inflater)
            convertView = groupBinding.root
            holder = GroupViewHolder()
            holder.label = groupBinding.listTitle
            convertView.tag = holder
        } else {
            holder = convertView.tag as GroupViewHolder

        }
        val listTitle = getGroup(listPosition) as String
        holder.label!!.text = listTitle
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

    inner class ItemViewHolder {
        internal var label: TextView? = null
        internal var btnAdd: Button? = null
        internal var btnCompartirQr: Button? = null
        internal var spHours : Spinner? = null
        internal var spMinutes : Spinner? = null
    }

    inner class GroupViewHolder {
        internal var label: TextView? = null
        internal var btnAdd: Button? = null
        internal var btnCompartirQr: Button? = null
    }

    fun createVisits(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(context.getString(com.sycnos.heyvisitas.R.string.urlDominio)+"/public/api/salidas", params, object : TextHttpResponseHandler() {
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
                    context)

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
                    if (jsonObject.getString("message") == "Salida correctamente.")
                    {
                        mensajes!!.mensajeAceptarCerrar("Mensaje",jsonObject.getString("message"),context);
                    }
                    else {
                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("message"),context);
                    }

                } catch (e: JSONException) {
                    progresoCreateVisits.dismiss()
                    e.printStackTrace()
                }
            }
        })
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

            context.startActivity(Intent.createChooser(waIntent, "Compartir Por"))
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                .show()
        }
    }
}