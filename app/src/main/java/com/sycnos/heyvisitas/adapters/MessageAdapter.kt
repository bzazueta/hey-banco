package com.sycnos.heyvisitas.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sycnos.heyvisitas.NoticesActivity
import com.sycnos.heyvisitas.R
import com.sycnos.heyvisitas.data.models.Messages
import com.sycnos.heyvisitas.data.models.Notices

class MessageAdapter (activity: Activity, private val arrayMessages: ArrayList<Messages>) : RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

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
      // holder.lblNotice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_circle_24, 0, 0, 0);

        if(messagesList.estatus.equals("Solucionado"))
        {
           // holder.lblNotice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_circle_24,  0,0,0);

        }
    }

    override fun getItemCount(): Int {
        return arrayMessages!!.size
    }
}
