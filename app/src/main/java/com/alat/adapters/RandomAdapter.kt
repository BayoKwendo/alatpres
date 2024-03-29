package com.alat.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alat.R
import com.alat.model.rgModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.util.*


class RandomAdapter(
    private val context: Context,
    private val contactList: List<rgModel>,
    private val listener: ContactsAdapterListener
) : RecyclerView.Adapter<RandomAdapter.MyViewHolder>(),
    Filterable {
    private var contactListFiltered: List<rgModel>

    inner class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var name: TextView
        var ale: TextView
        var thumbnail: ImageView? = null

        init {
            name = view.findViewById(R.id.name)
            ale = view.findViewById(R.id.alerts)

            view.setOnClickListener { // send selected contact in callback
                listener.onContactSelected(contactListFiltered[adapterPosition])
            }
        }
    }

    class ViewHolderAdMob(view: View) :
        RecyclerView.ViewHolder(view) {


        var mAdView: AdView

        init {
            mAdView = view.findViewById<View>(R.id.adView) as AdView
            val adRequest: AdRequest = AdRequest.Builder()
                .build()
            mAdView.loadAd(adRequest)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_row_item, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
//
//        when (holder.itemViewType) {
//            1 -> {
                val contact = contactListFiltered[position]
                holder.name.text = contact.group_name +"\tRG"
                holder.ale.text = "[\t RG\t" + contact.rg_id.toString() + "\t]"
            //}  2 -> {
//            }

//                  }
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }
//    override fun getItemViewType(position: Int): Int {
//        return if (position % 5 == 0) {
//            AD_TYPE
//        } else {
//            CONTENT_TYPE
//        }
//    }
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
                        if (row.group_name!!.toLowerCase().contains(charString.toLowerCase()) || row.id!!.toLowerCase().contains(charString.toLowerCase()) ) {
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