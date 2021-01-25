package com.alat.ui.fragments

import android.app.ProgressDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.ShareActionProvider
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.alat.HomePage
import com.alat.R
import com.alat.adapters.RGAdapter
import com.alat.helpers.Admob
import com.alat.helpers.Constants
import com.alat.helpers.MyDividerItemDecoration
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.ViewGroups
import com.alat.model.PreferenceModel
import com.alat.model.rgModel
import com.alat.ui.AboutUs
import com.alat.ui.activities.*
import com.alat.ui.activities.auth.LoginActivity
import com.alat.ui.activities.enterprise.CreateAlerteNT
import com.alat.ui.activities.mpesa.Ban_Transfer
import com.alat.ui.activities.mpesa.MPESAExpressActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import libs.mjn.prettydialog.PrettyDialog
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

class Alert_Enterpris : Fragment(),
    RGAdapter.ContactsAdapterListener {
    private val TAG = HomePage::class.java.simpleName
    private var recyclerView: RecyclerView? = null
    private var contactList: MutableList<rgModel>? = null
    private var mAdapter: RGAdapter? = null
    private var edtEmail: EditText? = null
    private var preferenceHelper: PreferenceModel? = null
    private var promptPopUpView: PromptPopUpView? = null
    var floatingActionButton: FloatingActionButton? = null
    var global: FloatingActionButton? = null
    private var btnResetPassword: Button? = null
    private var btnBack: Button? = null
    private var mProgress: ProgressDialog? = null
    var pref: SharedPreferences? = null
    private var accounts: String? = null
    private var userid: String? = null

    private var roleID: String? = null
    var MYCODE = 1000
    private var toolbar : Toolbar? = null
    private var ui_hot: TextView? = null
    private var hot_number = 0
    private var searchView: SearchView? = null
    var errorNull: TextView? = null
    private var mProgressLayout: LinearLayout? = null
    var views: View? = null
    private var mAdView: AdView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_home_enterpris, container, false)
        setHasOptionsMenu(true)
        preferenceHelper = PreferenceModel(activity!!)
        recyclerView = view.findViewById(R.id.recycler_view)
        errorNull = view.findViewById(R.id.texterror)
        contactList = ArrayList()
        mAdapter = RGAdapter(activity!!, contactList!!, this)
        recyclerView!!.isNestedScrollingEnabled = false;


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
        floatingActionButton =
            view.findViewById<View>(R.id.floating_action_button) as FloatingActionButton
        pref =
            context!!.getSharedPreferences("MyPref", 0)

        userid = pref!!.getString("userid", null)
        accounts = pref!!.getString("account_status", null)
        roleID = pref!!.getString("role", null)
        floatingActionButton =
            view.findViewById<View>(R.id.floating_action_button) as FloatingActionButton
        errorNull = view.findViewById(R.id.texterror)
        mProgressLayout = view.findViewById(R.id.layout_discussions_progress);
        global =
            view.findViewById<View>(R.id.joinGlobal) as FloatingActionButton
        global!!.setOnClickListener {
            if (accounts == "0") {
                subscribe()
                promptPopUpView?.changeStatus(1, "Kindly subscribe to a plan to enjoy this feature. Thank you")
            } else {
                startActivity(Intent(activity!!, JoinGlobal::class.java))
            }
        }
        if (accounts == "0"){
             var ADS: String? = null
            var pref2: SharedPreferences? = null

            pref2 =
                context!!.getSharedPreferences("ADS", 0) // 0 - for private mode
            ADS = pref2!!.getString("ads", null)

            if(ADS == "0") {
                MobileAds.initialize(activity, "ca-app-pub-8641077171287971/4788863450"); //TEST KEY
                views = activity!!.window!!.decorView.rootView;
                Admob.createLoadBanner(activity!!.applicationContext, views);
                Admob.createLoadInterstitial(activity!!.applicationContext, null);
                mAdView = view.findViewById<View>(R.id.adView) as AdView?
                val adRequest: AdRequest = AdRequest.Builder().build()
                mAdView!!.loadAd(adRequest)
            }
        }

         return view
    }



//    fun BackAlert() {
//
//
//        val pDialog = PrettyDialog(activity)
//        pDialog
//            .setIconTint(R.color.colorPrimary)
//            .setTitle("Upgrade to PRO ACCOUNT")
//            .setTitleColor(R.color.pdlg_color_blue)
//            .setMessage("Choose the payment method you recommend")
//            .setMessageColor(R.color.pdlg_color_gray)
//            .addButton(
//                "Mpesa Payment",
//                R.color.pdlg_color_white,
//                R.color.colorAccent
//            ) {
//                pDialog.dismiss()
//
//                    val i =
//                        Intent(activity!!, MPESAExpressActivity::class.java)
//                    i.putExtra("price", "")
//                    startActivity(i)
//
//            }
//            .addButton(
//                "Direct Bank Transfer",
//                R.color.pdlg_color_white,
//                R.color.colorAccent
//            ) {
//                pDialog.dismiss()
//                val i =
//                    Intent(activity!!, Ban_Transfer::class.java)
//                i.putExtra("price", price)
//                startActivity(i)
//
//            }
//            .show()
//    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        floatingActionButton!!.setOnClickListener {
            if (accounts == "0") {
                subscribe()
                promptPopUpView?.changeStatus(1, "Kindly subscribe to a plan to enjoy this feature. Thank you")
            } else {
                alerttype()
            }
        }
        getStudent()
        mProgressLayout!!.visibility = View.VISIBLE
        errorNull!!.visibility = View.GONE
    }
        //you can set the title for your toolbar here for different fragments different title }
        private fun subscribe() {

            promptPopUpView = PromptPopUpView(activity!!)

            AlertDialog.Builder(activity!!)
                .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->

                }
                .setCancelable(false)
                .setView(promptPopUpView)
                .show()
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

        params["userid"] = userid!!
        val api: ViewGroups = retrofit.create(ViewGroups::class.java)
        val call: Call<ResponseBody>? = api.viewRG(params)

        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()
                if (response.isSuccessful) {
                    val remoteResponse = response.body()!!.string()
                    try {
                        val o = JSONObject(remoteResponse)
                        if (o.getString("status") == "true") {
                            val array: JSONArray = o.getJSONArray("records")
                            for (i in 0 until array.length()) {
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
                            }
                        } else {
                            errorNull!!.visibility = View.VISIBLE
                            mProgressLayout!!.visibility = View.GONE
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {
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

    private fun internet() {}


    private fun alerttype() {

        val pDialog = PrettyDialog(activity)
        pDialog
            .setIconTint(R.color.colorPrimary)
            .setTitle("Create An Alat")
            .setTitleColor(R.color.pdlg_color_blue)
            .setMessage("Choose the type of alert you want to create")
            .setMessageColor(R.color.pdlg_color_gray)
            .addButton(
                "Update Alert",
                R.color.pdlg_color_white,
                R.color.colorAccent
            ) { pDialog.dismiss()
                startActivity(Intent(activity, CreateAlerteNT::class.java))
            }
            .addButton(
                "Incident Alert",
                R.color.pdlg_color_white,
                R.color.colorAccent) {
                pDialog.dismiss()
                startActivity(Intent(activity, CreateAlert::class.java))
            }

            .show()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_items, menu)
        val item9 = menu.findItem(R.id.join)

        MenuItemCompat.setActionView(
            item9,
            R.layout.action_bar_notifitcation_icon
        )
        val menu_hotlist = MenuItemCompat.getActionView(item9)
        ui_hot = menu_hotlist.findViewById(R.id.hotlist_hot) as TextView
        updateHotCount(hot_number)
        object : alerts.MyMenuItemStuffListener(menu_hotlist, "Show hot message") {
            override fun onClick(v: View?) {
                //  activity.onHotlistSelected()
            }
        }
        val item = menu.findItem(R.id.action_share)
        // Fetch and store ShareActionProvider
        val item7 = menu.findItem(R.id.aboutus)
        item7.isVisible = true
        val item6 = menu.findItem(R.id.action_invite)
        item6.isVisible = false

        val mShareActionProvider: ShareActionProvider? =
            MenuItemCompat.getActionProvider(item) as ShareActionProvider?
        mShareActionProvider?.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME)

        mShareActionProvider?.setShareIntent(createShareIntent())
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
    // call the updating code on the main thread,
    // so we can call this asynchronously
    fun updateHotCount(new_hot_number: Int) {
        hot_number = new_hot_number
        if (ui_hot == null) return
        activity!!.runOnUiThread(Runnable {
            if (new_hot_number == 0) ui_hot!!.visibility = View.INVISIBLE else {
                ui_hot!!.visibility = View.VISIBLE
                ui_hot!!.text = Integer.toString(new_hot_number)
            }
        })
    }



    // Create and return the Share Intent
    private fun createShareIntent(): Intent? {
        val shareIntent =
            Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT, """
          ALATPRES
     Get AlatPres.
     https://play.google.com/store/apps/details?id=com.alatpres
     """.trimIndent()
        )
        val intent =
            Intent.createChooser(shareIntent, "Share Via")
        return shareIntent
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
            R.id.join -> {
                startActivity(Intent(activity, Notification::class.java))

            }


            R.id.invites -> {
                startActivity(Intent(activity, Invitations::class.java))
            }

            R.id.aboutus -> {
                startActivity(Intent(activity, AboutUs::class.java))
            }

            R.id.about -> {

                    startActivity(Intent(activity, account_enterprise::class.java))

            }
//            R.id.team-> {
//                startActivity(Intent(activity, ResponseProviders::class.java))
//            }

            R.id.logout -> {
                logout()
            }

            else -> super.onOptionsItemSelected(item)
        }

        return true
    }


    private fun logout() {
        SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Are you sure?")
            .setContentText("You will be required to login again to access ALATPRES!")
            .setConfirmText("Yes, sign me out!")
            .setConfirmClickListener {

                    sDialog ->
                sDialog.dismissWithAnimation()

                pref =
                    context!!.getSharedPreferences("MyPref", 0) // 0 - for private mode

                val editor: SharedPreferences.Editor = pref!!.edit()
                editor.putBoolean("isLogin", false)
                editor.clear()
                editor.apply(); // commit changes


                startActivity(Intent(activity, LoginActivity::class.java))


            }
            .show()
    }

    override fun onContactSelected(contact: rgModel?) {


        val i =
            Intent(activity, AlertsPerResponse::class.java)
        i.putExtra("groupSelect", contact!!.group_name.toString())
        i.putExtra("groupID", contact!!.id.toString())

        startActivity(i)


    }




}