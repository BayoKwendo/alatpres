package com.alat.ui.activities.enterprise

import adil.dev.lib.materialnumberpicker.dialog.CountyDialog
import adil.dev.lib.materialnumberpicker.dialog.EmployDialogue
import adil.dev.lib.materialnumberpicker.dialog.OrgDialogue
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.alat.BasicUserActivity
import com.alat.HomePage
import com.alat.R
import com.alat.adapters.SimpleArrayListAdapter
import com.alat.adapters.SimpleListAdapter
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.AddClient
import com.alat.interfaces.AddStation
import com.alat.interfaces.GetClients
import com.alat.interfaces.ViewGPsEnteClient
import com.alat.model.TextViewDatePicke
import com.alat.model.rgModel
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.ganfra.materialspinner.MaterialSpinner
import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner
import gr.escsoft.michaelprimez.searchablespinner.interfaces.IStatusListener
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddStation : Fragment() {
    private var btn_submit: Button? = null
    private var mSearchableSpinner1: SearchableSpinner? = null
    private var mSimpleListAdapter: SimpleListAdapter? = null

    private var textInputname: TextInputLayout? = null
    private var textInputlocation: TextInputLayout? = null
    private var textInputemail: TextInputLayout? = null
    private var textInputmssidn: TextInputLayout? = null
    private var mSimpleArrayListAdapter: SimpleArrayListAdapter? = null
    private val mStrings: ArrayList<String> = ArrayList()

    private var mProgress: ProgressDialog? = null

    private var mProgressfetch: ProgressDialog? = null

    private var name: String? = null
    private var location: String? = null

    private var userid: String? = null

    private var postal: String? = null
    private var email: String? = null
    private var mssidn: String? = null
    private var website: String? = null
    private var nature: String? = null
    private var description: String? = null
    private var nameRG: String? = null
    private var nature_RG: String? = null
    private val ITEMS1 = arrayOf("Open", "Private")
    var selectedItem: String? = null
    private var promptPopUpView: PromptPopUpView? = null
    val catList: ArrayList<String> = ArrayList()
    private var selecteditem3: String? = null
    var pref: SharedPreferences? = null
    private var fullname: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.activity_add_station, container, false)
        pref =
            activity!!.getSharedPreferences("MyPref", 0) // 0 - for private mode
        userid = pref!!.getString("userid", null)


        fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

        mssidn = pref!!.getString("mssdn", null)


//       Toast.makeText(activity, "WOW"  +   userid, Toast.LENGTH_SHORT).show()

        textInputlocation  = view.findViewById(R.id.mlocation)

        textInputname  = view.findViewById(R.id.rg_name)

        mProgress = ProgressDialog(activity)
        mProgress!!.setMessage("Creating Station....")
        mProgress!!.setCancelable(true)

        mProgressfetch = ProgressDialog(activity)
        mProgressfetch!!.setMessage("Fetching Clients....")
        mProgressfetch!!.setCancelable(true)

        mSimpleListAdapter = SimpleListAdapter(activity, mStrings)
        mSimpleArrayListAdapter = SimpleArrayListAdapter(activity, mStrings)

        mSearchableSpinner1 = view.findViewById<View>(R.id.SearchableSpinner1) as SearchableSpinner

        mSearchableSpinner1!!.setAdapter(mSimpleListAdapter)
        mSearchableSpinner1!!.setOnItemSelectedListener(mOnItemSelectedListener1)
        mSearchableSpinner1!!.setStatusListener(object : IStatusListener {
            override fun spinnerIsOpening() {

            }

            override fun spinnerIsClosing() {}
        })
        //Adapters
        val adapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, ITEMS1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        btn_submit = view.findViewById(R.id.msubmit)

        btn_submit!!.setOnClickListener {
//            dialogue()
//            promptPopUpView?.changeStatus(2, "Client Station has been added successfully")
            if (!checkContactError()) return@setOnClickListener
            else {
                createStation()
                mProgress!!.show()
                btn_submit!!.setText("Submitting....")

            }
        }
        getStudent()

        return view
    }






    private val mOnItemSelectedListener1: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(view: View?, position: Int, id: Long) {
            if (position > 0) {
                selecteditem3 = mSimpleListAdapter!!.getItem(position).toString()
            }

           // Toast.makeText(activity, "VALUE" + selecteditem3, Toast.LENGTH_LONG).show();


        }

        override fun onNothingSelected() {
            Toast.makeText(activity, "Nothing Selected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun registerVar() {
        name = textInputname!!.editText?.text.toString().trim { it <= ' ' }
        location = textInputlocation!!.editText?.text.toString()?.trim { it <= ' ' }

    }


    private fun showKeyBoard() {
        val imm =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun checkContactError(): Boolean {

        registerVar()
       if (selecteditem3 == null) {
            Toast.makeText(
                activity,
                "Please  name of the client",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (Utils.checkIfEmptyString(name)) {
            textInputname!!.error = "Station Name is required"
            textInputname!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputname!!.error = null

        if (Utils.checkIfEmptyString(location)) {
            textInputlocation!!.error = " Station Location is mandatory"
            textInputlocation!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputlocation!!.error = null

        return true
    }


    private fun getStudent() {
        mProgressfetch!!.show()


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
        val params: java.util.HashMap<String, String> = java.util.HashMap()

        params["userid"] = userid!!
        val api: ViewGPsEnteClient = retrofit.create(ViewGPsEnteClient::class.java)
        val call: Call<ResponseBody>? = api.viewRG(params)

        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()
                if (response.isSuccessful) {
                    val remoteResponse = response.body()!!.string()
                    parseLogiData(remoteResponse)

                } else {
                    Log.d("bayo", response.errorBody()!!.string())

                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                //   btn.text = "Proceed"
                Log.i("onEmptyResponse", "" + t) //

                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //mProgress?.dismiss()
            }
        })
    }

    private fun parseLogiData(jsonresponse: String) {
        try {
            val o = JSONObject(jsonresponse)
            val array: JSONArray = o.getJSONArray("records")
            //  val array: JSONArray = JSONArray(jsonresponse)
            mProgressfetch!!.dismiss()
            val jsonarray = JSONArray(array.toString())
            for (i in 0 until jsonarray.length()) {
                val jsonobject: JSONObject = jsonarray.getJSONObject(i)
                mStrings.add(jsonobject.getString("name"))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }


    private fun createStation() {
        registerVar()

        // Toast.makeText(this@Enterprise, "VALUE" + selectedItem2, Toast.LENGTH_LONG).show();

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
            .addConverterFactory(ScalarsConverterFactory.create())

            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val params: HashMap<String, String> = HashMap()

        params["client_name"] = selecteditem3!!
        params["location"] = location!!
        params["userid"] = userid!!
        params["station_name"] = name!!
        params["fullname"] = fullname!!
        params["mssidn"] = mssidn!!


        val api: AddStation = retrofit.create(AddStation::class.java)
        val call: Call<ResponseBody> = api.createStation(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());


                if (response.isSuccessful) {
                    var remoteResponse = response.body()!!.string()
                    Log.d("Responsess", remoteResponse);

                    try {
                        var jsonObject = JSONObject(remoteResponse)
                        if (jsonObject.getString("status") == "true") {
                            dialogue()
                            mProgress?.dismiss()
                            btn_submit!!.setText("Submit")
                            promptPopUpView?.changeStatus(2, "Station has been added successfully")
                        } else if (jsonObject.getString("status") == "false") {
                            dialogue_error();
                            mProgress?.dismiss()
                            btn_submit!!.setText("Submit")
                            promptPopUpView?.changeStatus(1, "Unsuccessfully")
                        } else if (jsonObject.getString("status") == "normal"){
                            dialogue_error();
                            mProgress?.dismiss()
                            btn_submit!!.setText("Submit")
                            promptPopUpView?.changeStatus(1, "Station name is already taken. Please use a different name for the client.")
                        }
                        else {
                            dialogue_error();
                            btn_submit!!.setText("Submit")
                            promptPopUpView?.changeStatus(1, "Something went wrong")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {
                    mProgress?.dismiss()
                    dialogue_error()
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

    private fun dialogue() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)
            .setPositiveButton(
                "ok"
            ) { dialog, _ ->
                dialog.dismiss()
                val i = Intent(activity, HomePage::class.java)
                startActivity(i)
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    private fun dialogue_error() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)
            .setPositiveButton(
                "Ok"
            ) { dialog, _ ->
                dialog.dismiss()
            }


            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    fun onBackPressed() {
//        val intent = Intent(this@AddClientActivitity, BasicUserActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
//        finish()
    }
}

