package com.alat.ui.activities

import android.app.ProgressDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alat.HomePage
import com.alat.R
import com.alat.adapters.ResponseAdapter
import com.alat.helpers.Constants
import com.alat.helpers.MyDividerItemDecoration
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.GetAlerts
import com.alat.interfaces.GlobalDiscussion
import com.alat.interfaces.RespDiscussion
import com.alat.model.rgModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.set

@Suppress("UNREACHABLE_CODE")
class AlertGlobal : AppCompatActivity(), ResponseAdapter.ContactsAdapterListener  {

    var alert_id: String? = null

    //var response_group_name: String? = null
    private val TAG = AlertGlobal::class.java.simpleName
    private var recyclerView: RecyclerView? = null
    private var contactList: MutableList<rgModel>? = null
    private var mAdapter: ResponseAdapter? = null
    private var searchView: SearchView? = null
    private var mProgressLayout: LinearLayout? = null
    private var promptPopUpView: PromptPopUpView? = null
    private var btnResetPassword: Button? = null
    private var btnBack: Button? = null
    var errorNull: TextView? = null
    var addMessage: FloatingActionButton? = null
    private var mProgress: ProgressDialog? = null
        var group_id: String? = null


    var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)
        mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar!!);

        alert_id = intent.getStringExtra("alatpres_id")
        // response_group_name = intent.getStringExtra("groupName")

        //group_id = intent.getStringExtra("groupID")

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Global Response Group";

        addMessage = findViewById(R.id.fab_add_topic)



        addMessage!!.setOnClickListener {
            val i =
                Intent(this, AddGlobalActivity::class.java)
            i.putExtra("alertSelect", alert_id)
            i.putExtra("groupName", "Global Response Group")
            i.putExtra("groupID", "111")

            startActivity(i)

        }
        recyclerView = findViewById(R.id.rv_topics_list)
        errorNull = findViewById(R.id.texterror)

        contactList = ArrayList()
        mAdapter = ResponseAdapter(this, contactList!!, this)

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


        mToolbar!!.title = "Global Response Group"

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
        params["alertid"] = ""

        val api: GlobalDiscussion = retrofit.create(GlobalDiscussion::class.java)
        val call: Call<ResponseBody> = api.Global(params)

        call.enqueue( object : Callback<ResponseBody?> {
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
           // Collections.reverse(items);
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mToolbar!!.inflateMenu(R.menu.menu_items);

        // Associate searchable configuration with the SearchView


        var item = menu.findItem(R.id.action_share)
        val item2 = menu.findItem(R.id.join)
        val item3 = menu.findItem(R.id.invites)
        val item4 = menu.findItem(R.id.about)
        val item5 = menu.findItem(R.id.logout)
        //  val item6 = menu.findItem(R.id.action_invite)

        item.isVisible = false
        item2.isVisible = false
        item3.isVisible = false
        item4.isVisible = false
        item5.isVisible = false
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


    override fun onBackPressed() {
        // close search view on back button pressed
        if (!searchView!!.isIconified) {
            searchView!!.isIconified = true
            return
        }
        BackAlert()

    }


    fun BackAlert() {
        AlertDialog.Builder(this)
            .setMessage("Leaving Group for now??")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, id ->
                startActivity(Intent(this@AlertGlobal, HomePage::class.java))
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





    override fun onContactSelected(contact: rgModel?) {
//           Toast.makeText(
//            this,
//            "Selected: " +contact!!.id ,
//            Toast.LENGTH_LONG
//        ).show()

//
//        val i =
//            Intent(this, AlertDetails::class.java)
//             i.putExtra("alertSelect", contact!!.id.toString())
//             i.putExtra("level", contact.rl)
//        startActivity(i)
    }
}
