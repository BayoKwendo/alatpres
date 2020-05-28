package com.alat.ui.activities

import adil.dev.lib.materialnumberpicker.dialog.LevelDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.AddAlert
import com.alat.interfaces.GetRGs
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
import java.util.concurrent.TimeUnit


class CreateAlert : AppCompatActivity() {
    private val ITEMS = arrayOf(
        "Suspicious Activity [SA]",
        "Suspicious Person(s) [SP]", "Burglary/Robbery [BG]",
        "Fraud/Vandalism[FRAUD]", "Disturbing of Peace",
        "Riot/Demonstrations [RT]",
        "Terrorism [TM]", "Industrial Accident [ACC]", "Traffic Incidence [TI]",
        "Drugs & Alcohol [DRUG]"
    )

    var spinner: MaterialSpinner? = null
    var spinner_2: MaterialSpinner? = null
    var selectedItem: String? = null

    var selectedItem2: String? = null
    private var promptPopUpView: PromptPopUpView? = null


    var AUTOCOMPLETE_REQUEST_CODE = 1

    var PICK_FILE_REQUEST = 100;

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

    private var alert_namess: String? = null

    private var setLoc: String? = null

    private var fullname: String? = null
    private var mssidn: String? = null
    private var userid: String? = null

    private var addnotes: String? = null


    var pref: SharedPreferences? = null



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

        //Adapters
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ITEMS)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        textInputLocation = findViewById(R.id.alertname)
        alert = findViewById(R.id.alert)

        textInputAlert = findViewById(R.id.location)
        notes = findViewById(R.id.notes)

        loc = findViewById(R.id.loc)

//        loc!!.setOnClickListener {
//            searchPlace()
//        }
//        loc!!.requestFocus()
//        textInputLocation!!.setOnClickListener {
//            searchPlace()
//        }


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

            val intent = Intent()
            intent.type = "*/*"
            //allows to select data and return it
            intent.action = Intent.ACTION_GET_CONTENT
            //starts new activity to select file and return data
            startActivityForResult(
                Intent.createChooser(
                    intent,
                    "Choose File to Upload.."
                ), PICK_FILE_REQUEST
            )
        })
        btnLogin = findViewById(R.id.buttonLogin)


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

        getStudent()


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
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).setCountry("KE") //KENYA
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }


    private fun getStudent() {

        val group_name: String? = null
        val alerts = 0
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
        val api: GetRGs = retrofit.create(GetRGs::class.java)
        val call: Call<String>? = api.getRG(group_name, alerts)

        call?.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                Log.d("Responsestring", response.body().toString())
                //Toast.makeText()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Log.d("onSuccess", response.body().toString())
                        val jsonresponse = response.body().toString()
                        parseLogiData(jsonresponse)
                    } else {

                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        ) //Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("bayo", response.errorBody()!!.string())
//
//                    internet()
//                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")


                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                //   btn.text = "Proceed"
                Log.i("onEmptyResponse", "" + t) //

//                internet()
//                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //mProgress?.dismiss()
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
                catList.add(jsonobject.getString("group_name"))
            }

            val adapter_2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, catList)
            adapter_2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner_2 = findViewById<View>(R.id.spinner2) as MaterialSpinner
            spinner_2?.adapter = adapter_2
            spinner_2!!.isSelected = false;  // otherwise listener will be called on initialization
            spinner_2!!.setSelection(0, true)
            spinner_2?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>?, arg1: View?,
                    arg2: Int, arg3: Long
                ) {
                    if (spinner_2!!.selectedItem == null) {
                        // Toast.makeText(this@CreateAlert, "Please select an RG", Toast.LENGTH_LONG).show();
                        return
                    } else {
                        selectedItem2 = spinner_2!!.selectedItem.toString()
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
        if (Utils.checkIfEmptyString(setLevel)) {

            textInputlevel!!.error = "Setting Level Is Mandatory"
            textInputlevel!!.requestFocus()
            showKeyBoard()

          //showKeyBoard()
            return false

        } else textInputlevel!!.error = null

//        if(selectedItem == null){
//            Toast.makeText(this@CreateAlert, "Type of Alert is Mandatory!! Please select", Toast.LENGTH_LONG).show()
//            return false
//        }

        if(selectedItem2 == null){
            Toast.makeText(this@CreateAlert, "Select Response Group!! Please select", Toast.LENGTH_LONG).show()
            return false
        }

        if (Utils.checkIfEmptyString(setLoc)) {
            textInputLocation!!.error = "Location is Mandatory"
            textInputLocation!!.requestFocus()
          //showKeyBoard()
            return false
        } else textInputLocation!!.error = null
        return true
    }

    private fun bayo() {


        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode

        fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

        mssidn = pref!!.getString("mssdn", null)


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
        params["rg"] = selectedItem2!!
        params["rl"] = setLevel!!
        params["mssdn"] = mssidn!!
        params["location"] = setLoc!!
        params["notes"] = addnotes!!

      //  Toast.makeText(this@CreateAlert, "" + alert_namess , Toast.LENGTH_LONG).show();

        val api: AddAlert = retrofit.create(AddAlert::class.java)
        val call: Call<ResponseBody> = api.addAlert(params)

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
                level!!.setText("")
                loc!!.setText("")
                notes!!.setText("")

                alert!!.setText("")
                btnLogin!!.text = "Submit"

            } else{
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
            .setPositiveButton("Add Alert") { _: DialogInterface?, _: Int ->
                //      finish()

            }
            .setNegativeButton("Exit") { _: DialogInterface?, _: Int ->
                //      finish()
                startActivity(Intent(this@CreateAlert, HomePage::class.java))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                destinationAddress = place.name

                loc!!.setText(destinationAddress.toString())


                // lblAddress
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Toast.makeText(this, "Some went wrong. Search again", Toast.LENGTH_SHORT).show()
                // Log.i(TAG, status.getStatusMessage())
            }
        }


//        else if (requestCode == PICK_FILE_REQUEST) {
//                if (data == null) {
//                    //no data present
//                    return
//                }
//                val selectedFileUri: Uri? = data.data
//                selectedFilePath = FilePath.getPath(this, selectedFileUri)
//                Log.i(
//                    FragmentActivity.TAG,
//                    "Selected File Path:$selectedFilePath"
//                )
//                if (selectedFilePath != null && !selectedFilePath.equals("")) {
//                    tvFileName.setText(selectedFilePath)
//                } else {
//                    Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show()
//                }
//            }

    }

//
//    fun uploadFile(selectedFilePath: String): Int {
//        var serverResponseCode = 0
//        val connection: HttpURLConnection
//        val dataOutputStream: DataOutputStream
//        val lineEnd = "\r\n"
//        val twoHyphens = "--"
//        val boundary = "*****"
//        var bytesRead: Int
//        var bytesAvailable: Int
//        var bufferSize: Int
//        val buffer: ByteArray
//        val maxBufferSize = 1 * 1024 * 1024
//        val selectedFile = File(selectedFilePath)
//        val parts = selectedFilePath.split("/").toTypedArray()
//        val fileName = parts[parts.size - 1]
//        return if (!selectedFile.isFile()) {
//            dialog.dismiss()
//            runOnUiThread { tvFileName.setText("Source File Doesn't Exist: $selectedFilePath") }
//            0
//        } else {
//            try {
//                val fileInputStream = FileInputStream(selectedFile)
//                val url = URL(SERVER_URL)
//                connection = url.openConnection() as HttpURLConnection
//                connection.setDoInput(true) //Allow Inputs
//                connection.setDoOutput(true) //Allow Outputs
//                connection.setUseCaches(false) //Don't use a cached Copy
//                connection.setRequestMethod("POST")
//                connection.setRequestProperty("Connection", "Keep-Alive")
//                connection.setRequestProperty("ENCTYPE", "multipart/form-data")
//                connection.setRequestProperty(
//                    "Content-Type",
//                    "multipart/form-data;boundary=$boundary"
//                )
//                connection.setRequestProperty("uploaded_file", selectedFilePath)
//
//                //creating new dataoutputstream
//                dataOutputStream = DataOutputStream(connection.getOutputStream())
//
//                //writing bytes to data outputstream
//                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
//                dataOutputStream.writeBytes(
//                    "Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
//                            + selectedFilePath + "\"" + lineEnd
//                )
//                dataOutputStream.writeBytes(lineEnd)
//
//                //returns no. of bytes present in fileInputStream
//                bytesAvailable = fileInputStream.available()
//                //selecting the buffer size as minimum of available bytes or 1 MB
//                bufferSize = Math.min(bytesAvailable, maxBufferSize)
//                //setting the buffer as byte array of size of bufferSize
//                buffer = ByteArray(bufferSize)
//
//                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize)
//
//                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
//                while (bytesRead > 0) {
//                    //write the bytes read from inputstream
//                    dataOutputStream.write(buffer, 0, bufferSize)
//                    bytesAvailable = fileInputStream.available()
//                    bufferSize = Math.min(bytesAvailable, maxBufferSize)
//                    bytesRead = fileInputStream.read(buffer, 0, bufferSize)
//                }
//                dataOutputStream.writeBytes(lineEnd)
//                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
//                serverResponseCode = connection.getResponseCode()
//                val serverResponseMessage: String = connection.getResponseMessage()
//                Log.i(
//                    FragmentActivity.TAG,
//                    "Server Response is: $serverResponseMessage: $serverResponseCode"
//                )
//
//                //response code of 200 indicates the server status OK
//                if (serverResponseCode == 200) {
//                    runOnUiThread { tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\nhttp://coderefer.com/extras/uploads/$fileName") }
//                }
//
//                //closing the input and output streams
//                fileInputStream.close()
//                dataOutputStream.flush()
//                dataOutputStream.close()
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(this@NewClassPdf, "File Not Found", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            } catch (e: MalformedURLException) {
//                e.printStackTrace()
//                Toast.makeText(this@NewClassPdf, "URL error!", Toast.LENGTH_SHORT).show()
//            } catch (e: IOException) {
//                e.printStackTrace()
//                Toast.makeText(this@NewClassPdf, "Cannot Read/Write File!", Toast.LENGTH_SHORT)
//                    .show()
//            }
//            dialog.dismiss()
//            serverResponseCode
//        }
}




