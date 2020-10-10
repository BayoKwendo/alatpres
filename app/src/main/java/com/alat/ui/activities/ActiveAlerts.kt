package com.alat.ui.activities

import android.app.ProgressDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alat.HomePage
import com.alat.R
import com.alat.adapters.AlertAdapter
import com.alat.helpers.Constants
import com.alat.helpers.MyDividerItemDecoration
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.ALertDelete
import com.alat.interfaces.ALertRG
import com.alat.interfaces.GetActiveAlerts
import com.alat.interfaces.UpdateAlertStatus
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
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.collections.set

@Suppress("UNREACHABLE_CODE")
class ActiveAlerts : AppCompatActivity(), AlertAdapter.ContactsAdapterListener  {

   var response_group: String? = null

    private val TAG = ActiveAlerts::class.java.simpleName
    private var recyclerView: RecyclerView? = null
    private var contactList: MutableList<rgModel>? = null
    private var mAdapter: AlertAdapter? = null
    private var searchView: SearchView? = null
    private var mProgressLayout: LinearLayout? = null

    private var promptPopUpView: PromptPopUpView? = null

    var alert_id: String? = null
    var rg: String? = null

    var alert_rlss: String? = null

    var pref: SharedPreferences? = null


  //  var fname: String? = null
    var user: String? = null

    private var btnResetPassword: Button? = null
    private var btnBack: Button? = null
    var errorNull: TextView? = null
    private var mProgress: ProgressDialog? = null
    var MYCODE = 1000

    var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)
        mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar!!);

      //  response_group = intent.getStringExtra("groupSelect")

        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode
        user = pref!!.getString("userid", null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = response_group;


        recyclerView = findViewById(R.id.recycler_view)
        errorNull = findViewById(R.id.texterror)
        contactList = ArrayList()
        mAdapter = AlertAdapter(this, contactList!!, this)

        mProgressLayout = findViewById(R.id.layout_discussions_progress);

        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.addItemDecoration(
            MyDividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL,
                36
            )
        )
        recyclerView!!.adapter = mAdapter

        mProgressLayout!!.visibility = View.VISIBLE
        errorNull!!.visibility = View.GONE

        getStudent()

    }


    private fun getStudent() {


        mToolbar!!.title = "Active Alats"

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
        params["userid"] = user!!

        val api: GetActiveAlerts = retrofit.create(GetActiveAlerts::class.java)
        val call: Call<ResponseBody> = api.GetActive(params)

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
                        errorNull!!.visibility = View.VISIBLE
                        mProgressLayout!!.visibility = View.GONE
                    }
                    parseLoginData(remoteResponse)
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
    private fun parseLoginData(remoteResponse: String) {
        try {
            val o = JSONObject(remoteResponse)
            val array: JSONArray = o.getJSONArray("records")
            val names = arrayOfNulls<String>(array.length())

            val items: List<rgModel> =
                Gson().fromJson<List<rgModel>>(
                    array.toString(),
                    object : TypeToken<List<rgModel?>?>() {}.type
                )

            Collections.reverse(items);

            contactList!!.clear()

            contactList!!.addAll(items)
            mAdapter!!.notifyDataSetChanged()

            mProgressLayout!!.visibility = View.GONE
            errorNull!!.visibility = View.GONE


            //    Log.d("onSuccess1", firstSport.toString())

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mToolbar!!.inflateMenu(R.menu.menu_items);





        val item = menu?.findItem(R.id.action_share)
        val item2 = menu?.findItem(R.id.join)
        val item3 = menu?.findItem(R.id.invites)
        val item4 = menu?.findItem(R.id.about)
        val item5 = menu?.findItem(R.id.logout)
        val item6 = menu?.findItem(R.id.action_invite)


        item?.isVisible = false
        item2?.isVisible = false
        item3?.isVisible = false
        item4?.isVisible = false
        item5?.isVisible = false
        item6?.isVisible = false



        val searchManager =
            getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu!!.findItem(R.id.action_search)
            .actionView as SearchView
        searchView!!.setSearchableInfo(
            searchManager
                .getSearchableInfo(componentName)
        )
        searchView!!.maxWidth = Int.MAX_VALUE

        // listening to search query text change
        searchView!!.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                mAdapter!!.filter.filter(query)

                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                mAdapter!!.filter.filter(query)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu);
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
                BackAlert()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }


   fun BackAlert() {
       AlertDialog.Builder(this)
           .setMessage("Are you sure want to go back?")
           .setCancelable(false)
           .setPositiveButton("Yes") { _, id ->
               startActivity(Intent(this@ActiveAlerts, HomePage::class.java))
           }
           .setNegativeButton("No", null)
           .show().withCenteredButtons()
   }




    override fun onBackPressed() {
        // close search view on back button pressed
        if (!searchView!!.isIconified) {
            searchView!!.isIconified = true
            return
        }
        BackAlert()

    }

    override fun onContactSelected(contact: rgModel?) {
//           Toast.makeText(
//            this,
//            "Selected: " +contact!!.id ,
//            Toast.LENGTH_LONG
//        ).show()

        alert_rlss = contact!!.rl.toString()
        alert_id = contact!!.id.toString()



        AlertStatus()
    }

    fun AlertStatus() {

        AlertDialog.Builder(this)
            .setMessage("Change status of this Alat??")
            .setCancelable(true)
            .setPositiveButton("Neutralized") { _, id ->
                //  accept()
                getRGNAMES()
                mProgress!!.show()
                // startActivity(Intent(this@FriendRequests, HomePage::class.java))
            }
            .setNeutralButton("View Details") { _, id ->
                val i =
                    Intent(this, AlertReport::class.java)
                i.putExtra("alertSelect",alert_id)
                i.putExtra("level", alert_rlss)
                startActivity(i)
            }
            .setNegativeButton("Withdraw") { _, id ->
                // reject()
                getRGNAME()
                mProgress!!.show()
            }
            .show().withCenteredButtonss()
    }


    private fun AlertDialog.withCenteredButtonss() {

        val positive = getButton(AlertDialog.BUTTON_POSITIVE)
        val neutral = getButton(AlertDialog.BUTTON_NEUTRAL)

        val negative = getButton(AlertDialog.BUTTON_NEGATIVE)


        negative.setTextColor(ContextCompat.getColor(context, R.color.error))


        positive.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))

        neutral.setTextColor(ContextCompat.getColor(context, R.color.success))





    }

    private fun AlertDialog.withCenteredButtons() {
        val positive = getButton(AlertDialog.BUTTON_POSITIVE)
        val negative = getButton(AlertDialog.BUTTON_NEGATIVE)

        negative.setTextColor(ContextCompat.getColor(context, R.color.error))


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





    private fun getRGNAME() {

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

        params["id"] = alert_id!!

        val api: ALertRG = retrofit.create(ALertRG::class.java)
        val call: Call<ResponseBody> = api.getrg(params)


        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Log.d("Responsestring", response.body().toString())
                //Toast.makeText()
                if (response.isSuccessful) {
                    val jsonresponse = response.body()!!.string()
                    parseLoginDatassss(jsonresponse)

                } else {
                    Log.d("bayo", response.errorBody()!!.string())
                    errorNull!!.visibility = View.VISIBLE
                    mProgressLayout!!.visibility = View.GONE

                    // Toast.makeText(context,"Nothing" +  response.errorBody()!!.string(),Toast.LENGTH_LONG).show();
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")


                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                //   btn.text = "Proceed"
                Log.i("onEmptyResponse", "" + t) //
                // Toast.makeText(context,"Nothing ",Toast.LENGTH_LONG).show();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //mProgress?.dismiss()
            }
        })
    }

    private fun parseLoginDatassss(jsonresponse: String) {

        try {
            val jArray = JSONArray(jsonresponse)
            for (i in 0 until jArray.length()) {
                val json_obj: JSONObject = jArray.getJSONObject(i)

                rg =json_obj.getString("rg")
                delete()
             }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun getRGNAMES() {

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

        params["id"] = alert_id!!

        val api: ALertRG = retrofit.create(ALertRG::class.java)
        val call: Call<ResponseBody> = api.getrg(params)


        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Log.d("Responsestring", response.body().toString())
                //Toast.makeText()
                if (response.isSuccessful) {
                    val jsonresponse = response.body()!!.string()
                    parseLoginDatasssSS(jsonresponse)

                } else {
                    Log.d("bayo", response.errorBody()!!.string())
                    errorNull!!.visibility = View.VISIBLE
                    mProgressLayout!!.visibility = View.GONE

                    // Toast.makeText(context,"Nothing" +  response.errorBody()!!.string(),Toast.LENGTH_LONG).show();
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")


                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                //   btn.text = "Proceed"
                Log.i("onEmptyResponse", "" + t) //
                // Toast.makeText(context,"Nothing ",Toast.LENGTH_LONG).show();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //mProgress?.dismiss()
            }
        })
    }

    private fun parseLoginDatasssSS(jsonresponse: String) {

        try {
            val jArray = JSONArray(jsonresponse)
            for (i in 0 until jArray.length()) {
                val json_obj: JSONObject = jArray.getJSONObject(i)

                rg =json_obj.getString("rg")
                neutralized()
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

        params["id"] = alert_id!!
        params["rg"] = rg!!


        val api: UpdateAlertStatus = retrofit.create(UpdateAlertStatus::class.java)
        val call: Call<ResponseBody> = api.Update(params)

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
                        parseLoginDatas(remoteResponse)
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






    private fun delete() {

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

        params["id"] = alert_id!!
        params["rg"] = rg!!

        val api: ALertDelete = retrofit.create(ALertDelete::class.java)
        val call: Call<ResponseBody> = api.Delete(params)

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


                    parseLoginDatass(remoteResponse)

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

    private fun parseLoginDatass(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {
                dialogue();
                promptPopUpView?.changeStatus(2, "Alert withdrawal was successfully")

                mProgress!!.dismiss()

            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                mProgress!!.dismiss()

                // Toast.makeText(this@AddTopicActivity, "Please Something went wrong", Toast.LENGTH_LONG).show()

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }




    private fun dialogue() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                recreate()
            }

            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    private fun dialogue_error() {
        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                recreate()
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }
}
