package com.sycnos.heyvisitas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.sycnos.heyvisitas.databinding.ListGroupBinding
import com.sycnos.heyvisitas.databinding.ListItemBinding


class CustomExpandableListAdapter internal constructor(
    private val context: Context,
    private val titleList: List<String>,
    private val dataList: HashMap<String, List<String>>
) : BaseExpandableListAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var groupBinding: ListGroupBinding
    private lateinit var itemBinding: ListItemBinding

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
            holder.spHours = itemBinding.spHours
            convertView.tag = holder
        } else {
            holder = convertView.tag as ItemViewHolder
        }
        val expandedListText = getChild(listPosition, expandedListPosition) as String
        holder.label!!.text = expandedListText
        holder.btnAdd!!.setOnClickListener(View.OnClickListener {

            val spnHour = holder.spHours!!.selectedItem.toString()
            Toast.makeText(context,spnHour,Toast.LENGTH_SHORT).show()
            spnHour.toString()
            val expandedListText = getChild(listPosition, expandedListPosition) as String
            expandedListText.toString()
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
        internal var spHours : Spinner? = null
    }

    inner class GroupViewHolder {
        internal var label: TextView? = null
        internal var btnAdd: Button? = null
    }
}