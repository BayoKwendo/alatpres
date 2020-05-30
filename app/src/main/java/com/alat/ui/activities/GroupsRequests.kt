package com.alat.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
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
import com.alat.adapters.FRGAdapter
import com.alat.helpers.Constants
import com.alat.helpers.MyDividerItemDecoration
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.FriendRGs
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
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class GroupsRequests() : AppCompatActivity(),
    FRGAdapter.ContactsAdapterListener{

    private var toolbar : Toolbar? = null


    private val TAG = HomePage::class.java.simpleName
    private var recyclerView: RecyclerView? = null
    private var contactList: MutableList<rgModel>? = null
    private var mAdapter: FRGAdapter? = null
    private var searchView: SearchView? = null
    private var edtEmail: EditText? = null
    private var mProgressLayout: LinearLayout? = null

    private var preferenceHelper: PreferenceModel? = null
    private var promptPopUpView: PromptPopUpView? = null
    var mToolbar: Toolbar? = null

    private var btnResetPassword: Button? = null
    private var btnBack: Button? = null
    var errorNull: TextView? = null
    private var mProgress: ProgressDialog? = null
    var pref: SharedPreferences? = null

    var MYCODE = 1000

    var userid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)
        mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar!!);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Your RGs ";

        preferenceHelper = PreferenceModel(this)
        recyclerView = findViewById(R.id.recycler_view)
        errorNull =  findViewById(R.id.texterror)
        contactList = ArrayList()
        mAdapter = FRGAdapter(this, contactList!!, this)




        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode
        userid = pref!!.getString("userid", null)
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

        val params: HashMap<String, String> = HashMap()
        params["userid"] = userid!!


        val api: FriendRGs = retrofit.create(FriendRGs::class.java)
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


//    private fun dialogue() {
//
//        promptPopUpView = PromptPopUpView(activity)
//
//        AlertDialog.Builder(activity!!)
//            .setPositiveButton("PROCESSING....") { _: DialogInterface?, _: Int ->
//                //      finish()
//
//            }
//            .setCancelable(false)
//            .setView(promptPopUpView)
//            .show()
//    }
//    private fun dialogue_error() {
//
//        promptPopUpView = PromptPopUpView(activity)
//
//        AlertDialog.Builder(activity!!)
//            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
//
//            }
//            .setCancelable(false)
//            .setView(promptPopUpView)
//            .show()
//    }

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



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        when (id) {
           android.R.id.home -> {
               BackAlert()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }



    override fun onBackPressed() {
        BackAlert()
    }

    fun BackAlert() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure want to go back?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, id ->
                startActivity(Intent(this@GroupsRequests, HomePage::class.java))
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


        val i = Intent(this, FriendRequests::class.java)
        i.putExtra("groupSelect", contact!!.id.toString())
        //i.putExtra("groupName", contact.group_name)

        startActivity(i)



    }

}




