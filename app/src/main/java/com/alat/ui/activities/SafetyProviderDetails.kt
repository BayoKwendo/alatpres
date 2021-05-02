package com.alat.ui.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.GetSafetyProvider
import com.alat.interfaces.SendAlatRequest
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.rengwuxian.materialedittext.MaterialEditText
import dmax.dialog.SpotsDialog
import fr.ganfra.materialspinner.MaterialSpinner
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
import java.util.concurrent.TimeUnit

class SafetyProviderDetails : AppCompatActivity() {

    var mid: String? = null
    var mToolbar: Toolbar? = null
    val catList: ArrayList<String> = ArrayList()
    var updateFNamee: MaterialEditText? = null
    var alert: android.app.AlertDialog? = null
    var spinner_select: MaterialSpinner? = null

    //textview declaration
    var msafety_providdr_name: TextView? = null
    var mservices: TextView? = null
    var malatpres_id: TextView? = null
    var mareas_of_operations: TextView? = null
    var mRGs: TextView? = null


    var SEARCHPLACE = 5

    //MaterialEdit initialization
    var mcontactName: MaterialEditText? = null
    var mmsisdn: MaterialEditText? = null
    var mlocation: MaterialEditText? = null
    var mtype_alat: MaterialEditText? = null

    private var mProgress: ProgressDialog? = null

    //strings
    var safety_providdr_name: String? = null
    var services: String? = null
    var alatpres_id: String? = null
    var areas_of_operations: String? = null
    var url: String? = null
    var RGs: String? = null
    var msisdn_provider: String? = null

    var contactName: String? = null
    var msisdn: String? = null
    var location: String? = null
    var type_alat: String? = null
    var user: String? = null

    //button
    var mproceed: Button? = null

    var pref: SharedPreferences? = null
    private var promptPopUpView: PromptPopUpView? = null


    private val ITEMS_PROVIDER = arrayOf(
        "Send Request Alat",
        "Get Service"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safety_provider)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mid = intent.getStringExtra("safetyproviderid")

        //binding with UI
        msafety_providdr_name = findViewById(R.id.namesafety)
        mservices = findViewById(R.id.textView9)
        malatpres_id = findViewById(R.id.textView7)
        mareas_of_operations = findViewById(R.id.textView15)
        mRGs = findViewById(R.id.textView21)
        mproceed = findViewById(R.id.proceed)

        if (!Places.isInitialized()) {
            Places.initialize(this, application.getString(R.string.google_maps_key))
        }

        pref =
            getSharedPreferences("MyPref", 0) // 0 - for private mode
        user = pref!!.getString("userid", null)

        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Fetching.. please wait")
        mProgress!!.setCancelable(false)
        mProgress!!.show()

        mproceed!!.setOnClickListener {

            AlertDialog.Builder(this)
//                    .setMessage("Are you sure want to go back?")
                .setCancelable(false)
                .setPositiveButton("Proceed") { _, id ->
                    selectAlert()
                }
                .setNegativeButton("Back") { _, id ->
                    finish()
//                        selectAlert()
                }
                .show().withCenteredButton()
//            AlertSafe()
        }


        getSafeProviderDetails()

    }

    private fun getSafeProviderDetails() {
        mProgress!!.show()
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
        params["id"] = mid!!

        val api: GetSafetyProvider = retrofit.create(GetSafetyProvider::class.java)
        val call: Call<ResponseBody> = api.GetProvider(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());

                if (response.isSuccessful) {
//                        val jsonresponse = response.body().toString()

                    val remoteResponse = response.body()!!.string()
                    Log.d("test", remoteResponse)
                    if (response.code().toString() == "200") {

                        try {
                            val parentObject = JSONObject(remoteResponse)
                            Log.d("testss", parentObject.toString())
                            val array: JSONArray = parentObject.getJSONArray("records")
                            val jsonObject = array.getJSONObject(0)
                            msafety_providdr_name!!.text = jsonObject.getString("alert_name")
                            mservices!!.text = jsonObject.getString("services")
                            malatpres_id!!.text = jsonObject.getString("userid")
                            mareas_of_operations!!.text = jsonObject.getString("area_operation")
                            mRGs!!.text = jsonObject.getString("rg")
                            url = jsonObject.getString("url")
                            msisdn_provider = jsonObject.getString("msisdn")

                            mProgress!!.dismiss()

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    mProgress!!.dismiss()

                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.i("onEmptyResponse", "" + t) //
                mProgress!!.dismiss()
            }
        })
    }

//
//    private fun parseSafetyProvider(jsonresponse: String) {
//
//    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_search -> {
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }


    override fun onBackPressed() {
        // close search view on back button pressed
        finish()
    }


    fun selectAlert() {
        val alertDialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        alertDialog.setTitle("Options of accessing or requesting an intergrated service")

        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val layout_pwd: View =
            inflater.inflate(R.layout.layout_select_safety, null)
        alertDialog.setView(layout_pwd)

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, ITEMS_PROVIDER)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        alert = alertDialog.create()
        spinner_select = layout_pwd.findViewById<View>(R.id.spinner4) as MaterialSpinner
        spinner_select?.adapter = adapter2
        spinner_select!!.isSelected = false;  // otherwise listener will be called on initialization
        spinner_select!!.setSelection(0, true)
        spinner_select?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View?,
                arg2: Int, arg3: Long
            ) {
                if (spinner_select!!.selectedItem == null) {
//                    Toast.makeText(this@CreateAlert, "Create Alat", Toast.LENGTH_LONG).show();
                    return
                } else {
                    val selectedItem = spinner_select!!.selectedItem.toString()

                    if (selectedItem == "Send Request Alat") {
                        alert!!.dismiss()
                        sendRequest()
                    } else {
                        alert!!.dismiss()
                        val viewIntent = Intent(
                            "android.intent.action.VIEW",
                            Uri.parse(url)
                        )
                        startActivity(viewIntent)
                    }
//                     Toast.makeText(this@CreateAlert, selectedItem, Toast.LENGTH_LONG).show();
                }
                // TODO Auto-generated method stub
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }
        val updateButton: Button =
            layout_pwd.findViewById<View>(R.id.update) as Button
        updateButton.setOnClickListener(View.OnClickListener {
            alert!!.dismiss()


        })
        val dismissButton: Button =
            layout_pwd.findViewById<View>(R.id.cancel) as Button
        dismissButton.setOnClickListener(View.OnClickListener {
            alert!!.dismiss()
//            startActivity(Intent(this@CreateAlert, HomePage::class.java))


        })
        alertDialog.setView(layout_pwd)
        alert!!.setCancelable(false)
        alert!!.show()

    }


    fun sendRequest() {
        val alertDialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        alertDialog.setTitle("Send Alat/Request")

        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val layout_pwd: View =
            inflater.inflate(R.layout.layout_send_alat, null)
        alertDialog.setView(layout_pwd)
        alert = alertDialog.create()


        mcontactName = layout_pwd.findViewById<View>(R.id.etName) as MaterialEditText
        mmsisdn = layout_pwd.findViewById<View>(R.id.etPhone) as MaterialEditText
        mlocation = layout_pwd.findViewById<View>(R.id.location) as MaterialEditText
        mtype_alat = layout_pwd.findViewById<View>(R.id.type_alat) as MaterialEditText

        mcontactName!!.requestFocus()


        mlocation!!.setOnClickListener {
            searchPlace()
        }

        val updateButton: Button =
            layout_pwd.findViewById<View>(R.id.update) as Button
        updateButton.setOnClickListener(View.OnClickListener {

            val waitingDialog: android.app.AlertDialog =
                SpotsDialog.Builder().setContext(this).build()

            contactName = mcontactName!!.getText().toString()
            msisdn = mmsisdn!!.getText().toString()
//            updateID = updateIDD!!.getText()!!.toString()
            // updateAlatpressID = updateAlatpressIDD!!.getText().toString()
            location = mlocation!!.getText().toString()
            type_alat = mtype_alat!!.getText().toString()


            if (Utils.checkIfEmptyString(type_alat)) {
                mtype_alat!!.error = "Type Alat Is Mandatory"
                mtype_alat!!.requestFocus()
            } else {
                waitingDialog.show()
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
                                chain.request()
                                    .newBuilder() // .addHeader(Constant.Header, authToken)
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
                params["name"] = contactName!!
                params["msisdn"] = msisdn!!
                params["location"] = location!!
                params["alat_type"] = type_alat!!
                params["msisdn_provider"] = msisdn_provider!!
                params["userid"] = user!!

                val api: SendAlatRequest = retrofit.create(SendAlatRequest::class.java)
                val call: Call<ResponseBody> = api.sendRequest(params)

                call.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {

                        Log.d("Call request", call.request().toString());
                        Log.d("Response raw header", response.headers().toString());
                        Log.d("Response raw", response.toString());
                        Log.d("Response code", response.code().toString());


                        //Toast.makeText()

                        if (response.isSuccessful) {
                            val remoteResponse = response.body()!!.string()
                            Log.d("test", remoteResponse)
                            waitingDialog.dismiss()
                            try {
                                val jsonObject = JSONObject(remoteResponse)
                                if (jsonObject.getString("status") == "true") {
                                    dialogue();
                                    promptPopUpView?.changeStatus(2, "Your request has been sent successfully. The provider will reach out to you within the shortest time. Thank you")
                                    mProgress?.dismiss()

                                } else {
                                    mProgress?.dismiss()
                                    dialogue_error();

                                    promptPopUpView?.changeStatus(1, "Unable to send")
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else {
                            mProgress?.dismiss()
//                            dialogue_error();
//                            promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                            Log.d("BAYO", response.code().toString())
                            //btnLogin!!.text = "Submit"
                            mProgress?.dismiss()
                            dialogue_error();

                            promptPopUpView?.changeStatus(1, "Unable to send")
                            waitingDialog.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        //btnLogin!!.text = "Submit"
//
//                        dialogue_error()
//                        promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                        Log.i("onEmptyResponse", "" + t) //
                        mProgress?.dismiss()
                        dialogue_error();
                        promptPopUpView?.changeStatus(1, "Unable to send")
                    }
                })
            }

            //RequestBody body = RequestBody.Companion.create(json, JSON)\\\


        })
        val dismissButton: Button =
            layout_pwd.findViewById<View>(R.id.cancel) as Button
        dismissButton.setOnClickListener(View.OnClickListener { alert!!.dismiss() })
        alertDialog.setView(layout_pwd)
        alert!!.show()

    }


    private fun searchPlace() {

        // Set the fields to specify which types of place data to return.
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        // Start the autocomplete intent.
        val intent2 = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).setCountry("KE") //KENYA
            .build(this)
        startActivityForResult(intent2, SEARCHPLACE)
    }


    //
//
//    fun AlertSafe() {
//
//        AlertDialog.Builder(this)
//            .setMessage("I am free from danger\n\n")
//            .setCancelable(true)
//            .setPositiveButton("Safe") { _, id ->
//                //  accept()
//                safe()
//                mProgress!!.show()
//                // startActivity(Intent(this@FriendRequests, HomePage::class.java))
//            }
//            .setNegativeButton("Cancel") { _, id ->
//                // reject()
//                //getRGNAME()
//            }
//            .show().withCenteredButtonss()
//    }
//
//
//
    private fun dialogue() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->


            }

            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    private fun dialogue_error() {
        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }


    private fun AlertDialog.withCenteredButton() {
        val positive = getButton(AlertDialog.BUTTON_POSITIVE)
        val negative = getButton(AlertDialog.BUTTON_NEGATIVE)

        negative.setBackgroundColor(ContextCompat.getColor(context, R.color.error))

        negative.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))


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


    override fun onActivityResult(
        RC: Int,
        RQC: Int,
        I: Intent?
    ) {
        super.onActivityResult(RC, RQC, I)
        if (RC == SEARCHPLACE) {
            if (RQC == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(I!!)
                if (place != null) {
                    mlocation!!.setText(place.getName())
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress())
//                    Toast.makeText(
//                        this@CreateAlert,
//                        "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(),
//                        Toast.LENGTH_LONG
//                    ).show()
//                    val address = place.getAddress()
                } else {
                    Log.d("TEST", "place is null")
                }
                // do query with address
            } else if (RQC == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(I!!)
                Toast.makeText(
                    this@SafetyProviderDetails,
                    "Error: " + status.getStatusMessage(),
                    Toast.LENGTH_LONG
                ).show()
//                Log.i(TAG, status.getStatusMessage())
            } else if (RQC == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

}
