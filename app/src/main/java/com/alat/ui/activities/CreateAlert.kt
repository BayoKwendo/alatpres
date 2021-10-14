package com.alat.ui.activities

import adil.dev.lib.materialnumberpicker.dialog.LevelDialog
import android.annotation.SuppressLint
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
import android.os.Handler
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
import com.alat.adapters.SimpleArrayListAdapter
import com.alat.adapters.SimpleListAdapter
import com.alat.helpers.*
import com.alat.helpers.Constants.API_BASE_URL
import com.alat.interfaces.*
import com.bytcode.lib.spinner.multiselectspinner.data.KeyPairBoolData
import com.bytcode.lib.spinner.multiselectspinner.spinners.MultiSpinnerSearch
import com.bytcode.lib.spinner.multiselectspinner.spinners.SingleSpinner
import com.bytcode.lib.spinner.multiselectspinner.spinners.SingleSpinnerSearch
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputLayout
import com.hbisoft.pickit.PickiT
import com.rengwuxian.materialedittext.MaterialEditText
import dmax.dialog.SpotsDialog
import fr.ganfra.materialspinner.MaterialSpinner
import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner
import gr.escsoft.michaelprimez.searchablespinner.interfaces.IStatusListener
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener
import kotlinx.coroutines.*
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


class CreateAlert : AppCompatActivity() {
    private val ITEMS = arrayOf(
        "Suspicious Activity [SA]",
        "Suspicious Person(s) [SP]", "Burglary/Robbery [BG]", "CyberCrime[CC]",
        "Fraud/Vandalism[FRAUD]", "Disturbing of Peace[DOF]",
        "Riot/Demonstrations [RT]",
        "Terrorism [TM]", "Industrial Accident [ACC]", "Traffic Incidence [TI]",
        "Drugs & Alcohol [DRUG]",
        "Fire [FR]",
        "Medical Emergency [MED]",
        "Natural Disaster [ND]",
        "Gender Violence[GV]",
        "Environmental Crime[EC]",
        "Public Health Concern[PH]",
        "Poaching & Wildlife[PW]",
        "Domestic Violence/Homicide [DV]",
        "Sex Crime/Rape [RAPE]",
        "Murder Case [MDR]",
        "General Violence [GV]",
        "Bribery [BR]",
        "Illegal Business [IB]",
        "Child Abuse [CA]",
        "Female Genital Mutilation [FGM]",
        "Prostitution/Pornography [PHY]",
        "Kidnapping [KID]",
        "Gambling [GAME]"
    )


    private val ITEMS_ALERT = arrayOf(
        "to Response Group",
        "to Response Provider"
    )

    var mySpinner: MultiSelectionSpinner? = null

    var spinner: MaterialSpinner? = null
    private var mSearchableSpinner1: SearchableSpinner? = null
    private var mSimpleListAdapter: SimpleListAdapter? = null
    var spinner3: MaterialSpinner? = null

    var spinner_2: MaterialSpinner? = null
    var spinner_select: MaterialSpinner? = null

    var selectedItem: String? = null
    var pickiT: PickiT? = null

    var selectedItem2: String? = null
    private var promptPopUpView: PromptPopUpView? = null
    private val IMAGE_DIRECTORY = "/demonuts_upload_gallery"

    var bitmap: Bitmap? = null

    var path: String? = null
    var orgphone: String? = null
    var alertss: android.app.AlertDialog? = null

    var SelectImageGallery: Button? = null
    var UploadImageServer: Button? = null

    var imageView: ImageView? = null
    private var mSimpleArrayListAdapter: SimpleArrayListAdapter? = null


    private val mStrings: ArrayList<String> = ArrayList()


    var imageName: EditText? = null

    var progressDialog: ProgressDialog? = null

    var response_provider: String? = null

    var ImageName = "image_name"

    var ImagePath = "image_path"
    var ServerUploadPath = "https://youthsofhope.co.ke/api/upLoad.php"
    var SEARCHPLACE = 5
    var listString: String? = null

    //    private var mProgressfetch: ProgressDialog? = null
//    var PICK_FILE_REQUEST = 100;
    private var textInputLocation: TextInputLayout? = null
    private var textInputAlert: TextInputLayout? = null
    private var alert: EditText? = null
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
    var pref: SharedPreferences? = null
    private var GALLERY2 = 1
    private var DOCUMENTS = 1
    var fname: String? = null
    var user: String? = null

    var textLabel: TextView? = null
    val listofVehicleNames = arrayListOf<String>()

    var check: Boolean? = null

    var mrelative: RelativeLayout? = null

    private val REQUEST_RECORD_AUDIO = 0
    private var account: String? = null
    private var roleID: String? = null
    private val mID: ArrayList<String> = ArrayList()
    private var mSimpleListAdapter7: IDListAdapter? = null
    var selectedRG: ArrayList<String>? = null
    var searchSpinner: MultiSpinnerSearch ? = null
    var  marraylist: List<KeyPairBoolData> = ArrayList()

    val catList: ArrayList<String> = ArrayList()

    var mstatus: String? = null
    private var mProgress: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createalert)
        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Saving...")
        mProgress!!.setCancelable(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (!Places.isInitialized()) {
            Places.initialize(this, application.getString(R.string.google_maps_key))
        }

        searchSpinner =
            findViewById<View>(R.id.searchMultiSpinnerUnlimited) as MultiSpinnerSearch

        mrelative =
            findViewById<View>(R.id.searchMultiSpinnerUnlimitedLayout) as RelativeLayout



        pref =
            getSharedPreferences("MyPref", 0) // 0 - for private mode
        account = pref!!.getString("account_status", null)
        roleID = pref!!.getString("role", null)
        user = pref!!.getString("userid", null)
        mstatus = pref!!.getString("mstatus", null)

        getStudents()
        //Adapters
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ITEMS)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)



        mySpinner = findViewById(R.id.nature_response)

        textLabel = findViewById(R.id.rg_label)

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

        imageView = findViewById(R.id.imageView);
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

        mSimpleListAdapter = SimpleListAdapter(this, mStrings)
        mSimpleArrayListAdapter = SimpleArrayListAdapter(this, mStrings)
        mSimpleListAdapter7 = IDListAdapter(this, mID)

        mSearchableSpinner1 = findViewById<View>(R.id.SearchableSpinner1) as SearchableSpinner

        mSearchableSpinner1!!.setAdapter(mSimpleListAdapter)
        mSearchableSpinner1!!.setOnItemSelectedListener(mOnItemSelectedListener1)
        mSearchableSpinner1!!.setStatusListener(object : IStatusListener {
            override fun spinnerIsOpening() {

            }

            override fun spinnerIsClosing() {}
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
        getStudent()

        selectAlert()

    }


    fun selectAlert() {
        val alertDialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        alertDialog.setTitle("Alat Destination")

        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val layout_pwd: View =
            inflater.inflate(R.layout.layout_select_alert, null)
        alertDialog.setView(layout_pwd)

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, ITEMS_ALERT)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        alertsss = alertDialog.create()
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
                    Toast.makeText(this@CreateAlert, "Create Alat", Toast.LENGTH_LONG).show();
                    return
                } else {
                    val selectedItem = spinner_select!!.selectedItem.toString()

                    if (selectedItem == "to Response Group") {

                        findViewById<View>(R.id.spinner4).visibility = View.GONE

                        findViewById<View>(R.id.SearchableSpinner1).visibility = View.GONE

                        findViewById<View>(R.id.spinner2).visibility = View.GONE

                        mrelative!!.visibility = View.GONE

                        mySpinner!!.visibility = View.VISIBLE
                        textLabel!!.visibility = View.VISIBLE
                        alertsss!!.dismiss()


                    } else {
                        findViewById<View>(R.id.SearchableSpinner1).visibility = View.GONE
                        mrelative!!.visibility = View.VISIBLE


                        findViewById<View>(R.id.spinner2).visibility = View.GONE

                        findViewById<View>(R.id.spinner4).visibility = View.GONE

                        mySpinner!!.visibility = View.GONE
                        textLabel!!.visibility = View.GONE
                        alertsss!!.dismiss()

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
            alertsss!!.dismiss()


        })
        val dismissButton: Button =
            layout_pwd.findViewById<View>(R.id.cancel) as Button
        dismissButton.setOnClickListener(View.OnClickListener {
            alertsss!!.dismiss()
            startActivity(Intent(this@CreateAlert, HomePage::class.java))


        })
        alertDialog.setView(layout_pwd)
        alertsss!!.setCancelable(false)
        alertsss!!.show()

    }


    fun selectAlertDestination() {
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


    private val mOnItemSelectedListener1: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(view: View?, position: Int, id: Long) {
            if (position > 0) {
                response_provider = mSimpleListAdapter!!.getItem(position).toString()
//                orgphone = mSimpleListAdapter7!!.getItem(position).toString()

                // Toast.makeText(this@CreateAlert, "VALUE" + orgphone, Toast.LENGTH_LONG).show();

            }


        }

        override fun onNothingSelected() {
            Toast.makeText(this@CreateAlert, "Nothing Selected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getStudents() {

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

        val api: GetResponseProviders = retrofit.create(GetResponseProviders::class.java)
        val call: Call<ResponseBody> = api.GetAlert(params)

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
                        parseLogiDatas(remoteResponse)
                    } else {
//                        mProgressLayout!!.visibility = View.GONE
                        dialogue_error1();
                        promptPopUpView?.changeStatus(3, "No data in response provider database.")
                    }
                } else {
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
            }
        })
    }


    private fun parseLogiDatas(jsonresponse: String) {
        try {
            val o = JSONObject(jsonresponse)
            val array: JSONArray = o.getJSONArray("records")
            //  val array: JSONArray = JSONArray(jsonresponse)
            val jsonarray = JSONArray(array.toString())
            for (i in 0 until jsonarray.length()) {
                val jsonobject: JSONObject = jsonarray.getJSONObject(i)

                mStrings.add (jsonobject.getString("firstname") + ", " + jsonobject.getString("county") + ",\n"
                        + jsonobject.getString("town") + ",\n"
                        + jsonobject.getString("nature_response") + "\n"
                        +"/"+jsonobject.getString("mssdn")
                )
                mID.add(jsonobject.getString("mssdn"))
            }

            val listArray: MutableList<KeyPairBoolData> = ArrayList()
            for (i in 0 until mStrings.size) {
                val h = KeyPairBoolData()
                h.id = (i + 1).toLong()
                h.name = mStrings[i]
//                h.phone = mID[i]
                h.isSelected = false
                listArray.add(h)
            }
            searchSpinner!!.setItems( listArray, -1 ) { items ->



//                Toast.makeText(this,  searchSpinner!!.selectedItem.toString().substringAfterLast("/") , Toast.LENGTH_LONG).show();


                for (i in items.indices) {
                    if (items[i].isSelected) {
                        listofVehicleNames.add(items[i].name.substringAfterLast("/"))
                        orgphone = listofVehicleNames.toString()


//                        var songs: Array<String> = arrayOf()
//
//                        fun add(input: String) {
//                            songs += input
//                        }

//                        msisdn
//                        sendAlert()
//                        Toast.makeText(this,  items[i].name.substringAfterLast("/") , Toast.LENGTH_LONG).show();
//                        Log.i(TAG, i.toString() +  " : " + items[i].name + " : " + items[i].isSelected)
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
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
            ) {
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(galleryIntent, GALLERY2)
                pDialog.dismiss()
            }
            .addButton(
                "PDF Documents",
                R.color.pdlg_color_white,
                R.color.colorPrimary
            ) {
                browseDocuments()
                pDialog.dismiss()
            }
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
            startActivity(Intent(this@CreateAlert, HomePage::class.java))

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
        val intent2 = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).setCountry("KE") //KENYA
            .build(this)
        startActivityForResult(intent2, SEARCHPLACE)
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
        val api: ViewGroups = retrofit.create(ViewGroups::class.java)
        val call: Call<ResponseBody>? = api.viewRG(params)

        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()
                if (response.isSuccessful) {
                    val remoteResponse = response.body()!!.string()
                    parseLogiData(remoteResponse)
                } else {
                    Log.d("bayo", response.errorBody()!!.string())
                    // Toast.makeText(context,"Nothing" +  response.errorBody()!!.string(),Toast.LENGTH_LONG).show();
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                //   btn.text = "Proceed"
                Log.i("onEmptyResponse", "" + t) //
                // Toast.makeText(context,"Nothing ",Toast.LENGTH_LONG).show();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //mProgress?.dismiss()
            }
        })
    }

    @SuppressLint("LogNotTimber")
    private fun parseLogiData(jsonresponse: String) {
        try {
            val o = JSONObject(jsonresponse)
            val array: JSONArray = o.getJSONArray("records")
            //  val array: JSONArray = JSONArray(jsonresponse)
            val jsonarray = JSONArray(array.toString())

            for (i in 0 until jsonarray.length()) {
                val jsonobject: JSONObject = jsonarray.getJSONObject(i)
                catList.add(jsonobject.getString("group_name"))
            }

            mySpinner!!.setItems(catList)
            //data source for drop-down list
            selectedRG = mySpinner!!.selectedItems
            listString = TextUtils.join(", ", mySpinner!!.selectedItems)
            mySpinner!!.setSelection(selectedRG);



//            val CommaSeparated = "item1 , item2 , item3"


//            }
//            val adapter_2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, catList)
//            adapter_2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinner_2 = findViewById<View>(R.id.spinner2) as MaterialSpinner
//            spinner_2?.adapter = adapter_2
//            spinner_2!!.isSelected = false;  // otherwise listener will be called on initialization
//            spinner_2!!.setSelection(0, true)
//            spinner_2?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    arg0: AdapterView<*>?, arg1: View?,
//                    arg2: Int, arg3: Long
//                ) {
//                    if (spinner_2!!.selectedItem == null) {
//                        // Toast.makeText(this@CreateAlert, "Please select an RG", Toast.LENGTH_LONG).show();
//                        selectedItem2 = null
//                    } else {
//                        selectedItem2 = spinner_2!!.selectedItem.toString()
//                        // Toast.makeText(this@NFCWrite, tv, Toast.LENGTH_LONG).show();
//                    }
//                    // TODO Auto-generated method stub
//                }
//
//                override fun onNothingSelected(arg0: AdapterView<*>?) {
//                    // TODO Auto-generated method stub
//                }
//            }
////            val adapter_3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, catList)
////            adapter_3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////
////            spinner3 = findViewById<View>(R.id.spinner4) as MaterialSpinner
////            spinner3?.adapter = adapter_3
////            spinner3!!.isSelected = false;  // otherwise listener will be called on initialization
////            spinner3!!.setSelection(0, true)
////            spinner3?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
////                override fun onItemSelected(
////                    arg0: AdapterView<*>?, arg1: View?,
////                    arg2: Int, arg3: Long
////                ) {
////                    if (spinner3!!.selectedItem != null) {
////                        // Toast.makeText(this@CreateAlert, "Please select an RG", Toast.LENGTH_LONG).show();
////                        selecteditem3 = spinner3!!.selectedItem.toString()
////                    }   // Toast.makeText(this@NFCWrite, tv, Toast.LENGTH_LONG).show();
////                    else{
////                        selecteditem3 = null
////                    }
////
////                    // TODO Auto-generated method stub
////                }
////
////                override fun onNothingSelected(arg0: AdapterView<*>?) {
////                    // TODO Auto-generated method stub
////                }
//            }
//
//
//            val adapter_4 = ArrayAdapter(this, android.R.layout.simple_spinner_item, catList)
//            adapter_4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


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
                Toast.makeText(this@CreateAlert, "Please select an RG", Toast.LENGTH_LONG).show();

            } else {
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
        startActivity(Intent(this@CreateAlert, HomePage::class.java))

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
//            textInputlevel!!.error = "Setting Level Is Mandatory"
//            textInputlevel!!.requestFocus()
//            showKeyBoard()
//            //showKeyBoard()
//            return false
//
//        } else textInputlevel!!.error = null

        if (selectedItem == null) {
            Toast.makeText(
                this@CreateAlert,
                "Type of Alert is Mandatory!! Please select",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

//        if (selectedItem2 == null) {
//            Toast.makeText(
//                this@CreateAlert,
//                "Select Response Group!! Please select",
//                Toast.LENGTH_LONG
//            ).show()
//            return false
//        }

//        if (Utils.checkIfEmptyString(setLoc)) {
//            textInputLocation!!.error = "Location is Mandatory"
//            textInputLocation!!.requestFocus()
//            //showKeyBoard()
//            return false
//        } else textInputLocation!!.error = null
        return true
    }


    private fun parseLoginData(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {
                if (selecteditem3 == null) {
                    if (response_provider == null) {
                        // ImageUploadToServerFunction()
                        mProgress?.dismiss()
                        //waitingDialog!!.dismiss()
                        btnLogin!!.text = "Submit"
                        imageView!!.visibility = View.GONE
                        dialogue();
                        promptPopUpView?.changeStatus(2, "SUCCESSFUL")
                    } else {
                        sendAlert()
                    }
                } else {
                    if (path == null) {
                        uploadImages2()
                    } else {

                        uploadImage2(path!!)
                    }
                }
            } else {

                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //Log.d("BAYO", response.code().toString())
                btnLogin!!.text = "Submit"
                level!!.setText("")
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
            ) { dialog, _ ->
                dialog.dismiss()
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

//        Toast.makeText(this@CreateAlert, "Success!!" , Toast.LENGTH_LONG).show();
//
//
//        promptPopUpView = PromptPopUpView(this)
//
//        AlertDialog.Builder(this)
//            .setPositiveButton("Exit") { _: DialogInterface?, _: Int ->
//
////                finish()
//                // updates()
//
//                Handler().postDelayed(
//                    {
//                        // This method will be executed once the timer is over
//                    },
//                    1000 // value in milliseconds
//                )

        sendOfflineAlert()

//
//            }
//
//            .setCancelable(false)
//            .setView(promptPopUpView)
//            .show().withCenteredButtons()
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

    private fun dialogue_error1() {
        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("Please continue...") { _: DialogInterface?, _: Int ->

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
                    loc!!.setText(place.getName())
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
                    this@CreateAlert,
                    "Error: " + status.getStatusMessage(),
                    Toast.LENGTH_LONG
                ).show()
//                Log.i(TAG, status.getStatusMessage())
            } else if (RQC == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (RC == GALLERY2) {
            if (RQC == RESULT_OK) {
                val contentURI = I!!.data
                try {
                    bitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    if (bitmap != null) {
                        mattach!!.text =
                            "Image is attached successfully!! You now can submit your alert"
//                        Toast.makeText(
//                            this, "Document recorded successfully!", Toast.LENGTH_SHORT).show()
                        imageView!!.setImageBitmap(bitmap)
                        imageView!!.visibility = View.VISIBLE
                        path = saveImage(bitmap!!)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@CreateAlert,
                        e.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        if (RC == REQUEST_RECORD_AUDIO) {
            if (RQC == RESULT_OK) {
                mattach!!.text =
                    "Audio record is attached successfully!! You can now submit your alert"
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


    fun subm() {

        setLevel = textInputlevel!!.editText!!.text.toString().trim { it <= ' ' }
//        Toast.makeText(
//            this,
//            orgphone,
//            Toast.LENGTH_SHORT
//        ).show()

        if(orgphone != null){
            sendAlert()
        }else {
            if (path == null) {
                uploadImages()
            } else {
                uploadImage(path!!)
            }
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

        mProgress!!.show()
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
        if (response_provider == null) {


            for (str in mySpinner!!.selectedItems) {

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
                                chain.request()
                                    .newBuilder() // .addHeader(Constant.Header, authToken)
                                    .build()
                            return chain.proceed(request)
                        }
                    }).build()
                val imgname = Calendar.getInstance().timeInMillis.toString()
                val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
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
                val getResponse: MultiInterface = retrofit.create(MultiInterface::class.java)
                val call: Call<String> = getResponse.uploadImage(
                    fileToUpload,
                    alert_namess,
                    fullname,
                    selectedItem,
                    str.toString(),
                    "Level 1",
                    mssidn,
                    user,
                    setLoc,
                    addnotes,
                    filename
                )
                Log.d("assss", imgname)
                call.enqueue(object : Callback<String?> {
                    override fun onResponse(
                        @NonNull call: Call<String?>,
                        @NonNull response: Response<String?>
                    ) {
                        if (response.isSuccessful) {
                            val remoteResponse = response.body()!!
                            Log.d("test", remoteResponse)
                            // Log.d("TAG", "File Saved::--->" + t.toString())
//                            parseLoginData(remoteResponse)
                            val jsonObject = JSONObject(remoteResponse)
                            if (jsonObject.getString("status") == "true") {
                                if (response_provider == null) {
                                    // ImageUploadToServerFunction()
                                    mProgress?.dismiss()
                                    //waitingDialog!!.dismiss()
                                    btnLogin!!.text = "Submit"
                                    imageView!!.visibility = View.GONE

                                    val interceptor = HttpLoggingInterceptor()
                                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                                    val client: OkHttpClient = OkHttpClient.Builder()
                                        .addInterceptor(interceptor) //.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
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
                                    params["rg"] = str.toString()
                                    params["user_id"] = user!!
                                    params["alert_name"] = alert_namess!!
                                    params["alert_type"] = selectedItem!!
                                    params["rl"] = "Level 1"
                                    params["groupPhone"] = mssidn!!
                                    params["location"] = setLoc!!
                                    params["notes"] = addnotes!!
                                    params["name"] = fullname!!
                                    val api: SendOfflineAlert =
                                        retrofit.create(SendOfflineAlert::class.java)
                                    val call: Call<ResponseBody> = api.sendofflineAlert(params)
                                    call.enqueue(object : Callback<ResponseBody?> {
                                        override fun onResponse(
                                            call: Call<ResponseBody?>,
                                            response: Response<ResponseBody?>
                                        ) { //                }
                                        }

                                        override fun onFailure(
                                            call: Call<ResponseBody?>,
                                            t: Throwable
                                        ) {
                                            btnLogin!!.text = "Submit"
                                            dialogue_error()
                                            promptPopUpView?.changeStatus(
                                                1,
                                                "Something went wrong. Try again"
                                            )
                                            Log.i("onEmptyResponse", "" + t) //
                                            mProgress?.dismiss()
                                        }
                                    })

                                    Handler().postDelayed(
                                        {
                                            startActivity(
                                                Intent(
                                                    this@CreateAlert,
                                                    HomePage::class.java
                                                )
                                            )
                                            // This method will be executed once the timer is over
                                        },
                                        1000 // value in milliseconds
                                    )
                                    Toast.makeText(
                                        this@CreateAlert,
                                        "Alat Created Successful!!",
                                        Toast.LENGTH_LONG
                                    ).show();
                                }
                            } else {
                                mProgress?.dismiss()
                                dialogue_error();
                                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                                Log.d("BAYO", response.code().toString())
                                btnLogin!!.text = "Submit"
                                mProgress?.dismiss()
                            }
                        }

                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Log.d("TAG", "File Saved::--->" + t.toString())

                    }

                })
            }
        } else {
            sendAlert()
        }

    }


    private fun sendAlert() {

        listofVehicleNames.forEach { st ->
//            Toast.makeText(this@CreateAlert,  st.toString(), Toast.LENGTH_LONG).show()


            mProgress!!.show()
            pref =
                this.getSharedPreferences("MyPref", 0) // 0 - for private mode

            fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

            mssidn = pref!!.getString("mssdn", null)

            user = pref!!.getString("userid", null)

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
            //  response_id = intent.getStringExtra("groupSelect")

            params["mssdn"] = st
            params["user_id"] = user!!
            params["alert_name"] = alert_namess!!
            params["alert_type"] = selectedItem!!
            params["rl"] = "Level 1"
            params["groupPhone"] = "254717629732"
            params["location"] = setLoc!!
            params["name"] = fullname!!


            val api: sendSMSOffline = retrofit.create(sendSMSOffline::class.java)
            val call: Call<ResponseBody> = api.send(params)

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

                        if (response.code().toString() == "200") {

                            mProgress?.dismiss()
                            //waitingDialog!!.dismiss()
                            btnLogin!!.text = "Submit"
                            imageView!!.visibility = View.GONE

                            Handler().postDelayed(
                                {
                                    startActivity(
                                        Intent(
                                            this@CreateAlert,
                                            HomePage::class.java
                                        )
                                    )
                                    // This method will be executed once the timer is over
                                },
                                1000 // value in milliseconds
                            )

                            Toast.makeText(this@CreateAlert, "Alat was created successfully", Toast.LENGTH_LONG).show();

                            promptPopUpView?.changeStatus(2, "SUCCESSFUL")

                        } else {
                            dialogue_error()
                            promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                            Log.d("BAYO", response.code().toString())
                            mProgress?.dismiss()
                        }
                    } else {
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
    }


    private fun uploadImages() {

        if (response_provider == null) {
            pref =
                this.getSharedPreferences("MyPref", 0) // 0 - for private mode
            fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)
            mssidn = pref!!.getString("mssdn", null)
            user = pref!!.getString("userid", null)
            addnotes = notes!!.text.toString().trim { it <= ' ' }
            alert_namess = textInputAlert!!.editText!!.text.toString().trim { it <= ' ' }
            setLevel = textInputlevel!!.editText!!.text.toString().trim { it <= ' ' }
            setLoc = textInputLocation!!.editText!!.text.toString().trim { it <= ' ' }

            for (str in mySpinner!!.selectedItems) {
                mProgress?.show()
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                val client: OkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(interceptor) //.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
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
                params["alert_name"] = alert_namess!!
                params["fullname"] = fullname!!
                params["alert_type"] = selectedItem!!
                params["rg"] = str.toString()
                params["rl"] = "Level 1"
                params["mssdn"] = mssidn!!
                params["userid"] = user!!
                params["location"] = setLoc!!
                params["notes"] = addnotes!!

                //  Toast.makeText(this@CreateAlert, "" + alert_namess , Toast.LENGTH_LONG).show();

                val api: AddAlert = retrofit.create(AddAlert::class.java)
                val call: Call<ResponseBody> = api.addAlert(params)

                call.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        if (response.isSuccessful) {
                            val remoteResponse = response.body()!!.string()
                            Log.d("test", remoteResponse)

                            try {
                                val jsonObject = JSONObject(remoteResponse)
                                if (jsonObject.getString("status") == "true") {
                                    if (response_provider == null) {
                                        // ImageUploadToServerFunction()
                                        mProgress?.dismiss()
                                        //waitingDialog!!.dismiss()
                                        btnLogin!!.text = "Submit"
                                        imageView!!.visibility = View.GONE

                                        val interceptor = HttpLoggingInterceptor()
                                        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                                        val client: OkHttpClient = OkHttpClient.Builder()
                                            .addInterceptor(interceptor) //.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
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
                                        params["rg"] = str.toString()
                                        params["user_id"] = user!!
                                        params["alert_name"] = alert_namess!!
                                        params["alert_type"] = selectedItem!!
                                        params["rl"] = "Level 1"
                                        params["groupPhone"] = mssidn!!
                                        params["location"] = setLoc!!
                                        params["notes"] = addnotes!!
                                        params["name"] = fullname!!
                                        val api: SendOfflineAlert =
                                            retrofit.create(SendOfflineAlert::class.java)
                                        val call: Call<ResponseBody> = api.sendofflineAlert(params)
                                        call.enqueue(object : Callback<ResponseBody?> {
                                            override fun onResponse(
                                                call: Call<ResponseBody?>,
                                                response: Response<ResponseBody?>
                                            ) { //                }
                                            }

                                            override fun onFailure(
                                                call: Call<ResponseBody?>,
                                                t: Throwable
                                            ) {
                                                btnLogin!!.text = "Submit"
                                                dialogue_error()
                                                promptPopUpView?.changeStatus(
                                                    1,
                                                    "Something went wrong. Try again"
                                                )
                                                Log.i("onEmptyResponse", "" + t) //
                                                mProgress?.dismiss()
                                            }
                                        })

                                        Handler().postDelayed(
                                            {
                                                startActivity(
                                                    Intent(
                                                        this@CreateAlert,
                                                        HomePage::class.java
                                                    )
                                                )
                                                // This method will be executed once the timer is over
                                            },
                                            1000 // value in milliseconds
                                        )




                                        Toast.makeText(
                                            this@CreateAlert,
                                            "Alat Created Successful!!",
                                            Toast.LENGTH_LONG
                                        ).show();
                                    }

                                } else {

                                    dialogue_error();
                                    promptPopUpView?.changeStatus(
                                        1,
                                        "Something went wrong. Try again"
                                    )
                                    //Log.d("BAYO", response.code().toString())
                                    btnLogin!!.text = "Submit"
                                    level!!.setText("")
                                    loc!!.setText("")

                                    mProgress?.dismiss()

                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
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

//
//            CoroutineScope(Dispatchers.IO).launch {
//                delay(TimeUnit.SECONDS.toMillis(5))
//                withContext(Dispatchers.Main) {
////                    Toast.makeText(this@CreateAlert, "Nothing" + str.toString(), Toast.LENGTH_LONG)
////                        .show();
////                    //RequestBody body = RequestBody.Companion.create(json, JSON)\\\
//
////
//                 }
//            }


            }
        } else {
            sendAlert()
        }
    }


    private fun parseLoginData2(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            Log.d("tesTIMAGE3", jsonObject.toString())

            if (jsonObject.getString("status") == "true") {

                if (response_provider == null) {
                    // ImageUploadToServerFunction()
                    mProgress?.dismiss()
                    //waitingDialog!!.dismiss()
                    btnLogin!!.text = "Submit"
                    imageView!!.visibility = View.GONE
                    dialogue();
                    promptPopUpView?.changeStatus(2, "SUCCESSFUL")
                }
            } else {

                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrongssssssssss. Try again")
                //Log.d("BAYO", response.code().toString())
                btnLogin!!.text = "Submit"
                level!!.setText("")
                loc!!.setText("")

                mProgress?.dismiss()

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun uploadImage2(path: String) {

        mProgress!!.show()
        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode

        fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

        mssidn = pref!!.getString("mssdn", null)

        user = pref!!.getString("userid", null)

        addnotes = notes!!.text.toString().trim { it <= ' ' }

        alert_namess = textInputAlert!!.editText!!.text.toString().trim { it <= ' ' }
        setLevel = textInputlevel!!.editText!!.text.toString().trim { it <= ' ' }
        setLoc = textInputLocation!!.editText!!.text.toString().trim { it <= ' ' }
        if (response_provider == null) {


            for (str in mySpinner!!.selectedItems) {
                mProgress?.show()

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
                                chain.request()
                                    .newBuilder() // .addHeader(Constant.Header, authToken)
                                    .build()
                            return chain.proceed(request)
                        }
                    }).build()

                val imgname = Calendar.getInstance().timeInMillis.toString()
                val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
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
                val getResponse: MultiInterface = retrofit.create(MultiInterface::class.java)
                val call: Call<String> = getResponse.uploadImage(
                    fileToUpload,
                    alert_namess,
                    fullname,
                    selectedItem,
                    selecteditem3,
                    setLevel,
                    mssidn,
                    user,
                    setLoc,
                    addnotes,
                    filename
                )
                Log.d("assss", imgname)
                call.enqueue(object : Callback<String?> {
                    override fun onResponse(
                        @NonNull call: Call<String?>,
                        @NonNull response: Response<String?>
                    ) {
                        Log.d("parseLoginData2", response.toString())

                        if (response.isSuccessful) {
                            val remoteResponse = response.body()!!
                            try {
                                val jsonObject = JSONObject(remoteResponse)
                                Log.d("tesTIMAGE3", jsonObject.toString())

                                if (jsonObject.getString("status") == "true") {

                                    if (response_provider == null) {
                                        // ImageUploadToServerFunction()
                                        mProgress?.dismiss()
                                        //waitingDialog!!.dismiss()
                                        btnLogin!!.text = "Submit"
                                        imageView!!.visibility = View.GONE

                                        val interceptor = HttpLoggingInterceptor()
                                        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                                        val client: OkHttpClient = OkHttpClient.Builder()
                                            .addInterceptor(interceptor) //.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
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
                                        params["rg"] = str.toString()
                                        params["user_id"] = user!!
                                        params["alert_name"] = alert_namess!!
                                        params["alert_type"] = selectedItem!!
                                        params["rl"] = "Level 1"
                                        params["groupPhone"] = mssidn!!
                                        params["location"] = setLoc!!
                                        params["notes"] = addnotes!!
                                        params["name"] = fullname!!
                                        val api: SendOfflineAlert =
                                            retrofit.create(SendOfflineAlert::class.java)
                                        val call: Call<ResponseBody> = api.sendofflineAlert(params)
                                        call.enqueue(object : Callback<ResponseBody?> {
                                            override fun onResponse(
                                                call: Call<ResponseBody?>,
                                                response: Response<ResponseBody?>
                                            ) { //                }
                                            }

                                            override fun onFailure(
                                                call: Call<ResponseBody?>,
                                                t: Throwable
                                            ) {
                                                btnLogin!!.text = "Submit"
                                                dialogue_error()
                                                promptPopUpView?.changeStatus(
                                                    1,
                                                    "Something went wrong. Try again"
                                                )
                                                Log.i("onEmptyResponse", "" + t) //
                                                mProgress?.dismiss()
                                            }
                                        })

                                        Handler().postDelayed(
                                            {
                                                startActivity(
                                                    Intent(
                                                        this@CreateAlert,
                                                        HomePage::class.java
                                                    )
                                                )
                                                // This method will be executed once the timer is over
                                            },
                                            1000 // value in milliseconds
                                        )




                                        Toast.makeText(
                                            this@CreateAlert,
                                            "Alat Created Successful!!",
                                            Toast.LENGTH_LONG
                                        ).show();
                                    }
                                } else {

                                    dialogue_error();
                                    promptPopUpView?.changeStatus(
                                        1,
                                        "Something went wrongssssssssss. Try again"
                                    )
                                    //Log.d("BAYO", response.code().toString())
                                    btnLogin!!.text = "Submit"
                                    level!!.setText("")
                                    loc!!.setText("")

                                    mProgress?.dismiss()

                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        } else {
                            mProgress?.dismiss()
                            dialogue_error();
                            promptPopUpView?.changeStatus(
                                1,
                                "Something went wrondddddddddddg. Try again"
                            )
                            Log.d("BAYO", response.code().toString())
                            btnLogin!!.text = "Submit"
                            mProgress?.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Log.d("TAGSS", "File Saved::--->" + t.toString())

                    }


                })
            }
        } else {
            sendAlert()
        }
    }

    private fun sendOfflineAlert() {

        mProgress!!.show()
        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode

        fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

        mssidn = pref!!.getString("mssdn", null)

        user = pref!!.getString("userid", null)

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
        params["rg"] = selectedItem2!!
        params["user_id"] = user!!
        params["alert_name"] = alert_namess!!
        params["alert_type"] = selectedItem!!
        params["rl"] = "Level 1"
        params["groupPhone"] = mssidn!!
        params["location"] = setLoc!!
        params["notes"] = addnotes!!
        params["name"] = fullname!!
        val api: SendOfflineAlert = retrofit.create(SendOfflineAlert::class.java)
        val call: Call<ResponseBody> = api.sendofflineAlert(params)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());
                //Toast.makeText()
//                if (response.isSuccessful) {
//                    val remoteResponse = response.body()!!.string()
//                    Log.d("test", remoteResponse)
//
//                } else {
//                }
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
        params["alert_type"] = selectedItem!!
        params["rg"] = selecteditem3!!

        params["rl"] = "Level 1"
        params["mssdn"] = mssidn!!
        params["userid"] = user!!
        params["location"] = setLoc!!
        params["notes"] = addnotes!!

        //  Toast.makeText(this@CreateAlert, "" + alert_namess , Toast.LENGTH_LONG).show();

        val api: AddAlert = retrofit.create(AddAlert::class.java)
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
                    .timeInMillis.toString() + ".jpg"
            )
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





