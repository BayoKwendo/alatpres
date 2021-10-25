package com.alat.ui.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.*
import com.alat.model.PreferenceModel
import com.alat.ui.activities.level_1
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputLayout
import com.rengwuxian.materialedittext.MaterialEditText
import dmax.dialog.SpotsDialog
import fr.ganfra.materialspinner.MaterialSpinner
import io.karn.notify.Notify
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
import kotlin.collections.HashMap

class CreateRG : Fragment() {

    private var toolbar: Toolbar? = null
    var selectedItem: String? = null
    private var preferenceHelper: PreferenceModel? = null
    private var promptPopUpView: PromptPopUpView? = null
    private val ITEMS = arrayOf(
        "Open group",
        "Security providers",
        "Emergency service providers",
        "Public facility group",
        "Company group",
        "Family group",
        "Private group",
        "Others"
    )

    var updateFNamee: MaterialEditText? = null
    var spinner: MaterialSpinner? = null
    var AUTOCOMPLETE_REQUEST_CODE = 1
    private var textInputLocation: TextInputLayout? = null
    private var textInputSetGrpName: TextInputLayout? = null
    private var destinationAddress: String? = null
    private var setName: EditText? = null
    private var loc: EditText? = null
    var btnLogin: Button? = null
    private var setNameRG: String? = null
    private var setLoc: String? = null
    private var mProgress: ProgressDialog? = null
    var rg: String? = null
    var fullaname: String? = null
    var contactNumber: String? = null
    var contactName: String? = null
    private val RESULT_PICK_CONTACT = 1001
    private var mProgressS: ProgressDialog? = null
    private var mProgresss: ProgressDialog? = null
    private var mProgressSending: ProgressDialog? = null
    private var fullname: String? = null
    private var mssidn: String? = null
    private var userid: String? = null
    private var useid: String? = null
    private var mssidns: String? = null
    private var rg_id: String? = null

  var mstatus: String? = null

    private var fullnames: String? = null


    //  var fname: String? = null
    var user: String? = null
    var updateFName: String? = null


    private var btnResetPassword: Button? = null
    private var btnBack: Button? = null
    var errorNull: TextView? = null

    var MYCODE = 1000

    var alert: android.app.AlertDialog? = null

    var alert_id: String? = null

    var mToolbar: Toolbar? = null
    private var create_rg: LinearLayout? = null
    private var add_member: LinearLayout? = null


    private var rg_input: EditText? = null
    private var configuration: TextView? = null

    private var submit: Button? = null
    private var sentInvite: Button? = null

    private var skip: Button? = null

    var waitingDialog: android.app.AlertDialog? = null

    private var rg_add: String? = null

    var pref: SharedPreferences? = null


    private var account: String? = null

    private var roleID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.activity_createrg, container, false)

        if (!Places.isInitialized()) {
            Places.initialize(activity!!, getString(R.string.google_maps_key))
        }
        val adapter =
            activity?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, ITEMS) }
        adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        pref =
            activity!!.getSharedPreferences("MyPref", 0) // 0 - for private mode

        fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

        mssidn = pref!!.getString("mssdn", null)

        userid = pref!!.getString("userid", null)


        account = pref!!.getString("account_status", null)
        mstatus = pref!!.getString("mstatus", null)

        roleID = pref!!.getString("role", null)


//
//        Toast.makeText(this, "WOW"  +   pref.getString("email", null), Toast.LENGTH_SHORT).show()


        spinner = view.findViewById<View>(R.id.spinner) as MaterialSpinner

        sentInvite = view.findViewById<View>(R.id.btn_invite) as Button

        sentInvite!!.setOnClickListener {
            pickContact()
        }


        spinner?.adapter = adapter
        spinner!!.isSelected = false;  // otherwise listener will be called on initialization
        spinner!!.setSelection(0, true)
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View?,
                arg2: Int, arg3: Long
            ) {
                if (spinner!!.selectedItem == null) {
                   // Toast.makeText(activity, "Please select an Alert Type", Toast.LENGTH_LONG)
                     //   .show();
                    return
                } else {
                    selectedItem = spinner!!.selectedItem.toString()
                }
                // TODO Auto-generated method stub
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }


        }

        skip = view.findViewById(R.id.btn_back)



        create_rg = view.findViewById(R.id.createrg)
        add_member = view.findViewById(R.id.addmember)
        rg_input = view.findViewById(R.id.input_pin)
        submit = view.findViewById(R.id.btn_confirm)



        submit!!.setOnClickListener {
            validation()
        }

        textInputLocation = view.findViewById(R.id.location)
        loc = view.findViewById(R.id.loc)


        configuration =  view.findViewById(R.id.config)

        configuration!!.setOnClickListener {
            updates()
        }

        mProgressS = ProgressDialog(context!!)
        mProgressS!!.setMessage("Proceeding...");
        mProgressS!!.setCancelable(false)

        mProgresss = ProgressDialog(context!!)
        mProgresss!!.setMessage("Adding...");
        mProgresss!!.setCancelable(true)


        mProgressSending = ProgressDialog(context!!)
        mProgressSending!!.setMessage("Sending...");
        mProgressSending!!.setCancelable(false)

        mProgress = ProgressDialog(context!!)
        mProgress!!.setMessage("Saving...");
        mProgress!!.setCancelable(false)
        textInputSetGrpName = view.findViewById(R.id.setgroup)
        setName = view.findViewById(R.id.setgrp)

        btnLogin = view.findViewById(R.id.buttonRG)


        loc!!.setOnClickListener {
            searchPlace()
        }
        loc!!.requestFocus()
        textInputLocation!!.setOnClickListener {
            searchPlace()
        }


        btnLogin!!.setOnClickListener {
            if (!checkError()) return@setOnClickListener
            else {
                if (!isNetworkAvailable()) {
                    internet()
                    promptPopUpView?.changeStatus(1, "Connection Error\n\n Check your internet connectivity")
                }else {
                    checkRGg()
                    btnLogin!!.text = "Submitting.."
                    mProgress?.show()
                }

            }
        }
        skip!!.setOnClickListener {
            getSkip()
            mProgressS!!.show()
        }

        return view
    }

    private fun validation() {
        rg_add = rg_input?.text.toString().trim();
        if (TextUtils.isEmpty(rg_add)) {

            rg_input?.error = "User ID Required";
            rg_input?.requestFocus()
            val imm: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            return;
        }
        if (!TextUtils.isEmpty(rg_add)) {
            if (!isNetworkAvailable()) {
                internet()
                promptPopUpView?.changeStatus(1, "Connection Error\n\n Check your internet connectivity")
            } else {
                mProgresss?.show()
                getUserDetails()
            }
        }
//            intent = Intent(this, NfcHome::class.java)
//            this.startActivity(intent)
    }


    fun hideKeyboardFrom() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun showKeyBoard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun updates() {


        val alertDialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(activity)
        alertDialog.setTitle("Your Group")

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

                params["userid"] = userid!!
                params["rg_id"] = updateFName!!

                val api: checkAdmin = retrofit.create(checkAdmin::class.java)
                val call: Call<ResponseBody> = api.check(params)

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
                            parseLoginDatas(remoteResponse)
                        } else {
                            mProgress?.dismiss()
                            dialogue_error();
                            promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                            Log.d("BAYO", response.code().toString())
                            btnLogin!!.text = "Submit"
                            mProgress?.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        btnLogin!!.text = "Submit"

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                        Log.i("onEmptyResponse", "" + t) //
                        mProgress?.dismiss()
                    }
                })


                // waitingDialog.show()
            }


        })
        val dismissButton: Button =
            layout_pwd.findViewById<View>(R.id.cancel) as Button
        dismissButton.setOnClickListener(View.OnClickListener {
            alert!!.dismiss()
            hideKeyboardFrom()

            //startActivity(Intent(activity, HomePage::class.java))

        })
        alertDialog.setView(layout_pwd)
        alert!!.setCancelable(false)
        alert!!.show()

    }

    private fun parseLoginDatas(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {
                setNameRG = textInputSetGrpName!!.editText!!.text.toString().trim { it <= ' ' }

                dialoguess()
                promptPopUpView?.changeStatus(2, "Level 1 was successfully configured automatically!!\n\n Redirecting...")

                Handler().postDelayed({

                    val i = Intent(activity, level_1::class.java)
                    i.putExtra("rg_id", updateFName)
                    i.putExtra("rg_name", setNameRG)

                    startActivity(i)
                }, 3000)
                waitingDialog!!.dismiss()
                hideKeyboardFrom()
                alert!!.dismiss()
            } else {
                waitingDialog!!.dismiss()

                alert?.dismiss()
                dialogue_error();

                promptPopUpView?.changeStatus(1, "Youre not an admin to this group")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun checkError(): Boolean {
        setNameRG = textInputSetGrpName!!.editText!!.text.toString().trim { it <= ' ' }

        setLoc = textInputLocation!!.editText!!.text.toString().trim { it <= ' ' }

        if (Utils.checkIfEmptyString(setNameRG)) {
            textInputSetGrpName!!.error = "Group Name Is Mandatory"
            textInputSetGrpName!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputSetGrpName!!.error = null

        if(selectedItem == null){
            Toast.makeText(activity, "Type of Group is Mandatory!! Please select", Toast.LENGTH_LONG).show()
            return false
        }

        if (Utils.checkIfEmptyString(setLoc)) {
            textInputLocation!!.error = "Location is Mandatory"
            textInputLocation!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputLocation!!.error = null

        return true

    }
//            intent = Intent(this, NfcHome::class.java)
//            this.startActivity(intent)


    private fun searchPlace() {

        // Set the fields to specify which types of place data to return.
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).setCountry("KE") //KENYA
            .build(activity!!)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }


    private fun checkRGg() {

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

        params["userid"] = userid!!

        val api: countRG = retrofit.create(countRG::class.java)
        val call: Call<ResponseBody> = api.countRG(params)

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
                    parseCheckerData(remoteResponse)
                } else {
                    mProgress?.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    btnLogin!!.text = "Submit"
                    mProgress?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                dialogue_error()
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
                mProgress?.dismiss()
            }
        })
    }

    private fun parseCheckerData(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {

                if (account == "0") {
                    dialogue_error()
                    promptPopUpView?.changeStatus(1, "You have reached maximum groups you can create for this account. Upgrade to Pro to create more")
                    mProgress?.dismiss()
                    hideKeyboardFrom()
                    btnLogin!!.text = "Submit"
                } else if (account == "1" || mstatus == "0") {
                    bayo()
                }
            } else if (jsonObject.getString("status") == "false")  {
                bayo()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun dialoguess_exit()   {

        promptPopUpView = PromptPopUpView(requireContext())

        AlertDialog.Builder(requireContext())


            .setPositiveButton(
                "Finish"
            ) { dialog, _ ->
                dialog.dismiss()

                Handler().postDelayed({
                    val intent = Intent(requireActivity(), HomePage::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                   requireActivity().finish()
                }, 2000)

                Notify
                    .with(requireActivity())
                    .asTextList {
                        lines = listOf(
                            "Your group was created successfully",
                            "Group Name\t" +setNameRG,
                            "Group ID\t" +rg_id )
                        title = "Group Details!"
                        text = "Group Name\t"+setNameRG+",\t" + "Group ID\t" +rg_id
                    }
                    .show()


            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }
    private fun bayo() {

        setNameRG = textInputSetGrpName!!.editText!!.text.toString().trim { it <= ' ' }

        setLoc = textInputLocation!!.editText!!.text.toString().trim { it <= ' ' }

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
        params["nature_of_group"] = selectedItem!!
        params["group_name"] = setNameRG!!
        params["alerts"] = "0"
        params["response_area"] = setLoc!!
        params["userid"] = userid!!
        params["fullname"] = fullname!!
        params["mssidn"] = mssidn!!

        val api: AddResponse = retrofit.create(AddResponse::class.java)
        val call: Call<ResponseBody> = api.addRG(params)

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
                    parseLoginData(remoteResponse)
                } else {
                    mProgress?.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    btnLogin!!.text = "Submit"
                    mProgress?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                btnLogin!!.text = "Submit"

                dialogue_error()
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
                mProgress?.dismiss()
            }
        })
    }

    private fun parseLoginData(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {

                         create_rg!!.visibility = View.GONE
                         add_member!!.visibility = View.VISIBLE

                         hideKeyboardFrom()
                         //getMSSDN()

                        // mProgressSendin!!.show()
                         mProgress?.dismiss()
                         btnLogin!!.text = "Submit"


            }  else if (jsonObject.getString("status") == "false"){
                mProgress?.dismiss()
                dialogue_error();

                loc!!.setText("")

                promptPopUpView?.changeStatus(1, "Group Name already taken!! \n Please enter a different name for Response Group")
                btnLogin!!.text = "Submit"

            } else{
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //Log.d("BAYO", response.code().toString())
                btnLogin!!.text = "Submit"
                loc!!.setText("")

                mProgress?.dismiss()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun getMSSDN() {

        setNameRG = textInputSetGrpName!!.editText!!.text.toString().trim { it <= ' ' }


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
        params["group_name"] = setNameRG!!

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

                    parseLoginDattaS51(remoteResponse)
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

    private fun parseLoginDattaS51(remoteResponse: String) {
        try {
            val jArray3 = JSONArray(remoteResponse)
            for (i in 0 until jArray3.length()) {
                val jsonObjec: JSONObject = jArray3.getJSONObject(i)
                if (jsonObjec.getString("status") == "true")
                {
                    rg =
                        jsonObjec.getString("id")




                    //  inviteMember()
                } else if(jsonObjec.getString("status") == "false"){
                    mProgresss?.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "Something went wrong AlatPres")
                }

            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun getUserDetails() {

        rg_add = rg_input?.text.toString().trim();

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
                }else if(jsonObjec.getString("status") == "false"){
                    mProgresss?.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "User not Registered in AlatPres")
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun getSkip() {

        setNameRG = textInputSetGrpName!!.editText!!.text.toString().trim { it <= ' ' }


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
        params["group_name"] = setNameRG!!

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

                    parseLoginDattaS5(remoteResponse)
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

    private fun parseLoginDattaS5(remoteResponse: String) {
        try {
            val jArray3 = JSONArray(remoteResponse)
            for (i in 0 until jArray3.length()) {
                val jsonObjec: JSONObject = jArray3.getJSONObject(i)
                if (jsonObjec.getString("status") == "true")
                {
                   rg =
                        jsonObjec.getString("id")
                    setNameRG = textInputSetGrpName!!.editText!!.text.toString().trim { it <= ' ' }


                    mProgressS?.dismiss()


                    dialoguess_exit()
                    promptPopUpView?.changeStatus(2, "Response Group was created successfully")

                    mProgressS!!.dismiss()


                    //  inviteMember()
                } else if(jsonObjec.getString("status") == "false"){
                    mProgresss?.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "Something went wrong AlatPres")
                }

            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }




    private fun getRGID() {

        setNameRG = textInputSetGrpName!!.editText!!.text.toString().trim { it <= ' ' }


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
        params["group_name"] = setNameRG!!

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
                    mProgresss?.dismiss()
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

        val api: InviteMember = retrofit.create(InviteMember::class.java)
        val call: Call<ResponseBody> = api.invite(params)

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
                    mProgresss?.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    btnLogin!!.text = "Submit"
                    mProgress?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                btnLogin!!.text = "Submit"
                dialogue_error()
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
                mProgresss?.dismiss()
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
                promptPopUpView?.changeStatus(2, "Member added!! \n Invitation was successfully sent")
                mProgresss?.dismiss()
                loc!!.setText("")
                btnLogin!!.text = "Submit"

            }  else if (jsonObject.getString("status") == "false"){
                mProgresss?.dismiss()
                dialogue_error();
                loc!!.setText("")
                promptPopUpView?.changeStatus(1, "User has already been invited already!!")
                btnLogin!!.text = "Submit"

            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //Log.d("BAYO", response.code().toString())
                btnLogin!!.text = "Submit"
                loc!!.setText("")

                mProgresss?.dismiss()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun pickContact() {
        val contactPickerIntent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        )
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RESULT_PICK_CONTACT -> {
                    var cursor: Cursor? = null
                    try {

                        // getData() method will have the
// Content Uri of the selected contact
                        val uri: Uri? = data!!.data
                        //Query the content uri
                        cursor = activity!!.contentResolver.query(uri!!, null, null, null, null)
                        cursor!!.moveToFirst()
                        // column index of the phone number
                        val phoneIndex: Int = cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        )
                        // column index of the contact name
                        val nameIndex: Int = cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        )
                        contactNumber = cursor.getString(phoneIndex)
                        contactName = cursor.getString(nameIndex)
                        // Set the value to the textviews
//                        Toast.makeText(this, "Selected: " +contactNumber ,
//                       Toast.LENGTH_LONG
//                         ).show()
                        Confirm()
//                        tvContactName.setText("Contact Name : $contactName")
//                        tvContactNumber.setText("Contact Number : $contactNumber")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                destinationAddress = place.name

                loc!!.setText(destinationAddress.toString())

                // Toast.makeText(this@CreateAlert, "" + destinationAddress.toString() , Toast.LENGTH_LONG).show();

                // lblAddress
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
//                Toast.makeText(this@CreateRG, "Some went wrong. Search again", Toast.LENGTH_SHORT)
//                    .show()
                // Log.i(TAG, status.getStatusMessage())
            }
        }
    }
    fun Confirm() {
        AlertDialog.Builder(activity!!)
            .setTitle("Confirmation")
            .setMessage("Send Invitation to "+ contactName)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, id ->
                getSkips()
                mProgressSending!!.show()
            }
            .setNegativeButton("No", null)
            .show().withCenteredButton()
    }

    private fun AlertDialog.withCenteredButton() {
        val positive = getButton(AlertDialog.BUTTON_POSITIVE)
        val negative = getButton(AlertDialog.BUTTON_NEGATIVE)

        negative.setTextColor(ContextCompat.getColor(context, R.color.error))


        positive.setBackgroundColor(ContextCompat.getColor(context, R.color.success))

        positive.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))


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




        private fun isNetworkAvailable(): Boolean {
        // Using ConnectivityManager to check for Network Connection
        val connectivityManager = (
            activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val activeNetworkInfo = connectivityManager
            .activeNetworkInfo
        return activeNetworkInfo != null
    }


    private fun internet() {
        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)

            .setPositiveButton(
                "Retry"
            ) { dialog, _ -> dialog.dismiss()
                dialog.dismiss()

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


    private fun dialoguess() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)

            .setCancelable(false)
            .setView(promptPopUpView)
            .show().withCenteredButtons()
    }

    private fun dialogue() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)
            .setPositiveButton("Add More") { _: DialogInterface?, _: Int ->

            }
            .setNegativeButton("DONE") { _: DialogInterface?, _: Int ->

                dialoguess_exit()
                promptPopUpView?.changeStatus(2, "Response Group was created successfully")

                mProgressS!!.dismiss()
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show().withCenteredButtons()
    }




    private fun dialogues() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)

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



    private fun dialogue_error() {

        promptPopUpView = PromptPopUpView(activity!!)

        AlertDialog.Builder(activity!!)
            .setPositiveButton("Try Again") { _: DialogInterface?, _: Int ->

            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }


    private fun getSkips() {

        setNameRG = textInputSetGrpName!!.editText!!.text.toString().trim { it <= ' ' }


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
        params["group_name"] = setNameRG!!

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

                    parseLoginDattaS5s(remoteResponse)
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

    private fun parseLoginDattaS5s(remoteResponse: String) {
        try {
            val jArray3 = JSONArray(remoteResponse)
            for (i in 0 until jArray3.length()) {
                val jsonObjec: JSONObject = jArray3.getJSONObject(i)
                if (jsonObjec.getString("status") == "true")
                {
                    rg =
                        jsonObjec.getString("id")
                    checkMmeber()
                    //  inviteMember()
                } else if(jsonObjec.getString("status") == "false"){
                    mProgresss?.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "Something went wrong AlatPres")
                }

            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun checkMmeber() {

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
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(Constants.API_BASE_URL)
            .client(client) // This line is important
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val params: HashMap<String, String> = HashMap()
        //  response_id = intent.getStringExtra("groupSelect")

        params["mssdn"] = contactNumber!!
        params["rg_id"] = rg!!
        params["rg_name"] = setNameRG!!
        params["name"] = fullname!!


        val api: sendSMS = retrofit.create(sendSMS::class.java)
        val call: Call<ResponseBody> = api.send(params)

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

                        mProgressSending?.dismiss()
                        dialogue();
                        promptPopUpView?.changeStatus(
                            2,
                            "Invitation to " + contactName + " was send successfuly"
                        )

                    } else {
                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                        Log.d("BAYO", response.code().toString())
                        mProgressSending?.dismiss()
                    }
                } else {
                    dialogue_error()
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    mProgressSending?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
                mProgress?.dismiss()
            }
        })
    }






}