package com.sycnos.heyvisitas

import android.os.Bundle
import android.widget.ExpandableListAdapter
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.sycnos.heyvisitas.databinding.ActivityPendingVisitsBinding

class PendingVisitsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPendingVisitsBinding

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

            false
        }
    }

}