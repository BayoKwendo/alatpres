package com.alat.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.alat.R
import com.alat.ui.activities.ActiveAlerts
import com.alat.ui.activities.JoinGlobal
import com.alat.ui.activities.NeutralizeAlerts

class ManageAlert : Fragment() {

    private var toolbar : Toolbar? = null
    var card: CardView? = null
    var card2: CardView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.activity_alertmanage, container, false)
        card = view.findViewById(R.id.cardactive)

        card!!.setOnClickListener {
            startActivity(Intent(activity!!, ActiveAlerts::class.java))
        }

        card2 = view.findViewById(R.id.neutralized)

        card2!!.setOnClickListener {
            startActivity(Intent(activity!!, NeutralizeAlerts::class.java))
        }
        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different title
    }


}