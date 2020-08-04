package com.alat.ui.activities

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
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.alat.HomePage
import com.alat.Permission
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.*
import com.alat.model.rgModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

class GroupID : AppCompatActivity() {
    private var promptPopUpView: PromptPopUpView? = null
    private var edtPIN: EditText? = null
    private var btnConfirm: Button? = null
    private var btnBack: Button? = null

    private var btnRequerst: Button? = null

    private var btnbacks: Button? = null

    var response_id: String? = null
    var response_name: String? = null

    var group_id: String? = null


    private var fullname: String? = null
    private var mssidn: String? = null
    var const: ConstraintLayout? = null
    //private var mProgress: ProgressDialog? = null

    var const2: ConstraintLayout? = null

    var const3: ConstraintLayout? = null

    private var mProgressLayout: LinearLayout? = null

    var user: String? = null

    var checkd: String? = null


    var pref: SharedPreferences? = null

    //private var preferenceHelper: PreferenceHelper? = null

    private var mProgress: ProgressDialog? = null

    private var mProgress2: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm_group_id)
        edtPIN = findViewById<View>(R.id.input_pin) as EditText
        btnConfirm = findViewById<View>(R.id.btn_confirm) as Button
        btnBack = findViewById<View>(R.id.btn_back) as Button


        btnRequerst = findViewById<View>(R.id.btn_request) as Button

        mProgressLayout = findViewById(R.id.layout_discussions_progress);
        mProgressLayout!!.visibility = View.VISIBLE
        const = findViewById(R.id.constraintLayout1)
        const2 = findViewById(R.id.constraintLayout)

        const3 = findViewById(R.id.constraintLayout4)

        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Joining...")
        mProgress!!.setCancelable(true)


        mProgress2 = ProgressDialog(this)
        mProgress2!!.setMessage("Sending Request...")
        mProgress2!!.setCancelable(false)
        btnbacks = findViewById<View>(R.id.btn_backs1) as Button


        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode
        user = pref!!.getString("userid", null)
        fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

        mssidn = pref!!.getString("mssdn", null)

        checkd = intent.getStringExtra("grouptype")


        if(checkd == "bayo"){
            const!!.visibility = View.GONE
            const3!!.visibility = View.GONE
            const2!!.visibility = View.VISIBLE
            mProgressLayout!!.visibility = View.GONE

        }else {
            checkMmeber()
        }
        response_id = intent.getStringExtra("groupSelect")


       // response_name = intent.getStringExtra("groupName")

        btnRequerst!!.setOnClickListener {

            if (!isNetworkAvailable()) {
                internet()
                promptPopUpView?.changeStatus(
                    1,
                    "Connection Error\n\n Check your internet connectivity"
                )
            } else {
                mProgress2!!.show()
                sendRequest()
            }

        }


        edtPIN!!.setText(response_id)


        btnConfirm!!.setOnClickListener(View.OnClickListener {
            val pin = edtPIN!!.text.toString().trim { it <= ' ' }
            when {
                TextUtils.isEmpty(pin) -> {
                    Toast.makeText(applicationContext, "Enter your PIN!", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                pin == response_id -> {
                    dialogue();
                    promptPopUpView?.changeStatus(2, "Welcome  \n\n" + "Redirecting")
                    Handler().postDelayed({
                        getUserDetails()

                    }, 1000)
                }
                else -> {

                    dialogue_error()
                    promptPopUpView?.changeStatus(1, "Oooops Error!!  Try Again")

                }
            }
        })
        btnBack!!.setOnClickListener { finish() }

        btnbacks!!.setOnClickListener { finish() }

    }



    private fun getUserDetails() {



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
        params["rg_id"] = response_id!!

        val api: CheckName = retrofit.create(CheckName::class.java)
        val call: Call<ResponseBody> = api.checkN(params)

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

                   var groupname =
                        jsonObjec.getString("group_name")
                    val i =
                        Intent(this@GroupID, AlertsToResponse::class.java)
                    i.putExtra("groupID", response_id)
                    i.putExtra("groupNam", groupname)

                    startActivity(i)

                }
                else if(jsonObjec.getString("status") == "false"){

                   // waitingDialog!!.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "User not Registered to AlatPres")
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
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
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .client(client) // This line is important
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val params: HashMap<String, String> = HashMap()
        response_id = intent.getStringExtra("groupSelect")

        params["userid"] = user!!
        params["rg_id"] = response_id!!


        val api: CheckMember = retrofit.create(CheckMember::class.java)
        val call: Call<ResponseBody> = api.CheckMembe(params)

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

                    if (response.code().toString() == "200") {
                        parseLoginDatas(remoteResponse)
                    }
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

    private fun parseLoginDatas(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {
                mProgressLayout!!.visibility = View.GONE
                const!!.visibility = View.GONE
                const3!!.visibility = View.GONE
                const2!!.visibility = View.VISIBLE

 
            }
            else if (jsonObject.getString("status") == "come") {
                dialogue_errors()
                promptPopUpView?.changeStatus(1, "No group with ID " +response_id + " was found" )
               // Log.i("onEmptyResponse", "" + t) //
            }
            else{
                checkStatuss()
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun checkStatuss() {

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
        response_id = intent.getStringExtra("groupSelect")

        params["userid"] = user!!
        params["rg_id"] = response_id!!


        val api: CheckStatus = retrofit.create(CheckStatus::class.java)
        val call: Call<ResponseBody> = api.CheckSta(params)

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

                    if (response.code().toString() == "200") {
                        parseLoginDatasss(remoteResponse)
                    }
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

    private fun parseLoginDatasss(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {
                mProgressLayout!!.visibility = View.GONE
                const!!.visibility = View.GONE
                const3!!.visibility = View.VISIBLE
                const2!!.visibility = View.GONE
            } else if (jsonObject.getString("status") == "false") {
                mProgressLayout!!.visibility = View.GONE
                const!!.visibility = View.VISIBLE
                const2!!.visibility = View.GONE
                const3!!.visibility = View.GONE

                Toast.makeText(this@GroupID, "Please send request to join", Toast.LENGTH_LONG)
                    .show()


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun sendRequest() {

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
        response_id = intent.getStringExtra("groupSelect")
        params["fullname"] = fullname!!
        params["mssidn"] = mssidn!!
        params["userid"] = user!!
        params["rg_id"] = response_id!!


        val api: requestMember = retrofit.create(requestMember::class.java)
        val call: Call<ResponseBody> = api.RequesrM(params)

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

                    if (response.code().toString() == "200") {
                        parseLoginData(remoteResponse)
                    }
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

    private fun parseLoginData(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {
                mProgressLayout!!.visibility = View.GONE
                const!!.visibility = View.VISIBLE
                const2!!.visibility = View.GONE
                const3!!.visibility = View.GONE

                mProgress2!!.dismiss()

                dialogue_success()
                promptPopUpView?.changeStatus(
                    2,
                    "Your joining request  has been send successfully. Please wait for approval"
                )

            } else {
                mProgressLayout!!.visibility = View.GONE
                const!!.visibility = View.VISIBLE
                const2!!.visibility = View.GONE
                const3!!.visibility = View.GONE
                mProgress2!!.dismiss()

                dialogue_error()
                promptPopUpView?.changeStatus(
                    1,
                    "Your joining request was Unsuccessful. Please Try again"
                )


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun dialogue() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    private fun dialogue_error() {
        promptPopUpView = PromptPopUpView(this)
        AlertDialog.Builder(this)
            .setPositiveButton("Try Again") { _: DialogInterface?, _: Int ->
            }
            .setCancelable(true)
            .setView(promptPopUpView)
            .show()
    }

    private fun dialogue_errors() {
        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("Back to Home") { _: DialogInterface?, _: Int ->
                startActivity(Intent(this, HomePage::class.java))

            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
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

    private fun dialogue_success() {
        promptPopUpView = PromptPopUpView(this)
        AlertDialog.Builder(this)
            .setPositiveButton("Exit") { _: DialogInterface?, _: Int ->
                finish()
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }


}