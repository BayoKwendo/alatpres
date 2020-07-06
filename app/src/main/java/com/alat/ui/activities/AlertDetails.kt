package com.alat.ui.activities

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.GetAlertPost
import com.alat.interfaces.UpdateAlertStatus
import com.squareup.picasso.Picasso
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

    var saveImage: Button? = null

    private var promptPopUpView: PromptPopUpView? = null
    var rg: String? = null
    var view: View? = null

    var notes: String? = null

    var alertyp: String? = null
    var usr: String? = null
    var loc: String? = null
    var created: String? = null
    var nutralize: TextView? = null

    var imageView: ImageView? = null

    var url: String? = null
    private var mProgress: ProgressDialog? = null


    var downloadManager: DownloadManager? = null

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

        imageView =findViewById(R.id.image)
        nutralize =findViewById(R.id.neutralized)

        saveImage =findViewById(R.id.save)

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

        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Fetching...")
        mProgress!!.setCancelable(false)
        mProgress!!.show()

        saveImage!!.setOnClickListener {
            download()
        }

        nutralize!!.setOnClickListener {
            AlertStatus()
        }
        getStudent()

    }

    fun download() {
        AlertDialog.Builder(this)
            .setMessage("Download the image?")
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
            GetAlertPost::class.java)
        val call: Call<String>? = api.getAlert(mid,alert_type,name,rg,locations,attachment,notes,created, modified)
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


            addNotes!!.text = notes


            url = jsonObject.getString("attachment")

           //Toast.makeText(this@AlertDetails, "Success"  + url, Toast.LENGTH_LONG).show()

           if(url != null){
               saveImage!!.visibility = View.VISIBLE
               imageView!!.visibility = View.VISIBLE

            }

            Picasso.get().load(url).into(imageView)



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


    fun AlertStatus() {

        AlertDialog.Builder(this)
            .setMessage("Change status of this Alert??")
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

        params["id"] = mid!!

        val api: UpdateAlertStatus = retrofit.create(UpdateAlertStatus::class.java)
        val call: Call<ResponseBody> = api.Update(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Call request header", call.request().headers.toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {

                    if (response.body() != null) {
                        val remoteResponse = response.body()!!.string()

                        Log.d("test", remoteResponse)
                        parseLoginDatas(remoteResponse)

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

    private fun parseLoginDatas(remoteResponse: String) {
        try {


            val jsonObject = JSONObject(remoteResponse)
            // val jsonObject = JSONObject(jsonresponse)


            if (jsonObject.getString("status") == "true") {
                dialogue();
                promptPopUpView?.changeStatus(2, "Alert was Neutralized successfully!")

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
