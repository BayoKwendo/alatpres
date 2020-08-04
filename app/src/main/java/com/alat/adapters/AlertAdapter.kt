package com.alat.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alat.R
import com.alat.model.rgModel
import java.util.*

class AlertAdapter(
    private val context: Context,
    private val contactList: List<rgModel>,
    private val listener: ContactsAdapterListener
) : RecyclerView.Adapter<AlertAdapter.MyViewHolder>(),
    Filterable {
    private var contactListFiltered: List<rgModel>
    var itemView: View? = null
    inner class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var name: TextView
        var id: TextView
        var thumbnail: ImageView? = null

        init {

            name = view.findViewById(R.id.name)

            id = view.findViewById(R.id.id)

            view.setOnClickListener { // send selected contact in callback

                listener.onContactSelected(contactListFiltered[adapterPosition])

            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
         itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.alert_row_item, parent, false)

        return MyViewHolder(itemView!!)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int) {
        val contact = contactListFiltered[position]

        if(contact.rl == "Level 3") {
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.error))
        }else if (contact.rl == "Level 2"){
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        }else if (contact.rl == "Level 1"){
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.yellow))
        }else if (contact.rl == "Neutralized"){
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.accentGreen))
        }
        holder.name.text = contact.alert_name
        holder.id.text = contact.id.toString() }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                contactListFiltered = if (charString.isEmpty()) {
                    contactList


                } else {
                    val filteredList: MutableList<rgModel> =
                        ArrayList()
                    for (row in contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.alert_type!!.toLowerCase().contains(charString.toLowerCase()) ||
                            row.alert_name!!.toLowerCase().contains(charString.toLowerCase()) ||
                            row.id!!.toLowerCase().contains(charString.toLowerCase())
                        ) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults =
                    FilterResults()
                filterResults.values = contactListFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                contactListFiltered = filterResults.values as ArrayList<rgModel>
                notifyDataSetChanged()
            }
        }
    }

    interface ContactsAdapterListener {
        fun onContactSelected(contact: rgModel?)
    }

    init {
        contactListFiltered = contactList
    }
}