package com.alat.ui.activities

import android.app.Activity
import android.app.ProgressDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
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
import com.alat.Permission
import com.alat.R
import com.alat.adapters.CustomeAdapter
import com.alat.helpers.Constants
import com.alat.helpers.MyDividerItemDecoration
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.*
import com.alat.model.rgModel
import com.alat.ui.activities.auth.LoginActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.karn.notify.Notify
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
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.collections.set

@Suppress("UNREACHABLE_CODE")
class Level_2 : AppCompatActivity(), CustomeAdapter.ContactsAdapterListener  {

   var response_group: String? = null

    private val TAG = Level_2::class.java.simpleName
    private var recyclerView: RecyclerView? = null
    private var contactList: MutableList<rgModel>? = null
    private var mAdapter: CustomeAdapter? = null
    private var searchView: SearchView? = null
    private var mProgressLayout: LinearLayout? = null

    private var promptPopUpView: PromptPopUpView? = null


    var fistname: String? = null

    var lastnames: String? = null

    var pref: SharedPreferences? = null


    var iduser: String? = null

    var mssdns: String? = null

    var emails: String? = null

    var nature_response: String? = null
    var userid: String? = null

    var rg_id: String? = null
    var rg_namessss: String? = null

    private val RESULT_PICK_CONTACT = 1001

    private var btnResetPassword: Button? = null
    private var btnBack: Button? = null
    var errorNull: TextView? = null
    private var mProgress: ProgressDialog? = null

    private var mProgressS: ProgressDialog? = null

    var MYCODE = 1000

    private var mProgressSendin: ProgressDialog? = null

    private var fullname: String? = null
    private var mssidn: String? = null


    var mToolbar: Toolbar? = null

    var btn1: Button? = null

    var submit: Button? = null
    private var mProgressSending: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar!!);


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = response_group;



        lastnames = "NULL"

        Notify.defaultConfig {
            header {
                color = resources.getColor(R.color.colorPrimaryDark)
            }
            alerting(Notify.CHANNEL_DEFAULT_KEY) {
                lightColor = Color.RED
            }
        }

        emails = "NULL"

        nature_response = "NULL"

        userid = "NULL"

        pref =
            getSharedPreferences("MyPref", 0) // 0 - for private mode

        fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

        mssidn = pref!!.getString("mssdn", null)

        iduser = pref!!.getString("userid", null)

        rg_id = intent.getStringExtra("rg_id")

        rg_namessss = intent.getStringExtra("rg_namess")





        recyclerView = findViewById(R.id.recycler_view)

        btn1 = findViewById(R.id.btn_config)

        btn1!!.setOnClickListener {
            pickContact()
        }
        submit = findViewById(R.id.next)

        submit!!.text= "FINISH"


        submit!!.setOnClickListener {
            submitLevel()
        }

        errorNull = findViewById(R.id.texterror)
        contactList = ArrayList()
        mAdapter = CustomeAdapter(this, contactList!!, this)

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

        mToolbar!!.title = "Level 3 Configurations"

        recyclerView!!.adapter = mAdapter
        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Configuring...")
        mProgress!!.setCancelable(false)

        mProgressS = ProgressDialog(this)
        mProgressS!!.setMessage("Submitting...")
        mProgressS!!.setCancelable(false)



        mProgressSendin = ProgressDialog(this)
        mProgressSendin!!.setMessage("Sending sms...")
        mProgressSendin!!.setCancelable(false)

        mProgressLayout!!.visibility = View.VISIBLE
        errorNull!!.visibility = View.GONE

        getStudent()
title
    }


    private fun getStudent() {
        val group_name: String? = null
        val alerts = 0


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
       // params["rg"] = response_group_name!!

        val api: GetProviders = retrofit.create(GetProviders::class.java)
        val call: Call<ResponseBody> = api.findRGID(params)


        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Log.d("Responsestring", response.body().toString())
                //Toast.makeText()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Log.d("onSuccess", response.body().toString())
                        val jsonresponse = response.body()!!.string()


//                        if (response.code().toString() == "200") {
//                            errorNull!!.visibility = View.VISIBLE
//                            mProgressLayout!!.visibility = View.GONE
//                        }

                        parseLoginData(jsonresponse)
                    } else {
//                        errorNull!!.visibility = View.VISIBLE
//                        mProgressLayout!!.visibility = View.GONE

                        Log.i("onEmptyResponse", "Returned empty response")
                        //   Toast.makeText(context,"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("bayo", response.errorBody()!!.string())
//                    errorNull!!.visibility = View.VISIBLE
//                    mProgressLayout!!.visibility = View.GONE
//
//                    // Toast.makeText(context,"Nothing" +  response.errorBody()!!.string(),Toast.LENGTH_LONG).show();


//                    internet()
//                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")


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
    fun pickContact() {
        val contactPickerIntent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        )
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RESULT_PICK_CONTACT -> {
                    var cursor: Cursor? = null
                    try {

                        // getData() method will have the
// Content Uri of the selected contact
                        val uri: Uri? = data!!.data
                        //Query the content uri
                        cursor = contentResolver.query(uri!!, null, null, null, null)
                        cursor!!.moveToFirst()
                        // column index of the phone number
                        val phoneIndex: Int = cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        )
                        // column index of the contact name
                        val nameIndex: Int = cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        )
                        mssdns = cursor.getString(phoneIndex)
                        fistname = cursor.getString(nameIndex)
                        // Set the value to the textviews
//                        Toast.makeText(this, "Selected: " +contactNumber ,
//                       Toast.LENGTH_LONG
//                         ).show()
                        BackAlerts()
//                        tvContactName.setText("Contact Name : $contactName")
//                        tvContactNumber.setText("Contact Number : $contactNumber")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
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
               startActivity(Intent(this@Level_2, HomePage::class.java))
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

        contact!!.checked = true

        BackAlerts()

        fistname = contact.firstname

        lastnames = contact.lastname

        mssdns = contact.mssdn

        emails = contact.email

        nature_response = contact.nature_response

        userid = contact.userid





//
//        val i =
//            Intent(this, AlertDetails::class.java)
//             i.putExtra("alertSelect", contact!!.id.toString())
//             i.putExtra("level", contact.rl)
//        startActivity(i)
    }


    fun BackAlerts() {
//
//        Toast.makeText(
//            this,
//            "Selected: " +  user_idNo,
//            Toast.LENGTH_LONG
//        ).show()

        AlertDialog.Builder(this)
            .setMessage("Add this response provider to your group?\n\n")
            .setCancelable(true)
                .setPositiveButton("Add") { _, id ->
                //accept()
                mProgress!!.show()
                    config()
                // startActivity(Intent(this@FriendRequests, HomePage::class.java))
            }
            .setNeutralButton("Cancel"){_, id->

            }
            .setNegativeButton("Remove"){_, id ->
               // reject()
                deleteConfig()
                mProgress!!.show()

                // mProgress!!.show()
            }
            .show().withCenteredButtonss()
    }

    private fun AlertDialog.withCenteredButtonss() {
        val positive = getButton(AlertDialog.BUTTON_POSITIVE)
        val negative = getButton(AlertDialog.BUTTON_NEGATIVE)
        val neutral = getButton(AlertDialog.BUTTON_NEUTRAL)

        negative.setBackgroundColor(ContextCompat.getColor(context, R.color.error))
        negative.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))

        neutral.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))

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


    private fun config() {

        // Toast.makeText(this@Enterprise, "VALUE" + selectedItem2, Toast.LENGTH_LONG).show();

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

            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val params: HashMap<String, String> = HashMap()
        params["firstname"] = fistname!!
        params["lastname"] = lastnames!!
        params["email"] = emails!!
        params["mssdn"] = mssdns!!
        params["rg_id"] = rg_id!!
        params["nature_response"] = nature_response!!
        params["userid"] = userid!!

        val api: CONFIGlv3 = retrofit.create(CONFIGlv3::class.java)
        val call: Call<ResponseBody> = api.config(params)

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

                    if (response.code() == 400) {
                        mProgress?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Unable to create Provider! please try again")
                    } else if (response.code() == 200) {

                        mProgress?.dismiss()


                        dialogue()
                        promptPopUpView?.changeStatus(2, "Response provider was added successful!")



                    } else  if (response.code() == 201) {
                        mProgress?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Provider has been configured already")
                    }


                } else {
                    mProgress?.dismiss()
                    dialogue_error()
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.errorBody()!!.string())
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







    private fun deleteConfig() {

        // Toast.makeText(this@Enterprise, "VALUE" + selectedItem2, Toast.LENGTH_LONG).show();

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

            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val params: HashMap<String, String> = HashMap()
        params["mssdn"] = mssdns!!
        params["rg_id"] = rg_id!!
        params["userid"] = userid!!

        val api: deleteConfig3 = retrofit.create(deleteConfig3::class.java)
        val call: Call<ResponseBody> = api.config(params)

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

                    if (response.code() == 400) {
                        mProgress?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Unable! please try again")
                    } else if (response.code() == 200) {

                        mProgress?.dismiss()


                        dialogue()
                        promptPopUpView?.changeStatus(2, "Response provider was removed successful!")



                    } else  if (response.code() == 201) {
                        mProgress?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Provider has not yet been config")
                    }


                } else {
                    mProgress?.dismiss()
                    dialogue_error()
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.errorBody()!!.string())
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


    private fun submitLevel() {
       mProgressS!!.show()
        // Toast.makeText(this@Enterprise, "VALUE" + selectedItem2, Toast.LENGTH_LONG).show();

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

            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val params: HashMap<String, String> = HashMap()

        params["rg_id"] = rg_id!!

        params["userid"] = iduser!!

        val api: subMitLv = retrofit.create(subMitLv::class.java)
        val call: Call<ResponseBody> = api.submit(params)

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

                    if (response.code() == 400) {
                        mProgressS?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Unable to Submitr! please try again")
                    } else if (response.code() == 200) {


                        parseLoginDataasassss(remoteResponse)




                    } else  if (response.code() == 201) {
                        mProgressS?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Unable to Submit! please try again")
                    }


                } else {
                    mProgressS?.dismiss()
                    dialogue_error()
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.errorBody()!!.string())
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


    private fun parseLoginDataasassss(remoteResponse: String) {
        try {


            val jsonObject = JSONObject(remoteResponse)
            // val jsonObject = JSONObject(jsonresponse)


            if (jsonObject.getString("status") == "true") {

                mProgressS?.dismiss()


                dialoguess()
                promptPopUpView?.changeStatus(2, "Response Group was configured successfully")

            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                mProgressS!!.dismiss()


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }



    private fun level1() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)

            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }
    private fun dialogue() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)


            .setPositiveButton(
                "Ok"
            ) { dialog, _ ->
                val i = Intent(this, Level_2::class.java)
                i.putExtra("rg_id", rg_id)
                i.putExtra("rg_namess", rg_namessss )

                startActivity(i)
                dialog.dismiss()
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    private fun dialoguess()   {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)


            .setPositiveButton(
                "Finish"
            ) { dialog, _ ->
                dialog.dismiss()

                Handler().postDelayed({
                    val intent = Intent(this@Level_2, HomePage::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }, 2000)

                Notify
                    .with(this)
                    .asTextList {
                        lines = Arrays.asList(
                            "Your group was created successfully",
                            "Group Name\t" +rg_namessss,
                            "Group ID\t" +rg_id )
                        title = "Group Details!"
                        text = "Group Name\t"+rg_namessss+",\t" + "Group ID\t" +rg_id
                    }
                    .show()


            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }


    private fun dialogue_error() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
        AlertDialog.Builder(this)

            .setPositiveButton(
                "Ok"
            ) { dialog, _ ->
                val i = Intent(this, Level_2::class.java)
                i.putExtra("rg_id", rg_id)
                i.putExtra("rg_namess", rg_namessss )

                startActivity(i)
                dialog.dismiss()
            }


            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }



    private fun mssdn() {

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
        params["rg_id"] = rg_id!!
        params["rg_name"] = rg_namessss!!

        val api: sendConfirm = retrofit.create(sendConfirm::class.java)
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
                        mProgressSendin?.dismiss()
                        dialogued();


                    } else {
                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                        Log.d("BAYO", response.code().toString())
                        mProgressSending?.dismiss()
                    }
                } else {
                    dialogue_error()
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    mProgressSending?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
                mProgress?.dismiss()
            }
        })
    }



    private fun dialogued() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("ok") { _: DialogInterface?, _: Int ->
                startActivity(Intent(this@Level_2, HomePage::class.java))

            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show().withCenteredButtons()
    }


}
