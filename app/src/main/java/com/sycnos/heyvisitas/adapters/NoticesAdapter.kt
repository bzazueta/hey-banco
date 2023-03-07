package com.sycnos.heyvisitas.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sycnos.heyvisitas.NoticesActivity
import com.sycnos.heyvisitas.R
import com.sycnos.heyvisitas.data.models.Notices

class NoticesAdapter(noticesActivity: NoticesActivity, private val arrayNotices: ArrayList<Notices>) : RecyclerView.Adapter<NoticesAdapter.MyViewHolder>() {

    var x : String = ""

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

       // var lblTotalLista: TextView
       // var lblVerMasInfoLista: TextView
        //var lblVerMasInfoLista: TextView


        var lblNotice: TextView = view.findViewById(R.id.tvMessage)
           // lblTotalLista = view.findViewById<TextView>(R.id.lblTotalLista)
           // lblVerMasInfoLista = view.findViewById<TextView>(R.id.lblVerMasInfoLista)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_notices, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var noticesList : Notices = arrayNotices!![position]
        holder.lblNotice.setText(noticesList.titulo)
        holder.lblNotice.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        //holder.lblTotalLista.setText(infoList.getTotal())


//        holder.lblVerMasInfoLista.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                final InfoList infoList = registrosListFiltered.get(position);
////                Intent intent = new Intent(context, DetalleAccesosActivity.class);
////                intent.putExtra("Acceso a " , infoList.getCalle());
////                intent.putExtra("Visita a " , infoList.getTotal());
////
////                context.startActivity(intent);
////                Toast.makeText(context.getApplicationContext(), "info " + infoList.getTotal(), Toast.LENGTH_LONG).show();
//
//            }
//        });
    }

    override fun getItemCount(): Int {
        return arrayNotices!!.size
    }
}