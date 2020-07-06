package com.alat.ui.activities

import adil.dev.lib.materialnumberpicker.dialog.LevelDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.*
import com.google.android.material.textfield.TextInputEditText
import com.rengwuxian.materialedittext.MaterialEditText
import dmax.dialog.SpotsDialog
import fr.ganfra.materialspinner.MaterialSpinner
import kotlinx.android.synthetic.main.activity_configure.*
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
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class AddMembers : AppCompatActivity() {
    private var configlevel: TextInputEditText? = null

    var frontier: MaterialSpinner? = null


    var spinner2: MaterialSpinner? = null


    var spinner3: MaterialSpinner? = null


    private var mPostBtn: Button? = null
    var pref: SharedPreferences? = null
    var user: String? = null
    var fname: String? = null
    private var fullnames: String? = null

    var alert_id: String? = null
    var alert: android.app.AlertDialog? = null
    var waitingDialog: android.app.AlertDialog? = null


    var selectedItem2: String? = null


    var level_2: String? = null

    var level_3: String? = null



    var rgID: String? = null

    var rg_id: String? = null

    var rg_name: String? = null

    private var mssidns: String? = null

    private var rg_add: String? = null

    var updateFName: String? = null

    var response_id: String? = null
    private var promptPopUpView: PromptPopUpView? = null

    var updateFNamee: MaterialEditText? = null


    var Linear1: LinearLayout? = null
    var Linear2: LinearLayout? = null

    var finished: Button? = null
    private var mProgresst: ProgressDialog? = null

    private var mProgresss: ProgressDialog? = null


    private var mProgress: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure)


//        updates()
        configlevel = findViewById(R.id.level)


        frontier = findViewById(R.id.spinner)



        spinner2 = findViewById(R.id.spinner2)


        spinner3 = findViewById(R.id.spinner3)



        rgID = intent.getStringExtra("rg_id")

      //  rg_name = intent.getStringExtra("rg_name")
       // Toast.makeText(activity,  rg, Toast.LENGTH_SHORT).show()


        mProgresst = ProgressDialog(this)
        mProgresst!!.setMessage("Saving...");
        mProgresst!!.setCancelable(false)


        configlevel!!.setOnClickListener(View.OnClickListener { v: View? ->
            configlevel!!.clearFocus()
            configlevel!!.isFocusable = false
            val dialog = LevelDialog(this)
            dialog.setOnSelectingLevel { value -> configlevel?.setText(value) }
            dialog.show()
        })
        mPostBtn = findViewById(R.id.buttonLogin)


        Linear1 = findViewById(R.id.linear1)
        Linear2 = findViewById(R.id.linear2)
        finished = findViewById(R.id.submit)

        mProgresss = ProgressDialog(this)
        mProgresss!!.setMessage("Loading...");
        mProgresss!!.setCancelable(true)
        mProgresss!!.show()

        getStudent()

        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode
        user = pref!!.getString("userid", null)
        fname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)
        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Processing...")
        mProgress!!.setCancelable(false)
        mPostBtn!!.setOnClickListener(View.OnClickListener {
            val title = configlevel!!.getText().toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(title)) {
                configlevel!!.setError("Alert Level is compulsory")
                return@OnClickListener
            }
            if (!isNetworkAvailable()) {
                internet()
                promptPopUpView?.changeStatus(1, "Connection Error\n\n Check your internet connectivity")

            } else {

             //   postTopic(title)

                submitLevelUpdates()
                mProgresst!!.show()
            }
        })

        finished!!.setOnClickListener(View.OnClickListener {
            val title = configlevel!!.getText().toString().trim { it <= ' ' }
            if (!isNetworkAvailable()) {
                internet()
                promptPopUpView?.changeStatus(1, "Connection Error\n\n Check your internet connectivity")

            } else {


                finalsubmit()
                mProgresst!!.show()
            }
        })

    }




    private fun submitLevelUpdates() {

        rg_add = configlevel?.text.toString().trim();


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
        params["rg_id"] = rgID!!
        params["level"] = rg_add!!

        val api: updateRGL = retrofit.create(updateRGL::class.java)
        val call: Call<ResponseBody> = api.UpdateL(params)

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

                    parseLoginDattass(remoteResponse)
                } else {
                    mProgresst?.dismiss()
                    dialogue_error()
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    mProgresst?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
                mProgress?.dismiss()
            }
        })
    }

    private fun parseLoginDattass(remoteResponse: String) {
        try {
            val jsonObject = JSONObject(remoteResponse)
            if (jsonObject.getString("status") == "true") {

                linear1!!.visibility = View.GONE
                linear2!!.visibility = View.VISIBLE
              mProgresst!!.dismiss()
            }  else{
                mProgresst!!.dismiss()
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Unable!! try again")

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }







    private fun showKeyBoard() {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }


    fun updates() {
        val alertDialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        alertDialog.setTitle("Add Rescue Member")

        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val layout_pwd: View =
            inflater.inflate(R.layout.layout_add_member, null)
        alertDialog.setView(layout_pwd)
        alert = alertDialog.create()
        updateFNamee = layout_pwd.findViewById<View>(R.id.etName) as MaterialEditText
        updateFNamee!!.requestFocus()
        showKeyBoard()

        val updateButton: Button =
            layout_pwd.findViewById<View>(R.id.update) as Button
        updateButton.setOnClickListener(View.OnClickListener {

             waitingDialog =
                SpotsDialog.Builder().setContext(this).build()

             updateFName = updateFNamee!!.getText().toString()

            updateFNamee!!.clearFocus()

            if (Utils.checkIfEmptyString(updateFName)) {
                updateFNamee!!.error = "Members ID is Mandatory"
                updateFNamee!!.requestFocus()
            } else {

                if (!isNetworkAvailable()) {
                    internet()
                    promptPopUpView?.changeStatus(1, "Connection Error\n\n Check your internet connectivity")

                } else {
                     waitingDialog!!.show()
                    getUserDetails()
                }

            }


        } )
        val dismissButton: Button =
            layout_pwd.findViewById<View>(R.id.cancel) as Button
        dismissButton.setOnClickListener(View.OnClickListener { alert!!.dismiss() })
        alertDialog.setView(layout_pwd)
        alert!!.setCancelable(true)
        alert!!.show()

    }

//    leftSpacer?.visibility = View.GONE


    private fun getUserDetails() {

        rg_add = updateFNamee?.text.toString().trim();


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
        params["userid"] = rg_add!!

        val api: GetUserDetails = retrofit.create(GetUserDetails::class.java)
        val call: Call<ResponseBody> = api.getDetails(params)

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

                    parseLoginDatta(remoteResponse)
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

    private fun parseLoginDatta(remoteResponse: String) {
        try {

            val jArray3 = JSONArray(remoteResponse)
            for (i in 0 until jArray3.length()) {
                val jsonObjec: JSONObject = jArray3.getJSONObject(i)
                if (jsonObjec.getString("status") == "true")
                {
                    fullnames =
                        jsonObjec.getString("firstname") + "\t" + jsonObjec.getString("lastname")
                    mssidns =
                        jsonObjec.getString("mssdn")
                    getRGID()
                }
                else if(jsonObjec.getString("status") == "false"){

                    waitingDialog!!.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "User not Registered to AlatPres")
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private fun getRGID() {



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
        params["group_name"] = rg_name!!

        val api: GetRGID = retrofit.create(GetRGID ::class.java)
        val call: Call<ResponseBody> = api.findRGID(params)

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

                    parseLoginDattaS(remoteResponse)
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

    private fun parseLoginDattaS(remoteResponse: String) {
        try {
            val jArray3 = JSONArray(remoteResponse)
            for (i in 0 until jArray3.length()) {
                val jsonObjec: JSONObject = jArray3.getJSONObject(i)
                if (jsonObjec.getString("status") == "true")
                {
                    rg_id =
                        jsonObjec.getString("id")
                    inviteMember()
                } else if(jsonObjec.getString("status") == "false"){
                    waitingDialog!!.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "Something went wrong AlatPres")
                }

            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }





    private fun inviteMember() {

        //RequestBody body = RequestBody.Companion.create(json, JSON)\\\

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
        params["rg_id"] = rg_id!!
        params["userid"] = rg_add!!
        params["fullname"] = fullnames!!
        params["mssidn"] = mssidns!!

        val api: AddRescue = retrofit.create(AddRescue ::class.java)
        val call: Call<ResponseBody> = api.AddR(params)

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
                    parseLoginDataa(remoteResponse)
                } else {
                    waitingDialog!!.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    mProgress?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                dialogue_error()
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //

                waitingDialog!!.dismiss()
            }
        })
    }

    private fun parseLoginDataa(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {

//                create_rg!!.visibility = View.GONE
//                add_member!!.visibility = View.VISIBLE

                dialogue();
                promptPopUpView?.changeStatus(2, "Member added Successfully")
                waitingDialog!!.dismiss()

            }  else if (jsonObject.getString("status") == "false"){
                waitingDialog!!.dismiss()
                dialogue_error();
                promptPopUpView?.changeStatus(1, "User has already been added!!")

            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //Log.d("BAYO", response.code().toString())
                waitingDialog!!.dismiss()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }



    private fun dialogue() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("Add More") { _: DialogInterface?, _: Int ->
                val i =
                    Intent(this, AddMembers::class.java)
                i.putExtra("rg_id", rgID)

                i.putExtra("rg_name", rg_name)


                startActivity(i)
            }
            .setNegativeButton("Close") { _: DialogInterface?, _: Int ->
                alert!!.dismiss()
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show().withCenteredButtons()
    }



    private fun dialogue_error() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("Try Again") { _: DialogInterface?, _: Int ->

            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }



    private fun getStudent() {

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
        params["rg_id"] = rgID!!

        val api: RescueMember = retrofit.create(RescueMember::class.java)
        val call: Call<ResponseBody> = api.RequesrM(params)

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
                    parseLogiData(remoteResponse)
                } else {
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    mProgress?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
            }
        })
    }

    private fun parseLogiData(jsonresponse: String) {
        try {
            val o = JSONObject(jsonresponse)
            val array: JSONArray = o.getJSONArray("records")
            //  val array: JSONArray = JSONArray(jsonresponse)

            val catList: ArrayList<String> = ArrayList()

            val jsonarray = JSONArray(array.toString())

            for (i in 0 until jsonarray.length()) {
                val jsonobject: JSONObject = jsonarray.getJSONObject(i)
                catList.add(jsonobject.getString("userid"))
            }

            val adapter_2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, catList)
            adapter_2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            frontier = findViewById<View>(R.id.spinner) as MaterialSpinner
            frontier?.adapter = adapter_2
            frontier!!.isSelected = false;  // otherwise listener will be called on initialization
            frontier!!.setSelection(0, true)
            frontier?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>?, arg1: View?,
                    arg2: Int, arg3: Long
                ) {
                    if (frontier!!.selectedItem == null) {
                        // Toast.makeText(this@CreateAlert, "Please select an RG", Toast.LENGTH_LONG).show();
                        return
                    } else {
                        selectedItem2 = frontier!!.selectedItem.toString()
                        // Toast.makeText(this@NFCWrite, tv, Toast.LENGTH_LONG).show();
                    }
                    // TODO Auto-generated method stub
                }

                override fun onNothingSelected(arg0: AdapterView<*>?) {
                    // TODO Auto-generated method stub
                }



            }





            mProgresss!!.dismiss()



            spinner2?.adapter = adapter_2
            spinner2!!.isSelected = false;  // otherwise listener will be called on initialization
            spinner2!!.setSelection(0, true)
            spinner2?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>?, arg1: View?,
                    arg2: Int, arg3: Long
                ) {
                    if (spinner2!!.selectedItem == null) {
                        // Toast.makeText(this@CreateAlert, "Please select an RG", Toast.LENGTH_LONG).show();
                        return
                    } else {
                        level_2 = frontier!!.selectedItem.toString()
                        // Toast.makeText(this@NFCWrite, tv, Toast.LENGTH_LONG).show();
                    }
                    // TODO Auto-generated method stub
                }

                override fun onNothingSelected(arg0: AdapterView<*>?) {
                    // TODO Auto-generated method stub
                }

            }



            spinner3?.adapter = adapter_2
            spinner3!!.isSelected = false;  // otherwise listener will be called on initialization
            spinner3!!.setSelection(0, true)
            spinner3?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>?, arg1: View?,
                    arg2: Int, arg3: Long
                ) {
                    if (spinner3!!.selectedItem == null) {
                        // Toast.makeText(this@CreateAlert, "Please select an RG", Toast.LENGTH_LONG).show();
                        return
                    } else {
                        level_3 = frontier!!.selectedItem.toString()
                        // Toast.makeText(this@NFCWrite, tv, Toast.LENGTH_LONG).show();
                    }
                    // TODO Auto-generated method stub
                }

                override fun onNothingSelected(arg0: AdapterView<*>?) {
                    // TODO Auto-generated method stub
                }
            }



        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }

    private fun finalsubmit() {

      //  rg_add = configlevel?.text.toString().trim();


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
        params["rg_id"] = rgID!!
        params["userid"] = selectedItem2!!

        val api: finalSubmit = retrofit.create(finalSubmit::class.java)
        val call: Call<ResponseBody> = api.finalS(params)

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

                    parseLoginDattasss(remoteResponse)
                } else {
                    mProgresst?.dismiss()
                    dialogue_error()
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    mProgresst?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
                mProgress?.dismiss()
            }
        })
    }

    private fun parseLoginDattasss(remoteResponse: String) {
        try {
            val jsonObject = JSONObject(remoteResponse)
            if (jsonObject.getString("status") == "true") {

                dialoguesss();
                promptPopUpView?.changeStatus(2, "Response Group was Created Successful!")
                mProgresst!!.dismiss()

            }  else{
                mProgresst!!.dismiss()
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Unable!! try again")

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun dialoguesss() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("Exit") { _: DialogInterface?, _: Int ->
                startActivity(Intent(this@AddMembers, HomePage::class.java))

            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show().withCenteredButtons()
    }





    private fun isNetworkAvailable(): Boolean {
        // Using ConnectivityManager to check for Network Connection
        val connectivityManager = (this
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val activeNetworkInfo = connectivityManager
            .activeNetworkInfo
        return activeNetworkInfo != null
    }

    private fun internet() {
        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)

            .setPositiveButton(
                "Retry"
            ) { dialog, _ -> dialog.dismiss()
                recreate()
            }

            .setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                dialog.dismiss()
            }

            .setCancelable(false)
            .setView(promptPopUpView)
            .show().withCenteredButtons()
    }


    private fun AlertDialog.withCenteredButtons() {
        val positive = getButton(AlertDialog.BUTTON_POSITIVE)
        val negative = getButton(AlertDialog.BUTTON_NEGATIVE)

        //Disable the material spacer view in case there is one
        val parent = positive.parent as? LinearLayout
        parent?.gravity = Gravity.CENTER_HORIZONTAL
        val leftSpacer = parent?.getChildAt(1)
        leftSpacer?.visibility = View.GONE

        //Force the default buttons to center
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.weight = 1f
        layoutParams.gravity = Gravity.CENTER

        positive.layoutParams = layoutParams
        negative.layoutParams = layoutParams
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {

            android.R.id.home -> {
               finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                // write your code here
                BackAlerts()
            }
        }
        return true
    }


    fun BackAlerts() {
        AlertDialog.Builder(this)
            .setMessage("Your are leaving?\n Response group was created but configuration are incomplete")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, id ->
                startActivity(Intent(this@AddMembers, HomePage::class.java))
            }
            .setNegativeButton("No", null)
            .show().withCenteredButtons()
    }
}