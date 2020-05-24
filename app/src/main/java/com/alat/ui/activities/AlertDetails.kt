package com.alat.ui.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.alat.HomePage
import com.alat.Permission
import com.alat.R
import com.alat.helpers.Constants
import com.alat.interfaces.GetAlertPost
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class AlertDetails : AppCompatActivity() {

    var mid: String? = null
    var mToolbar: Toolbar? = null

    var response_group: TextView? = null
    var alert_type: TextView? = null
    var fullnaem: TextView? = null
    var location: TextView? = null

    var createdOn: TextView? = null



    var rg: String? = null
    var alertyp: String? = null
    var usr: String? = null
    var loc: String? = null
    var created: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        mid = intent.getStringExtra("alertSelect")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        response_group = findViewById(R.id.textView)
        alert_type = findViewById(R.id.textView7)
        fullnaem =findViewById(R.id.textView9)
        location = findViewById(R.id.textView15)
        createdOn = findViewById(R.id.textView21)

        getStudent()

    }


    private fun getStudent() {

        val alert_type: String? = null
        val rg: String? = null
        val name: String? = null
        val locations: String? = null
        val created: String? = null
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .build()
                chain.proceed(request)
            }
            .build()
      //  val id: String = gf
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        val api: GetAlertPost = retrofit.create(
            GetAlertPost::class.java)
        val call: Call<String>? = api.getAlert(mid,alert_type,name,rg,locations,created)
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




        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


}
