package com.alat.ui.activities

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.FindTime
import com.alat.interfaces.paypal
import com.alat.ui.activities.mpesa.MPESAC2B
import com.alat.ui.activities.mpesa.MPESAExpressActivity
import fr.ganfra.materialspinner.MaterialSpinner
import libs.mjn.prettydialog.PrettyDialog
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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class account_enterprise : AppCompatActivity() {
    private var promptPopUpView: PromptPopUpView? = null
    private var edtPIN: TextView? = null
    var date2: String? = null
    private val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private var linear_layout_1: LinearLayout? = null
    private var linear_layout_2: android.widget.LinearLayout? = null
    private var tv_days: TextView? = null
    private var tv_hour: android.widget.TextView? = null
    var firstname: kotlin.String? = null
    private var roleID: String? = null
    var check_first: kotlin.String? = null
    var email: kotlin.String? = null
    var sname: kotlin.String? = null
    var dob: kotlin.String? = null
    var gender: kotlin.String? = null
    var mssdn: kotlin.String? = null
    var idNo: kotlin.String? = null
    var county: kotlin.String? = null
    var clients: kotlin.String? = null
    var responseprovider: kotlin.String? = null
    var Calling_URL: String? = null
    var spinner_3: MaterialSpinner? = null
    var tv_minute: android.widget.TextView? = null
    private var tv_second: android.widget.TextView? = null
    private val handler: Handler = Handler()
    private var runnable: Runnable? = null
    private var btnConfirm: Button? = null
    private var btnBack: Button? = null
    private var firstRun = true
    var selectedItem3: String? = null
    var account_status: String? = null
    var date: String? = null
    var price: String? = null
    var pref: SharedPreferences? = null
    private var account: String? = null
    private var userid: String? = null
    var TAB_REQUEST_CODE = 465
    var mstatus: String? = null
    var adsstatus: String? = null
    var account_type:TextView? = null
    var Mmeesage:TextView? = null
    var TITLE:TextView? = null
    private var mProgress: ProgressDialog? = null
    private var upgradetrial: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acountenterprise)
        Objects.requireNonNull(supportActionBar)!!.setDisplayShowHomeEnabled(true)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        edtPIN = findViewById<View>(R.id.account) as TextView
        account_type = findViewById<View>(R.id.enter) as TextView
        TITLE = findViewById<View>(R.id.title) as TextView
        btnBack = findViewById<View>(R.id.btn_disable) as Button
        btnBack!!.setText("Disable Ads")
        Mmeesage = findViewById<View>(R.id.accounts) as TextView
        mProgress = ProgressDialog(this);
        mProgress!!.setMessage("Redirecting..");
        mProgress!!.setCancelable(false);

        upgradetrial = findViewById<View>(R.id.btn_upgrade_trial) as Button

        btnBack!!.setVisibility(View.VISIBLE)

        pref =
            this.getSharedPreferences("ADS_ENTER", 0) // 0 - for private mode
        adsstatus = pref!!.getString("ads_enter", null)

        if (adsstatus == "1"){
            btnBack!!.setText("Enable Ads")

        }else{
            btnBack!!.setText("Disable Ads")
        }
        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode
        userid = pref!!.getString("userid", null)
        account = pref!!.getString("account_status", null)
        roleID = pref!!.getString("role", null)
        responseprovider = pref!!.getString("response_provider", null)
        firstname = pref!!.getString("fname", null)
        sname = pref!!.getString("lname", null)
        email = pref!!.getString("email", null)
        dob = pref!!.getString("dob", null)
        gender = pref!!.getString("gender", null)
        mssdn = pref!!.getString("mssdn", null)
        idNo = pref!!.getString("idNo", null)
        county = pref!!.getString("county", null)
        mstatus = pref!!.getString("mstatus", null)
        clients = pref!!.getString("clients", null)


        pref =
            this.getSharedPreferences("FIRSTCHECK", 0) // 0 - for private mode

        check_first = pref!!.getString("first_check", null)

//        Toast.makeText(this, check_first, Toast.LENGTH_LONG).show();


        if (responseprovider == "YES") {

            val ITEMS3 = arrayOf("Monthly ksh. 2600", "Quarterly Ksh. 7500", "Yearly Ksh. 29500")

            account_type!!.setText("You're currently subscribed to Enterprise PREMIUM Account:  \n\n Expire in... ")
            TITLE!!.setText("ALATPRES ENTERPRISE PREMIUM ACCOUNT")
            Mmeesage!!.setText("Kindly Renew / Subscribe to your plan of Choice to enjoy all your Premium Account features")


            val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, ITEMS3)
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_3 = findViewById<View>(R.id.package_sub) as MaterialSpinner
            spinner_3?.adapter = adapter2
            spinner_3!!.isSelected = false;  // otherwise listener will be called on initialization
            spinner_3!!.setSelection(0, true)
            spinner_3?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>?, arg1: View?,
                    arg2: Int, arg3: Long  ) {
                    if (spinner_3!!.selectedItem == null) {
                        return
                    } else {
                        selectedItem3 = spinner_3!!.selectedItem.toString()

                        if (selectedItem3 == "Monthly ksh. 2600") {
                            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val c = Calendar.getInstance()
                            c.add(Calendar.DATE, 30)
                            date = dateFormat.format(c.time)
                            price = "2600"
                            //  Toast.makeText(this@account, "Please"+ date, Toast.LENGTH_LONG).show();
                        } else if (selectedItem3 == "Quarterly Ksh. 7500") {
                            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val c = Calendar.getInstance()
                            c.add(Calendar.DATE, 90)
                            date = dateFormat.format(c.time)
                            price = "7500"
                            // Toast.makeText(this@account, "Please"+ date, Toast.LENGTH_LONG).show();
                        } else if (selectedItem3 == "Yearly Ksh. 29500") {
                            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val c = Calendar.getInstance()
                            c.add(Calendar.DATE, 355)
                            date = dateFormat.format(c.time)
                            price = "29500"
                        }

                    }
                }
                // TODO Auto-generated method stub
                override fun onNothingSelected(arg0: AdapterView<*>?) {
                    // TODO Auto-generated method stub
                }
            }
        } else {
            val ITEMS3 = arrayOf("Monthly ksh. 1200", "Quarterly Ksh. 3350", "Yearly Ksh. 11500")

            account_type!!.setText("You're currently subscribed to Enterprise PRO Account:  \n\n Expire in... ")
            TITLE!!.setText("ALATPRES ENTERPRISE PRO ACCOUNT")
            Mmeesage!!.setText("Kindly Renew/Subscribe to your plan of Choice to enjoy all your PRO Account features")

            val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, ITEMS3)
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_3 = findViewById<View>(R.id.package_sub) as MaterialSpinner
            spinner_3?.adapter = adapter2
            spinner_3!!.isSelected = false;  // otherwise listener will be called on initialization
            spinner_3!!.setSelection(0, true)
            spinner_3?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>?, arg1: View?,
                    arg2: Int, arg3: Long  ) {
                    if (spinner_3!!.selectedItem == null) {
                        // Toast.makeText(this@Enterprise, "Please select an Alert Type", Toast.LENGTH_LONG).show();
                        return
                    } else {
                        selectedItem3 = spinner_3!!.selectedItem.toString()
                        selectedItem3 = spinner_3!!.selectedItem.toString()

                        if (selectedItem3 == "Monthly ksh. 1200") {
                            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val c = Calendar.getInstance()
                            c.add(Calendar.DATE, 30)
                            date = dateFormat.format(c.time)
                            price = "1200"
                            //  Toast.makeText(this@account, "Please"+ date, Toast.LENGTH_LONG).show();
                        } else if (selectedItem3 == "Quarterly Ksh. 3350") {
                            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val c = Calendar.getInstance()
                            c.add(Calendar.DATE, 90)
                            date = dateFormat.format(c.time)

                            price = "3350"
                            // Toast.makeText(this@account, "Please"+ date, Toast.LENGTH_LONG).show();
                        } else if (selectedItem3 == "Yearly Ksh. 11500") {
                            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val c = Calendar.getInstance()
                            c.add(Calendar.DATE, 355)
                            date = dateFormat.format(c.time)
                            price = "11500"
                        }
                    }
                }
                // TODO Auto-generated method stub
                override fun onNothingSelected(arg0: AdapterView<*>?) {
                    // TODO Auto-generated method stub
                }
            }


        }



        linear_layout_1 = findViewById(R.id.linear_layout_1);
        linear_layout_2 = findViewById(R.id.linear_layout_2);
        tv_days = findViewById(R.id.tv_days);
        tv_hour = findViewById(R.id.tv_hour);
        tv_minute = findViewById(R.id.tv_minute);
        tv_second = findViewById(R.id.tv_second);
        btnConfirm = findViewById<View>(R.id.btn_upgrade) as Button




        when (account) {
            "1" -> {
                if(mstatus == "0"){
                    btnBack!!.setVisibility(View.VISIBLE)

                    pref =
                        applicationContext.getSharedPreferences("ADS", 0) // 0 - for private mode
                    val editor2: SharedPreferences.Editor = pref!!.edit()
                    editor2.putString("ads", "0")
                    editor2.clear()
                    editor2.apply()

                    upgradetrial!!.visibility = View.VISIBLE

                    account_type!!.setText("You're currently using trial Account:  \n\n Expire in... ")
                    TITLE!!.setText("ALATPRES ENTERPRISE TRIAL ACCOUNT")
                    linear_layout_1!!.setVisibility(View.GONE)
                    linear_layout_2!!.setVisibility(View.VISIBLE)
                    getStudent()
                }else {
                    linear_layout_1!!.setVisibility(View.GONE)
                    linear_layout_2!!.setVisibility(View.VISIBLE)
                    upgradetrial!!.visibility = View.GONE

                    getStudent()
                }

            }
            "0"-> {
                upgradetrial!!.visibility = View.GONE
                linear_layout_1!!.setVisibility(View.VISIBLE)
                linear_layout_2!!.setVisibility(View.GONE)
            }




            //  Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();
            //Adapters
            // otherwise listener will be called on initialization
        }

      //  Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();
        //Adapters
        btnConfirm!!.setOnClickListener(View.OnClickListener {
            BackAlert()
        })


        upgradetrial!!.setOnClickListener(View.OnClickListener {
            linear_layout_1!!.setVisibility(View.VISIBLE)
            linear_layout_2!!.setVisibility(View.GONE)
            upgradetrial!!.visibility = View.GONE

        })

//        upgradetrial!!.setOnClickListener(View.OnClickListener {
//            BackAlert()
//        })

        btnBack!!.setOnClickListener {
//            if(mstatus == "0") {
//                dialogue_error()
//                promptPopUpView?.changeStatus(1, "Kindly subscribe to a plan to enjoy this feature. Thank you")
//            }
            if (account == "1" && adsstatus == "0") {
                dialogue()
                promptPopUpView?.changeStatus(2, "Ads were disabled successfully")

                pref =
                    applicationContext.getSharedPreferences("ADS", 0) // 0 - for private mode

                val editor2: SharedPreferences.Editor = pref!!.edit()
                editor2.putString("ads", "1")
                editor2.clear()
                editor2.apply()


                pref =
                    applicationContext.getSharedPreferences("ADS_ENTER", 0) // 0 - for private mode

                val editor4: SharedPreferences.Editor = pref!!.edit()
                editor4.putString("ads_enter", "1")
                editor4.clear()
                editor4.apply()
            }
           else if (account == "1" && adsstatus == "1") {
                dialogue()
                promptPopUpView?.changeStatus(2, "Ads were enabled successfully")

                pref =
                    applicationContext.getSharedPreferences("ADS_ENTER", 0) // 0 - for private mode

                val editor4: SharedPreferences.Editor = pref!!.edit()
                editor4.putString("ads_enter", "0")
                editor4.clear()
                editor4.apply()


                pref =
                    applicationContext.getSharedPreferences("ADS", 0) // 0 - for private mode

                val editor2: SharedPreferences.Editor = pref!!.edit()
                editor2.putString("ads", "0")
                editor2.clear()
                editor2.apply()

            }else {
                dialogue_error()
                promptPopUpView?.changeStatus(1, "Kindly subscribe to a plan to enjoy this feature. Thank you")
            }
        }

    }

    private fun dialogue() {
        promptPopUpView = PromptPopUpView(this)
        AlertDialog.Builder(this)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                    pref =
                        this.getSharedPreferences("ADS_ENTER", 0) // 0 - for private mode
                    adsstatus = pref!!.getString("ads_enter", null)

                    if (adsstatus == "1") {
                        btnBack!!.setText("Enable Ads")

                    } else {
                        btnBack!!.setText("Disable Ads")
                    }
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    private fun dialogue_error() {
        promptPopUpView = PromptPopUpView(this)
        AlertDialog.Builder(this)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int -> }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    fun paypal_demo(price: String) {

        try {
            mProgress!!.show()

            val price2: Int? = price.toInt()

            val price4: Double? = price2!! * 0.0092

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
                .baseUrl(Constants.PAYPAL)
                .client(client) // This line is important
                .addConverterFactory(GsonConverterFactory.create())

                .build()
            val params: HashMap<String, String> = HashMap()

            params["price"] = price4.toString()
            val api: paypal = retrofit.create(paypal::class.java)
            val call: Call<ResponseBody>? = api.paypal(params)

            call?.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    //Toast.makeText()
                    if (response.isSuccessful) {
                        val remoteResponse = response.body()!!.string()
                        try {
                            mProgress!!.dismiss()

                            Log.i("response", remoteResponse)

                            val o = JSONObject(remoteResponse)
                            val links: JSONArray = o.getJSONArray("links")

                            for (i in 0 until links.length()) {
                                val elements = links.getJSONObject(i)
                                val keys: Iterator<*> = elements.keys()
                                while (keys.hasNext()) {
                                    val key = keys.next() as String
                                    if (elements[key].toString() == "REDIRECT") {
                                        Calling_URL = elements["href"].toString()
                                    }
                                }
                            }
                            open_cct()
                        } catch (e: JSONException) {
                            mProgress!!.dismiss()
                            e.printStackTrace()
                        }
                    } else {
                        mProgress!!.dismiss()
                        Log.d("bayo", response.errorBody()!!.string())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    //   btn.text = "Proceed"
                    mProgress!!.dismiss()
                    Log.i("onEmptyResponse", "" + t) //
                }
            })
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(
                getApplicationContext(),
                "Chrome Browser Not Installed",
                Toast.LENGTH_LONG
            ).show()
        }

    }


    private fun getStudent() {

//         Toast.makeText(this@account_enterprise, userid  , Toast.LENGTH_LONG).show()

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


        val api: FindTime = retrofit.create(FindTime::class.java)
        val call: Call<ResponseBody> = api.fRGs(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Log.d("Responsestring", response.body().toString())
                //Toast.makeText()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            Log.d("ALATPRES", response.body().toString())
                            val o = JSONObject(response.body()!!.string())
                            val array: JSONArray = o.getJSONArray("records")
                            for (i in 0 until array.length()) {

                                val dataobj: JSONObject = array.getJSONObject(i)
                                date2 = dataobj.getString("subscription_date")
//                                Toast.makeText(this@account_enterprise, dataobj.getString("subscription_date")  , Toast.LENGTH_LONG).show()
                                val sdf =
                                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                val strDate = sdf.parse(dataobj.getString("subscription_date"))
                                if (System.currentTimeMillis() > strDate.time) {
                                    val preferences =
                                        getSharedPreferences("MyPref", 0)
                                    val editor =
                                        preferences.edit()
                                    editor.putBoolean("isLogin", true)
                                    editor.putString("fname", firstname)
                                    editor.putString("lname", sname)
                                    editor.putString("email", email)
                                    editor.putString("dob", dob)
                                    editor.putString("role", roleID)
                                    editor.putString("mssdn", mssdn)
                                    editor.putString("gender", gender)
                                    editor.putString("idNo", idNo)
                                    editor.putString("county", county)
                                    editor.putString("userid", userid)
                                    editor.putString("account_status", "0")
                                    editor.putString("mstatus", "1")
                                    editor.putString("clients", clients)
                                    editor.putString("response_provider", responseprovider)
                                    editor.clear()
                                    editor.apply()


                                    pref =
                                        applicationContext.getSharedPreferences("FIRSTCHECK", 0) // 0 - for private mode
                                    val editor9: SharedPreferences.Editor = pref!!.edit()
                                    editor9.putString("first_check", check_first)
                                    editor9.clear()
                                    editor9.apply()


                                    pref =
                                        applicationContext.getSharedPreferences("ADS_ENTER", 0) // 0 - for private mode

                                    val editor4: SharedPreferences.Editor = pref!!.edit()
                                    editor4.putString("ads_enter", "0")
                                    editor4.clear()
                                    editor4.apply()


                                    recreate()
                                } else {
                                    //Toast.makeText(this@account, "you"  , Toast.LENGTH_LONG).show()
                                    countDownStart()
                                }
                            }
                                //Log.d("onSuccess1", firstSport.toString())

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {

                        Log.i("onEmptyResponse", "Returned empty response") //Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Log.d("bayo", response.errorBody()!!.string())
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")


                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                //   btn.text = "Proceed"
                Log.i("onEmptyResponse", "" + t) //

                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //mProgress?.dismiss()
            }

        })
    }


    private fun countDownStart() {
        runnable = object : Runnable {
            override fun run() {
                try {
                   // Toast.makeText(this@account, "you" + date2  , Toast.LENGTH_LONG).show()

                    handler.postDelayed(this, 1000)
                    val dateFormat =
                        SimpleDateFormat(DATE_FORMAT)
                    val event_date: Date = dateFormat.parse(date2)
                    val current_date = Date()
                    if (!current_date.after(event_date)) {
                        val diff = event_date.time - current_date.time
                        val Days = diff / (24 * 60 * 60 * 1000)
                        val Hours = diff / (60 * 60 * 1000) % 24
                        val Minutes = diff / (60 * 1000) % 60
                        val Seconds = diff / 1000 % 60
                        //
                        tv_days!!.setText(String.format("%02d", Days))
                        tv_hour!!.setText(String.format("%02d", Hours))
                        tv_minute!!.setText(String.format("%02d", Minutes))
                        tv_second!!.setText(String.format("%02d", Seconds))
                    } else {
                        linear_layout_1!!.setVisibility(View.VISIBLE)
                        linear_layout_2!!.setVisibility(View.GONE)
                        handler.removeCallbacks(runnable)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }

        }
        handler.postDelayed(runnable, 0)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this, HomePage::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, HomePage::class.java))
    }



    fun BackAlert() {


        val pDialog = PrettyDialog(this)
        pDialog
            .setIconTint(R.color.colorPrimary)
            .setTitle("Kindly subscribe to a plan to enjoy all product features. Thank you")
            .setTitleColor(R.color.pdlg_color_blue)
            .setMessage("Choose the payment method you recommend")
            .setMessageColor(R.color.pdlg_color_gray)
            .addButton(
                "Mpesa Payment",
                R.color.pdlg_color_white,
                R.color.colorAccent
            ) { pDialog.dismiss()
                if (selectedItem3 == null) {
                    Toast.makeText(this@account_enterprise, "Please select a subscription package", Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(this@account_enterprise, date2, Toast.LENGTH_LONG).show();
                    if (check_first == "0") {
                        CheckFirst()
                    } else {

                        val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this@account_enterprise)
                        alert.setTitle("MPESA VIA")
                        alert.setMessage("Select your preferred mpesa payment method")
                        alert.setPositiveButton("C2B") { dialog, which ->
                            val i =
                                Intent(this@account_enterprise, MPESAC2B::class.java)
                            i.putExtra("price", price)
                            i.putExtra("time", date)
                            startActivity(i)
                        }
                        alert.setNeutralButton("STK Push"
                        ) { dialog, which ->

                            val i =
                                Intent(this@account_enterprise, MPESAExpressActivity::class.java)
                            i.putExtra("price", price)
                            i.putExtra("time", date)
                            startActivity(i)                        }
                        alert.show()
                         }
                }
              }
            .addButton(
                "PayPal",
                R.color.pdlg_color_white,
                R.color.colorAccent) {
                pDialog.dismiss()
                if (check_first == "0") {
                    CheckFirst23()
                } else {
//                    Toast.makeText(this@account_enterprise, price, Toast.LENGTH_LONG).show();
                    paypal_demo(price!!)
                }
            }
            .show()
    }


    fun CheckFirst() {
        val pDialog = PrettyDialog(this)
        pDialog
            .setIconTint(R.color.colorPrimary)
            .setTitle("ONBOARDING FEE")
            .setTitleColor(R.color.pdlg_color_blue)
            .setMessage("ONBOARDING FEE " + 3500 +"\n\n  SUBSCRIPTION FEE "+Integer.parseInt(price!!)+
                    "\n\n TOTAL  " + (Integer.parseInt(price!!) + 3500) +

                    "\n\n(Onboarding fee is one-time) Continue with the payment"  )

            .setMessageColor(R.color.pdlg_color_gray)
            .addButton(
                "Continue",
                R.color.pdlg_color_white,
                R.color.colorAccent
            ) { pDialog.dismiss()
                if (selectedItem3 == null) {
                    Toast.makeText(this@account_enterprise, "Please select a subscription package", Toast.LENGTH_LONG).show();
                } else {
                    val i = Intent(this@account_enterprise, MPESAC2B::class.java)
                    i.putExtra("price", (Integer.parseInt(price!!) + 3500))
                    i.putExtra("time", date)
                    startActivity(i)
                }
            }

            .show()
    }


    fun CheckFirst23() {
        val pDialog = PrettyDialog(this)
        pDialog
            .setIconTint(R.color.colorPrimary)
            .setTitle("ONBOARDING FEE")
            .setTitleColor(R.color.pdlg_color_blue)
            .setMessage(
                "ONBOARDING FEE " + 3500 + "\n\n  SUBSCRIPTION FEE " + Integer.parseInt(price!!) +
                        "\n\n TOTAL  " + (Integer.parseInt(price!!) + 3500) +

                        "\n\n (Onboarding fee is one-time) Continue with the payment"
            )

            .setMessageColor(R.color.pdlg_color_gray)

            .addButton(
                "Continue",
                R.color.pdlg_color_white,
                R.color.colorAccent
            ) {
                pDialog.dismiss()

                paypal_demo((Integer.parseInt(price!!) + 3500).toString()) // Paypal Demo

//                startActivity(Intent(this, Ban_Transfer::class.java))

//                if (selectedItem3 == null) {
//
//                }else {
//                    Toast.makeText(this@account_enterprise, "Coming Soon", Toast.LENGTH_LONG).show();
//                }
            }
            .show()
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


    fun isPackageInstalled(packageName: String?): Boolean {
        return try {
            applicationContext.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun open_cct() {
        val packageName = "com.android.chrome"
        val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder().build()

        // check if chrome is installed if installed always open in chrome so we can have OneTouch Feature !
        if (isPackageInstalled(packageName)) {
            customTabsIntent.intent.setPackage(packageName)
        }
        customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        customTabsIntent.intent.setData(Uri.parse(Calling_URL))
        startActivityForResult(customTabsIntent.intent, TAB_REQUEST_CODE)
    }


}