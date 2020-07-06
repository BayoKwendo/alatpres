package com.alat.ui.activities

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.GetSpecificProviders
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
import java.util.concurrent.TimeUnit

class ResponseDetails : AppCompatActivity() {

    var mid: String? = null
    var mToolbar: Toolbar? = null


    var response_group: TextView? = null
    var alert_type: TextView? = null
    var fullnaem: TextView? = null
    var location: TextView? = null

    var createdOn: TextView? = null

    var addNotes: TextView? = null
    private var promptPopUpView: PromptPopUpView? = null

    private var mProgress: ProgressDialog? = null


    var rg: String? = null
    var view: View? = null


    var alertyp: String? = null
    var usr: String? = null
    var loc: String? = null
    var created: String? = null


    var url: String? = null
     ;

    var downloadManager: DownloadManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_providerdetail)
        mid = intent.getStringExtra("responseSelect")

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        response_group = findViewById(R.id.textView)
        alert_type = findViewById(R.id.textView7)
        fullnaem =findViewById(R.id.textView9)
        addNotes =findViewById(R.id.textView11)



        location = findViewById(R.id.textView15)
        createdOn = findViewById(R.id.textView21)
        view = findViewById(R.id.view)

        mProgress = ProgressDialog(this);
        mProgress!!.setMessage("Fetching...");
        mProgress!!.setCancelable(false);
        mProgress!!.show()
        getStudent()

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
        params["userid"] = mid!!

        val api: GetSpecificProviders = retrofit.create(GetSpecificProviders::class.java)
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

                    if (response.code().toString() == "200"){
                        parseLoginData(remoteResponse)

                    } else {
                        mProgress!!.dismiss()
                        dialogue_error();
                        promptPopUpView?.changeStatus(1, "Something went wront. Try again")
                    }
                } else {
                    dialogue_error();

                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                dialogue_error();

                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
            }
        })

    }


    private fun parseLoginData(jsonresponse: String) {
        try {
            mProgress!!.dismiss()

            val o = JSONObject(jsonresponse)
            val jsonArrayNid: JSONArray = o.getJSONArray("records")

            for (i in 0 until jsonArrayNid.length()) {
                val jsonObject = jsonArrayNid.getJSONObject(i)

                alertyp = jsonObject.getString("userid")


                alert_type!!.text = alertyp

                rg = jsonObject.getString("firstname")+"\t"+jsonObject.getString("lastname")

                response_group!!.text = rg




                usr = jsonObject.getString("nature_response")

                fullnaem!!.text = usr



                loc = jsonObject.getString("town")

                location!!.text = loc

                created = jsonObject.getString("county")

                createdOn!!.text = created
            }

          //  val jsonObject = JSONObject(jsonresponse)








        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    fun saveImag(){
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

    private fun dialogue_error() {
        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("Back Home") { _: DialogInterface?, _: Int ->
                startActivity(Intent(this@ResponseDetails, HomePage::class.java))
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }
}
