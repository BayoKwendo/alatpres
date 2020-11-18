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

class RespProvAdapter(
    private val context: Context,
    private val contactList: List<rgModel>,
    private val listener: ContactsAdapterListener
) : RecyclerView.Adapter<RespProvAdapter.MyViewHolder>(),
    Filterable {
    private var contactListFiltered: List<rgModel>
    var itemView: View? = null
    inner class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var response_id: TextView

        var name: TextView

        var nature_response: TextView

        var county: TextView

        var town: TextView


        init {

            response_id = view.findViewById(R.id.provider_id)


            name = view.findViewById(R.id.fullname)

            nature_response = view.findViewById(R.id.nature_response)

            county = view.findViewById(R.id.county)

            town = view.findViewById(R.id.town)

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
            .inflate(R.layout.item_response_provider, parent, false)

        return MyViewHolder(itemView!!)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int) {
        val contact = contactListFiltered[position]


        holder.response_id.text = contact.userid+"(id)"
        holder.name.text ="NAME: " +contact.firstname +"\t"+ contact.lastname
        holder.nature_response.text = contact.nature_response
        holder.county.text = "COUNTY: " +contact.county
        holder.town.text = "TOWN: " +contact.town

    }

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
                        if (
                            row.nature_response!!.toLowerCase().contains(charString.toLowerCase())||
                            row.userid!!.toLowerCase().contains(charString.toLowerCase())||
                            row.firstname!!.toLowerCase().contains(charString.toLowerCase())||
                            row.lastname!!.toLowerCase().contains(charString.toLowerCase())||
                            row.county!!.toLowerCase().contains(charString.toLowerCase())||
                            row.town!!.toLowerCase().contains(charString.toLowerCase())

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