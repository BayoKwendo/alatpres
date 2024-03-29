package com.alat.ui.activities.enterprise

import adil.dev.lib.materialnumberpicker.dialog.LevelDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Typeface
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder
import cafe.adriel.androidaudiorecorder.TypefaceHelper
import cafe.adriel.androidaudiorecorder.model.AudioChannel
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate
import cafe.adriel.androidaudiorecorder.model.AudioSource
import com.alat.HomePage
import com.alat.R
import com.alat.adapters.MultiSelectionSpinner
import com.alat.helpers.Constants
import com.alat.helpers.FileUtils
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputLayout
import com.rengwuxian.materialedittext.MaterialEditText
import dmax.dialog.SpotsDialog
import fr.ganfra.materialspinner.MaterialSpinner
import libs.mjn.prettydialog.PrettyDialog
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class CreateAlerteNT : AppCompatActivity() {
    private val ITEMS = arrayOf(
        "Suspicious Activity [SA] (Security)",
        "Suspicious Person(s) [SP] (Security)",
        "Burglary/Robbery [BG] (Security)",
        "CyberCrime [CC] (Security)",
        "Fraud/Vandalism[ FRAUD] (Security)",
        "Disturbing of Peace [DOF] (Security)",
        "Riot/Demonstrations [RT] (Security)",
        "Terrorism [TM] (Security)",
        "Industrial Accident [ACC] (medical)",
        "Traffic Incidence [TI]",
        "Drugs & Alcohol [DRUG]",
        "Fire [FR]",
        "Medical Emergency [MED]",
        "Natural Disaster [ND]",
        "Gender Violence[GV]",
        "Environmental Crime[EC]",
        "Public Health Concern [PH] (medical)",
        "Poaching & Wildlife[PW] ",
        "Domestic Violence/Homicide [DV]",
        "Sex Crime/Rape [RAPE] (Security)",
        "Murder Case [MDR] (Security)",
        "General Violence [GV] (medical)",
        "Bribery [BR]",
        "Illegal Business [IB] (Security)",
        "Child Abuse [CA] ",
        "Female Genital Mutilation [FGM]",
        "Prostitution/Pornography [PHY]",
        "Kidnapping [KID]",
        "Gambling [GAME]")

    var spinner: MaterialSpinner? = null

    var spinner3: MaterialSpinner? = null

    var spinner_2: MultiSelectionSpinner? = null
    var selectedItem: String? = null

    var selectedItem2: String? = null
    private var promptPopUpView: PromptPopUpView? = null
    private val IMAGE_DIRECTORY = "/demonuts_upload_gallery"

    var bitmap: Bitmap? = null
    var SEARCHPLACE = 5

    var path: String? = null

    var imageView: ImageView? = null

    var imageName: EditText? = null

    var progressDialog: ProgressDialog? = null

    var GetImageNameEditText: String? = null

    var PICK_FILE_REQUEST = 100;

    private var textInputLocation: TextInputLayout? = null

    private var textInputAlert: TextInputLayout? = null
    private var alert: EditText? = null
    var selectedRG: ArrayList<String>? = null

    var rg: String? = null
    private var destinationAddress: String? = null
    private var mfile: ImageView? = null
    private var textInputlevel: TextInputLayout? = null
    private var level: EditText? = null
    private var loc: EditText? = null
    private var notes: EditText? = null

    var btnLogin: Button? = null
    private var setLevel: String? = null


    private var selecteditem3: String? = null
    private var mattach: TextView? = null

    private var alert_namess: String? = null

    private var setLoc: String? = null

    private var fullname: String? = null
    private var mssidn: String? = null
    private var userid: String? = null
    var updateFNamee: MaterialEditText? = null

    var waitingDialog: android.app.AlertDialog? = null

    private var addnotes: String? = null


    var alertsss: android.app.AlertDialog? = null

   var mstatus: String? = null
    var pref: SharedPreferences? = null
    private var GALLERY = 1


    private var DOCUMENTS = 1
    val catList: ArrayList<String> = ArrayList()
    val catList1: ArrayList<String> = ArrayList()

    var fname: String? = null
    var user: String? = null
    var check: Boolean? = null
    var listString: String? = null


    private val REQUEST_RECORD_AUDIO = 0
    private var account: String? = null
    private var roleID: String? = null

    private var mProgress: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createalertclient)
        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Saving...")
        mProgress!!.setCancelable(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (!Places.isInitialized()) {
            Places.initialize(this, application.getString(R.string.google_maps_key))
        }
        pref =
            getSharedPreferences("MyPref", 0) // 0 - for private mode
        account = pref!!.getString("account_status", null)
        roleID = pref!!.getString("role", null)
        user = pref!!.getString("userid", null)
        mstatus = pref!!.getString("mstatus", null)

        //Adapters
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ITEMS)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        textInputLocation = findViewById(R.id.location)
        alert = findViewById(R.id.alert)

        textInputAlert = findViewById(R.id.alertname)
        notes = findViewById(R.id.notes)
        mattach = findViewById(R.id.attach)

        if (account == "0") {
            notes!!.visibility = View.GONE

        } else if (account == "1" || mstatus == "0") {
            notes!!.visibility = View.VISIBLE
        }

        spinner_2 = findViewById(R.id.spinner2)

        imageView = findViewById(R.id.imageView)
        imageView!!.visibility = View.GONE
        loc = findViewById(R.id.loc)
        loc!!.setOnClickListener {
            searchPlace()
        }
//        loc!!.requestFocus()
        textInputLocation!!.setOnClickListener {
            searchPlace()
        }
        textInputlevel = findViewById(R.id.level_layout)
        level = findViewById(R.id.level)
        level!!.setOnClickListener(View.OnClickListener { v: View? ->
            level!!.clearFocus()
            level!!.isFocusable = false
            val dialog = LevelDialog(this)
            dialog.setOnSelectingLevel { value -> level?.setText(value) }
            dialog.show()
        })
        spinner = findViewById<View>(R.id.spinner) as MaterialSpinner
        spinner?.adapter = adapter
        spinner!!.isSelected = false;  // otherwise listener will be called on initialization
        spinner!!.setSelection(0, true)
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View?,
                arg2: Int, arg3: Long
            ) {
                if (spinner!!.selectedItem == null) {
                    // Toast.makeText(this@CreateAlert, "Please select an Alert Type", Toast.LENGTH_LONG).show();
                    selectedItem = null
                    return
                } else {
                    selectedItem = spinner!!.selectedItem.toString()
                    // Toast.makeText(this@NFCWrite, tv, Toast.LENGTH_LONG).show();
                }
                // TODO Auto-generated method stub
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }
        mfile = findViewById(R.id.file)

        mfile!!.setOnClickListener(View.OnClickListener { v: View? ->

            if (account == "0") {
                dialogue_error()
                promptPopUpView?.changeStatus(1, "Upgrade to Pro to add attachments and audios")
//                waitingDialog!!.dismiss()
            } else if (account == "1" || mstatus == "0") {
                attachemts()
            }

        })
        btnLogin = findViewById(R.id.buttonLogin)


        btnLogin!!.setOnClickListener {
            if (!checkError()) return@setOnClickListener
            else {
                if (!isNetworkAvailable()) {
                    internet()
                    promptPopUpView?.changeStatus(
                        1,
                        "Connection Error\n\n Check your internet connectivity"
                    )
                } else {
                    subm()
                }
            }
        }
        getStation()

        getStudent()
    }

    private fun attachemts() {

        val pDialog = PrettyDialog(this)
        pDialog
            .setIconTint(R.color.colorPrimary)
            .setTitle("Add Attachment")
            .setTitleColor(R.color.pdlg_color_blue)
            .setMessage("Select type of attachment you would like to add")
            .setMessageColor(R.color.pdlg_color_gray)
            .addButton(
                "Images",
                R.color.pdlg_color_white,
                R.color.colorPrimary
            ) {   val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                   startActivityForResult(galleryIntent, GALLERY)
                pDialog.dismiss()
            }
            .addButton(
                "PDF Documents",
                R.color.pdlg_color_white,
                R.color.colorPrimary
            ) {browseDocuments()
                pDialog.dismiss()}
            .addButton(
                "Record an Audio",
                R.color.pdlg_color_white,
                R.color.colorPrimary
            ) {
                pDialog.dismiss()
                TypefaceHelper.DEFAULT =
                    Typeface.createFromAsset(
                        assets,
                        "fonts/IRANSansMobile-Regular.ttf"
                    )
                path =
                    Environment.getExternalStorageDirectory().path + "/recorded_audio.wav"

                AndroidAudioRecorder.with(this) // Required
                    .setFilePath(path)
                    .setColor(ContextCompat.getColor(this, R.color.recorder_bg))
                    .setRequestCode(REQUEST_RECORD_AUDIO) // Optional
                    .setSource(AudioSource.MIC)
                    .setChannel(AudioChannel.STEREO)
                    .setSampleRate(AudioSampleRate.HZ_48000)
                    .setAutoStart(false)
                    .setKeepDisplayOn(true) // Start recording
                    .record()
            }

            .show()

    }


    private fun browseDocuments() {
        val chooseFile: Intent
        val intent: Intent
        chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
        chooseFile.type = "application/pdf"
        intent = Intent.createChooser(chooseFile, "Choose a file")
        startActivityForResult(intent, DOCUMENTS)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        return if (id == android.R.id.home) {
            startActivity(Intent(this@CreateAlerteNT, HomePage::class.java))

            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }


    private fun isNetworkAvailable(): Boolean {
        // Using ConnectivityManager to check for Network Connection
        val connectivityManager = (this
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val activeNetworkInfo = connectivityManager
            .activeNetworkInfo
        return activeNetworkInfo != null
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
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).setCountry("KE") //KENYA
            .build(this)
        startActivityForResult(intent, SEARCHPLACE)
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
        val params: java.util.HashMap<String, String> = java.util.HashMap()

        params["userid"] = user!!
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
                    internet()
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                //   btn.text = "Proceed"
                Log.i("onEmptyResponse", "" + t) //
                internet()
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //mProgress?.dismiss()
            }
        })
    }

    private fun parseLogiData(jsonresponse: String) {
        try {
            val o = JSONObject(jsonresponse)
            val array: JSONArray = o.getJSONArray("records")
            val jsonarray = JSONArray(array.toString())
            for (i in 0 until jsonarray.length()) {
                val jsonobject: JSONObject = jsonarray.getJSONObject(i)
                catList.add(jsonobject.getString("name"))
            }
            spinner_2!!.setItems(catList)
            //data source for drop-down list
            selectedRG = spinner_2!!.selectedItems
            listString = TextUtils.join(", ", spinner_2!!.selectedItems)
            spinner_2!!.setSelection(selectedRG);
        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }


    private fun getStation() {


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

        params["userid"] = user!!
        val api: ViewGPsEnterprise = retrofit.create(ViewGPsEnterprise::class.java)
        val call: Call<ResponseBody>? = api.viewRG(params)

        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()
                if (response.isSuccessful) {
                    val remoteResponse = response.body()!!.string()
                    parseStationData(remoteResponse)
                } else {
                    Log.d("bayo", response.errorBody()!!.string())
                    internet()
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                //   btn.text = "Proceed"
                Log.i("onEmptyResponse", "" + t) //
                internet()
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //mProgress?.dismiss()
            }
        })
    }
    private fun parseStationData(jsonresponse: String) {
        try {
            val o = JSONObject(jsonresponse)
            val array: JSONArray = o.getJSONArray("records")
            //  val array: JSONArray = JSONArray(jsonresponse)
            val jsonarray = JSONArray(array.toString())
            for (i in 0 until jsonarray.length()) {
                val jsonobject: JSONObject = jsonarray.getJSONObject(i)
                catList1.add(jsonobject.getString("station_name"))
            }
            val adapter_3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, catList1)
            adapter_3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner3 = findViewById<View>(R.id.spinner4) as MaterialSpinner
            spinner3?.adapter = adapter_3
            spinner3!!.isSelected = false;  // otherwise listener will be called on initialization
            spinner3!!.setSelection(0, true)
            spinner3?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>?, arg1: View?,
                    arg2: Int, arg3: Long
                ) {
                    if (spinner3!!.selectedItem != null) {
                        // Toast.makeText(this@CreateAlert, "Please select an RG", Toast.LENGTH_LONG).show();
                        selecteditem3 = spinner3!!.selectedItem.toString()
                    }   // Toast.makeText(this@NFCWrite, tv, Toast.LENGTH_LONG).show();

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





    fun updates() {


        val alertDialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        alertDialog.setTitle("Attach Alert other Response Group")

        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val layout_pwd: View =
            inflater.inflate(R.layout.layout_submit_rl, null)
        alertDialog.setView(layout_pwd)

        alertsss = alertDialog.create()


        val adapter_3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, catList)
        adapter_3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner3 = layout_pwd.findViewById<View>(R.id.spinner2) as MaterialSpinner
        spinner3?.adapter = adapter_3
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
                    selectedItem2 = spinner3!!.selectedItem.toString()
                    // Toast.makeText(this@NFCWrite, tv, Toast.LENGTH_LONG).show();
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
            waitingDialog =
                SpotsDialog.Builder().setContext(this).build()
            if (spinner3!!.selectedItem == null) {
                Toast.makeText(this@CreateAlerteNT, "Please select an RG", Toast.LENGTH_LONG).show();

            }
             else {
                alertsss!!.dismiss()

                mProgress!!.show()

                subm()



            }


        })
        val dismissButton: Button =
            layout_pwd.findViewById<View>(R.id.cancel) as Button
        dismissButton.setOnClickListener(View.OnClickListener {
            alertsss!!.dismiss()

            startActivity(Intent(this, HomePage::class.java))

        })
        alertDialog.setView(layout_pwd)
        alertsss!!.setCancelable(false)
        alertsss!!.show()

    }

    override fun onBackPressed() {
        // close search view on back button pressed
        startActivity(Intent(this@CreateAlerteNT, HomePage::class.java))

    }

    private fun showKeyBoard() {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
    private fun checkError(): Boolean {
        setLevel = textInputlevel!!.editText!!.text.toString().trim { it <= ' ' }

        alert_namess = textInputAlert!!.editText!!.text.toString().trim { it <= ' ' }


        setLoc = textInputLocation!!.editText!!.text.toString().trim { it <= ' ' }



        if (Utils.checkIfEmptyString(alert_namess)) {

            textInputAlert!!.error = "Setting an alert name is compulsory"
            textInputAlert!!.requestFocus()
            showKeyBoard()

            //showKeyBoard()
            return false

        } else textInputAlert!!.error = null
//        if (Utils.checkIfEmptyString(setLevel)) {
//
//            textInputlevel!!.error = "Setting Level Is Mandatory"
//            textInputlevel!!.requestFocus()
//            showKeyBoard()
//
//          //showKeyBoard()
//            return false
//
//        } else textInputlevel!!.error = null

        if(selectedItem == null){
            Toast.makeText(this, "Type of Alert is Mandatory!! Please select", Toast.LENGTH_LONG).show()
            return false
        }

//        if(selectedItem2 == null){
//            Toast.makeText(this@CreateAlerteNT, "Select Response Group!! Please select", Toast.LENGTH_LONG).show()
//            return false
//        }

        if (Utils.checkIfEmptyString(setLoc)) {
            textInputLocation!!.error = "Location is Mandatory"
            textInputLocation!!.requestFocus()
          //showKeyBoard()
            return false
        } else textInputLocation!!.error = null
        return true
    }


    private fun parseLoginData(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)

            if (jsonObject.getString("status") == "true") {


                if (selecteditem3 == null) {

                    Toast.makeText(this@CreateAlerteNT, "Update Alat was successfully created", Toast.LENGTH_LONG).show()

                    startActivity(Intent(this@CreateAlerteNT, HomePage::class.java))

                    // ImageUploadToServerFunction()
                    mProgress?.dismiss()
                    //waitingDialog!!.dismiss()
                    btnLogin!!.text = "Submit"

                    imageView!!.visibility = View.GONE
//                    dialogue();
//                    promptPopUpView?.changeStatus(2, "SUCCESSFUL")
                }
                else{

                        if(path == null){
                            uploadImages2()
                        }else {
                            uploadImage2(path!!)
                        }
                    }




            } else {

                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //Log.d("BAYO", response.code().toString())
                btnLogin!!.text = "Submit"
                level  !!.setText("")
                loc!!.setText("")

                mProgress?.dismiss()

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
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


    private fun dialogue() {

        promptPopUpView = PromptPopUpView(this)

            AlertDialog.Builder(this)
            .setPositiveButton("Exit") { _: DialogInterface?, _: Int ->
                //      finish()
               // updates()

                startActivity(Intent(this@CreateAlerteNT, HomePage::class.java))

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
        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->

            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    override fun onActivityResult(
        RC: Int,
        RQC: Int,
        I: Intent?
    ) {
        super.onActivityResult(RC, RQC, I)
        if (RC == SEARCHPLACE) {
            if (RQC == RESULT_OK)
            {
                val place = Autocomplete.getPlaceFromIntent(I!!)

                if(place != null ) {
                    loc!!.setText(place.getName())
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress())
//                    Toast.makeText(
//                        this@CreateAlert,
//                        "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(),
//                        Toast.LENGTH_LONG
//                    ).show()
//                    val address = place.getAddress()
                }
                else{
                    Log.d("TEST" , "place is null")
                }
                // do query with address
            }
            else if (RQC == AutocompleteActivity.RESULT_ERROR)
            {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(I!!)
                Toast.makeText(this@CreateAlerteNT, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show()
//                Log.i(TAG, status.getStatusMessage())
            }
            else if (RQC == RESULT_CANCELED)
            {
                // The user canceled the operation.
            }
        }

        if (RC == GALLERY) {
            if (RQC == RESULT_OK) {
                val contentURI = I!!.data
                try {
                    bitmap =
                        MediaStore.Images.Media.getBitmap( this.contentResolver, contentURI)
                    if(bitmap != null){
                        mattach!!.text = "Image is attached successfully!! You now can submit your alert"
//                        Toast.makeText(
//                            this, "Document recorded successfully!", Toast.LENGTH_SHORT).show()
                        imageView!!.setImageBitmap(bitmap)
                        imageView!!.visibility = View.VISIBLE
                        path = saveImage(bitmap!!)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@CreateAlerteNT,
                        e.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        if (RC == REQUEST_RECORD_AUDIO) {
            if (RQC == RESULT_OK) {
                mattach!!.text = "Audio record is attached successfully!! You can now submit your alert"
                Toast.makeText(
                    this,
                    "Audio recorded successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (RQC == RESULT_CANCELED) {
                Toast.makeText(
                    this,
                    "Audio was not recorded",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (RC == DOCUMENTS) {
            if (RQC == RESULT_OK) {
                mattach!!.text = "Document is attached successfully!! You now can submit your alert"

//                mattach!!.text = "Document is attached successfully!! You now can submit your alert"
                val uri: Uri = I!!.getData()!!
                val filePathFromUri = FileUtils.getRealPath(this, uri)
                val file = File(filePathFromUri)
                path = file.absolutePath

//                path = pickiT!!.getPath(I!!.data, Build.VERSION.SDK_INT).toString();
//                Toast.makeText(
//                    this, path,
//                    Toast.LENGTH_SHORT
//                ).show()
            } else if (RQC == RESULT_CANCELED) {
                Toast.makeText(
                    this,
                    "Document not attached",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    fun subm(){
            mProgress?.show()
            if(path == null){
                uploadImages()
            }else {
                uploadImage(path!!)
            }

    }

    fun subm2(){
        setLevel = textInputlevel!!.editText!!.text.toString().trim { it <= ' ' }
        if (setLevel == "Level 1"){
            mProgress?.show()
            if(path == null){
                uploadImages2()
            }else {
                uploadImage2(path!!)
            }
        }else {
            done()
        }
    }

    fun done() {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to initiate a dispatch from your response teams?\n It is punishable in law to raise false alarms\n\nProceed??")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, id ->
                btnLogin!!.text = "Submitting.."
                if (path == null) {
                    uploadImages()
                } else {
                    uploadImage(path!!)
                }
                mProgress?.show()
            }
            .setNegativeButton("No") { _, id ->
            }
            .show().withCenteredButtons()
    }



    private fun uploadImage(path: String) {

        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode

        fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

        mssidn = pref!!.getString("mssdn", null)

        user = pref!!.getString("userid", null)

        addnotes = notes!!.text.toString().trim { it <= ' ' }

        alert_namess = textInputAlert!!.editText!!.text.toString().trim { it <= ' ' }
        setLevel = textInputlevel!!.editText!!.text.toString().trim { it <= ' ' }
        setLoc = textInputLocation!!.editText!!.text.toString().trim { it <= ' ' }

        //RequestBody body = RequestBody.Companion.create(json, JSON)\\\

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES) // write timeout
            .readTimeout(2, TimeUnit.MINUTES) // read timeout
            .addInterceptor(interceptor) //.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .addNetworkInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                    val request: Request =
                        chain.request().newBuilder() // .addHeader(Constant.Header, authToken)
                            .build()
                    return chain.proceed(request)
                }
            }).build()
        val imgname = Calendar.getInstance().timeInMillis.toString()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        //Create a file object using file path
        val file = File(path)
        // Parsing any Media type file
        val requestBody =
            RequestBody.create("*/*".toMediaTypeOrNull(), file)
        val fileToUpload =
            MultipartBody.Part.createFormData("filename", file.name, requestBody)
        val filename =
            RequestBody.create("text/plain".toMediaTypeOrNull(), imgname)
        val getResponse: MultiInterfaceClient = retrofit.create(MultiInterfaceClient::class.java)
        val call: Call<String> = getResponse.uploadImage(fileToUpload, alert_namess,fullname,selectedItem,selectedItem2,setLevel,mssidn,user,setLoc,addnotes, filename)
        Log.d("assss", imgname)
        call.enqueue(object : Callback<String?> {
           override fun onResponse(
                @NonNull call: Call<String?>,
                @NonNull response: Response<String?>) {
               if (response.isSuccessful) {
                   val remoteResponse = response.body()!!
                   Log.d("test", remoteResponse)
                 parseLoginData(remoteResponse)
                   mProgress?.dismiss()

               } else {
                   mProgress?.dismiss()
                   dialogue_error();
                   promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                   Log.d("BAYO", response.code().toString())
                   btnLogin!!.text = "Submit"
                   mProgress?.dismiss()
               }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.d("TAG", "File Saved::--->" + t.toString())

            }


        })
    }




    private fun uploadImages() {
        for (str in spinner_2!!.selectedItems) {

            pref =
                this.getSharedPreferences("MyPref", 0) // 0 - for private mode

            fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

            mssidn = pref!!.getString("mssdn", null)

            user = pref!!.getString("userid", null)

            addnotes = notes!!.text.toString().trim { it <= ' ' }




            alert_namess = textInputAlert!!.editText!!.text.toString().trim { it <= ' ' }

            setLevel = textInputlevel!!.editText!!.text.toString().trim { it <= ' ' }
            setLoc = textInputLocation!!.editText!!.text.toString().trim { it <= ' ' }

            //RequestBody body = RequestBody.Companion.create(json, JSON)\\\

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor) //.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
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
            params["alert_name"] = alert_namess!!
            params["fullname"] = fullname!!
            params["alert_type"] = "NULL"
            params["rg"] = str.toString()
            params["rl"] = "Level 1"
            params["mssdn"] = mssidn!!
            params["userid"] = user!!
            params["location"] = setLoc!!
            params["notes"] = addnotes!!

            //  Toast.makeText(this@CreateAlert, "" + alert_namess , Toast.LENGTH_LONG).show();

            val api: AddAlertClient = retrofit.create(AddAlertClient::class.java)
            val call: Call<ResponseBody> = api.addAlert(params)

            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
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
    }



    private fun parseLoginData2(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)

            if (jsonObject.getString("status") == "true") {
                    // ImageUploadToServerFunction()
                    mProgress?.dismiss()
                    //waitingDialog!!.dismiss()
                    btnLogin!!.text = "Submit"

                    imageView!!.visibility = View.GONE
                    dialogue();
                    promptPopUpView?.changeStatus(2, "SUCCESSFUL")

            } else {

                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //Log.d("BAYO", response.code().toString())
                btnLogin!!.text = "Submit"
                level  !!.setText("")
                loc!!.setText("")

                mProgress?.dismiss()

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun uploadImage2(path: String) {
        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode

        fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

        mssidn = pref!!.getString("mssdn", null)

        user = pref!!.getString("userid", null)

        addnotes = notes!!.text.toString().trim { it <= ' ' }

        alert_namess = textInputAlert!!.editText!!.text.toString().trim { it <= ' ' }
        setLevel = textInputlevel!!.editText!!.text.toString().trim { it <= ' ' }
        setLoc = textInputLocation!!.editText!!.text.toString().trim { it <= ' ' }

        //RequestBody body = RequestBody.Companion.create(json, JSON)\\\
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES) // write timeout
            .readTimeout(2, TimeUnit.MINUTES) // read timeout
            .addInterceptor(interceptor) //.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .addNetworkInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                    val request: Request =
                        chain.request().newBuilder() // .addHeader(Constant.Header, authToken)
                            .build()
                    return chain.proceed(request)
                }
            }).build()
        val imgname = Calendar.getInstance().timeInMillis.toString()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        //Create a file object using file path
        val file = File(path)
        // Parsing any Media type file
        val requestBody =
            RequestBody.create("*/*".toMediaTypeOrNull(), file)
        val fileToUpload =
            MultipartBody.Part.createFormData("filename", file.name, requestBody)
        val filename =
            RequestBody.create("text/plain".toMediaTypeOrNull(), imgname)
        val getResponse: MultiInterfaceStation = retrofit.create(MultiInterfaceStation::class.java)
        val call: Call<String> = getResponse.uploadImage(fileToUpload, alert_namess,fullname,selectedItem,selecteditem3,setLevel,mssidn,user,setLoc,addnotes, filename)
        Log.d("assss", imgname)
        call.enqueue(object : Callback<String?> {
            override fun onResponse(
                @NonNull call: Call<String?>,
                @NonNull response: Response<String?>) {
                if (response.isSuccessful) {
                    val remoteResponse = response.body()!!
                    Log.d("test", remoteResponse)

                    mProgress?.dismiss()
                    //waitingDialog!!.dismiss()
                    btnLogin!!.text = "Submit"

                    imageView!!.visibility = View.GONE
                    dialogue();
                    promptPopUpView?.changeStatus(2, "SUCCESSFUL")

                } else {
                    mProgress?.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    btnLogin!!.text = "Submit"
                    mProgress?.dismiss()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.d("TAG", "File Saved::--->" + t.toString())

            }


        })
    }




    private fun uploadImages2() {

        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode

        fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

        mssidn = pref!!.getString("mssdn", null)

        user = pref!!.getString("userid", null)

        addnotes = notes!!.text.toString().trim { it <= ' ' }




        alert_namess = textInputAlert!!.editText!!.text.toString().trim { it <= ' ' }

        setLevel = textInputlevel!!.editText!!.text.toString().trim { it <= ' ' }
        setLoc = textInputLocation!!.editText!!.text.toString().trim { it <= ' ' }

        //RequestBody body = RequestBody.Companion.create(json, JSON)\\\

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor) //.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
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
        params["alert_name"] = alert_namess!!
        params["fullname"] = fullname!!
        params["alert_type"] = "NULL"
        params["rg"] = selecteditem3!!
        params["rl"] = "Level 1"
        params["mssdn"] = mssidn!!
        params["userid"] = user!!
        params["location"] = setLoc!!
        params["notes"] = addnotes!!

        //  Toast.makeText(this@CreateAlert, "" + alert_namess , Toast.LENGTH_LONG).show();

        val api: AddAlertStation = retrofit.create(AddAlertStation::class.java)
        val call: Call<ResponseBody> = api.addAlert(params)

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
                    parseLoginData2(remoteResponse)
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

   fun saveImage(myBitmap: Bitmap): String? {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            Environment.getExternalStorageDirectory()
                .toString() + IMAGE_DIRECTORY
        )
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }
        try {
            val f = File(
                wallpaperDirectory, Calendar.getInstance()
                    .timeInMillis.toString() + ".jpg" )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                this,
                arrayOf(f.path),
                arrayOf("image/jpeg"),
                null
            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }



}





