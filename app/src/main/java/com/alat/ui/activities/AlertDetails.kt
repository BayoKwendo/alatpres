package com.alat.ui.activities

import android.app.DownloadManager
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
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.*
import com.squareup.picasso.Picasso
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
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class AlertDetails : AppCompatActivity() {

    var mid: String? = null
    var mToolbar: Toolbar? = null


    var rl: String? = null
    val catList: ArrayList<String> = ArrayList()


    var response_group: TextView? = null
    var alert_type: TextView? = null
    var fullnaem: TextView? = null
    var location: TextView? = null

    var createdOn: TextView? = null

    var addNotes: TextView? = null

    var saveImage: Button? = null
    var spinner3: MaterialSpinner? = null
    var selectedItem2: String? = null

    var waitingDialog: android.app.AlertDialog? = null

    private var addnotes: String? = null


    var alertsss: android.app.AlertDialog? = null

    private var promptPopUpView: PromptPopUpView? = null

    var rg: String? = null
    var view: View? = null

    var notes: String? = null

    var alertyp: String? = null
    var usr: String? = null
    var loc: String? = null
    var created: String? = null
    var nutralize: TextView? = null
    var safe: TextView? = null
    var ignore: TextView? = null
    var shout: TextView? = null
    var elevates: TextView? = null
    var mshare: TextView? = null

    var imageView: ImageView? = null

    var url: String? = null
    private var mProgress: ProgressDialog? = null


    var alert_name: String? = null
    var rls: String? = null
    var mssdn: String? = null
    var userid: String? = null


    var ignore_count: String? = null
    var shout_count: String? = null
    var elevate_count: String? = null
    var share_count: String? = null
    var neutral_count: String? = null
    var safe_count: String? = null

    var pref: SharedPreferences? = null

    var mfullname: TextView? = null
    var mname: TextView? = null

    var Vmfullname: String? = null
    var Vmname: String? = null

    var fname: String? = null
    var user: String? = null
    var downloadManager: DownloadManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        mid = intent.getStringExtra("alertSelect")
        rl = intent.getStringExtra("level")

        pref =
            getSharedPreferences("MyPref", 0) // 0 - for private mode
        fname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)
        user = pref!!.getString("userid", null)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        getGroupNames()
        response_group = findViewById(R.id.textView)
        alert_type = findViewById(R.id.textView7)
        fullnaem = findViewById(R.id.textView9)
        addNotes = findViewById(R.id.textView11)

        imageView = findViewById(R.id.image)
        nutralize = findViewById(R.id.neutralized)
        safe = findViewById(R.id.safe)
        ignore = findViewById(R.id.ignore)
        shout = findViewById(R.id.shouts)
        elevates = findViewById(R.id.elevate)
        mshare = findViewById(R.id.share)

        mfullname = findViewById(R.id.name)
        mname = findViewById(R.id.fname)

        saveImage = findViewById(R.id.save)

        location = findViewById(R.id.textView15)
        createdOn = findViewById(R.id.textView21)
        view = findViewById(R.id.view)
        counts()
        if (rl == "Level 3") {
            view!!.setBackgroundColor(ContextCompat.getColor(this, R.color.error))
            ///holder.name.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        } else if (rl == "Level 2") {
            view!!.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
            // holder.name.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        } else if (rl == "Level 1") {
            view!!.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
            //  holder.name.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        } else if (rl == "Neutralized") {
            view!!.setBackgroundColor(ContextCompat.getColor(this, R.color.accentGreen))
            //holder.name.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        }

        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Processing...")
        mProgress!!.setCancelable(false)
        mProgress!!.show()

        saveImage!!.setOnClickListener {
            download()
        }

        nutralize!!.setOnClickListener {
            AlertStatus()
        }

        safe!!.setOnClickListener {
            AlertSafe()
        }



        ignore!!.setOnClickListener {
            AlertIgnore()
        }

        shout!!.setOnClickListener {
            AlertShout()
        }

        elevates!!.setOnClickListener {
            AlertElevate()
        }

        mshare!!.setOnClickListener {
            updates()
        }
        getStudent()

    }


    fun AlertElevate() {

        AlertDialog.Builder(this)
            .setMessage("The situation is out of control and furthur rescue is needed\n\n")
            .setCancelable(true)
            .setPositiveButton("Elevate") { _, id ->
                //  accept()
                elevate()
                mProgress!!.show()
                // startActivity(Intent(this@FriendRequests, HomePage::class.java))
            }
            .setNegativeButton("Cancel") { _, id ->
                // reject()
                //getRGNAME()
            }
            .show().withCenteredButtonss()
    }

    fun AlertShout() {

        AlertDialog.Builder(this)
            .setMessage("I am in danger and i need immediate action\n\n")
            .setCancelable(true)
            .setPositiveButton("Shout!!") { _, id ->
                //  accept()
                shout()
                mProgress!!.show()
                // startActivity(Intent(this@FriendRequests, HomePage::class.java))
            }
            .setNegativeButton("Cancel") { _, id ->
                // reject()
                //getRGNAME()
            }
            .show().withCenteredButtonss()
    }


    fun AlertIgnore() {

        AlertDialog.Builder(this)
            .setMessage(" I am not affected in anyway by the Alert \n\n")
            .setCancelable(true)
            .setPositiveButton("Ignore") { _, id ->
                //  accept()
                ignore()
                mProgress!!.show()
                // startActivity(Intent(this@FriendRequests, HomePage::class.java))
            }
            .setNegativeButton("Cancel") { _, id ->
                // reject()
                //getRGNAME()
            }
            .show().withCenteredButtonss()
    }

    fun download() {
        AlertDialog.Builder(this)
            .setMessage("Download the file?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, id ->
                saveImag()

            }
            .setNegativeButton("No", null)
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


    private fun getStudent() {

        val alert_type: String? = null
        val rg: String? = null
        val name: String? = null
        val locations: String? = null
        val notes: String? = null


        val alert_name: String? = null
        val rl: String? = null
        val mssdn: String? = null
        val userid: String? = null

        val modified: String? = null
        val attachment: String? = null


        val created: String? = null
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
            .build()


        val api: GetAlertPost = retrofit.create(
            GetAlertPost::class.java
        )
        val call: Call<String>? = api.getAlert(
            mid,
            alert_name,
            alert_type,
            rl,
            mssdn,
            userid,
            name,
            rg,
            locations,
            attachment,
            notes,
            created,
            modified
        )
        call?.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                Log.d("Responsestring", response.toString())
                //Toast.makeText()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Log.d("onSuccess", response.body().toString())
                        //Toast.makeText(this@LoginActivity, "Success"  + response.body(), Toast.LENGTH_LONG).show()
                        val jsonresponse = response.body().toString()
                        parseLoginData(jsonresponse)
                    } else {
                        mProgress!!.dismiss()
                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        ) //Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.i("onEmptyResponse", "" + t) //
                mProgress!!.dismiss()
            }
        })
    }


    private fun parseLoginData(jsonresponse: String) {
        try {


            mProgress!!.dismiss()
            val jsonObject = JSONObject(jsonresponse)
            alertyp = jsonObject.getString("alert_type")
            alert_type!!.text = alertyp

            rg = jsonObject.getString("rg")
            response_group!!.text = rg
            usr = jsonObject.getString("fullname")
            fullnaem!!.text = usr

            loc = jsonObject.getString("location")

            location!!.text = loc

            created = jsonObject.getString("created")

            createdOn!!.text = created

            notes = jsonObject.getString("notes")



            alert_name = jsonObject.getString("alert_name")

            rls = jsonObject.getString("rl")


            mssdn = jsonObject.getString("mssdn")


            userid = jsonObject.getString("userid")



            addNotes!!.text = notes


            url = jsonObject.getString("attachment")

            //Toast.makeText(this@AlertDetails, "Success"  + url, Toast.LENGTH_LONG).show()

            if (url != "") {
                saveImage!!.visibility = View.VISIBLE
                imageView!!.visibility = View.VISIBLE
                Picasso.get().load(url).into(imageView)

            }


        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }



    private fun counts() {

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

        params["alert_id"] = mid!!


        val api: AlertCounts = retrofit.create(AlertCounts::class.java)
        val call: Call<ResponseBody> = api.Mark(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {

                    if (response.body() != null) {
                        val remoteResponse = response.body()!!.string()

                        Log.d("test", remoteResponse)
                        parseDetails(remoteResponse)

                        //Toast.makeText(this@ActiveAlerts,"Nothing "  + remoteResponse,Toast.LENGTH_LONG).show();

                    } else {

                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        )
                    }

                } else {

                    dialogue_error();
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

    private fun parseDetails(remoteResponse: String) {
        try {
            val o = JSONObject(remoteResponse)

            val array: JSONArray = o.getJSONArray("records")

            for (i in 0 until array.length()) {
                val jsonObject: JSONObject = array.getJSONObject(i)


                ignore_count = jsonObject.getString("ignore_counts")
                ignore!!.setText("IGNORE (" + ignore_count +" )")


                shout_count = jsonObject.getString("shout_counts")
                shout!!.setText("SHOUT (" + shout_count +" )")


                safe_count = jsonObject.getString("safe_counts")
                safe!!.setText("SAFE (" + safe_count +" )")


                elevate_count = jsonObject.getString("elevate_counts")
                elevates!!.setText("ELEVATE (" + elevate_count +" )")


                share_count = jsonObject.getString("share_counts")
                mshare!!.setText("SHARE (" + share_count +" )")

                Vmfullname = jsonObject.getString("fullname")
                mfullname!!.setText(Vmfullname)

                Vmname = jsonObject.getString("fullname")
                mname!!.setText(Vmname)


                neutral_count = jsonObject.getString("neutralized_counts")
                nutralize!!.setText("NEUTRALIZED (" + neutral_count +" )")

            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }



    fun saveImag() {
        val downloadManager =
            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val reference = downloadManager.enqueue(request)

    }

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


    fun AlertStatus() {

        AlertDialog.Builder(this)
            .setMessage("Alert has been acted upon and address successfully\n\n")
            .setCancelable(true)
            .setPositiveButton("Neutralized") { _, id ->
                //  accept()
                neutralized()
                mProgress!!.show()
                // startActivity(Intent(this@FriendRequests, HomePage::class.java))
            }
            .setNegativeButton("Cancel") { _, id ->
                // reject()
                //getRGNAME()
            }
            .show().withCenteredButtonss()
    }


    fun AlertSafe() {

        AlertDialog.Builder(this)
            .setMessage("I am free from danger\n\n")
            .setCancelable(true)
            .setPositiveButton("Safe") { _, id ->
                //  accept()
                safe()
                mProgress!!.show()
                // startActivity(Intent(this@FriendRequests, HomePage::class.java))
            }
            .setNegativeButton("Cancel") { _, id ->
                // reject()
                //getRGNAME()
            }
            .show().withCenteredButtonss()
    }


    private fun safe() {

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

        params["alert_id"] = mid!!
        params["rg"] = rg!!

        val api: AlertSafwe = retrofit.create(AlertSafwe::class.java)
        val call: Call<ResponseBody> = api.Mark(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {

                    if (response.body() != null) {
                        val remoteResponse = response.body()!!.string()

                        Log.d("test", remoteResponse)
                        parseLoginDataas(remoteResponse)

                        //Toast.makeText(this@ActiveAlerts,"Nothing "  + remoteResponse,Toast.LENGTH_LONG).show();

                    } else {

                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        )
                    }

                } else {

                    dialogue_error();
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

    private fun parseLoginDataas(remoteResponse: String) {
        try {


            val jsonObject = JSONObject(remoteResponse)
            // val jsonObject = JSONObject(jsonresponse)


            if (jsonObject.getString("status") == "true") {

                dialogue();
                promptPopUpView?.changeStatus(2, "Feedback Successfully received!")
                mfullname!!.visibility=View.VISIBLE
                mProgress!!.dismiss()

            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                mProgress!!.dismiss()


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    private fun ignore() {

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

        params["alert_id"] = mid!!
        params["rg"] = rg!!
        params["fullname"] = fname!!
        params["userid"] = user!!

        val api: AlertIgnore = retrofit.create(AlertIgnore::class.java)
        val call: Call<ResponseBody> = api.Mark(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {

                    if (response.body() != null) {
                        val remoteResponse = response.body()!!.string()

                        Log.d("test", remoteResponse)
                        parseLoginDataass(remoteResponse)

                        //Toast.makeText(this@ActiveAlerts,"Nothing "  + remoteResponse,Toast.LENGTH_LONG).show();

                    } else {

                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        )
                    }

                } else {

                    dialogue_error();
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

    private fun parseLoginDataass(remoteResponse: String) {
        try {


            val jsonObject = JSONObject(remoteResponse)
            // val jsonObject = JSONObject(jsonresponse)


            if (jsonObject.getString("status") == "true") {
                dialogue();
                promptPopUpView?.changeStatus(2, "Well received!")

                mProgress!!.dismiss()

            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                mProgress!!.dismiss()


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    private fun shout() {

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

        params["alert_id"] = mid!!
        params["rg"] = rg!!
        params["fullname"] = fname!!
        params["userid"] = user!!

        val api: AlertShout = retrofit.create(AlertShout::class.java)
        val call: Call<ResponseBody> = api.Mark(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {

                    if (response.body() != null) {
                        val remoteResponse = response.body()!!.string()

                        Log.d("test", remoteResponse)
                        parseLoginDataaass(remoteResponse)

                        //Toast.makeText(this@ActiveAlerts,"Nothing "  + remoteResponse,Toast.LENGTH_LONG).show();

                    } else {

                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        )
                    }

                } else {

                    dialogue_error();
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

    private fun parseLoginDataaass(remoteResponse: String) {
        try {


            val jsonObject = JSONObject(remoteResponse)
            // val jsonObject = JSONObject(jsonresponse)


            if (jsonObject.getString("status") == "true") {
                dialogue();
                promptPopUpView?.changeStatus(2, "Successfully reported!!")

                mProgress!!.dismiss()

                mname!!.visibility=View.VISIBLE


            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                mProgress!!.dismiss()


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun neutralized() {

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

        params["alert_id"] = mid!!
        params["rg"] = rg!!
        params["fullname"] = fname!!
        params["userid"] = user!!

        val api: AlertNeutral = retrofit.create(AlertNeutral::class.java)
        val call: Call<ResponseBody> = api.Mark(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {

                    if (response.body() != null) {
                        val remoteResponse = response.body()!!.string()

                        Log.d("test", remoteResponse)
                        parseLoginDataaassss(remoteResponse)

                        //Toast.makeText(this@ActiveAlerts,"Nothing "  + remoteResponse,Toast.LENGTH_LONG).show();

                    } else {

                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        )
                    }

                } else {

                    dialogue_error();
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

    private fun parseLoginDataaassss(remoteResponse: String) {
        try {


            val jsonObject = JSONObject(remoteResponse)
            // val jsonObject = JSONObject(jsonresponse)


            if (jsonObject.getString("status") == "true") {
                dialogue();
                promptPopUpView?.changeStatus(2, "Successfully marked!!")

                mProgress!!.dismiss()

            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                mProgress!!.dismiss()


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    private fun elevate() {

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

        params["alert_id"] = mid!!
        params["rg"] = rg!!
        params["fullname"] = fname!!
        params["userid"] = user!!


        val api: AlertElevate = retrofit.create(AlertElevate::class.java)
        val call: Call<ResponseBody> = api.Mark(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {

                    if (response.body() != null) {
                        val remoteResponse = response.body()!!.string()

                        Log.d("test", remoteResponse)
                        parseLoginDataaasss(remoteResponse)

                        //Toast.makeText(this@ActiveAlerts,"Nothing "  + remoteResponse,Toast.LENGTH_LONG).show();

                    } else {

                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        )
                    }

                } else {

                    dialogue_error();
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

    private fun parseLoginDataaasss(remoteResponse: String) {
        try {


            val jsonObject = JSONObject(remoteResponse)
            // val jsonObject = JSONObject(jsonresponse)


            if (jsonObject.getString("status") == "true") {
                dialogue();
                promptPopUpView?.changeStatus(2, "Successfully reported!!")

                mProgress!!.dismiss()

            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                mProgress!!.dismiss()


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    private fun getGroupNames() {

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
            .addConverterFactory(ScalarsConverterFactory.create())
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


            val jsonarray = JSONArray(array.toString())

            for (i in 0 until jsonarray.length()) {
                val jsonobject: JSONObject = jsonarray.getJSONObject(i)
                catList.add(jsonobject.getString("group_name"))
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }

    fun updates() {

        val alertDialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        alertDialog.setTitle("Share Alert to other Response Groups")

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
        updateButton!!.setText("Share")
        updateButton.setOnClickListener(View.OnClickListener {
            waitingDialog =
                SpotsDialog.Builder().setContext(this).build()
            if (spinner3!!.selectedItem == null) {
                Toast.makeText(this@AlertDetails, "Please select an RG", Toast.LENGTH_LONG).show();

            } else {
                alertsss!!.dismiss()

                uploadImages()
                mProgress!!.show()

                //subm()


            }


        })
        val dismissButton: Button =
            layout_pwd.findViewById<View>(R.id.cancel) as Button
        dismissButton.setOnClickListener(View.OnClickListener {
            alertsss!!.dismiss()


        })
        alertDialog.setView(layout_pwd)
        alertsss!!.setCancelable(false)
        alertsss!!.show()

    }


    private fun share() {

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

        params["alert_id"] = mid!!
        params["rg"] = rg!!
        params["fullname"] = fname!!
        params["userid"] = user!!

        val api: AlertShare = retrofit.create(AlertShare::class.java)
        val call: Call<ResponseBody> = api.Mark(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {

                    if (response.body() != null) {
                        val remoteResponse = response.body()!!.string()

                        Log.d("test", remoteResponse)
                        parseLoginDataasassss(remoteResponse)

                        //Toast.makeText(this@ActiveAlerts,"Nothing "  + remoteResponse,Toast.LENGTH_LONG).show();

                    } else {

                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        )
                    }

                } else {

                    dialogue_error();
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

    private fun parseLoginDataasassss(remoteResponse: String) {
        try {


            val jsonObject = JSONObject(remoteResponse)
            // val jsonObject = JSONObject(jsonresponse)


            if (jsonObject.getString("status") == "true") {
                dialogue();
                promptPopUpView?.changeStatus(
                    2,
                    "Alert was Successfully share to " + selectedItem2 + " RG!!"
                )

                mProgress!!.dismiss()

            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                mProgress!!.dismiss()


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    private fun uploadImages() {

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
        params["alert_name"] = alert_name!!
        params["fullname"] = usr!!
        params["alert_type"] = alertyp!!
        params["rg"] = selectedItem2!!
        params["rl"] = rls!!
        params["mssdn"] = mssdn!!
        params["userid"] = userid!!
        params["location"] = loc!!
        params["notes"] = notes!!
        params["attachment"] = url!!

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
                    Log.d("test", remoteResponse)
                    parseLoginDataasasssss(remoteResponse)
                } else {
                    mProgress?.dismiss()
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
                mProgress?.dismiss()
            }
        })
    }

    private fun parseLoginDataasasssss(remoteResponse: String) {
        try {


            val jsonObject = JSONObject(remoteResponse)
            // val jsonObject = JSONObject(jsonresponse)


            if (jsonObject.getString("status") == "true") {
                share()

            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                mProgress!!.dismiss()


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    private fun dialogue() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->

                counts()
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


    private fun AlertDialog.withCenteredButtonss() {
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

}
