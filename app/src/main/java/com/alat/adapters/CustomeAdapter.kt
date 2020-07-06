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

class CustomeAdapter(
    private val context: Context,
    private val contactList: List<rgModel>,
    private val listener: ContactsAdapterListener
) : RecyclerView.Adapter<CustomeAdapter.MyViewHolder>(),
    Filterable {
    private var contactListFiltered: List<rgModel>
    var itemView: View? = null
    inner class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var name: TextView

        var naturerg: TextView
        var checkBox: CheckBox? = null


        var thumbnail: ImageView? = null

        init {

            name = view.findViewById(R.id.txtName)


            naturerg = view.findViewById(R.id.text2)
            checkBox = view.findViewById(R.id.checkBox)

            view.setOnClickListener { // send selected contact in callback

                listener.onContactSelected(contactListFiltered[adapterPosition])
                checkBox!!.setChecked(true);
//                checkBox!!.setEnabled(false);

            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
         itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item, parent, false)

        return MyViewHolder(itemView!!)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int) {
        val contact = contactListFiltered[position]

        holder.checkBox!!.isChecked = contact.checked!!

        holder.name.text = contact.firstname + "\t"+ contact.lastname
        holder.naturerg.text = contact.nature_response }

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
                        if (row.firstname!!.toLowerCase().contains(charString.toLowerCase()) || row.lastname!!.toLowerCase().contains(charString.toLowerCase())
                            || row.nature_response!!.contains(charString.toLowerCase())) {
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