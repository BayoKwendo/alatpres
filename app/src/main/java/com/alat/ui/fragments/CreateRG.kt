package com.alat.ui.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
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
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.AddResponse
import com.alat.interfaces.LoginInterface
import com.alat.model.PreferenceModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputLayout
import fr.ganfra.materialspinner.MaterialSpinner
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
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

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
    private var fullname: String? = null
    private var mssidn: String? = null
    private var userid: String? = null

    var pref: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

//
//        Toast.makeText(this, "WOW"  +   pref.getString("email", null), Toast.LENGTH_SHORT).show()


        spinner = view.findViewById<View>(R.id.spinner) as MaterialSpinner
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
        textInputLocation = view.findViewById(R.id.location)
        loc = view.findViewById(R.id.loc)

        mProgress = ProgressDialog(context!!)
        mProgress!!.setMessage("Saving...");
        mProgress!!.setCancelable(true)
        textInputSetGrpName = view.findViewById(R.id.setgroup)
        setName = view.findViewById(R.id.setgrp)

        btnLogin = view.findViewById(R.id.buttonRG)


//        loc!!.setOnClickListener {
//            searchPlace()
//        }
//        loc!!.requestFocus()
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
                    bayo()
                    btnLogin!!.text = "Submitting.."
                    mProgress?.show()
                }

            }
        }
        return view
    }


    private fun showKeyBoard() {
        val imm =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
                Log.d("Call request header", call.request().headers.toString());
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
                dialogue();
                promptPopUpView?.changeStatus(2, "SUCCESSFUL")
                mProgress?.dismiss()
                setName!!.setText("")
                loc!!.setText("")

                btnLogin!!.text = "Submit"

            }  else if (jsonObject.getString("status") == "false"){
                mProgress?.dismiss()
                dialogue_error();

                setName!!.setText("")
                loc!!.setText("")

                promptPopUpView?.changeStatus(1, "Group Name already taken!! \n Please enter a different name for Response Group")
                btnLogin!!.text = "Submit"

            } else{
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //Log.d("BAYO", response.code().toString())
                btnLogin!!.text = "Submit"
                setName!!.setText("")
                loc!!.setText("")

                mProgress?.dismiss()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
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
                activity!!.recreate()
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

    private fun dialogue() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)
            .setPositiveButton("Add RG") { _: DialogInterface?, _: Int ->
                //      finish()

            }
            .setNegativeButton("Exit") { _: DialogInterface?, _: Int ->
                //      finish()
                startActivity(Intent(activity, HomePage::class.java))
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



    private fun dialogue_error() {

        promptPopUpView = PromptPopUpView(activity!!)

        AlertDialog.Builder(activity!!)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->

            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }


}