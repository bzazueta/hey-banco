package com.sycnos.heyvisitas.adapters

import android.app.Activity
import android.graphics.Color
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.scale
import androidx.recyclerview.widget.RecyclerView
import com.sycnos.heyvisitas.R
import com.sycnos.heyvisitas.data.models.Messages


class MessageAdapter (private val activity: Activity, private val arrayMessages: ArrayList<Messages>) : RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    var x : String = ""

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lblNotice: TextView = view.findViewById(R.id.tvMessage)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_notices, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var messagesList : Messages = arrayMessages!![position]
        holder.lblNotice.setText(messagesList.titulo)
        holder.lblNotice.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        if(messagesList.estatus.equals("Solucionado"))
        {
            val spannable = SpannableString(activity.getString(R.string.true_tick))

            //holder.lblNotice.setText(messagesList.titulo + "   " + spannable.toString())
            holder.lblNotice.setText(
                Html.fromHtml("<html><body><font size=5 color=black> ${messagesList.titulo}</font><font size=1 color=green> ${spannable} </font> </body><html>")
            )

            //holder.lblNotice.setCompoundDrawablesWithIntrinsicBounds(0,  0,R.drawable.baseline_check_circle_24,0);

        }
    }

    override fun getItemCount(): Int {
        return arrayMessages!!.size
    }
}
