package com.alat.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.alat.R
import com.alat.model.rgModel
import java.util.*

class FriendAdapter(
    private val context: Context,
    private val contactList: List<rgModel>,
    private val listener: ContactsAdapterListener
) : RecyclerView.Adapter<FriendAdapter.MyViewHolder>(),
    Filterable {
    private var contactListFiltered: List<rgModel>
    var itemView: View? = null
    inner class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var name: TextView
        var rg_mode: TextView

       // var id: TextView
        var thumbnail: ImageView? = null

        init {

            name = view.findViewById(R.id.tv_topic_name)
            rg_mode = view.findViewById(R.id.tv_topic_starter)

          //  id = view.findViewById(R.id.id)

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
            .inflate(R.layout.item_request_row, parent, false)

        return MyViewHolder(itemView!!)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int) {
        val contact = contactListFiltered[position]

        holder.name.text = contact.fullname + "\nWants to Join the group you created of ID\t" + contact.rg_id
        holder.rg_mode.text = "From\t" + contact.fullname }

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
                        if (row.alert_type!!.toLowerCase().contains(charString.toLowerCase())||
                            row.alert_name!!.toLowerCase().contains(charString.toLowerCase())) {
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