package com.alat.ui.activities

import android.app.ProgressDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
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
import com.alat.adapters.AlertAdapter
import com.alat.adapters.JGAdapter
import com.alat.helpers.Constants
import com.alat.helpers.MyDividerItemDecoration
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.*
import com.alat.model.PreferenceModel
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
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.collections.set

@Suppress("UNREACHABLE_CODE")

class ExistGroup : AppCompatActivity(), JGAdapter.ContactsAdapterListener  {

        private var toolbar : Toolbar? = null
        private val TAG = HomePage::class.java.simpleName
        private var recyclerView: RecyclerView? = null
        private var contactList: MutableList<rgModel>? = null
        private var mAdapter: JGAdapter? = null
        private var searchView: SearchView? = null
        private var edtEmail: EditText? = null
        private var mProgressLayout: LinearLayout? = null
        private var preferenceHelper: PreferenceModel? = null
        private var promptPopUpView: PromptPopUpView? = null
        private var btnResetPassword: Button? = null
        private var btnBack: Button? = null
        var errorNull: TextView? = null
        private var mProgress: ProgressDialog? = null
        var pref: SharedPreferences? = null

    var mToolbar: Toolbar? = null

    var global: CardView? = null
        var MYCODE = 1000
    private var userid: String? = null
    private var rg_ids: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)
        mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar!!);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Exit RG";

        pref =
            this!!.getSharedPreferences("MyPref", 0) // 0 - for private mode


        userid = pref!!.getString("userid", null)
            recyclerView = findViewById(R.id.recycler_view)
            errorNull = findViewById(R.id.texterror)
            contactList = ArrayList()
            mAdapter = JGAdapter(this, contactList!!, this)
           // global= findViewById(R.id.cardactive)
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

//            global!!.setOnClickListener {
//                startActivity(Intent(this, JoinGlobal::class.java))
//            }

        getStudent()

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

        val params: java.util.HashMap<String, String> = java.util.HashMap()
        params["userid"] = userid!!


        val api: FriendYourRGs = retrofit.create(FriendYourRGs::class.java)
        val call: Call<ResponseBody> = api.fRGs(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Log.d("Responsestring", response.body().toString())
                //Toast.makeText()
                if (response.isSuccessful) {
                    if (response.body() != null) {

                        // val jsonresponse = response.body().toString()

                        // Log.d("onSuccessS", response.errorBody()!!.toString())


                        if (response.code().toString() == "200"){
                            errorNull!!.visibility = View.VISIBLE
                            mProgressLayout!!.visibility = View.GONE
                        }


                        try {

                            Log.d("SUCCESS", response.body().toString())
                            val o = JSONObject(response.body()!!.string())
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
                    } else {
                        errorNull!!.visibility = View.VISIBLE
                        mProgressLayout!!.visibility = View.GONE

                        Log.i("onEmptyResponse", "Returned empty response") //Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Log.d("bayo", response.errorBody()!!.string())
                    errorNull!!.visibility = View.VISIBLE
                    mProgressLayout!!.visibility = View.GONE

                    internet()
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")


                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                //   btn.text = "Proceed"
                Log.i("onEmptyResponse", "" + t) //

                internet()
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //mProgress?.dismiss()
            }

        })
    }



        private fun dialogue() {

            promptPopUpView = PromptPopUpView(this)

            AlertDialog.Builder(this)
                .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                    //      finish()
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

                }
                .setCancelable(false)
                .setView(promptPopUpView)
                .show()
        }

        private fun internet() {
//        promptPopUpView = PromptPopUpView(activity)
//
//        AlertDialog.Builder( activity!!)
//
//            .setPositiveButton(
//                "Retry"
//            ) { dialog, _ -> dialog.dismiss()
//                activity!!.recreate()
//            }
//         .setNegativeButton(
//            "Cancel"
//         ) { dialog, _ -> dialog.dismiss() }
//
//            .setCancelable(false)
//            .setView(promptPopUpView)
//            .show().withCenteredButtons()
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



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        mToolbar!!.inflateMenu(R.menu.menu_items);




        var item = menu?.findItem(R.id.action_share)
        val item2 = menu?.findItem(R.id.join)
        val item3 = menu?.findItem(R.id.invites)
        val item4 = menu?.findItem(R.id.about)
        val item5 = menu?.findItem(R.id.logout)

        item?.isVisible = false
        item2?.isVisible = false
        item3?.isVisible = false
        item4?.isVisible = false
        item5?.isVisible = false





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
                    backs()
                }


                else -> super.onOptionsItemSelected(item)
            }

            return true
        }



       override fun onBackPressed() {
           if (!searchView!!.isIconified()) {
               searchView!!.isIconified = true
               return
           }  else {
               backs()
           }
       }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == MYCODE) {
                if (!searchView!!.isIconified()) {
                    searchView!!.isIconified = true
                    return
                }
                // do something good
            }
        }

        override fun onContactSelected(contact: rgModel?) {


//            val i = Intent(this, GroupID::class.java)
//            i.putExtra("groupSelect", contact!!.id.toString())
//            i.putExtra("groupName", contact.group_name)
//
//            startActivity(i)
            rg_ids = contact!!.id.toString()

            AlertStatus()


        }








    fun backs() {

        AlertDialog.Builder(this)
            .setMessage("Back to Homepage")
            .setCancelable(true)
            .setPositiveButton("Yes") { _, id ->
                //  accept()
//                neutralized()
                // mProgress!!.show()
                startActivity(Intent(this@ExistGroup, HomePage::class.java))

            }
            .setNegativeButton("No") { _, _ ->
                // reject()
                //  getRGNAME()
            }
            .show().withCenteredButtonss()
    }








    fun AlertStatus() {

        AlertDialog.Builder(this)
            .setMessage("Exit Response Group\n")
            .setCancelable(true)
            .setPositiveButton("Yes") { _, id ->
                //  accept()
//                neutralized()
               // mProgress!!.show()
                exit()
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


        params["rg_id"] = rg_ids!!
        params["userid"] = userid!!

        val api: existRG = retrofit.create(existRG::class.java)
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


}






