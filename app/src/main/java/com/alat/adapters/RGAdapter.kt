package com.alat.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.provider.Settings.Secure
import android.provider.Settings.Secure.getString
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


class RGAdapter(
    private val context: Context,
    private val contactList: List<rgModel>,
    private val listener: ContactsAdapterListener

) : RecyclerView.Adapter<RGAdapter.MyViewHolder>(),
    Filterable {

    private val deviceID: String = getString(
        context.contentResolver,
        Secure.ANDROID_ID
    )


    private var contactListFiltered: List<rgModel>

    inner class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var name: TextView
        var ale: TextView
        var alat_no: TextView
        var thumbnail: ImageView? = null

        init {
            name = view.findViewById(R.id.name)
            ale = view.findViewById(R.id.alerts)
            alat_no= view.findViewById(R.id.fabCounter)

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
        holder.name.text = contact.group_name + "\tRG"
        holder.ale.text = "[\t" + contact.alerts.toString() + "\t]"
        holder.alat_no.visibility= View.GONE

        if  (contact.alerts!! > contact.alats_no!!){

            if(contact.device_id == deviceID ) {

                holder.alat_no.visibility= View.VISIBLE
                holder.alat_no.text = (contact.alerts!! - contact.alats_no!!).toString()
            }

        }


//
//        holder.itemView.setOnClickListener {
//            holder.itemView.setBackgroundColor(Color.parseColor("#000000"));
//
//        }
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
                        if (row.group_name!!.toLowerCase().contains(charString.toLowerCase())) {
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