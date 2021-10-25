package com.alat.ui.fragments

import android.app.ProgressDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.ShareActionProvider
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.alat.HomePage
import com.alat.R
import com.alat.adapters.RandomAdapter
import com.alat.helpers.Constants
import com.alat.helpers.MyDividerItemDecoration
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.*
import com.alat.model.PreferenceModel
import com.alat.model.rgModel
import com.alat.ui.AboutUs
import com.alat.ui.activities.*
import com.alat.ui.activities.auth.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rengwuxian.materialedittext.MaterialEditText
import dmax.dialog.SpotsDialog
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

class randomClients : Fragment(),
    RandomAdapter.ContactsAdapterListener {

    private var toolbar: Toolbar? = null

    var updateFNamee: MaterialEditText? = null

    var waitingDialog: android.app.AlertDialog? = null
    var fullname: String? = null
    var mssidn: String? = null
    var fname: String? = null
    var user: String? = null
    private val TAG = HomePage::class.java.simpleName
    private var recyclerView: RecyclerView? = null
    private var contactList: MutableList<rgModel>? = null
    private var mAdapter: RandomAdapter? = null
    private var searchView: SearchView? = null
    private var edtEmail: EditText? = null
    private var mProgressLayout: LinearLayout? = null

    private var preferenceHelper: PreferenceModel? = null
    private var promptPopUpView: PromptPopUpView? = null

    var alert: android.app.AlertDialog? = null
    var alert_name: String? = null

    var floatingActionButton: FloatingActionButton? = null

    var global: FloatingActionButton? = null
    private var hot_number = 0
    private var ui_hot: TextView? = null
    private var btnResetPassword: Button? = null
    private var btnBack: Button? = null
    var errorNull: TextView? = null
    private var mProgress: ProgressDialog? = null
    var pref: SharedPreferences? = null
    private var accounts: String? = null
    private var userid: String? = null

    private var roleID: String? = null

    private var groupname: String? = null

    private var groupid: String? = null
    var MYCODE = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_homeclient, container, false)
        setHasOptionsMenu(true)
        preferenceHelper = PreferenceModel(activity!!)
        recyclerView = view.findViewById(R.id.recycler_view)
        errorNull = view.findViewById(R.id.texterror)
        contactList = ArrayList()
        mAdapter = RandomAdapter(activity!!, contactList!!, this)
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
        mProgress = ProgressDialog(activity!!)
        mProgress!!.setMessage("Processing...")
        mProgress!!.setCancelable(true)

        recyclerView!!.adapter = mAdapter
        floatingActionButton =
            view.findViewById<View>(R.id.floating_action_button) as FloatingActionButton
        pref =
            context!!.getSharedPreferences("MyPref", 0)

        userid = pref!!.getString("userid", null)

        accounts = pref!!.getString("account_status", null)

        roleID = pref!!.getString("role", null)


        global =
            view.findViewById<View>(R.id.joinGlobal) as FloatingActionButton

        global!!.visibility= View.GONE

//
//        global!!.setOnClickListener {
//            startActivity(Intent(activity!!, JoinGlobal::class.java))
//        }
//
//        floatingActionButton!!.setOnClickListener {
//            startActivity(Intent(activity!!, CreateAlert::class.java))
//        }
        floatingActionButton!!.visibility= View.GONE


//      `  val runnable = Runnable { loadCustomAd() }
//        runnable.run()`

        view.context
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgressLayout!!.visibility = View.VISIBLE
        errorNull!!.visibility = View.GONE
        getStudent()
        //you can set the title for your toolbar here for different fragments different title
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

        //params["userid"] = userid!!
        val api: ViewRandom = retrofit.create(ViewRandom::class.java)
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
                                val dataobj: JSONObject = array.getJSONObject(i)

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

                    // Toast.makeText(context,"Nothing" +  response.errorBody()!!.string(),Toast.LENGTH_LONG).show();

                    internet()
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                //   btn.text = "Proceed"
                Log.i("onEmptyResponse", "" + t) //
                // Toast.makeText(context,"Nothing ",Toast.LENGTH_LONG).show();

                internet()
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //mProgress?.dismiss()
            }
        })
    }



    fun updatesclient() {

        val alertDialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(activity)
        alertDialog.setTitle("Send an Update")

        val inflater: LayoutInflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater



        val layout_pwd: View =
            inflater.inflate(R.layout.layout_find_update, null)
        alertDialog.setView(layout_pwd)
        alert = alertDialog.create()
        updateFNamee = layout_pwd.findViewById<View>(R.id.etName) as MaterialEditText
        updateFNamee!!.requestFocus()
        showKeyBoard()

        val updateButton: Button =
            layout_pwd.findViewById<View>(R.id.update) as Button
        updateButton.setOnClickListener(View.OnClickListener {
            waitingDialog =
                SpotsDialog.Builder().setContext(activity).build()



            updateFNamee!!.clearFocus()
            alert_name = updateFNamee!!.text.toString()

            if (Utils.checkIfEmptyString(alert_name)) {
                updateFNamee!!.error = "Is Mandatory"
                updateFNamee!!.requestFocus()
            } else {
                waitingDialog!!.show()

                pref =
                    activity!!.getSharedPreferences("MyPref", 0) // 0 - for private mode

                fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

                mssidn = pref!!.getString("mssdn", null)

                user = pref!!.getString("userid", null)



                //RequestBody body = RequestBody.Companion.create(json, JSON)\\\

                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                val client: OkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(interceptor) //.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
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
                params["alert_name"] = alert_name!!
                params["fullname"] = fullname!!
                params["alert_type"] = "Client Update"!!
                params["rg"] = groupname!!
                params["rl"] = "Level 1"
                params["mssdn"] = mssidn!!
                params["userid"] = user!!
                params["location"] = "null"!!
                params["notes"] = "null"!!

                //  Toast.makeText(this@CreateAlert, "" + alert_namess , Toast.LENGTH_LONG).show();

                val api: AddAlert = retrofit.create(AddAlert::class.java)
                val call: Call<ResponseBody> = api.addAlert(params)

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
                            parseLoginData2(remoteResponse)
                        } else {
                            mProgress?.dismiss()
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
                        mProgress?.dismiss()
                    }
                })

            }


        })
        val dismissButton: Button =
            layout_pwd.findViewById<View>(R.id.cancel) as Button
        dismissButton.setOnClickListener(View.OnClickListener {
            alert!!.dismiss()

        })
        alertDialog.setView(layout_pwd)
        alert!!.setCancelable(false)
        alert!!.show()

    }

//    private fun uploadImages2() {
//
//    }

    private fun parseLoginData2(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {
                dialogue();
                promptPopUpView?.changeStatus(2, "SUCCESSFUL")
                waitingDialog?.dismiss()
            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //Log.d("BAYO", response.code().toString())
                waitingDialog?.dismiss()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private fun showKeyBoard() {
        val imm =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun hideKeyboardFrom() {
        val imm =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

    }

    private fun dialogue() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)
            .setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                alert!!.dismiss()

//                alert!!.dismiss()
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    private fun dialogue_error() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)
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

//    inflater.inflate(R.menu.menu_items, menu)



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_items, menu)
    var item = menu.findItem(R.id.action_share)
    val item2 = menu.findItem(R.id.join)
    val item3 = menu.findItem(R.id.invites)
    val item4 = menu.findItem(R.id.about)
    val item5 = menu.findItem(R.id.logout)
    val item7 = menu.findItem(R.id.action_notif)
    val item8 = menu.findItem(R.id.aboutus)

    item?.isVisible = false
    item2?.isVisible = false
    item3?.isVisible = false
    item4?.isVisible = false
    item5?.isVisible = false
    item7?.isVisible = false
    item8?.isVisible = false



        // Fetch and store ShareActionProvider

        val item6 = menu.findItem(R.id.action_invite)
        item6.isVisible = false

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

    internal abstract class MyMenuItemStuffListener(
        private val view: View,
        private val hint: String
    ) :
        View.OnClickListener, View.OnLongClickListener {
        abstract override fun onClick(v: View?)
        override fun onLongClick(v: View?): Boolean {
            val screenPos = IntArray(2)
            val displayFrame = Rect()
            view.getLocationOnScreen(screenPos)
            view.getWindowVisibleDisplayFrame(displayFrame)
            val context = view.context
            val width = view.width
            val height = view.height
            val midy = screenPos[1] + height / 2
            val screenWidth = context.resources.displayMetrics.widthPixels
            val cheatSheet: Toast = Toast.makeText(context, hint, Toast.LENGTH_SHORT)
            if (midy < displayFrame.height()) {
                cheatSheet.setGravity(
                    Gravity.TOP or Gravity.RIGHT,
                    screenWidth - screenPos[0] - width / 2, height
                )
            } else {
                cheatSheet.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, height)
            }
            cheatSheet.show()
            return true
        }

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }
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
      https://play.google.com/store/apps/details?id=com.alat
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
                if (roleID == "1") {
                    startActivity(Intent(activity, com.alat.ui.activities.account::class.java))
                } else if (roleID == "2") {
                    startActivity(Intent(activity, account_enterprise::class.java))
                }
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

    fun onBackPressed() {
        // close search view on back button pressed
        if (!searchView!!.isIconified()) {
            searchView!!.isIconified = true
            return
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        // Result OK.d.
        if (requestCode == MYCODE) {
            if (!searchView!!.isIconified()) {
                searchView!!.isIconified = true
                return
            }
            // do something good
        }
    }


    override fun onContactSelected(contact: rgModel?) {


      groupname = contact!!.group_name
        groupid = contact.rg_id
       AlertStatus()


    }




    fun AlertStatus() {

        AlertDialog.Builder(activity!!)
            .setMessage("Manage Random Client")
            .setCancelable(true)
            .setPositiveButton("Send Update") { _, id ->
                //  accept()
//                getRGNAMES()
                updatesclient()

//                mProgress!!.show()
                // startActivity(Intent(this@FriendRequests, HomePage::class.java))
            }
            .setNeutralButton("View Alats") { _, id ->
                val i =
                    Intent(activity, AlertsPerResponse::class.java)
                i.putExtra("groupSelect",groupname)
                i.putExtra("groupID", groupid)

                startActivity(i)
            }

            .setNegativeButton("Delete Client") { _, id ->
                // reject()
                exit()
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


        params["rg_id"] = groupid!!

        val api: deleteClient = retrofit.create(deleteClient::class.java)
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

                startActivity(Intent(activity!!, HomePage::class.java))

                Toast.makeText(activity, "Successfully removed", Toast.LENGTH_LONG).show()

                mProgress!!.dismiss()



            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")

                mProgress!!.dismiss()
                Toast.makeText(activity, groupid!!, Toast.LENGTH_LONG).show()

                // Toast.makeText(this@AddTopicActivity, "Please Something went wrong", Toast.LENGTH_LONG).show()

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }
}
