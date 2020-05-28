package com.alat.ui.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.CheckMember
import com.alat.interfaces.CreateResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AddTopicActivity : AppCompatActivity() {
    private var mTitleInput: EditText? = null
    private var mPostBtn: Button? = null
    var pref: SharedPreferences? = null
    var user: String? = null
    var fname: String? = null
    var alert_id: String? = null

    var group_name: String? = null


    var response_id: String? = null
    private var promptPopUpView: PromptPopUpView? = null


    private var mProgress: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_topic)
        mTitleInput = findViewById(R.id.input_topic_title)
        mPostBtn = findViewById(R.id.btn_submit_topic)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode
        user = pref!!.getString("userid", null)
        fname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)



        response_id = intent.getStringExtra("groupID")
        alert_id = intent.getStringExtra("alertSelect")

        group_name = intent.getStringExtra("groupName")



        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Processing...")
        mProgress!!.setCancelable(false)
        mPostBtn!!.setOnClickListener(View.OnClickListener {
            val title = mTitleInput!!.getText().toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(title)) {
                mTitleInput!!.setError("Enter a topic first")
                return@OnClickListener
            }
            if (!isNetworkAvailable()) {
                internet()
                promptPopUpView?.changeStatus(1, "Connection Error\n\n Check your internet connectivity")

            } else {
                postTopic(title)
            }
        })
    }

    private fun postTopic(title: String) {
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

        params["content"] = title
        params["fullname"] = fname!!
        params["userid"] = user!!
        params["rg_id"] = response_id!!
        params["alertid"] = alert_id!!



        val api: CreateResponse = retrofit.create(CreateResponse::class.java)
        val call: Call<ResponseBody> = api.addResp(params)

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

                    if (response.code().toString() == "200"){
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
                mProgress!!.dismiss()

         val i =
              Intent(this, AlertDiscussion::class.java)
              i.putExtra("alertSelect",alert_id)
               i.putExtra("groupID", response_id)
                i.putExtra("groupName", group_name)

                startActivity(i)

              //  Toast.makeText(this@AddTopicActivity, "Great!!", Toast.LENGTH_LONG).show()

            }else{
                Toast.makeText(this@AddTopicActivity, "Please Something went wrong", Toast.LENGTH_LONG).show()

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

}