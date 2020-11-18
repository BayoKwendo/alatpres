package com.alat.ui.activities.mpesa

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.sendSMS
import com.alat.interfaces.sendSMSBank
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class braintree : AppCompatActivity() {


    var pref: SharedPreferences? = null
    var fname: String? = null
    var user: String? = null
    var emailuser: String? = null
    var county_name: String? = null
    var dob_user: String? = null
    var mssidn: String? = null
    var id: String? = null
    var price: String? = null
    private var mProgress: ProgressDialog? = null
    private var btnConfirm: Button? = null

    private var promptPopUpView: PromptPopUpView? = null

    var time: String? = null

    var mToolbar: Toolbar? = null

      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank)
          supportActionBar!!.setDisplayHomeAsUpEnabled(true)
          pref =  getSharedPreferences("MyPref", 0) // 0 - for private mode
          fname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)
          mssidn = pref!!.getString("mssdn", null)
          user = pref!!.getString("userid", null)
          id = pref!!.getString("idNo", null)
          emailuser = pref!!.getString("email", null)
          county_name = pref!!.getString("county", null)
          dob_user = pref!!.getString("dob", null)
          price = intent.getStringExtra("price")
          time = intent.getStringExtra("time")
          btnConfirm = findViewById<View>(R.id.msubmit) as Button


          btnConfirm!!.setOnClickListener(View.OnClickListener {
              sendConfirmation()
          })

          mProgress = ProgressDialog(this)
          mProgress!!.setMessage("Sending Confirmation...")
          mProgress!!.setCancelable(true)

      }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {

            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }


    private fun sendConfirmation() {
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
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(Constants.API_BASE_URL)
            .client(client) // This line is important
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val params: HashMap<String, String> = HashMap()
        //  response_id = intent.getStringExtra("groupSelect")

        params["mssdn"] = mssidn!!
        params["user_id"] = user!!
        params["price"] = price!!
        params["name"] = fname!!


        val api: sendSMSBank = retrofit.create(sendSMSBank::class.java)
        val call: Call<ResponseBody> = api.send(params)

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

                        mProgress?.dismiss()
                        dialogue();
                        promptPopUpView?.changeStatus(
                            2,
                            "The confirmation request is well received. We will get back to you soon."
                        )

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
}