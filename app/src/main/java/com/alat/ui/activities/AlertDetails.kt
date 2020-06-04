package com.alat.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.interfaces.GetAlertPost
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
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
import java.util.concurrent.TimeUnit

class AlertDetails : AppCompatActivity() {

    var mid: String? = null
    var mToolbar: Toolbar? = null


    var rl: String? = null


    var response_group: TextView? = null
    var alert_type: TextView? = null
    var fullnaem: TextView? = null
    var location: TextView? = null

    var createdOn: TextView? = null

    var addNotes: TextView? = null



    var rg: String? = null
    var view: View? = null

    var notes: String? = null

    var alertyp: String? = null
    var usr: String? = null
    var loc: String? = null
    var created: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        mid = intent.getStringExtra("alertSelect")
        rl = intent.getStringExtra("level")

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        response_group = findViewById(R.id.textView)
        alert_type = findViewById(R.id.textView7)
        fullnaem =findViewById(R.id.textView9)
        addNotes =findViewById(R.id.textView11)

        location = findViewById(R.id.textView15)
        createdOn = findViewById(R.id.textView21)
        view = findViewById(R.id.view)

        if(rl == "Level 3") {
            view!!.setBackgroundColor(ContextCompat.getColor(this, R.color.error))
            ///holder.name.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        }else if (rl == "Level 2"){
            view!!.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
           // holder.name.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        }else if (rl == "Level 1"){
            view!!.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
          //  holder.name.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        }else if (rl == "Neutralized"){
            view!!.setBackgroundColor(ContextCompat.getColor(this, R.color.accentGreen))
            //holder.name.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        }

        getStudent()

    }


    private fun getStudent() {

        val alert_type: String? = null
        val rg: String? = null
        val name: String? = null
        val locations: String? = null
        val notes: String? = null

        val modified: String? = null


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
            GetAlertPost::class.java)
        val call: Call<String>? = api.getAlert(mid,alert_type,name,rg,locations,notes,created, modified)
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
                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        ) //Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.i("onEmptyResponse", "" + t) //
            }
        })
    }


    private fun parseLoginData(jsonresponse: String) {
        try {
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


            addNotes!!.text = notes


        } catch (e: JSONException) {
            e.printStackTrace()
        }
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


}
