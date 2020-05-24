package com.alat.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.alat.R

class Profile : Fragment() {

    private var toolbar : Toolbar? = null
    var fullname: TextView? = null
    var userid: TextView? = null
    var email: TextView? = null
    var county: TextView? = null
    var dob: TextView? = null
    var phone: TextView? = null
    var idNo: TextView? = null

    var pref: SharedPreferences? = null


    var fname: String? = null
    var user: String? = null
    var emailuser: String? = null
    var county_name: String? = null
    var dob_user: String? = null
    var mssidn: String? = null
    var id: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.activity_profile, container, false)

        fullname = view.findViewById(R.id.textView)
        userid = view.findViewById(R.id.textView2)
        idNo = view.findViewById(R.id.textView21)
        phone = view.findViewById(R.id.textView7)
        email = view.findViewById(R.id.textView9)
        dob = view.findViewById(R.id.textView15)
        county = view.findViewById(R.id.textView11)

        pref =
            activity!!.getSharedPreferences("MyPref", 0) // 0 - for private mode
        fname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)
        mssidn = pref!!.getString("mssdn", null)
        user = pref!!.getString("userid", null)
        id = pref!!.getString("idNo", null)

        emailuser = pref!!.getString("email", null) + "\t" + pref!!.getString("lname", null)
        county_name = pref!!.getString("county", null)
        dob_user = pref!!.getString("dob", null)



        fullname!!.text = fname

        idNo!!.text = id

        phone!!.text = mssidn

        email!!.text = emailuser

        dob!!.text = dob_user

        county!!.text = county_name

        userid!!.text = "AlatPres ID\t$user"



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different title
    }


}