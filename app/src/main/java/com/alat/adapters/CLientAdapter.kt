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


class CLientAdapter(
    private val context: Context,
    private val contactList: List<rgModel>,
    private val listener: ContactsAdapterListener
) : RecyclerView.Adapter<CLientAdapter.MyViewHolder>(),
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
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()
            mAdView.loadAd(adRequest)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

//        var viewHolder: RGAdapter.MyViewHolder? = null
//        val inflater =
//            LayoutInflater.from(parent.context)
//        when (viewType) {
//            1 -> {
//                val v: View = inflater.inflate(R.layout.user_row_item, parent, false)
//                viewHolder = MyViewHolder(v)
//            }
//            2 -> {
////                val v: View = inflater.inflate(R.layout.list_item_admob, parent, false)
////                viewHolder = ViewHolderAdMob(v)
//            }
//        }
//        return viewHolder!!

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
                  holder.name.text = contact.name +"\tRG"
                holder.ale.text = "[\t" + contact.alerts.toString() + "\t]"
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

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.name!!.toLowerCase().contains(charString.toLowerCase()) ) {
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