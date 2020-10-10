package com.alat.ui.fragments

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.PromptPopUpView
import com.alat.ui.activities.*

class ManageAlert : Fragment() {

    private var toolbar : Toolbar? = null
    var card: CardView? = null
    var card2: CardView? = null
    var card3: CardView? = null
    var card4: CardView? = null

    var pref: SharedPreferences? = null

    private var account: String? = null

    private var userid: String? = null
    private var promptPopUpView: PromptPopUpView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.activity_alertmanage, container, false)

        card = view.findViewById(R.id.cardactive)
        card4 = view.findViewById(R.id.cardvv)

        card3 = view.findViewById(R.id.card_ucrs)
        card2 = view.findViewById(R.id.neutralized)





        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref =
            activity!!.getSharedPreferences("MyPref", 0) // 0 - for private mode


        account = pref!!.getString("account_status", null)
        userid = pref!!.getString("userid", null)

        if (account == "0"){
            dialogue_error()
            promptPopUpView?.changeStatus(1, "You're not allowed to manage alerts! \n kindly upgrade to Pro Account")


        }else if (account =="1") {
            card!!.setOnClickListener {
                startActivity(Intent(activity!!, ActiveAlerts::class.java))
            }
            card2!!.setOnClickListener {
                startActivity(Intent(activity!!, NeutralizeAlerts::class.java))
            }
            card3!!.setOnClickListener {
                startActivity(Intent(activity!!, DangerAlerts::class.java))
            }
            card4!!.setOnClickListener {
                startActivity(Intent(activity!!, ElevateAlert::class.java))
            }
        }

        //you can set the title for your toolbar here for different fragments different title
    }

    private fun dialogue_error() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)
            .setPositiveButton("Back to Home") { _: DialogInterface?, _: Int ->
                startActivity(Intent(activity, HomePage::class.java))

            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

}