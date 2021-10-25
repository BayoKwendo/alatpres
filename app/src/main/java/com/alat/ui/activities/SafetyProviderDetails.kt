package com.alat.ui.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Typeface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder
import cafe.adriel.androidaudiorecorder.TypefaceHelper
import cafe.adriel.androidaudiorecorder.model.AudioChannel
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate
import cafe.adriel.androidaudiorecorder.model.AudioSource
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.FileUtils
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.GetSafetyProvider
import com.alat.interfaces.MultiInterfaceSafety
import com.alat.interfaces.SendAlatRequest
import com.alat.ui.activities.mpesa.MPESAC2B
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
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

class SafetyProviderDetails : AppCompatActivity() {

    var mid: String? = null
    var mToolbar: Toolbar? = null
    val catList: ArrayList<String> = ArrayList()
    var updateFNamee: MaterialEditText? = null
    var alert: android.app.AlertDialog? = null
    var spinner_select: MaterialSpinner? = null
    private var mfile: ImageView? = null
    var path: String? = null
    private val IMAGE_DIRECTORY = "/demonuts_upload_gallery"

    //textview declaration
    var msafety_providdr_name: TextView? = null
    var mservices: TextView? = null
    var malatpres_id: TextView? = null
    var mareas_of_operations: TextView? = null
    var mRGs: TextView? = null
    var imageView: ImageView? = null
    var SEARCHPLACE = 5
    private var mattach: TextView? = null
    private var GALLERY2 = 1
    var bitmap: Bitmap? = null

    //MaterialEdit initialization
    var mcontactName: MaterialEditText? = null
    var mmsisdn: MaterialEditText? = null
    var mlocation: MaterialEditText? = null
    var mtype_alat: MaterialEditText? = null
    private var DOCUMENTS = 1
    private var mProgress: ProgressDialog? = null

    //strings
    var safety_providdr_name: String? = null
    var services: String? = null
    var areas_of_operations: String? = null
    var url: String? = null
    var RGs: String? = null
    var msisdn_provider: String? = null
    var alatpres_id: String? = null

    var contactName: String? = null
    var msisdn: String? = null
    var location: String? = null
    var type_alat: String? = null
    var user: String? = null

    //button
    var mproceed: Button? = null
    private val REQUEST_RECORD_AUDIO = 0

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

                            alatpres_id =jsonObject.getString("userid")

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
        alertDialog.setTitle("Select Preferred Service Access Option")

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


                        val i =
                            Intent(this@SafetyProviderDetails, MPESAC2B::class.java)
                        i.putExtra("price", "50")
                        i.putExtra("service_redirecting_url", url)
                        i.putExtra("is_account_paid", true)
                        startActivity(i)

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


    fun sendRequest() {
        val alertDialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        alertDialog.setTitle("Send Alat/Request")

        val inflater: LayoutInflater =
            getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val layout_pwd: View =
            inflater.inflate(R.layout.layout_send_alat, null)
        alertDialog.setView(layout_pwd)
        alert = alertDialog.create()


        mcontactName = layout_pwd.findViewById<View>(R.id.etName) as MaterialEditText
        mmsisdn = layout_pwd.findViewById<View>(R.id.etPhone) as MaterialEditText
        mlocation = layout_pwd.findViewById<View>(R.id.location) as MaterialEditText
        mtype_alat = layout_pwd.findViewById<View>(R.id.type_alat) as MaterialEditText
        mattach = layout_pwd.findViewById<View>(R.id.attach) as TextView

//         = findViewById(R.id.attach)


        imageView = layout_pwd.findViewById<View>(R.id.imageView) as ImageView

        imageView!!.visibility = View.GONE


        mfile = layout_pwd.findViewById<View>(R.id.file) as ImageView

        mfile!!.setOnClickListener(View.OnClickListener { v: View? ->
            attachemts()
        }
        )

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
            msisdn = mmsisdn!!.text.toString()
//            updateID = updateIDD!!.getText()!!.toString()
            // updateAlatpressID = updateAlatpressIDD!!.getText().toString()
            location = mlocation!!.text.toString()
            type_alat = mtype_alat!!.getText().toString()


            if (Utils.checkIfEmptyString(type_alat)) {
                mtype_alat!!.error = "Type Alat Is Mandatory"
                mtype_alat!!.requestFocus()
            } else {
                waitingDialog.show()

                if (path == null) {

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
                    params["user_id"] = alatpres_id!!

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
                                        alert!!.dismiss();
                                        promptPopUpView?.changeStatus(
                                            2,
                                            "Your request has been sent successfully. The provider will reach out to you within the shortest time. Thank you"
                                        )
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
                } else {

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
                    val getResponse: MultiInterfaceSafety = retrofit.create(MultiInterfaceSafety::class.java)
                    val call: Call<String> = getResponse.uploadImage(
                        fileToUpload,
                        contactName!!,
                        msisdn!!,
                        location!!,
                        type_alat!!,
                        msisdn_provider!!,
                        alatpres_id,
                        filename
                    )
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
                                        dialogue();
                                        alert!!.dismiss();
                                        waitingDialog.dismiss()

                                        promptPopUpView?.changeStatus(
                                            2,
                                            "Your request has been sent successfully. The provider will reach out to you within the shortest time. Thank you"
                                        )
                                        mProgress?.dismiss()
                                    } else {
                                        dialogue_error();
                                        alert!!.dismiss();
                                        waitingDialog.dismiss()

                                        promptPopUpView?.changeStatus(
                                            1,
                                            "Error! please check your image and try again"
                                        )
                                        mProgress?.dismiss()
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            } else {
                                mProgress?.dismiss()
                                dialogue_error();
                                waitingDialog.dismiss()
                                alert!!.dismiss();
                                promptPopUpView?.changeStatus(
                                    1,
                                    "Something went wrondddddddddddg. Try again"
                                )
                                Log.d("BAYO", response.code().toString())
                                mProgress?.dismiss()
                            }
                        }

                        override fun onFailure(call: Call<String?>, t: Throwable) {
                            Log.d("TAGSS", "File Saved::--->" + t.toString())
                        }
                    })
                }
            }

            //RequestBody body = RequestBody.Companion.create(json, JSON)\\\


        })
        val dismissButton: Button =
            layout_pwd.findViewById<View>(R.id.cancel) as Button
        dismissButton.setOnClickListener(View.OnClickListener { alert!!.dismiss() })
        alertDialog.setView(layout_pwd)
        alert!!.show()
    }
    //RequestBody body = RequestBody.Companion.create(json, JSON)\\\


    private fun browseDocuments() {
        val chooseFile: Intent
        val intent: Intent
        chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
        chooseFile.type = "application/pdf"
        intent = Intent.createChooser(chooseFile, "Choose a file")
        startActivityForResult(intent, DOCUMENTS)
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
                        this@SafetyProviderDetails,
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
                mattach!!.text = "Document is attached successfully!! You now can submit your alat"

//                mattach!!.text = "Document is attached successfully!! You now can submit your alert"
                val uri: Uri = I!!.getData()!!
                val filePathFromUri = FileUtils.getRealPath(this, uri)
                val file = File(filePathFromUri)
                path = file.absolutePath

//                p ath = pickiT!!.getPath(I!!.data, Build.VERSION.SDK_INT).toString();
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
