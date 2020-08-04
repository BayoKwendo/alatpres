package com.alat.ui.activities

import android.app.Activity
import android.app.ProgressDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alat.HomePage
import com.alat.R
import com.alat.adapters.MembersAdapter
import com.alat.helpers.Constants
import com.alat.helpers.MyDividerItemDecoration
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.*
import com.alat.model.PreferenceModel
import com.alat.model.rgModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rengwuxian.materialedittext.MaterialEditText
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.layout_member.*
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
import kotlin.collections.set

@Suppress("UNREACHABLE_CODE")

class GroupMembers : AppCompatActivity(), MembersAdapter.ContactsAdapterListener {

    private var recyclerView: RecyclerView? = null
    private var contactList: MutableList<rgModel>? = null
    private var mAdapter: MembersAdapter? = null
    private var searchView: SearchView? = null
    private var mProgressLayout: LinearLayout? = null
    private var promptPopUpView: PromptPopUpView? = null
    var floatingActionButton: FloatingActionButton? = null
    var alert: android.app.AlertDialog? = null
    var errorNull: TextView? = null
    private var mProgress: ProgressDialog? = null
    var pref: SharedPreferences? = null
    var mToolbar: Toolbar? = null
    var contactNumber: String? = null
    var contactName: String? = null
    var global: CardView? = null
    var MYCODE = 1000
    private var userid: String? = null
    private var rg_ids: String? = null
    private var rg: String? = null
    private var rg_name: String? = null
    private val RESULT_PICK_CONTACT = 1001
    var fname: String? = null
    var updateFName: String? = null
    var updateFNamee: EditText? = null
    var waitingDialog: android.app.AlertDialog? = null
    private var mssidns: String? = null
    private var fullnames: String? = null
    private var account: String? = null
    private var usersid: String? = null
    private var muserid: String? = null


    private var mstatus: String? = null
    private var mrg_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar!!);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Group Members";
        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Processing...")
        mProgress!!.setCancelable(true)
        pref =
            this!!.getSharedPreferences("MyPref", 0) // 0 - for private mode
        muserid = pref!!.getString("userid", null)
        account = pref!!.getString("account_status", null)


        rg = intent.getStringExtra("groupID")
        rg_name = intent.getStringExtra("groupNAME")
        floatingActionButton =
            findViewById<View>(R.id.floating_action_button) as FloatingActionButton
        userid = pref!!.getString("userid", null)
        recyclerView = findViewById(R.id.recycler_view)
        errorNull = findViewById(R.id.texterror)
        contactList = ArrayList()
        mAdapter = MembersAdapter(this, contactList!!, this)
        // global= findViewById(R.id.cardactive)
        mProgressLayout = findViewById(R.id.layout_discussions_progress);
        pref =
            getSharedPreferences("MyPref", 0) // 0 - for private mode
        fname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

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

//            global!!.setOnClickListener {
//                startActivity(Intent(this, JoinGlobal::class.java))
//            }
        floatingActionButton!!.setOnClickListener {
            addMember()
           // startActivity(Intent(activity!!, CreateAlert::class.java))
        }
        getMembers()

    }


    private fun getMembers() {
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
        params["rg_id"] = rg!!


        val api: ViewMmembers = retrofit.create(ViewMmembers::class.java)
        val call: Call<ResponseBody> = api.viewM(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Log.d("Responsestring", response.body().toString())
                //Toast.makeText()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            Log.d("SUCCESS", response.body().toString())
                            val o = JSONObject(response.body()!!.string())

                            if (o.getString("status") == "true") {
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
                            } else if (o.getString("status") == "false"){
                                errorNull!!.visibility = View.VISIBLE
                                mProgressLayout!!.visibility = View.GONE
                            }
                            //    Log.d("onSuccess1", firstSport.toString())
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        errorNull!!.visibility = View.VISIBLE
                        mProgressLayout!!.visibility = View.GONE
                      //Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("bayo", response.errorBody()!!.string())
                    errorNull!!.visibility = View.VISIBLE
                    mProgressLayout!!.visibility = View.GONE
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        mToolbar!!.inflateMenu(R.menu.menu_items);
        var item = menu?.findItem(R.id.action_share)
        val item2 = menu?.findItem(R.id.join)
        val item3 = menu?.findItem(R.id.invites)
        val item4 = menu?.findItem(R.id.about)
        val item5 = menu?.findItem(R.id.logout)
        val item6 = menu?.findItem(R.id.aboutus)


        item?.isVisible = false
        item2?.isVisible = false
        item3?.isVisible = false
        item4?.isVisible = false
        item5?.isVisible = false
        item6?.isVisible = false


        // Associate searchable configuration with the SearchView
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
        val id = item.itemId
        when (id) {
            R.id.action_search -> {
                true
            }
            android.R.id.home -> {
                finish()
            }
            R.id.action_invite -> {
                pickContact()
                true
            }


            else -> super.onOptionsItemSelected(item)
        }

        return true
    }


    override fun onBackPressed() {
        if (!searchView!!.isIconified()) {
            searchView!!.isIconified = true
            return
        } else {
            finish()
        }
    }


    override fun onContactSelected(contact: rgModel?) {
       // rg_ids = contact!!.id.toString()

        usersid = contact!!.userid.toString()
        mstatus = contact!!.status.toString()
        mrg_id = contact!!.rg_id.toString()



        AlertStatus()


    }




    fun AlertStatus() {

        AlertDialog.Builder(this)
            .setMessage("Remove this Member?\n")
            .setCancelable(true)
            .setPositiveButton("Yes") { _, id ->
                if(usersid == muserid && mstatus == "1" ){
                      exit()
                 //   Toast.makeText(this@GroupMembers, "Youre not an admin", Toast.LENGTH_LONG).show()
                }else {
                    Toast.makeText(this@GroupMembers, "You're not an  admin", Toast.LENGTH_LONG).show()

                }

            }
            .setNegativeButton("cancel") { _, _ ->
                // reject()
                //  getRGNAME()
            }
            .show().withCenteredButtonss()
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


    private fun exit() {

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


        params["rg_id"] = rg!!
        params["userid"] = usersid!!

        val api: removeRG = retrofit.create(removeRG::class.java)
        val call: Call<ResponseBody> = api.exit(params)

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
                        parseLoginDatass(remoteResponse)
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

    private fun parseLoginDatass(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {
                dialogue();
                promptPopUpView?.changeStatus(2, "Done!")
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
                        contactNumber = cursor.getString(phoneIndex)
                        contactName = cursor.getString(nameIndex)
                        // Set the value to the textviews
//                        Toast.makeText(this, "Selected: " +contactNumber ,
//                       Toast.LENGTH_LONG
//                         ).show()
                        Confirm()
//                        tvContactName.setText("Contact Name : $contactName")
//                        tvContactNumber.setText("Contact Number : $contactNumber")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else   if (requestCode == MYCODE) {
            if (!searchView!!.isIconified()) {
                searchView!!.isIconified = true
                return
            }
            // do something good
        }
    }

    fun Confirm() {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Send Invitation to "+ contactName)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, id ->
                checkMmeber()
                mProgress!!.show()
            }
            .setNegativeButton("No", null)
            .show().withCenteredButton()
    }

    private fun AlertDialog.withCenteredButton() {
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

    private fun checkMmeber() {

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
        //  response_id = intent.getStringExtra("groupSelect")

        params["mssdn"] = contactNumber!!
        params["rg_id"] = rg!!
        params["rg_name"] = rg_name!!
        params["name"] = fname!!

        val api: sendSMS = retrofit.create(sendSMS::class.java)
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
                        promptPopUpView?.changeStatus(2, "Invitation to " + contactName+ " was send successfuly")

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

    fun hideKeyboardFrom() {
        val view: View = currentFocus ?: View(this)

        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyBoard() {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun addMember() {

        val alertDialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
       alertDialog.setTitle("Add New Member")

        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val layout_pwd: View =
            inflater.inflate(R.layout.layout_member, null)
        alertDialog.setView(layout_pwd)
        alert = alertDialog.create()
        updateFNamee = layout_pwd.findViewById<View>(R.id.input_pin) as EditText
        updateFNamee!!.requestFocus()
        showKeyBoard()

        val updateButton: Button =
            layout_pwd.findViewById<View>(R.id.update) as Button
        updateButton.setOnClickListener(View.OnClickListener {

            waitingDialog =
                SpotsDialog.Builder().setContext(this).build()

            updateFName = updateFNamee!!.getText().toString()

            updateFNamee!!.clearFocus()

            if (Utils.checkIfEmptyString(updateFName)) {
                updateFNamee!!.error = "Members ID is Mandatory"
                updateFNamee!!.requestFocus()
            } else {
                waitingDialog!!.show()
                getUserDetails()
            }
        })
        val dismissButton: Button =
            layout_pwd.findViewById<View>(R.id.cancel) as Button
        dismissButton.setOnClickListener(View.OnClickListener {
            alert!!.dismiss()
            hideKeyboardFrom()
        })
        val inviteButton: Button =
            layout_pwd.findViewById<View>(R.id.btn_invite) as Button
        inviteButton.setOnClickListener(View.OnClickListener {
            alert!!.dismiss()
            pickContact()
            hideKeyboardFrom()
        })

        alertDialog.setView(layout_pwd)
        alert!!.setCancelable(false)
        alert!!.show()

    }



    private fun getUserDetails() {
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
        params["userid"] = updateFName!!
        val api: GetUserDetails = retrofit.create(GetUserDetails::class.java)
        val call: Call<ResponseBody> = api.getDetails(params)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());

                if (response.isSuccessful) {
                    val remoteResponse = response.body()!!.string()
                    Log.d("test", remoteResponse)

                    parseLoginDatta(remoteResponse)
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

    private fun parseLoginDatta(remoteResponse: String) {
        try {

            val jArray3 = JSONArray(remoteResponse)
            for (i in 0 until jArray3.length()) {
                val jsonObjec: JSONObject = jArray3.getJSONObject(i)
                if (jsonObjec.getString("status") == "true")
                {
                    fullnames =
                        jsonObjec.getString("firstname") + "\t" + jsonObjec.getString("lastname")
                    mssidns =
                        jsonObjec.getString("mssdn")
                    counts()
                }else if(jsonObjec.getString("status") == "false"){
                    waitingDialog!!.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "User not Registered in AlatPres")
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun inviteMember() {

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
        params["rg_id"] = rg!!
        params["userid"] = updateFName!!
        params["fullname"] = fullnames!!
        params["mssidn"] = mssidns!!

        val api: InviteMember = retrofit.create(InviteMember::class.java)
        val call: Call<ResponseBody> = api.invite(params)

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
                    parseLoginDataa(remoteResponse)
                } else {
                    waitingDialog?.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    mProgress?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                dialogue_error()
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
                waitingDialog?.dismiss()
            }
        })
    }

    private fun parseLoginDataa(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {
                dialogue();
                promptPopUpView?.changeStatus(2, "Member added!! \n Invitation was successfully sent")
                waitingDialog?.dismiss()

            }  else if (jsonObject.getString("status") == "false"){
                waitingDialog?.dismiss()
                dialogue_error();
                promptPopUpView?.changeStatus(1, "User has already been invited already!!")
            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                waitingDialog?.dismiss()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun counts() {

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
        params["rg_id"] = rg!!
        params["userid"] = updateFName!!

        val api: countMember = retrofit.create(countMember::class.java)
        val call: Call<ResponseBody> = api.countRG(params)

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
                    parseCheckerData(remoteResponse)
                } else {
                    mProgress?.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    waitingDialog!!.dismiss()

                    mProgress?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                dialogue_error()
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
                mProgress?.dismiss()
            }
        })
    }

    private fun parseCheckerData(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {
                hideKeyboardFrom()
                if (account == "0") {
                    waitingDialog!!.dismiss()
                    dialogue_error()
                    promptPopUpView?.changeStatus(1, "Upgrade to Pro to add more members")
                    waitingDialog!!.dismiss()
                } else if (account == "1") {
                    inviteMember()
                }
            } else if (jsonObject.getString("status") == "false")  {
                hideKeyboardFrom()
                inviteMember()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }




}






