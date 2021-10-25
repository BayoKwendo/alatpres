package com.alat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.alat.R
import com.alat.model.DataModel
import java.util.*

class CustomAdapter(private val dataSet: ArrayList<*>, var mContext: Context) :
    ArrayAdapter<Any?>(mContext, R.layout.row_item, dataSet) {

    // View lookup cache
    private class ViewHolder {
        var txtName: TextView? = null
        var checkBox: CheckBox? = null


    }


    override fun getCount(): Int {
        return dataSet.size
    }

    override fun getItem(position: Int): DataModel? {
        return dataSet[position] as DataModel
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        var convertView = convertView
        val viewHolder: ViewHolder
        val result: View
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item, parent, false)
            viewHolder.txtName =
                convertView!!.findViewById<View>(R.id.txtName) as TextView
            viewHolder.checkBox =
                convertView.findViewById<View>(R.id.checkBox) as CheckBox
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }
        val item = getItem(position)
        viewHolder.txtName!!.text = item!!.group_name
        viewHolder.checkBox!!.isChecked = item.checked
        return result
    }



}