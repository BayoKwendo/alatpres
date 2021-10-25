package com.alat.ui.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import com.alat.interfaces.CheckRGName
import com.alat.interfaces.CheckRGNameEnter
import com.alat.interfaces.countRGJOIN
import com.alat.model.rgModel
import com.alat.ui.activities.ElevateAlert
import com.alat.ui.activities.GroupID
import com.alat.ui.activities.GroupIDEnter
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

class JoinRGEnter : Fragment() {


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


    var pref: SharedPreferences? = null

    private var account: String? = null
    private var userid: String? = null
    private var roleID: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.sample, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref =
            activity!!.getSharedPreferences("MyPref", 0) // 0 - for private mode
        account = pref!!.getString("account_status", null)
        userid = pref!!.getString("userid", null)
        roleID = pref!!.getString("role", null)
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
        alertDialog.setTitle("Find Group")
        val inflater: LayoutInflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout_pwd: View =
            inflater.inflate(R.layout.layout_find_rg, null)
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
            updateFName = updateFNamee!!.getText().toString()
            updateFNamee!!.clearFocus()
            if (Utils.checkIfEmptyString(updateFName)) {
                updateFNamee!!.error = "Group ID is Mandatory"
                updateFNamee!!.requestFocus()
            } else {
                 waitingDialog!!.show()
                 checkStatuss()
            }
        })
        val dismissButton: Button =
            layout_pwd.findViewById<View>(R.id.cancel) as Button
        dismissButton.setOnClickListener(View.OnClickListener {
            alert!!.dismiss()
            hideKeyboardFrom()
            startActivity(Intent(activity, HomePage::class.java))

        })
        alertDialog.setView(layout_pwd)
        alert!!.setCancelable(false)
        alert!!.show()

    }


    private fun checkStatuss() {

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor) //.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES) // write timeout
            .readTimeout(2, TimeUnit.MINUTES) // read timeout
            .addNetworkInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                    val request: Request =
                        chain.request().newBuilder() // .addHeader(Constant.Header, authToken)
                            .build()
                    return chain.proceed(request)
                }
            }).build()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .client(client) // This line is important
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val params: HashMap<String, String> = HashMap()

        params["rg_id"] = updateFName!!

        val api: CheckRGNameEnter = retrofit.create(CheckRGNameEnter::class.java)

        val call: Call<ResponseBody> = api.CheckSta(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {
                    val remoteResponse = response.body()!!.string()
                    Log.d("test", remoteResponse)

                    if (response.code().toString() == "200") {
                        parseLoginDatasss(remoteResponse)
                    }
                } else {
                    mProgress?.dismiss()
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    mProgress?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
                mProgress?.dismiss()
            }
        })
    }

    private fun parseLoginDatasss(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {
                waitingDialog!!.dismiss()
                hideKeyboardFrom()
                val i = Intent(activity, GroupIDEnter::class.java)
                i.putExtra("groupSelect", updateFName)
                i.putExtra("grouptype", "bayo")


                startActivity(i)
            } else if (jsonObject.getString("status") == "false") {

                waitingDialog!!.dismiss()

                val i = Intent(activity, GroupIDEnter::class.java)
                i.putExtra("groupSelect", updateFName)

                startActivity(i)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun dialogue_error() {

        promptPopUpView = PromptPopUpView(activity!!)

        AlertDialog.Builder(activity!!)
            .setPositiveButton("Try Again") { _: DialogInterface?, _: Int ->
                //hideKeyboardFrom()
                hideKeyboardFrom()
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }


    fun hideKeyboardFrom() {
        val imm =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

    }

}