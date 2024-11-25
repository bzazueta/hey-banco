package com.sycnos.heyvisitas.adapters

import android.app.Activity
import android.text.Html
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sycnos.heyvisitas.R
import com.sycnos.heyvisitas.data.models.Messages
import com.sycnos.heyvisitas.util.VariablesGlobales

class ChatAdapter(private val activity: Activity, private val arrayMessages: List<Messages>) : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {


    var x : String = ""

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //val tvSender: TextView = itemView.findViewById(R.id.tvSender)
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
       // val tvSender2: TextView = itemView.findViewById(R.id.tvSender2)
        val tvMessage2: TextView = itemView.findViewById(R.id.tvMessage2)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var messagesList : Messages = arrayMessages!![position]

        if(!messagesList.id.equals(VariablesGlobales.idUser))
        {
           // holder.tvSender.text = messagesList.titulo
            holder.tvMessage.text = messagesList.cuerpo
        }
        else{
            //holder.tvSender2.text = messagesList.titulo
            holder.tvMessage2.text = messagesList.cuerpo
        }
    }

    override fun getItemCount(): Int {
        return arrayMessages!!.size
    }

}