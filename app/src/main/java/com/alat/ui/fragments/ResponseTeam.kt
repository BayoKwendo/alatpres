package com.alat.ui.fragments

import adil.dev.lib.materialnumberpicker.dialog.LevelDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.alat.HomePage
import com.alat.R
import com.alat.adapters.AlertAdapter
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.ALertUpgrade
import com.alat.interfaces.CheckRGName
import com.alat.interfaces.CheckStatus
import com.alat.model.rgModel
import com.alat.ui.activities.ElevateAlert
import com.alat.ui.activities.GroupID
import com.alat.ui.activities.ResponseProviders
import com.rengwuxian.materialedittext.MaterialEditText
import dmax.dialog.SpotsDialog
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class ResponseTeam : Fragment() {


    var response_group: String? = null

    private val TAG = ElevateAlert::class.java.simpleName
    private var recyclerView: RecyclerView? = null
    private var contactList: MutableList<rgModel>? = null
    private var mAdapter: AlertAdapter? = null
    private var searchView: SearchView? = null
    private var mProgressLayout: LinearLayout? = null

    private var promptPopUpView: PromptPopUpView? = null
    var updateFNamee: MaterialEditText? = null

    var waitingDialog: android.app.AlertDialog? = null

    var pref: SharedPreferences? = null


    //  var fname: String? = null
    var user: String? = null
    var updateFName: String? = null


    private var btnResetPassword: Button? = null
    private var btnBack: Button? = null
    var errorNull: TextView? = null
    private var mProgress: ProgressDialog? = null
    var MYCODE = 1000
    var alert: android.app.AlertDialog? = null

    var alert_id: String? = null

    var mToolbar: Toolbar? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.sample, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updates()

        //you can set the title for your toolbar here for different fragments different title
    }

    private fun showKeyBoard() {
        val imm =
           activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
    fun updates() {


        val alertDialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(activity)
        alertDialog.setTitle("Find Response Team")

        val inflater: LayoutInflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val layout_pwd: View =
            inflater.inflate(R.layout.layout_find_team, null)
        alertDialog.setView(layout_pwd)
        alert = alertDialog.create()
        updateFNamee = layout_pwd.findViewById<View>(R.id.etName) as MaterialEditText
        updateFNamee!!.requestFocus()
        showKeyBoard()

        val updateButton: Button =
            layout_pwd.findViewById<View>(R.id.update) as Button
        updateButton.setOnClickListener(View.OnClickListener {
            waitingDialog =
                SpotsDialog.Builder().setContext(activity).build()


            updateFName = updateFNamee!!.text.toString()

            updateFNamee!!.clearFocus()

            if (Utils.checkIfEmptyString(updateFName)) {
                updateFNamee!!.error = "Team name/location is Mandatory"
                updateFNamee!!.requestFocus()
            } else {

               // waitingDialog!!.show()
                val i = Intent(activity, ResponseProviders::class.java)
                i.putExtra("groupSelect", updateFName)
                startActivity(i)

            }


        })
        val dismissButton: Button =
            layout_pwd.findViewById<View>(R.id.cancel) as Button
        dismissButton.setOnClickListener(View.OnClickListener {
            alert!!.dismiss()

            startActivity(Intent(activity, HomePage::class.java))

        })
        alertDialog.setView(layout_pwd)
        alert!!.setCancelable(false)
        alert!!.show()

    }
}