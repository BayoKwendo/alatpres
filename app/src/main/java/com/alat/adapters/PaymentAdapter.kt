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

class PaymentAdapter(
    private val context: Context,
    private val contactList: List<rgModel>,
    private val listener: ContactsAdapterListener
) : RecyclerView.Adapter<PaymentAdapter.MyViewHolder>(),
    Filterable {
    private var contactListFiltered: List<rgModel>
    var itemView: View? = null
    inner class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var transactionid: TextView
        var amount: TextView
        var mphone: TextView
        var mdate: TextView
        var thumbnail: ImageView? = null

        init {

            transactionid = view.findViewById(R.id.transactionid)

            amount = view.findViewById(R.id.amount)

            mphone = view.findViewById(R.id.phone)

            mdate = view.findViewById(R.id.date)

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
            .inflate(R.layout.payment_row_item, parent, false)

        return MyViewHolder(itemView!!)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int) {
        val contact = contactListFiltered[position]

        holder.mphone.text = contact.phoneNumber
        holder.amount.text = contact.TransAmount
        holder.mdate.text = contact.TransDate
        holder.transactionid.text = contact.TransID

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
                        if (row.TransDate!!.toLowerCase().contains(charString.toLowerCase()) ||
                            row.phoneNumber!!.toLowerCase().contains(charString.toLowerCase()) ||
                            row.TransID!!.toLowerCase().contains(charString.toLowerCase())
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