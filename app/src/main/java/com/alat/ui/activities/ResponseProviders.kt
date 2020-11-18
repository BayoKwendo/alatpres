package com.alat.ui.activities

import android.app.Activity
import android.app.ProgressDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.ShareActionProvider
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alat.HomePage
import com.alat.R
import com.alat.adapters.RespProvAdapter
import com.alat.helpers.Constants
import com.alat.helpers.MyDividerItemDecoration
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.CheckMember
import com.alat.interfaces.GetAlerts
import com.alat.interfaces.GetResponseProviders
import com.alat.interfaces.sendSMS
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
class ResponseProviders : Fragment(), RespProvAdapter.ContactsAdapterListener  {

    var response_group_id: String? = null

    //var response_group_name: String? = null


    private val TAG = ResponseProviders::class.java.simpleName
    private var recyclerView: RecyclerView? = null
    private var contactList: MutableList<rgModel>? = null
    private var mAdapter: RespProvAdapter? = null
    private var searchView: SearchView? = null
    private var mProgressLayout: LinearLayout? = null

    private var promptPopUpView: PromptPopUpView? = null



    var contactNumber: String? = null
    var contactName: String? = null
    private var btnResetPassword: Button? = null
    private var btnBack: Button? = null
    var errorNull: TextView? = null
    var MYCODE = 1000
    private val RESULT_PICK_CONTACT = 1001


    var mToolbar: Toolbar? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_alert, container, false)
        setHasOptionsMenu(true)
        recyclerView = view.findViewById(R.id.recycler_view)
        errorNull = view.findViewById(R.id.texterror)
        mToolbar =  view.findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar!!);
        mToolbar!!.title = "Response Providers Database"
        contactList = ArrayList()
        mAdapter = RespProvAdapter(activity!!, contactList!!, this)
        recyclerView!!.setNestedScrollingEnabled(false);
        mProgressLayout = view.findViewById(R.id.layout_discussions_progress);

        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(activity)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.addItemDecoration(
            MyDividerItemDecoration(
                activity!!,
                DividerItemDecoration.VERTICAL,
                36
            )
        )

        recyclerView!!.adapter = mAdapter


        view.context
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getStudent()

        //you can set the title for your toolbar here for different fragments different title
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

        val api: GetResponseProviders = retrofit.create(GetResponseProviders::class.java)
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
                        mProgressLayout!!.visibility = View.GONE
                        dialogue_error();
                        promptPopUpView?.changeStatus(1, "No data in response provider database")
                    }
                } else {
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



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_items, menu)

        var item = menu?.findItem(R.id.action_share)
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
            activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.action_search)
            .actionView as SearchView
        searchView!!.setSearchableInfo(
            searchManager
                .getSearchableInfo(activity!!.componentName)
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

    private fun dialogue() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->

            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    private fun dialogue_error() {
        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)
            .setPositiveButton("Back Home") { _: DialogInterface?, _: Int ->


                startActivity(Intent(activity, HomePage::class.java))
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }


     fun onBackPressed() {
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


        val i =
            Intent(activity, ResponseDetails::class.java)
             i.putExtra("responseSelect", contact!!.id.toString())
        startActivity(i)
    }



    fun BackAlert() {
        AlertDialog.Builder(activity!!)
            .setMessage("Leaving this page??")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, id ->
                    startActivity(Intent(activity, HomePage::class.java))
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

}
