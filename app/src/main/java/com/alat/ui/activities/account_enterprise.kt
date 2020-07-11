package com.alat.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.FindTime
import com.alat.ui.activities.mpesa.MPESAExpressActivity
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
    private  var linear_layout_2:android.widget.LinearLayout? = null

    private var tv_days: TextView? = null
    private  var tv_hour:android.widget.TextView? = null


    private val ITEMS3= arrayOf("Monthly ksh. 1500", "Quarterly Ksh. 1400", "Yearly Ksh. 1,100")

    var spinner_3: MaterialSpinner? = null

    var tv_minute:android.widget.TextView? = null

    private  var tv_second:android.widget.TextView? = null
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

    private var mProgress: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acountenterprise)
        Objects.requireNonNull(supportActionBar)!!.setDisplayShowHomeEnabled(true)


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        edtPIN = findViewById<View>(R.id.account) as TextView


        pref =
            this!!.getSharedPreferences("MyPref", 0) // 0 - for private mode


        account = pref!!.getString("account_status", null)
        userid = pref!!.getString("userid", null)


        linear_layout_1 = findViewById(R.id.linear_layout_1);

        linear_layout_2 = findViewById(R.id.linear_layout_2);

        tv_days = findViewById(R.id.tv_days);
        tv_hour = findViewById(R.id.tv_hour);
        tv_minute = findViewById(R.id.tv_minute);
        tv_second = findViewById(R.id.tv_second);

        btnConfirm = findViewById<View>(R.id.btn_upgrade) as Button

        when (account) {
            "1" -> {
                linear_layout_1!!.setVisibility(View.GONE)
                linear_layout_2!!.setVisibility(View.VISIBLE)
                getStudent()


            }
            "0"-> {


                linear_layout_1!!.setVisibility(View.VISIBLE)
                linear_layout_2!!.setVisibility(View.GONE)
            }


            //  Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();
            //Adapters
            // otherwise listener will be called on initialization
        }





      //  Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();
        //Adapters
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

                    if(selectedItem3 == "Monthly ksh. 100"){
                        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val c = Calendar.getInstance()
                        c.add(Calendar.DATE, 30)
                        date = dateFormat.format(c.time)

                        price = "100"
                      //  Toast.makeText(this@account, "Please"+ date, Toast.LENGTH_LONG).show();
                    }else if(selectedItem3 == "Quarterly Ksh. 400"){
                        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val c = Calendar.getInstance()
                        c.add(Calendar.DATE, 120)
                        date = dateFormat.format(c.time)

                        price = "400"
                       // Toast.makeText(this@account, "Please"+ date, Toast.LENGTH_LONG).show();
                    }
                    else if(selectedItem3 == "Yearly Ksh. 1,100"){
                        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val c = Calendar.getInstance()
                        c.add(Calendar.DATE, 355)
                        date = dateFormat.format(c.time)
                        price = "1100"
                    }
                }
            }


            // TODO Auto-generated method stub

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }




        btnConfirm!!.setOnClickListener(View.OnClickListener {
            BackAlert()
        })



    }



    private fun getStudent() {

        // Toast.makeText(this@GroupsRequests, userid  , Toast.LENGTH_LONG).show()

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

                        // val jsonresponse = response.body().toString()

                        // Log.d("onSuccessS", response.errorBody()!!.toString())

                        try {

                            Log.d("SUCCESS", response.body().toString())
                            val o = JSONObject(response.body()!!.string())
                            val array: JSONArray = o.getJSONArray("records")

                            for (i in 0 until array.length()) {
                                    val dataobj: JSONObject = array.getJSONObject(i)



                                 date2 = dataobj.getString("subscription_date")

                                val sdf =
                                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                val strDate = sdf.parse(dataobj.getString("subscription_date"))
                                if (System.currentTimeMillis() > strDate.time) {
                                    //Toast.makeText(this@account, "me" + dataobj.getString("subscription_date")  , Toast.LENGTH_LONG).show()


                                } else {
                                    //Toast.makeText(this@account, "you"  , Toast.LENGTH_LONG).show()
                                }

                                countDownStart()


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
        AlertDialog.Builder(this)
            .setMessage("Upgrade to Enterprise Account")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, id ->
                if (selectedItem3 == null) {

                    Toast.makeText(this@account_enterprise, "Please select a subscription package", Toast.LENGTH_LONG).show();

                }else {
                    val i =
                        Intent(this@account_enterprise, MPESAExpressActivity::class.java)
                    i.putExtra("price", price)
                    i.putExtra("time", date)
                    startActivity(i)
                }
            }
                //finish()
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



}